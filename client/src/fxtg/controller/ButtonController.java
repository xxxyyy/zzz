package fxtg.controller;

import fxtg.view.MainPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 按钮的控制类。用于控制面板中的按钮。
 *
 * @author LS
 */
public class ButtonController implements ActionListener {

    private MainPanel mainPanel;

    /**
     * 构造函数。传入主面板，用于调用主面板的方法。
     *
     * @param mainPanel 主面板
     */
    public ButtonController(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * 当有按钮按下时，调用此方法。通过调用的按钮调用相应的方法。
     *
     * @param e 按钮事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "登录":
                mainPanel.login();
                break;
            case "注册":
                mainPanel.register();
                break;
            case "注销":
                mainPanel.logout();
                break;
            case "发起团购":
                mainPanel.createDeal(mainPanel.getSelectedID());
                break;
            case "我的所有订单":
                mainPanel.showDeal(true);
                break;
            case "加入团购":
                mainPanel.joinDeal(mainPanel.getSelectedID(), true);
                break;
            case "从已有订单搜索":
                mainPanel.search();
                break;
            case "竞标团购":
                mainPanel.offerSell();
                break;
            case "修改我的联系方式":
                mainPanel.changeContact();
                break;
            case "取消购买":
                mainPanel.cancelBuy();
                break;
            case "取消竞标":
                mainPanel.cancelSell();
                break;
            case "显示购买":
                mainPanel.showBuy();
                break;
            case "获取卖家联系方式":
                mainPanel.getSellerContact();
                break;
            case "显示我的出价":
                mainPanel.showOfferPrice();
                break;
        }
        mainPanel.updateUI();
    }
}
