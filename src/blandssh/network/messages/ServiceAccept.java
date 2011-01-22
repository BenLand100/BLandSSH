package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ServiceAccept extends Message {

    public String service;

    public ServiceAccept() {
        this("");
    }

    public ServiceAccept(String service) {
        super(SSHNumbers.SSH_MSG_SERVICE_ACCEPT);
        this.service = service;
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        service = packet.readString();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeString(service);
    }

}
