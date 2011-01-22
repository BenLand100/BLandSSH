package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class ServiceRequest extends Message {

    public String service;

    public ServiceRequest() {
        this("");
    }

    public ServiceRequest(String service) {
        super(SSHNumbers.SSH_MSG_SERVICE_REQUEST);
        this.service = service;
    }

    protected void readMsg(SSHInputStream packet) throws IOException {
        service = packet.readString();
    }

    protected void writeMsg(SSHOutputStream packet) throws IOException {
        packet.writeString(service);
    }

}