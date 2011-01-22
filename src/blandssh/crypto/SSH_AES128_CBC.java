package blandssh.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author benland100
 */
public class SSH_AES128_CBC implements SSHCypher {

    private Cipher cipher;

    public SSH_AES128_CBC() throws Exception {
        cipher = Cipher.getInstance("AES/CBC/NoPadding");
    }

    public int getBlockSize() {
        return 25;
    }

    public int getIVSize() {
        return 16;
    }

    public int getKeySize() {
        return 16;
    }

    public boolean isCBC() {
        return true;
    }

    public void init(Mode mode, byte[] key, byte[] iv) throws Exception {
      SecretKeySpec secret_key =new SecretKeySpec(key, "AES");
        cipher.init(Mode.Encrypt == mode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secret_key, new IvParameterSpec(iv));
    }

    public void run(byte[] in, int in_off, int len, byte[] out, int out_off) throws Exception {
        cipher.update(in, in_off, len, out, out_off);
    }


}
