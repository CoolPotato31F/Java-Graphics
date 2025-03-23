package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The Rectangle class implements the GraphicsObject interface and represents a drawable rectangle.
 */
public class Rectangle implements GraphicsObject {

    private Point point1; // Top-left corner of the rectangle
    private Point point2; // Bottom-right corner of the rectangle
    private Point center; // Center of the rectangle
    private int width = 1; // Outline width
    private int w; // Width of the rectangle
    private int h; // Height of the rectangle
    private GraphWin canvas; // Reference to the canvas where the rectangle is drawn
    private Color fillColor; // Color used to fill the rectangle
    private Color outlineColor = Color.BLACK; // Default outline color is black

    /**
     * Constructs a Rectangle object with two diagonal points.
     * 
     * @param p1 One corner of the rectangle.
     * @param p2 The opposite corner of the rectangle.
     */
    public Rectangle(Point p1, Point p2) {
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
     * Draws the rectangle on the given canvas.
     * 
     * @param canvas The canvas on which the rectangle will be drawn.
     * @throws Error if the rectangle is already drawn on a canvas.
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
     * Removes the rectangle from the canvas.
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
     * Sets the fill color of the rectangle.
     * 
     * @param color The color to fill the rectangle with.
     */
    public void setFill(Color color) {
        this.fillColor = color;
    }

    /**
     * Sets the outline color of the rectangle.
     * 
     * @param color The outline color of the rectangle.
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Gets the first corner of the rectangle.
     * 
     * @return The first corner (top-left) of the rectangle.
     */
    public Point getP1() {
        return point1;
    }

    /**
     * Gets the second corner of the rectangle.
     * 
     * @return The second corner (bottom-right) of the rectangle.
     */
    public Point getP2() {
        return point2;
    }

    /**
     * Gets the center point of the rectangle.
     * 
     * @return The center point of the rectangle.
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Gets the size of the rectangle as a Point object where x is width and y is height.
     * 
     * @return A Point representing the width and height of the rectangle.
     */
    public Point getSize() {
        return new Point(w, h);
    }

    /**
     * Sets the width of the rectangle's outline.
     * 
     * @param width The width of the outline.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the width of the rectangle's outline.
     * 
     * @return The width of the outline.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Moves the rectangle by a specified amount in the x and y directions.
     * 
     * @param dx The amount to move the rectangle along the x-axis.
     * @param dy The amount to move the rectangle along the y-axis.
     */
    public void move(double dx, double dy) {
        point1.move(dx, dy);
        point2.move(dx, dy);
        if (this.canvas != null && this.canvas.autoflush) {
            this.canvas.repaint();
        }
    }

    @Override
    public void drawPanel(Graphics2D graphics) {
        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillRect((int) point1.getX(), (int) point1.getY(), w, h);
        }
        graphics.setStroke(new BasicStroke(width));
        graphics.setColor(outlineColor);
        graphics.drawRect((int) point1.getX(), (int) point1.getY(), w, h);
    }
    
    @Override
    public String toString() {
        return "Rectangle(" +
               "point1=" + point1 + ", " +
               "point2=" + point2 + ", " +
               "center=" + center + ", " +
               "width=" + width + ", " +
               "fillColor=" + (fillColor != null ? fillColor : "None") + ", " +
               "outlineColor=" + outlineColor + ")";
    }
}
