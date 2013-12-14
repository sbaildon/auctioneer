import java.awt.List;
import java.io.*;
import java.util.HashMap;
import java.rmi.Naming;
import java.util.Map;
import java.util.Random;
import javax.crypto.*;

public class AuctionClient {
    public static final AuctionClient client = new AuctionClient();
    private Auction a;
    private static GUI gui;
    private User currentUser;


    private final static String SERVER = "rmi://localhost:2020";

    public static void main(String args[]) {
        gui = new GUI();
    }

    private AuctionClient() {
        String node = findRandomNode();
        System.out.println("Connecting to " + node + "...\n");
        connectToNode(node);
    }

    private String[] findAllNodes() {
        String[] nodes;

        try {
            nodes = Naming.list(SERVER);
        } catch (Exception e) {
            nodes = new String[] {};
            System.out.println("Failed finding nodes");
        }

        return nodes;
    }

    public void listNodes() {
        String[] allNodes = findAllNodes();

        if (allNodes.length == 0) {
            System.out.println("No nodes to list");
            return;
        }

        System.out.println("Found these nodes");
        for (String node : allNodes) {
            System.out.println(node);
        }
        System.out.println();
    }

    private String findRandomNode() {
        String[] allNodes = findAllNodes();

        if (allNodes.length == 0)
            return "none";

        int nodeAtIndex;
        Random rand = new Random();
        nodeAtIndex = rand.nextInt(allNodes.length);

        return  allNodes[nodeAtIndex];
    }

    private void connectToNode(String node) {
        try {
            a = (Auction) Naming.lookup("rmi:" + node);
        } catch (Exception e) {
            System.out.println("Failed to find RMI");
            return;
        }
    }

    private boolean reconnect() {
        String node = findRandomNode();

        if (node.equals("none")) {
            System.out.println("No available nodes");
            return false;
        }

        System.out.println("Connecting to node " + node + "...");
        try {
            a = (Auction) Naming.lookup("rmi:" + node);
            System.out.println("Connected.\n");
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't connect");
        }

        return false;
    }

    private boolean multipleRetry() {
        int i = 0;
        boolean finished;

        System.out.println("Trying to reconnect\n");

        do {
            finished = reconnect();
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
        } while (i < 5 && !finished);

        return finished;
    }

    public static AuctionClient getInstance() {
        return client;
    }

    public void newUser(String email, String password) {
        User user;
        user = new User(email, password);
        try {
            SecretKey skey = a.addUser(user);
            if (skey != null) {
                gui.sendMessage("Account created");
                writeKey(user.getEmail(), skey);
            } else {
                gui.sendMessage("Email already in use");
            }
        } catch (Exception e) {
            if (multipleRetry()) {
                try {
                    newUser(email, password);
                    return;
                } catch (Exception ex) {}
            }
            System.out.println("Failed to create user (serious)");
        }
    }

    public void login(String email, String password) {
        User user = new User(email, password);
        SecretKey skey = getKey(email);
        SealedObject sealedUser;

        if (skey == null) {
            gui.sendMessage("Couldn't find the correct authentication");
            return;
        } else {
            sealedUser = seal(user, skey);
        }

        try {
            if (a.login(user.getEmail(), sealedUser)) {
                currentUser = user;
                gui.proceedToAuction();
            } else {
                gui.sendMessage("Account doesn't exist");
            }
        } catch (Exception e) {
            if (multipleRetry()) {
                try {
                    login(email, password);
                    return;
                } catch (Exception ex) {}
            }
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

        SecretKey skey = getKey(currentUser.getEmail());
        SealedObject sealedItem;

        if (skey == null) {
            gui.sendMessage("Couldn't find the correct authentication");
            return;
        } else {
            sealedItem = seal(item, skey);
        }

        int i;
        for (i = 0; i < 1; i++) {
            try {
                a.addItem(currentUser.getEmail(), sealedItem);
                gui.sendMessage("Auction created");
                return;
            } catch (Exception e) {
                multipleRetry();
                gui.sendMessage("Failed to create auction (serious)");
            }
        }

        listNodes();
    }

    public void bid(int id, double amount) {
        int result;
        SealedObject sealedBidItem;
        SecretKey skey = getKey(currentUser.getEmail());
        BidItem bidItem = new BidItem(id, amount, currentUser);

        if (skey == null) {
            gui.sendMessage("Couldn't find the correct authentication");
            return;
        } else {
            sealedBidItem = seal(bidItem, skey);
        }


        try {
            result = a.bid(currentUser.getEmail(), sealedBidItem);
        } catch (Exception e) {
            if (multipleRetry()) {
                try {
                    bid(id, amount);
                    return;
                } catch (Exception ex) {}
            }
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
            SealedObject sealedUser;
            SecretKey skey = getKey(currentUser.getEmail());
            sealedUser = seal(currentUser, skey);
            try {
                items = a.getSoldAuctions(currentUser.getEmail(), sealedUser);
            } catch (Exception e) {
                if (multipleRetry()) {
                    try {
                        populateList(list, won);
                        return;
                    } catch (Exception ex) {}
                }
                System.out.println("Failed to get your won auctions");
            }
        } else {
            try {
                items = a.getAvailableAuctions();
            } catch (Exception e) {
                if (multipleRetry()) {
                    try {
                        populateList(list, won);
                        return;
                    } catch (Exception ex) {}
                }
                System.out.println("Failed to get available auctions");
            }
        }

        for (Map.Entry<Integer, Item> e : items.entrySet()) {
            list.add(e.getValue().getName() + " (£" + e.getValue().getPrice() + ") [" + e.getKey() + "]");
        }

    }

    public void closeAuction(int id) {
        int response;
        SecretKey skey = getKey(currentUser.getEmail());
        SealedObject sealedUser;

        sealedUser = seal(currentUser, skey);
        try {
            response = a.closeAuction(id, currentUser.getEmail(), sealedUser);
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
            if (multipleRetry()) {
                try {
                    closeAuction(id);
                    return;
                } catch (Exception ex) {}
            }
            gui.sendMessage("Couldn't close auction (serious)");
        }

    }

    private SealedObject seal(User obj, SecretKey skey) {
        SealedObject sealed;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            sealed = new SealedObject(obj, cipher);
            return sealed;
        } catch (Exception e) {
            gui.sendMessage("Failed to seal user");
        }

        return null;
    }

    private SealedObject seal(BidItem obj, SecretKey skey) {
        SealedObject sealed;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            sealed = new SealedObject(obj, cipher);
            return sealed;
        } catch (Exception e) {
            gui.sendMessage("Failed to seal user");
        }

        return null;
    }

    private SealedObject seal(Item obj, SecretKey skey) {
        SealedObject sealed;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            sealed = new SealedObject(obj, cipher);
            return sealed;
        } catch (Exception e) {
            gui.sendMessage("Failed to seal item");
        }
        return null;
    }

    private SecretKey getKey(String fileName) {
        try {
            FileInputStream fis = new FileInputStream("keys/" + fileName + ".key");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SecretKey obj = (SecretKey) ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.out.println("Failed reading key\n\n" + e);
        }
        return null;
    }

    private boolean writeKey(String fileName, SecretKey skey) {
        File file;
        OutputStream stream;
        ObjectOutputStream objStream;
        file = new File("keys/" + fileName + ".key");
        try {
            stream = new FileOutputStream(file);
            objStream = new ObjectOutputStream(stream);

            objStream.writeObject(skey);

            stream.close();
            objStream.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
