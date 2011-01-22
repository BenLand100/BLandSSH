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

import blandssh.network.Payload;
import blandssh.network.SSHInputStream;
import blandssh.network.SSHOutputStream;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public abstract class Message implements Payload {

    public final byte id;
    
    protected Message(byte id) {
        this.id = id;
    }

    protected abstract void readMsg(SSHInputStream in) throws IOException;

    public final void read(SSHInputStream in) throws IOException {
        byte id = in.readByte();
        if (this.id != id) throw new IOException("Invalid message ID:" + id);
        readMsg(in);
    }

    protected abstract void writeMsg(SSHOutputStream out) throws IOException;

    public final void write(SSHOutputStream out) throws IOException {
        out.writeByte(id);
        writeMsg(out);
    }

    public String toString() {
        return "MessageID:" + id;
    }

}
