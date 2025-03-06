package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Represents an oval shape that can be drawn on a graphical window.
 */
public class Oval implements GraphicsObject {
    private Point point1;
    private Point point2;
    private Point center;
    private int width = 1;
    private int w;
    private int h;
    private GraphWin canvas;
    private Color fillColor;
    private Color outlineColor = Color.BLACK;

    /**
     * Constructs an Oval using two points as defining boundaries.
     *
     * @param p1 The first point defining the oval.
     * @param p2 The second point defining the oval.
     */
    public Oval(Point p1, Point p2) {
        Point np1 = new Point(0, 0);
        Point np2 = new Point(0, 0);
        
        if (p1.getX() > p2.getX()) {
            np1.move(p2.getX(), 0);
            np2.move(p1.getX(), 0);
        } else {
            np1.move(p1.getX(), 0);
            np2.move(p2.getX(), 0);
        }

        if (p1.getY() > p2.getY()) {
            np1.move(0, p2.getY());
            np2.move(0, p1.getY());
        } else {
            np1.move(0, p1.getY());
            np2.move(0, p2.getY());
        }

        this.point1 = np1;
        this.point2 = np2;
        this.center = new Point(np2.getX() - np1.getX(), np2.getY() - np1.getY());
        w = (int) (np2.getX() - np1.getX());
        h = (int) (np2.getY() - np1.getY());
    }

    /**
     * Draws the oval on the given canvas.
     *
     * @param canvas The graphical window where the oval should be drawn.
     */
    @Override
    public void draw(GraphWin canvas) {
        if (this.canvas != null) {
            throw new Error("Object is already drawn");
        }
        this.canvas = canvas;
        canvas.addItem(this);
        if (canvas.autoflush) {
            canvas.update();
        }
    }

    /**
     * Removes the oval from the canvas.
     */
    @Override
    public void undraw() {
        if (canvas != null) {
            canvas.deleteItem(this);
            if (canvas.autoflush) {
                canvas.update();
            }
            this.canvas = null;
        }
    }

    /**
     * Sets the fill color of the oval.
     *
     * @param color The color to fill the oval.
     */
    public void setFill(Color color) {
        this.fillColor = color;
    }

    /**
     * Sets the outline color of the oval.
     *
     * @param color The outline color of the oval.
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Returns a string representation of the oval.
     *
     * @return A string describing the oval.
     */
    @Override
    public String toString() {
        return "Oval(Point(" + point1.getX() + ", " + point1.getY() + "), Point(" + point2.getX() + ", " + point2.getY() + "))";
    }

    /**
     * Gets the first defining point of the oval.
     *
     * @return The first point.
     */
    public Point getP1() {
        return point1;
    }

    /**
     * Gets the second defining point of the oval.
     *
     * @return The second point.
     */
    public Point getP2() {
        return point2;
    }

    /**
     * Gets the center point of the oval.
     *
     * @return The center point.
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Returns the size of the oval as a Point representing width and height.
     *
     * @return A Point containing the width and height of the oval.
     */
    public Point getSize() {
        return new Point(w, h);
    }

    /**
     * Sets the outline width of the oval.
     *
     * @param width The width of the outline.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the outline width of the oval.
     *
     * @return The outline width.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Moves the oval by the specified x and y distances.
     *
     * @param dx The distance to move along the x-axis.
     * @param dy The distance to move along the y-axis.
     */
    public void move(double dx, double dy) {
        point1.move(dx, dy);
        point2.move(dx, dy);
        if (this.canvas != null && this.canvas.autoflush) {
            this.canvas.repaint();
        }
    }

    /**
     * Draws the oval on a Graphics2D panel.
     *
     * @param graphics The Graphics2D object used to render the oval.
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillOval((int) point1.getX(), (int) point1.getY(), w, h);
        }
        graphics.setStroke(new BasicStroke(width));
        graphics.setColor(outlineColor);
        graphics.drawOval((int) point1.getX(), (int) point1.getY(), w, h);
    }
}
