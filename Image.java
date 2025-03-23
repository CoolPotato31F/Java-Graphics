package graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;


/**
 * Represents an image that can be drawn onto a graphical window.
 */
public class Image implements GraphicsObject {

    private Point position; // Position stored as a Point
    private int width, height; // Image dimensions
    private BufferedImage image;
    private GraphWin canvas;
    private Color outlineColor = null; // Outline color (null means no outline)
    private int outlineWidth = 1; // Outline thickness
    private String alignment = "center"; // Default alignment4
    private double rotation = 0;
    private BufferedImage original;
    private String filePath;
    /**
     * Constructs an Image object with a specified file path and position.
     * 
     * @param filePath the path to the image file
     * @param position the position of the image (Point object)
     */
    public Image(Point position, String filePath) {
        try {
            this.image = loadImage(filePath);
            this.original = deepCopy(this.image);
            this.filePath = filePath;
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.position = position;
    }

    public Image(Point position, URL filePath) {
        try {
            this.image = ImageIO.read(filePath);
            this.original = deepCopy(this.image);
            this.filePath = filePath.getFile();
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.position = position;
    }

    /**
     * Loads an image from a file path.
     * If the path is relative, it loads from the class's directory.
     * 
     * @param filePath the file path of the image
     * @return the loaded BufferedImage
     * @throws IOException if the image cannot be found or loaded
     */
    private BufferedImage loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        // Check if the path is absolute
        if (file.isAbsolute() && file.exists()) {
            return ImageIO.read(file);
        } 
        
        // Try loading from the class's resource directory
        URL resource = this.getClass().getResource(filePath);
        if (resource != null) {
            return ImageIO.read(resource);
        }
        
        // Try loading from the current working directory
        file = new File(System.getProperty("user.dir"), filePath);
        if (file.exists()) {
            return ImageIO.read(file);
        }

        throw new IOException("Image file not found: " + filePath);
    }

    /**
     * Sets the outline color of the image.
     * 
     * @param color the new outline color
     */
    public void setOutline(Color color) {
        this.outlineColor = color;
    }

    /**
     * Sets the outline width (thickness).
     * 
     * @param width the width of the outline
     */
    public void setOutlineWidth(int width) {
        this.outlineWidth = width;
    }

    /**
     * Moves the image by a given offset.
     * 
     * @param dx the change in x position
     * @param dy the change in y position
     */
    public void move(double dx, double dy) {
        this.position.move(dx, dy);
    }

    /**
     * Sets the size of the image to specific width and height.
     * 
     * @param width  the new width of the image
     * @param height the new height of the image
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Scales the image by a given factor.
     * 
     * @param scaleFactor the scale factor (e.g., 2.0 doubles the size, 0.5 halves it)
     */
    public void setScale(double scaleFactor) {
        this.width = (int) (original.getWidth() * scaleFactor);
        this.height = (int) (original.getHeight() * scaleFactor);
        BufferedImage resizedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Use high-quality rendering settings
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, this.width, this.height, null);
        g2d.dispose();

        original = resizedImage;
    }

    /**
     * Sets the alignment of the image.
     * Supported values: "top-left", "top-right", "bottom-left", "bottom-right", "center".
     * 
     * @param alignment the new alignment setting
     */
    
    public void setAlignment(String alignment) {
        if (!alignment.equals("top-left") && !alignment.equals("top-right") &&
            !alignment.equals("bottom-left") && !alignment.equals("bottom-right") &&
            !alignment.equals("center")) {
            throw new IllegalArgumentException("Invalid alignment value: " + alignment);
        }
        this.alignment = alignment;
    }

    /**
     * Calculates the adjusted x-coordinate based on alignment.
     */
    private int getAlignedX() {
        switch (alignment) {
            case "top-right":
            case "bottom-right":
                return (int) (position.getX() - width);
            case "center":
                return (int) (position.getX() - width / 2);
            default: // "top-left", "bottom-left"
                return (int) position.getX();
        }
    }

    /**
     * Calculates the adjusted y-coordinate based on alignment.
     */
    private int getAlignedY() {
        switch (alignment) {
            case "bottom-left":
            case "bottom-right":
                return (int) (position.getY() - height);
            case "center":
                return (int) (position.getY() - height / 2);
            default: // "top-left", "top-right"
                return (int) position.getY();
        }
    }
    public BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    /**
     * Rotates the image by a specified angle in degrees.
     * 
     * @param angle the angle in degrees to rotate the image
     */
    public void rotate(double angle) {
        rotation += angle;

        double radians = Math.toRadians(rotation);
        int origWidth = original.getWidth();
        int origHeight = original.getHeight();

        // Calculate new dimensions after rotation
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.floor(origWidth * cos + origHeight * sin);
        int newHeight = (int) Math.floor(origWidth * sin + origHeight * cos);

        // Create a new rotated image with a transparent background
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // Enable smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Transform to rotate around center
        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - origWidth) / 2.0, (newHeight - origHeight) / 2.0);
        transform.rotate(radians, origWidth / 2.0, origHeight / 2.0);
        
        g2d.drawImage(original, transform, null);
        g2d.dispose();

        // Update image and dimensions
        image = rotatedImage;
        width = newWidth;
        height = newHeight;
    }

    /**
     * Draws the image on a graphical window.
     * 
     * @param canvas the {@code GraphWin} where the image will be drawn
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
     * Removes the image from the graphical window.
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
     * Draws the image on a {@code Graphics2D} panel.
     * 
     * @param graphics the {@code Graphics2D} object used for rendering
     */
    @Override
    public void drawPanel(Graphics2D graphics) {
        int x = getAlignedX();
        int y = getAlignedY();

        // Draw the image
        graphics.drawImage(image, x, y, width, height, null);

        // Draw an outline if it is set
        if (outlineColor != null) {
            graphics.setColor(outlineColor);
            graphics.setStroke(new BasicStroke(outlineWidth));
            graphics.drawRect(x, y, width, height);
        }
    }
    
    @Override
    public String toString() {
        return String.format("Image(filePath=%s, position=%s, width=%d, height=%d, outlineColor=%s, " + 
                             "outlineWidth=%d, alignment=%s, rotation=%.2f)",
                             filePath, position.toString(), width, height, 
                             (outlineColor != null ? outlineColor.toString() : "none"), 
                             outlineWidth, alignment, rotation);
    }
}
