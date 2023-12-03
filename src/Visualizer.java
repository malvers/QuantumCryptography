import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Visualizer extends JButton {

    private static final int height = 800;
    private static final int width = 1700;
    private Rectangle2D.Double highlighter;
    private BufferedImage image;
    private ArrayList<Bit> allBitsAlice;
    private ArrayList<Scheme> allSchemesAlice;
    private ArrayList<Transmission> allTransmissions;
    private ArrayList<Scheme> allSchemesBob;
    private ArrayList<Scheme> allSchemesEve;
    private ArrayList<Bit> allBitsBob;
    private final int offsetY = 80;
    private BufferedImage alice;
    private BufferedImage bob;
    private BufferedImage eve;
    private boolean drawEve = false;
    private int offsetX;

    public Visualizer() {

        loadImages();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            private void handleMouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                highlighter.x = e.getX();
                repaint();
            }
        });

        createAll();
    }

    private void createAll() {

        Random random = new Random();
        double x = MyBox.width;
        double y = offsetY;

        highlighter = new Rectangle2D.Double(-100, offsetY, x, offsetY * 6 + MyBox.width);

        offsetX = 160;

        /// create Alice's random bit-string ///////////////////////////////////////////////////////////////////////////
        allBitsAlice = new ArrayList<>();
        int numBits = 64;
        for (int i = 0; i < numBits; i++) {
            int bit = random.nextInt(2);
            double xPos = x * i + offsetX;
            allBitsAlice.add(new Bit(bit, xPos, y, x, x));
        }

        /// create Alice's random schemes //////////////////////////////////////////////////////////////////////////////
        allSchemesAlice = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesAlice.add(new Scheme(random.nextInt(2), xPos, y + offsetY, x, x));
        }

        /// create transmissions ///////////////////////////////////////////////////////////////////////////////////////
        allTransmissions = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {

            double xPos = x * i + offsetX;
            int theCase = -1;
            if (allBitsAlice.get(i).theBit == 1 && allSchemesAlice.get(i).filter == 0) {
                theCase = 0;
            } else if (allBitsAlice.get(i).theBit == 0 && allSchemesAlice.get(i).filter == 0) {
                theCase = 1;
            } else if (allBitsAlice.get(i).theBit == 0 && allSchemesAlice.get(i).filter == 1) {
                theCase = 2;
            } else if (allBitsAlice.get(i).theBit == 1 && allSchemesAlice.get(i).filter == 1) {
                theCase = 3;
            }
            allTransmissions.add(new Transmission(theCase, xPos, y + 2 * offsetY, x, x));
        }

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesEve = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesEve.add(new Scheme(random.nextInt(2), xPos, 4 * offsetY, x, x));
        }

        /// create Bob's es ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesBob.add(new Scheme(random.nextInt(2), xPos, 6 * offsetY, x, x));
        }

        /// create Bob's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            int bit = -1;
            if (allSchemesBob.get(i).filter == allSchemesAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }
            double xPos = x * i + offsetX;
            allBitsBob.add(new Bit(bit, xPos, 7 * offsetY, x, x));
        }
    }

    private void loadImages() {
        try {
            image = ImageIO.read(new File("universe.png"));
            alice = ImageIO.read(new File("alice.png"));
            bob = ImageIO.read(new File("bob.png"));
            eve = ImageIO.read(new File("eve.png"));
        } catch (IOException e) {
            System.out.println("clouds not found :-/");
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int) (MyBox.width / 1.8)));

        drawBackgroundImage(g2d);

        drawAliceBobEve(g2d);

        drawHighlighter(g2d);

        drawPhotonStuff(g2d);
    }

    private void drawAliceBobEve(Graphics2D g2d) {
        g2d.drawImage(alice, 30, offsetY, alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        g2d.drawImage(bob, 30, 6 * offsetY, alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        if (drawEve) {
            g2d.drawImage(eve, 30, 4 * offsetY, alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        }
    }

    private void drawHighlighter(Graphics2D g2d) {

        g2d.setColor(Color.WHITE);

        highlighter.x = Math.round(highlighter.x / MyBox.width) * MyBox.width + MyBox.width / 2.0 - 5;
        g2d.fill(highlighter);
    }

    private void drawPhotonStuff(Graphics2D g2d) {

        int upShiftHeader = 6;
        for (Bit b : allBitsAlice) {
            b.draw(g2d);
        }
        g2d.setColor(MyColors.mySandLikeColor);
        String str = getPercentBitString(allBitsAlice);
        g2d.drawString("Alice's random bit-string - " + str, offsetX, offsetY - upShiftHeader);

        for (Scheme s : allSchemesAlice) {
            s.draw(g2d);
        }
        for (Transmission t : allTransmissions) {
            t.draw(g2d);
        }

        if (drawEve) {
            for (Scheme b : allSchemesEve) {
                b.draw(g2d);
            }
        }

        for (int i = 0; i < allSchemesBob.size(); i++) {
            Scheme s = allSchemesBob.get(i);
            if (allSchemesAlice.get(i).filter == s.filter) {
                s.setVilidity(true);
            } else {
                s.setVilidity(false);
            }
            allSchemesBob.get(i).draw(g2d);
        }

        for (Bit b : allBitsBob) {
            b.draw(g2d);
        }
        g2d.setColor(MyColors.mySandLikeColor);
        str = getPercentBitString(allBitsBob);
        g2d.drawString("Bob's bit-string - " + str, offsetX, 7 * offsetY - upShiftHeader);
    }

    public String getPercentBitString(ArrayList<Bit> list) {

        int count0 = 0;
        int count1 = 0;
        int countArbitrary = 0;
        for (Bit b : list) {
            if (b.theBit == 1) {
                count1++;
            } else if (b.theBit < 0) {
                countArbitrary++;
            } else if (b.theBit == 0) {
                count0++;
            }
        }
        int percent1 = (count1 * 100) / list.size();
        int percent0 = (count0 * 100) / list.size();
        String str = percent1 + " % '1' | " + percent0 + " % '0'";

        if (countArbitrary > 0) {
            str += " - may be wrong " + (100 - percent1 - percent0) + " %";
        }

        return str;
    }

    private void drawBackgroundImage(Graphics2D g2d) {

        if (image == null) {
            return;
        }
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), Color.BLACK, this);
    }

    private void handleKeyPress(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_SPACE:
                createAll();
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_UP:
                break;
            case KeyEvent.VK_DOWN:
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                break;
            case KeyEvent.VK_E:
                drawEve = !drawEve;
                break;
            case KeyEvent.VK_H:
                break;
            case KeyEvent.VK_I:
                break;
            case KeyEvent.VK_P:
                break;
            case KeyEvent.VK_T:
                break;
            case KeyEvent.VK_ESCAPE:
                highlighter.x = -100;
                break;
            case KeyEvent.VK_W:
                System.exit(0);
                break;
        }
        repaint();

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame();
            Visualizer v = new Visualizer();
            f.add(v);
            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {

                }

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    // This will be called when the window is closed
                    System.out.println("Window is closed");
                    // Add your custom logic here
                }
            });
            f.setSize(width, height);
            f.setLocation(0, 0);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
