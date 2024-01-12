package com.jinhuynh.server.io;

import java.net.Socket;

import com.jinhuynh.models.player.Player;
import com.jinhuynh.server.Controller;
import com.jinhuynh.data.DataGame;
import com.jinhuynh.jdbc.daos.GodGK;
import com.jinhuynh.models.item.Item;
import com.girlkun.network.session.Session;
import com.girlkun.network.session.TypeSession;
import com.girlkun.network.io.Message;
import com.jinhuynh.server.Client;
import com.jinhuynh.server.Maintenance;
import com.jinhuynh.server.Manager;
import com.jinhuynh.server.model.AntiLogin;
import com.jinchill.services.ItemService;
import com.jinchill.services.MapService;
import com.jinchill.services.Service;
import com.jinhuynh.services.func.ChangeMapService;
import com.jinhuynh.utils.Logger;
import com.jinhuynh.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySession extends Session {

    private static final Map<String, AntiLogin> ANTILOGIN = new HashMap<>();
    public Player player;

    public byte timeWait = 100;

    public boolean connected;
    public boolean sentKey;

    public static final byte[] KEYS = {0};
    public byte curR, curW;

    public String ipAddress;
    public boolean isAdmin;
    public int userId;
    public String uu;
    public String pp;

    public int typeClient;
    public byte zoomLevel;

    public long lastTimeLogout;
    public boolean joinedGame;

    public long lastTimeReadMessage;

    public int actived;

    public int goldBar;
    
    public int hello;

    public int dangu;
    
    public int nro3s;
        public boolean nhanngocxanh;

    public int coinBar;
    public List<Item> itemsReward;
    public String dataReward;
    public boolean is_gift_box;
    public double bdPlayer;

    public int version;
    public int active;
    public long vnd;
    public long coin;
    public int Bar;

    public MySession(Socket socket) {
        super(socket);
        ipAddress = socket.getInetAddress().getHostAddress();
    }

    public void initItemsReward() {
        try {
            this.itemsReward = new ArrayList<>();
            String[] itemsReward = dataReward.split(";");
            for (String itemInfo : itemsReward) {
                if (itemInfo == null || itemInfo.equals("")) {
                    continue;
                }
                String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
                String[] baseInfo = subItemInfo[0].split(":");
                int itemId = Integer.parseInt(baseInfo[0]);
                int quantity = Integer.parseInt(baseInfo[1]);
                Item item = ItemService.gI().createNewItem((short) itemId, quantity);
                if (subItemInfo.length == 2) {
                    String[] options = subItemInfo[1].split(",");
                    for (String opt : options) {
                        if (opt == null || opt.equals("")) {
                            continue;
                        }
                        String[] optInfo = opt.split(":");
                        int tempIdOption = Integer.parseInt(optInfo[0]);
                        int param = Integer.parseInt(optInfo[1]);
                        item.itemOptions.add(new Item.ItemOption(tempIdOption, param));
                    }
                }
                this.itemsReward.add(item);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void sendKey() throws Exception {
        super.sendKey();
        this.startSend();
    }

    public void sendSessionKey() {
        Message msg = new Message(-27);
        try {
            msg.writer().writeByte(KEYS.length);
            msg.writer().writeByte(KEYS[0]);
            for (int i = 1; i < KEYS.length; i++) {
                msg.writer().writeByte(KEYS[i] ^ KEYS[i - 1]);
            }
            this.sendMessage(msg);
            msg.cleanup();
            sentKey = true;
        } catch (Exception e) {
        }
    }

    public void login(String username, String password) {
        AntiLogin al = ANTILOGIN.get(this.ipAddress);
        if (al == null) {
            al = new AntiLogin();
            ANTILOGIN.put(this.ipAddress, al);
        }
        if (!al.canLogin()) {
            Service.gI().sendThongBaoOK(this, al.getNotifyCannotLogin());
            return;
        }
        if (Manager.LOCAL) {
            Service.gI().sendThongBaoOK(this, "Server này chỉ để lưu dữ liệu\nVui lòng qua server khác");
            return;
        }
        if (Maintenance.isRuning) {
            Service.gI().sendThongBaoOK(this, "Server đang trong thời gian bảo trì, vui lòng quay lại sau");
            return;
        }
        if (!this.isAdmin && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
            Service.gI().sendThongBaoOK(this, "Máy chủ hiện đang quá tải, "
                    + "cư dân vui lòng di chuyển sang máy chủ khác.");
            return;
        }
        if (this.player != null) {
            return;
        } else {
            Player player = null;
            try {
                long st = System.currentTimeMillis();
                this.uu = username;
                this.pp = password;
                
                
                player = GodGK.login(this, al);
                if (player != null) {
                    // -77 max small
                    DataGame.sendSmallVersion(this);
                    // -93 bgitem version
                    Service.gI().sendMessage(this, -93, "1630679752231_-93_r");

                    this.timeWait = 0;
                    this.joinedGame = true;
                    player.nPoint.calPoint();
                    player.nPoint.setHp(player.nPoint.hp);
                    player.nPoint.setMp(player.nPoint.mp);
                    player.zone.addPlayer(player);
                    if (player.pet != null) {
                        player.pet.nPoint.calPoint();
                        player.pet.nPoint.setHp(player.pet.nPoint.hp);
                        player.pet.nPoint.setMp(player.pet.nPoint.mp);
                    }

                    player.setSession(this);
                    Client.gI().put(player);
                    this.player = player;
                    //-28 -4 version data game
                    DataGame.sendVersionGame(this);
                    //-31 data item background
                    DataGame.sendDataItemBG(this);
                    Controller.getInstance().sendInfo(this);

                    Logger.warning("Login thành công player " + this.player.name + ": " + (System.currentTimeMillis() - st) + " ms\n");
//                    Service.gI().sendThongBaoOK(this, "Ngọc rồng sao đen sẽ mở lúc 21h hôm nay");
                }
            } catch (Exception e) {
                if (player != null) {
                    player.dispose();
                }
            }
        }
    }
}
