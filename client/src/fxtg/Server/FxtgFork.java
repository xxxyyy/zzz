package fxtg.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author xiaodai
 */
public interface FxtgFork extends Remote {

    public FxtgServer getAServer() throws RemoteException;
}
