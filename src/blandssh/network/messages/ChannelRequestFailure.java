package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelRequestFailure extends ChannelMessage {

    public ChannelRequestFailure() {
        this(-1);
    }

    public ChannelRequestFailure(int id) {
        super(SSHNumbers.SSH_MSG_CHANNEL_FAILURE);
        recipient = id;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
    }

}