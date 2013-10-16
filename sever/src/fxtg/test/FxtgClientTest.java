package fxtg.test;

import fxtg.Client.FxtgClient;

/**
 * 这个是Server作者用来测试Client行为的。
 *
 * @author xiaodai
 */
public class FxtgClientTest {

    public static void main(String args[]) {
        try {
            FxtgClient testC = new FxtgClient();
            //testC.register("test12", "123456", 2);
            testC.login("test01", "123456");
            testC.setMyContactWay("iamsb!");
            System.out.println(testC.getMyContactWay());
            //testC.createOrder(20330912673L, 24, 24);
            System.out.println(testC.getUserID());
        } catch (Exception ex) {
            System.out.println("excption!");
        }

    }
}
