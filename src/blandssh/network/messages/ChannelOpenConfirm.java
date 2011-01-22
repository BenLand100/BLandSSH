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
public class ChannelOpenConfirm  extends ChannelMessage {

    public int sender, windowSize, packetSize;
    public byte[] data;

    public int len;

    public ChannelOpenConfirm(int recipient, int sender, int windowSize, int packetSize, byte[] data) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION);
        this.recipient = recipient;
        this.sender = sender;
        this.windowSize = windowSize;
        this.packetSize = packetSize;
        this.data = data;
    }

    public ChannelOpenConfirm(int len) {
        super(SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION);
        this.len = len;
        data = null;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        sender = in.readUInt32();
        windowSize = in.readUInt32();
        packetSize = in.readUInt32();
        data = new byte[len-1-16];
        in.read(data);
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeUInt32(sender);
        out.writeUInt32(windowSize);
        out.writeUInt32(packetSize);
        out.write(data);
    }

}
