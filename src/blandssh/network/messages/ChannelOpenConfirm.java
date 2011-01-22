package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelOpenConfirm  extends ChannelMessage {

    public int sender, windowSize, packetSize;
    public byte[] data;

    public int len;

    public ChannelOpenConfirm(int recipient, int sender, int windowSize, int packetSize, byte[] data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION);
        this.recipient = recipient;
        this.sender = sender;
        this.windowSize = windowSize;
        this.packetSize = packetSize;
        this.data = data;
    }

    public ChannelOpenConfirm(int len) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION);
        this.len = len;
        data = null;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        sender = in.readUInt32();
        windowSize = in.readUInt32();
        packetSize = in.readUInt32();
        data = new byte[len-1-16];
        in.read(data);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeUInt32(sender);
        out.writeUInt32(windowSize);
        out.writeUInt32(packetSize);
        out.write(data);
    }

}
