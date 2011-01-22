package blandssh;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 *
 * @author benland100
 */
public class Terminal extends JPanel implements Reciever {

    int cols = 80, rows = 24, width = 640, height = 480;
    int col = 0, row = 0, lcol = 0, lrow = 0;
    int scrollU = 0, scrollL = rows-1;
    Attribute current = new Attribute();
    Attribute[][] attribs = new Attribute[rows][cols];
    byte[][] chars = new byte[rows][cols];

    Session s;


    public Terminal(Session s) {
        this.s = s;
        s.addReciever(this);
        setMaximumSize(new Dimension(width,height));
        setMinimumSize(new Dimension(width,height));
        setPreferredSize(new Dimension(width,height));
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                attribs[r][c] = new Attribute();
            }
        }
    }
    
    public void paint(Graphics g) {
        int perrow = height / rows;
        int percol = width / cols;
        double percent = 0.65;
        int size = (int)(perrow * percent);
        int off = (int)(perrow * (percent + (1-percent)/2));
        g.setFont(new Font(Font.MONOSPACED,Font.BOLD,size));
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                g.setColor(x==col&&y==row ? Color.GRAY : attribs[y][x].background);
                g.fillRect(percol*x,perrow*y,percol,perrow);
                g.setColor(attribs[y][x].foreground);
                g.drawBytes(chars[y], x, 1, x*percol, y*perrow + off);
            }
        }
        
    }
    
    private void carriagereturn() {
        col = 0;
    }

    private void reverselinefeed() {
        System.out.println(scrollU + "-" + scrollL);
        if (row == scrollU) {
            for (int y = scrollL; y > scrollU; y--) {
                System.arraycopy(chars[y-1], 0, chars[y], 0, cols);
                System.arraycopy(attribs[y-1], 0, attribs[y], 0, cols);
            }
            chars[scrollU] = new byte[cols];
            for (int c = 0; c < cols; c++) {
                attribs[scrollU][c] = new Attribute();
            }
        } else {
            row--;
        }

    }

    private void linefeed() {
        if (row == scrollL) {
            for (int y = scrollU+1; y <= scrollL; y++) {
                System.arraycopy(chars[y], 0, chars[y-1], 0, cols);
                System.arraycopy(attribs[y], 0, attribs[y-1], 0, cols);
            }
            chars[scrollL] = new byte[cols];
            for (int c = 0; c < cols; c++) {
                attribs[scrollL][c] = new Attribute();
            }
        } else {
            row++;
        }
    }

    private void dec() {
        col--;
    }

    private void inc() {
        col++;
    }

    private void setcol(int x) {
        col = x;
        if (col < 0) col = 0;
        if (col >= cols) row = cols-1;
    }

    private void inccol(int x) {
        setcol(col+x);
    }

    private void deccol(int x) {
        setcol(col-x);
    }

    private void setrow(int y) {
        //System.out.println("SETROW:" + row + " ["+scrollU+","+scrollL+"]");
        row = y;
        if (row < 0) row = 0;
        if (row >= rows) row = rows-1;
    }

    private void incrow(int y) {
        setrow(row+y);
    }

    private void decrow(int y) {
        setrow(row-y);
    }

    private void setpos(int r, int c) {
        setrow(r);
        setcol(c);
    }

    private void savepos() {
        lcol = col;
        lrow = row;
    }

    private void restorepos() {
        col = lcol;
        row = lrow;
    }

    private void erasescreen(int mode) {
        switch (mode) {
            case 0:
                for (int c = col; c < cols; c++) {
                    chars[row][c] = 0;
                    attribs[row][c] = new Attribute();
                }
                for (int r = row+1; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        chars[r][c] = 0;
                        attribs[r][c] = new Attribute();
                    }
                }
                break;
            case 1:
                for (int r = 0; r < row; r++) {
                    for (int c = 0; c < cols; c++) {
                        chars[r][c] = 0;
                        attribs[r][c] = new Attribute();
                    }
                }
                for (int c = 0; c <= col; c++) {
                    chars[row][c] = 0;
                    attribs[row][c] = new Attribute();
                }
                break;
            case 2:
                chars = new byte[rows][cols];
                col = row = 0;
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        attribs[r][c] = new Attribute();
                    }
                }
        }
    }

    private void eraseline(int mode) {
        switch (mode) {
            case 0:
                for (int x = col; x < cols; x++) {
                    chars[row][x] = 0;
                    attribs[row][x] = new Attribute();
                }
                break;
            case 1:
                for (int x = 0; x <= col; x++) {
                    chars[row][x] = 0;
                    attribs[row][x] = new Attribute();
                }
                break;
            case 2:
                for (int x = 0; x < cols; x++) {
                    chars[row][x] = 0;
                    attribs[row][x] = new Attribute();
                }
        }
    }

    private void setscroll(int u, int l) {
        scrollU = u;
        scrollL = l;
    }

    private void put(byte b) {
        if (col >= cols) {
            linefeed();
            carriagereturn();
        }
        chars[row][col] = b;
        current.apply(attribs[row][col]);
    }

    public void recieve(byte[] bytes) {
        //System.out.println(new String(bytes));
        //System.out.println(Arrays.toString(bytes));
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b < 32) {
                switch (b) {
                    case 0:
                        break;
                    case 8:
                        dec();
                        break;
                    case 10:
                        linefeed();
                        break;
                    case 13:
                        carriagereturn();
                        break;
                    case 27:
                        int c = i + 1;
                        byte n = bytes[c];
                        if (n == '[') {
                            do {
                                c++;
                            } while (bytes[c] < 64 || bytes[c] > 126);
                            String seq = new String(bytes,i+2,c-i-2);
                            byte id = bytes[c];
                            //System.out.println("Escape: " + seq + (char)id);
                            switch (id) {
                                case 'H':
                                case 'f': {
                                    String[] pos = seq.split("\\;");
                                    if (pos.length == 2) {
                                        setpos(Integer.parseInt(pos[0])-1,Integer.parseInt(pos[1])-1);
                                    } else {
                                        setpos(0,0);
                                    }
                                } break;
                                case 'r': {
                                    String[] pos = seq.split("\\;");
                                    if (pos.length == 2) {
                                        setscroll(Integer.parseInt(pos[0])-1,Integer.parseInt(pos[1])-1);
                                    } else {
                                        setscroll(0,rows-1);
                                    }
                                    } break;
                                case 'A':
                                    decrow(seq.length()==0?1:Integer.parseInt(seq));
                                    break;
                                case 'B':
                                    incrow(seq.length()==0?1:Integer.parseInt(seq));
                                    break;
                                case 'C':
                                    inccol(seq.length()==0?1:Integer.parseInt(seq));
                                    break;
                                case 'D':
                                    deccol(seq.length()==0?1:Integer.parseInt(seq));
                                    break;
                                case 's':
                                    savepos();
                                    break;
                                case 'u':
                                    restorepos();
                                    break;
                                case 'J':
                                    erasescreen(seq.length()==0?0:Integer.parseInt(seq));
                                    break;
                                case 'K':
                                    eraseline(seq.length()==0?0:Integer.parseInt(seq));
                                    break;
                                case 'm':
                                    for (String s : seq.split("\\;")) {
                                        switch (Integer.parseInt(s)) {
                                            case 0:
                                                current = new Attribute();
                                                break;
                                            case 1:
                                                current.bold = true;
                                                break;
                                            case 4:
                                                current.underscore = true;
                                                break;
                                            case 5:
                                                current.blink = true;
                                                break;
                                            case 7:
                                                current.negative = true;
                                                break;
                                            case 8:
                                                current.invisible = true;
                                                break;
                                            case 30:
                                                current.foreground = Color.BLACK;
                                                break;
                                            case 31:
                                                current.foreground = Color.RED;
                                                break;
                                            case 32:
                                                current.foreground = Color.GREEN;
                                                break;
                                            case 33:
                                                current.foreground = Color.YELLOW;
                                                break;
                                            case 34:
                                                current.foreground = Color.BLUE;
                                                break;
                                            case 35:
                                                current.foreground = Color.MAGENTA;
                                                break;
                                            case 36:
                                                current.foreground = Color.CYAN;
                                                break;
                                            case 37:
                                                current.foreground = Color.WHITE;
                                                break;
                                            case 40:
                                                current.background = Color.BLACK;
                                                break;
                                            case 41:
                                                current.background = Color.RED;
                                                break;
                                            case 42:
                                                current.background = Color.GREEN;
                                                break;
                                            case 43:
                                                current.background = Color.YELLOW;
                                                break;
                                            case 44:
                                                current.background = Color.BLUE;
                                                break;
                                            case 45:
                                                current.background = Color.MAGENTA;
                                                break;
                                            case 46:
                                                current.background = Color.CYAN;
                                                break;
                                            case 47:
                                                current.background = Color.WHITE;
                                                break;
                                            default:
                                                System.out.println("Unknown formatting: " + s);
                                        }
                                    }
                                    break;
                                case 'h':
                                case 'l':
                                case 'p':
                                    System.out.println("UNDEFINED ESCAPE: " + seq + (char)id);
                            }
                            i = c;
                            break;
                        } else if (n < 96 && n > 63) {
                            switch (n) {
                                case 'D':
                                    linefeed();
                                    break;
                                case 'E':
                                    linefeed();
                                    carriagereturn();
                                    break;
                                case 'M':
                                    reverselinefeed();
                                    carriagereturn();
                                    break;
                                default:
                                    System.out.println("UNDEFINED ESCAPE CHAR: " + n);
                            }
                            i = c;
                            break;
                        }
                        System.out.println("Fail: " + (char)n);
                        break;
                    default:
                        System.out.println(b);
                }
            } else {
                put(b);
                inc();
            }
        }
        repaint();
    }
    
    public void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            s.send(new byte[]{(byte)e.getKeyChar()});
        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    s.send(new byte[]{27,'[','A'});
                    break;
                case KeyEvent.VK_DOWN:
                    s.send(new byte[]{27,'[','B'});
                    break;
                case KeyEvent.VK_RIGHT:
                    s.send(new byte[]{27,'[','C'});
                    break;
                case KeyEvent.VK_LEFT:
                    s.send(new byte[]{27,'[','D'});
                    break;
                case KeyEvent.VK_PAGE_UP:
                    s.send(new byte[]{27,'[','5','~'});
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    s.send(new byte[]{27,'[','6','~'});
                    break;
            }
        }
    }

    private static class Attribute {
        Color foreground, background;
        boolean bold,underscore,blink,negative,invisible;

        Attribute() {
            foreground = Color.BLACK;
            background = Color.WHITE;
        }

        void alloff() {
            bold = false;
            underscore = false;
            blink = false;
            negative = false;
        }

        void apply(Attribute other) {
            other.foreground = foreground;
            other.background = background;
            other.blink = blink;
            other.bold = bold;
            other.invisible = invisible;
            other.negative = negative;
            other.underscore = underscore;
        }
    }

}
