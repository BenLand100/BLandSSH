package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class Ignored  extends Message {

    byte[] data;

    public Ignored() {
        this(0);
    }

    public Ignored(int size) {
        super(SSHNumbers.SSH_MSG_IGNORE);
        data = new byte[size];
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        data = packet.readBytes();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeBytes(data);
    }

}
