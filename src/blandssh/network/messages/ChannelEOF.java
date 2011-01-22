package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelEOF extends ChannelMessage {

    public ChannelEOF() {
        this(-1);
    }

    public ChannelEOF(int id) {
        super(SSHNumbers.SSH_MSG_CHANNEL_EOF);
        recipient = id;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
    }

}
