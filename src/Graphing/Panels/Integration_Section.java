package Graphing.Panels;

import Graphing.GraphingGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Integration_Section extends JPanel implements ActionListener, ChangeListener {
    private JLabel header,
            lb_label, ub_label, //label that says "lower bound" or "upper bound"
            rw_label // "rectangle width"
    ;
    public JSpinner lower_bound, upper_bound;
    private JSlider rect_w_slider;
    private JButton approx_integral_btn;
    int width = 440, height = 120;
    GraphingGUI parent;
    public Integration_Section(int x, int y, GraphingGUI parent) {
        this.parent = parent;
        setLocation(x, y);
        setSize(width, height);
        setLayout(null);
        Dimension d;

        header = new JLabel("Integration");
        header.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        d = header.getPreferredSize();
        header.setBounds((width/2) - (d.width/2), header.getY() + 5, d.width, d.height);
        header.setVisible(true);

        lb_label = new JLabel("Lower Bound:");
        lb_label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = lb_label.getPreferredSize();
        lb_label.setBounds(10, header.getY() + header.getHeight() + 5, d.width, d.height);
        lb_label.setVisible(true);

        SpinnerModel lb_model = new SpinnerNumberModel(0, -36, 36, 0.01);
        lower_bound = new JSpinner(lb_model);
        lower_bound.setBounds(
                lb_label.getX() + lb_label.getWidth() + 5, lb_label.getY(),
                70, d.height
        );

        ub_label = new JLabel("Upper Bound:");
        ub_label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = ub_label.getPreferredSize();
        ub_label.setBounds(
                lower_bound.getX() + lower_bound.getWidth() + 5,
                lb_label.getY(), d.width, lb_label.getHeight()
        );
        ub_label.setVisible(true);

        SpinnerModel ub_model = new SpinnerNumberModel(0, -36, 36, 0.01);
        upper_bound = new JSpinner(ub_model);
        upper_bound.setBounds(
                ub_label.getX() + ub_label.getWidth() + 5, ub_label.getY(),
                70, ub_label.getHeight()
        );

        rect_w_slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
        rect_w_slider.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
        rect_w_slider.setMinorTickSpacing(1);
        rect_w_slider.setMajorTickSpacing(5);
        rect_w_slider.setPaintLabels(true);
        rect_w_slider.setPaintTicks(true);
        d = rect_w_slider.getPreferredSize();
        rect_w_slider.setBounds(
                lb_label.getX(), lb_label.getY() + lb_label.getHeight() + 20,
                d.width, d.height
        );
        rect_w_slider.addChangeListener(this);
        rect_w_slider.setVisible(true);

        rw_label = new JLabel("Rectangle Width");
        rw_label.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
        d = rw_label.getPreferredSize();
        rw_label.setBounds(
                rect_w_slider.getX() + (rect_w_slider.getWidth()/2) - (d.width/2),
                rect_w_slider.getY() - 10,
                d.width, d.height
        );
        rw_label.setVisible(true);

        approx_integral_btn = new JButton();
        approx_integral_btn.setText("Approximate Integral");
        approx_integral_btn.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = approx_integral_btn.getPreferredSize();
        approx_integral_btn.setBounds(
                rect_w_slider.getX() + rect_w_slider.getWidth() + 5,
                rect_w_slider.getY() + (rect_w_slider.getHeight()/2) - (d.height/2),
                d.width, d.height
        );
        approx_integral_btn.addActionListener(this);
        approx_integral_btn.setVisible(true);

        add(header); add(lb_label); add(ub_label); add(upper_bound); add(lower_bound);
        add(rect_w_slider); add(approx_integral_btn); add(rw_label);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approx_integral_btn) {
            parent.integ_u_bound = ((double) upper_bound.getValue());
            parent.integ_l_bound = ((double) lower_bound.getValue());

            parent.show_integral = true;
            parent.show_tangent = false;

//            parent.show_derivative = false;
//            parent.d_section.show_deriv_box.setSelected(false);
//            parent.func_heads[1] = null; //remove derivative

            parent.d_section.show_tang_box.setSelected(false);
            parent.curr_click = null; //ensures the limit definition of derivative box doesn't trigger
            parent.repaint();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == rect_w_slider) {
            parent.rect_width = rect_w_slider.getValue();
            if (parent.rect_width == 0) parent.rect_width = 0.1;
        }
        parent.repaint();
    }
}
