import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI {
    Frame frame;

    public GUI() {
        this.frame = createWindow("Auction House", 600, 500);
    }

    public Frame createWindow(String title, int width, int height){
        Frame frame = new Frame(title);
        frame.setMinimumSize(new Dimension(width, height));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
        return frame;
    }
}
