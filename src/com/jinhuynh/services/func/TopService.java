package com.jinhuynh.services.func;

import com.girlkun.database.GirlkunDB;
import com.jinhuynh.server.Manager;
import com.jinhuynh.utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class TopService implements Runnable{
    private static TopService i;

    public static TopService gI() {
        if (i == null) {
            i = new TopService();
        }
        return i;
    }
    public static String getTopNap() {
        StringBuffer sb = new StringBuffer("");

        String SELECT_TOP_POWER = "SELECT player.name, SUM(account.tongnap) AS tongnap FROM account JOIN player ON account.id = player.account_id GROUP BY player.name ORDER BY tongnap DESC LIMIT 10;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = GirlkunDB.getConnection();
            ps = conn.prepareStatement(SELECT_TOP_POWER);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while(rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("tongnap")).append("\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
     public static String getTopSK() {
        StringBuffer sb = new StringBuffer("");

        String SELECT_TOP_POWER = "SELECT player.name, SUM(account.topsk16) AS topsk16 FROM account JOIN player ON account.id = player.account_id GROUP BY player.name ORDER BY topsk16 DESC LIMIT 10;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = GirlkunDB.getConnection();
            ps = conn.prepareStatement(SELECT_TOP_POWER);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while(rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("topsk16")).append("\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    @Override
    public void run() {
        while(true){
            try{
                if (Manager.timeRealTop + (30 * 60 * 1000) < System.currentTimeMillis()) {
                    Manager.timeRealTop = System.currentTimeMillis();
                    try (Connection con = GirlkunDB.getConnection()) {
                        Manager.topNV = Manager.realTop(Manager.queryTopNV, con);
                        Manager.topSM = Manager.realTop(Manager.queryTopSM, con);
                        Manager.topSK = Manager.realTop(Manager.queryTopSK, con);
                        Manager.topPVP = Manager.realTop(Manager.queryTopPVP, con);
                        Manager.topNHS = Manager.realTop(Manager.queryTopNHS, con);
                    } catch (Exception ignored) {
                        Logger.error("Lỗi đọc top");
                    }
                }
                Thread.sleep(1000);
            }catch (Exception ignored) {
            }
        }
    }

}
