import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI implements ActionListener {
    private Panel panel;
    private TextField username, email;
    private Button loginBtn, regisBtn;
    private Label intro, info;
    private Frame frame;
    private AuctionClient client;

    public GUI() {
        client = client.getInstance();
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
        intro = new Label("Login");
        intro.setFont(new Font("Sans Serif", Font.PLAIN, 38));
        intro.setBounds(((width / 2) - 52), 30, 105, 400);

        username = new TextField(15);
        username.setBounds(((width / 2) - 65), ((height / 2) - 40), 130, 25);
        username.setText("username");

        email = new TextField(15);
        email.setBounds(((width / 2) - 65), (height / 2), 130, 25);
        email.setText("email");

        loginBtn = new Button("Login");
        loginBtn.setBounds(((width / 2) - 77), (height - (height / 3)), 75, 25);
        loginBtn.addActionListener(this);

        regisBtn = new Button("Register");
        regisBtn.setBounds(((width / 2) + 7), (height - (height / 3)), 75, 25);
        regisBtn.addActionListener(this);

        info = new Label();
        info.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        info.setBounds(((width / 2) - 52), 30, 300, 104);

        panel = new Panel();
        panel.setLayout(null);

        panel.add(loginBtn);
        panel.add(regisBtn);
        panel.add(username);
        panel.add(email);
        panel.add(intro);
        panel.add(info);

        return panel;
    }

    public void provideFeedback(String message) {
        info.setText(message);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        info.setText(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            client.login(username.getText(), email.getText());
        } else if (e.getSource() == regisBtn) {
            client.addUser(username.getText(), email.getText());
        }
    }
}
