import java.awt.*;
import java.awt.geom.AffineTransform;

public class Scheme extends MyBox {

    protected int filter;
    private final Color backGroundColor = MyColors.myDarkGray;
    protected boolean valid = true;

    public Scheme(int filter, double x, double y, double w, double h) {
        super(x, y, w, h);
        this.filter = filter;
    }

    protected void draw(Graphics2D g2d) {

        g2d.setColor(backGroundColor);
        g2d.fill(this);
        g2d.setColor(Color.GRAY);
        g2d.draw(this);

        g2d.setColor(MyColors.myRed);
        double facY;
        double facX;
        double ox;
        double oy;

        facY = 1.4;
        facX = 6.0;
        if (filter == 0) {

            if (valid) {
                g2d.setColor(MyColors.myRed);
            } else {
                g2d.setColor(MyColors.myGray.darker());
            }
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));
            facY = 6.0;
            facX = 1.4;
            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

        } else {

            if (valid) {
                g2d.setColor(MyColors.myGreen);
            } else {
                g2d.setColor(MyColors.myGray.darker());
            }
            AffineTransform oldTransform = g2d.getTransform();

            AffineTransform rotation = new AffineTransform();
            rotation.rotate(Math.toRadians(45), getCenterX(), getCenterY());
            g2d.setTransform(rotation);

            ox = (width - (width / facX)) / 2;
            oy = (height - (height / facY)) / 2;
            g2d.fill(new Double(x + ox, y + oy, width / facX, height / facY));

            g2d.setTransform(oldTransform);

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

    public void setValidity(boolean v) {
       valid = v;
    }
}
