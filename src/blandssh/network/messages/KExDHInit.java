package blandssh.network.messages;

import blandssh.network.Payload;
import blandssh.network.SSHInputStream;
import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 *
 * @author benland100
 */
public class KExDHInit extends Message {

    public BigInteger e;
    
    public KExDHInit() {
        this(BigInteger.ZERO);
    }
    
    public KExDHInit(BigInteger e) {
        super(SSHNumbers.SSH_MSG_KEXDH_INIT);
        this.e = e;
    }
    
    protected void readMsg(SSHInputStream in) throws IOException {
        e = in.readMPInt();
    }
    
    protected void writeMsg(SSHOutputStream out) throws IOException {
        out.writeMPInt(e);
    }


}
