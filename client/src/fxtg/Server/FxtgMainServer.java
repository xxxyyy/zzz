package fxtg.Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import javax.naming.NamingException;

/**
 * 服务器主线程，运行着唯一的Fork实例，给每个Client分配Server对象。
 *
 * @author xiaodai
 */
public class FxtgMainServer {

    public static void main(String[] args) throws RemoteException, NamingException {
        try {
            LocateRegistry.createRegistry(1099);
            FxtgForkImpl mainFork = new FxtgForkImpl();
            Naming.rebind("fxtg_Fork", mainFork);
            System.out.println("RMI ok!");
        } catch (MalformedURLException ex) {
            System.out.println("RMI registry failed!");
        }
    }
}
