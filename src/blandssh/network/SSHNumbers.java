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