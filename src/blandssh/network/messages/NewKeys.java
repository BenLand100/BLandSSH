package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class NewKeys extends Message {

    public NewKeys() {
        super(SSHNumbers.SSH_MSG_NEWKEYS);
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
    }

}
