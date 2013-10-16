package fxtg.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * 修改联系方式的面板，读取原联系方式，并显示出来，修改后点击确定则更新联系方式。
 *
 * @author LS
 */
public class ChangeContactPanel extends javax.swing.JPanel {

    private boolean ok = false;
    private JDialog dialog;

    /**
     * 初始构造函数。同时设置好联系方式文本域中的内容。
     * 
     * @param str 原联系方式
     */
    public ChangeContactPanel(String str) {
        initComponents();
        contactArea.setText(str);
    }

    /**
     * 检查输入合法性。主要检查新的联系方式的长度。
     *
     * @return 合法则true，否则false。
     */
    public boolean checkValid() {
        if (contactArea.getText().length() > 40) {
            JOptionPane.showMessageDialog(this, "联系方式不得超过40字符！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 获得新的联系方式的文本。
     *
     * @return
     */
    public String getContactText() {
        return contactArea.getText();
    }

    /**
     * 在对话框中显示修改联系方式的面板。
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
        dialog.setTitle("修改联系方式");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        contactArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        contactArea.setLineWrap(true);
        contactArea.setWrapStyleWord(true);
        contactArea.setColumns(20);
        contactArea.setRows(5);
        jScrollPane1.setViewportView(contactArea);

        jLabel1.setText("新的联系方式（不超过40字符）：");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(btnOK)
                        .addGap(18, 18, 18)
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
    private javax.swing.JTextArea contactArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
