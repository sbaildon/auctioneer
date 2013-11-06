import java.awt.*;
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
            gui.sendMessage("(serious) Failed to create auction (serious)");
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

    /* Boolean: true for items user has won, false
     * for all auctions available
     */
    public void fillItems(List list, boolean won) {
        ArrayList<Item> auctions;
        try {
            if (won) {
                auctions = a.getWonAuctions(currentUser);
            } else {
                auctions = a.getAvailableAuctions();
            }
        } catch (Exception e) {
            gui.sendMessage("Failed to get auctions");
            return;
        }

        int i;
        for (i = 0; i < auctions.size(); i++) {
            list.add(auctions.get(i).name + " (" + auctions.get(i).currentPrice + ") [" + auctions.get(i).ID + "]");
        }
    }

    public void bid(int id, int amount) {
        int result;
        try {
            result = a.bid(id, amount);
        } catch (Exception e) {
            return;
        }

        if (result == 0) {
            gui.sendMessage("Bid successful");
        } else {
            gui.sendMessage("Failed");
        }
    }

    public void closeAuction(int id) {
        int response;
        try {
            response = a.closeAuction(id, currentUser);
            switch (response) {
                case 0: gui.sendMessage("Item is not your own/doesn't exist");
                        break;
                case 1: gui.sendMessage("Item closed, but didn't meet reserve");
                        break;
                case 2: gui.sendMessage("Item closed, someone won!");
                        break;
                default:gui.sendMessage("What?");
                        break;
            }
        } catch (Exception e) {
            gui.sendMessage("Couldn't close auction (serious)");
        }

    }

    

}
