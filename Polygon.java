package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Represents a polygon that can be drawn and manipulated in a graphical window.
 */
public class Polygon implements GraphicsObject {
    protected Point[] points;
    protected int width = 10; // Default stroke width
    protected GraphWin canvas;
    protected Color fillColor;
    protected Color outlineColor = Color.BLACK;

    /**
     * Constructs a Polygon from an array of points.
     * 
     * @param p Array of Points defining the polygon vertices.
     * @throws IllegalArgumentException if the array is null or contains fewer than 3 points.
     */
    public Polygon(Point[] p) {
        if (p == null || p.length < 3) {
            throw new IllegalArgumentException("A polygon must have at least three points.");
        }
        this.points = p.clone(); // Defensive copy to prevent external modifications
    }

    /**
     * Gets the x-coordinates of the polygon's points.
     * 
     * @return An array of x-coordinates.
     */
    public int[] getXCords() {
        int[] xCoords = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xCoords[i] = (int) points[i].getX();
        }
        return xCoords;
    }

    /**
     * Gets the y-coordinates of the polygon's points.
     * 
     * @return An array of y-coordinates.
     */
    public int[] getYCords() {
        int[] yCoords = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            yCoords[i] = (int) points[i].getY();
        }
        return yCoords;
    }
    
    /**
     * Gets every point in the polygon
     * 
     * @return An array of every Points
     */
    public Point[] getPoints() {
    	return points;
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
     * Sets the width of the rectangle's outline.
     * 
     * @param width The width of the outline.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Polygon(");
        for (Point p : points) {
            str.append(p.toString()).append(", ");
        }
        return str.append(")").toString();
    }

    /**
     * Draws the polygon on the provided `GraphWin` canvas.
     * 
     * @param canvas The `GraphWin` instance.
     * @throws IllegalStateException if the polygon is already drawn.
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
     * Removes the polygon from the graphical window.
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
     * Renders the polygon onto a `Graphics2D` panel.
     * 
     * @param graphics The `Graphics2D` object used for rendering.
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        int[] xCoords = getXCords();
        int[] yCoords = getYCords();

        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillPolygon(xCoords, yCoords, points.length);
        }

        graphics.setStroke(new BasicStroke(width));
        graphics.setColor(outlineColor);
        graphics.drawPolygon(xCoords, yCoords, points.length);
    }
}
