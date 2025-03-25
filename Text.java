package graphics;

import java.awt.*;
import java.awt.font.GlyphVector;


public class Text implements GraphicsObject {
    private String content;
    private Point position;
    private Font font = new Font("Arial", Font.PLAIN, 25);
    private Color textFillColor = Color.BLACK; // Text fill color
    private Color rectangleFillColor = null; // Rectangle background color
    private Color borderColor = Color.BLACK; // Rectangle border color
    private Color textOutlineColor = Color.BLACK; // Text outline color
    private int borderWidth = 1;
    private int textOutlineWidth = 0;
    private GraphWin canvas;
    private String alignment = "left"; // Default alignment

    /**
     * Constructs a Text object.
     * 
     * @param content  The text string.
     * @param position The top-left position of the text.
     */
    public Text(String content, Point position) {
        this.content = content;
        this.position = position;
    }
    
    /**
     * Instantly moves the text by the specified x and y distances.
     *
     * @param dx The distance to move along the x-axis.
     * @param dy The distance to move along the y-axis.
     */
    public void move(double dx, double dy) {
        position.move(position.getX() + dx, position.getY() + dy);
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
            long endTime = startTime + (long) (time * 1_000_000_000); // Convert seconds to nanoseconds
            double startX = this.position.getX();
            double startY = this.position.getY();

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0; // Convert to seconds
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0; // Clamp to ensure no overshooting

                // Interpolate position
                this.position.moveTo(startX + dx * progress, startY + dy * progress);

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
            this.position.moveTo(startX + dx, startY + dy);

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
            double startX = this.position.getX();
            double startY = this.position.getY();

            while (System.nanoTime() < endTime) {
                double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
                double progress = elapsedTime / time;
                if (progress > 1.0) progress = 1.0;
                double easedProgress = applyEasing(progress, easingStyle, easingDirection);

                this.position.moveTo(startX + dx * easedProgress, startY + dy * easedProgress);

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

            this.position.moveTo(startX + dx, startY + dy);

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

    // Setters to change text, rectangle, and outline properties
    public void setFont(Font font) {
        this.font = font;
    }
    public void setFont(String fontName, int style, int size) {
        this.font = new Font(fontName, style, size);
    }

    public void setFill(Color color) {
        this.textFillColor = color; // Changes the color of the text
    }

    public void setBackground(Color color) {
        this.rectangleFillColor = color; // Changes the background color of the rectangle
    }

    public void setBorder(Color color) {
        this.borderColor = color; // Changes the border color of the rectangle
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width; // Changes the rectangle's border width
    }

    public void setOutline(Color color) {
        this.textOutlineColor = color; // Changes the text's outline color
    }

    public void setOutlineWidth(int width) {
        this.textOutlineWidth = width; // Changes the text's outline width
    }

    public void setAlignment(String alignment) {
        // Set alignment to one of "left", "center", or "right"
        if (alignment.equals("left") || alignment.equals("center") || alignment.equals("right")) {
            this.alignment = alignment;
        } else {
            throw new IllegalArgumentException("Invalid alignment: Use 'left', 'center', or 'right'");
        }
    }

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

    @Override
    public void drawPanel(Graphics2D graphics) {
        // Remember the original settings to restore later
        Color originalColor = graphics.getColor();
        Stroke originalStroke = graphics.getStroke();
        RenderingHints originalHints = graphics.getRenderingHints();

        graphics.setFont(font);

        String[] lines = content.split("\\n"); // Split content into lines

        int totalTextHeight = 0;
        int maxWidth = 0;
        int[] lineWidths = new int[lines.length];

        // Calculate total height and max width
        for (int i = 0; i < lines.length; i++) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            int textWidth = metrics.stringWidth(lines[i]);
            int textHeight = metrics.getHeight();
            totalTextHeight += textHeight;
            maxWidth = Math.max(maxWidth, textWidth);
            lineWidths[i] = textWidth;
        }

        int x = (int) position.getX();
        int y = (int) position.getY();

        // Adjust x-coordinate based on alignment
        if (alignment.equals("center")) {
            x -= maxWidth / 2;
        } else if (alignment.equals("right")) {
            x -= maxWidth;
        }

        // Draw filled rectangle background
        if (rectangleFillColor != null) {
            graphics.setColor(rectangleFillColor);
            graphics.fillRect(x - 5, y - totalTextHeight, maxWidth + 10, totalTextHeight + 5);
        }

        // Draw border rectangle
        if (borderWidth > 0) {
            graphics.setStroke(new BasicStroke(borderWidth));
            graphics.setColor(borderColor);
            graphics.drawRect(x - 5, y - totalTextHeight, maxWidth + 10, totalTextHeight + 5);
        }

        int yOffset = 0; // Offset for each line

        for (int i = 0; i < lines.length; i++) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            int textHeight = metrics.getHeight();

            int lineX = x;

            // Adjust lineX based on line width and overall alignment
            if (alignment.equals("center")) {
                lineX = x + (maxWidth - lineWidths[i]) / 2;
            } else if (alignment.equals("right")) {
                lineX = x + (maxWidth - lineWidths[i]);
            }

            int lineY = y - totalTextHeight + yOffset + metrics.getAscent();

            // Draw text outline
            if (textOutlineWidth > 0) {
                GlyphVector glyphVector = font.createGlyphVector(graphics.getFontRenderContext(), lines[i]);
                Shape textShape = glyphVector.getOutline(lineX, lineY);

                // Activate anti-aliasing for text rendering
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                graphics.setStroke(new BasicStroke(textOutlineWidth));
                graphics.setColor(textOutlineColor);
                graphics.draw(textShape); // Draw the outline
            }

            // Draw filled text
            graphics.setStroke(new BasicStroke(1)); // Reset stroke to default for text filling
            graphics.setColor(textFillColor); // Set text fill color
            graphics.drawString(lines[i], lineX, lineY); // Fill the text shape with color

            yOffset += textHeight; // Increment yOffset for the next line
        }

        // Restore original settings
        graphics.setColor(originalColor);
        graphics.setStroke(originalStroke);
        graphics.setRenderingHints(originalHints);
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Text(");
        
        // Add content
        str.append("Content='").append(content).append("', ");
        
        // Add position
        str.append("Position=").append(position.toString()).append(", ");
        
        // Add font details
        str.append("Font=").append(font.getName()).append(", Size=").append(font.getSize()).append(", ");
        
        // Add colors
        str.append("TextFillColor=").append(textFillColor.toString()).append(", ");
        str.append("RectangleFillColor=").append(rectangleFillColor != null ? rectangleFillColor.toString() : "None").append(", ");
        str.append("BorderColor=").append(borderColor.toString()).append(", ");
        str.append("TextOutlineColor=").append(textOutlineColor.toString()).append(", ");
        
        // Add border width and outline width
        str.append("BorderWidth=").append(borderWidth).append(", ");
        str.append("TextOutlineWidth=").append(textOutlineWidth).append(", ");
        
        // Add alignment
        str.append("Alignment=").append(alignment);
        
        return str.append(")").toString();
    }
}
