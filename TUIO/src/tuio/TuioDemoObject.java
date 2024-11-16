package TUIO;

import javax.swing.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import TUIO.*;
import V2048Game.V2048;

public class TuioDemoObject extends TuioObject {

    private Shape circle;  // Changed to circle

    public TuioDemoObject(TuioObject tobj) {
        super(tobj);
        int size = TuioDemoComponent.object_size;
        circle = new Ellipse2D.Float(-size / 2, -size / 2, size, size);  // Circle instead of square

        AffineTransform transform = new AffineTransform();
        transform.translate(xpos, ypos);
        transform.rotate(angle, xpos, ypos);
        circle = transform.createTransformedShape(circle);
    }

    public void paint(Graphics2D g, int width, int height) {
        float Xpos = xpos * width;
        float Ypos = ypos * height;
        float scale = height / (float) TuioDemoComponent.table_size;

        AffineTransform trans = new AffineTransform();
        trans.translate(-xpos, -ypos);
        trans.translate(Xpos, Ypos);
        trans.scale(scale, scale);
        Shape s = trans.createTransformedShape(circle);

        g.setPaint(Color.black);
        g.fill(s);
        g.setPaint(Color.white);
        if (symbol_id == 0) {
            g.drawString("Pause Game", Xpos - 10, Ypos);  // Updated label
            if (V2048.instance != null && !V2048.instance.getIsPaused()) {
                V2048.instance.onPauseClick();
            }
        } 
        else if (symbol_id == 1) {
            g.drawString("Resume", Xpos - 10, Ypos);  // Updated label
            if (V2048.instance != null && V2048.instance.getIsPaused()) {
                V2048.instance.onPauseClick();
            } 
        }
    }

    public void update(TuioObject tobj) {

        float dx = tobj.getX() - xpos;
        float dy = tobj.getY() - ypos;
        float da = tobj.getAngle() - angle;

        if (dx != 0 || dy != 0 || da != 0) {
            this.xpos = tobj.getX();
            this.ypos = tobj.getY();
            this.angle = tobj.getAngle();
            int size = TuioDemoComponent.object_size;
            circle = new Ellipse2D.Float(-size / 2, -size / 2, size, size);
            AffineTransform transform = new AffineTransform();
            transform.translate(xpos, ypos);
            transform.rotate(angle, xpos, ypos);
            circle = transform.createTransformedShape(circle);
        }
    }
}
