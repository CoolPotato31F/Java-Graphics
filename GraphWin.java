package graphics;

import java.util.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Kaiser Fechner
 * @version 0.0.1
 * Represents a graphical window where graphical objects can be drawn and interacted with.
 * Provides methods to add, remove, and update graphical objects like shapes, text, and images.
 * Supports mouse and keyboard input for interactive applications.
 * 
 * All versions below are implemented unless explicitly stated in a above version.
 * 
 * |================= Version 0.0.2 =================|
 * Added RotatablePolygon to the library.
 * The RotatablePolygon is castable to a Polygon which will just make it a
 * standard polygon with no ability to rotate.
 * |================= Version 0.0.1 =================|
 * Most simple version with just basic shapes and uses for those shape. 
 * Very simple structure and all shapes are easily changed to adjust apperance
 */

public class GraphWin extends JFrame {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 9015929356158313978L;

    /**
     * A set of standard colors used in graphical objects.
     */
    public static final Color[] STANDARD_COLORS = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, 
        Color.CYAN, Color.MAGENTA, Color.BLACK, 
        Color.LIGHT_GRAY, Color.DARK_GRAY, Color.ORANGE
    };

    private int width;
    private int height;
    public Panel panel;
    private ArrayList<GraphicsObject> items;
    public boolean autoflush;
    private CountDownLatch latch;
    private Point mousePosition;
    private double deltaTime = 0;
    private long lastTime;
    private int lastKey = -1;
    public boolean redraw = false;
    private final ArrayList<Integer> keysPressed = new ArrayList<Integer>();
    private boolean mousePressed = false;

    /**
     * Main method that demonstrates usage of GraphWin.
     *
     * @param args command-line arguments
     * @throws InterruptedException if interrupted during the program execution
     */
    public static void main(String[] args) throws InterruptedException {
        GraphWin window = new GraphWin("Testing", 500, 500, false);
        // Set the background color to cyan.
        window.setBackground(Color.CYAN);
        // Update the window to display the background.
        

        // Draw a point.
        Point point = new Point(100, 100);
        point.draw(window);

        // Draw various lines with different styles and widths.
        Line line = new Line(new Point(450, 123), new Point(350, 150));
        line.setWidth(4);
        line.setType("dashed");
        line.draw(window);

        line = new Line(new Point(450, 133), new Point(350, 160));
        line.setWidth(3);
        line.setType("dotted");
        line.draw(window);

        line = new Line(new Point(450, 113), new Point(350, 140));
        line.setWidth(3);
        line.setType("solid");
        line.draw(window);
        // Load and draw an image.
        Image image = new Image(new Point(450, 420), GraphWin.class.getResource("TestImage.jpg")); // Image initialization can take time.
        image.setScale(0.15);
        image.draw(window);

        // Draw a rectangle.
        Rectangle rect = new Rectangle(new Point(46, 200), new Point(146, 300));
        rect.setFill(Color.BLUE);
        rect.setWidth(3);
        rect.draw(window);

        // Draw a rotatable polygon.
        Point[] points = {new Point(350, 230), new Point(375, 300), new Point(245, 385)};
        RotatablePolygon poly = new RotatablePolygon(points);
        poly.rotate(40);
        poly.setWidth(15);
        poly.setFill(Color.MAGENTA);
        poly.draw(window);
        poly.getCenter().draw(window); //draw the center of the polygon.

        // Draw a circle.
        Circle circ = new Circle(new Point(245, 180), 55);
        circ.setFill(Color.RED);
        circ.setWidth(3);
        circ.draw(window);

        // Draw an oval.
        Oval oval = new Oval(new Point(30, 350), new Point(180, 450));
        oval.setFill(Color.YELLOW);
        oval.setWidth(10);
        oval.setOutline(Color.BLUE);
        oval.draw(window);

        // Draw text with various formatting.
        Text text = new Text("abcdefghijklmnopqrstuvwxyz\nABCDEFGHIJKLMNOPQRSTUFWXYZ\n1234567890!@#$%^&*()", new Point(250, 100));
        text.setFill(Color.BLUE);
        text.setOutlineWidth(4);
        text.setOutline(Color.BLACK);
        text.setBackground(Color.GREEN);
        text.setBorderWidth(2);
        text.setBorder(Color.ORANGE);
        text.setAlignment("center");
        text.setFont("Arial", Font.BOLD, 25);
        text.draw(window);

        // Update the window to display all drawn objects.
        window.update();

        // Animation loop: rotate the polygon.
        while (window.isVisible()) {
            poly.rotate(100 * window.getDeltaTime());
            image.rotate(100 * window.getDeltaTime());
            window.setTitle("FPS: "+Math.round(1/window.getDeltaTime()));
            window.update();
        }

        // Dispose of the window resources.
        window.dispose();
    }

    /**
     * Constructs a GraphWin window with a specified width, height, title, and autoflush setting.
     * 
     * @param Name the title of the window
     * @param w the width of the window
     * @param h the height of the window
     * @param Autoflush whether the window should automatically flush and repaint
     */
    public GraphWin(String Name, int w, int h, boolean Autoflush) {
        super(Name);
        items = new ArrayList<GraphicsObject>();
        width = w;
        height = h;
        autoflush = Autoflush;
        setVisible(true);
        Insets insets = getInsets();
        setSize(insets.left + insets.right + w, insets.top + insets.bottom + h);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.panel = new Panel();
        this.panel.setPreferredSize(new Dimension(width, height));
        add(this.panel);

        latch = new CountDownLatch(1);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                latch.countDown();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = false;
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                lastKey = e.getKeyChar();
                int keyString = e.getKeyCode();
                if (!keysPressed.contains(keyString)) {
                    keysPressed.add(keyString);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            	int keyString = e.getKeyCode();
            	
                keysPressed.remove(keysPressed.indexOf(keyString));
            }
        });
        mousePosition = new Point(-1, -1);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Insets insets = getInsets();
                mousePosition = new Point(e.getX() - insets.left, e.getY() - insets.top);
            }
        });

        setVisible(true);
        update();
    }

    /**
     * Constructs a GraphWin window with a specified width, height, and title.
     * The autoflush setting is set to false by default.
     * 
     * @param Name the title of the window
     * @param w the width of the window
     * @param h the height of the window
     */
    public GraphWin(String Name, int w, int h) {
        super(Name);
        items = new ArrayList<GraphicsObject>();
        width = w;
        height = h;
        autoflush = false;
        setVisible(true);
        Insets insets = getInsets();
        setSize(insets.left + insets.right + w, insets.top + insets.bottom + h);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new Panel();
        panel.setPreferredSize(new Dimension(width, height));
        add(panel);

        latch = new CountDownLatch(1);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                latch.countDown();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = false;
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                lastKey = e.getKeyChar();
                int keyString = e.getKeyCode();
                if (!keysPressed.contains(keyString)) {
                    keysPressed.add(keyString);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyString = e.getKeyCode();
                keysPressed.remove(keyString);
            }
        });
        mousePosition = new Point(-1, -1);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Insets insets = getInsets();
                mousePosition = new Point(e.getX() - insets.left, e.getY() - insets.top);
            }
        });

        setVisible(true);
        update();
    }

    /**
     * Inner class to represent the panel within the window that handles rendering graphical objects.
     */
    private class Panel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8207838817598203160L;

		public Panel() {
            super();
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
        	if (!redraw) {return;}
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < items.size(); i++) {
                items.get(i).drawPanel(g2d);
            }
        }
    }

    /**
     * Retrieves the color at the specified point in the window.
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the color at the point, or null if no component exists at the point
     */
    public Color getColorAtPoint(int x, int y) {
        Component component = getComponentAt(x, y);
        if (component != null) {
            return component.getBackground();
        } else {
            return null;
        }
    }

    /**
     * Retrieves the current position of the mouse in the window.
     * This method blocks until the mouse is clicked.
     * 
     * @return a Point object representing the current mouse position
     */
    public boolean getMouse() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /**
     * Returns the current position of the mouse as a Point.
     * 
     * @return the current mouse position
     */
    public Point getCurrentMousePosition() {
        return mousePosition;
    }

    /**
     * Waits for a key press and returns the key that was pressed.
     * This method blocks until a key is pressed.
     * 
     * @return the key that was pressed as a String
     */
    public int getKey() {
        lastKey = -1;
        while (lastKey == -1) {
            if (!isDisplayable()) {
                throw new RuntimeException("getKey in closed window");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int key = lastKey;
        lastKey = -1;
        return key;
    }

    /**
     * Checks if the left mouse button is being held down.
     * 
     * @return true if the left mouse button is pressed, false otherwise
     */
    public boolean checkMouse() {
        return mousePressed;
    }
    /**
     * @return the current width of the GraphWin object
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * @return the current height of the GraphWin object
     */
    @Override
    public int getHeight() {
        return height;
    }
    /**
     * Sets the background of the GraphWin
     * 
     * @param clr the color to set the Background
     */
    @Override
    public void setBackground(Color clr) {
    	// Forces the background to also call the panel set Background
    	if (this.panel != null) {
    		super.setBackground(clr);
        	this.panel.setBackground(clr);
        	if (autoflush) {
        		update();
        	}
    	} else { // Forced to do this because this function gets called on GraphWin initialization
    		super.setBackground(clr);
    	}
    }

    /**
     * Removes a graphical object from the window.
     * 
     * @param object the graphical object to remove
     */
    public void deleteItem(GraphicsObject object) {
        items.remove(object);
    }

    /**
     * Adds a graphical object to the window.
     * 
     * @param object the graphical object to add
     */
    public void addItem(GraphicsObject object) {
        items.add(object);
    }
    

    /**
     * Returns all objects drawn on the window;
     *  
     * @return Array of all items in the window
     */
    public ArrayList<GraphicsObject> getItems() {
        return items;
    }

    /**
     * Updates the window by calculating delta time and forcing a repaint.
     */
    public void update() {
        redraw = true;
        if (lastTime == 0) {
            lastTime = System.nanoTime();
        }
        deltaTime = (System.nanoTime() - lastTime) / 1_000_000_000.0;
        lastTime = System.nanoTime();
        
        SwingUtilities.invokeLater(() -> {
            panel.repaint();
        });

        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Returns the time difference between the last update and the current update.
     * 
     * @return the delta time in seconds
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     * Returns a list of the keys that are currently pressed.
     * 
     * @return a list of pressed keys
     */
    public ArrayList<Integer> checkKeys() {
        return new ArrayList<>(keysPressed);
    }
    
    public void help() {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            	System.out.println("Opening Java Graphics Wiki...");
                desktop.browse(new URI("https://github.com/CoolPotato31F/Java-Graphics/wiki"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
