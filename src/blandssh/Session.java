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

package blandssh;

import blandssh.network.SSHNumbers;
import blandssh.network.SSHOutputStream;
import blandssh.network.messages.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author benland100
 */
public class Session extends SSHChannel {

    public enum RequestStatus {
        Waiting,Success,Failure;
    }

    public static class Request {
        private RequestStatus status = RequestStatus.Waiting;
        public RequestStatus getStatus() {
            return status;
        }
    }

    private final LinkedList<Request> reqs = new LinkedList<Request>();
    private final TreeSet<Reciever> recievers = new TreeSet<Reciever>();

    public Session() {
        super("session",9000,9000);
    }

    public Request requestPTY() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            SSHOutputStream stream = new SSHOutputStream(buffer);
            stream.writeString("vt100");
            stream.writeUInt32(80);
            stream.writeUInt32(24);
            stream.writeUInt32(640);
            stream.writeUInt32(480);
            stream.writeString("");
            synchronized (reqs) {
                Request r = new Request();
                reqs.addLast(r);
                conn.send(new ChannelRequest(remoteID,"pty-req",true,buffer.toByteArray()));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Request requestShell() {
        try {
            synchronized (reqs) {
                Request r = new Request();
                reqs.addLast(r);
                conn.send(new ChannelRequest(remoteID,"shell",true,new byte[0]));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addReciever(Reciever r) {
        recievers.add(r);
    }

    public void removeReciever(Reciever r) {
        recievers.remove(r);
    }

    public int send(byte[] data) {
        int max = Math.min(data.length, remoteWindow);
        byte[] copy = new byte[max];
        System.arraycopy(data, 0, copy, 0, max);
        try {
            conn.send(new ChannelData(remoteID,copy));
            return max;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected void open() throws IOException {
        System.out.println("Attempting to open interactive session");
        ChannelOpen open = new ChannelOpen(type,localID,localWindow,maxPacket,new byte[0]);
        conn.send(open);
    }

    public void recieve(Message m) throws IOException {
        switch (m.id) {
            case SSHNumbers.SSH_MSG_CHANNEL_OPEN_CONFIRMATION:
                status = Status.Opened;
                remoteID = ((ChannelOpenConfirm)m).sender;
                remoteWindow = ((ChannelOpenConfirm)m).windowSize;
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_OPEN_FAILURE:
                status = Status.Rejected;
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_SUCCESS:
                synchronized (reqs) {
                    Request r = reqs.removeFirst();
                    r.status = RequestStatus.Success;
                }
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_FAILURE:
                synchronized (reqs) {
                    Request r = reqs.removeFirst();
                    r.status = RequestStatus.Failure;
                }
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_CLOSE:
                status = Status.Closed;
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_WINDOW_ADJUST:
                ChannelWindowAdjust w = (ChannelWindowAdjust)m;
                //System.out.print("LW:"+localWindow+" RW:"+remoteWindow+ " => ");
                remoteWindow += w.size;
                //System.out.println("LW:"+localWindow+" RW:"+remoteWindow);
                return;
            case SSHNumbers.SSH_MSG_CHANNEL_DATA:
                ChannelData cd = (ChannelData)m;
                for (Reciever r : recievers) {
                    r.recieve(cd.data);
                }
                localWindow -= cd.data.length;
                if (localWindow < 10000) {
                    conn.send(new ChannelWindowAdjust(remoteID,20000));
                    localWindow += 20000;
                }
                //System.out.println("LW:" + localWindow);
                return;
            default:
                throw new IOException("Unhandled session message ID:"+m.id);
        }
    }

    protected void close() throws IOException {
        conn.send(new ChannelEOF(remoteID));
        conn.send(new ChannelClose(remoteID));
    }

}
