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
