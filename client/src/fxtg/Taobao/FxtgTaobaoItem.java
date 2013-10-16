package fxtg.Taobao;

import java.util.*;

/**
 * 这个类用于记录从淘宝上获得的商品信息。
 *
 * @author LS
 */
public class FxtgTaobaoItem {

    private Long num_iid = 0L;
    private String name;
    private String pic_url;
    private Double price = 0D;
    private Long cid = 0L;
    private String cid_name;
    private ArrayList<Long> cidAll;
    private ArrayList<String> cidAllName;
    public boolean err = false;

    /**
     * 默认构造函数。
     */
    public FxtgTaobaoItem() {
        cidAll = new ArrayList<Long>();
        cidAllName = new ArrayList<String>();
    }

    /**
     * 传入商品ID构造函数。
     *
     * @param n 商品ID
     */
    public FxtgTaobaoItem(Long n) {
        num_iid = n;
        cidAll = new ArrayList<Long>();
        cidAllName = new ArrayList<String>();
    }

    /**
     * 获得商品ID。
     *
     * @return 商品ID
     */
    public Long getNum_iid() {
        return num_iid;
    }

    /**
     * 设置商品ID。
     *
     * @param num_iid 商品ID
     */
    public void setNum_iid(Long num_iid) {
        this.num_iid = num_iid;
    }

    /**
     * 获得商品名称。
     *
     * @return 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称。
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得商品图片链接。
     *
     * @return 商品图片链接
     */
    public String getPic_url() {
        return pic_url;
    }

    /**
     * 设置商品图片链接。
     *
     * @param pic_url 商品图片链接
     */
    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    /**
     * 获得商品单价。
     *
     * @return 商品单价
     */
    public Double getPrice() {
        return price;
    }

    /**
     * 设置商品单价。
     *
     * @param price 商品单价
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 获得商品类别ID。
     *
     * @return 商品类别ID
     */
    public Long getCid() {
        return cid;
    }

    /**
     * 设置商品类别ID。
     *
     * @param cid 商品类别ID
     */
    public void setCid(Long cid) {
        this.cid = cid;
    }

    /**
     * 获得商品类别名称。
     *
     * @return 商品类别名称
     */
    public String getCid_name() {
        return cid_name;
    }

    /**
     * 设置商品类别名称。
     *
     * @param cid_name 商品类别名称
     */
    public void setCid_name(String cid_name) {
        this.cid_name = cid_name;
    }

    /**
     * 获得商品的类别ID的数组列表。
     *
     * @return 商品的类别ID的数组列表
     */
    public ArrayList<Long> getCidAll() {
        return cidAll;
    }

    /**
     * 设置商品的类别ID的数组列表。
     *
     * @param cidAll 商品的类别ID的数组列表
     */
    public void setCidAll(ArrayList<Long> cidAll) {
        this.cidAll = cidAll;
    }

    /**
     * 获得商品的类别名称的数组列表。
     *
     * @return 商品的类别名称的数组列表
     */
    public ArrayList<String> getCidAllName() {
        return cidAllName;
    }

    /**
     * 设置商品的类别名称的数组列表。
     *
     * @param cidAllName 商品的类别名称的数组列表
     */
    public void setCidAllName(ArrayList<String> cidAllName) {
        this.cidAllName = cidAllName;
    }
}
