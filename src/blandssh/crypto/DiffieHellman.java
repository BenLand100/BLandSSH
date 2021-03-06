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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

/**
 *
 * @author benland100
 */
public class DiffieHellman {

    public static final byte[] Oakley_Group_2 = {
        (byte) 0x00,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xC9, (byte) 0x0F, (byte) 0xDA, (byte) 0xA2, (byte) 0x21, (byte) 0x68, (byte) 0xC2, (byte) 0x34,
        (byte) 0xC4, (byte) 0xC6, (byte) 0x62, (byte) 0x8B, (byte) 0x80, (byte) 0xDC, (byte) 0x1C, (byte) 0xD1,
        (byte) 0x29, (byte) 0x02, (byte) 0x4E, (byte) 0x08, (byte) 0x8A, (byte) 0x67, (byte) 0xCC, (byte) 0x74,
        (byte) 0x02, (byte) 0x0B, (byte) 0xBE, (byte) 0xA6, (byte) 0x3B, (byte) 0x13, (byte) 0x9B, (byte) 0x22,
        (byte) 0x51, (byte) 0x4A, (byte) 0x08, (byte) 0x79, (byte) 0x8E, (byte) 0x34, (byte) 0x04, (byte) 0xDD,
        (byte) 0xEF, (byte) 0x95, (byte) 0x19, (byte) 0xB3, (byte) 0xCD, (byte) 0x3A, (byte) 0x43, (byte) 0x1B,
        (byte) 0x30, (byte) 0x2B, (byte) 0x0A, (byte) 0x6D, (byte) 0xF2, (byte) 0x5F, (byte) 0x14, (byte) 0x37,
        (byte) 0x4F, (byte) 0xE1, (byte) 0x35, (byte) 0x6D, (byte) 0x6D, (byte) 0x51, (byte) 0xC2, (byte) 0x45,
        (byte) 0xE4, (byte) 0x85, (byte) 0xB5, (byte) 0x76, (byte) 0x62, (byte) 0x5E, (byte) 0x7E, (byte) 0xC6,
        (byte) 0xF4, (byte) 0x4C, (byte) 0x42, (byte) 0xE9, (byte) 0xA6, (byte) 0x37, (byte) 0xED, (byte) 0x6B,
        (byte) 0x0B, (byte) 0xFF, (byte) 0x5C, (byte) 0xB6, (byte) 0xF4, (byte) 0x06, (byte) 0xB7, (byte) 0xED,
        (byte) 0xEE, (byte) 0x38, (byte) 0x6B, (byte) 0xFB, (byte) 0x5A, (byte) 0x89, (byte) 0x9F, (byte) 0xA5,
        (byte) 0xAE, (byte) 0x9F, (byte) 0x24, (byte) 0x11, (byte) 0x7C, (byte) 0x4B, (byte) 0x1F, (byte) 0xE6,
        (byte) 0x49, (byte) 0x28, (byte) 0x66, (byte) 0x51, (byte) 0xEC, (byte) 0xE6, (byte) 0x53, (byte) 0x81,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };
    public static final byte[] Oakley_Group_14 = {
        (byte) 0x00,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xC9, (byte) 0x0F, (byte) 0xDA, (byte) 0xA2, (byte) 0x21, (byte) 0x68, (byte) 0xC2, (byte) 0x34,
        (byte) 0xC4, (byte) 0xC6, (byte) 0x62, (byte) 0x8B, (byte) 0x80, (byte) 0xDC, (byte) 0x1C, (byte) 0xD1,
        (byte) 0x29, (byte) 0x02, (byte) 0x4E, (byte) 0x08, (byte) 0x8A, (byte) 0x67, (byte) 0xCC, (byte) 0x74,
        (byte) 0x02, (byte) 0x0B, (byte) 0xBE, (byte) 0xA6, (byte) 0x3B, (byte) 0x13, (byte) 0x9B, (byte) 0x22,
        (byte) 0x51, (byte) 0x4A, (byte) 0x08, (byte) 0x79, (byte) 0x8E, (byte) 0x34, (byte) 0x04, (byte) 0xDD,
        (byte) 0xEF, (byte) 0x95, (byte) 0x19, (byte) 0xB3, (byte) 0xCD, (byte) 0x3A, (byte) 0x43, (byte) 0x1B,
        (byte) 0x30, (byte) 0x2B, (byte) 0x0A, (byte) 0x6D, (byte) 0xF2, (byte) 0x5F, (byte) 0x14, (byte) 0x37,
        (byte) 0x4F, (byte) 0xE1, (byte) 0x35, (byte) 0x6D, (byte) 0x6D, (byte) 0x51, (byte) 0xC2, (byte) 0x45,
        (byte) 0xE4, (byte) 0x85, (byte) 0xB5, (byte) 0x76, (byte) 0x62, (byte) 0x5E, (byte) 0x7E, (byte) 0xC6,
        (byte) 0xF4, (byte) 0x4C, (byte) 0x42, (byte) 0xE9, (byte) 0xA6, (byte) 0x37, (byte) 0xED, (byte) 0x6B,
        (byte) 0x0B, (byte) 0xFF, (byte) 0x5C, (byte) 0xB6, (byte) 0xF4, (byte) 0x06, (byte) 0xB7, (byte) 0xED,
        (byte) 0xEE, (byte) 0x38, (byte) 0x6B, (byte) 0xFB, (byte) 0x5A, (byte) 0x89, (byte) 0x9F, (byte) 0xA5,
        (byte) 0xAE, (byte) 0x9F, (byte) 0x24, (byte) 0x11, (byte) 0x7C, (byte) 0x4B, (byte) 0x1F, (byte) 0xE6,
        (byte) 0x49, (byte) 0x28, (byte) 0x66, (byte) 0x51, (byte) 0xEC, (byte) 0xE4, (byte) 0x5B, (byte) 0x3D,
        (byte) 0xC2, (byte) 0x00, (byte) 0x7C, (byte) 0xB8, (byte) 0xA1, (byte) 0x63, (byte) 0xBF, (byte) 0x05,
        (byte) 0x98, (byte) 0xDA, (byte) 0x48, (byte) 0x36, (byte) 0x1C, (byte) 0x55, (byte) 0xD3, (byte) 0x9A,
        (byte) 0x69, (byte) 0x16, (byte) 0x3F, (byte) 0xA8, (byte) 0xFD, (byte) 0x24, (byte) 0xCF, (byte) 0x5F,
        (byte) 0x83, (byte) 0x65, (byte) 0x5D, (byte) 0x23, (byte) 0xDC, (byte) 0xA3, (byte) 0xAD, (byte) 0x96,
        (byte) 0x1C, (byte) 0x62, (byte) 0xF3, (byte) 0x56, (byte) 0x20, (byte) 0x85, (byte) 0x52, (byte) 0xBB,
        (byte) 0x9E, (byte) 0xD5, (byte) 0x29, (byte) 0x07, (byte) 0x70, (byte) 0x96, (byte) 0x96, (byte) 0x6D,
        (byte) 0x67, (byte) 0x0C, (byte) 0x35, (byte) 0x4E, (byte) 0x4A, (byte) 0xBC, (byte) 0x98, (byte) 0x04,
        (byte) 0xF1, (byte) 0x74, (byte) 0x6C, (byte) 0x08, (byte) 0xCA, (byte) 0x18, (byte) 0x21, (byte) 0x7C,
        (byte) 0x32, (byte) 0x90, (byte) 0x5E, (byte) 0x46, (byte) 0x2E, (byte) 0x36, (byte) 0xCE, (byte) 0x3B,
        (byte) 0xE3, (byte) 0x9E, (byte) 0x77, (byte) 0x2C, (byte) 0x18, (byte) 0x0E, (byte) 0x86, (byte) 0x03,
        (byte) 0x9B, (byte) 0x27, (byte) 0x83, (byte) 0xA2, (byte) 0xEC, (byte) 0x07, (byte) 0xA2, (byte) 0x8F,
        (byte) 0xB5, (byte) 0xC5, (byte) 0x5D, (byte) 0xF0, (byte) 0x6F, (byte) 0x4C, (byte) 0x52, (byte) 0xC9,
        (byte) 0xDE, (byte) 0x2B, (byte) 0xCB, (byte) 0xF6, (byte) 0x95, (byte) 0x58, (byte) 0x17, (byte) 0x18,
        (byte) 0x39, (byte) 0x95, (byte) 0x49, (byte) 0x7C, (byte) 0xEA, (byte) 0x95, (byte) 0x6A, (byte) 0xE5,
        (byte) 0x15, (byte) 0xD2, (byte) 0x26, (byte) 0x18, (byte) 0x98, (byte) 0xFA, (byte) 0x05, (byte) 0x10,
        (byte) 0x15, (byte) 0x72, (byte) 0x8E, (byte) 0x5A, (byte) 0x8A, (byte) 0xAC, (byte) 0xAA, (byte) 0x68,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };
    private BigInteger p;
    private BigInteger g;
    private BigInteger e;  // local public key
    private BigInteger f;  // remote public key
    private BigInteger K;  // shared secret key
    private KeyPairGenerator myKpairGen;
    private KeyAgreement myKeyAgree;
    private boolean evalid = false, kvalid = false;

    public DiffieHellman() throws Exception {
        myKpairGen = KeyPairGenerator.getInstance("DH");
        myKeyAgree = KeyAgreement.getInstance("DH");
    }

    public BigInteger getE() throws Exception {
        if (e == null && !evalid) {
            DHParameterSpec dhSkipParamSpec = new DHParameterSpec(p, g);
            myKpairGen.initialize(dhSkipParamSpec);
            KeyPair myKpair = myKpairGen.generateKeyPair();
            myKeyAgree.init(myKpair.getPrivate());
            e = ((javax.crypto.interfaces.DHPublicKey) (myKpair.getPublic())).getY();
            evalid = true;
        }
        return e;
    }

    public BigInteger getK() throws Exception {
        if (K == null && !kvalid) {
            KeyFactory myKeyFac = KeyFactory.getInstance("DH");
            DHPublicKeySpec keySpec = new DHPublicKeySpec(f, p, g);
            PublicKey yourPubKey = myKeyFac.generatePublic(keySpec);
            myKeyAgree.doPhase(yourPubKey, true);
            byte[] secret = myKeyAgree.generateSecret();
            if ((secret[0]&0x80)!=0) {
                byte[] temp = new byte[secret.length+1];
                System.arraycopy(secret, 0, temp, 1, secret.length);
                secret = temp;
            }
            K = new BigInteger(secret);
            kvalid = true;
        }
        return K;
    }

    public void setP(BigInteger p) {
        this.p = p;
        evalid = kvalid = false;
    }

    public void setG(BigInteger g) {
        this.g = g;
        evalid = kvalid = false;
    }

    public void setF(BigInteger f) {
        this.f = f;
        evalid = false;
    }
}
