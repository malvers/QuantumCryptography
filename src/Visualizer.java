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
    private static final int windowHeight = 800;
    private static final int windowWidth = 1700;
    private Rectangle2D.Double highlighter;
    private BufferedImage image;
    private ArrayList<Bit> allBitsAlice;
    private ArrayList<Scheme> allSchemesAlice;
    private ArrayList<Transmission> alicesTransmissions;
    private ArrayList<Transmission> evesTransmissions;
    private ArrayList<Scheme> allSchemesBob;
    private ArrayList<Scheme> allSchemesEve;
    private ArrayList<Bit> allBitsBob;
    private ArrayList<Bit> allBitsEve;
    private final int offsetX = 140;
    private final int offsetY = windowHeight / 9;
    private BufferedImage alice;
    private BufferedImage bob;
    private BufferedImage eve;
    private boolean drawEve = false;
    private double boxWidth;
    private int numBits = 42;

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
                handleMouse(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouse(e);
            }
        });

        createAll();
    }

    private void createAll() {

        Random random = new Random();

        boxWidth = 1500.0 / numBits;
        if (boxWidth > 36) {
            boxWidth = 36;
        }
//        System.out.println("boxWidth: " + boxWidth);

        double x = boxWidth;
        double y = offsetY;

        highlighter = new Rectangle2D.Double(-100, offsetY, x, offsetY * 6 + boxWidth);

        /// create Alice's random bit-string ///////////////////////////////////////////////////////////////////////////
        allBitsAlice = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            int bit = random.nextInt(2);
            double xPos = x * i + offsetX;
            allBitsAlice.add(new Bit(bit, xPos, y, x, x));
        }

        /// create Alice's random schemes //////////////////////////////////////////////////////////////////////////////
        allSchemesAlice = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesAlice.add(new Scheme(random.nextInt(2), xPos, 2 * offsetY, x, x));
        }

        /// create Alice's transmissions ///////////////////////////////////////////////////////////////////////////////
        alicesTransmissions = new ArrayList<>();
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
            alicesTransmissions.add(new Transmission(theCase, xPos, 3 * offsetY, x, x));
        }

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesEve = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesEve.add(new Scheme(random.nextInt(2), xPos, 4 * offsetY, x, x));
        }

        /// create Eve's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsEve = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            int bit = -1;
            if (allSchemesEve.get(i).filter == allSchemesAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }
            double xPos = x * i + offsetX;
            allBitsEve.add(new Bit(bit, xPos, 5 * offsetY, x, x));
        }

        /// create Eve's transmissions /////////////////////////////////////////////////////////////////////////////////
        evesTransmissions = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {

            double xPos = x * i + offsetX;
            int theCase = -1;
            if (allBitsEve.get(i).theBit == 1 && allSchemesAlice.get(i).filter == 0) {
                theCase = 0;
            } else if (allBitsEve.get(i).theBit == 0 && allSchemesAlice.get(i).filter == 0) {
                theCase = 1;
            } else if (allBitsEve.get(i).theBit == 0 && allSchemesAlice.get(i).filter == 1) {
                theCase = 2;
            } else if (allBitsEve.get(i).theBit == 1 && allSchemesAlice.get(i).filter == 1) {
                theCase = 3;
            }
            evesTransmissions.add(new Transmission(theCase, xPos, 6 * offsetY, x, x));
        }

        /// create Bob's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesBob.add(new Scheme(random.nextInt(2), xPos, 7 * offsetY, x, x));
        }

        /// create Bob's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            int bit = -1;
            if (allSchemesBob.get(i).filter == allSchemesAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }
            double xPos = x * i + offsetX;
            allBitsBob.add(new Bit(bit, xPos, 8 * offsetY, x, x));
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

    /// paint section //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackgroundImage(g2d);

        /// draw after here :-) ////////////////////////////////////////////////////////////////////////////////////////

        drawNumberOfBits(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, (int) (boxWidth / 1.8)));

        drawAliceBobEve(g2d);

        drawHighlighter(g2d, true);

        drawPhotonStuff(g2d);

        drawHighlighter(g2d, false);

        drawHeaders(g2d);
    }

    private void drawNumberOfBits(Graphics2D g2d) {

        g2d.setColor(MyColors.myOrange);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Number of bits: " + numBits, offsetX, 40);
    }

    private void drawAliceBobEve(Graphics2D g2d) {

        g2d.drawImage(alice, 30, (int) (1.7 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        g2d.drawImage(bob, 30, (int) (7.1 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        if (drawEve) {
            g2d.drawImage(eve, 30, (int) (4.7 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        }
    }

    private void drawHighlighter(Graphics2D g2d, boolean fill) {

        if (fill) {
            g2d.setColor(MyColors.myOrange);
            g2d.fill(highlighter);
        } else {
            g2d.setColor(MyColors.myLightGray);
            g2d.draw(highlighter);
        }
    }

    private void drawPhotonStuff(Graphics2D g2d) {

        for (Bit b : allBitsAlice) {
            b.draw(g2d);
        }
        for (Scheme s : allSchemesAlice) {
            s.draw(g2d);
        }
        for (Transmission t : alicesTransmissions) {
            t.draw(g2d);
        }
        if (drawEve) {

            for (int i = 0; i < allSchemesEve.size(); i++) {
                Scheme s = allSchemesEve.get(i);
                s.setValidity(allSchemesAlice.get(i).filter == s.filter);
                allSchemesEve.get(i).draw(g2d);
            }
            for (Bit b : allBitsEve) {
                b.draw(g2d);
            }
            for (Transmission t : evesTransmissions) {
                t.draw(g2d);
            }
        }

        for (int i = 0; i < allSchemesBob.size(); i++) {
            Scheme s = allSchemesBob.get(i);
            s.setValidity(allSchemesAlice.get(i).filter == s.filter);
            allSchemesBob.get(i).draw(g2d);
        }

        for (Bit b : allBitsBob) {
            b.draw(g2d);
        }
    }

    private void drawHeaders(Graphics2D g2d) {

        int upShiftHeader = 6;
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(MyColors.mySandLikeColor);

        String str;

        str = getPercentSchemes(allSchemesAlice);
        g2d.drawString("Alice's schemes - " + str, offsetX, 2 * offsetY - upShiftHeader);
        str = getPercentSchemes(allSchemesBob);
        g2d.drawString("Bob's schemes - " + str, offsetX, 7 * offsetY - upShiftHeader);
        str = getPercentBitString(allBitsAlice);
        g2d.drawString("Alice's random bit-string - " + str, offsetX, offsetY - upShiftHeader);
        str = getPercentBitString(allBitsBob);
        g2d.drawString("Bob's bit-string - " + str, offsetX, 8 * offsetY - upShiftHeader);
    }

    public String getPercentSchemes(ArrayList<Scheme> list) {

        int count0 = 0;
        int count1 = 0;
        int countArbitrary = 0;
        for (Scheme b : list) {
            if (b.filter == 1) {
                count1++;
            } else if (!b.valid) {
                countArbitrary++;
            } else if (b.filter == 0) {
                count0++;
            }
        }
        int percent1 = (count1 * 100) / list.size();
        int percent0 = (count0 * 100) / list.size();
        String str = percent1 + " % '+' - " + (100 - percent1) + " % 'x'";

        if (countArbitrary > 0) {
            str += " - may be wrong " + (100 - percent1 - percent0) + " %";
        }

        return str;
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
        String str = percent1 + " % '1' - " + percent0 + " % '0'";

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

    /// handle key and mouse events ////////////////////////////////////////////////////////////////////////////////////
    private void handleKeyPress(KeyEvent e) {

//        System.out.println("code: " + e.getKeyCode());

        switch (e.getKeyCode()) {

            case KeyEvent.VK_SPACE:
                createAll();
                break;
            case KeyEvent.VK_UP:
                numBits += 2;
                if (numBits > 128) {
                    numBits = 128;
                } else {
                    createAll();
                }
                break;
            case KeyEvent.VK_DOWN:
                numBits -= 2;
                if (numBits < 10) {
                    numBits = 10;
                } else {
                    createAll();
                }
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_4:
                numBits = 42;
                createAll();
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
            case KeyEvent.VK_M:
                if (e.isShiftDown()) {
                    numBits = 256;
                } else {
                    numBits = 4;
                }
                createAll();
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

    private void handleMouse(MouseEvent e) {
        highlighter.x = Math.round((e.getX() - offsetX) / boxWidth) * boxWidth + offsetX;
        if (highlighter.x < offsetX) {
            highlighter.x = offsetX;
        }
        repaint();
    }

    /// last but not least the main function ///////////////////////////////////////////////////////////////////////////
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
            f.setSize(windowWidth, windowHeight);
            f.setLocation(0, 0);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
