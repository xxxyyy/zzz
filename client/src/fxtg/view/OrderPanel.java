package fxtg.view;

import fxtg.Taobao.FxtgTaobao;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 用来显示订单的面板。该面板只包含一个用于显示订单的表格。
 *
 * @author LS
 */
public class OrderPanel extends JPanel {

    private MyTableModel tableModel;
//     private Object[][] data;
    private JTable table;
//    private ImageIcon icon;

    /**
     * 构造函数。
     */
    public OrderPanel() {
        super(new GridLayout(1, 0));

//        try {
//            icon = new ImageIcon(new URL("http://img01.taobaocdn.com/tps/i1/T1fpK3XhpmXXXXXXXX-70-30.png")); // 百事淘宝
////            icon = new ImageIcon(new URL("http://img02.taobaocdn.com/bao/uploaded/i2/T1pmzvXXXpXXajhgzb_124139.jpg")); //Kinston
////            icon = new ImageIcon(new URL("http://img.alimama.cn/adbrand/adboard/picture/2012-09-12/145465160001120912093412.jpg"));//大图
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        String[] columnNames = {"订单号", "商品名称", "图片", "价格", "数量"};
//        Object[][] data = {
//            {"1234567890", "百事可乐", icon, new Double(3.20), new Integer(2)},
//            {"1234567891", "可口可乐", icon, new Double(3.50), new Integer(23)},
//            {"1234567892", "金士顿U盘", icon, new Double(200.00), new Integer(5)},
//            {"1234567893", "金士顿内存", icon, new Double(120.00), new Integer(2)},
//            {"1234567894", "ThinkPad E420", icon, new Double(4530.00), new Integer(1)}};

//        tableModel = new MyTableModel(data, columnNames);
        tableModel = new MyTableModel(null, null);
        table = new JTable(tableModel);
//        Object[] tmp = {"1234567890", "百事可乐", icon, new Double(3.20), new Integer(2)};
//        tableModel.addRow(tmp);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

//        TableColumn column = table.getColumnModel().getColumn(2);
//        column.setPreferredWidth(icon.getIconWidth());
//        if (icon.getIconHeight() < 1) {
//            System.out.println("设置表格宽度出错！");
//        } else {
//            table.setRowHeight(icon.getIconHeight());
//        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && tableModel.getColumnCount() == 7) {
                    // 取得右键点击所在行
                    int row = e.getY() / table.getRowHeight();
                    if (row > table.getRowCount() - 1 || table.getRowCount() == 0) {
                        return;
                    }
                    Long itemID = Long.parseLong(table.getValueAt(row, 1).toString());
                    String url = FxtgTaobao.getPicUrl(itemID);
                    if (url == null) {
                        return;
                    }
                    ImageIcon icon;
                    try {
                        icon = new ImageIcon(new URL(url));
                    } catch (MalformedURLException ex) {
                        return;
                    }
                    JLabel pic = new JLabel(icon);
                    pic.setSize(icon.getIconWidth(), icon.getIconHeight());
                    JFrame jf = new JFrame("商品" + itemID + "图片展示");
                    jf.add(pic);
                    jf.setSize(icon.getIconWidth(), icon.getIconHeight());
                    jf.setLocationRelativeTo(null);
                    jf.setVisible(true);
                }
            }
        });
    }

    /**
     * 设置表格的列信息。
     *
     * @param column 列信息的数组，包含列的名字
     */
    public void setColumn(String column[]) {
        tableModel = new MyTableModel(null, column);
        table.setModel(tableModel);
    }

    /**
     * 加入一行信息。
     *
     * @param data 所要加入的信息数组
     */
    public void addRow(Object data[]) {
        tableModel.addRow(data);
    }

    /**
     * 获得选中的行中信息的ID。
     *
     * @return ID
     */
    public Long getSelectedID() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return Long.parseLong(table.getValueAt(row, 0).toString());
    }
}

/**
 * 自定义的表格，用于显示订单信息。自定义表格主要是为了显示图片。
 *
 * @author LS
 */
class MyTableModel extends DefaultTableModel {

    /**
     * 构造函数。调用父类的构造函数，传入表格内容。
     *
     * @param data 表格内容
     * @param columnNames 表格列信息
     */
    public MyTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    /**
     * 覆盖父类方法：获得列c的类。之所以能够显示图片主要就是要覆盖父类的此方法。
     *
     * @param c 列的下标
     * @return 列c的类
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * 覆盖父类方法：返回行row，列col是否可编辑。在这里不允许编辑任何内容。
     *
     * @param row 行的下标
     * @param col 列的下标
     * @return 始终是false，表示禁止编辑
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
