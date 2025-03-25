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
     * Moves the line by a given offset.
     *
     * @param dx the change in x position
     * @param dy the change in y position
     */
    public void move(double dx, double dy) {
        point1.move(point1.getX() + dx, point1.getY() + dy);
        point2.move(point2.getX() + dx, point2.getY() + dy);
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
            throw new IllegalArgumentException("Invalid line type: " + type + "\nValid types: \"solid\", \"dashed\", \"dotted\"");
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
    
    @Override
    public String toString() {
        return String.format("Line(point1=%s, point2=%s, width=%d, outlineColor=%s, lineType=%s)",
                             point1.toString(), point2.toString(), width, outlineColor.toString(), lineType);
    }
}
