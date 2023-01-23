package Graphing.Panels;

import Graphing.Calculate;
import Graphing.GraphingGUI;
import Graphing.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Maclaurin_Section extends JPanel implements ActionListener {
    GraphingGUI parent;
    int width = 440, height = 120;
    JLabel header;
    JButton next_term_btn;

    public int m_index = 3; // start at -1 because we want the first term to be the 0th term
    public Maclaurin_Section(int x, int y, GraphingGUI parent) {
        this.parent = parent;
        setLocation(x, y);
        setSize(width, height);
        setLayout(null);
        Dimension d;

        header = new JLabel("Maclaurin Series");
        header.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        d = header.getPreferredSize();
        header.setBounds((width/2) - (d.width/2), header.getY() + 5, d.width, d.height);
        header.setVisible(true);

        next_term_btn = new JButton();
        next_term_btn.setText("Next Maclaurin Polynomial");
        next_term_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = next_term_btn.getPreferredSize();
        next_term_btn.setBounds(
                (width/2) - (d.width/2),
                (height/2) - (d.height/2),
                d.width, d.height
        );
        next_term_btn.addActionListener(this);
        next_term_btn.setVisible(true);

        add(header); add(next_term_btn);
        setVisible(true);
    }

    public double[] collapse_f_values(ArrayList<Double> f_values, double step_size) {
        ArrayList<ArrayList<Double>> all_y_vals = new ArrayList<>();
        ArrayList<Double> prev_values = f_values;
        ArrayList<Double> values = new ArrayList<>();
        // "collapse" the f_values to find the derivative values at 0
        for (int n = 0; n < 10; n++) {
            for (int i = 0; i < prev_values.size() - 1; i++) {
                double y1 = prev_values.get(i), y2 = prev_values.get(i + 1);
                double dy = y2 - y1; // dx = step_size
                values.add(dy / step_size);
            }
            all_y_vals.add(values);
            prev_values = values;
            values = new ArrayList<>();
        }

        //Get just the first element in each array of all_y_vals and return that
        double[] res = new double[11]; int i = 0;
        res[i++] = f_values.get(0);
        for (ArrayList<Double> row : all_y_vals) {
            res[i++] = row.get(0);
        }

        return res;
    }

    public String get_maclaurin_function(double step_size) {
        int n_vals = 11; // number of values to be found at this specific level

        //—————————————————————————————————————————————————————————————————————
        ArrayList<Double> f_values = new ArrayList<>();
        String raw_func = parent.curr_func;
        if (raw_func == null || raw_func.trim().equals("")) { return null; }
        String func = raw_func.split("=")[1].trim();
        for (double x = 0; f_values.size() < n_vals; x += step_size) {
            // get the first 11 values of f(x)
            String expr = func.replaceAll("x", String.valueOf(x));
            f_values.add(Calculate.eval(expr));
        }

        double[] collapsed = collapse_f_values(f_values, step_size); int i = 0;
        for (double val : collapsed) {
            System.out.println(i++ + " derivative = " + val);
        }

        // construct the equation for the maclaurin series
        StringBuilder mac_equation = new StringBuilder("y = ");
        for (i = 0; i < m_index; i++) {
            mac_equation.append(String.format("( %f * ( x ^ %d) / %d )", collapsed[i], i, factorial(i)));
            if ((i + 1) < m_index) {
                mac_equation.append(" + ");
            }
        }
        System.out.println(mac_equation);
        //—————————————————————————————————————————————————————————————————————
//        Node prev_func_node = null;
//        Node func_head = null;
//        for (double x = (double) -parent.width/2; x < (double) parent.width/2; x += 0.1) {
//            String string_x = String.valueOf(x);
//            if (String.valueOf(x).contains("E")) {
//                string_x = "0";
//            }
//            String expr = mac_equation.toString().replaceAll("x", string_x);
//
//            try {
//                double y = Calculate.eval(expr); // if this throws, just return
////                System.out.println(expr + " = " + y);
//                double trans_x = parent.cvt_to_gridspace(x, true);
//                double trans_y = parent.cvt_to_gridspace(y, false);
//
//                if (trans_x > width || trans_y > height) {
//                    continue;
//                }
//
//                Node curr_func_node = new Node(null, trans_x, trans_y); //current node of the original function
//                if (prev_func_node != null) {
//                    prev_func_node.next = curr_func_node; //make the previous point towards the current
//                } else {
//                    func_head = curr_func_node; //saves the beginning of the list as the head so it can be returned
//                }
//                prev_func_node = curr_func_node;
//            } catch (Error ignored) {
//                return null;
//            }
//        }
        return mac_equation.toString();
    }

    private int factorial(int n) {
        if (n == 0) return 1;

        return n * factorial(n - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == next_term_btn) {
            m_index += 1;

            String mac_func = get_maclaurin_function(0.01);
            Node head = parent.get_points_from(mac_func, false, 0.01)[0];
            for (int i = 2; i < parent.func_heads.length; i++) {
                if (parent.func_heads[i] == null) {
                    System.out.println("func_heads[" + i + "] = maclaurin");
                    System.out.println("(" + head.x + ", " + head.y + ")");
                    parent.func_heads[i] = head;
                    break;
                }
            }
            parent.repaint();
        }
    }
}
