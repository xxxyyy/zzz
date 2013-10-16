package fxtg.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * 重新连接的面板。通过输入服务器端的IP进行重新连接。
 *
 * @author LS
 */
public class ReConPanel extends javax.swing.JPanel {

    private boolean ok = false;
    private JDialog dialog;

    /**
     * 初始化面板。
     */
    public ReConPanel() {
        initComponents();
    }

    /**
     * 获取IP文本框中的内容。
     * 
     * @return IP文本框中的内容
     */
    public String getIP(){
        return IPField.getText();
    }
        
    /**
     * 在对话框中显示输入服务器IP的面板。
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
        dialog.setTitle("重新连接服务器");
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        IPField = new javax.swing.JTextField();

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ok = true;
                dialog.setVisible(false);
            }
        });
        btnOK.setText("确定");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("服务器端IP：");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IPField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(btnOK)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IPField;
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
