package blandssh.crypto;

/**
 *
 * @author benland100
 */
public interface SSHMac {

    public int getKeySize();
    public int getDigestSize();
    public void init(byte[] key) throws Exception;
    public byte[] run(int seq, byte[] data);

}
