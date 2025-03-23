# Java Graphics

## Overview
The **Graphics** package provides a simple yet powerful framework for creating graphical windows and rendering objects in Java. It supports various graphical elements such as shapes, text, images, and interactive input via mouse and keyboard.

## Features
- Basic graphical window (`GraphWin`) for rendering shapes and images
- `GraphicsObject` interface for managing graphical elements
- `Point` class for representing and drawing 2D points
- Interactive user input handling (keyboard and mouse)
- Support for updating and refreshing graphical components

## Installation
To use the **Graphics** package, add the `graphics` package to your Java project and ensure the necessary dependencies (such as `javax.swing` and `java.awt`) are included.

## Usage
### Creating a Graphical Window
```java
GraphWin window = new GraphWin("My Window", 500, 500, false);
```
This creates a 500x500 pixel window with the title "My Window".

### Drawing a Point
```java
Point point = new Point(100, 100);
point.draw(window);
```
This creates and draws a point at coordinates (100, 100).

### Drawing a Line
```java
Line line = new Line(new Point(450, 123), new Point(350, 150));
line.setWidth(4);
line.setType("dashed");
line.draw(window);
```
This creates a dashed line with a width of 4 pixels.

### Drawing a Circle
```java
Circle circ = new Circle(new Point(245, 180), 55);
circ.setFill(Color.RED);
circ.setWidth(3);
circ.draw(window);
```
This creates a Circle with a width of 3 and a filled color of red

### Drawing a Oval
```java
Oval oval = new Oval(new Point(30, 350), new Point(180, 450));
oval.setFill(Color.YELLOW);
oval.setWidth(10);
oval.setOutline(Color.BLUE);
oval.draw(window);
```
This creates a Oval with a width of 10 and a filled color of Yellow and a outline collor of Blue

### Drawing a Text
```java
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
```
This creates Text with a outline of 4 a box around it colored green a border of that box that is orange with aligned text in the center with the arial font at size 25

### Drawing a Polyon
```java
Point[] points = {new Point(350, 230), new Point(375, 300), new Point(245, 385)};
Polygon poly = new Polygon(points);
poly.setWidth(10);
poly.setFill(Color.MAGENTA);
poly.draw(window);
```
This creates a Polygon with a filled color Magenta with a width of 10

### Handling User Input
To capture key presses:
```java
int keyPressed = window.getKey();
System.out.println("Key Pressed: " + keyPressed);
```
To get the current mouse position:
```java
Point mousePos = window.getMouse();
System.out.println("Mouse Position: " + mousePos);
```

## Version History
### Version 0.0.2
- Added `RotatablePolygon` to the library

### Version 0.0.1
- Basic graphical objects and rendering functionality

## License
This package is licensed under the MIT License.

## Author
**Kaiser Fechner**

For any issues or feature requests, please open an issue on GitHub.

![image](https://github.com/user-attachments/assets/a3f3c404-8fee-4ea2-89d5-b352040b3f31)
