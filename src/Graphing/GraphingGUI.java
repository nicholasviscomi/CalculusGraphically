package Graphing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class GraphingGUI extends JPanel implements ActionListener, ChangeListener {
    private JFrame frame;
    private JTextField func_field, lb_field, ub_field; // lb = lower bound, ub = upper bound
    private JButton graph_btn, clear_btn, approx_integral_btn;
    private JCheckBox show_tang_box, show_deriv_box; //show tangent line
    private JLabel slope_label, area_label;
    private JSlider rect_w_slider;
    private Graphics2D g2d;
    private final int width;
    private final int height;
    private String curr_func = null;

    //these will be changed by Frame.java during mouse events
    public Point curr_click = null, curr_mouse = null;
    public boolean mouse_on_screen, show_tangent;

    public double rect_width = 5;

    public boolean show_integral = false, show_derivative = false;
    /*
    Stores the head to the linked list of each function/derivative
    Maximum of 5 functions allowed
     */
    private Node[] func_heads = new Node[5];

    // Timer that gradually decreases the distance between the upper
    // and lower bounds until they are very close together.
    private Timer limit_def_timer;
    private final double BOUND_VAL = 45;
    private double deriv_l_bound = BOUND_VAL, deriv_u_bound = BOUND_VAL;
    private double integ_l_bound = 0, integ_u_bound = 0;
    private final Color[] colors = new Color[] {
            Color.BLUE, Color.RED, Color.ORANGE
    };

    private Section diff_section, integ_section, transf_section;

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

        show_tang_box = new JCheckBox("Show Tangent");
        show_tang_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = show_tang_box.getPreferredSize();
        show_tang_box.setBounds(
                func_field.getX(), func_field.getY() + func_field.getHeight(),
                d.width, d.height
        );
        show_tang_box.setOpaque(true);
        show_tang_box.setBackground(new Color(0xFFFFFF));
        show_tang_box.addChangeListener(this);
        show_tang_box.setVisible(true);

        slope_label = new JLabel("Slope: ");
        slope_label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        slope_label.setBounds(
                show_tang_box.getX() + show_tang_box.getWidth() + 10, show_tang_box.getY(),
                100, show_tang_box.getHeight()
        );
        slope_label.setOpaque(true);
        slope_label.setBackground(new Color(0xFFFFFF));
        slope_label.setVisible(true);

        show_deriv_box = new JCheckBox("Show Derivative");
        show_deriv_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = show_deriv_box.getPreferredSize();
        show_deriv_box.setBounds(
                slope_label.getX() + slope_label.getWidth() + 5, slope_label.getY(),
                d.width, slope_label.getHeight()
        );
        show_deriv_box.addChangeListener(this);
        show_deriv_box.setOpaque(true);
        show_deriv_box.setBackground(new Color(0xFFFFFF));
        show_deriv_box.setVisible(true);

        lb_field = new JTextField("Lower Bound: 0");
        lb_field.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = lb_field.getPreferredSize();
        lb_field.setBounds(
                func_field.getX(), show_tang_box.getY() + show_tang_box.getHeight() + 5,
                120, d.height
        );
        lb_field.setVisible(true);

        ub_field = new JTextField("Upper Bound: 6.28");
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

        approx_integral_btn = new JButton();
        approx_integral_btn.setText("Approximate Integral");
        approx_integral_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = approx_integral_btn.getPreferredSize();
        approx_integral_btn.setBounds(
                ub_field.getX() + ub_field.getWidth() + 5,
                ub_field.getY(), d.width, ub_field.getHeight()
        );
        approx_integral_btn.addActionListener(this);
        approx_integral_btn.setVisible(true);

        area_label = new JLabel("Area: ");
        area_label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        area_label.setBounds(
                approx_integral_btn.getX() + approx_integral_btn.getWidth() + 10, approx_integral_btn.getY(),
                100, approx_integral_btn.getHeight()
        );
        area_label.setOpaque(true);
        area_label.setBackground(new Color(0xFFFFFF));
        area_label.setVisible(true);

        diff_section = new Section(new Color(0xFFFFFF), 400, 300, 200, 100, "Differentiation");
        integ_section = new Section(new Color(0xFFFFFF), 400, 410, 200, 100, "Integration");
        transf_section = new Section(new Color(0xFFFFFF), 400, 520, 200, 100, "Transformations");

        frame.add(func_field);
        frame.add(graph_btn);
        frame.add(clear_btn);
        frame.add(show_tang_box);
        frame.add(slope_label);
        frame.add(lb_field);
        frame.add(ub_field);
        frame.add(approx_integral_btn);
        frame.add(rect_w_slider);
        frame.add(show_deriv_box);
        frame.add(area_label);
        frame.add(diff_section);
        frame.add(integ_section);
        frame.add(transf_section);

        limit_def_timer = new Timer(3, this);
        show_tangent = false;

        func_heads = new Node[] {
                null, null, null, null, null
        };
    }

    public JFrame get_frame() {
        return frame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Draw grid
        g2d = (Graphics2D) g;
        g2d.setColor(new Color(0x7E4F4F4F, true));
        g2d.setStroke(new BasicStroke(1));

        //horiz lines
        for (int y = 0; y < height; y += 20) {
            g2d.drawLine(0, y, width, y);
        }
        //vert lines
        for (int x = 0; x < width; x += 20) {
            g2d.drawLine(x, 0, x, height);
        }

        g2d.setColor(new Color(0xFF000000, true));
        //y-axis labels
        int label = height / 20 / 2;
        int offset;
        for (int y = 0; y < height; y += 20) {
            if (label == 0) {
                label--;
                continue;
            }
            if (label > 0) {
                offset = 5;
            } else {
                offset = 0;
            }
            if (label % 2 == 0) {
                g.drawString(String.valueOf(label), width / 2 + 2 + offset, y + 5);
            }
            label--;
        }

        //x-axis labels
        label = -(width / 20) / 2;
        for (int x = 0; x < width; x += 20) {
            if (label == 0) {
                label++;
                continue;
            }
            if (label < 0) {
                offset = 12;
            } else {
                offset = 8;
            }
            if (label % 2 == 0) {
                g.drawString(String.valueOf(label), x - offset, height/2 + 14);
            }
            label++;
        }

        g2d.setColor(new Color(0,0,0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, height/2, width, height/2); //x-axis
        g2d.drawLine(width/2,  0, width/2, height); //y-axis
        //----------------------------------------------------------------

        Image integral_img = Toolkit.getDefaultToolkit().getImage("src/Assets/Integral.png");
        g2d.setColor(new Color(0xFFFFFF));
        g2d.fillRect(width/2 + 35, 25, integral_img.getWidth(this)/3, integral_img.getHeight(this)/3 + 10);
        g2d.drawImage(integral_img, width/2 + 40, 30, integral_img.getWidth(this)/3, integral_img.getHeight(this)/3, this);

        Image lim_def_img = Toolkit.getDefaultToolkit().getImage("src/Assets/limit_def_deriv.png");
        g2d.setColor(new Color(0xFFFFFF));
        g2d.fillRect(width/2 + 205, 25, lim_def_img.getWidth(this)/6, lim_def_img.getHeight(this)/6);
        g2d.drawImage(lim_def_img, width/2 + 200, 30, lim_def_img.getWidth(this)/6, lim_def_img.getHeight(this)/6, this);

        //draw the functions
        for (int i = 0; i < func_heads.length; i++) {
            Node curr = func_heads[i];
            Color color = colors[i];

            if (curr == null) { break; }
            Node prev_node = curr;
            curr = curr.next;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(color);
            while (curr.next != null) {
                Line2D.Double line = new Line2D.Double(prev_node.x, prev_node.y, curr.x, curr.y);
                g2d.draw(line);
                prev_node = curr;
                curr = curr.next;
            }
        }

        g2d.setColor(new Color(0));
        g2d.setStroke(new BasicStroke(3));

        if (!Objects.equals(curr_func, "")) {
            if (curr_mouse != null && show_tangent) {
                //graph derivative
                line_bt_points(curr_mouse.x, curr_mouse.x + 0.0001);
                if (show_derivative) {
                    //plot a circle on the deriv graph
                    double y = Double.parseDouble(slope_label.getText().split(":")[1]);
                    int rad = 10;
                    g2d.fillOval(curr_mouse.x - rad/2, (int) (cvt_to_gridspace(y, false) - rad/2), rad, rad);
                }
            } else if (curr_click != null && !show_tangent) {
                //animate limit definition of derivative
                if (!limit_def_timer.isRunning()) {
                    limit_def_timer.start();
                }
                line_bt_points(curr_click.x, curr_click.x + deriv_u_bound);
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

        double total_area = 0;
        g2d.setColor(new Color(0x9B4D47DC, true));
        String func = curr_func.split("=")[1].trim();
        for (double x = lb; x + w <= ub; x += w) {
            String expr = func.replaceAll("x", String.valueOf(revert_from_gridspace(x, true)));
            double height = Calculate.eval(expr); // rectangle goes from x-axis to function

            double offset = height * 20, factor = 1;
            if (height < 0) {
                //below x-axis case
                offset = 0;
                //used to turn the height positive (can't have height <0)
                //allows rectangles to be draw above and below x-axis
                factor = -1;
            }

            Rectangle2D.Double rect = new Rectangle2D.Double(
                    x, cvt_to_gridspace(0, false) - offset,
                    w, height * 20 * factor
            );

            g2d.fill(rect);

            total_area += (rect.getWidth() / 20) * (rect.getHeight() / 20);
        }
        area_label.setText(String.format("Area: %.2f", total_area));
    }
    public void line_bt_points(double x1, double x2) {
        if (curr_func == null || curr_func.trim().equals("")) {
            System.out.println("Exit");
            return;
        }
        x1 = revert_from_gridspace(x1, true); x2 = revert_from_gridspace(x2, true);

        String func = curr_func.split("=")[1].trim();
        String expr = func.replaceAll("x", String.valueOf(x1));
        double y1 = Calculate.eval(expr);

        expr = func.replaceAll("x", String.valueOf(x2));
        double y2 = Calculate.eval(expr);

        double dx = x2 - x1, dy = y2 - y1;
        double m = dy/dx;
        String d_func = String.format("%f * (x - %f) + %f", m, x1, y1); // y - y1 = m (x - x1)

        double dist = 1; //how far away the line should go from the point
        double a_x = x1 > x2 ? x2 - dist : x1 - dist; // 3 less than point farthest left
        double a_y = Calculate.eval(d_func.replaceAll("x", String.valueOf(a_x)));
        a_x = cvt_to_gridspace(a_x, true);
        a_y = cvt_to_gridspace(a_y, false);

        double b_x = x1 > x2 ? x1 + dist : x2 + dist; // 3 more than point farthest right
        double b_y = Calculate.eval(d_func.replaceAll("x", String.valueOf(b_x)));
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

    public Node[] get_points_from(String raw_func, boolean get_deriv) {
        if (raw_func == null || raw_func.trim().equals("")) { return null; }
        String func = raw_func.split("=")[1].trim();
//        System.out.println(func);

        Node prev_func_node = null, prev_deriv_node = null;;
        Node func_head = null, deriv_head = null;
        for (double x = (double) -width/2; x < (double) width/2; x += 0.1) {
            String string_x = String.valueOf(x);
            if (String.valueOf(x).contains("E")) {
                string_x = "0";
            }
            String expr = func.replaceAll("x", string_x);
//            System.out.println(expr);

            try {
                double y = Calculate.eval(expr); // if this throws, just return
                double trans_x = cvt_to_gridspace(x, true);
                double trans_y = cvt_to_gridspace(y, false);

                if (trans_x > width || trans_y > height) {
                    continue;
                }

                Node curr_func_node = new Node(null, trans_x, trans_y); //current node of the original function
                if (prev_func_node != null) {
                    prev_func_node.next = curr_func_node; //make the previous point towards the current
                } else {
                    func_head = curr_func_node; //saves the beginning of the list as the head so it can be returned
                }

                if (get_deriv) {
                    if (prev_func_node == null) { prev_func_node = curr_func_node; continue; }

                    double dy = trans_y - prev_func_node.y;
                    double dx = trans_x - prev_func_node.x;
                    double m = dy/dx;
                    Node curr_deriv_node = new Node(null, trans_x, (m * 20) + ((double) height/2)); // m needs to be stretched and shifted down to fit on the grid

                    if (prev_deriv_node != null) {
                        prev_deriv_node.next = curr_deriv_node;
                    } else {
                        deriv_head = curr_deriv_node;
                    }
                    prev_deriv_node = curr_deriv_node;
                }

                prev_func_node = curr_func_node;
            } catch (Error ignored) {
                return null;
            }
        }

        return new Node[] { func_head, deriv_head };
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
            Node[] heads = get_points_from(curr_func, show_derivative);
            func_heads[0] = heads[0];
            if (show_derivative && heads.length > 1) {
                func_heads[1] = heads[1];
            }
            repaint();
        }

        if (e.getSource() == clear_btn) {
            curr_func = "";
            func_field.setText("");
            slope_label.setText("Slope: ");

            func_heads = new Node[] { null, null, null, null, null };

            show_derivative = false;
            show_deriv_box.setSelected(false);

            show_tang_box.setSelected(false);
            curr_click = null;

            show_integral = false;
            area_label.setText("Area: ");

            lb_field.setText("Lower Bound: ");
            ub_field.setText("Lower Bound: ");
            repaint();
        }

        if (e.getSource() == approx_integral_btn) {
            integ_l_bound = Double.parseDouble(lb_field.getText().split(":")[1].trim());
            integ_u_bound = Double.parseDouble(ub_field.getText().split(":")[1].trim());

            show_integral = true;
            show_tangent = false;

            show_derivative = false;
            show_deriv_box.setSelected(false);
            func_heads[1] = null; //remove derivative

            show_tang_box.setSelected(false);
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

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == show_tang_box) {
            show_tangent = show_tang_box.isSelected();
            curr_func = func_field.getText();
            if (show_tangent) {
                show_integral = false;
                area_label.setText("Area: ");
            }

            slope_label.setText("Slope: ");
            repaint();
        }

        if (e.getSource() == rect_w_slider) {
            rect_width = rect_w_slider.getValue();
            if (rect_width == 0) rect_width = 0.1;
            repaint();
        }

        if (e.getSource() == show_deriv_box) {
            show_derivative = show_deriv_box.isSelected();
            if (show_derivative) {
                show_integral = false;
                area_label.setText("Area: ");

                curr_func = func_field.getText();
                Node[] heads = get_points_from(curr_func, show_derivative);
                func_heads[0] = heads[0];
                if (show_derivative && heads.length > 1) {
                    func_heads[1] = heads[1];
                }
            } else {
                func_heads[1] = null;
            }
            repaint();
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

}
