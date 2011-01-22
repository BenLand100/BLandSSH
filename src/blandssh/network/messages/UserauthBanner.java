package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class UserauthBanner extends Message {

    public String msg, lang;

    public UserauthBanner() {
        super(SSHNumbers.SSH_MSG_USERAUTH_BANNER);
        msg = lang = "";
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        msg = packet.readString();
        lang = packet.readString();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeString(msg);
        packet.writeString(lang);
    }

}
