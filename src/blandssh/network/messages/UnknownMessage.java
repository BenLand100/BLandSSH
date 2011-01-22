package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class UnknownMessage extends Message {

    byte[] data;

    public UnknownMessage(byte id, int len) {
        super(id);
        data = new byte[len-1];
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        in.read(data);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.write(data);
    }

}
