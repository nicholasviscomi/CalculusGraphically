package Graphing;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    public Dimension pref_size;
    private final JPanel parent;
    public Label(String text, Color bg, int font_size, JPanel parent) {
        setText(text);
        setOpaque(true);
        setBackground(bg);
        setFont(new Font(Font.SERIF, Font.PLAIN, font_size));
        setVisible(true);
        this.pref_size = getPreferredSize();
        this.parent = parent;
    }

    public void center_on_screen(int y) {
        System.out.println("parent.width = " + parent.getWidth());
        System.out.println("parent.height = " + parent.getHeight());
        setBounds(parent.getWidth()/2 - pref_size.width/2, y, pref_size.width, pref_size.height);
    }
}
