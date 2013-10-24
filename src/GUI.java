import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI {
    Frame frame;

    public GUI() {
        this.frame = windowFrame("Auction House", 480, 300);
        this.frame.add(loginPanel(480, 300));
        this.frame.setVisible(true);
    }

    private Frame windowFrame(String title, int width, int height){
        Frame frame = new Frame(title);
        frame.setResizable(false);
        frame.setSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        return frame;
    }

    private Panel loginPanel(int width, int height) {
        Panel panel;
        TextField username, email;
        Button loginBtn;
        Label intro;

        intro = new Label("Login");
        intro.setFont(new Font("Sans Serif", Font.PLAIN, 38));
        intro.setBounds(((width / 2) - 52), 30, 105, 400);

        username = new TextField(15);
        username.setBounds(((width / 2) - 65), ((height / 2) - 40), 130, 25);

        email = new TextField(15);
        email.setBounds(((width / 2) - 65), (height / 2), 130, 25);

        loginBtn = new Button("login");
        loginBtn.setBounds(((width / 2 ) - 37), (height - (height / 3)), 75, 25);

        panel = new Panel();
        panel.setLayout(null);

        panel.add(loginBtn);
        panel.add(username);
        panel.add(email);
        panel.add(intro);

        panel.setVisible(true);
        return panel;
    }
}
