package fxtg.test;

import fxtg.FxtgRecord;
import fxtg.Server.FxtgServerImpl;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * 用来测试Server。
 *
 * @author xiaodai
 */
public class FxtgServerTest {

    public static void main(String args[]) {
        try {
            FxtgServerImpl tmpS = new FxtgServerImpl();
            //tmpS.register("test02", "123456", 2);
            tmpS.login("test01", "123456");
            tmpS.setMyContactWay("sb!");
            System.out.println(tmpS.getMyContactWay());
            /*
            ArrayList<FxtgRecord> tmp = tmpS.getRecord("", 31, new ArrayList<Long>(), 0L);
            for (int i = 0; i != tmp.size(); i++) {
                System.out.println(tmp.get(i).OrderStartDate);
            }
            */
        } catch (RemoteException ex) {
            System.out.println("error!");
        }
    }
}
