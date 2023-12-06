import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class Bit extends MyBox {

    protected int theBit;

    public Bit(int b, double x, double y, double w, double h) {
        super(x, y, w, h);
        theBit = b;
    }

    protected void draw(Graphics2D g2d) {

        g2d.setColor(MyColors.myLightGray);
        g2d.draw(this);

        if (theBit < 0) {

            g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));

            g2d.setColor(MyColors.myGray);
            g2d.fill(this);

            g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));

            g2d.setColor(MyColors.myMediumDarkGray);

            g2d.drawString("" + 1, (int) x + 4, (int) (y + 1 + height / 2));
            g2d.drawString("" + 0, (int) (x + width / 2 + 2), (int) (y + height - 2));

            g2d.draw(new Line2D.Double(x, y + height, x + width, y));

        } else if (theBit == 0) {

            g2d.setColor(Color.BLACK);
            g2d.fill(this);
            g2d.setColor(Color.WHITE);
            String str;
            str = "" + theBit;
            int ws = g2d.getFontMetrics().stringWidth(str);
            int hs = g2d.getFontMetrics().getHeight();
            int posX = (int) (x + ((width - ws) / 2.0));
            int posY = (int) (y + ((height - hs) / 2.0) + height / 2.0 + 1);
            g2d.drawString(str, posX, posY);

        } else if (theBit == 1) {

            g2d.setColor(Color.WHITE);
            g2d.fill(this);
            g2d.setColor(Color.DARK_GRAY);
            String str;
            str = "" + theBit;
            int ws = g2d.getFontMetrics().stringWidth(str);
            int hs = g2d.getFontMetrics().getHeight();
            int posX = (int) (x + ((width - ws) / 2.0));
            int posY = (int) (y + ((height - hs) / 2.0) + height / 2.0 + 1);
            g2d.drawString(str, posX, posY);
        }
    }
}
