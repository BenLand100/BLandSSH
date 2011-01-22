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
public class Disconnect extends Message {

    int code;
    String why;
    String language;

    public Disconnect() {
        this(0,"","");
    }

    public Disconnect(int code, String why, String language) {
        super(SSHNumbers.SSH_MSG_DISCONNECT);
        this.code = code;
        this.why = why;
        this.language = language;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        code = in.readUInt32();
        why = in.readString();
        language = in.readString();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(code);
        out.writeString(why);
        out.writeString(language);
    }

    public void dump() {
        System.out.println(code + " : " + why);
    }


}
