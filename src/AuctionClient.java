import java.rmi.Naming;

public class AuctionClient {

    public static void main(String args[]) {
        try {
            Auction a = (Auction) Naming.lookup("rmi://localhost:2020/AuctioneerService");
        } catch (Exception e) {
            System.out.println("Failed to find RMI");
        }
    }
}