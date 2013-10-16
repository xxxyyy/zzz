package fxtg.Taobao;

//import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.request.ItemcatsGetRequest;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemcatsGetResponse;
import java.util.*;

/**
 * 这个类用于使用淘宝API，主要通过商品ID（淘宝下为num_iid）来获得有关该商品的具体信息。
 *
 * @author LS
 */
public class FxtgTaobao {

    private static String url = "http://gw.api.taobao.com/router/rest";
    private static String appkey = "21165587";
    private static String secret = "a7ca8bfc1292bd01ee664ff23efda7ba";

    /**
     * 传入一个商品ID（淘宝下的num_iid），通过该ID向淘宝发起请求得到有关该商品的信息。
     *
     * @param num_iid 商品ID（淘宝下的num_iid）
     */
    public static FxtgTaobaoItem getItem(Long num_iid) {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        ItemGetRequest req = new ItemGetRequest();
        req.setFields("num_iid,title,price,props_name,props,pic_url,cid");
        req.setNumIid(num_iid);
        ItemGetResponse res;
        FxtgTaobaoItem item = new FxtgTaobaoItem(num_iid);
        try {
            res = client.execute(req);
            item.setName(res.getItem().getTitle());
            item.setPrice(Double.valueOf(res.getItem().getPrice()));
            item.setCid(res.getItem().getCid());
            item.setPic_url(res.getItem().getPicUrl());
        } catch (Exception | NoClassDefFoundError e) {
            item.err = true;
            return item;
        }
        Long cid = res.getItem().getCid();
        ArrayList<Long> cidAll = new ArrayList<Long>();
        ArrayList<String> cidNameAll = new ArrayList<String>();
        ItemcatsGetRequest qcats = new ItemcatsGetRequest();
        qcats.setFields("cid,parent_cid,name");
        qcats.setCids(cid.toString());
        cidAll.add(cid);
        try {
            ItemcatsGetResponse rcats = client.execute(qcats);
            cidNameAll.add(rcats.getItemCats().get(0).getName());
            item.setCid_name(rcats.getItemCats().get(0).getName());
            Long parCid = rcats.getItemCats().get(0).getParentCid();
            String parName;
            parName = rcats.getItemCats().get(0).getName();
            while (parCid != 0) {
                cidAll.add(0, parCid);
                qcats.setCids(parCid.toString());
                rcats = client.execute(qcats);
                parCid = rcats.getItemCats().get(0).getParentCid();
                parName = rcats.getItemCats().get(0).getName();
                cidNameAll.add(0, parName);
            }
            item.setCidAll(cidAll);
            item.setCidAllName(cidNameAll);
        } catch (Exception | NoClassDefFoundError e) {
            item.err = true;
            return item;
        }
        item.err = false;
        return item;
    }

    /**
     * 传入一个商品ID，返回该商品的名字。
     *
     * @param num_iid 商品ID
     * @return 若该商品存在，返回商品名字，否则返回null
     */
    public static String getItemName(Long num_iid) {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        ItemGetRequest req = new ItemGetRequest();
        req.setFields("num_iid,title");
        req.setNumIid(num_iid);
        ItemGetResponse res;
        try {
            res = client.execute(req);
            if (res.isSuccess()) {
                return res.getItem().getTitle();
            } else {
                return null;
            }
        } catch (Exception | NoClassDefFoundError e) {
            return null;
        }
    }
}
