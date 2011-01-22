package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelData extends ChannelMessage {

    public byte[] data;

    public ChannelData() {
        this(-1,new byte[0]);
    }

    public ChannelData(int id,byte[]data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_DATA);
        recipient = id;
        this.data = data;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        data = in.readBytes();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeBytes(data);
    }

}