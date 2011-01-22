package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelRequest extends ChannelMessage {

    public String type;
    public boolean wantReply;
    public byte[] data;

    int len;

    public ChannelRequest(int recipient, String type, boolean wantReply, byte[] data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_REQUEST);
        this.type = type;
        this.recipient = recipient;
        this.wantReply = wantReply;
        this.data = data;
    }

    public ChannelRequest(int len) {
        super(SSHNumbers.SSH_MSG_CHANNEL_REQUEST);
        this.len = len;
        type = "";
        data = null;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        type = in.readString();
        wantReply = in.readBoolean();
        data = new byte[len-1-4-4-type.getBytes().length-1];
        in.read(data);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeString(type);
        out.writeBoolean(wantReply);
        out.write(data);
    }

}
