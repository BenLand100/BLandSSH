package blandssh.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author benland100
 */
public class SSH_3DES_CBC implements SSHCypher {
    
    private Cipher cipher;
    private SecretKeyFactory keyfactory;

    public SSH_3DES_CBC() throws Exception {
        cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        keyfactory = SecretKeyFactory.getInstance("DESede");
    }

    public int getBlockSize() {
        return 8;
    }

    public int getIVSize() {
        return 8;
    }

    public int getKeySize() {
        return 24;
    }

    public boolean isCBC() {
        return true;
    }

    public void init(Mode mode, byte[] key, byte[] iv) throws Exception {
        DESedeKeySpec keyspec=new DESedeKeySpec(key);
        SecretKey secret_key = keyfactory.generateSecret(keyspec);
        cipher.init(Mode.Encrypt == mode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secret_key, new IvParameterSpec(iv));
    }

    public void run(byte[] in, int in_off, int len, byte[] out, int out_off) throws Exception {
        cipher.update(in, in_off, len, out, out_off);
    }


}
