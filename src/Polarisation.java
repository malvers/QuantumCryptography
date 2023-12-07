import java.awt.*;
import java.awt.geom.AffineTransform;

public class Polarisation extends MyBox {

    protected final int polarization;

    public Polarisation(int theCase, double x, double y, double w, double h) {
        super(x, y, w, h);
        polarization = theCase;
    }

    protected void draw(Graphics2D g2d) {

        Color color = g2d.getColor();

        if (polarization != -2) {
            g2d.setColor(MyColors.myGray);
            g2d.fill(this);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.draw(this);
        }

        double facY;
        double facX;
        double ox;
        double oy;

        if (polarization == 0) {

            g2d.setColor(MyColors.myRed);
            facY = 1.4;
            facX = 6.0;
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

        } else if (polarization == 1) {

            g2d.setColor(MyColors.myGreen);
            facY = 6.0;
            facX = 1.4;
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

        } else if (polarization == 2) {

            facY = 6.0;
            facX = 1.4;

            g2d.setColor(MyColors.myOrange);
            AffineTransform oldTransform = g2d.getTransform();

            AffineTransform rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(45), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

            g2d.setTransform(oldTransform);

        } else if (polarization == 3) {

            facY = 6.0;
            facX = 1.4;

            g2d.setColor(MyColors.myCyan);
            AffineTransform oldTransform = g2d.getTransform();

            AffineTransform rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(-45), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

            g2d.setTransform(oldTransform);

        } else if (polarization == -1) {
            /// is empty
        }

        g2d.setColor(color);
    }
}
