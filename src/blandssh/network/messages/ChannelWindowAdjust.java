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
public class ChannelWindowAdjust extends ChannelMessage {

    public int size;

    public ChannelWindowAdjust() {
        this(-1,-1);
    }

    public ChannelWindowAdjust(int id,int size) {
        super(SSHNumbers.SSH_MSG_CHANNEL_WINDOW_ADJUST);
        recipient = id;
        this.size = size;
    }

    protected void readMsg(SSHInputStream in) throws IOException {
        recipient = in.readUInt32();
        size = in.readUInt32();
    }

    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeUInt32(recipient);
        out.writeUInt32(size);
    }

}
