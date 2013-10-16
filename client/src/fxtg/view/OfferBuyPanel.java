package fxtg.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 加入团购的面板。输入订单号、需要购买的商品数量及单价进行加入团购。
 *
 * @author LS
 */
public class OfferBuyPanel extends javax.swing.JPanel {

    private boolean ok;
    private JDialog dialog;

    /**
     * 构造函数，初始化窗体。
     */
    public OfferBuyPanel() {
        initComponents();
    }

    /**
     * 传入已有的订单号。在订单号的文本框中设置好相应的订单号。
     *
     * @param id 订单号
     * @param editable 订单号的文本框是否可编辑
     */
    public void setIDAndEditable(Long id, boolean editable) {
        if (id != null) {
            orderID.setText(id.toString());
        }
        orderID.setEditable(editable);
    }

    /**
     * 获得订单号。
     *
     * @return 订单号
     */
    public Long getID() {
        return Long.parseLong(orderID.getText());
    }

    /**
     * 获得要购买的商品数量。
     *
     * @return 要购买的商品数量
     */
    public int getAmount() {
        return (int) amount.getValue();
    }

    /**
     * 获得要出的价格。
     *
     * @return 价格
     */
    public double getPrice() {
        return (double) prize.getValue();
    }

    /**
     * 检查输入合法性。主要是订单号的输入合法性检查。
     *
     * @return 合法则true，否则false。
     */
    public boolean checkValid() {
        if (orderID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "订单号不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String id = orderID.getText();
        for (int i = 0; i < id.length(); ++i) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') {
                JOptionPane.showMessageDialog(this, "请输入合法的订单号！", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * 在对话框中显示加入团购的面板。
     *
     * @param parent 面板拥有者
     * @return 检测无误则返回true，否则返回false。
     */
    public boolean showDialog(Component parent) {
        ok = false;
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, true);
        dialog.add(this);
        dialog.getRootPane().setDefaultButton(btnOK);
        dialog.setTitle("加入团购");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return ok;
    }

    /**
     * 此方法在构造函数中被调用以初始化面板。
     * 警告: 此方法为NetBeans自动生成，不应修改。
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SpinnerModel model1 = new SpinnerNumberModel(1, 1, 100000, 1);
        amount = new javax.swing.JSpinner(model1);
        jLabel4 = new javax.swing.JLabel();
        orderID = new javax.swing.JTextField();
        SpinnerModel model2 = new SpinnerNumberModel(10.00, 0.01, 100000.00, 0.01);
        prize = new javax.swing.JSpinner(model2);

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                while (!checkValid()) {
                    return;
                }
                ok = true;
                dialog.setVisible(false);
            }
        });
        btnOK.setText("确定");

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
            }
        });
        btnCancel.setText("取消");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("订单号：");

        jLabel3.setText("商品数量：");

        amount.setToolTipText("商品数量（1~10000）");

        jLabel4.setText("商品单价：");

        orderID.setToolTipText("订单号");

        prize.setToolTipText("商品单价（0.01~100000.00）");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(prize))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(30, 30, 30)
                        .addComponent(orderID, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(orderID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(prize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(btnOK)
                        .addGap(28, 28, 28)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(16, 16, 16))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner amount;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField orderID;
    private javax.swing.JSpinner prize;
    // End of variables declaration//GEN-END:variables
}
