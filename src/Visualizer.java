import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Visualizer extends JButton {

    private static final int initialWindowHeight = 800;
    private static final int initialWindowWidth = 1700;
    private Rectangle2D.Double highlighter;
    private BufferedImage image;
    private ArrayList<Bit> allBitsAlice;
    private ArrayList<Scheme> allSchemesAlice;
    private ArrayList<Transmission> allTransmissionsAlice;
    private ArrayList<Transmission> allTransmissionsEve;
    private ArrayList<Scheme> allSchemesBob;
    private ArrayList<Scheme> allSchemesEve;
    private ArrayList<Bit> allBitsBob;
    private ArrayList<Bit> allBitsEve;
    private final int offsetX = 140;
    private double offsetY = initialWindowHeight / 9.0;
    private BufferedImage alice;
    private BufferedImage bob;
    private BufferedImage eve;
    private boolean eavesDropping = false;
    private double boxWidth;
    private int numBits = 42;
    private boolean demoMode = false;
    private double gap = 6;

    /**
     * A program to visualize the BB84 algorithm
     */
    public Visualizer(JFrame f) {

        Thread myHook = new Thread(() -> {
            writeSettings();
        });
        Runtime.getRuntime().addShutdownHook(myHook);

        loadImages();

        f.setTitle("BB 84 - Bennett-Brassard 1984");

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleComponentEvents();
            }
        });

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

        readSettings();

        creation();
    }

    private void writeSettings() {

        System.out.println("writeSettings ...");
        try {
            String uh = System.getProperty("user.home");
            FileOutputStream f = new FileOutputStream(uh + "/QuantumCryptography.bin");
            ObjectOutputStream os = new ObjectOutputStream(f);

            os.writeBoolean(eavesDropping);
            os.writeBoolean(demoMode);

            os.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSettings() {

        try {
            String uh = System.getProperty("user.home");
            FileInputStream f = new FileInputStream(uh + "/QuantumCryptography.bin");
            ObjectInputStream os = new ObjectInputStream(f);

            eavesDropping = os.readBoolean();
            demoMode = os.readBoolean();

            os.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void creation() {
        if (demoMode == true) {
            createAllPossibilities();
        } else {
            createAllRandom();
        }
    }

    /// create all bits and filters  ///////////////////////////////////////////////////////////////////////////////////
    private void createAllPossibilities() {

        numBits = 40;
        boxWidth = 1500.0 / numBits;
        if (boxWidth > 36) {
            boxWidth = 36;
        }
        double x = boxWidth;
        double y = offsetY;

        int[] pattern;

/////// Alice //////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Alice's bits 0 | 1 //////////////////////////////////////////////////////////////////////////////////
        allBitsAlice = new ArrayList<>();
        pattern = new int[]{0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1};
        int i = 0;
        gap = 0;
        for (int bit : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsAlice.add(new Bit(bit, xPos + gap, y, x, x));
        }

        /// create Alice's schemes 'x | +' /////////////////////////////////////////////////////////////////////////////
        allSchemesAlice = new ArrayList<>();
        pattern = new int[]{0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1};
        i = 0;
        gap = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allSchemesAlice.add(new Scheme(filter, xPos + gap, 2 * offsetY, x, x));
        }

        /// create Alice's transmissions '- \ / |' /////////////////////////////////////////////////////////////////////
        pattern = new int[]{0, 1, 2, 3, 0, 0, 1, 1, 0, 1, 2, 3};
        allTransmissionsAlice = new ArrayList<>();
        i = 0;
        gap = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allTransmissionsAlice.add(new Transmission(filter, xPos + gap, 3 * offsetY, x, x));
        }

/////// Eve ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 0, 1, 1};
        allSchemesEve = new ArrayList<>();
        i = 0;
        gap = 0;
        for (int scheme : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            Scheme s = new Scheme(scheme, xPos + gap, 4 * offsetY, x, x);
            s.setValidity(scheme == s.filter);
            allSchemesEve.add(s);
        }

        /// create Eve's transmissions /////////////////////////////////////////////////////////////////////////////////
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 2, 3};
        allTransmissionsEve = new ArrayList<>();
        i = 0;
        gap = 0;
        for (int transmission : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allTransmissionsEve.add(new Transmission(transmission, xPos + gap, 5 * offsetY, x, x));
        }

        /// create Eve's bits //////////////////////////////////////////////////////////////////////////////////////////
        allBitsEve = new ArrayList<>();
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 0, 1};
        i = 0;
        gap = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsEve.add(new Bit(filter, xPos + gap, 6 * offsetY, x, x));
        }

/////// Bob ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Bob's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        pattern = new int[]{0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1};
        i = 0;
        gap = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            Scheme s = new Scheme(filter, xPos + gap, 7 * offsetY, x, x);
            s.setValidity(filter == s.filter);
            allSchemesBob.add(s);
        }

        /// create Bob's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsBob = new ArrayList<>();
        pattern = new int[]{0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1};
        i = 0;
        gap = 0;
        for (int bit : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsBob.add(new Bit(bit, xPos + gap, 8 * offsetY, x, x));
        }

        highlighter = new Rectangle2D.Double(-100, offsetY, x, offsetY * 7 + boxWidth);
    }

    private void createAllRandom() {

        Random random = new Random();
        boxWidth = 1500.0 / numBits;
        if (boxWidth > 36) {
            boxWidth = 36;
        }
        double x = boxWidth;
        double y = offsetY;

        highlighter = new Rectangle2D.Double(-100, offsetY, x, offsetY * 7 + boxWidth);

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
        allTransmissionsAlice = new ArrayList<>();
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
            allTransmissionsAlice.add(new Transmission(theCase, xPos, 3 * offsetY, x, x));
        }

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesEve = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            int r = random.nextInt(2);
            Scheme s = new Scheme(r, xPos, 4 * offsetY, x, x);
            s.setValidity(r == s.filter);
            allSchemesEve.add(s);
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
        allTransmissionsEve = new ArrayList<>();
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
            allTransmissionsEve.add(new Transmission(theCase, xPos, 6 * offsetY, x, x));
        }

        /// create Bob's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            int r = random.nextInt(2);
            Scheme s = new Scheme(r, xPos, 7 * offsetY, x, x);
            s.setValidity(r == s.filter);
            allSchemesBob.add(s);
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

    private void addGaps(int i) {

        if (i == 4 || i == 6 || i == 8) {
            gap += 6;
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

        /// TODO: include again drawHeaders(g2d);
    }

    private void drawNumberOfBits(Graphics2D g2d) {

        g2d.setColor(MyColors.myOrange);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Number of bits: " + numBits, offsetX, 40);
    }

    private void drawAliceBobEve(Graphics2D g2d) {

        int indent = 30;
        g2d.setColor(MyColors.mySandLikeColor);

        g2d.drawImage(alice, indent, (int) (1.7 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        g2d.drawString("Alice", indent + 10, (int) (3 * offsetY));

        if (eavesDropping) {
            g2d.drawImage(eve, 30, (int) (4.7 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
            g2d.drawString("Eve", indent + 14, (int) (6 * offsetY));
        }

        g2d.drawImage(bob, indent, (int) (7.1 * offsetY), alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        g2d.drawString("Bob", indent + 14, (int) (8.38 * offsetY));
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
        for (Transmission t : allTransmissionsAlice) {
            t.draw(g2d);
        }
        if (eavesDropping) {

            g2d.setColor(MyColors.mySandLikeColor);
            g2d.draw(new Rectangle2D.Double(10, 4 * offsetY - 10, getWidth() - 20, 2.64 * offsetY));

            for (int i = 0; i < allSchemesEve.size(); i++) {
                Scheme s = allSchemesEve.get(i);
                allSchemesEve.get(i).draw(g2d);
            }
            for (Bit b : allBitsEve) {
                b.draw(g2d);
            }
            for (Transmission t : allTransmissionsEve) {
                t.draw(g2d);
            }
        }

        for (int i = 0; i < allSchemesBob.size(); i++) {
            Scheme s = allSchemesBob.get(i);
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
        g2d.drawString("Alice's schemes - " + str, offsetX, (int) (2 * offsetY - upShiftHeader));
        str = getPercentSchemes(allSchemesBob);
        g2d.drawString("Bob's schemes - " + str, offsetX, (int) (7 * offsetY - upShiftHeader));
        str = getPercentBitString(allBitsAlice);
        g2d.drawString("Alice's random bit-string - " + str, offsetX, (int) (offsetY - upShiftHeader));
        str = getPercentBitString(allBitsBob);
        g2d.drawString("Bob's bit-string - " + str, offsetX, (int) (8 * offsetY - upShiftHeader));
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

    /// handle events //////////////////////////////////////////////////////////////////////////////////////////////////

    private void handleComponentEvents() {
        offsetY = getHeight() / 9.0;
        creation();
        repaint();
    }

    private void handleKeyPress(KeyEvent e) {

//        System.out.println("code: " + e.getKeyCode());

        switch (e.getKeyCode()) {

            case KeyEvent.VK_SPACE:
                creation();
                break;
            case KeyEvent.VK_UP:
                numBits += 2;
                if (numBits > 128) {
                    numBits = 128;
                } else {
                    creation();
                }
                break;
            case KeyEvent.VK_DOWN:
                numBits -= 2;
                if (numBits < 10) {
                    numBits = 10;
                } else {
                    creation();
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
                creation();
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                demoMode = !demoMode;
                createAllPossibilities();
                break;
            case KeyEvent.VK_E:
                eavesDropping = !eavesDropping;
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
                creation();
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

        for (MyBox b : allBitsAlice) {
            Rectangle2D.Double rect = new Rectangle2D.Double(b.x, 0, boxWidth, getHeight());
            if (rect.contains(e.getX(), e.getY())) {
                highlighter.x = b.x;
            }
        }
        repaint();
    }

    /// last but not least the main function ///////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame();
            Visualizer v = new Visualizer(f);
            f.add(v);
            f.setSize(initialWindowWidth, initialWindowHeight);
            f.setLocation(0, 0);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
