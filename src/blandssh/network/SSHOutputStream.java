package blandssh.network;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 *
 * @author benland100
 */
public class SSHOutputStream extends FilterOutputStream  {

    public SSHOutputStream(OutputStream out) {
        super(out);
    }

    public void writeByte(byte b) throws IOException {
        out.write(b);
    }

    public void writeBoolean(boolean b) throws IOException {
        out.write(b ? 1 : 0);
    }

    public void writeUInt32(int i) throws IOException {
        byte[] buffer = new byte[4];
        buffer[0] = (byte)((i >> 24) & 0xff);
        buffer[1] = (byte)((i >> 16) & 0xff);
        buffer[2] = (byte)((i >> 8) & 0xff);
        buffer[3] = (byte)((i >> 0) & 0xff);
        out.write(buffer, 0, 4);
    }

    public void writeUInt64(long l) throws IOException {
        byte[] buffer = new byte[8];
        buffer[0] = (byte)((l >> 56) & 0xff);
        buffer[1] = (byte)((l >> 48) & 0xff);
        buffer[2] = (byte)((l >> 40) & 0xff);
        buffer[3] = (byte)((l >> 32) & 0xff);
        buffer[4] = (byte)((l >> 24) & 0xff);
        buffer[5] = (byte)((l >> 16) & 0xff);
        buffer[6] = (byte)((l >> 8) & 0xff);
        buffer[7] = (byte)((l >> 0) & 0xff);
        out.write(buffer, 0, 8);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        writeUInt32(bytes.length);
        out.write(bytes,0,bytes.length);
    }

    public void writeString(String s) throws IOException {
        byte[] bytes = s.getBytes();
        writeUInt32(bytes.length);
        out.write(bytes,0,bytes.length);
    }

    public void writeMPInt(BigInteger i) throws IOException {
        byte[] bytes = i.toByteArray();
        if (bytes.length == 1 && bytes[0] == 0) {
            writeUInt32(0);
        } else {
            writeUInt32(bytes.length);
            out.write(bytes,0,bytes.length);
        }
    }

    public void writeNameList(String[] names) throws IOException {
        StringBuilder b = new StringBuilder();
        for (String s : names) {
            b.append(s).append(',');
        }
        int last = b.lastIndexOf(",");
        if (last != -1)  b.deleteCharAt(last);
        writeString(b.toString());
    }

}
