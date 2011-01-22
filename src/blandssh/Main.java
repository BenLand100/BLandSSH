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

import java.net.Socket;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("127.0.0.1",22);
        final SSHConnection conn = new SSHConnection(s);
        conn.connect();
        conn.auth(args[0], args[1]);
        Thread thread = new Thread("ssh-process") {
            public void run() {
                try {
                    conn.process();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        Session session = new Session();
        session.openChannel(conn);
        while (session.getStatus() == SSHChannel.Status.Opening) {
            Thread.sleep(500);
        }
        System.out.println("Interactive session was: " + session.getStatus());

        JFrame f = new JFrame("SSHTerm");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Terminal term = new Terminal(session);
        f.add(term);
        f.pack();
        f.setVisible(true);

        Session.Request pty = session.requestPTY();
        Session.Request shell = session.requestShell();
        while (pty.getStatus() == Session.RequestStatus.Waiting && pty.getStatus() == Session.RequestStatus.Waiting) {
            Thread.sleep(500);
        }
        
    }

}
