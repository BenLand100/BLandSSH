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

package blandssh.network;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @author benland100
 */
public class SSHInputStream extends FilterInputStream {

    public SSHInputStream(InputStream in) {
        super(in);
    }

    public byte readByte() throws IOException {
        return (byte)(in.read() & 0xff);
    }

    public boolean readBoolean() throws IOException {
        return in.read() == 0 ? false : true;
    }

    public int readUInt32() throws IOException {
        byte[] buffer = new byte[4];
        in.read(buffer,0,4);
        return ((int)buffer[3] & 0xff) | (((int)buffer[2] & 0xff) << 8) | (((int)buffer[1] & 0xff) << 16) | (((int)buffer[0] & 0xff) << 24);
    }

    public long readUInt64() throws IOException {
        byte[] buffer = new byte[8];
        in.read(buffer,0,8);
        return ((long)buffer[7] & 0xff) | (((long)buffer[6] & 0xff) << 8) | (((long)buffer[5] & 0xff) << 16) | (((long)buffer[4] & 0xff) << 24) | (((long)buffer[3] & 0xff) << 32) | (((long)buffer[2] & 0xff) << 40) | (((long)buffer[1] & 0xff) << 48) | (((long)buffer[0] & 0xff) << 56);
    }

    public byte[] readBytes() throws IOException {
        int len = readUInt32();
        byte[] buffer = new byte[len];
        in.read(buffer,0,len);
        return buffer;
    }


    public String readString() throws IOException {
        int len = readUInt32();
        byte[] buffer = new byte[len];
        in.read(buffer,0,len);
        return new String(buffer,0,len);
    }

    public BigInteger readMPInt() throws IOException {
        int len = readUInt32();
        byte[] buffer = new byte[len];
        in.read(buffer,0,len);
        return new BigInteger(buffer);
    }

    public String[] readNameList() throws IOException {
        String s = readString();
        return s.split(",");
    }

}
