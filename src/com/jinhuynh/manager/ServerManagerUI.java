package com.jinhuynh.manager;

import com.jinhuynh.server.Maintenance;
import com.jinhuynh.utils.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerManagerUI extends JFrame {

    public ServerManagerUI() {
        setTitle("Chương trình Bảo trì");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton maintenanceButton = new JButton("Bảo trì");
        maintenanceButton.addActionListener(e -> showMaintenanceDialog());
        getContentPane().add(maintenanceButton);

        setVisible(true);
    }

    private void showMaintenanceDialog() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Bảo trì", "Bảo trì", dialogButton);
        if(dialogResult == 0) {
            Logger.error("Server tiến hành bảo trì");
            Maintenance.gI().start(15);
        } else {
          
        }
    }
}
