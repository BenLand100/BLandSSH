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

/**
 *
 * @author benland100
 */
public interface SSHNumbers {

    public static final byte SSH_MSG_DISCONNECT = 1;
    public static final byte SSH_MSG_IGNORE = 2;
    public static final byte SSH_MSG_UNIMPLEMENTED = 3;
    public static final byte SSH_MSG_DEBUG = 4;
    public static final byte SSH_MSG_SERVICE_REQUEST = 5;
    public static final byte SSH_MSG_SERVICE_ACCEPT = 6;
    public static final byte SSH_MSG_KEXINIT = 20;
    public static final byte SSH_MSG_NEWKEYS = 21;
    public static final byte SSH_MSG_KEXDH_INIT = 30;
    public static final byte SSH_MSG_KEXDH_REPLY = 31;
    public static final byte SSH_MSG_USERAUTH_REQUEST = 50;
    public static final byte SSH_MSG_USERAUTH_FAILURE = 51;
    public static final byte SSH_MSG_USERAUTH_SUCCESS = 52;
    public static final byte SSH_MSG_USERAUTH_BANNER = 53;
    public static final byte SSH_MSG_GLOBAL_REQUEST = 80;
    public static final byte SSH_MSG_REQUEST_SUCCESS = 81;
    public static final byte SSH_MSG_REQUEST_FAILURE = 82;
    public static final byte SSH_MSG_CHANNEL_OPEN = 90;
    public static final byte SSH_MSG_CHANNEL_OPEN_CONFIRMATION = 91;
    public static final byte SSH_MSG_CHANNEL_OPEN_FAILURE = 92;
    public static final byte SSH_MSG_CHANNEL_WINDOW_ADJUST = 93;
    public static final byte SSH_MSG_CHANNEL_DATA = 94;
    public static final byte SSH_MSG_CHANNEL_EXTENDED_DATA = 95;
    public static final byte SSH_MSG_CHANNEL_EOF = 96;
    public static final byte SSH_MSG_CHANNEL_CLOSE = 97;
    public static final byte SSH_MSG_CHANNEL_REQUEST = 98;
    public static final byte SSH_MSG_CHANNEL_SUCCESS = 99;
    public static final byte SSH_MSG_CHANNEL_FAILURE = 100;
}
