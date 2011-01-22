package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author benland100
 */
public class KExInit extends Message {

    public byte[] cookie;
    public String[] kex_algorithms;
    public String[] server_host_key_algorithms;
    public String[] encryption_algorithms_client_to_server;
    public String[] encryption_algorithms_server_to_client;
    public String[] mac_algorithms_client_to_server;
    public String[] mac_algorithms_server_to_client;
    public String[] compression_algorithms_client_to_server;
    public String[] compression_algorithms_server_to_client;
    public String[] languages_client_to_server;
    public String[] languages_server_to_client;
    public boolean first_kex_packet_follows;
    public int ignored;

    public KExInit() {
        super(SSHNumbers.SSH_MSG_KEXINIT);
        cookie = new byte[16];
        kex_algorithms = new String[0];
        server_host_key_algorithms = new String[0];
        encryption_algorithms_client_to_server = new String[0];
        encryption_algorithms_server_to_client = new String[0];
        mac_algorithms_client_to_server = new String[0];
        mac_algorithms_server_to_client = new String[0];
        compression_algorithms_client_to_server = new String[0];
        compression_algorithms_server_to_client = new String[0];
        languages_client_to_server = new String[0];
        languages_server_to_client = new String[0];
        first_kex_packet_follows = false;
        ignored = 0;
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        packet.read(cookie, 0, 16);
        kex_algorithms = packet.readNameList();
        server_host_key_algorithms = packet.readNameList();
        encryption_algorithms_client_to_server = packet.readNameList();
        encryption_algorithms_server_to_client = packet.readNameList();
        mac_algorithms_client_to_server = packet.readNameList();
        mac_algorithms_server_to_client = packet.readNameList();
        compression_algorithms_client_to_server = packet.readNameList();
        compression_algorithms_server_to_client = packet.readNameList();
        languages_client_to_server = packet.readNameList();
        languages_server_to_client = packet.readNameList();
        first_kex_packet_follows = packet.readBoolean();
        ignored = packet.readUInt32();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.write(cookie,0,16);
        packet.writeNameList(kex_algorithms);
        packet.writeNameList(server_host_key_algorithms);
        packet.writeNameList(encryption_algorithms_client_to_server);
        packet.writeNameList(encryption_algorithms_server_to_client);
        packet.writeNameList(mac_algorithms_client_to_server);
        packet.writeNameList(mac_algorithms_server_to_client);
        packet.writeNameList(compression_algorithms_client_to_server);
        packet.writeNameList(compression_algorithms_server_to_client);
        packet.writeNameList(languages_client_to_server);
        packet.writeNameList(languages_server_to_client);
        packet.writeBoolean(first_kex_packet_follows);
        packet.writeUInt32(ignored);
    }

    public void dump() {
        System.out.println(Arrays.toString(cookie));
        System.out.println(Arrays.toString(kex_algorithms));
        System.out.println(Arrays.toString(server_host_key_algorithms));
        System.out.println(Arrays.toString(encryption_algorithms_client_to_server));
        System.out.println(Arrays.toString(encryption_algorithms_server_to_client));
        System.out.println(Arrays.toString(mac_algorithms_client_to_server));
        System.out.println(Arrays.toString(mac_algorithms_server_to_client));
        System.out.println(Arrays.toString(compression_algorithms_client_to_server));
        System.out.println(Arrays.toString(compression_algorithms_server_to_client));
        System.out.println(Arrays.toString(languages_client_to_server));
        System.out.println(Arrays.toString(languages_server_to_client));
        System.out.println(first_kex_packet_follows);
    }

}
