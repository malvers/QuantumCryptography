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
    private static final int width = 1500;
    private Rectangle2D.Double highlighter;
    private BufferedImage image;
    private ArrayList<Bit> allBitsAlice;
    private ArrayList<Schema> allSchemasAlice;
    private ArrayList<Transmission> allTransmissions;
    private ArrayList<Schema> allSchemasBob;
    private ArrayList<Bit> allBitsBob;
    private final int numBits = 60;
    private final int offsetY = 80;
    private BufferedImage alice;
    private BufferedImage bob;

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
        double h = x;

        highlighter = new Rectangle2D.Double(-100, offsetY, x, offsetY * 6 + MyBox.width);

        double offsetX = 160;//(width - (numBits * x)) / 2.0;

        allBitsAlice = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            int bit = random.nextInt(2);
            double xPos = x * i + offsetX;
            allBitsAlice.add(new Bit(bit, xPos, y, x, h));
        }
        allSchemasAlice = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemasAlice.add(new Schema(random.nextInt(2), xPos, y + offsetY, x, h));
        }
        allTransmissions = new ArrayList<>();

        for (int i = 0; i < numBits; i++) {

            double xPos = x * i + offsetX;
            int theCase = -1;
            if (allBitsAlice.get(i).theBit == 1 && allSchemasAlice.get(i).filter == 0) {
                theCase = 0;
            } else if (allBitsAlice.get(i).theBit == 0 && allSchemasAlice.get(i).filter == 0) {
                theCase = 1;
            } else if (allBitsAlice.get(i).theBit == 0 && allSchemasAlice.get(i).filter == 1) {
                theCase = 2;
            } else if (allBitsAlice.get(i).theBit == 1 && allSchemasAlice.get(i).filter == 1) {
                theCase = 3;
            }
            allTransmissions.add(new Transmission(theCase, xPos, y + 3 * offsetY, x, h));
        }

        allSchemasBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {
            double xPos = x * i + offsetX;
            allSchemasBob.add(new Schema(random.nextInt(2), xPos, 6 * offsetY, x, h));
        }

        allBitsBob = new ArrayList<>();
        for (int i = 0; i < numBits; i++) {

            int bit = -1;//random.nextInt(2);

            if (allSchemasBob.get(i).filter == allSchemasAlice.get(i).filter) {
                bit = allBitsAlice.get(i).theBit;
            }

            double xPos = x * i + offsetX;
            allBitsBob.add(new Bit(bit, xPos, 7 * offsetY, x, h));
        }
    }

    private void loadImages() {
        try {
            image = ImageIO.read(new File("universe.png"));
            alice = ImageIO.read(new File("alice.png"));
            bob = ImageIO.read(new File("bob.png"));
        } catch (IOException e) {
            System.out.println("clouds not found :-/");
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int) (MyBox.width / 1.8)));

        drawImage(g2d);

        g2d.drawImage(alice, 30, offsetY, alice.getWidth() / 16, alice.getHeight() / 16, null, this);
        g2d.drawImage(bob, 30, 6 * offsetY, alice.getWidth() / 16, alice.getHeight() / 16, null, this);

        drawHighlighter(g2d);

        drawQuantumStuff(g2d);
    }

    private void drawHighlighter(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);

        double factor = MyBox.width;
        highlighter.x = Math.round(highlighter.x / factor) * factor + MyBox.width / 2.0 - 5;
        g2d.fill(highlighter);
    }

    private void drawQuantumStuff(Graphics2D g2d) {

        for (Bit b : allBitsAlice) {
            b.draw(g2d);
        }
        for (Schema s : allSchemasAlice) {
            s.draw(g2d);
        }
        for (Transmission t : allTransmissions) {
            t.draw(g2d);
        }

        for (int i = 0; i < allSchemasBob.size(); i++) {
            Schema s = allSchemasBob.get(i);
            if (allSchemasAlice.get(i).filter == s.filter) {
                s.setBackground(Color.BLACK);
            } else {
                s.setBackground(Color.GRAY);
            }
            allSchemasBob.get(i).draw(g2d);
        }

        for (Bit b : allBitsBob) {
            b.draw(g2d);
        }
    }

    private void drawImage(Graphics2D g2d) {

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
            f.setLocation(200, 0);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
