package blandssh.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author benland100
 */
public class HMAC_SHA1 implements SSHMac {

    private final Mac mac;

    public HMAC_SHA1() {
        Mac temp = null;
        try {
            temp = Mac.getInstance("HmacSHA1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mac = temp;
    }

    public int getDigestSize() {
        return 20;
    }

    public int getKeySize() {
        return 20;
    }

    public void init(byte[] key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        mac.init(signingKey);
    }

    public byte[] run(int seq, byte[] data) {
        mac.update((byte)((seq>>24)&0xff));
        mac.update((byte)((seq>>16)&0xff));
        mac.update((byte)((seq>>8)&0xff));
        mac.update((byte)((seq)&0xff));
        mac.update(data);
        return mac.doFinal();
    }

}
