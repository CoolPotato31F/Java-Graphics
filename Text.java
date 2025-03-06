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

}
