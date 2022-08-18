package Graphing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Objects;

public class GraphingGUI extends JPanel implements ActionListener, ChangeListener {
    private JFrame frame;
    private JTextField func_field, lb_field, ub_field; // lb = lower bound, ub = upper bound
    private JButton graph_btn, clear_btn, solve_integral_btn;
    private JCheckBox show_deriv_box;
    private JLabel slope_label;
    private JSlider rect_w_slider;
    private Graphics2D g2d;
    private final int width;
    private final int height;
    private String curr_func = null;

    //these will be changed by Frame.java during mouse events
    public Point curr_click = null, curr_mouse = null;
    public boolean mouse_on_screen, show_derivative;

    public double rect_width = 5;

    public boolean show_integral = false;
    /*
    Stores the head to the linked list of each function/derivative
    Maximum of 5 functions allowed
     */
    private Node[] func_heads = new Node[5];

    // Timer that gradually decreases the distance between the upper
    // and lower bounds until they are very close together.
    private Timer limit_def_timer;
    private final double BOUND_VAL = 60;
    private double deriv_l_bound = BOUND_VAL, deriv_u_bound = BOUND_VAL;
    private double integ_l_bound = 0, integ_u_bound = 0;
    public GraphingGUI(int width, int height) {
        initialize_components();

        this.width = width;
        this.height = height;
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
        // the main frame will have the mouse motion listeners

        func_field = new JTextField("y = 10 * sin(0.5 * x)");
        func_field.setBounds(30, 30, 300, 50);
        func_field.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        func_field.setVisible(true);

        graph_btn = new JButton();
        graph_btn.setText("Graph");
        graph_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        Dimension d = graph_btn.getPreferredSize();
        graph_btn.setBounds(
                func_field.getX() + func_field.getWidth() + 5,
                func_field.getY() + (func_field.getHeight()/2) - (d.height/2),
                d.width, d.height
        );
        graph_btn.addActionListener(this);
        graph_btn.setVisible(true);

        clear_btn = new JButton();
        clear_btn.setText("Clear");
        clear_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = clear_btn.getPreferredSize();
        clear_btn.setBounds(
                graph_btn.getX() + graph_btn.getWidth() + 5,
                func_field.getY() + (func_field.getHeight()/2) - (d.height/2),
                d.width, d.height
        );
        clear_btn.addActionListener(this);
        clear_btn.setVisible(true);

        show_deriv_box = new JCheckBox("Show Derivative");
        show_deriv_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = show_deriv_box.getPreferredSize();
        show_deriv_box.setBounds(
                func_field.getX(), func_field.getY() + func_field.getHeight(),
                d.width, d.height
        );
        show_deriv_box.setOpaque(true);
        show_deriv_box.setBackground(new Color(0xFFFFFF));
        show_deriv_box.addChangeListener(this);
        show_deriv_box.setVisible(true);

        slope_label = new JLabel("Slope: ");
        slope_label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        slope_label.setBounds(
                show_deriv_box.getX() + show_deriv_box.getWidth() + 10, show_deriv_box.getY(),
                100, show_deriv_box.getHeight()
        );
        slope_label.setOpaque(true);
        slope_label.setBackground(new Color(0xFFFFFF));
        slope_label.setVisible(true);

        lb_field = new JTextField("Lower Bound:");
        lb_field.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = lb_field.getPreferredSize();
        lb_field.setBounds(
                func_field.getX(), show_deriv_box.getY() + show_deriv_box.getHeight() + 5,
                120, d.height
        );
        lb_field.setVisible(true);

        ub_field = new JTextField("Upper Bound:");
        ub_field.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = lb_field.getPreferredSize();
        ub_field.setBounds(
                lb_field.getX() + lb_field.getWidth() + 5, lb_field.getY(),
                120, d.height
        );
        ub_field.setVisible(true);

        rect_w_slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
        rect_w_slider.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        rect_w_slider.setMinorTickSpacing(1);
        rect_w_slider.setMajorTickSpacing(5);
        rect_w_slider.setPaintLabels(true);
        rect_w_slider.setPaintTicks(true);
        rect_w_slider.setOpaque(true);
        rect_w_slider.setBackground(new Color(0xFFFFFF));
        d = rect_w_slider.getPreferredSize();
        rect_w_slider.setBounds(
                lb_field.getX(), lb_field.getY() + lb_field.getHeight() + 5,
                (ub_field.getX() + ub_field.getWidth()) - lb_field.getX(), d.height
        );
        rect_w_slider.addChangeListener(this);
        rect_w_slider.setVisible(true);

        solve_integral_btn = new JButton();
        solve_integral_btn.setText("Solve Integral");
        solve_integral_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = solve_integral_btn.getPreferredSize();
        solve_integral_btn.setBounds(
                ub_field.getX() + ub_field.getWidth() + 5,
                ub_field.getY(), d.width, ub_field.getHeight()
        );
        solve_integral_btn.addActionListener(this);
        solve_integral_btn.setVisible(true);

        frame.add(func_field);
        frame.add(graph_btn);
        frame.add(clear_btn);
        frame.add(show_deriv_box);
        frame.add(slope_label);
        frame.add(lb_field);
        frame.add(ub_field);
        frame.add(solve_integral_btn);
        frame.add(rect_w_slider);

        limit_def_timer = new Timer(3, this);
        show_derivative = false;
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
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, height/2, width, height/2); //x-axis
        g2d.drawLine(width/2,  0, width/2, height); //y-axis
        //----------------------------------------------------------------

        //draw the function
        Node curr = get_points_from(curr_func);
        if (curr == null) { return; }
        Node prev_node = curr;
        curr = curr.next;

        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(new Color(0x4848C0));
        while (curr.next != null) {
            Line2D.Double line = new Line2D.Double(prev_node.x, prev_node.y, curr.x, curr.y);
            g2d.draw(line);
            prev_node = curr;
            curr = curr.next;
        }

        g2d.setColor(new Color(0x5B5B5B));
        g2d.setStroke(new BasicStroke(3));

        if (!Objects.equals(curr_func, "")) {
            if (curr_mouse != null && show_derivative) {
                //graph derivative
                line_bt_points(curr_mouse.x, curr_mouse.x + 0.0001);
            } else if (curr_click != null && !show_derivative) {
                if (!limit_def_timer.isRunning()) {
                    limit_def_timer.start();
                }
                line_bt_points(curr_click.x - deriv_l_bound, curr_click.x + deriv_u_bound);
            } else if (show_integral) {
                approx_integral(integ_l_bound, integ_u_bound, rect_width);
            }
        }

    }

    /*
    @params lb = lower bound x-value, ub = upper bound, w = width of rectangles
     */
    public void approx_integral(double lb, double ub, double w) {
        if (curr_func == null || curr_func.trim().equals("")) {
            System.out.println("Exit");
            return;
        }
        lb = cvt_to_gridspace(lb, true);
        ub = cvt_to_gridspace(ub, true);

        g2d.setColor(new Color(0x9B4D47DC, true));
        String func = curr_func.split("=")[1].trim();
        for (double x = lb; x + w <= ub; x += w) {
            String expr = func.replaceAll("x", String.valueOf(revert_from_gridspace(x, true)));
            double height = eval(expr); // rectangle goes from x-axis to function
            System.out.println("height = " + height);

            double offset = height * 20, factor = 1;
            if (height < 0) {
                //below x-axis case
                offset = 0;
                factor = -1; //used to turn the height positive (can't have height <0)
            }

            Rectangle2D.Double rect = new Rectangle2D.Double(
                    x, cvt_to_gridspace(0, false) - offset,
                    w, height * 20 * factor
            );

            g2d.fill(rect);
        }
    }
    public void line_bt_points(double x1, double x2) {
        if (curr_func == null || curr_func.trim().equals("")) {
            System.out.println("Exit");
            return;
        }
        x1 = revert_from_gridspace(x1, true); x2 = revert_from_gridspace(x2, true);

        String func = curr_func.split("=")[1].trim();
        String expr = func.replaceAll("x", String.valueOf(x1));
        double y1 = eval(expr);

        expr = func.replaceAll("x", String.valueOf(x2));
        double y2 = eval(expr);

        double dx = x2 - x1, dy = y2 - y1;
        double m = dy/dx;
        System.out.println("m:" + m);
        String d_func = String.format("%f * (x - %f) + %f", m, x1, y1); // y - y1 = m (x - x1)

        double a_x = x1 > x2 ? x2 - 3 : x1 - 3; // 3 less than point farthest left
        double a_y = eval(d_func.replaceAll("x", String.valueOf(a_x)));
        a_x = cvt_to_gridspace(a_x, true);
        a_y = cvt_to_gridspace(a_y, false);

        double b_x = x1 > x2 ? x1 + 3 : x2 + 3; // 3 more than point farthest right
        double b_y = eval(d_func.replaceAll("x", String.valueOf(b_x)));
        b_x = cvt_to_gridspace(b_x, true);
        b_y = cvt_to_gridspace(b_y, false);

        Line2D.Double line = new Line2D.Double(a_x, a_y, b_x, b_y);
        g2d.draw(line);

        x1 = cvt_to_gridspace(x1, true); y1 = cvt_to_gridspace(y1, false);
        x2 = cvt_to_gridspace(x2, true); y2 = cvt_to_gridspace(y2, false);
        g2d.fillOval((int) (x1 - 5), (int) (y1 - 5), 10, 10);
        g2d.fillOval((int) (x2 - 5), (int) (y2 - 5), 10, 10);

        if (!Double.isNaN(m)) {
            slope_label.setText(String.format("Slope: %.1f", m));
        }
    }

    public Node get_points_from(String raw_func) {
        if (raw_func == null || raw_func.trim().equals("")) { return null; }
        String func = raw_func.split("=")[1].trim();
//        System.out.println(func);

        Node prev_node = null;
        Node head = null;
        for (double x = (double) -width/2; x < (double) width/2; x += 0.1) {
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
    private double revert_from_gridspace(double val, boolean isX) {
        // Convert from grid coordinates to raw
        // shift left/up then divide by 20
        if (isX) {
            return (val - (double) (width/2)) / 20;
        } else {
            return -(val - (double) (height/2)) / 20;
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
            slope_label.setText("");

            show_integral = false;
            lb_field.setText("Lower Bound: ");
            ub_field.setText("Lower Bound: ");
            repaint();
        }

        if (e.getSource() == solve_integral_btn) {
            System.out.println("Solving integral from " + lb_field.getText() + " to " + ub_field.getText());

            integ_l_bound = Double.parseDouble(lb_field.getText().split(":")[1].trim());
            integ_u_bound = Double.parseDouble(ub_field.getText().split(":")[1].trim());

            show_integral = true;
            show_derivative = false;
            show_deriv_box.setSelected(false);
            curr_click = null; //ensures the limit definition of derivative box doesn't trigger
            repaint();
        }

        if (e.getSource() == limit_def_timer) {
            if (deriv_u_bound > 0.1 && deriv_l_bound > 0.1) {
                deriv_u_bound -= 0.1;
                deriv_l_bound -= 0.1;
                repaint();
            } else {
                limit_def_timer.stop();
                deriv_u_bound = BOUND_VAL;
                deriv_l_bound = BOUND_VAL;
            }
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
                    else if (func.equals("abs")) x = Math.abs(x);
                    else if (func.equals("arcsin")) x = Math.asin(x);
                    else if (func.equals("arccos")) x = Math.acos(x);
                    else if (func.equals("arctan")) x = Math.atan(x);
                    else if (func.equals("log")) x = Math.log(x);
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
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == show_deriv_box) {
            show_derivative = show_deriv_box.isSelected();
            if (show_derivative) show_integral = false;
            slope_label.setText("Slope: ");
            repaint();
        }

        if (e.getSource() == rect_w_slider) {
            rect_width = rect_w_slider.getValue();
            if (rect_width == 0) rect_width = 0.5;
            repaint();
        }
    }
}
