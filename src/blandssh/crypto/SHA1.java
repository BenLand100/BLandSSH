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
