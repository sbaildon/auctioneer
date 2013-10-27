import java.rmi.Naming;
import java.util.ArrayList;

public class AuctionClient {
    public static final AuctionClient client = new AuctionClient();
    private static Auction a;
    private static GUI gui;

    public static void main(String args[]) {
        try {
            a = (Auction) Naming.lookup("rmi://localhost:2020/AuctioneerService");
        } catch (Exception e) {
            System.out.println("Failed to find RMI");
            e.printStackTrace();
            return;
        }
        gui = new GUI();
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
                gui.sendMessage("Account created");
            } else {
                gui.sendMessage("Email already in use");
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
                gui.proceedToAuction();
            } else {
                gui.sendMessage("Account doesn't exist");
            }
        } catch (Exception e) {
            gui.sendMessage("Failed to login");
        }
    }

    

}
