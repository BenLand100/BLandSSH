package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class UserauthSuccess extends Message {

    public UserauthSuccess() {
        super(SSHNumbers.SSH_MSG_USERAUTH_SUCCESS);
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
    }

}

