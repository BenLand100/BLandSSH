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
