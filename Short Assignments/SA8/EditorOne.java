import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

/**
 * Basic shape drawing GUI
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, lightly revised Winter 2014
 * @author CBK, restructured Shape/Drawer and some of the GUI, Spring 2016
 * @author CBK, more restructuring and simplification, Fall 2016
 */

public class EditorOne extends JFrame {	
	private static final int width = 800, height = 800;

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	private Ellipse shape = null;				// the only object (if any) in our editor
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

	public EditorOne() {
		super("Graphical Editor");

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				// TODO: YOUR CODE HERE
				// Call helper method to draw the sketch on g
				drawSketch(g);
				System.out.println("repainting!");
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				// TODO: YOUR CODE HERE
				// Call helper method to handle the mouse
				handlePress(event.getPoint());
//				System.out.println("pressed at "+event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				// TODO: YOUR CODE HERE
				// Call helper method to handle the mouse release
				handleRelease();
//				System.out.println("released at "+event.getPoint());
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				// TODO: YOUR CODE HERE
				// Call helper method to handle the mouse drag
				handleDrag(event.getPoint());
//				System.out.println("dragged to "+event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse"};
		JComboBox<String> shapeB = new JComboBox<>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Helper method for press at point
	 */
	private void handlePress(Point p) {
		// TODO: YOUR CODE HERE
		switch (mode) {
			case DRAW -> {
				// In drawing mode, start drawing a new shape
				if (drawFrom == null) drawFrom = p;
				else {
					int x1 = (int) drawFrom.getX();
					int y1 = (int) drawFrom.getY();
					int x2 = (int) p.getX();
					int y2 = (int) p.getY();
					this.shape = new Ellipse(x1, y1, x2, y2, color);
					System.out.println(this.shape);
				}
			}
			case MOVE -> {
				// In moving mode, start dragging if clicked in the shape
				if (moveFrom == null) moveFrom = p;
			}
			case RECOLOR -> {
				// In recoloring mode, change the shape's color if clicked in it
				Random random = new Random();

				//NB: I also noticed I was trying to generate new colors with nextInt(),
				// instead of floats between 0.0 and 1.0
				this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
				shape.setColor(color);

			}
			case DELETE -> {
				// In deleting mode, delete the shape if clicked in it
				if (shape.contains((int)p.getX(), (int)p.getY())) {
					shape = null;
					drawFrom = null;
					moveFrom = null;
				}
			}
		}

		// Be sure to refresh the canvas (repaint) if the appearance has changed
		repaint();
	}

	/**
	 * Helper method for drag to new point
	 */
	private void handleDrag(Point p) {
		// TODO: YOUR CODE HERE
		switch (mode) {
			case DRAW -> {
				// In drawing mode, revise the shape as it is stretched out
				int x1 = (int) drawFrom.getX();
				int y1 = (int) drawFrom.getY();
				int x2 = (int)p.getX();
				int y2 = (int)p.getY();
				if (shape == null) shape = new Ellipse(x1, y1, x2, y2, color);
				else shape.setCorners(x1, y1, x2, y2);
			}

			case MOVE -> {
				// In moving mode, shift the object and keep track of where next step is from
				if (shape.contains((int)p.getX(), (int)p.getY())) {
					int dx = (int) (p.getX() - moveFrom.getX());
					int dy = (int) (p.getY() - moveFrom.getY());
					this.shape.moveBy(dx, dy);
					moveFrom = p;
				}
			}
		}

		// Be sure to refresh the canvas (repaint) if the appearance has changed
		repaint();
	}

	/**
	 * Helper method for release
	 */
	private void handleRelease() {
		// TODO: YOUR CODE HERE
		// In moving mode, stop dragging the object
		if (mode == Mode.MOVE) {
			moveFrom = null;
		}
		// Be sure to refresh the canvas (repaint) if the appearance has changed
//		repaint();
	}

	/**
	 * Draw the whole sketch (here maybe a single shape)
	 */
	private void drawSketch(Graphics g) {
		// TODO: YOUR CODE HERE
		// Draw the current shape if it exists
		if (shape != null) shape.draw(g);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(EditorOne::new);
	}
}
