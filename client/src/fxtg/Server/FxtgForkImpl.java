package fxtg.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 此类将注册到RMI注册表中，远程client通过注册表得到该类的对象的存根，用于开启独立的server。
 *
 * @author xiaodai
 */
public class FxtgForkImpl extends UnicastRemoteObject implements FxtgFork {

    public FxtgForkImpl() throws RemoteException {
    }

    public FxtgServer getAServer() throws RemoteException {
        System.out.println("A Client Connection is build!");
        FxtgServer tmpServer = new FxtgServerImpl();
        return tmpServer;
    }
}
