package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelOpen extends Message {

    public String type;
    public int sender, windowSize, packetSize;
    public byte[] data;

    int len;

    public ChannelOpen(String type, int senderID, int windowSize, int packetSize, byte[] data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN);
        this.type = type;
        this.sender = senderID;
        this.windowSize = windowSize;
        this.packetSize = packetSize;
        this.data = data;
    }

    public ChannelOpen(int len) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN);
        this.len = len;
        type = "";
        data = null;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        type = in.readString();
        sender = in.readUInt32();
        windowSize = in.readUInt32();
        packetSize = in.readUInt32();
        data = new byte[len-1-4-type.getBytes().length-4-4-4];
        in.read(data);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeString(type);
        out.writeUInt32(sender);
        out.writeUInt32(windowSize);
        out.writeUInt32(packetSize);
        out.write(data);
    }

}
