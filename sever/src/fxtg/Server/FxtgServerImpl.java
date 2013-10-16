package fxtg.Server;

import fxtg.FxtgPriceAndAmount;
import fxtg.FxtgRecord;
import fxtg.Taobao.FxtgTaobao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 为远程客户端提供服务的类。 每一个client都通过fork获得一个独立的fxtgServerImpl类的对象。
 * 教程备份：http://home.open-open.com/space-28809-do-blog-id-4681.html
 *
 * @version 0.2
 * @author xiaodai
 */
public class FxtgServerImpl extends UnicastRemoteObject implements FxtgServer {

    //登陆信息
    private Long userID;
    private String userName;
    private int userType;
    //private Date logDate; //用户AFK时间限制（实现起来太麻烦，先不做了）
    //数据库连接信息
    private final String CLASSFORNAME = "com.mysql.jdbc.Driver";
    private final String SERVANDDB = "jdbc:mysql://127.0.0.1:3306/fxtgDB";
    private final String DB_USER = "root";
    private final String DB_PWD = "floyd";
    private Connection dbConnection;
    private Statement stat; //不含参数的语句用它，含参数则使用预备语句
    //一些感觉应该作为常量的参数
    private final int orderMinAmountLimit = 10; //一个团购最少的购买数量（若数量不够则被关闭）
    //团购持续的两个时间段长度的极大极小限制（小时）
    private final int timeWaitBuyerMin = 24;
    private final int timeWaitBuyerMax = 72;
    private final int timeWaitSellerMin = 24;
    private final int timeWaitSellerMax = 72;
    //一个卖家的出价最少满足多少数量的购买
    private final int minAmountLimit = 1;

    /**
     * 初始化登陆状态和数据库连接。
     *
     * @throws RemoteException
     */
    public FxtgServerImpl() throws RemoteException {
        //重置登陆状态
        this.userID = 0L;
        this.userName = "";
        //初始化数据库连接
        try {
            Class.forName(CLASSFORNAME);
            dbConnection = DriverManager.getConnection(SERVANDDB, DB_USER, DB_PWD);
            stat = dbConnection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            //客户端不应该接受数据库异常，直接抛个远程连接异常完事。
            throw new RemoteException();
        }
    }

    /**
     * 为用户提供确认他登陆状态的方法。
     *
     * @return 返回当前登陆的用户ID，返回0则表示未登录或者登陆失效。
     *
     * @throws RemoteException
     */
    public Long checkLog() throws RemoteException {
        return this.userID;
    }

    /**
     * 返回当前登陆账号的用户名。
     *
     * @return 若登陆，则返回用户名；否则返回null。
     * @throws RemoteException
     */
    public String getUserName() throws RemoteException {
        if (!this.userID.equals(0L)) {
            return this.userName;
        }
        return null;
    }

    /**
     * 返回当前登陆用户的类型。
     *
     * @return 若登陆，返回用户类型；否则返回-1。
     * @throws RemoteException
     */
    public int getUserType() throws RemoteException {
        if (!this.userID.equals(0L)) {
            return this.userType;
        }
        return -1;
    }

    /**
     * 通过用户名和密码登陆。 当前用户名密码均已明码表示、传递和保存。以后应该用MD5等方法来确保安全性。
     *
     * @param UserName 用户名
     * @param Password 密码
     * @return 0表示正常登陆，1表示用户名不存在或者密码错误，-1未定义错误（包括但不限于已经登陆过、用户名密码不合法）。
     * @exception RemoteException
     */
    public int login(String UserName, String Password) throws RemoteException {
        //已经有登陆状态
        if (!this.userID.equals(0L)) {
            return -1;
        }

        //检查用户名密码的合法性（防止注入）
        if (!(this.checkUserNameLegality(UserName) && this.checkPasswordLegality(Password))) {
            return -1;
        }

        //核对账户信息
        try {
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("select * from user where User_Name = ?;");
            pstat.setString(1, UserName);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                if (!rs.getString("User_PWD").equals(Password)) {
                    return 1;
                }
                //密码正确，登陆成功！
                this.userID = rs.getLong("User_ID");
                this.userName = rs.getString("User_Name");
                this.userType = rs.getInt("User_Type");
                return 0;
            } else {
                return 1;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 重置登陆状态。
     *
     * @return 0表示登陆状态已经被重置，不论原来是否已经登陆。-1未定义错误。
     * @exception RemoteException
     */
    public int logout() throws RemoteException {
        this.userID = 0L;
        this.userName = "";
        this.userType = 0;
        return 0;
    }

    /**
     * 由关键字、状态限定掩码、类别向量及用户ID搜索符合条件的订单记录信息。
     *
     * @param KeyString 搜索的关键字，目前支持的比较简单（%str%）。 <em>若无限制应该传空串而不是null。</em>
     * KeyString需要做一些处理以避免注入，现在只是用了PreparedStatement来凑合一下。
     * 这个函数实在是太长了，以后有机会得缩减拆分一下。
     * @param stateMask
     * 搜索的订单的状态限定，1表示已经成功结束，2表示等待买家，4表示等待卖家，8表示买家不足团购被中止，16表示没有卖家出价而被中止（这个规则是二进制的掩码形式）。
     * <em>Server会检查这个请求是否合法，并不是所有的请求都会得到回应。调用者应该尽量使得它合法，以减少麻烦。</em>
     * 以掩码的形式传递需要的订单状态应该是够的，它有足够的可扩充性。
     * @param typeVector Long型的向量，传递一个物品类型的父子链，这个是与淘宝API挂钩的。(暂不处理)
     * @param userID
     * 如果需要返回与某ID用户有关的记录（这个应该用于类似于“我参加的团购”（对于买家）、“我出价的团购”(对于卖家)等等）。
     * 合理使用状态掩码可以得到丰富的结果。<em>这个功能目前只支持请求自己的ID，因为现在账户系统尚不支持权限这么高级的东西。</em>
     * <em>如果不限定，则传入0（使用者应该分清楚0与Guest的差别，以免在Guest时调用了与语义不符合的数据）</em>
     * @return 返回fxtgRecord的向量，为搜索的结果。 返回的结果可能是一个空的Vector或者null，两者的含义有区别。
     * 若为空向量则表示没有搜索到结果或者发生了比较安全的错误(比如给定的类别父子链不合法，返回空结果符合逻辑)，返回null则表示错误可能更严重而以至于不能返回一个“空结果”（很可能是请求越权）。
     * @throws RemoteException
     */
    public ArrayList<FxtgRecord> getRecord(String KeyString, int stateMask, ArrayList<Long> typeVector, Long userID) throws RemoteException {
        //检查参数的合法性
        //关键字不能为null
        if (KeyString == null) {
            return null;
        }
        //若有用户限定，则需要检查权限（自己只能申请自己的信息）
        if ((!userID.equals(0L)) && (!this.checkLog().equals(userID))) {
            return null;
        }

        //若状态掩码为0，则返回空结果（以免不存在限制条件的查询导致的查询异常）
        if (stateMask == 0) {
            return new ArrayList<>();
        }

        //类别向量不能为null
        if (typeVector == null) {
            return null;
        }

        //解析stateMask成sql语句
        String stateStr = "(";
        boolean flag = false;
        int cnt = 0;
        int tmpMask = stateMask;
        while (tmpMask > 0) {
            cnt++;
            if (flag) {
                stateStr = stateStr + " or ";
            } else {
                flag = true;
            }
            stateStr = stateStr + "deal.Order_State = '" + cnt + "'";
            tmpMask /= 2;
        }
        stateStr = stateStr + ")";

        ArrayList<FxtgRecord> ret;
        ret = new ArrayList<>();

        try {
            this.updateOrderState(); //更新数据库
            if (userID.equals(0L)) { //通用查询
                //sqlStr = "select deal.Order_ID from deal,item where deal.Order_Goods_ID = item.Item_ID and item.Item_Name like ? and " + stateStr;
                //ResultSet rs = stat.executeQuery(sqlStr);
                PreparedStatement pstat1;

                pstat1 = dbConnection.prepareStatement("select distinct deal.Order_ID from deal,item where deal.Order_Goods_ID = item.Item_ID and item.Item_Name like ? and " + stateStr + ";");
                pstat1.setString(1, "%" + KeyString + "%");
                ResultSet rs = pstat1.executeQuery();
                while (rs.next()) {
                    long tmpOrderID = rs.getLong("Order_ID");
                    FxtgRecord tmpRecord = getARecord(tmpOrderID);
                    if (tmpRecord != null) {
                        ret.add(tmpRecord);
                    }
                }
            } else {    //用户相关信息查询
                if (this.userType == 1) {   //若是买家
                    PreparedStatement pstat2;
                    pstat2 = dbConnection.prepareStatement("select distinct deal.Order_ID from deal,item,buy where deal.Order_Goods_ID = item.Item_ID and buy.Buy_Which = deal.Order_ID and buy.Buy_Who = ? and item.Item_Name like ? and " + stateStr + ";");
                    pstat2.setLong(1, userID);
                    pstat2.setString(2, "%" + KeyString + "%");
                    ResultSet rs = pstat2.executeQuery();
                    while (rs.next()) {
                        long tmpOrderID = rs.getLong("Order_ID");
                        FxtgRecord tmpRecord = getARecord(tmpOrderID);
                        if (tmpRecord != null) {
                            ret.add(tmpRecord);
                        }
                    }
                } else {    //若是卖家
                    PreparedStatement pstat3;
                    pstat3 = dbConnection.prepareStatement("select distinct deal.Order_ID from deal,item,sell where deal.Order_Goods_ID = item.Item_ID and sell.Sell_Which = deal.Order_ID and sell.Sell_Who = ? and item.Item_Name like ? and " + stateStr + ";");
                    pstat3.setLong(1, userID);
                    pstat3.setString(2, "%" + KeyString + "%");
                    ResultSet rs = pstat3.executeQuery();
                    while (rs.next()) {
                        long tmpOrderID = rs.getLong("Order_ID");
                        FxtgRecord tmpRecord = getARecord(tmpOrderID);
                        if (tmpRecord != null) {
                            ret.add(tmpRecord);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            //所有数据库异常都返回null;God Bless这个函数能返回一个非null结果吧。。
            return null;
        }
        return ret;
    }

    /**
     * 请求一个订单该用户作为买家的出价信息向量。
     *
     * @param OrderID 订单ID，这个值应当由返回的fxtgRecord中的OrderID获得。
     * @return 调用者应该确保这个查询时合法的（1、是买家；2、该ID的订单出过价；3、其他合法限制）。
     * 调用者保证调用合法并不困难，因此对于不合法的请求Server会返回不可控的结果。
     * 将会返回一个向量，包含买家多次提出购买请求（如果有的话，server支持同个买家以多种价格和数量来购买）。
     * <em>返回的向量的元素顺序无任何意义。</em>
     * @throws RemoteException
     */
    public ArrayList<FxtgPriceAndAmount> getMyBuyPrice(Long OrderID) throws RemoteException {
        if (this.userType != 1) {
            return null;
        }  //该操作必须是买家
        try {
            this.updateOrderState(); //更新数据库
            ArrayList<FxtgPriceAndAmount> ret = new ArrayList<>();
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("select * from buy where Buy_Who = ? and Buy_Which = ?;");
            pstat.setLong(1, this.userID);
            pstat.setLong(2, OrderID);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                FxtgPriceAndAmount tmpPaA = new FxtgPriceAndAmount();
                tmpPaA.ID = rs.getLong("Buy_ID");
                tmpPaA.amount = rs.getInt("Buy_Amount");
                tmpPaA.price = rs.getDouble("Buy_Price");
                ret.add(tmpPaA);
            }
            return ret;
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * 请求一个订单该用户作为卖家的出价信息。
     *
     * @param OrderID 请求出价的订单ID，返回价格和该价格下的买家数量（不是人数，是物品数量）。
     * @return 同样，不合法的请求结果不可控（大多返回null），请调用者自己保证。
     * @throws RemoteException
     */
    public FxtgPriceAndAmount getMySellPrice(Long OrderID) throws RemoteException {
        if (this.userType != 2) {
            return null;
        }  //该操作必须是买家
        try {
            this.updateOrderState(); //更新数据库
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("select * from sell where Sell_Who = ? and Sell_Which = ?;");
            pstat.setLong(1, this.userID);
            pstat.setLong(2, OrderID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                FxtgPriceAndAmount tmpPaA = new FxtgPriceAndAmount();
                tmpPaA.ID = rs.getLong("Sell_ID");
                tmpPaA.price = rs.getDouble("Sell_Price");
                tmpPaA.amount = this.askBuyerAmount(OrderID, tmpPaA.price);
                return tmpPaA;
            } else {
                return null;
            }
        } catch (SQLException ex) {
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
     * @return 满足出价的购买数。返回-1则表示请求被拒绝。
     * @throws RemoteException
     */
    public int askBuyerAmount(Long OrderID, double price) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("select sum(Buy_Amount) from buy where Buy_Which = ? and Buy_Price >= ?;");
            pstat.setLong(1, OrderID);
            pstat.setDouble(2, price);
            ResultSet rs;
            rs = pstat.executeQuery();
            if (rs.next()) {
                return rs.getInt("sum(Buy_Amount)");
            } else {
                return -1;
            }
        } catch (SQLException ex) {
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
     * @throws RemoteException
     */
    public int offerSellPrice(Long OrderID, double price) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            if (this.userType != 2) {
                return -1;//非卖家身份
            }
            if (getOrderState(OrderID) != 2) {
                return -1;//订单不处于等待卖家状态
            }
            if (this.getMySellPrice(OrderID) != null) {
                return -1;//该订单卖家已经出过价了。getMySellPrice函数若请求失败也会返回null，这步检查似乎并不那么安全。
            }
            if (this.askBuyerAmount(OrderID, price) < minAmountLimit) {
                return -1;//出价不能满足最小限制
            }
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("insert into sell values(NULL,?,?,?);");
            pstat.setLong(1, userID);
            pstat.setLong(2, OrderID);
            pstat.setDouble(3, price);
            pstat.executeUpdate();
            return 0;
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 买家提供给一个商品的ID，发起一个团购请求。
     * <em>该商品应该无正在进行的订单，Server规定同一商品同时只能有一个团购，这在逻辑上是说得通的。</em>
     *
     * @param ItemID
     * @return 返回建立的团购的ID。<em>调用者应当妥善保管这个结果。</em>
     * 对订单的操作需要有OrderID，而若没有妥善保管这个ID。若对订单无购买操作则不会通过searchRecord得到。
     * 若-1则表示请求被拒绝了，0则表示成功。 目前server对拒绝请求的原因并不准备提供更详尽的原因， 若以后有精力可以完善。
     * @throws RemoteException
     */
    public Long createOrder(Long ItemID, int timeWaitBuyer, int timeWaitSeller) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            if (this.userType != 1) {
                return -1L;//检查登陆状态，必须买家
            }
            int tmpItemState = checkItemState(ItemID);  //获得该物品的当前状态
            if (tmpItemState == 1 || tmpItemState == 2) {
                return -1L; //该物品处于活跃状态，不能再发起一个订单了。
            }
            if (tmpItemState == -1) {
                if (!addAItem(ItemID)) {
                    return -1L;   //若该物品在数据库中不存在，则从taobaoAPI中得到相关产品信息。若此ID不合法（或其他淘宝API错误则返回-1）。
                }
            }
            //检查两个时间段长度是否合法
            if (timeWaitBuyer < this.timeWaitBuyerMin || timeWaitBuyer > this.timeWaitBuyerMax) {
                return -1L;
            }
            if (timeWaitSeller < this.timeWaitSellerMin || timeWaitSeller > this.timeWaitSellerMax) {
                return -1L;
            }
            //计算三个时间点
            java.util.Date tmpNow = new java.util.Date();
            Timestamp tmpStartDate;
            tmpStartDate = new Timestamp(tmpNow.getTime());
            Timestamp tmpMidDate;
            tmpMidDate = new Timestamp(tmpNow.getTime() + timeWaitBuyer * 1000 * 3600);
            Timestamp tmpFinalDate;
            tmpFinalDate = new Timestamp(tmpNow.getTime() + (timeWaitBuyer + timeWaitSeller) * 1000 * 3600);
            //插入数据库
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("insert into deal values(NULL,?,?,?,?,?,?);");
            pstat.setLong(1, ItemID);
            pstat.setTimestamp(2, tmpStartDate);
            pstat.setTimestamp(3, tmpMidDate);
            pstat.setTimestamp(4, tmpFinalDate);
            pstat.setLong(5, this.userID);
            pstat.setInt(6, 1);;
            pstat.executeUpdate();
            PreparedStatement pstat2;
            pstat2 = dbConnection.prepareStatement("select Order_ID from deal where Order_Goods_ID = ? and Order_State = '1';");
            pstat2.setLong(1, ItemID);
            ResultSet rs;
            rs = pstat2.executeQuery();
            if (rs.next()) {
                return rs.getLong("Order_ID");
            } else {
                return -1L;
            }
        } catch (SQLException ex) {
            return -1L;
        }
    }

    /**
     * 买家由订单ID向该订单提出购买意向。 调用者应当保证该订单处于等待买家的状态。
     *
     * @param OrderID 订单ID
     * @param price 单价
     * @param amount 数量（目前认为商品的数量为离散的）
     * @return 0表示出价成功，-1表示被拒绝。
     * @throws RemoteException
     */
    public int offerBuyPriceAndAmount(Long OrderID, double price, int amount) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            if (this.userType != 1) {
                return -1;//检查登陆状态，必须买家
            }
            if (this.getOrderState(OrderID) != 1) {
                return -1; //订单状态必须为等待买家
            }
            if (amount < 1) {
                return -1;//数量至少是1
            }
            if (price < 0.01) {
                return -1;    //价格至少是一分
            }
            PreparedStatement pstat;
            pstat = dbConnection.prepareStatement("insert into buy values(NULL,?,?,?,?);");
            pstat.setLong(1, OrderID);
            pstat.setLong(2, this.userID);
            pstat.setDouble(3, price);
            pstat.setInt(4, amount);
            pstat.executeUpdate();
            return 0;
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 取消一个购买动作 <em>只有处于等待买家状态时可以取消，若订单已经过渡到等待卖家阶段，则不可反悔。</em>
     *
     * @param BuyID 要取消的购买动作的ID。
     * @return 若server拒绝请求，则返回-1。成功则返回0。
     * @throws RemoteException
     */
    public int cancelBuy(Long BuyID) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            PreparedStatement pstat = dbConnection.prepareStatement("select * from buy where Buy_ID = ?;");
            pstat.setLong(1, BuyID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                if (rs.getLong("Buy_Who") != this.userID) {
                    return -1;//只能删除自己的购买动作
                }
                if (getOrderState(rs.getLong("Buy_Which")) != 1L) {
                    return -1;//只能在等待买家阶段取消购买动作
                }
                PreparedStatement pstat2 = dbConnection.prepareStatement("delete from buy where Buy_ID = ?;");
                pstat2.setLong(1, BuyID);
                pstat2.executeUpdate();
                return 0;
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 取消一个出售动作。 若卖家要重新报价，则必须先使用这个方法。以免重复报价造成插入异常。
     *
     * @param SellID 出售动作的ID。
     * @return -1拒绝，0成功。
     * @throws RemoteException
     */
    public int cancelSell(Long SellID) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            PreparedStatement pstat = dbConnection.prepareStatement("select * from sell where Sell_ID = ?;");
            pstat.setLong(1, SellID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                if (rs.getLong("Sell_Who") != this.userID) {
                    return -1;//只能删除自己的出售动作
                }
                if (getOrderState(rs.getLong("Sell_Which")) != 2) {
                    return -1;//只能在等待卖家阶段取消出售动作
                }
                PreparedStatement pstat2 = dbConnection.prepareStatement("delete from sell where Sell_ID = ?;");
                pstat2.setLong(1, SellID);
                pstat2.executeUpdate();
                return 0;
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 用来注册新账号的方法。
     *
     * @param userName
     * 用户名<em>应该是不少于6个字符，不超过20个字符，以英文开头的英文和数字混合的字符串（不区分大小写）。</em>
     * @param password 密码<em>应该是6-10位的纯数字，尚不支持忘记密码找回。</em>
     * @return 0表示新建成功，-1表示被拒绝。
     * @throws RemoteException
     */
    public int register(String userName, String password, int type) throws RemoteException {
        try {
            this.updateOrderState(); //更新数据库
            if (!this.checkUserNameLegality(userName)) {
                return -1;
            }
            if (!this.checkPasswordLegality(password)) {
                return -1;
            }
            if (type != 1 && type != 2) {
                return -1;
            }
            PreparedStatement pstat = dbConnection.prepareStatement("select * from user where User_Name = ?;");
            pstat.setString(1, userName);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                return -1;//用户名存在
            } else {
                PreparedStatement pstat2;
                pstat2 = dbConnection.prepareStatement("insert into user (User_ID,User_Name,User_PWD,User_Type) values(NULL,?,?,?);");
                pstat2.setString(1, userName);
                pstat2.setString(2, password);
                pstat2.setInt(3, type);
                pstat2.executeUpdate();
                return 0;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 设置当前登陆账户的联系方式，用于买卖双方建立联系。 需成功登陆后才可调用。
     *
     * @param str 联系方式(不得超过40个字的string)
     * @return 0成功，-1失败。
     */
    public int setMyContactWay(String str) {
        if (this.userID.equals(0L)) {
            return -1;
        }
        if (str == null || str.length() > 40) {
            return -1;
        }
        try {
            PreparedStatement pstat = dbConnection.prepareStatement("update user set User_ContactWay = ? where User_ID = ?;");
            pstat.setString(1, str);
            pstat.setLong(2, userID);
            pstat.executeUpdate();
            return 0;
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * 获得当前登陆账户的联系方式。
     *
     * @return 若出错则为null，否则为相应的string
     */
    public String getMyContactWay() {
        if (this.userID.equals(0L)) {
            return null;
        } else {
            return getContactWay(this.userID);
        }
    }

    private String getContactWay(Long tmpID) {
        if (tmpID.equals(0L)) {
            return null;
        }
        try {
            PreparedStatement pstat = dbConnection.prepareStatement("select * from user where User_ID = ?;");
            pstat.setLong(1, tmpID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                return rs.getString("User_ContactWay");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * 由订单号获得成功团购中买家的联系方式。
     *
     * @param OrderID 订单必须是已经结束的。
     * @return 联系方式，若请求被拒绝则返回null。
     */
    public String findSellerContactWay(Long OrderID) {
        PreparedStatement pstat;
        try {
            pstat = dbConnection.prepareStatement("select * from final_order where Order_ID = ?;");
            pstat.setLong(1, OrderID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                return getContactWay(rs.getLong("Order_Final_seller"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * 由订单ID得到一个fxtgRecord的封装。
     *
     * @param tmpOrderID 查询的OrderID
     * @return fxtgRecord的即时的封装。
     * @throws SQLException
     */
    public FxtgRecord getARecord(long tmpOrderID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        FxtgRecord ret = new FxtgRecord();
        PreparedStatement pstat = dbConnection.prepareStatement("select * from deal where Order_ID = ?;");
        pstat.setLong(1, tmpOrderID);
        ResultSet rs = pstat.executeQuery();
        if (rs.next()) {
            ret.ItemID = rs.getLong("Order_Goods_ID");
            ret.OrderID = rs.getLong("Order_ID");
            ret.OrderStartDate = rs.getTimestamp("Order_Start_Date");
            ret.OrderMidDate = rs.getTimestamp("Order_Mid_Date");
            ret.OrderFinalDate = rs.getTimestamp("Order_Final_Date");
            ret.OrderState = rs.getInt("Order_State");
            ret.buyAmount = getBuyAmount(tmpOrderID);
            ret.sellAmount = getSellAmount(tmpOrderID);
            return ret;
        } else {
            return null;
        }
    }

    /**
     * 由Order_ID统计该单的购买数量（商品个数，不是人数）
     *
     * @param tmpOrderID 查询的Order_ID
     * @return 商品购买次数（若ID不合法，从逻辑上依然是0购买次数，依然会返回0）。
     * @throws SQLException
     */
    private int getBuyAmount(long tmpOrderID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        PreparedStatement pstat;
        pstat = dbConnection.prepareStatement("select sum(Buy_Amount) from buy where Buy_Which = ?;");
        pstat.setLong(1, tmpOrderID);
        ResultSet rs = pstat.executeQuery();
        if (rs.next()) {
            return rs.getInt("sum(Buy_Amount)");
        } else {
            return 0;
        }
    }

    /**
     * 由Order_ID得到某订单的卖家出价的人数。
     *
     * @param tmpOrderID 查询的Order_ID
     * @return 该订单的出价人数，若ID不合法则返回0。
     * @throws SQLException
     */
    private int getSellAmount(long tmpOrderID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        PreparedStatement pstat;
        pstat = dbConnection.prepareStatement("select count(*) from sell where Sell_Which = ?;");
        pstat.setLong(1, tmpOrderID);
        ResultSet rs = pstat.executeQuery();
        if (rs.next()) {
            return rs.getInt("count(*)");
        } else {
            return 0;
        }
    }

    /**
     * 查询某订单ID的状态。
     *
     * @param OrderID 订单ID
     * @return 订单的即时状态，订单不存在返回-1。
     */
    private int getOrderState(Long OrderID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        PreparedStatement pstat;
        pstat = dbConnection.prepareStatement("select Order_State from deal where Order_ID = ?;");
        pstat.setLong(1, OrderID);
        ResultSet rs;
        rs = pstat.executeQuery();
        if (rs.next()) {
            return rs.getInt("Order_State");
        } else {
            return -1;
        }
    }

    /**
     * 用来检查一个字符串是否符合用户名的标准。
     * 用户名<em>应该是不少于6个字符，不超过20个字符，以英文开头的英文和数字混合的字符串（不区分大小写）。</em>
     *
     * @param userName 被检查的字符串。
     * @return true符合标准，可以用作用户名。false，不符合标准。
     */
    private boolean checkUserNameLegality(String tmpName) {
        if (tmpName == null) {
            return false;
        }
        //长度不符合
        if (tmpName.length() < 6 || tmpName.length() > 20) {
            return false;
        }
        //不区分大小写，一律统一为小写字母
        tmpName = tmpName.toLowerCase();
        //首字母不是字母
        if (tmpName.charAt(0) < 'a' || tmpName.charAt(0) > 'z') {
            return false;
        }
        //仅能由字母和数字组成
        for (int i = 0; i != tmpName.length(); i++) {
            if (!(('a' <= tmpName.charAt(i) && tmpName.charAt(i) <= 'z') || ('0' <= tmpName.charAt(i) && tmpName.charAt(i) <= '9'))) {
                return false;
            }
        }
        //检查条件通过
        return true;
    }

    /**
     * 检查密码字符串的合法性，6-10位的数字。
     *
     * @param tmpPWD 被检查的字符串。
     * @return true符合标准，可以用作用户名。false，不符合标准。
     */
    private boolean checkPasswordLegality(String tmpPWD) {
        if (tmpPWD == null) {
            return false;
        }
        //长度不符合
        if (tmpPWD.length() < 6 || tmpPWD.length() > 10) {
            return false;
        }

        //仅能数字组成
        for (int i = 0; i != tmpPWD.length(); i++) {
            if (!('0' <= tmpPWD.charAt(i) && tmpPWD.charAt(i) <= '9')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取一个物品的当前订单状态，一件物品只能属于一个活跃的订单。
     *
     * @param ItemID
     * @return 0不存在处于活跃的订单，1等待买家，2等待卖家，-1该物品不存在。
     */
    private int checkItemState(Long ItemID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        PreparedStatement pstat;
        pstat = dbConnection.prepareStatement("select Item_ID from item where Item_ID = ?;");
        pstat.setLong(1, ItemID);
        ResultSet rs;
        rs = pstat.executeQuery();
        if (rs.next()) {
            PreparedStatement pstat2;
            pstat2 = dbConnection.prepareStatement("select Order_State from item,deal where Item_ID = ? and Order_Goods_ID = Item_ID and Order_State = '1' or Order_State = '2';");
            pstat2.setLong(1, ItemID);
            ResultSet rs2;
            rs2 = pstat2.executeQuery();
            if (rs2.next()) {
                return rs2.getInt("Order_State");
            } else {
                return 0;
            }
        } else {
            return -1;
        }

    }

    /**
     * 将淘宝ID的商品加入系统自己的数据库（应该保证原来不存在）。
     *
     * @param ItemID 淘宝商品ID
     */
    private boolean addAItem(Long ItemID) throws SQLException, RemoteException {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.updateOrderState(); //更新数据库
        if (this.checkItemState(ItemID) != -1) {
            return true;//商品已经存在了
        }
        PreparedStatement pstat;
        String tmpItemName = FxtgTaobao.getItemName(ItemID);
        if (tmpItemName == null) {
            return false;//TaobaoAPI没能返回结果
        }
        pstat = dbConnection.prepareStatement("insert into item (Item_ID,Item_Name) values(?,?);");
        pstat.setLong(1, ItemID);
        pstat.setString(2, tmpItemName);
        pstat.executeUpdate();
        return true;
    }

    public Long getSuccessSeller(Long OrderID)
    {
        try {
            PreparedStatement pstat = dbConnection.prepareStatement("select * from final_order where Order_ID = ?;");
            pstat.setLong(1, OrderID);
            ResultSet rs = pstat.executeQuery();
            if (rs.next())
            {
                return rs.getLong("Order_Final_Seller");
            }
            else return null;
        } catch (SQLException ex) {
            return null;
        }
    }
    /**
     * 用来更新数据库中订单的状态。 用于确保数据库中订单的状态随时间的变化而被更新。
     * 为了保证线程安全，这个函数必须是互斥的，函数的第一步即将互斥锁锁死。
     */
    public void updateOrderState() throws SQLException, RemoteException {
        Lock myLock = new ReentrantLock();
        /*
         * 状态编号说明：
         * 0 预留（在该函数中将订单时间结束且有卖家出价的订单先设为0状态，处理后设为5，以区别完成的订单是否已经处理。因为这个函数是互斥的，因此这个中间状态仅存在于函数执行过程中，不会出问题。）
         * 1 等待买家
         * 2 等待卖家
         * 3 买家人数不足
         * 4 无卖家出价
         * 5 已经成交
         */
        //myLock.lock();//锁死函数，以免数据库更新异常
        if (!myLock.tryLock()) {
            return;
        }  //若函数正在被锁住，不需要阻塞当前线程，因为并发多次update是毫无意义的，直接返回即可。
        try {
            ResultSet rs;
            String sqlStr;

            //将等待买家时间段结束的而人数不足的订单关闭。
            PreparedStatement pstat1 = dbConnection.prepareStatement("update deal set Order_State = '3' where Order_State = '1' and Order_ID in (select Order_ID from(select deal.Order_ID from deal,buy where buy.Buy_Which = deal.Order_ID and Order_Mid_Date < now() and Order_Final_Date > now() group by Order_ID having sum(Buy_Amount) < ?) as tmptable);");
            pstat1.setInt(1, this.orderMinAmountLimit);
            pstat1.executeUpdate();
            //若某订单无购买动作，上面的语句会查询异常，用这条语句弥补一下。
            sqlStr = "update deal set Order_State = '3' where Order_State = '1'  and Order_ID in (select Order_ID from(select Order_ID from deal where Order_Mid_Date < now() and not exists(select Order_ID from buy where deal.Order_ID = buy.Buy_Which)) as tmptable);";
            stat.executeUpdate(sqlStr);

            //将等待买家时间段结束的而人数足够的订单变为等待卖家状态。
            PreparedStatement pstat2 = dbConnection.prepareStatement("update deal set Order_State = '2' where Order_State = '1' and Order_ID in (select Order_ID from(select deal.Order_ID from deal,buy where buy.Buy_Which = deal.Order_ID and Order_Mid_Date < now() and Order_Final_Date > now() group by Order_ID having sum(Buy_Amount) >= ?) as tmptable);");
            pstat2.setInt(1, this.orderMinAmountLimit);
            pstat2.executeUpdate();

            //将等待卖家时间段结束而无人出价的订单关闭。
            sqlStr = "update deal set Order_State = '4' where Order_State = '2' and Order_ID in (select Order_ID from(select Order_ID from deal where Order_Final_Date < now() and not exists(select Order_ID from sell where deal.Order_ID = sell.Sell_Which)) as tmptable);";
            stat.executeUpdate(sqlStr);

            //将等待卖家时间段结束，有卖家出价，处理成功订单。
            //将已经到达结束时间且有人出价的订单的状态标记为0
            sqlStr = "update deal set Order_State = '0' where Order_State = '2' and Order_ID in (select Order_ID from(select Order_ID from deal where Order_Final_Date < now() and exists(select Order_ID from sell where deal.Order_ID = sell.Sell_Which)) as tmptable);";
            stat.executeUpdate(sqlStr);
            sqlStr = "select * from deal where Order_State = '0';";
            rs = stat.executeQuery(sqlStr);
            while (rs.next()) {
                Long tmpOrderID = rs.getLong("Order_ID");
                ResultSet tmpRS;

                //选取价格最低的卖家（若最低卖家不唯一则结果不确定）
                PreparedStatement pstat3 = dbConnection.prepareStatement("select * from sell where Sell_Which = ? order by Sell_Price limit 1;");
                pstat3.setLong(1, tmpOrderID);
                tmpRS = pstat3.executeQuery();
           
                if (tmpRS.next()) {
                    //状态设为5
                    PreparedStatement pstat4 = dbConnection.prepareStatement("update deal set Order_State = '5' where Order_ID = ?;");
                    pstat4.setLong(1, tmpOrderID);
                    pstat4.executeUpdate();

                    //添加成功订单的记录
                    double tmpPrice = tmpRS.getDouble("Sell_Price");
                    Long tmpSeller = tmpRS.getLong("Sell_Who");
                    int tmpAmount = askBuyerAmount(tmpOrderID, tmpPrice);
                    PreparedStatement pstat5 = dbConnection.prepareStatement("insert into final_order values(?,?,?,?);");
                    pstat5.setLong(1, tmpOrderID);
                    pstat5.setDouble(2, tmpPrice);
                    pstat5.setLong(3, tmpSeller);
                    pstat5.setInt(4, tmpAmount);
                    pstat5.executeUpdate();
                }
            }
        } finally {
            myLock.unlock();
        }
    }
}
