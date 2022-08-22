import Graphing.GraphingGUI;
import Graphing.Panels.Integration_Section;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Frame extends JPanel implements MouseListener, MouseMotionListener {
    //need hard coded values for sake of doing math easily
    public int width = 1400;
    public int height = 828; // height of window bar is 28. Want the content pane to be 800

    public JFrame frame;
    GraphingGUI graphing_gui = null;
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
//        frame.setContentPane(graphing_gui.get_frame().getContentPane());

        Integration_Section is = new Integration_Section(graphing_gui, graphing_gui);
        is.setLocation(100, 100);
        frame.add(is);

        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, width, height);
    }

    public static void main(String[] args) {
        new Frame();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (graphing_gui == null)  return;

        graphing_gui.curr_click = e.getPoint();
        graphing_gui.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (graphing_gui == null)  return;

        graphing_gui.mouse_on_screen = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (graphing_gui == null)  return;

        graphing_gui.mouse_on_screen = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (graphing_gui == null)  return;

        graphing_gui.curr_mouse = e.getPoint();
        if (graphing_gui.show_tangent) {
            graphing_gui.repaint();
        }
    }
}
