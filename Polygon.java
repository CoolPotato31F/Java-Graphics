package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Represents a polygon that can be drawn and manipulated in a graphical window.
 */
public class Polygon implements GraphicsObject {
    protected Point[] points;
    protected int width = 1; // Default stroke width
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
     * Instantly moves the polygon by the specified x and y distances.
     *
     * @param dx The distance to move along the x-axis.
     * @param dy The distance to move along the y-axis.
     */
    public void move(double dx, double dy) {
        for (Point point : points) {
            point.move(point.getX() + dx, point.getY() + dy);
        }
        if (canvas != null && canvas.autoflush) {
            canvas.repaint();
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
            long endTime = startTime + (long) (time * 1_000_000_000);

            // Store the initial positions of all points
            Point[] startPositions = new Point[points.length];
            for (int i = 0; i < points.length; i++) {
                startPositions[i] = new Point(points[i].getX(), points[i].getY());
            }

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;

                // Interpolate the new position based on original positions
                for (int i = 0; i < points.length; i++) {
                    double newX = startPositions[i].getX() + dx * progress;
                    double newY = startPositions[i].getY() + dy * progress;
                    points[i].moveTo(newX, newY);
                }

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

            // Ensure final position is set exactly
            for (int i = 0; i < points.length; i++) {
                points[i].moveTo(startPositions[i].getX() + dx, startPositions[i].getY() + dy);
            }

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
            Point[] startPositions = new Point[points.length];
            for (int i = 0; i < points.length; i++) {
                startPositions[i] = new Point(points[i].getX(), points[i].getY());
            }
            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;
                double easedProgress = applyEasing(progress, easingStyle, easingDirection);

                for (int i = 0; i < points.length; i++) {
                    double newX = startPositions[i].getX() + dx * easedProgress;
                    double newY = startPositions[i].getY() + dy * easedProgress;
                    points[i].moveTo(newX, newY);
                }

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

            for (int i = 0; i < points.length; i++) {
                double newX = startPositions[i].getX() + dx;
                double newY = startPositions[i].getY() + dy;
                points[i].moveTo(newX, newY);
            }

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
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Polygon(");
        for (int i = 0; i < points.length; i++) {
            str.append(points[i].toString());
            if (i < points.length - 1) {
                str.append(", ");
            }
        }
        str.append(")");
        return str.toString();
    }

}
