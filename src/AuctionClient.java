import java.rmi.Naming;

public class AuctionClient {
    public static final AuctionClient client = new AuctionClient();
    private static Auction a;
    private static GUI gui;

    public static void main(String args[]) {
        try {
            a = (Auction) Naming.lookup("rmi://localhost:2020/AuctioneerService");
            gui = new GUI();
        } catch (Exception e) {
            System.out.println("Failed to find RMI");
            e.printStackTrace();
        }
    }

    private AuctionClient() {
    }

    public static AuctionClient getInstance() {
        return client;
    }

    public void addUser(String name, String email) {
        User user;
        user = new User(name, email);
        try {
            if (a.addUser(user)) {
                gui.provideFeedback("Account created");
            } else {
                gui.provideFeedback("Email already in use");
            }
        } catch (Exception e) {
            System.out.println("Failed to create user (serious)");
        }
    }

    public void login(String name, String email) {
        User user;
        user = new User(name, email);
        try {
            if (a.login(user)) {
            }
        } catch (Exception e) {
            gui.provideFeedback("Failed to login");
        }
    }

}
