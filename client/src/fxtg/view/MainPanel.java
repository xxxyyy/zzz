package fxtg.view;

import fxtg.Client.FxtgClient;
import fxtg.FxtgPriceAndAmount;
import fxtg.FxtgRecord;
import fxtg.Taobao.FxtgTaobao;
import fxtg.controller.ButtonController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Timer;

/**
 * 程序的主面板，所有的控件都在这个面板里面。
 *
 * @author LS
 */
public class MainPanel extends JPanel {

    FxtgClient client = null;
    private JTextField fUser, fKey;
    private JPasswordField fPwd;
    /**
     * 通用按钮。
     */
    private JButton btnLogin, btnLogout, btnReg, btnSearch, btnShowDeal, btnContact;
    /**
     * 买家按钮
     */
    private JButton btnCreateDeal, btnJoinDeal, btnCancelBuy, btnShowBuy, btnShowContact;
    /**
     * 卖家按钮
     */
    private JButton btnOfferDeal, btnCancelSell, btnShowOfferPrice;
    private JPanel pnlLeft, pnlRight, pnlLog;
    private RegPanel regDialog = null;
    private CreateDealPanel crtDialog = null;
    private OrderPanel ordPanel = null;
    private JLabel statusLabel;
    private boolean regFail = false;

    /**
     * 构造函数，构造主面板。
     */
    public MainPanel() {
        super(new BorderLayout());
        pnlLeft = new JPanel();
        pnlRight = new JPanel(new BorderLayout());
        pnlLog = new JPanel(new BorderLayout());
        JToolBar statusBar = new JToolBar("状态栏");
        statusBar.add(statusLabel = new JLabel(" "));
        statusBar.setFloatable(false);
        JPanel pnlInput = new JPanel();
        JPanel pnlRightUp = new JPanel();
        JPanel pnlBtn = new JPanel();
        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        pnlLeft.add(pnlLog, BorderLayout.NORTH);
        pnlRight.add(pnlRightUp, BorderLayout.NORTH);
        ordPanel = new OrderPanel();
        pnlRight.add(ordPanel, BorderLayout.CENTER);
        pnlLog.setBorder(BorderFactory.createTitledBorder("用户登录"));
        pnlLog.add(pnlInput, BorderLayout.CENTER);
        pnlLog.add(pnlBtn, BorderLayout.SOUTH);
        pnlInput.setLayout(new GridLayout(2, 2));
        pnlInput.add(new JLabel("用户名："));
        pnlInput.add(fUser = new JTextField(13));
        pnlInput.add(new JLabel("密码："));
        pnlInput.add(fPwd = new JPasswordField());
        btnLogin = new JButton("登录");
        btnLogout = new JButton("注销");
        btnReg = new JButton("注册");
        btnSearch = new JButton("从已有订单搜索");
        btnSearch.setToolTipText("通过搜索商品名的关键字显示符合条件的订单");
        btnShowDeal = new JButton("我的所有订单");
        btnShowDeal.setToolTipText("显示与我有关的所有订单");
        btnCreateDeal = new JButton("发起团购");
        btnCreateDeal.setToolTipText("发起一项团购");
        btnJoinDeal = new JButton("加入团购");
        btnJoinDeal.setToolTipText("可选中一个订单以加入团购");
        btnOfferDeal = new JButton("竞标团购");
        btnOfferDeal.setToolTipText("可选中一个订单以竞标团购");
        btnCancelSell = new JButton("取消竞标");
        btnCancelSell.setToolTipText("可选中一个订单以取消出价");
        btnCancelBuy = new JButton("取消购买");
        btnCancelBuy.setToolTipText("选中一个购买动作以取消购买意向");
        btnCancelBuy.setEnabled(false);
        btnContact = new JButton("修改我的联系方式");
        btnContact.setToolTipText("修改我的联系方式");
        btnShowBuy = new JButton("显示购买");
        btnShowBuy.setToolTipText("选中一个订单以显示有关该订单的购买动作");
        btnShowContact = new JButton("获取卖家联系方式");
        btnShowContact.setToolTipText("获取我的所有成功订单中得标卖家的联系方式");
        btnShowOfferPrice = new JButton("显示我的出价");
        btnShowOfferPrice.setToolTipText("选中一个订单以显示我的出价");
        pnlBtn.add(btnLogin);
        pnlBtn.add(btnReg);
        pnlRightUp.add(fKey = new JTextField(70));
        fKey.setToolTipText("商品名称关键字");
        pnlRightUp.add(btnSearch);
//        client = new FxtgClient();
        client = new FxtgClient("192.168.3.129");
        if (client == null || !client.checkConnection()) {
            setStatusText("连接服务端错误，请检测网络连接！");
            return;
        }
        showDeal(false);
    }

    /**
     * 设置状态栏文字，有5秒持续时间。
     *
     * @param str 要显示的文字。
     */
    private void setStatusText(String str) {
        statusLabel.setText(str);
        Timer t;
        ActionListener taskPerformer;
        taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                statusLabel.setText(" ");
            }
        };
        t = new Timer(5000, taskPerformer);
        t.start();
    }

    /**
     * 登录。从用户名文本框与密码输入框中读取信息进行登录。
     */
    public void login() {
        if (client == null || !client.checkConnection()) {
            setStatusText("连接服务端错误，请检测网络连接！");
            ReConPanel reConPanel = new ReConPanel();
            if (reConPanel.showDialog(this)) {
                if (!client.tryToConnect(reConPanel.getIP())) {
                    JOptionPane.showMessageDialog(this, "连接服务器" + reConPanel.getIP() + "失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }
        if (client == null || client.login(fUser.getText(), new String(fPwd.getPassword())) != 0) {
            setStatusText("登陆错误！");
            return;
        }
        pnlLeft.removeAll();

        JPanel pnlAlign = new JPanel(new GridLayout(6, 1));
        String tmpStr = "<html>  欢迎您，" + fUser.getText() + "<br>";
        JPanel btnPnl1 = new JPanel(new FlowLayout());
        JPanel btnPnl2 = new JPanel(new FlowLayout());
        JPanel btnPnl3 = new JPanel(new FlowLayout());
        JPanel btnPnl4 = new JPanel(new FlowLayout());
        JPanel btnPnl5 = new JPanel(new FlowLayout());
        btnPnl1.add(btnShowDeal);
        btnPnl1.add(btnLogout);
        if (client.getUserType() == 1) { // 买家身份
            tmpStr += "您的身份是：买家</html>";
            btnPnl2.add(btnCreateDeal);
            btnPnl2.add(btnJoinDeal);
            btnPnl3.add(btnShowBuy);
            btnPnl3.add(btnCancelBuy);
            btnPnl4.add(btnContact);
            btnPnl5.add(btnShowContact);

        } else if (client.getUserType() == 2) { // 卖家身份
            tmpStr += "您的身份是：卖家</html>";
            btnPnl2.add(btnOfferDeal);
            btnPnl2.add(btnCancelSell);
            btnPnl3.add(btnShowOfferPrice);
            btnPnl4.add(btnContact);
        }
        pnlAlign.add(new JLabel(tmpStr, JLabel.CENTER), BorderLayout.NORTH);
        pnlAlign.add(btnPnl1);
        pnlAlign.add(btnPnl2);
        pnlAlign.add(btnPnl3);
        pnlAlign.add(btnPnl4);
        pnlAlign.add(btnPnl5);
        pnlLeft.add(pnlAlign, BorderLayout.NORTH);
    }

    /**
     * 注销。直接从服务器端中注销。
     */
    public void logout() {
        if (client != null) {
            client.logout();
        } else {
            setStatusText("连接服务器出错！");
        }
        pnlLeft.removeAll();
        fUser.setText("");
        fPwd.setText("");
        ordPanel.setColumn(null);
        pnlLeft.add(pnlLog, BorderLayout.NORTH);
    }

    /**
     * 注册新用户。可选择注册买家身份或者卖家身份。
     */
    public void register() {
        if (client == null || !client.checkConnection()) {
            setStatusText("连接服务端错误，请检测网络连接！");
            ReConPanel reConPanel = new ReConPanel();
            if (reConPanel.showDialog(this)) {
                if (!client.tryToConnect(reConPanel.getIP())) {
                    JOptionPane.showMessageDialog(this, "连接服务器" + reConPanel.getIP() + "失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }
        User user = null;
        if (!regFail) {
            regDialog = new RegPanel();
        }
        if (regDialog.showDialog(this)) {
            user = regDialog.getUser();
        } else {
            return;
        }
        if (client == null) {
            setStatusText("创建客户端出错，无法注册！");
            return;
        }
        if (client.register(user.getName(), user.getPassword(), user.getType()) == 0) {
            setStatusText("注册成功");
            client.login(user.getName(), user.getPassword());
            if (client.setMyContactWay(user.getContact()) != 0) {
                setStatusText("注册成功但联系方式写入失败！");
            }
            client.logout();
            regFail = false;
        } else {
            setStatusText("注册出错！");
            JOptionPane.showMessageDialog(this, "注册出错！可能此用户名已被使用！", "错误", JOptionPane.ERROR_MESSAGE);
            regFail = true;
        }
    }

    /**
     * 修改联系方式。同时显示原联系方式。
     */
    public void changeContact() {
        btnCancelBuy.setEnabled(false);
        String contact = client.getMyContactWay();
        if (contact == null) {
            setStatusText("获取联系方式失败！");
            return;
        }
        ChangeContactPanel contactPanel = new ChangeContactPanel(contact);
        if (contactPanel.showDialog(this)) {
            contact = contactPanel.getContactText();
        } else {
            return;
        }
        if (client.setMyContactWay(contact) == -1) {
            setStatusText("设置新的联系方式失败！");
            JOptionPane.showMessageDialog(this, "设置新的联系方式失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
        setStatusText("设置新的联系方式成功");
        JOptionPane.showMessageDialog(this, "设置新的联系方式成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 发起团购。输入相应的商品ID和时间限制等进行发起团购。
     *
     * @param id 传入一个商品ID，直接在相应的文本域预置好。
     */
    public void createDeal(Long id) {
        btnCancelBuy.setEnabled(false);
        int buyerTime, sellerTime;
        crtDialog = new CreateDealPanel(id);
        if (crtDialog.showDialog(this)) {
            id = crtDialog.getItemID();
            buyerTime = crtDialog.getBuyerWaitTime();
            sellerTime = crtDialog.getSellerWaitTime();
        } else {
            return;
        }

        id = client.createOrder(id, buyerTime, sellerTime);
        if (id == -1L) {
            setStatusText("发起团购失败！");
            JOptionPane.showMessageDialog(this, "发起团购失败！可能此商品已有团购，不能再发起。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            setStatusText("发起团购成功！");
            JOptionPane.showMessageDialog(this, "发起团购成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
        joinDeal(id, false);
    }

    /**
     * 买家显示某一订单中自己对该订单详细的购买信息。
     */
    public void showBuy() {
        Long id = getSelectedID();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "请选中需要显示购买的订单。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<FxtgPriceAndAmount> rtnData = client.getMyBuyPrice(id);
        if (rtnData == null) {
            JOptionPane.showMessageDialog(this, "操作失败。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Long> arr = new ArrayList<>();
        arr.add(0L);
        ArrayList<FxtgRecord> retFxtgRecord = client.getRecord("", FxtgClient.ALL_STATE, arr, client.getUserID());
        Long tmpID;
        int tmpAmount;
        double tmpDouble;
        String itemName = null;

        for (int i = 0; i < retFxtgRecord.size(); ++i) {
            if (retFxtgRecord.get(i).OrderID.equals(id)) {
                itemName = FxtgTaobao.getItemName(retFxtgRecord.get(i).ItemID);
                break;
            }
        }
        if (itemName == null) {
            itemName = "<获取商品名称失败>";
        }
        String column[] = {"购买动作ID", "订单ID", "商品名称", "您的出价", "您的购买数量"};
        ordPanel.setColumn(column);
        for (int i = 0; i < rtnData.size(); ++i) {
            FxtgPriceAndAmount tmpData = rtnData.get(i);
            tmpID = tmpData.ID;
            tmpAmount = tmpData.amount;
            tmpDouble = tmpData.price;
            Object[] tmp = {tmpID, id, itemName, tmpDouble, tmpAmount};
            ordPanel.addRow(tmp);
        }
        btnCancelBuy.setEnabled(true);
        setStatusText("获得" + rtnData.size() + "条结果");
    }

    /**
     * 显示选中订单中自己对该订单的出价信息。
     */
    public void showOfferPrice() {
        Long id = getSelectedID();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "请选中需要显示出价的订单。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FxtgPriceAndAmount rtnData = client.getMySellPrice(id);
        if (rtnData == null) {
            JOptionPane.showMessageDialog(this, "请确认您选择的订单信息正确。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long sid = client.getSueecssSeller(id);
        String str;
        if (client.getUserID().equals(sid)) {
            str = "您已得标！";
        } else {
            str = "您没有得标！";
        }
        JOptionPane.showMessageDialog(this, "您对订单" + id + "的出价为：￥" + rtnData.price + "。" + str, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 买家取消选中的订单，取消自己的购买意向。
     */
    public void cancelBuy() {
        btnCancelBuy.setEnabled(false);
        Long id = getSelectedID();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "请选中需要取消的购买动作。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rtn = JOptionPane.showConfirmDialog(this, "您将要取消购买动作：" + id, "提示", JOptionPane.INFORMATION_MESSAGE);
        if (rtn != 0) {
            return;
        }
        if (client.cancelBuy(id) == -1) {
            setStatusText("取消购买动作失败");
            JOptionPane.showMessageDialog(this, "取消购买动作失败！请确认该订单处于等待买家状态。", "错误", JOptionPane.ERROR_MESSAGE);
        } else {
            setStatusText("取消成功");
            showDeal(true);
            JOptionPane.showMessageDialog(this, "取消购买成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 卖家取消选中的订单，取消自己的出价。
     */
    public void cancelSell() {
        Long id = getSelectedID();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "请选中需要取消的订单并确保订单处于等待卖家状态且您已出价。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FxtgPriceAndAmount rtnData = client.getMySellPrice(id);
        if (rtnData == null) {
            JOptionPane.showMessageDialog(this, "请确认您选择的订单信息正确。", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int rtn = JOptionPane.showConfirmDialog(this, "您将要取消订单" + id + "的出价。您的出价为：￥" + rtnData.price, "提示", JOptionPane.INFORMATION_MESSAGE);
        if (rtn != 0) {
            return;
        }
        if (client.cancelSell(rtnData.ID) == -1) {
            setStatusText("取消订单失败");
            JOptionPane.showMessageDialog(this, "取消订单失败！请确保订单处于等待卖家状态且您已出价。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 获得成功订单的卖家联系人信息。
     */
    public void getSellerContact() {
        btnCancelBuy.setEnabled(false);
        ArrayList<Long> arr = new ArrayList<>();
        arr.add(0L);
        Long ID = client.getUserID();
        ArrayList<FxtgRecord> retFxtgRecord;
        retFxtgRecord = client.getRecord("", FxtgClient.ALL_STATE, arr, ID);
        String result = new String();

        for (int i = 0; i < retFxtgRecord.size(); ++i) {
            FxtgRecord rcd = retFxtgRecord.get(i);
            if (rcd.OrderState == FxtgRecord.SUCCESSFUL_END) {
                ID = rcd.OrderID;
                result += "订单" + ID + "：";
                result += client.findSellerContactWay(ID);
                result += "\n\n";
            }
        }
        if (result.isEmpty()) {
            result = "无成功订单。\n";
        }
        ShowContact showContact = new ShowContact();
        showContact.setContactText(result);
        showContact.showDialog(this);
    }

    /**
     * 竞标团购。出价以赢得订单的竞标。
     */
    public void offerSell() {
        Long id = getSelectedID();
        double price;
        bool test = new bool(false);
        OfferSellPanel offerSellPanel = new OfferSellPanel(id);
        while (offerSellPanel.showDialog(this, test)) {
            if (test.v) { // 测试出价
                price = offerSellPanel.getPrice();
                id = offerSellPanel.getOrderID();
                int rtn = client.askBuyerAmount(id, price);
                if (rtn == -1) {
                    setStatusText("请求失败！");
                    JOptionPane.showMessageDialog(this, "请求测试出价失败！", "错误", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(offerSellPanel, "对订单" + id + "出价" + price + "将满足" + rtn + "个商品。", "测试出价", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                test.v = true;
                break;
            }
        }
        if (!test.v) {
            return;
        }
        price = offerSellPanel.getPrice();
        id = offerSellPanel.getOrderID();
        if (client.offerSellPrice(id, price) == -1) {
            setStatusText("竞标团购失败！");
            JOptionPane.showMessageDialog(this, "竞标团购失败！请检查输入的订单号是否正确或是否已参加竞标！", "错误", JOptionPane.ERROR_MESSAGE);
        } else {
            setStatusText("竞标团购成功！");
            JOptionPane.showMessageDialog(this, "竞标团购成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 加入团购。传入一个订单号id，若id为null，则表示未选中订单进行加入团购操作。
     *
     * @param id 订单号
     * @param editable 该订单号是否可编辑
     */
    public void joinDeal(Long id, boolean editable) {
        btnCancelBuy.setEnabled(false);
        int amount;
        double price;
        OfferBuyPanel offerBuyPanel = new OfferBuyPanel();
        offerBuyPanel.setIDAndEditable(id, editable);
        if (offerBuyPanel.showDialog(this)) {
            id = offerBuyPanel.getID();
            amount = offerBuyPanel.getAmount();
            price = offerBuyPanel.getPrice();
        } else {
            return;
        }
        if (client.offerBuyPriceAndAmount(id, price, amount) == 0) {
            setStatusText("加入团购成功！");
        } else {
            setStatusText("加入团购失败！");
            JOptionPane.showMessageDialog(this, "加入团购失败！请检查输入的订单号是否正确！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 通过文本域输入商品的关键字进行搜索已有的团购信息。
     */
    public void search() {
        btnCancelBuy.setEnabled(false);
        if (client == null || !client.checkConnection()) {
            setStatusText("连接服务端错误，请检测网络连接！");
            ReConPanel reConPanel = new ReConPanel();
            if (reConPanel.showDialog(this)) {
                if (!client.tryToConnect(reConPanel.getIP())) {
                    JOptionPane.showMessageDialog(this, "连接服务器" + reConPanel.getIP() + "失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }
        showDeal(false);
    }

    /**
     * 获得选中的行中的ID。实质上是获得第1列中的数据。
     *
     * @return ID，null表示没有行被选中
     */
    public Long getSelectedID() {
        return ordPanel.getSelectedID();
    }

    /**
     * 显示订单信息。可以显示当前用户的订单信息，也可以通过搜索按钮搜索商品名称以显示订单信息。
     *
     * @param flag true表示显示当前用户的订单信息，false表示是通过搜索按钮显示订单信息
     */
    public final void showDeal(boolean flag) {
        btnCancelBuy.setEnabled(false);
        ArrayList<Long> arr = new ArrayList<>();
        arr.add(0L);
        Long ID = client.getUserID();
        ArrayList<FxtgRecord> retFxtgRecord;
        if (flag) {
            retFxtgRecord = client.getRecord("", FxtgClient.ALL_STATE, arr, ID);
        } else {
            retFxtgRecord = client.getRecord(fKey.getText(), FxtgClient.ALL_STATE, arr, 0L);
        }
        if (retFxtgRecord == null) {
            setStatusText("获取订单信息失败！");
            JOptionPane.showMessageDialog(this, "获取订单信息失败。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FxtgRecord tmpRd;
        int buyAmount, sellAmount;
        String itemName, orderState = new String();
        Long itmID, orderID;
        Date midDate, startDate, finalDate;
        String column[] = {"订单ID", "商品ID", "商品名", "起始时间", "等待买家结束时间", "最终结束时间", "订单状态"};
        ordPanel.setColumn(column);
        for (int i = 0; i < retFxtgRecord.size(); ++i) {
            tmpRd = retFxtgRecord.get(i);
            buyAmount = tmpRd.buyAmount;
            sellAmount = tmpRd.sellAmount;
            itmID = tmpRd.ItemID;
            orderID = tmpRd.OrderID;
            midDate = tmpRd.OrderMidDate;
            startDate = tmpRd.OrderStartDate;
            finalDate = tmpRd.OrderFinalDate;
            // 区分订单状态
            switch (tmpRd.OrderState) {
                case FxtgRecord.WAITING_BUYER:
                    orderState = "等待买家";
                    break;
                case FxtgRecord.WAITING_SELLER:
                    orderState = "等待卖家";
                    break;
                case FxtgRecord.NOT_ENOUGHT_BUYER:
                    orderState = "买家不足团购被中止";
                    break;
                case FxtgRecord.NOT_ENOUGHT_SELLER:
                    orderState = "没有卖家出价被中止";
                    break;
                case FxtgRecord.SUCCESSFUL_END:
                    orderState = "成功结束";
                    break;
                default:
                    orderState = "未知状态";
                    break;
            }
            itemName = FxtgTaobao.getItemName(itmID);
            if (itemName == null) {
                itemName = "<获取商品名字失败>";
            }
            Object[] tmp = {orderID, itmID, itemName, startDate.toString(), midDate.toString(), finalDate.toString(), orderState};
            ordPanel.addRow(tmp);
        }
        setStatusText("获取到" + retFxtgRecord.size() + "条订单信息");
    }

    /**
     * 替所有按钮设置按钮的监听器。
     *
     * @param buttonController 按钮监听器
     */
    protected void setController(ButtonController buttonController) {
        btnLogin.addActionListener(buttonController);
        btnReg.addActionListener(buttonController);
        btnSearch.addActionListener(buttonController);
        btnShowDeal.addActionListener(buttonController);
        btnCreateDeal.addActionListener(buttonController);
        btnJoinDeal.addActionListener(buttonController);
        btnLogout.addActionListener(buttonController);
        btnOfferDeal.addActionListener(buttonController);
        btnCancelSell.addActionListener(buttonController);
        btnCancelBuy.addActionListener(buttonController);
        btnContact.addActionListener(buttonController);
        btnShowBuy.addActionListener(buttonController);
        btnShowContact.addActionListener(buttonController);
        btnShowOfferPrice.addActionListener(buttonController);
    }
}
