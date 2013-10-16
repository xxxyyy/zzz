package fxtg.view;

import java.awt.Component;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * 显示成功订单的卖家联系方式的面板。
 *
 * @author LS
 */
public class ShowContact extends javax.swing.JPanel {

    private JDialog dialog;
    
   /**
    * 初始化构造函数。
    */
    public ShowContact() {
        initComponents();
    }
    
    /**
     * 设置文本框内容。
     * 
     * @param str 要设置的文本内容
     */
    public void setContactText(String str) {
        contactArea.setText(str);
    }

    /**
     * 在对话框中显示成功订单的卖家联系方式面板。
     *
     * @param parent 面板拥有者
     */
    public void showDialog(Component parent) {
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, true);
        dialog.add(this);
        dialog.setTitle("显示成功订单的卖家信息");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * 此方法在构造函数中被调用以初始化面板。
     * 警告: 此方法为NetBeans自动生成，不应修改。
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contactArea = new javax.swing.JTextArea();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "卖家联系方式", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        contactArea.setColumns(20);
        contactArea.setRows(5);
        contactArea.setToolTipText("显示所有成功订单中得标卖家的联系方式");
        jScrollPane1.setViewportView(contactArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea contactArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
