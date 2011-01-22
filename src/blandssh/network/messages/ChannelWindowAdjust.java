/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
