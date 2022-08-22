package Graphing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Section extends JPanel implements ActionListener {
    private final Color bg;
    private final int width, height;
    private boolean is_collapsed = false;
    public Section(Color bg, int x, int y, int width, int height, String title) {
        this.bg = bg;
        this.width = width;
        this.height = height;

        setLocation(x, y);
        setSize(width, height);
        setOpaque(true);
        setBackground(new Color(0xCBCBCB));
        setLayout(null);

        init_components(title);
        this.setVisible(true);
    }
    
    private void init_components(String title) {
        Label header = new Label(title, new Color(0xFFFFFF), 20, this);
        header.center_on_screen(10);
        this.add(header);

        Button collapse_btn = new Button("Collapse", new Color(0xFFFFFF), 10, this);
        collapse_btn.addActionListener(e -> {
            is_collapsed = !is_collapsed;
            revalidate();
        });
        collapse_btn.right_of(header, 5);
        this.add(collapse_btn);

        switch (title) {
            case "Differentiation":  //derivatives section
                Button btn = new Button("PRepajfdsj", new Color(67), 14, this);
                btn.bottom_of(header, 30);
                this.add(btn);
                break;
            case "Integration":  //integration section

                break;
            case "Transformations":  //transformations section

                break;
        }

        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(bg);
        g.fillRect(0, 0, width, height);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}