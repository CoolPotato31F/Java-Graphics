package graphics;

import java.util.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CountDownLatch;

/**
 * @author Kaiser Fechner
 * @version 0.0.1
 * Represents a graphical window where graphical objects can be drawn and interacted with.
 * Provides methods to add, remove, and update graphical objects like shapes, text, and images.
 * Supports mouse and keyboard input for interactive applications.
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
    private int preferedX;
    private int preferedY;
    public Panel panel;
    private ArrayList<GraphicsObject> items;
    public boolean autoflush;
    private CountDownLatch latch;
    private Point mousePosition;
    private double deltaTime = 0;
    private long lastTime;
    private int lastKey = -1;
    private final ArrayList<Integer> keysPressed = new ArrayList<Integer>();

    /**
     * Main method that demonstrates usage of GraphWin.
     *
     * @param args command-line arguments
     * @throws InterruptedException if interrupted during the program execution
     */
    public static void main(String[] args) throws InterruptedException {
        // Window setup and example usage code
        GraphWin window = new GraphWin(500, 500, "Testing", false);

        // Example of drawing graphical objects
        Point point = new Point(100, 100);
        point.draw(window);

        Line line = new Line(new Point(450, 123), new Point(350, 150));
        line.setWidth(4);
        line.setType("dashed");
        line.draw(window);

        Filter filter = new Filter(0.1f, 1f, 0f);
        Image image = new Image(new Point(450, 420), GraphWin.class.getResource("TestImage.jpg"));
        image.setScale(0.15);
        image.applyFilter(filter);
        image.draw(window);

        window.update();
        while (window.isVisible()) {
            System.out.println(window.getKey());
        }
        window.dispose();
    }

    /**
     * Constructs a GraphWin window with a specified width, height, title, and autoflush setting.
     * 
     * @param w the width of the window
     * @param h the height of the window
     * @param Name the title of the window
     * @param Autoflush whether the window should automatically flush and repaint
     */
    public GraphWin(int w, int h, String Name, boolean Autoflush) {
        super(Name);
        items = new ArrayList<GraphicsObject>();
        width = w;
        height = h;
        autoflush = Autoflush;
        setVisible(true);
        preferedX = super.getPreferredSize().width + 1;
        preferedY = super.getPreferredSize().height + 1;
        setSize(preferedX + w, preferedY + h);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        System.out.println("panel");
        this.panel = new Panel();
        this.panel.setPreferredSize(new Dimension(width, height));
        add(this.panel);

        latch = new CountDownLatch(1);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                latch.countDown();
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
                mousePosition = new Point(e.getX() - preferedX / 2, e.getY() - preferedY / 2);
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
        preferedX = super.getPreferredSize().width + 1;
        preferedY = super.getPreferredSize().height + 1;
        setSize(preferedX + w, preferedY + h);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new Panel();
        panel.setPreferredSize(new Dimension(width, height));
        System.out.println(panel);
        add(panel);

        latch = new CountDownLatch(1);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                latch.countDown();
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
                mousePosition = new Point(e.getX() - preferedX / 2, e.getY() - preferedY / 2);
            }
        });

        setVisible(true);
        update();
    }

    /**
     * Inner class to represent the panel within the window that handles rendering graphical objects.
     */
    public class Panel extends JPanel {
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
    public Point getMouse() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
     * Checks and returns the current position of the mouse without waiting for a click.
     * 
     * @return the current mouse position
     */
    public Point checkMouse() {
        return mousePosition;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public void setBackground(Color clr) {
    	if (this.panel != null) {
        	this.panel.setBackground(clr);
    	} else {
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
     * Updates the window by calculating delta time and forcing a repaint.
     */
    public void update() {
        if (lastTime == 0) {
            lastTime = System.nanoTime();
        }
        deltaTime = (System.nanoTime() - lastTime) / 1_000_000_000.0;
        lastTime = System.nanoTime();
        panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
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
}
