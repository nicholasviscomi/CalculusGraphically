package Graphing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphingGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private JFrame frame;
    private JTextField func_field;
    private JButton graph_btn;
    private JButton clear_btn;
    private Graphics2D g2d;
    private final int width;
    private final int height;
    private String curr_func = null;
    private Point curr_click = null;

    /*
    x-values are the keys, y-values are the values
    These key value pairs will dictate at what points things are drawn
    If the space is empty, it will have the UNUSED_Y value
     */
    private final double UNUSED_Y = 10000;
    private Map<Double, Double> grid;

    /*
    Stores the head to the linked list of each function
    Maximum of 5 functions allowed
     */
    private Node[] func_heads = new Node[5];

    public GraphingGUI(int width, int height) {
        initialize_components();

        this.width = width;
        this.height = height;

        //initialize grid
        grid = new HashMap<>();
        for (double x = (double) -width/2; x < (double) width/2; x += 0.05) {
            grid.put(cvt_to_gridspace(x, true), UNUSED_Y);
        }

    }

    private void initialize_components() {
        frame = new JFrame();
        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Calculus Graphically");
        frame.setLocationRelativeTo(null);
        frame.setBackground(new Color(0xADADAD));
        frame.setLayout(null);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);

        func_field = new JTextField();
        func_field.setBounds(30, 30, 200, 30);
        func_field.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        func_field.setVisible(true);

        graph_btn = new JButton();
        graph_btn.setText("Graph");
        Dimension d = graph_btn.getPreferredSize();
        graph_btn.setBounds(235, 30, d.width, d.height);
        graph_btn.addActionListener(this);
        graph_btn.setVisible(true);

        clear_btn = new JButton();
        clear_btn.setText("Clear");
        d = clear_btn.getPreferredSize();
        clear_btn.setBounds(240 + graph_btn.getWidth(), 30, d.width, d.height);
        clear_btn.addActionListener(this);
        clear_btn.setVisible(true);

        frame.add(func_field);
        frame.add(graph_btn);
        frame.add(clear_btn);
    }

    public JFrame get_frame() {
        return frame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Draw grid
        g2d = (Graphics2D) g;
        g2d.setColor(new Color(0xC34F4F4F, true));
        g2d.setStroke(new BasicStroke(1));

        for (int y = 0; y < height; y += 20) {
            g2d.drawLine(0, y, width, y);
        }

        for (int x = 0; x < width; x += 20) {
            g2d.drawLine(x, 0, x, height);
        }

        g2d.setColor(new Color(0,0,0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(0, height/2, width, height/2); //x-axis
        g2d.drawLine(width/2,  0, width/2, height); //y-axis
        //----------------------------------------------------------------

        Node curr = get_points_from(curr_func);
        if (curr == null) { return; }
        Node prev_node = curr;
        curr = curr.next;

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(0x4848C0));
        while (curr.next != null) {
            Line2D.Double line = new Line2D.Double(prev_node.x, prev_node.y, curr.x, curr.y);
            g2d.draw(line);
            prev_node = curr;
            curr = curr.next;
        }
    }

    public Node get_points_from(String raw_func) {
        if (raw_func == null || raw_func.trim().equals("")) { return null; }
        String func = raw_func.split("=")[1].trim();
//        System.out.println(func);

        Node prev_node = null;
        Node head = null;
        for (double x = (double) -width/2; x < (double) width/2; x += 0.05) {
            String string_x = String.valueOf(x);
            if (String.valueOf(x).contains("E")) {
                string_x = "0";
            }
            String expr = func.replaceAll("x", string_x);
//            System.out.println(expr);

            try {
                double y = eval(expr); // if this throws, just return
                double trans_x = cvt_to_gridspace(x, true);
                double trans_y = cvt_to_gridspace(y, false);

                if (trans_x > width || trans_y > height) {
                    continue;
                }

                Node curr_node = new Node(null, trans_x, trans_y);
                if (prev_node != null) {
                    prev_node.next = curr_node;
                } else {
                    head = curr_node;
                }
                prev_node = curr_node;
            } catch (Error ignored) {
                return null;
            }
        }

        return head;
    }

    public void graph_derivative(Point p) {
        if (p == null) { return; }
        try {
            double x = cvt_to_gridspace(p.x, true);

            if (curr_func == null || curr_func.trim().equals("")) { return; }
            String func = curr_func.split("=")[1].trim();
            String expr = func.replaceAll("x", String.valueOf(x));
            double y = eval(expr);

            double dy = (y + 0.000001) - y;
            double dx = (x + 0.000001) - x;
            double m = dy/dx;
            String d_func = String.format("%f (x - %f) + %f", m, x, y); // y - y1 = m (x - x1)
            System.out.println("Derivative Function: " + d_func);
            curr_func = d_func;
            repaint();

        } catch (Error ignored) {}
    }

    private double cvt_to_gridspace(double val, boolean isX) {
        // Convert from computer
        // to human coordinates
        // Multiply by 20 so it fits on 20x20 grid boxes
        // Shift right by half the screen because of how the original coordinate system works
        if (isX) {
            return (val * 20) + (double) width/2;
        } else {
            return -(val * 20) + (double) height/2;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == graph_btn) {
            curr_func = func_field.getText();
            repaint();
        }

        if (e.getSource() == clear_btn) {
            curr_func = "";
            func_field.setText("");
            repaint();
        }
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(x);
                    else if (func.equals("cos")) x = Math.cos(x);
                    else if (func.equals("tan")) x = Math.tan(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked");
        curr_click = e.getPoint();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("laskdfjladskfj");
    }
}
