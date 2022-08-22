package Graphing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Button extends JButton {

    public Dimension pref_size;
    private final JPanel parent;
    public Button(String text, Color bg, int font_size, JPanel parent) {
        setText(text);
        setOpaque(true);
        setBackground(bg);
        setFont(new Font(Font.SERIF, Font.PLAIN, font_size));
        setVisible(true);
        this.pref_size = getPreferredSize();
        this.parent = parent;
    }

    public void center_on_screen(int y) {
        setBounds(parent.getWidth()/2 - pref_size.width/2, y, pref_size.width, pref_size.height);
    }

    public void left_of(JComponent comp, int padding) {
        setBounds(
                comp.getX() - this.getWidth() - padding, // left edge minus padding
                comp.getY() + (comp.getHeight()/2) - (this.getHeight()/2), // center vertically
                pref_size.width, pref_size.height
        );
    }

    public void right_of(JComponent comp, int padding) {
        System.out.println(comp.getBounds());
        setBounds(
                comp.getX() + this.getWidth() + padding, //right edge plus padding
                comp.getY() + (comp.getHeight()/2) - (this.getHeight()/2), //center vertically
                pref_size.width, pref_size.height
        );
    }

    public void top_of(JComponent comp, int padding) {
        setBounds(
                comp.getX() + (comp.getWidth()/2) - (this.getWidth()/2),
                comp.getY() - this.getHeight() - padding,
                pref_size.width, pref_size.height
        );
    }

    public void bottom_of(JComponent comp, int padding) {
        setBounds(
                comp.getX() + (comp.getWidth()/2) - (this.getWidth()/2),
                comp.getY() + comp.getHeight() + padding,
                pref_size.width, pref_size.height
        );
    }
}
