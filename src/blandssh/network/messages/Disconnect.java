package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class Disconnect extends Message {

    int code;
    String why;
    String language;

    public Disconnect() {
        this(0,"","");
    }

    public Disconnect(int code, String why, String language) {
        super(SSHNumbers.SSH_MSG_DISCONNECT);
        this.code = code;
        this.why = why;
        this.language = language;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        code = in.readUInt32();
        why = in.readString();
        language = in.readString();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(code);
        out.writeString(why);
        out.writeString(language);
    }

    public void dump() {
        System.out.println(code + " : " + why);
    }


}
