package fxtg.Client;

import fxtg.FxtgPriceAndAmount;
import fxtg.FxtgRecord;
import fxtg.Server.FxtgFork;
import fxtg.Server.FxtgServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * FxtgClient类封装了客户端的行为，GUI应该调用这个类以与服务器进行交互，包括登陆、信息查询和操作等。
 *
 * @author xiaodai
 * @version 0.2 - 修改了RMI的实现方式，加入了对网络异常的一些处理的尝试。
 */
public class FxtgClient {

    private FxtgServer nowServer;
    private boolean OnlineState;
    public static final int SUCESSFUL_END = 1;
    public static final int WAITING_BUYER = 2;
    public static final int WAITING_SELLER = 4;
    public static final int NOT_ENOUGH_BUYER = 8;
    public static final int NOT_ENOUGH_SELLER = 16;
    public static final int ALL_STATE = 31;

    /**
     * 初始化，并尝试与Server进行连接。 支持两种构造器，默认构造会认为Server在本地。或者传入Server的IP。
     *
     */
    public FxtgClient() {
        this.tryToConnect("localhost");
    }

    public FxtgClient(String tmpURL) {
        this.tryToConnect(tmpURL);
    }

    /**
     * 试图用给定的IP重新连接服务器。
     *
     * @param tmpURL Server的IP
     * @return true - 连接成功；false - 连接失败。
     */
    public boolean tryToConnect(String tmpURL) {
        FxtgFork nowFork;
        try {
            nowFork = (FxtgFork) Naming.lookup("//" + tmpURL + ":1099/fxtg_Fork");
            //用fork获得server存根
            nowServer = nowFork.getAServer();
            nowServer.logout();//重置登陆状态以免不测
            this.OnlineState = true;
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            this.OnlineState = false;
            return false;
        }
    }

    /**
     * 检查当前与服务器的连通性（类似于PING）。
     *
     * @return true - 连接正常；false - 连接失败。
     */
    public boolean checkConnection() {
        try {
            //this.checkLog();
            this.nowServer.checkLog();
            this.OnlineState = true;
            return true;
        } catch (Exception ex) {
            this.OnlineState = false;
            return false;
        }
    }

    /**
     * 获得上一次远程调用的成功与否。
     *
     * @return false连接一定失败；true上次的连接是成功的（尚未遇到失败的情况）。
     */
    public boolean getNowConnectionState() {
        return this.OnlineState;
    }

    /**
     * 用来检查当前对象的登陆状态。
     *
     * @return true当前登陆状态有效，false则表示无效(处于未登录状态)。
     * 若返回false，说明当前的登陆状态已经于远程不统一，应该logout以确保健壮性。
     */
    public boolean checkLog() {
        try {
            if (!nowServer.checkLog().equals(0L)) {
                this.OnlineState = true;
                return true;
            } else {
                this.OnlineState = true;
                return false;
            }
        } catch (Exception ex) {
            this.OnlineState = false;
            return false;
        }
    }

    /**
     * 通过用户名和密码向server认证身份。 当前用户名密码均已明码表示、传递和保存。 <em>以后应该用MD5等方法来确保安全性。</em>
     *
     * @param UserName 用户名
     * @param Password 密码
     * @return 0表示正常登陆，1表示用户名不存在或者密码错误，-1其他错误（用户名、密码不合法，已有登陆状态，其他未知错误）。
     */
    public int login(String UserName, String Password) {
        try {
            int ret = nowServer.login(UserName, Password);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 用来重置登陆状态。
     *
     * @return 0表示登陆状态已经被重置，不论原来是否已经登陆。-1未定义错误。
     */
    public int logout() {
        try {
            int ret = nowServer.logout();
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }
    
    public Long getSueecssSeller(Long OrderID)
    {
        try{
            Long ret = nowServer.getSuccessSeller(OrderID);
            this.OnlineState= true;
            return ret;
        } catch (Exception ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 获得登陆账号的ID。
     *
     * @return 当前登陆账号的ID，注意为64位整型。Guset为0。连接失败为-1。
     */
    public Long getUserID() {
        try {
            Long ret = nowServer.checkLog();
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1L;
        }
    }

    /**
     * 设置我的联系方式，需保证登陆。
     *
     * @param str 不得超过40个字。
     * @return 0成功，-1失败。
     */
    public int setMyContactWay(String str) {
        try {
            int ret = nowServer.setMyContactWay(str);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 得到我的联系方式，需登陆。
     *
     * @return 若失败返回null
     */
    public String getMyContactWay() {
        try {
            String ret = nowServer.getMyContactWay();
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 由订单号得到已经成功的订单卖家的联系方式。
     *
     * @param OrderID 需要保证订单已经成功结束，且当前用户参与了此次团购。
     * @return 失败返回null。
     */
    public String findSellerContactWay(Long OrderID) {
        try {
            String ret = nowServer.findSellerContactWay(OrderID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 获得登陆账号的用户名。
     *
     * @return 当前登陆账号的用户名，若未登陆则为返回“Guset”（用户名至少6个字符，不会冲突）。连接失败返回null。
     */
    public String getUserName() {
        try {
            String tmpName = nowServer.getUserName();
            this.OnlineState = true;
            if (tmpName == null) {
                return "Guest";
            } else {
                return tmpName;
            }
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 得到登陆用户的类型
     *
     * @return 0表示Guest，1表示买家，2表示卖家,-1表示连接失败。
     */
    public int getUserType() {
        try {
            int tmpType = nowServer.getUserType();
            this.OnlineState = true;
            if (tmpType == -1) {
                nowServer.logout();
                return 0;
            }
            return tmpType;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 由关键字、状态限定掩码、类别向量及用户ID向服务器搜索符合条件的订单记录信息。
     *
     * @param KeyString 搜索的关键字，目前支持的比较简单（%str%）。 <em>若无限制应该传空串而不是null。</em>
     * @param stateMask
     * 搜索的订单的状态限定，1表示已经成功结束，2表示等待买家，4表示等待卖家，8表示买家不足团购被中止，16表示没有卖家出价而被中止（这个规则是二进制的掩码形式）。
     * <em>Server会检查这个请求是否合法，并不是所有的请求都会得到回应。调用者应该尽量使得它合法，以减少麻烦。</em>
     * 以掩码的形式传递需要的订单状态应该是够的，它有足够的可扩充性。
     * @param typeVector Long型的向量，传递一个物品类型的父子链，这个是与淘宝API挂钩的。
     * @param userID
     * 如果需要返回与某ID用户有关的记录（这个应该用于类似于“我参加的团购”（对于买家）、“我出价的团购”(对于卖家)等等）。
     * 合理使用状态掩码可以得到丰富的结果。<em>这个功能目前只支持请求自己的ID，因为现在账户系统尚不支持权限这么高级的东西。</em>
     * <em>如果不限定，则传入0（使用者应该分清楚0与Guest的差别，以免在Guest时调用了与语义不符合的数据）</em>
     * @return 返回FxtgRecord的向量，为搜索的结果。 返回的结果可能是一个空的Vector或者null，两者的含义有区别。
     * 若为空向量则表示没有搜索到结果或者发生了比较安全的错误(比如给定的类别父子链不合法，返回空结果符合逻辑)，返回null则表示错误可能更严重而以至于不能返回一个“空结果”（很可能是请求越权）。
     * 若连接失败则返回null。
     */
    public ArrayList<FxtgRecord> getRecord(String KeyString, int stateMask, ArrayList<Long> typeVector, Long userID) {
        try {
            ArrayList<FxtgRecord> ret = nowServer.getRecord(KeyString, stateMask, typeVector, userID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 请求一个订单该用户作为买家的出价信息向量
     *
     * @param OrderID 订单ID，这个值应当由返回的FxtgRecord中的OrderID获得。
     * @return 调用者应该确保这个查询时合法的（1、是买家；2、该ID的订单出过价；3、其他合法限制）。
     * 调用者保证调用合法并不困难，因此对于不合法的请求Server会返回不可控的结果。
     * 将会返回一个向量，包含买家多次提出购买请求（如果有的话，server支持同个买家以多种价格和数量来购买）。
     * <em>返回的向量的元素顺序无任何意义。</em> 连接失败返回null。
     */
    public ArrayList<FxtgPriceAndAmount> getMyBuyPrice(Long OrderID) {
        try {
            ArrayList<FxtgPriceAndAmount> ret = nowServer.getMyBuyPrice(OrderID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }

    }

    /**
     * 请求一个订单该用户作为卖家的出价信息
     *
     * @param OrderID 请求出价的订单ID，返回价格和该价格下的买家数量（不是人数，是物品数量）。
     * @return 同样，不合法的请求结果不可控（大多返回null），请调用者自己保证。 连接失败返回null。
     */
    public FxtgPriceAndAmount getMySellPrice(Long OrderID) {
        try {
            FxtgPriceAndAmount ret = nowServer.getMySellPrice(OrderID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return null;
        }
    }

    /**
     * 卖家由订单号和出价得到买家数量。 此价格可以满足多少买家的出价，这个数量是商品数量，因为单个买家的购买数量可以是多个的。
     * <em>这是一个询问请求，不是正式出价。</em>因此Server对这个请求的安全判定比较弱。
     * 因此GUI不需要卖家登陆既可以使用该方法得到一个商品的买家购买数量-价格的分布（比如用于图形化，我认为这样直观友好）。
     * <em>以现有的数据库设计，这个操作消耗很大，请适度请求。</em>
     * 原则上，调用者应该尽量调用处于等待卖家的订单，不保证处于其他状态的请求返回的结果。
     * （比如在等待买家的阶段数据是动态的，维护这个将非常消耗且麻烦，而且我觉得意义不大）
     *
     * @param OrderID 请求订单ID。
     * @param price 请求的出价。
     * @return 满足出价的购买数。返回-1则表示请求被拒绝。 连接失败返回-1。
     */
    public int askBuyerAmount(Long OrderID, double price) {
        try {
            int ret = nowServer.askBuyerAmount(OrderID, price);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }

    }

    /**
     * 卖家向OrderID订单出价。 这是卖家正式报价的方法。
     *
     * @param OrderID 订单ID。
     * @param price 出价。
     * @return 调用者应该确保请求是合法的（1、有效的卖家登陆；2、订单处于等待卖家状态；3、其他合法约束）。
     * <em>与买家不同，卖家只能对一个订单报一个价格。如果要重新报价，需要先取消他的卖家报价。否则会插入异常。</em>
     * 返回-1则表明这个请求被拒绝了，0则表示成功。
     */
    public int offerSellPrice(Long OrderID, double price) {
        try {
            int ret = nowServer.offerSellPrice(OrderID, price);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 买家提供给一个商品的ID，发起一个团购请求。
     * <em>该商品应该无正在进行的订单，Server规定同一商品同时只能有一个团购，这在逻辑上是说得通的。</em>
     *
     * @param ItemID 发起团购的物品的淘宝ID
     * @param timeWaitBuyer 等待买家时间段长度
     * @param timeWaitSeller 等待卖家时间段长度
     * @return 返回建立的团购的ID。<em>调用者应当妥善保管这个结果。</em>
     * 对订单的操作需要有OrderID，而若没有妥善保管这个ID。若对订单无购买操作则不会通过searchRecord得到。
     * 若-1则表示请求被拒绝了，0则表示成功。 目前server对拒绝请求的原因并不准备提供更详尽的原因，若以后有精力可以完善。
     * <em>对于两个时间段，为了server健壮性，会有极大极小的限制。（保证24-72小时是一定有效的，但server可能修改这个设定）若时间段不合法则返回-1。</em>
     * 若连接失败，返回-1L。
     */
    public Long createOrder(Long ItemID, int timeWaitBuyer, int timeWaitSeller) {
        try {
            Long ret = nowServer.createOrder(ItemID, timeWaitBuyer, timeWaitSeller);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1L;
        }
    }

    /**
     * 买家由订单ID向该订单提出购买意向。 调用者应当保证该订单处于等待买家的状态。
     *
     * @param OrderID 订单ID
     * @param price 单价
     * @param amount 数量（目前认为商品的数量为离散的）
     * @return 0表示出价成功，-1表示被拒绝或连接失败。
     */
    public int offerBuyPriceAndAmount(Long OrderID, double price, int amount) {
        try {
            int ret = nowServer.offerBuyPriceAndAmount(OrderID, price, amount);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 取消一个购买动作 <em>只有处于等待买家状态时可以取消，若订单已经过渡到等待卖家阶段，则不可反悔。</em>
     *
     * @param BuyID 要取消的购买动作的ID。
     * @return 若server拒绝请求或连接失败，则返回-1。成功则返回0。
     */
    public int cancelBuy(Long BuyID) {
        try {
            int ret = nowServer.cancelBuy(BuyID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }

    }

    /**
     * 取消一个出售动作。 若卖家要重新报价，则必须先使用这个方法。以免重复报价造成插入异常。
     *
     * @param SellID 出售动作的ID。
     * @return -1拒绝或连接失败，0成功。
     */
    public int cancelSell(Long SellID) {
        try {
            int ret = nowServer.cancelSell(SellID);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }

    /**
     * 用来注册新账号的方法。
     *
     * @param userName
     * 用户名<em>应该是不少于6个字符，不超过20个字符，以英文开头的英文和数字混合的字符串（不区分大小写）。</em>
     * @param password 密码<em>应该是6-10位的纯数字，尚不支持忘记密码找回。</em>
     * @param type 注册用户的类别。
     * @return 0表示新建成功，-1表示被拒绝或连接失败。
     */
    public int register(String userName, String password, int type) {
        try {
            int ret = nowServer.register(userName, password, type);
            this.OnlineState = true;
            return ret;
        } catch (RemoteException ex) {
            this.OnlineState = false;
            return -1;
        }
    }
}
