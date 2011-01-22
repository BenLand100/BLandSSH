package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelRequestSuccess extends ChannelMessage {

    public ChannelRequestSuccess() {
        this(-1);
    }

    public ChannelRequestSuccess(int id) {
        super(SSHNumbers.SSH_MSG_CHANNEL_SUCCESS);
        recipient = id;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
    }

}