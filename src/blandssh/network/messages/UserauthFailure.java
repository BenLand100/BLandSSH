package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class UserauthFailure extends Message {

    public String[] auths_cont;
    public boolean partial;

    public UserauthFailure() {
        super(SSHNumbers.SSH_MSG_USERAUTH_FAILURE);
        auths_cont = new String[0];
        partial = true;
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        auths_cont = packet.readNameList();
        partial = packet.readBoolean();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeNameList(auths_cont);
        packet.writeBoolean(partial);
    }

}