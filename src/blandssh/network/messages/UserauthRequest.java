package blandssh.network.messages;

import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class UserauthRequest extends Message {

    public String username, service, method;
    byte[] rest;

    int len;

    public UserauthRequest(int len) {
        super(SSHNumbers.SSH_MSG_USERAUTH_REQUEST);
        rest = new byte[len-12];
    }

    public UserauthRequest(String username, String service, String method, byte[] rest) {
        super(SSHNumbers.SSH_MSG_USERAUTH_REQUEST);
        this.rest = rest;
        this.username = username;
        this.service = service;
        this.method = method;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        username = in.readString();
        service = in.readString();
        method = in.readString();
        rest = new byte[len-12-username.getBytes().length-service.getBytes().length-method.getBytes().length];
        in.read(rest);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        len = 12+username.getBytes().length+service.getBytes().length+method.getBytes().length+rest.length;
        out.writeString(username);
        out.writeString(service);
        out.writeString(method);
        out.write(rest);
    }

}