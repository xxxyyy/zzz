package fxtg;

import java.io.Serializable;
import java.util.Date;

/**
 * fxtgRecord是一个简单纯粹的记录类型，只有成员对象而没有方法。
 * 记录一个订单的中的可公开信息，主要用户client与server通讯的数据的传递。
 * <em>这是一个静态记录，它的所有成员均是请求时服务器返回的信息。
 * 利用这些信息的时候应该确保它依然有效。</em>
 * 根据RMI的远程请求的要求，它必须是可序列化的以进行流传递，因此定义了Serializable接口。
 * Date类使用的是java.sql.Date，与sql更好的兼容。
 * @author xiaodai
 */
public class FxtgRecord implements Serializable{
    /**
     * 物品的淘宝ID，64位整型。
     */
    public Long ItemID;
    /**
     * 订单的ID，64位整型。
     */
    public Long OrderID;
    /**
     * 团购的发起时间。
     */
    public Date OrderStartDate;
    /**
     * 团购的中间时间点，在这个时间点订单从买家操作转变为卖家操作
     */
    public Date OrderMidDate;
    /**
     * 团购的结束时间点。
     */
    public Date OrderFinalDate;
    /**
     * 订单的状态 5表示已经成功结束，1表示等待买家，2表示等待卖家，3表示买家不足团购被中止，4表示没有卖家出价而被中止。
     */
    public int OrderState;
    /**
     * 买家订购数量（数量，不是人数，支持一个人订购多件）
     */
    public int buyAmount;
    /**
     * 卖家出价人数
     */
    public int sellAmount;
    public static final int WAITING_BUYER = 1;
    public static final int WAITING_SELLER = 2;
    public static final int NOT_ENOUGHT_BUYER = 3;
    public static final int NOT_ENOUGHT_SELLER = 4;
    public static final int SUCCESSFUL_END = 5;
}
