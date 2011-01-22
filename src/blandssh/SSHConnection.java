package blandssh;

import blandssh.crypto.*;
import blandssh.network.*;
import blandssh.network.messages.*;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.spec.*;
import java.util.*;

/**
 *
 * @author benland100
 */
public class SSHConnection {

    public static enum Kex_Algorithm { DH_G1_SHA1 }
    public static enum Key_Format { SSH_RSA, SSH_DSA }
    public static enum Encrypt_Algorithm { SSH_AES128_CBC, SSH_3DES_CBC; }
    public static enum Mac_Algorithm { HMAC_SHA1 }
    public static enum Compression_Algorithm { NONE }

    public static final LinkedHashMap<String, Kex_Algorithm> kex_map = new LinkedHashMap<String, Kex_Algorithm>();
    public static final LinkedHashMap<String, Key_Format> key_map = new LinkedHashMap<String, Key_Format>();
    public static final LinkedHashMap<String, Encrypt_Algorithm> encr_map = new LinkedHashMap<String, Encrypt_Algorithm>();
    public static final LinkedHashMap<String, Mac_Algorithm> mac_map = new LinkedHashMap<String, Mac_Algorithm>();
    public static final LinkedHashMap<String, Compression_Algorithm> comp_map = new LinkedHashMap<String, Compression_Algorithm>();

    public static final HashMap<String, BigInteger> primes = new HashMap<String, BigInteger>();

    static {
        primes.put("diffie-hellman-group1-sha1", new BigInteger(DiffieHellman.Oakley_Group_2));
        kex_map.put("diffie-hellman-group1-sha1", Kex_Algorithm.DH_G1_SHA1);
        key_map.put("ssh-rsa", Key_Format.SSH_RSA);
        key_map.put("ssh-dsa", Key_Format.SSH_DSA);
        encr_map.put("3des-cbc", Encrypt_Algorithm.SSH_3DES_CBC);
        encr_map.put("aes128-cbc", Encrypt_Algorithm.SSH_AES128_CBC);
        mac_map.put("hmac-sha1", Mac_Algorithm.HMAC_SHA1);
        comp_map.put("none", Compression_Algorithm.NONE);
    }
    
    public static final String VERSION = "SSH-2.0-BLandSSH_1.0\r\n";
    private final Socket socket;
    private final SSHInputStream in;
    private final SSHOutputStream out;
    private boolean connected, authed;
    private int in_count, out_count;
    private BigInteger secretKey;
    private byte[] sessionID, secretHash;
    private SSHHash hash = null;
    private SSHCypher encryption_in = null;
    private SSHCypher encryption_out = null;
    private SSHMac mac_in = null;
    private SSHMac mac_out = null;

    private TreeMap<Integer,SSHChannel> channelMap = new TreeMap<Integer,SSHChannel>();

    private final Object readlock = new Object(), writelock = new Object();

    public SSHConnection(Socket s) throws IOException {
        socket = s;
        in = new SSHInputStream(socket.getInputStream());
        out = new SSHOutputStream(socket.getOutputStream());
        connected = false;
        authed = false;
    }

    public void connect() throws IOException {
        if (connected) {
            throw new IOException("Already connected");
        }
        out.write(VERSION.getBytes());
        byte[] V_C = VERSION.substring(0, VERSION.length() - 2).getBytes();
        String s = "Attempting connection at transport level\r\n";
        while (!s.startsWith("SSH-")) {
            System.out.print(s);
            StringBuilder str = new StringBuilder();
            byte b = 0;
            while (b != '\n') {
                b = in.readByte();
                str.append((char) b);
            }
            s = str.toString();
        }
        byte[] V_S = s.substring(0, s.length() - 2).getBytes();
        String version = s.substring(4, s.indexOf("-", 4));
        if (version.equals("2.0") || version.equals("1.99")) {
        } else {
            throw new IOException("Invalid host SSH version");
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        KExInit our_kex = createKExInit();
        writeMessage(our_kex);
        our_kex.write(new SSHOutputStream(bout));
        byte[] I_C = bout.toByteArray();
        bout.reset();

        KExInit host_kex = (KExInit) readMessage();
        host_kex.write(new SSHOutputStream(bout));
        byte[] I_S = bout.toByteArray();
        bout.reset();

        System.out.println("Negotiating algorithms");
        AlgoNegotiator algos = new AlgoNegotiator(our_kex, host_kex);
        if (!algos.goodguess && host_kex.first_kex_packet_follows) {
            readMessage();
        }

        algos.dump();

        hash = new SHA1();

        DiffieHellman dh;
        BigInteger p = primes.get(algos.kex_algo);
        BigInteger g = BigInteger.valueOf(2);
        BigInteger e = null;

        if (p == null) {
            throw new IOException("Unknown prime for key exchange");
        }
        try {
            dh = new DiffieHellman();
            dh.setP(p);
            dh.setG(g);
            e = dh.getE();
            if (e == null) {
                throw new Exception("E was null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Diffie-Hellman generation failed");
        }

        writeMessage(new KExDHInit(e));
        KExDHReply reply = (KExDHReply) readMessage();

        BigInteger k = null;
        try {
            dh.setF(reply.f);
            k = dh.getK();
            if (k == null) {
                throw new Exception("K was null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Diffie-Hellman validation failed");
        }

        SSHOutputStream data = new SSHOutputStream(bout);
        data.writeBytes(V_C);
        data.writeBytes(V_S);
        data.writeBytes(I_C);
        data.writeBytes(I_S);
        data.writeBytes(reply.k_s);
        data.writeMPInt(e);
        data.writeMPInt(reply.f);
        data.writeMPInt(k);

        byte[] H = hash.hash(bout.toByteArray());

        SSHInputStream key = new SSHInputStream(new ByteArrayInputStream(reply.k_s));
        SSHInputStream sig = new SSHInputStream(new ByteArrayInputStream(reply.sig_H));
        switch (key_map.get(algos.key_format)) {
            case SSH_DSA: {
                key.readString();
                BigInteger dsa_p = key.readMPInt();
                BigInteger dsa_q = key.readMPInt();
                BigInteger dsa_g = key.readMPInt();
                BigInteger dsa_y = key.readMPInt();

               sig.readString();
                byte[] blob = sig.readBytes();

                try {
                    Signature signature = Signature.getInstance("SHA1withDSA");
                    KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                    DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(dsa_y,dsa_p,dsa_q,dsa_g);
                    PublicKey pubKey = keyFactory.generatePublic(dsaPubKeySpec);
                    signature.initVerify(pubKey);
                    signature.update(H);
                    if (!signature.verify(blob))
                        throw new Exception("Key Invalid");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new IOException("Could not verify signature");
                }
            } break;
            case SSH_RSA: {
                key.readString();
                BigInteger rsa_e = key.readMPInt();
                BigInteger rsa_n = key.readMPInt();

                sig.readString();
                byte[] blob = sig.readBytes();

                try {
                    Signature signature = Signature.getInstance("SHA1withRSA");
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPublicKeySpec dsaPubKeySpec = new RSAPublicKeySpec(rsa_n,rsa_e);
                    PublicKey pubKey = keyFactory.generatePublic(dsaPubKeySpec);
                    signature.initVerify(pubKey);
                    signature.update(H);
                    if (!signature.verify(blob))
                        throw new Exception("Key Invalid");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new IOException("Could not verify signature");
                }
            } break;
        }

        System.out.println("Signature verified");
        NewKeys newkeys = (NewKeys)readMessage();
        writeMessage(new NewKeys());

        System.out.println("Transport connection established");
        secretKey = k;
        secretHash = H;
        sessionID = H;
        setAlgorithms(algos);
        connected = true;
    }

    public void auth(String username, String password) throws IOException {
        if (authed) throw new IOException("Already authenticated");
        Message m;
        writeMessage(new ServiceRequest("ssh-userauth"));
        m = readMessage();
        if (!(m instanceof ServiceAccept && ((ServiceAccept)m).service.equals("ssh-userauth")))
            throw new IOException("Authentication not enabled");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SSHOutputStream stream = new SSHOutputStream(buffer);
        stream.writeBoolean(false);
        stream.writeString(password);
        UserauthRequest req = new UserauthRequest(username,"ssh-connection","password",buffer.toByteArray());
        writeMessage(req);
        System.out.println("Attempting to authenticate user");
        m = readMessage();
        while (m instanceof UserauthSuccess == false) {
            if (m instanceof UserauthBanner) {
                System.out.println(((UserauthBanner)m).msg);
            } else {
                System.out.println("Message ID:"+m.id);
                throw new IOException("Authentication failed");
            }
            m = readMessage();
        }
        System.out.println("User authenticated");
        authed = true;
    }

    public void process() throws IOException {
        try {
            read:
            while (connected && authed) {
                Message m = readMessage();
                if (m.id < 50) {
                    throw new IOException("Unhandled transport level message");
                } else if (m.id < 80) {
                    throw new IOException("Unhandled authentication level message");
                } else if (m.id < 128) {
                    switch (m.id) {
                        case SSHNumbers.SSH_MSG_CHANNEL_OPEN:
                            throw new IOException("Opening channel not supported yet");
                        default:
                            if (!(m instanceof ChannelMessage))
                                throw new IOException("Unimplemented communication level message");
                            ChannelMessage cmsg = (ChannelMessage)m;
                            SSHChannel chan = channelMap.get(cmsg.recipient);
                            switch (cmsg.id) {
                                case SSHNumbers.SSH_MSG_CHANNEL_CLOSE:
                                    channelMap.remove(cmsg.recipient);
                                    break;
                                case SSHNumbers.SSH_MSG_CHANNEL_OPEN_FAILURE:
                                    channelMap.remove(cmsg.recipient);
                                    break;
                            }
                            if (chan != null) {
                                chan.recieve(m);
                            } else {
                                throw new IOException("Server referenced unknown channel ID");
                            }
                    }
                } else if (m.id < 192) {
                    throw new IOException("Unhandled client level message");
                } else {
                    throw new IOException("Unhandled local level message");
                }
            }
            throw new IOException("Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int addChannel(SSHChannel chan) {
        if (channelMap.containsValue(chan)) {
            for (Map.Entry<Integer,SSHChannel> e : channelMap.entrySet()) {
                if (e.getValue() == chan) {
                    return e.getKey();
                }
            }
        } else {
            int last = -1;
            for (int i : channelMap.navigableKeySet()) {
                if (i - last != 1) {
                    channelMap.put(last + 1, chan);
                    return last + 1;
                }
                last++;
            }
            channelMap.put(0, chan);
            return 0;
        }
        return -1;
    }

    private byte[] genKeyHash(char x, int bytes) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SSHOutputStream stream = new SSHOutputStream(buffer);
        try {
            stream.writeMPInt(secretKey);
            stream.write(secretHash);
            stream.writeByte((byte)x);
            stream.write(sessionID);
            byte[] key = hash.hash(buffer.toByteArray());
            ArrayList<byte[]> hashes = new ArrayList<byte[]>();
            hashes.add(key);
            for (int i = 1; key.length*hashes.size() < bytes; i++) {
                buffer.reset();
                stream.writeMPInt(secretKey);
                stream.write(secretHash);
                for (byte[] h : hashes) {
                    stream.write(h);
                }
                hashes.add(hash.hash(buffer.toByteArray()));
            }
            buffer.reset();
            for (byte[] h : hashes) {
                stream.write(h);
            }
            key = new byte[bytes];
            System.arraycopy(buffer.toByteArray(), 0, key, 0, bytes);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setAlgorithms(AlgoNegotiator algos) throws IOException {
         try {
             switch (encr_map.get(algos.enc_s2c_algo)) {
                case SSH_AES128_CBC:
                    encryption_in = new SSH_AES128_CBC();
                    break;
                case SSH_3DES_CBC:
                    encryption_in = new SSH_3DES_CBC();
                    break;
            }
            encryption_in.init(SSHCypher.Mode.Decrypt, genKeyHash('D',encryption_in.getKeySize()), genKeyHash('B',encryption_in.getIVSize()));
         } catch (Exception e) {
             e.printStackTrace();
             throw new IOException("Failed to initilize input cypher");
         }
         try {
             switch (encr_map.get(algos.enc_c2s_algo)) {
                case SSH_AES128_CBC:
                    encryption_out = new SSH_AES128_CBC();
                    break;
                case SSH_3DES_CBC:
                    encryption_out = new SSH_3DES_CBC();
                    break;
            }
            encryption_out.init(SSHCypher.Mode.Encrypt, genKeyHash('C',encryption_in.getKeySize()), genKeyHash('A',encryption_in.getIVSize()));
         } catch (Exception e) {
             e.printStackTrace();
             throw new IOException("Failed to initilize output cypher");
         }
         try {
             switch (mac_map.get(algos.mac_s2c_algo)) {
                 case HMAC_SHA1:
                     mac_in = new HMAC_SHA1();
                     mac_in.init(genKeyHash('F',mac_in.getKeySize()));
             }
         } catch (Exception e) {
             e.printStackTrace();
             throw new IOException("Failed to intilize input hmac");
         }
         try {
             switch (mac_map.get(algos.mac_c2s_algo)) {
                 case HMAC_SHA1:
                     mac_out = new HMAC_SHA1();
                     mac_out.init(genKeyHash('E',mac_out.getKeySize()));
             }
         } catch (Exception e) {
             e.printStackTrace();
             throw new IOException("Failed to initilize output hmac");
         }
    }

    private KExInit createKExInit() {
        KExInit msg = new KExInit();
        for (int i = 0; i < 16; i++) {
            msg.cookie[i] = (byte) ((int) (Math.random() * 0xff) & 0xff);
        }
        msg.kex_algorithms = kex_map.keySet().toArray(new String[0]);
        msg.server_host_key_algorithms = key_map.keySet().toArray(new String[0]);
        msg.encryption_algorithms_client_to_server = encr_map.keySet().toArray(new String[0]);
        msg.encryption_algorithms_server_to_client = encr_map.keySet().toArray(new String[0]);
        msg.mac_algorithms_client_to_server = mac_map.keySet().toArray(new String[0]);
        msg.mac_algorithms_server_to_client = mac_map.keySet().toArray(new String[0]);
        msg.compression_algorithms_client_to_server = comp_map.keySet().toArray(new String[0]);
        msg.compression_algorithms_server_to_client = comp_map.keySet().toArray(new String[0]);
        return msg;
    }

    private Message readMessage() throws IOException {
        synchronized (readlock) {
            int blocksize = 8;
            if (encryption_in != null) {
                blocksize = Math.max(blocksize,encryption_in.getBlockSize());
            }
            byte[] block = new byte[blocksize];
            in.read(block, 0, blocksize);
            byte[] block_dec;
            if (encryption_in != null) {
                try {
                    block_dec = new byte[blocksize];
                    encryption_in.run(block, 0, blocksize, block_dec, 0);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            } else {
                block_dec = block;
            }
            SSHInputStream stream = new SSHInputStream(new ByteArrayInputStream(block_dec));

            int len = stream.readUInt32();
            int pad = (int) (stream.readByte() & 0xff);
            byte[] rest = new byte[len+4-blocksize];
            in.read(rest);
            byte[] rest_dec;
            if (encryption_in != null) {
                try {
                    rest_dec = new byte[rest.length];
                    encryption_in.run(rest, 0, rest.length, rest_dec, 0);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            } else {
                rest_dec = rest;
            }

            byte[] rawdata = new byte[len + 4];
            System.arraycopy(block_dec, 0, rawdata, 0, blocksize);
            System.arraycopy(rest_dec, 0, rawdata, blocksize, rest.length);
            SSHInputStream data = new SSHInputStream(new ByteArrayInputStream(rawdata,5,rawdata.length-5));
            //System.out.println("In:  " + Arrays.toString(rawdata));
            byte id = rawdata[5];
            Message m;
            switch (id) {
                case SSHNumbers.SSH_MSG_DISCONNECT:
                    m = new Disconnect();
                    break;
                case SSHNumbers.SSH_MSG_KEXINIT:
                    m = new KExInit();
                    break;
                case SSHNumbers.SSH_MSG_KEXDH_INIT:
                    m = new KExDHInit();
                    break;
                case SSHNumbers.SSH_MSG_KEXDH_REPLY:
                    m = new KExDHReply();
                    break;
                case SSHNumbers.SSH_MSG_NEWKEYS:
                    m = new NewKeys();
                    break;
                case SSHNumbers.SSH_MSG_DEBUG:
                    m = new Debug();
                    break;
                case SSHNumbers.SSH_MSG_IGNORE:
                    m = new Ignored();
                    break;
                case SSHNumbers.SSH_MSG_USERAUTH_SUCCESS:
                    m = new UserauthSuccess();
                    break;
                case SSHNumbers.SSH_MSG_USERAUTH_FAILURE:
                    m = new UserauthFailure();
                    break;
                case SSHNumbers.SSH_MSG_USERAUTH_BANNER:
                    m = new UserauthBanner();
                    break;
                case SSHNumbers.SSH_MSG_USERAUTH_REQUEST:
                    m = new UserauthRequest(len-1-pad);
                    break;
                case SSHNumbers.SSH_MSG_SERVICE_REQUEST:
                    m = new ServiceRequest();
                    break;
                case SSHNumbers.SSH_MSG_SERVICE_ACCEPT:
                    m = new ServiceAccept();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION:
                    m = new ChannelOpenConfirm(len-1-pad);
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_OPEN_FAILURE:
                    m = new ChannelOpenFail();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_DATA:
                    m = new ChannelData();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_EXTENDED_DATA:
                    m = new ChannelExtendedData();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_WINDOW_ADJUST:
                    m = new ChannelWindowAdjust();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_REQUEST:
                    m = new ChannelRequest(len);
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_SUCCESS:
                    m = new ChannelRequestSuccess();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_FAILURE:
                    m = new ChannelRequestFailure();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_CLOSE:
                    m = new ChannelClose();
                    break;
                case SSHNumbers.SSH_MSG_CHANNEL_EOF:
                    m = new ChannelEOF();
                    break;
                default:
                    m = new UnknownMessage(id,len-1-pad);
                    System.out.println("Unknown message ID:" + id);
            }
            if (mac_in != null) {
                byte[] digest = new byte[mac_in.getDigestSize()];
                in.read(digest);
                if (!Arrays.equals(digest, mac_in.run(in_count, rawdata))) {
                    throw new IOException("MAC digest failed");
                }
            }
            in_count++;
            if (m != null) {
                m.read(data);
                switch (m.id) {
                    case SSHNumbers.SSH_MSG_DISCONNECT:
                        ((Disconnect) m).dump();
                        throw new IOException("Disconnected");
                    case SSHNumbers.SSH_MSG_IGNORE:
                        return readMessage();
                    case SSHNumbers.SSH_MSG_DEBUG:
                        System.out.println("Debug: " + ((Debug)m).msg);
                        return readMessage();
                    default:
                        return m;
                }
            }
            throw new IOException("Should never get here...");
        }
    }

    public void send(Message m) throws IOException {
        writeMessage(m);
    }

    private void writeMessage(Message m) throws IOException {
        synchronized (writelock) {
            int blocksize = 8;
            if (encryption_out != null) {
                blocksize = Math.max(blocksize,encryption_out.getBlockSize());
            }
            int len;
            byte pad;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            SSHOutputStream stream = new SSHOutputStream(bout);
            m.write(stream);
            byte[] data = bout.toByteArray();
            len = 1 + data.length;
            pad = (byte) (2*blocksize - (len + 4) % blocksize);
            len += pad;
            bout.reset();
            stream.writeUInt32(len);
            stream.writeByte(pad);
            stream.write(data, 0, data.length);
            for (int i = 0; i < pad; i++) {
                stream.writeByte((byte) ((int) (Math.random() * 0xff) & 0xff));
            }
            data = bout.toByteArray();
            byte[] enc;
            if (encryption_out != null) {
                enc = new byte[data.length];
                try {
                    encryption_out.run(data, 0, data.length, enc, 0);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            } else {
                enc = data;
            }
            //System.out.println("Out: " + Arrays.toString(enc));
            out.write(enc);
            if (mac_out != null) {
                out.write(mac_out.run(out_count, data));
            }
            out_count++;
        }
    }
}
