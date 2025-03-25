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
     * Moves the point smoothly by `dx` and `dy` over the given time duration without blocking the main program.
     * 
     * @param dx    The total change in x-coordinate.
     * @param dy    The total change in y-coordinate.
     * @param time  The duration (in seconds) over which the movement should complete.
     */
    public void move(double dx, double dy, double time) {
        new Thread(() -> {
            long startTime = System.nanoTime();
            long endTime = startTime + (long) (time * 1_000_000_000); // Convert seconds to nanoseconds
            double startX = this.x;
            double startY = this.y;

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0; // Convert to seconds
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0; // Clamp to ensure no overshooting

                // Interpolate position
                this.x = startX + dx * progress;
                this.y = startY + dy * progress;

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
            this.x = startX + dx;
            this.y = startY + dy;

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
            double startX = this.x;
            double startY = this.y;

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;
                double easedProgress = applyEasing(progress, easingStyle, easingDirection);

                this.x = startX + dx * easedProgress;
                this.y = startY + dy * easedProgress;

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

            this.x = startX + dx;
            this.y = startY + dy;

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
                        if (t < 1 / 2.75) {
                            return 7.5625 * t * t;
                        } else if (t < 2 / 2.75) {
                            t -= 1.5 / 2.75;
                            return 7.5625 * t * t + 0.75;
                        } else if (t < 2.5 / 2.75) {
                            t -= 2.25 / 2.75;
                            return 7.5625 * t * t + 0.9375;
                        } else {
                            t -= 2.625 / 2.75;
                            return 7.5625 * t * t + 0.984375;
                        }
                    default:
                        return t; // Default to linear if the easing type is unknown
                }
        }
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
