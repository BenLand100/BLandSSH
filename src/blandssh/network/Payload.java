package blandssh.network;

import java.io.IOException;

/**
 *
 * @author benland100
 */
public interface Payload {

    public void read(SSHInputStream packet) throws IOException;
    public void write(SSHOutputStream packet) throws IOException;

}
