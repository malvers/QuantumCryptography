import java.awt.*;
import java.awt.geom.AffineTransform;

public class Bit extends MyBox {

    protected int theBit;

    public Bit(int b, double x, double y, double w, double h) {
        super(x, y, w, h);
        theBit = b;
    }

    protected void draw(Graphics2D g2d) {

        g2d.setColor(Color.GRAY);
        g2d.draw(this);

        if (theBit < 0) {
            g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
            g2d.fill(this);
            g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
            AffineTransform oldTransform = g2d.getTransform();

            AffineTransform rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(0), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            g2d.drawString("" + 1, (int) x + 8, (int) (y + height - 6));

            rotation.rotate(Math.toRadians(+90), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            g2d.drawString("" + 0, (int) x + 8, (int) (y + height - 6));
            g2d.setTransform(oldTransform);

        } else {
            g2d.setColor(Color.WHITE);
            g2d.fill(this);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString("" + theBit, (int) x + 8, (int) (y + height - 6));
        }
    }
}
