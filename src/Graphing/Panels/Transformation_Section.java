package Graphing.Panels;

import Graphing.GraphingGUI;

import javax.swing.*;
import java.awt.*;

public class Transformation_Section extends JPanel {
    private GraphingGUI parent;
    private JCheckBox squared, reciprocal, abs_y, abs_x;
    private JLabel header;
    private final int width = 440, height = 120;
    public Transformation_Section(int x, int y, GraphingGUI parent) {
        this.parent = parent;
        setLocation(x, y);
        setSize(width, height);
        setLayout(null);
        Dimension d;

        header = new JLabel("Advanced Transformations");
        header.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        d = header.getPreferredSize();
        header.setBounds((width/2) - (d.width/2), header.getY() + 5, d.width, d.height);
        header.setVisible(true);

        add(header);
        setVisible(true);
    }
}
