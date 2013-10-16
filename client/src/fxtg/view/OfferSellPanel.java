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
 * 竞标团购的面板。输入订单号、需要竞标的价格进行竞标团购，可以测试出价。
 *
 * @author LS
 */
public class OfferSellPanel extends javax.swing.JPanel {

    private boolean ok;
    private bool test;
    private JDialog dialog;

    /**
     * 初始化构造函数。可通过传入订单号先设置好订单文本框。
     * 
     * @param id 
     */
    public OfferSellPanel(Long id) {
        initComponents();
        if (id != null) {
            orderID.setText(id.toString());
        }
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
     * 获得卖家输入的出价值。
     *
     * @return 出的价格
     */
    public double getPrice() {
        return (double) price.getValue();
    }

    /**
     * 获得订单号。
     *
     * @return 订单号
     */
    public Long getOrderID() {
        return Long.parseLong(orderID.getText());
    }

    /**
     * 在对话框中显示竞标团购的面板。
     *
     * @param parent 面板拥有者
     * @return 检测无误则返回true，否则返回false。
     */
    public boolean showDialog(Component parent, bool test) {
        ok = false;
        this.test = test;
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, true);
        dialog.add(this);
        dialog.getRootPane().setDefaultButton(btnOK);
        dialog.setTitle("竞标团购");
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

        jPanel1 = new javax.swing.JPanel();
        orderID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnTest = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        SpinnerModel model = new SpinnerNumberModel(10.00, 0.01, 100000.00, 0.01);
        price = new javax.swing.JSpinner(model);
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        orderID.setToolTipText("订单号");

        jLabel1.setText("订单号：");

        btnTest.setText("测试出价");
        btnTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                while (!checkValid()) {
                    return;
                }
                test.v = true;
                ok = true;
                dialog.setVisible(false);
            }
        });
        btnTest.setFocusable(false);
        btnTest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel2.setText("我要出价：");

        price.setToolTipText("要出的价格（0.01~100000.00）");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(orderID)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(btnTest)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(orderID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTest)
                .addContainerGap())
        );

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                while (!checkValid()) {
                    return;
                }
                ok = true;
                test.v = false;
                dialog.setVisible(false);
            }
        });
        btnOK.setText("确定出价");

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
                test.v = false;
            }
        });
        btnCancel.setText("取消");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField orderID;
    private javax.swing.JSpinner price;
    // End of variables declaration//GEN-END:variables
}

/**
 * boolean值的一个对象，用于引用传参。
 *
 * @author LS
 */
class bool {

    /**
     * boolean值
     */
    public boolean v;

    public bool() {
    }

    public bool(boolean v) {
        this.v = v;
    }
}
