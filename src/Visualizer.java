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
    private final ReadExplanations explanations;
    private BufferedImage image;
    private ArrayList<Bit> allBitsAlice;
    private ArrayList<Scheme> allSchemesAlice;
    private ArrayList<Polarisation> allPolarizationsAlice;
    private ArrayList<Polarisation> allPolarizationsEve;
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
    private double bitBoxWidth;
    private int numberOfBits = 42;
    private boolean demoMode = false;
    private double gapForDemo = 6;
    private Rectangle2D.Double toolTip;
    private String toolTipText = "";
    private Rectangle2D.Double highlighter;
    private int highlighterBitPosition = 0;
    private boolean showHighLighter = false;

    /**
     * A program to visualize the BB84 algorithm
     */
    public Visualizer(JFrame f) {

        Thread myHook = new Thread(this::writeSettings);
        Runtime.getRuntime().addShutdownHook(myHook);

        loadImages();

        explanations = new ReadExplanations();

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
        if (demoMode) {
            createAllPossibilities();
        } else {
            createAllRandom();
        }
        handleLeftRight(0);
    }

    /// create all bits and filters  ///////////////////////////////////////////////////////////////////////////////////
    private void createAllPossibilities() {

        bitBoxWidth = 1500.0 / numberOfBits;
        if (bitBoxWidth > 36) {
            bitBoxWidth = 36;
        }
        double x = bitBoxWidth;
        double y = offsetY;

        int[] pattern;

/////// Alice //////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Alice's bits 0 | 1 //////////////////////////////////////////////////////////////////////////////////
        allBitsAlice = new ArrayList<>();
        pattern = new int[]{0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1};
        int i = 0;
        gapForDemo = 0;
        for (int bit : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsAlice.add(new Bit(bit, xPos + gapForDemo, y, x, x));
        }

        /// create Alice's schemes 'x | +' /////////////////////////////////////////////////////////////////////////////
        allSchemesAlice = new ArrayList<>();
        pattern = new int[]{0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
        i = 0;
        gapForDemo = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allSchemesAlice.add(new Scheme(filter, xPos + gapForDemo, 2 * offsetY, x, x));
        }

        /// create Alice's polarizations '- \ / |' /////////////////////////////////////////////////////////////////////
        pattern = new int[]{0, 1, 2, 3, 0, 0, 1, 1, 0, 1, 2, 3, 0, 0, 2, 2, 1, 1, 3, 3};
        allPolarizationsAlice = new ArrayList<>();
        i = 0;
        gapForDemo = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allPolarizationsAlice.add(new Polarisation(filter, xPos + gapForDemo, 3 * offsetY, x, x));
        }

/////// Eve ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0};
        allSchemesEve = new ArrayList<>();
        i = 0;
        gapForDemo = 0;
        for (int scheme : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            Scheme s = new Scheme(scheme, xPos + gapForDemo, 4 * offsetY, x, x);
            s.setValidity(scheme == s.filter);
            allSchemesEve.add(s);
        }

        /// create Eve's bits //////////////////////////////////////////////////////////////////////////////////////////
        allBitsEve = new ArrayList<>();
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
        i = 0;
        gapForDemo = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsEve.add(new Bit(filter, xPos + gapForDemo, 5 * offsetY, x, x));
        }
        /// create Eve's polarizations /////////////////////////////////////////////////////////////////////////////////
        pattern = new int[]{-2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 2, 3, -1, -1, -1, -1, -1, -1, -1, -1};
        allPolarizationsEve = new ArrayList<>();
        i = 0;
        gapForDemo = 0;
        for (int polarization : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            Polarisation t = new Polarisation(polarization, xPos + gapForDemo, 6 * offsetY, x, x);
//            t.set
            allPolarizationsEve.add(t);
        }


/////// Bob ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /// create Bob's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        pattern = new int[]{0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
        i = 0;
        gapForDemo = 0;
        for (int filter : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            Scheme s = new Scheme(filter, xPos + gapForDemo, 7 * offsetY, x, x);
            s.setValidity(filter == s.filter);
            allSchemesBob.add(s);
        }

        /// create Bob's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsBob = new ArrayList<>();
        pattern = new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
        i = 0;
        gapForDemo = 0;
        for (int bit : pattern) {
            double xPos = x * (i++) + offsetX;
            addGaps(i);
            allBitsBob.add(new Bit(bit, xPos + gapForDemo, 8 * offsetY, x, x));
        }

        numberOfBits = allBitsAlice.size();

        highlighter = new Rectangle2D.Double(offsetX, offsetY, x, offsetY * 7 + bitBoxWidth);

        toolTip = new Rectangle2D.Double(offsetX, bitBoxWidth / 2.0, getWidth() - offsetX - bitBoxWidth, bitBoxWidth);
    }

    private void createAllRandom() {

        numberOfBits = 42;

        Random random = new Random();
        bitBoxWidth = 1500.0 / numberOfBits;
        if (bitBoxWidth > 36) {
            bitBoxWidth = 36;
        }
        double x = bitBoxWidth;
        double y = offsetY;

        highlighter = new Rectangle2D.Double(offsetX, offsetY, x, offsetY * 7 + bitBoxWidth);

        /// create Alice's random bit-string ///////////////////////////////////////////////////////////////////////////
        allBitsAlice = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            int bit = random.nextInt(2);
            double xPos = x * i + offsetX;
            allBitsAlice.add(new Bit(bit, xPos, y, x, x));
        }

        /// create Alice's random schemes //////////////////////////////////////////////////////////////////////////////
        allSchemesAlice = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            double xPos = x * i + offsetX;
            allSchemesAlice.add(new Scheme(random.nextInt(2), xPos, 2 * offsetY, x, x));
        }

        /// create Alice's polarizations ///////////////////////////////////////////////////////////////////////////////
        allPolarizationsAlice = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {

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
            allPolarizationsAlice.add(new Polarisation(theCase, xPos, 3 * offsetY, x, x));
        }

        /// create Eve's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesEve = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            double xPos = x * i + offsetX;
            int r = random.nextInt(2);
            Scheme s = new Scheme(r, xPos, 4 * offsetY, x, x);
            s.setValidity(r == s.filter);
            allSchemesEve.add(s);
        }

        /// create Eve's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsEve = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            int bit = -1;
            if (allSchemesEve.get(i).filter == allSchemesAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }
            double xPos = x * i + offsetX;
            allBitsEve.add(new Bit(bit, xPos, 5 * offsetY, x, x));
        }

        /// create Eve's polarizations /////////////////////////////////////////////////////////////////////////////////
        allPolarizationsEve = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {

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
            allPolarizationsEve.add(new Polarisation(theCase, xPos, 6 * offsetY, x, x));
        }

        /// create Bob's schemes ///////////////////////////////////////////////////////////////////////////////////////
        allSchemesBob = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            double xPos = x * i + offsetX;
            int r = random.nextInt(2);
            Scheme s = new Scheme(r, xPos, 7 * offsetY, x, x);
            s.setValidity(r == s.filter);
            allSchemesBob.add(s);
        }

        /// create Bob's bit-string ////////////////////////////////////////////////////////////////////////////////////
        allBitsBob = new ArrayList<>();
        for (int i = 0; i < numberOfBits; i++) {
            int bit = -1;
            if (allSchemesBob.get(i).filter == allSchemesAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }
            double xPos = x * i + offsetX;
            allBitsBob.add(new Bit(bit, xPos, 8 * offsetY, x, x));
        }

        toolTip = new Rectangle2D.Double(offsetX, bitBoxWidth / 2.0, getWidth() - offsetX - bitBoxWidth, bitBoxWidth);
    }

    private void addGaps(int i) {

        if (i == 3 || i == 5 || i == 7 || i == 9 || i == 11 || i == 13 || i == 17) {
            double gapSize = 12;
            gapForDemo += gapSize;
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

        g2d.setFont(new Font("Arial", Font.PLAIN, (int) (bitBoxWidth / 1.8)));

        drawAliceBobEve(g2d);

        if (showHighLighter) {
            drawHighlighter(g2d, true);
        }

        drawPhotonStuff(g2d);

//        if (showHighLighter) {
//            drawHighlighter(g2d, false);
//        }

        if (!demoMode) {
            drawHeaders(g2d);
        }
    }

    private void drawNumberOfBits(Graphics2D g2d) {

        g2d.setColor(MyColors.myGray);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Number of bits: " + numberOfBits, 10, 16);
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

        Color textColor = MyColors.myDarkGray;
        if (fill) {
            g2d.setColor(MyColors.myGreen);
            if (toolTipText.contains("Contradiction") ||
                    toolTipText.contains("'0' or a '1'") ||
                    allBitsBob.get(highlighterBitPosition).theBit == -1) {
                g2d.setColor(MyColors.myRed);
                textColor = MyColors.mySandLikeColor;
            }
            g2d.fill(highlighter);
            g2d.fill(toolTip);
            g2d.setFont(new Font("Arial", Font.PLAIN, (int) (bitBoxWidth / 2)));
            g2d.setColor(textColor);
            g2d.drawString(toolTipText, offsetX + 6, (int) (toolTip.y + bitBoxWidth - 12));
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
        for (Polarisation t : allPolarizationsAlice) {
            t.draw(g2d);
        }
        if (eavesDropping) {

            g2d.setColor(MyColors.mySandLikeColor);
            g2d.draw(new Rectangle2D.Double(10, 4 * offsetY - 24, getWidth() - 20, 2.80 * offsetY));

            for (Scheme s : allSchemesEve) {
                s.draw(g2d);
            }
            for (Bit b : allBitsEve) {
                b.draw(g2d);
            }
            for (Polarisation t : allPolarizationsEve) {
                t.draw(g2d);
            }
        }
        for (Scheme s : allSchemesBob) {
            s.draw(g2d);
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
        g2d.drawString("Alice's schemes: " + str, offsetX, (int) (2 * offsetY - upShiftHeader));
        str = getPercentPolarizations(allPolarizationsAlice);
        g2d.drawString("Polarizations: - " + str, offsetX, (int) (3 * offsetY - upShiftHeader));

        if (eavesDropping) {
            str = getPercentSchemes(allSchemesEve);
            g2d.drawString("Eve's schemes: " + str, offsetX, (int) (4 * offsetY - upShiftHeader));
            str = getPercentBit(allBitsEve);
            g2d.drawString("Eve's bit-string: " + str, offsetX, (int) (5 * offsetY - upShiftHeader));
            str = getPercentPolarizations(allPolarizationsEve);
            g2d.drawString("Polarizations: - " + str, offsetX, (int) (6 * offsetY - upShiftHeader));
        }

        str = getPercentSchemes(allSchemesBob);
        g2d.drawString("Bob's schemes: " + str, offsetX, (int) (7 * offsetY - upShiftHeader));
        str = getPercentBit(allBitsAlice);
        g2d.drawString("Alice's random bit-string: " + str, offsetX, (int) (offsetY - upShiftHeader));
        str = getPercentBit(allBitsBob);
        g2d.drawString("Bob's bit-string: " + str, offsetX, (int) (8 * offsetY - upShiftHeader));
    }

    private void drawBackgroundImage(Graphics2D g2d) {

        if (image == null) {
            return;
        }
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), Color.BLACK, this);
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
        String str = percent1 + " % '+' and " + (100 - percent1) + " % 'x'";

        if (countArbitrary > 0) {
            str += " - may be wrong " + (100 - percent1 - percent0) + " % '1|0'";
        }

        return str;
    }

    public String getPercentPolarizations(ArrayList<Polarisation> list) {

        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count_1 = 0;
        for (Polarisation b : list) {
            if (b.polarization == 0) {
                count0++;
            } else if (b.polarization == 1) {
                count1++;
            } else if (b.polarization == 2) {
                count2++;
            } else if (b.polarization == 3) {
                count3++;
            } else if (b.polarization == -1) {
                count_1++;
            }
        }
        int percent0 = (count0 * 100) / list.size();
        int percent1 = (count1 * 100) / list.size();
        int percent2 = (count2 * 100) / list.size();
        int percent3 = (count3 * 100) / list.size();
        int percent_1 = (count_1 * 100) / list.size();

        return percent0 + " % '|' and " + percent1 + " % '-' " + percent2 + " % '\\' " + percent3 + " % '/' " + percent_1 + " % '|-/\\' ";
    }

    public String getPercentBit(ArrayList<Bit> list) {

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
        String str = percent1 + " % '1' and " + percent0 + " % '0'";

        if (countArbitrary > 0) {
            str += " - may be wrong " + (100 - percent1 - percent0) + " % '1|0'";
        }

        return str;
    }

    private String createExplanations() {

        String str = "";

        str += "Alice sends '" + allBitsAlice.get(highlighterBitPosition).theBit + "' ";

        int fia = allSchemesAlice.get(highlighterBitPosition).filter;
        if (fia == 0) {
            str += "using the '+' scheme. ";
        } else {
            str += "using the 'x' scheme. ";
        }
        int po = allPolarizationsAlice.get(highlighterBitPosition).polarization;
        if (po == 0) {
            str += "The polarization is '|'. ";
        } else if (po == 1) {
            str += "The polarization is '-'. ";
        } else if (po == 2) {
            str += "The polarization is '\\'. ";
        } else if (po == 3) {
            str += "The polarization is '/'. ";
        }
        if (eavesDropping) {

        }
        int fib = allSchemesBob.get(highlighterBitPosition).filter;
        str += "Bob uses the ";
        if (fia == fib) {
            str += "right ";
        } else {
            str += "wrong ";
        }
        if (fib == 0) {
            str += "'+' scheme. ";
        } else {
            str += "'x' scheme. ";
        }

        return str;
    }

    /// handle events //////////////////////////////////////////////////////////////////////////////////////////////////

    private void handleComponentEvents() {
        offsetY = getHeight() / 9.0;
        creation();
        repaint();
    }

    private void handleKeyPress(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                showHighLighter = false;
                break;
            case KeyEvent.VK_SPACE:
                creation();
                break;
            case KeyEvent.VK_UP:
                numberOfBits += 2;
                if (numberOfBits > 256) {
                    numberOfBits = 256;
                } else {
                    creation();
                }
                break;
            case KeyEvent.VK_DOWN:
                numberOfBits -= 2;
                if (numberOfBits < 12) {
                    numberOfBits = 12;
                } else {
                    creation();
                }
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_LEFT:
                handleLeftRight(-1);
                break;
            case KeyEvent.VK_RIGHT:
                handleLeftRight(+1);
                break;

            /// number keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_4:
                numberOfBits = 42;
                creation();
                break;

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                demoMode = !demoMode;
                creation();
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
                    numberOfBits = 256;
                } else {
                    numberOfBits = 12;
                }
                creation();
                break;
            case KeyEvent.VK_P:
                break;
            case KeyEvent.VK_T:
                break;
            case KeyEvent.VK_W:
                System.exit(0);
                break;
        }
        repaint();
    }

    private void handleLeftRight(int dir) {

        highlighterBitPosition += dir;

        if (highlighterBitPosition < 0) {
            highlighterBitPosition = 0;
        }
        if (highlighterBitPosition >= allBitsAlice.size()) {
            highlighterBitPosition = allBitsAlice.size() - 1;
        }

        highlighter.x = allBitsAlice.get(highlighterBitPosition).x;
        if (demoMode) {
            toolTipText = explanations.getLine(highlighterBitPosition);
        } else {
            toolTipText = createExplanations();
        }
    }

    private void handleMouse(MouseEvent e) {

        int i = 0;
        for (MyBox b : allBitsAlice) {
            Rectangle2D.Double rect = new Rectangle2D.Double(b.x, 0, bitBoxWidth, getHeight());
            if (demoMode) {
                toolTipText = explanations.getLine(i);
            }
            if (rect.contains(e.getX(), e.getY())) {
                showHighLighter = true;
                highlighter.x = b.x;
                highlighterBitPosition = i;
                break;
            }
            i++;
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
