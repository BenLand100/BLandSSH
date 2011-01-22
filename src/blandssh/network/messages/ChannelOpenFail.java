package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ChannelOpenFail extends ChannelMessage {

    public int reason;
    public String why,lang;

    public ChannelOpenFail(int localID, int reason, String why, String lang) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_FAILURE);
        this.recipient = localID;
        this.reason = reason;
        this.why = why;
        this.lang = lang;
    }

    public ChannelOpenFail() {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_FAILURE);
        why = lang = "";
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        reason = in.readUInt32();
        why = in.readString();
        lang = in.readString();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeUInt32(reason);
        out.writeString(why);
        out.writeString(lang);
    }

}
