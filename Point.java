package graphics;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Represents a point in 2D space that can be drawn, moved, and checked for collisions.
 */
public class Point implements GraphicsObject {
    private double x;
    private double y;
    private GraphWin canvas;
    private Color outlineColor = Color.BLACK;
    private int width = 2; // Default point size

    /**
     * Constructs a point at the specified coordinates.
     * 
     * @param x1 The x-coordinate of the point.
     * @param y1 The y-coordinate of the point.
     */
    public Point(double x1, double y1) {
        this.x = x1;
        this.y = y1;
    }

    /**
     * @return String representation of the Point
     */
    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    /**
     * @return X position of the Point
     */
    public double getX() {
        return x;
    }
    /**
     * @return Y position of the Point
     */
    public double getY() {
        return y;
    }

    /**
     * Moves the point by the specified delta values.
     * 
     * @param dx The change in x-coordinate.
     * @param dy The change in y-coordinate.
     */
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
    
    /**
     * Moves to the specified point.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public void moveTo(double x, double y) {
    	this.x = x;
    	this.y = y;
    }
    
    /**
     * Sets the width (size) of the point when drawn.
     * 
     * @param width The new width of the point.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Checks if a point is inside a given circle.
     * 
     * @param point  The point to check.
     * @param circle The circle to check against.
     * @return true if the point is inside the circle, false otherwise.
     */
    public static boolean checkCollisionPointXCircle(Point point, Circle circle) {
        return Math.pow(point.getX() - circle.getCenter().getX(), 2) +
               Math.pow(point.getY() - circle.getCenter().getY(), 2)
               <= Math.pow(circle.getRadius(), 2);
    }

    /**
     * Creates a new point with the same coordinates.
     * 
     * @return A clone of the current point.
     */
    public Point clone() {
        return new Point(x, y);
    }

    /**
     * Moves the point to the specified position.
     * 
     * @param p The new position.
     */
    public void moveTo(Point p) {
        this.move(p.getX() - this.getX(), p.getY() - this.getY());
    }

    /**
     * Draws the point on the specified `GraphWin`.
     * 
     * @param canvas The `GraphWin` instance.
     * @throws IllegalStateException if the point is already drawn.
     */
    @Override
    public void draw(GraphWin canvas) {
        if (this.canvas != null) {
            throw new IllegalStateException("Object is already drawn");
        }
        this.canvas = canvas;
        canvas.addItem(this);
        if (canvas.autoflush) {
            canvas.update();
        }
    }

    /**
     * Removes the point from the graphical window.
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
     * Sets the outline color of the point.
     * 
     * @param color The new outline color.
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Renders the point onto a `Graphics2D` panel.
     * 
     * @param graphics The `Graphics2D` object used for rendering.
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        graphics.setColor(outlineColor);
        graphics.fillRect((int) (x - width / 2), (int) (y - width / 2), width, width);
    }
}
