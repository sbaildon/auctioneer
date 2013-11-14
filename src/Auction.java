import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface Auction extends Remote {

    public int bid(int itemID, int bidAmount, User user)
            throws RemoteException;

    public boolean addItem(Item item)
            throws RemoteException;

    public int closeAuction(int id, User user)
            throws RemoteException;

    public boolean addUser(User user)
            throws RemoteException;

    public boolean login(User user)
            throws RemoteException;

    public HashMap getAvailableAuctions()
            throws RemoteException;

    public ArrayList getSoldAuctions(User user)
            throws RemoteException;
}
