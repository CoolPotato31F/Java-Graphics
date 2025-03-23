package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


/**
 * The Circle class implements the GraphicsObject interface and represents a drawable circle.
 */
public class Circle implements GraphicsObject {

    private Point point1; // Center point of the circle
    private int radius; // Radius of the circle
    private Color outlineColor = Color.BLACK; // Default outline color is black
    private int width = 1; // Default outline width
    private GraphWin canvas; // Reference to the canvas where the circle is drawn
    private Color fillColor; // Color used to fill the circle

    /**
     * Constructs a Circle object with a given center point and radius.
     * 
     * @param p1 The center point of the circle.
     * @param r The radius of the circle.
     */
    public Circle(Point p1, int r) {
        point1 = p1;
        radius = r;
    }

    /**
     * Draws the circle on the given canvas.
     * 
     * @param canvas The canvas on which the circle will be drawn.
     * @throws Error if the circle is already drawn on a canvas.
     */
    @Override
    public void draw(GraphWin canvas) {
        if (this.canvas != null) {
            throw new Error("Object is already drawn");
        }
        this.canvas = canvas;
        canvas.addItem(this);
        if (canvas.autoflush) {
            canvas.repaint();
        }
    }

    /**
     * Removes the circle from the canvas.
     */
    @Override
    public void undraw() {
        if (canvas != null) {
            canvas.deleteItem(this);
            if (canvas.autoflush) {
                canvas.repaint();
            }
            this.canvas = null;
        }
    }

    /**
     * Sets the fill color of the circle.
     * 
     * @param color The color to fill the circle with.
     */
    public void setFill(Color color) {
        this.fillColor = color;
    }

    /**
     * Sets the outline color of the circle.
     * 
     * @param color The outline color of the circle.
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Sets the outline width of the circle.
     * 
     * @param width The width of the circle's outline.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Draws the circle on a graphics panel.
     * 
     * @param graphics The Graphics2D object used for rendering.
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        graphics.setStroke(new BasicStroke(0));

        // Draws the filled portion of the circle if a fill color is set
        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillOval((int) point1.getX() - radius, (int) point1.getY() - radius, radius * 2, radius * 2);
        }

        // Draws the outline of the circle
        graphics.setStroke(new BasicStroke(width));
        graphics.setColor(outlineColor);
        graphics.drawOval((int) point1.getX() - radius, (int) point1.getY() - radius, radius * 2, radius * 2);
    }

    /**
     * Moves the circle by a specified amount in the x and y directions.
     * 
     * @param dx The amount to move the circle along the x-axis.
     * @param dy The amount to move the circle along the y-axis.
     */
    public void move(double dx, double dy) {
        point1.move(dx, dy);
    }

    /**
     * Gets the center point of the circle.
     * 
     * @return The center point of the circle.
     */
    public Point getCenter() {
        return point1;
    }

    /**
     * Gets the radius of the circle.
     * 
     * @return The radius of the circle.
     */
    public int getRadius() {
        return radius;
    }
    
    @Override
    public String toString() {
        return "Circle{" +
               "center=" + point1 +
               ", radius=" + radius +
               ", outlineColor=" + outlineColor +
               ", outlineWidth=" + width +
               ", fillColor=" + (fillColor != null ? fillColor : "None") +
               '}';
    }
}
