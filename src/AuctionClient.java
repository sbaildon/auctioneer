import java.rmi.Naming;
import java.util.ArrayList;

public class AuctionClient {
    public static final AuctionClient client = new AuctionClient();
    private static Auction a;
    private static GUI gui;
    private User currentUser;

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

    public void newUser(String name, String email) {
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

    public void addItem(String name, int startPrice, int reservePrice) {
        if (startPrice > reservePrice) {
            gui.sendMessage("Prices are wrong" );
            return;
        }

        Item item;
        item = new Item(currentUser, name, startPrice, reservePrice);
        try {
            if(a.addItem(item)) {
                gui.sendMessage("Auction created");
            } else {
                gui.sendMessage("Failed to create auction");
            }
        } catch (Exception e) {
            gui.sendMessage("Failed to create auction (serious)");
        }
    }

    public void login(String name, String email) {
        User user;
        user = new User(name, email);
        try {
            if (a.login(user)) {
                gui.proceedToAuction();
                currentUser = new User(name, email);
            } else {
                gui.sendMessage("Account doesn't exist");
            }
        } catch (Exception e) {
            gui.sendMessage("Failed to login (serious)");
        }
    }

    

}
