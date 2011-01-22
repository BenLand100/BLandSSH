package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class Debug extends Message {

    public boolean display;
    public String msg, lang;

    public Debug() {
        super(SSHNumbers.SSH_MSG_DEBUG);
        display = false;
        msg = lang = "";
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        display = packet.readBoolean();
        msg = packet.readString();
        lang = packet.readString();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeBoolean(display);
        packet.writeString(msg);
        packet.writeString(lang);
    }

}