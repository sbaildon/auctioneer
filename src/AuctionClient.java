import java.awt.List;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.rmi.Naming;
import java.util.Map;
import javax.crypto.*;
import javax.crypto.spec.*;

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

    public void login(String name, String email) {
        User user;
        user = new User(name, email);
        try {
            if (a.login(user)) {
                currentUser = user;
                gui.proceedToAuction();
            } else {
                gui.sendMessage("Account doesn't exist");
            }
        } catch (Exception e) {
            gui.sendMessage("Failed to login (serious)");
        }
    }

    public void addItem(String name, double startPrice, double reservePrice) {
        if (startPrice > reservePrice) {
            gui.sendMessage("Prices are wrong" );
            return;
        }

        Item item;
        item = new Item(currentUser, name, startPrice, reservePrice);
        try {
            a.addItem(item);
            gui.sendMessage("Auction created");
        } catch (Exception e) {
            gui.sendMessage("(serious) Failed to create auction (serious)");
        }
    }

    public void bid(int id, double amount) {
        int result;
        try {
            result = a.bid(id, amount, currentUser);
        } catch (Exception e) {
            gui.sendMessage("Bid failed (serious)");
            return;
        }

        switch (result) {
            case 3: gui.sendMessage("No auction exists");
                    break;
            case 2: gui.sendMessage("You can't bid on your own auctions!");
                    break;
            case 1: gui.sendMessage("Your bid was too small");
                    break;
            case 0: gui.sendMessage("Bid successful");
                    break;
            default:gui.sendMessage("What?");
                    break;
        }
    }

    /*
     * Set the 'won' boolean to true to check for only auctions
     * the current user has won
     */
    public void populateList(List list, boolean won) {
        HashMap<Integer, Item> items = new HashMap<Integer, Item>();

        if (won) {
            try {
                items = a.getSoldAuctions(currentUser);
            } catch (Exception e) {System.out.println("Failed here");}
        } else {
            try {
                items = a.getAvailableAuctions();
            } catch (Exception e) {System.out.println("Failed");}
        }

        for (Map.Entry<Integer, Item> e : items.entrySet()) {
            list.add(e.getValue().getName() + " (£" + e.getValue().getPrice() + ") [" + e.getKey() + "]");
        }

    }

    public void closeAuction(int id) {
        int response;
        try {
            response = a.closeAuction(id, currentUser);
            switch (response) {
                case 3: gui.sendMessage("That auction doesn't exist");
                        break;
                case 2: gui.sendMessage("Not your auction");
                        break;
                case 1: gui.sendMessage("Item closed, but didn't meet reserve");
                        break;
                case 0: gui.sendMessage("Item closed, " + a.getAuctionWinner(id) + " won!");
                        break;
                default:gui.sendMessage("What?");
                        break;
            }
        } catch (Exception e) {
            gui.sendMessage("Couldn't close auction (serious)");
        }

    }

}
