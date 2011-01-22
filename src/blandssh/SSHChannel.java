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

package blandssh;

import blandssh.network.messages.Message;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public abstract class SSHChannel {

    public static enum Status { Initilized, Opening, Opened, Rejected, Closed };

    protected Status status = Status.Initilized;
    protected SSHConnection conn;
    protected final String type;
    protected int remoteID, localID, maxPacket;

    protected int localWindow, remoteWindow;

    public SSHChannel(String type, int initWindow, int maxPacket) {
        this.localID = -1;
        this.type = type;
        this.maxPacket = maxPacket;
        this.localWindow = initWindow;
    }

    protected abstract void open() throws IOException;
    protected abstract void close() throws IOException;

    public abstract void recieve(Message m) throws IOException;

    public final void openChannel(SSHConnection conn) throws IOException {
        if (status != Status.Initilized) throw new IOException("Channel already opened");
        this.conn = conn;
        localID = conn.addChannel(this);
        status = Status.Opening;
        open();
    }

    public final void closeChannel() throws IOException {
        if (status != Status.Opened) throw new IOException("Channel already closed");
        close();
        status = Status.Closed;
    }

    public Status getStatus() {
        return status;
    }

}
