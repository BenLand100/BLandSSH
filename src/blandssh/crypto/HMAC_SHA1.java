/**
 *  Copyright 2010 by Benjamin J. Land (a.k.a. BenLand100)
 *
 *  This file is part of BLandSSH.
 *
 *  BLandSSH is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BLandSSH is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BLandSSH. If not, see <http://www.gnu.org/licenses/>.
 */

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
