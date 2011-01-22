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
