package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
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
    private String alignment = "center"; // Default alignment

    /**
     * Constructs an Image object with a specified file path and position.
     * 
     * @param filePath the path to the image file
     * @param position the position of the image (Point object)
     */
    public Image(Point position, String filePath) {
        try {
            this.image = loadImage(filePath);
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
        System.out.println(filePath);
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
        this.width = (int) (image.getWidth() * scaleFactor);
        this.height = (int) (image.getHeight() * scaleFactor);
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
    public void applyFilter(Filter filter) {
        // Create a scaling array with three constants (RGB)
        float[] scaleFactors = new float[3];
        for (int i = 0; i < 3; i++) {
            scaleFactors[i] = 1.0f + filter.getBrightness();  // Apply brightness scaling to R, G, and B
        }

        // Create the RescaleOp with the proper scaling factors for RGB channels
        RescaleOp rescaleOp = new RescaleOp(scaleFactors, new float[]{0, 0, 0}, null);
        
        // Create a new image to hold the brightness-adjusted result
        BufferedImage brightnessAdjusted = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = brightnessAdjusted.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        rescaleOp.filter(brightnessAdjusted, brightnessAdjusted);

        // Now apply hue and saturation adjustments
        for (int x = 0; x < brightnessAdjusted.getWidth(); x++) {
            for (int y = 0; y < brightnessAdjusted.getHeight(); y++) {
                Color originalColor = new Color(brightnessAdjusted.getRGB(x, y));
                
                // Convert to HSB (Hue, Saturation, Brightness) to adjust hue and saturation
                float[] hsbValues = Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), null);
                
                // Adjust hue and saturation
                float hue = (hsbValues[0] + filter.getHue()) % 1.0f; // Keep hue in the range [0, 1)
                float saturation = Math.min(1.0f, hsbValues[1] * filter.getSaturation()); // Clamp saturation to 1
                hsbValues[0] = hue;
                hsbValues[1] = saturation;

                // Convert back to RGB and set the pixel
                int newRGB = Color.HSBtoRGB(hsbValues[0], hsbValues[1], hsbValues[2]);
                brightnessAdjusted.setRGB(x, y, newRGB);
            }
        }

        // Set the adjusted image as the new image
        this.image = brightnessAdjusted;
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
}
