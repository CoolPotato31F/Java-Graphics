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
    }

    /**
     * Removes the rectangle from the canvas.
     */
    @Override
    public void undraw() {
        if (canvas != null) {
            canvas.deleteItem(this);
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
    
    /**
     * Moves the rectangle smoothly over a given duration.
     *
     * @param dx   The total change in x-coordinate.
     * @param dy   The total change in y-coordinate.
     * @param time The duration (in seconds) for the movement.
     */
    public void move(double dx, double dy, double time) {
        new Thread(() -> {
            long startTime = System.nanoTime();
            long endTime = startTime + (long) (time * 1_000_000_000); // Convert seconds to nanoseconds
            double startX = this.point1.getX();
            double startY = this.point1.getY();
            double startX2 = this.point2.getX();
            double startY2 = this.point2.getY();

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0; // Convert to seconds
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0; // Clamp to ensure no overshooting

                // Interpolate position
                this.point1.moveTo(startX + dx * progress, startY + dy * progress);
                this.point2.moveTo(startX2 + dx * progress, startY2 + dy * progress);

                if (canvas != null) {
                    canvas.update();
                }

                try {
                    Thread.sleep(10); // Sleep briefly to allow smooth rendering
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Ensure final position is set exactly
            this.point1.moveTo(startX + dx, startY + dy);
            this.point2.moveTo(startX2 + dx, startY2 + dy);

            if (canvas != null) {
                canvas.update();
            }
        }).start();
    }

    /**
     * Smoothly moves the point from its current position by (dx, dy) over a specified time
     * using the given easing style and direction.
     *
     * @param dx              The total change in x-coordinate.
     * @param dy              The total change in y-coordinate.
     * @param time            The duration (in seconds) over which the movement should complete.
     * @param easingStyle     The easing function that dictates the acceleration curve.
     * @param easingDirection The direction of the easing (In, Out, or InOut).
     */
    public void move(double dx, double dy, double time, EasingStyle easingStyle, EasingDirection easingDirection) {
        new Thread(() -> {
            long startTime = System.nanoTime();
            long endTime = startTime + (long) (time * 1_000_000_000);
            double startX = this.point1.getX();
            double startY = this.point1.getY();
            double startX2 = this.point2.getX();
            double startY2 = this.point2.getY();

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;
                double easedProgress = applyEasing(progress, easingStyle, easingDirection);

                this.point1.moveTo(startX + dx * easedProgress, startY + dy * easedProgress);
                this.point2.moveTo(startX2 + dx * easedProgress, startY2 + dy * easedProgress);

                if (canvas != null) {
                    canvas.update();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            this.point1.moveTo(startX + dx, startY + dy);
            this.point2.moveTo(startX2 + dx, startY2 + dy);

            if (canvas != null) {
                canvas.update();
            }
        }).start();
    }
    
    private double applyEasing(double t, EasingStyle style, EasingDirection easingDirection) {
        switch (easingDirection) {
            case OUT:
                // Reverse the easing by applying (1 - easing(1 - t))
                return 1 - applyEasing(1 - t, style, EasingDirection.IN);
            case INOUT:
                // First half uses In, second half uses Out
                return t < 0.5 
                    ? applyEasing(t * 2, style, EasingDirection.IN) / 2 
                    : 1 - applyEasing((1 - t) * 2, style, EasingDirection.IN) / 2;
            case IN:
            default:
                // Normal easing behavior
                switch (style) {
                    case LINEAR:
                        return t;
                    case SINE:
                        return 1 - Math.cos(t * Math.PI / 2);
                    case QUAD:
                        return t * t;
                    case CUBIC:
                        return t * t * t;
                    case QUART:
                        return t * t * t * t;
                    case QUINT:
                        return t * t * t * t * t;
                    case EXPONENTIAL:
                        return t == 0 ? 0 : Math.pow(2, 10 * (t - 1));
                    case CIRCULAR:
                        return 1 - Math.sqrt(1 - t * t);
                    case BACK:
                        double s = 1.70158;  // Default overshoot amount for "back" easing
                        return t * t * ((s + 1) * t - s);
                    case ELASTIC:
                        if (t == 0 || t == 1) return t;
                        double p = 0.3; // Period of oscillation
                        return -Math.pow(2, 10 * (t - 1)) * Math.sin((t - 1.1) * (2 * Math.PI) / p);
                    case BOUNCE:
                        if (t > (1 - 1 / 2.75)) {
                            t = 1 - t;
                            return 1 - (7.5625 * t * t);
                        } else if (t > (1 - 2 / 2.75)) {
                            t = 1 - t - (1.5 / 2.75);
                            return 1 - (7.5625 * t * t + 0.75);
                        } else if (t > (1 - 2.5 / 2.75)) {
                            t = 1 - t - (2.25 / 2.75);
                            return 1 - (7.5625 * t * t + 0.9375);
                        } else {
                            t = 1 - t - (2.625 / 2.75);
                            return 1 - (7.5625 * t * t + 0.984375);
                        }
                    default:
                        return t; // Default to linear if the easing type is unknown
                }
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
