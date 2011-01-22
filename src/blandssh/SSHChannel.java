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
