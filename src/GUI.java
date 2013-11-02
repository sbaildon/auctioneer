import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI implements ActionListener {
    private final int LOGIN_WIDTH = 480;
    private final int LOGIN_HEIGHT = 300;
    private final int AUCT_WIDTH = 600;
    private final int AUCT_HEIGHT = 600;

    private Label msg;
    private Frame mainAuctionWindow;

    private AuctionClient client;

    public GUI() {
        client = client.getInstance();
        mainAuctionWindow = loginWindow();
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
        Label intro;
        Button loginBtn, regisBtn;
        final TextField username, email;

        intro = new Label("Hello");
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
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.login(username.getText(), email.getText());
            }
        });

        regisBtn = new Button("Register");
        regisBtn.setBounds(((width / 2) + 7), (height - (height / 3)), 75, 25);
        regisBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.newUser(username.getText(), email.getText());
            }
        });

        msg = new Label("");
        msg.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        msg.setBounds(((width / 2) - 52), (height - 60), 300, 104);

        panel = new Panel();
        panel.setLayout(null);

        panel.add(loginBtn);
        panel.add(regisBtn);
        panel.add(username);
        panel.add(email);
        panel.add(intro);
        panel.add(msg);

        return panel;
    }

    private Frame loginWindow() {
        Frame frame;
        Panel panel;

        frame = windowFrame("Login", LOGIN_WIDTH, LOGIN_HEIGHT);
        panel = loginPanel(LOGIN_WIDTH, LOGIN_HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        return frame;
    }

    public void createNewAuctionDialog() {
        final Button ok;
        final TextField name, reservePrice, startingPrice;
        final Dialog newAuctionDialog;

        name = new TextField(15);
        name.setText("Item name");

        reservePrice = new TextField(6);
        reservePrice.setText("Reserve");

        startingPrice = new TextField(6);
        startingPrice.setText("Starting");

        ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    client.addItem(name.getText(),
                                Integer.parseInt(startingPrice.getText()),
                                Integer.parseInt(reservePrice.getText()));
                    ok.getParent().setVisible(false);
                } catch (Exception e) {
                    sendMessage("Need integers");
                }
            }
        });

        newAuctionDialog = new Dialog(mainAuctionWindow, "New Auction", true);

        newAuctionDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                newAuctionDialog.setVisible(false);
            }
        });

        newAuctionDialog.setSize(new Dimension(170, 120));
        newAuctionDialog.setLayout(new FlowLayout());
        newAuctionDialog.setLocationRelativeTo(null);

        newAuctionDialog.add(name);
        newAuctionDialog.add(reservePrice);
        newAuctionDialog.add(startingPrice);
        newAuctionDialog.add(ok);

        newAuctionDialog.setVisible(true);
    }

    private Panel auctionPanel(int width, int height) {
        Panel panel, auctions;

        panel = new Panel();
        panel.setLayout(new BorderLayout());

        auctions = new Panel();

        panel.add(auctions, BorderLayout.WEST);
        panel.add(detailsPanel(), BorderLayout.CENTER);
        panel.add(statusPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private Panel detailsPanel() {
        Panel panel;
        Button createBtn;

        panel = new Panel();

        createBtn = new Button("Create");
        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createNewAuctionDialog();
            }
        });

        panel.add(createBtn);

        return panel;
    }

    private Panel auctionListPanel() {
        Panel panel;

        panel = new Panel();

        return panel;
    }

    private Panel statusPanel() {
        Panel panel;

        msg.setText("Ready");
        msg.setAlignment(Label.LEFT);

        panel = new Panel();
        panel.add(msg);

        return panel;
    }

    private Frame auctionWindow() {
        Frame frame;
        Panel panel;

        frame = windowFrame("Auction House", AUCT_WIDTH, AUCT_HEIGHT);
        panel = auctionPanel(AUCT_WIDTH, AUCT_HEIGHT);

        frame.add(panel);
        frame.setVisible(true);

        return frame;
    }

    public void proceedToAuction() {
        mainAuctionWindow.setVisible(false);
        mainAuctionWindow = null;
        mainAuctionWindow = auctionWindow();
    }

    public void sendMessage(String message) {
        String oldMessage = msg.getText();
        msg.setText(message);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        msg.setText(oldMessage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
