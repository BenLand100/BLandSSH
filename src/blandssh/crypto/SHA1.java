package blandssh.crypto;

import java.security.MessageDigest;

/**
 *
 * @author benland100
 */
public class SHA1 extends SSHHash {

    private final MessageDigest sha1;

    public SHA1() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sha1 = md;
    }

    @Override
    public byte[] hash(byte[] data) {
        if (sha1 == null) return null;
        byte[] sha1hash = new byte[40];
        synchronized (sha1) {
            sha1.reset();
            sha1.update(data, 0, data.length);
            sha1hash = sha1.digest();
            return sha1hash;
        }
    }

}
