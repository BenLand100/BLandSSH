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
