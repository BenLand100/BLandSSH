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
