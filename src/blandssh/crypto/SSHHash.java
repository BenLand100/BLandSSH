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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author benland100
 */
public abstract class SSHHash {

    private static final byte[] toHex = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final byte[] toByte = new byte[256];

    static {
        for (char i = '0'; i <= '9'; i++) {
            toByte[i] = (byte) (i - '0');
        }
        for (char i = 'A'; i <= 'F'; i++) {
            toByte[i] = (byte) (i - 'A' + 10);
        }
    }

    public static String hexEncode(byte[] bytes) {
        byte[] chars = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            chars[i * 2] = toHex[(bytes[i] >> 4) & 0xf];
            chars[i * 2 + 1] = toHex[bytes[i] & 0xf];
        }
        return new String(chars);
    }

    public static byte[] hexDecode(String str) {
        byte[] chars = str.getBytes();
        byte[] bytes = new byte[chars.length / 2];
        for (int i = 0; i < chars.length;) {
            bytes[i / 2] = (byte) ((toByte[chars[i++]] << 4) | (toByte[chars[i++]]));
        }
        return bytes;
    }

    public abstract byte[] hash(byte[] data);

}
