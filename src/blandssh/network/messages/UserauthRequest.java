/**
 *  Copyright 2010 by Benjamin J. Land (a.k.a. BenLand100)
 *
 *  This file is part of BLandSSH.
 *
 *  BLandSSH is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BLandSSH is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BLandSSH. If not, see <http://www.gnu.org/licenses/>.
 */

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
