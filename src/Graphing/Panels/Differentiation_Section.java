package Graphing.Panels;

import Graphing.GraphingGUI;
import Graphing.Node;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class Differentiation_Section extends JPanel implements ChangeListener {
    GraphingGUI parent;
    int width = 440, height = 120;
    JLabel header;
    JCheckBox show_tang_box, show_deriv_box, limdef_box;
    JSlider limdef_dist; //distance between the points in the limit definition animation
    public JLabel slope;
    public Differentiation_Section(int x, int y, GraphingGUI parent) {
        this.parent = parent;
        setLocation(x, y);
        setSize(width, height);
        setLayout(null);
        Dimension d;

        header = new JLabel("Differentiation");
        header.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        d = header.getPreferredSize();
        header.setBounds((width/2) - (d.width/2), header.getY() + 5, d.width, d.height);
        header.setVisible(true);

        slope = new JLabel("Slope: ");
        slope.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = slope.getPreferredSize();
        slope.setBounds(
                width - d.width - 50, header.getY() + (header.getHeight()/2) - (d.height/2),
                d.width + 30, d.height
        );
        slope.setVisible(true);

        show_tang_box = new JCheckBox("Show Tangent");
        show_tang_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = show_tang_box.getPreferredSize();
        show_tang_box.setBounds(
                10, header.getY() + header.getHeight() + 5,
                d.width, d.height
        );
        show_tang_box.addChangeListener(this);
        show_tang_box.setVisible(true);

        show_deriv_box = new JCheckBox("Show Derivative");
        show_deriv_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = show_deriv_box.getPreferredSize();
        show_deriv_box.setBounds(
                show_tang_box.getX() + show_tang_box.getWidth() + 5,
                show_tang_box.getY(), d.width, show_tang_box.getHeight()
        );
        show_deriv_box.addChangeListener(this);
        show_deriv_box.setVisible(true);

        limdef_box = new JCheckBox("Limit Definition of Derivative");
        limdef_box.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        d = limdef_box.getPreferredSize();
        limdef_box.setBounds(
                show_tang_box.getX(), show_tang_box.getY() + show_tang_box.getHeight() + 5,
                d.width, show_tang_box.getHeight()
        );
        limdef_box.addChangeListener(this);
        limdef_box.setVisible(true);

        limdef_dist = new JSlider(JSlider.HORIZONTAL, 0, 5, 2);
        limdef_dist.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        limdef_dist.setMajorTickSpacing(1);
        limdef_dist.setPaintLabels(true);
        limdef_dist.setPaintTicks(true);
        d = limdef_dist.getPreferredSize();
        limdef_dist.setBounds(
                width - d.width + 45, height - d.height + 10,
                d.width - 60, d.height - 10
        );
        limdef_dist.addChangeListener(this);
        limdef_dist.setVisible(false);

        add(header); add(slope); add(show_tang_box); add(show_deriv_box); add(limdef_box);
        add(limdef_dist);
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (limdef_box.isSelected()) {
            Image lim_def_img = Toolkit.getDefaultToolkit().getImage("src/Assets/limit_def_deriv.png");
            int w = lim_def_img.getWidth(this)/7, h = lim_def_img.getHeight(this)/7;
//        g.setColor(new Color(0xFFFFFF));
//        g.fillRect(width - w, 10, lim_def_img.getWidth(this)/7, lim_def_img.getHeight(this)/7);
            g.drawImage(
                    lim_def_img,
                    width - w, 10,
                    w, h,
                    this
            );

        }


    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == limdef_box) {
            boolean val = ((JCheckBox) e.getSource()).isSelected();
            limdef_dist.setVisible(val);
            repaint();
            revalidate();

            parent.show_limdef = val;
            parent.repaint();
        } else if (e.getSource() == show_tang_box) {
            parent.show_tangent = ((JCheckBox) e.getSource()).isSelected();
            parent.repaint();
        } else if (e.getSource() == show_deriv_box) {
            parent.show_derivative = ((JCheckBox) e.getSource()).isSelected();
            if (parent.show_derivative) {
                parent.show_integral = false;

                parent.func_heads = parent.get_points_from(parent.func_field.getText(), parent.show_derivative);
            } else {
                parent.func_heads[1] = null;
            }

            parent.repaint();
        }
    }
}
