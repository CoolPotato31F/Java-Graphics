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

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0; // Convert to seconds
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0; // Clamp to ensure no overshooting

                // Interpolate position
                this.point1.moveTo(startX + dx * progress, startY + dy * progress);

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

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;
                double easedProgress = applyEasing(progress, easingStyle, easingDirection);

                this.point1.moveTo(startX + dx * easedProgress, startY + dy * easedProgress);

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

            if (canvas != null) {
                canvas.update();
            }
        }).start();
    }
    private double applyEasing(double t, EasingStyle style, EasingDirection easingDirection) {
        switch (easingDirection) {
            case Out:
                // Reverse the easing by applying (1 - easing(1 - t))
                return 1 - applyEasing(1 - t, style, EasingDirection.In);
            case InOut:
                // First half uses In, second half uses Out
                return t < 0.5 
                    ? applyEasing(t * 2, style, EasingDirection.In) / 2 
                    : 1 - applyEasing((1 - t) * 2, style, EasingDirection.In) / 2;
            case In:
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
