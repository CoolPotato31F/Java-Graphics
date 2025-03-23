package graphics;

import java.awt.*;

public class RotatablePolygon extends Polygon{
    private Point[] originalPoints;
    private Point center;
    private double rotation = 0;
    
    public RotatablePolygon(Point[] p) {
        super(p);  // Call the superclass constructor (Polygon)
        this.originalPoints = deepCopy(p);
        this.points = deepCopy(p);
        center = findCentroid();
    }

    private static Point[] deepCopy(Point[] original) {
        if (original == null) return null;

        Point[] copy = new Point[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new Point(original[i].getX(), original[i].getY()); // Creating a new object for deep copy
        }
        return copy;
    }

    private Point findCentroid() {
        double sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.getX();
            sumY += p.getY();
        }
        center = new Point((int) (sumX / points.length), (int) (sumY / points.length));
        return center;
    }

    public Point getCenter() {
        if (center == null) {
            return findCentroid();
        }
        return center.clone();
    }

    public void rotate(double degree) {
        if (points == null || points.length == 0) {
            return; // Nothing to rotate
        }

        rotation += degree;
        rotation %= 360;

        double radians = Math.toRadians(rotation);
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);

        for (int i = 0; i < originalPoints.length; i++) {
            double x = originalPoints[i].getX() - center.getX();
            double y = originalPoints[i].getY() - center.getY();

            double newX = x * cosTheta - y * sinTheta + center.getX();
            double newY = x * sinTheta + y * cosTheta + center.getY();

            points[i].moveTo(newX, newY); // Use setLocation and round
        }
    }

    public Point[] getOriginalPoints() {
        return originalPoints;
    }

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
        StringBuilder str = new StringBuilder("RotatablePolygon(");
        
        // Add information about the current points of the polygon
        str.append("Points=[");
        for (Point p : points) {
            str.append(p.toString()).append(", ");
        }
        // Remove the last comma and space
        if (points.length > 0) {
            str.setLength(str.length() - 2);
        }
        str.append("], ");
        
        // Add center point
        str.append("Center=").append(center.toString()).append(", ");
        
        // Add rotation angle
        str.append("Rotation=").append(rotation).append("°");
        
        return str.append(")").toString();
    }
}
