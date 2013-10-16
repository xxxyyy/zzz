package fxtg.view;

import fxtg.controller.ButtonController;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 反向团购程序的主界面窗体类。程序入口在此类中。负责显示程序的主窗口。
 *
 * @author LS
 */
public class Fxtg extends JFrame {

    /**
     * 构造函数，构造主界面窗体。
     */
    public Fxtg() {
        super("反向团购 v0.1");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        MainPanel mainPanel = new MainPanel();
        ButtonController buttonController = new ButtonController(mainPanel);
        mainPanel.setController(buttonController);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * 程序主函数。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try { // 设置Java显示风格为系统显示风格。
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("设置显示风格错误！");
        }
        new Fxtg();
    }
}
