import Graphing.GraphingGUI;

import javax.swing.*;
import java.awt.*;

public class Frame extends JPanel {
    //need hard coded values for sake of doing math easily
    public int width = 1400;
    public int height = 828; // height of window bar is 28. Want the content pane to be 800

    public JFrame frame;
    GraphingGUI graphing_gui;
    public Frame() {
        setLayout(null);
        setFocusable(true);
        requestFocus();

        frame = new JFrame();
        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Calculus Graphically");
        frame.setLocationRelativeTo(null);

        graphing_gui = new GraphingGUI(width, height - 28);
        frame.setContentPane(graphing_gui.get_frame().getContentPane());

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public static void main(String[] args) {
        new Frame();
    }
}
