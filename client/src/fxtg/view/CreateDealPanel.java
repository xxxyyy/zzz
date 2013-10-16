package fxtg.view;

import fxtg.Taobao.FxtgTaobao;
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
 * 负责发起团购的面板。用于弹出对话框让用户输入相关信息以发起对商品的团购意向。
 *
 * @author LS
 */
public class CreateDealPanel extends javax.swing.JPanel {

    private boolean ok = false;
    private JDialog dialog;

    /**
     * 构造函数。同时设置商品ID的文本框内容。
     *
     * @param id 要设置的ID值，null则文本框内无内容
     */
    public CreateDealPanel(Long id) {
        initComponents();
        if (id != null) {
            IDField.setText(id.toString());
        }
    }

    /**
     * 检查输入合法性。判断商品ID、等待时间的合法性。
     *
     * @return 合法则true，否则false。
     */
    public boolean checkValid() {
        String str = IDField.getText();
        if (str.isEmpty()) {
            JOptionPane.showMessageDialog(this, "商品ID不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                JOptionPane.showMessageDialog(this, "请输入合法的商品ID！", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        Long tmpLong = Long.valueOf(str);
        if (FxtgTaobao.getItemName(tmpLong) == null) {
            JOptionPane.showMessageDialog(this, "该商品ID不存在，请输入合法的商品ID！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int i = (int) buyerWaitTime.getValue();
        if (i < 24 || i > 72) {
            JOptionPane.showMessageDialog(this, "等待时间应为24到72小时！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        i = (int) sellerWaitTime.getValue();
        if (i < 24 || i > 72) {
            JOptionPane.showMessageDialog(this, "等待时间应为24到72小时！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 获得用户输入的商品ID。
     *
     * @return 商品ID
     */
    public Long getItemID() {
        return Long.valueOf(IDField.getText());
    }

    /**
     * 获得用户输入的买家等待时间。
     *
     * @return 买家等待时间
     */
    public int getBuyerWaitTime() {
        return (int) buyerWaitTime.getValue();
    }

    /**
     * 获得用户输入的卖家等待时间。
     *
     * @return 卖家等待时间
     */
    public int getSellerWaitTime() {
        return (int) sellerWaitTime.getValue();
    }

    /**
     * 检查输入是否合法。包括商品ID的合法性检查。
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
        dialog.setTitle("发起团购");
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
        SpinnerModel model2 = new SpinnerNumberModel(24, 24, 72, 1);
        sellerWaitTime = new javax.swing.JSpinner(model2);
        jLabel3 = new javax.swing.JLabel();
        IDField = new javax.swing.JTextField();
        SpinnerModel model1 = new SpinnerNumberModel(24, 24, 72, 1);
        buyerWaitTime = new javax.swing.JSpinner(model1);
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        btnOK.setText("确定");
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                while (!checkValid()) {
                    return;
                }
                ok = true;
                dialog.setVisible(false);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
            }
        });
        btnCancel.setText("取消");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        sellerWaitTime.setToolTipText("等待卖家时间（24~72小时）");

        jLabel3.setText("等待卖家时间：");

        IDField.setToolTipText("商品ID（来源于淘宝的商品ID）");

        buyerWaitTime.setToolTipText("等待买家时间（24~72小时）");

        jLabel2.setText("等待买家时间：");

        jLabel1.setText("要创建团购的商品ID：");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sellerWaitTime)
                    .addComponent(IDField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buyerWaitTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(IDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(buyerWaitTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(sellerWaitTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(btnOK)
                .addGap(42, 42, 42)
                .addComponent(btnCancel)
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
    private javax.swing.JTextField IDField;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JSpinner buyerWaitTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner sellerWaitTime;
    // End of variables declaration//GEN-END:variables
}
