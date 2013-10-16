package fxtg.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * 负责注册的面板。用于弹出对话框让用户输入相关信息以进行注册。
 *
 * @author LS
 */
public class RegPanel extends javax.swing.JPanel {

    private boolean ok;
    private JDialog dialog;

    /**
     * 初始化窗体。
     */
    public RegPanel() {
        initComponents();
    }

    /**
     * 检查输入是否合法。包括用户名、密码的合法性检查。
     *
     * @return 合法则返回true，否则false
     */
    public boolean checkInput() {
        if (userName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pwd.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "密码不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String tmpPWD = new String(pwd.getPassword());
        if (!tmpPWD.equals(new String(pwdComfirm.getPassword()))) {
            JOptionPane.showMessageDialog(this, "两次密码输入不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (userName.getText().length() < 6 || userName.getText().length() > 20) {
            JOptionPane.showMessageDialog(this, " 用户名长度须为6-20字符！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //不区分大小写，一律统一为小写字母
        String tmpName = userName.getText().toLowerCase();
        char firstChar = tmpName.charAt(0);
        if (firstChar < 'a' || firstChar > 'z') {
            JOptionPane.showMessageDialog(this, "用户名首字母应以英文字母开头！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //仅能由字母和数字组成
        for (int i = 0; i != tmpName.length(); i++) {
            if (!(('a' <= tmpName.charAt(i) && tmpName.charAt(i) <= 'z') || ('0' <= tmpName.charAt(i) && tmpName.charAt(i) <= '9'))) {
                JOptionPane.showMessageDialog(this, "用户名应是英文开头的英文和数字混合的字符串！", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        tmpPWD = new String(pwd.getPassword());
        //长度不符合
        if (tmpPWD.length() < 6 || tmpPWD.length() > 10) {
            JOptionPane.showMessageDialog(this, "密码应该是6-10位的纯数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (int i = 0; i != tmpPWD.length(); ++i) {
            if (!('0' <= tmpPWD.charAt(i) && tmpPWD.charAt(i) <= '9')) {
                JOptionPane.showMessageDialog(this, "密码应该是6-10位的纯数字！", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * 在对话框中显示注册面板。
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
        dialog.setTitle("注册");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return ok;
    }

    /**
     * 将对话框中的的信息组合生成一个用户类并返回。
     *
     * @return 用户类
     */
    public User getUser() {
        User user;
        user = new User(userName.getText(), new String(pwd.getPassword()), usrType.getSelectedIndex() + 1);
        user.setContact(usrContact.getText());
        return user;
    }

    /**
     * 此方法在构造函数中被调用以初始化面板。 警告: 此方法为NetBeans自动生成，不应修改。
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        usrType = new javax.swing.JComboBox();
        usrType.addItem("买家（发起团购）");         usrType.addItem("卖家（参与竞价）");
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pwd = new javax.swing.JPasswordField();
        pwdComfirm = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        userName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usrContact = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                while (!checkInput()) {
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

        usrType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "买家（发起团购）", "卖家（参与竞价）" }));
        usrType.setToolTipText("用户类型，可以为买家或卖家");

        jLabel3.setText("用户类型：");

        jLabel2.setText("确认密码：");

        pwd.setToolTipText("6~10位的纯数字，尚不支持忘记密码找回");

        pwdComfirm.setToolTipText("确认密码");

        jLabel4.setText("用户名：");

        userName.setToolTipText("用户名（6~20字符，为英文开头的英文数字混合的字符串（不区分大小写））");

        jLabel5.setText("联系方式（不超过40字符）：");

        usrContact.setColumns(20);
        usrContact.setRows(5);
        usrContact.setToolTipText("不超过40字符的任意字符串");
        usrContact.setLineWrap(true);
        usrContact.setWrapStyleWord(true);
        jScrollPane1.setViewportView(usrContact);

        jLabel1.setText("密码：");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userName, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pwd, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pwdComfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usrType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(userName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwdComfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usrType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addGap(50, 50, 50)
                        .addComponent(btnOK)
                        .addGap(26, 26, 26)
                        .addComponent(btnCancel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPasswordField pwd;
    private javax.swing.JPasswordField pwdComfirm;
    private javax.swing.JTextField userName;
    private javax.swing.JTextArea usrContact;
    private javax.swing.JComboBox usrType;
    // End of variables declaration//GEN-END:variables
}

/**
 * 用户类。用于注册时返回所注册的用户信息。
 *
 * @author LS
 */
class User {

    private String name, password, contact, remark;
    private int type;

    /**
     * 空构造函数。初始化所有成员。
     */
    public User() {
        name = password = contact = remark = "";
        type = 0;
    }

    /**
     * 构造函数。传入用户名、密码及用户类型构造用户类。
     *
     * @param aName 用户名
     * @param aPassword 用户密码
     * @param aType 用户类型
     */
    public User(String aName, String aPassword, int aType) {
        name = aName;
        password = aPassword;
        type = aType;
    }

    /**
     * 返回用户名。
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 返回用户密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 返回联系方式。
     *
     * @return 联系方式
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置用户的联系方式。
     *
     * @param contact 联系方式
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * 返回用户类型。0为买家，1为卖家。
     *
     * @return 用户类型
     */
    public int getType() {
        return type;
    }
}
