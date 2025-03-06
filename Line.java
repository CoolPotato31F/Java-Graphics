package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Represents a line segment between two points, with customizable
 * width, outline color, and line style (solid, dotted, dashed).
 */
public class Line implements GraphicsObject {

    private Point point1;
    private Point point2;
    private int width = 1; // Default width
    private Color outlineColor = Color.BLACK; // Default color
    private String lineType = "solid"; // Default line type (solid)
    private GraphWin canvas;

    /**
     * Constructs a Line between two specified points.
     * 
     * @param p1 the starting point of the line
     * @param p2 the ending point of the line
     */
    public Line(Point p1, Point p2) {
        this.point1 = p1;
        this.point2 = p2;
    }

    /**
     * Sets the outline color of the line.
     * 
     * @param color the new outline color
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Sets the first point of the line.
     * 
     * @param point the new first point
     */
    public void setP1(Point point) {
        this.point1 = point;
    }

    /**
     * Sets the second point of the line.
     * 
     * @param point the new second point
     */
    public void setP2(Point point) {
        this.point2 = point;
    }

    /**
     * Gets the width (thickness) of the line.
     * 
     * @return the width of the line
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the width (thickness) of the line.
     * 
     * @param width the new width of the line
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the type of the line (solid, dotted, or dashed).
     * 
     * @param type the type of the line ("solid", "dotted", or "dashed")
     */
    public void setType(String type) {
        if (type.equals("solid") || type.equals("dotted") || type.equals("dashed")) {
            this.lineType = type;
        } else {
            throw new IllegalArgumentException("Invalid line type: " + type);
        }
    }

    /**
     * Calculates and returns the length of the line.
     * 
     * @return the length of the line
     */
    public double getLength() {
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) +
                         Math.pow(point1.getY() - point2.getY(), 2));
    }

    /**
     * Draws the line on the provided graphical window.
     * 
     * @param canvas the {@code GraphWin} where the line will be drawn
     * @throws IllegalStateException if the object is already drawn
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
     * Removes the line from the graphical window.
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
     * Draws the line on a {@code Graphics2D} panel.
     * 
     * @param graphics the {@code Graphics2D} object used for rendering
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        BasicStroke stroke = null;
        
        // Adjust the dash pattern based on lineType
        if (lineType.equals("solid")) {
            stroke = new BasicStroke(width);
        } else if (lineType.equals("dotted")) {
            // Ensure the gap between dots is adjusted based on width
            float dashLength = 1f; // Length of dashes
            float gapLength = width*2f;  // Gap between dashes/dots
            stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {dashLength, gapLength}, 0.0f);
        } else if (lineType.equals("dashed")) {
            float dashLength = width*3f; // Length of dashes
            float gapLength = width*1.5f;  // Gap between dashes/dots
            // Ensure the gap between dashes is adjusted based on width
            stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {dashLength, gapLength}, 0.0f);
        }

        graphics.setStroke(stroke);
        graphics.setColor(outlineColor);
        graphics.drawLine((int) point1.getX(), (int) point1.getY(), 
                          (int) point2.getX(), (int) point2.getY());
    }
}
