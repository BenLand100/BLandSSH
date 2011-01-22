package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 *
 * @author benland100
 */
public class KExDHReply extends Message {

    public byte[] k_s;
    public BigInteger f;
    public byte[] sig_H;

    public KExDHReply() {
        super(SSHNumbers.SSH_MSG_KEXDH_REPLY);
        k_s = new byte[0];
        sig_H = new byte[0];
        f = BigInteger.ZERO;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        k_s = in.readBytes();
        f = in.readMPInt();
        sig_H = in.readBytes();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeBytes(k_s);
        out.writeMPInt(f);
        out.writeBytes(sig_H);
    }

}
