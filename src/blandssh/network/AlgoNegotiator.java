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

import blandssh.network.messages.KExInit;
import java.io.IOException;

/**
 *
 * @author benland100
 */
public class AlgoNegotiator {

    public String kex_algo = null;
    public String key_format = null;
    public String mac_c2s_algo = null;
    public String mac_s2c_algo = null;
    public String enc_c2s_algo = null;
    public String enc_s2c_algo = null;
    public String com_c2s_algo = null;
    public String com_s2c_algo = null;
    public boolean goodguess = true;

    public AlgoNegotiator(KExInit client, KExInit server) throws IOException {
        if (server.kex_algorithms[0].equals(client.kex_algorithms[0])) {
            kex_algo = client.kex_algorithms[0];
        } else {
            goodguess = false;
            kex_search:
            for (String c_algo : client.kex_algorithms) {
                for (String s_algo : server.kex_algorithms) {
                    if (c_algo.equals(s_algo)) {
                        kex_algo = c_algo;
                        break kex_search;
                    }
                }
            }
        }
        if (server.server_host_key_algorithms[0].equals(client.server_host_key_algorithms[0])) {
            key_format = client.server_host_key_algorithms[0];
        } else {
            goodguess = false;
            key_search:
            for (String c_format : client.server_host_key_algorithms) {
                for (String s_format : server.server_host_key_algorithms) {
                    if (c_format.equals(s_format)) {
                        key_format = c_format;
                        break key_search;
                    }
                }
            }
        }
        mac_c2s_search:
        for (String c_algo : client.mac_algorithms_client_to_server) {
            for (String h_algo : server.mac_algorithms_client_to_server) {
                if (c_algo.equals(h_algo)) {
                    mac_c2s_algo = c_algo;
                    break mac_c2s_search;
                }
            }
        }
        mac_s2c_search:
        for (String c_algo : client.mac_algorithms_server_to_client) {
            for (String h_algo : server.mac_algorithms_server_to_client) {
                if (c_algo.equals(h_algo)) {
                    mac_s2c_algo = c_algo;
                    break mac_s2c_search;
                }
            }
        }
        enc_c2s_search:
        for (String c_algo : client.encryption_algorithms_client_to_server) {
            for (String h_algo : server.encryption_algorithms_client_to_server) {
                if (c_algo.equals(h_algo)) {
                    enc_c2s_algo = c_algo;
                    break enc_c2s_search;
                }
            }
        }
        enc_s2c_search:
        for (String c_algo : client.encryption_algorithms_server_to_client) {
            for (String h_algo : server.encryption_algorithms_server_to_client) {
                if (c_algo.equals(h_algo)) {
                    enc_s2c_algo = c_algo;
                    break enc_s2c_search;
                }
            }
        }
        com_c2s_search:
        for (String c_algo : client.compression_algorithms_client_to_server) {
            for (String h_algo : server.compression_algorithms_client_to_server) {
                if (c_algo.equals(h_algo)) {
                    com_c2s_algo = c_algo;
                    break com_c2s_search;
                }
            }
        }
        com_s2c_search:
        for (String c_algo : client.compression_algorithms_server_to_client) {
            for (String h_algo : server.compression_algorithms_server_to_client) {
                if (c_algo.equals(h_algo)) {
                    com_s2c_algo = c_algo;
                    break com_s2c_search;
                }
            }
        }
        if (kex_algo == null || key_format == null || mac_c2s_algo == null || mac_s2c_algo == null || enc_c2s_algo == null || enc_s2c_algo == null || com_c2s_algo == null || com_s2c_algo == null) {
            throw new IOException("Algorithm negotiation failed");
        }
    }

    public void dump() {
        System.out.println("Key Ex Algo: " + kex_algo);
        System.out.println("Key Format: " + key_format);
        System.out.println("MAC C->S Algo: " + mac_c2s_algo);
        System.out.println("MAC S->C Algo: " + mac_s2c_algo);
        System.out.println("Enc C->S Algo: " + enc_c2s_algo);
        System.out.println("Enc S->C Algo: " + enc_s2c_algo);
        System.out.println("Com C->S Algo: " + com_c2s_algo);
        System.out.println("Com S->C Algo: " + com_s2c_algo);
    }

}
