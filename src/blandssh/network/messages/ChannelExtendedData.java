package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelExtendedData extends ChannelMessage {

    public int type;
    public byte[] data;

    public ChannelExtendedData() {
        this(-1,-1,new byte[0]);
    }

    public ChannelExtendedData(int id,int type,byte[]data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_EXTENDED_DATA);
        recipient = id;
        this.type = type;
        this.data = data;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        type = in.readUInt32();
        data = in.readBytes();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeUInt32(type);
        out.writeBytes(data);
    }

}