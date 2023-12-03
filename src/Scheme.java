import java.awt.*;
import java.awt.geom.AffineTransform;

public class Scheme extends MyBox {

    protected int filter = -1;
    private Color bg = Color.black;

    public Scheme(int filter, double x, double y, double w, double h) {
        super(x, y, w, h);
        this.filter = filter;
    }

    protected void draw(Graphics2D g2d) {

        g2d.setColor(bg);
        g2d.fill(this);
        g2d.setColor(Color.GRAY);
        g2d.draw(this);

        g2d.setColor(MyColors.myRed);
        double facY;
        double facX;
        double ox;
        double oy;

        if (filter == 0) {
            g2d.setColor(MyColors.myRed);
            facY = 1.4;
            facX = 6.0;
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));
            /// g2d.setColor(MyColors.myGreen);
            facY = 6.0;
            facX = 1.4;
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

        } else {

            facY = 6.0;
            facX = 1.4;

            g2d.setColor(MyColors.myGreen);
            AffineTransform oldTransform = g2d.getTransform();

            AffineTransform rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(45), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

            g2d.setTransform(oldTransform);

            /// g2d.setColor(MyColors.myBlue);
            oldTransform = g2d.getTransform();

            rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(-45), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

            g2d.setTransform(oldTransform);
        }
    }

    public void setBackground(Color color) {
        bg = color;
    }
}
