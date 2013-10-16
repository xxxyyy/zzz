package fxtg.Server;

import fxtg.FxtgPriceAndAmount;
import fxtg.FxtgRecord;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * 远程调用中提供给client的server接口类。
 *
 * @author xiaodai
 */
public interface FxtgServer extends Remote {

    public Long checkLog() throws RemoteException;

    public String getUserName() throws RemoteException;

    public int getUserType() throws RemoteException;
    
    public Long getSuccessSeller(Long OrderID) throws RemoteException;

    public int login(String UserName, String Password) throws RemoteException;

    public int logout() throws RemoteException;

    public ArrayList<FxtgRecord> getRecord(String KeyString, int stateMask, ArrayList<Long> typeVector, Long userID) throws RemoteException;

    public ArrayList<FxtgPriceAndAmount> getMyBuyPrice(Long OrderID) throws RemoteException;

    public FxtgPriceAndAmount getMySellPrice(Long OrderID) throws RemoteException;

    public int askBuyerAmount(Long OrderID, double price) throws RemoteException;

    public int offerSellPrice(Long OrderID, double price) throws RemoteException;

    public Long createOrder(Long ItemID, int timeWaitBuyer, int timeWaitSeller) throws RemoteException;

    public int offerBuyPriceAndAmount(Long OrderID, double price, int amount) throws RemoteException;

    public int cancelBuy(Long BuyID) throws RemoteException;

    public int cancelSell(Long SellID) throws RemoteException;

    public int register(String userName, String password, int type) throws RemoteException;

    public int setMyContactWay(String str) throws RemoteException;

    public String getMyContactWay() throws RemoteException;

    public String findSellerContactWay(Long OrderID) throws RemoteException;
}
