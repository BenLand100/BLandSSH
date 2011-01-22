package blandssh.network.messages;

/**
 *
 * @author benland100
 */
public abstract class ChannelMessage extends Message {

    public int recipient;

    public ChannelMessage(byte id) {
        super(id);
    }

}
