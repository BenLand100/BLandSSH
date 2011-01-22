package blandssh.crypto;

/**
 *
 * @author benland100
 */
public interface SSHCypher {

    public static enum Mode {
        Encrypt, Decrypt
    }

    public int getBlockSize();
    public int getIVSize();
    public int getKeySize();
    public boolean isCBC();

    public void init(Mode mode, byte[] key, byte[] iv) throws Exception;

    public void run(byte[] in, int in_off, int len, byte[] out, int out_off) throws Exception;

}
