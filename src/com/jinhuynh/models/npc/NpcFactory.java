package com.jinhuynh.models.npc;

import com.jinhuynh.consts.ConstDataEvent;
import com.jinhuynh.consts.ConstMap;

import com.jinchill.services.*;
import com.jinhuynh.consts.ConstNpc;
import com.jinhuynh.consts.ConstPlayer;
import com.jinhuynh.consts.ConstTask;
import com.jinhuynh.jdbc.daos.PlayerDAO;
import com.jinhuynh.kygui.ShopKyGuiService;
import com.jinhuynh.models.map.challenge.MartialCongressService;
import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossManager;
import com.jinhuynh.models.boss.list_boss.NhanBan;
import com.jinhuynh.models.boss.list_boss.Sukienhe.BoCanhCungVuong;
import com.jinhuynh.models.boss.list_boss.Sukienhe.NgaiDem;
import com.jinhuynh.models.clan.Clan;
import com.jinhuynh.models.clan.ClanMember;

import java.util.HashMap;
import java.util.List;

import com.jinhuynh.services.func.ChangeMapService;
import com.jinhuynh.services.func.SummonDragon;

import static com.jinhuynh.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.jinhuynh.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.jinhuynh.services.func.SummonDragon.SHENRON_SAY;

import com.jinhuynh.models.player.Player;
import com.jinhuynh.models.item.Item;
import com.jinhuynh.models.item.Item.ItemOption;
import com.jinhuynh.models.map.BDKB.BanDoKhoBau;
import com.jinhuynh.models.map.BDKB.BanDoKhoBauService;
import com.jinhuynh.models.map.Map;
import com.jinhuynh.models.map.Zone;
import com.jinhuynh.models.map.blackball.BlackBallWar;
import com.jinhuynh.models.map.MapMaBu.MapMaBu;
import com.jinhuynh.models.map.doanhtrai.DoanhTrai;
import com.jinhuynh.models.map.doanhtrai.DoanhTraiService;
import com.jinhuynh.models.player.Inventory;
import com.jinhuynh.models.player.NPoint;
import com.jinhuynh.models.matches.PVPService;
import com.jinhuynh.models.matches.pvp.DaiHoiVoThuat;
import com.jinhuynh.models.matches.pvp.DaiHoiVoThuatService;
import com.jinhuynh.models.shop.ShopServiceNew;
import com.jinhuynh.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.jinhuynh.server.Client;
import com.jinhuynh.server.Maintenance;
import com.jinhuynh.server.Manager;
import com.jinhuynh.services.func.CombineServiceNew;
import com.jinhuynh.services.func.Input;
import com.jinhuynh.services.func.LuckyRound;
import com.jinhuynh.services.func.TopService;
import com.jinhuynh.utils.Logger;
import com.jinhuynh.utils.TimeUtil;
import com.jinhuynh.utils.Util;
import java.util.ArrayList;
import com.jinhuynh.services.func.ChonAiDay;
import static com.jinhuynh.services.func.Input.LOAI_THE;
import static com.jinhuynh.services.func.Input.MENH_GIA;
import com.jinhuynh.utils.SkillUtil;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class NpcFactory {

    private static final int COST_HD = 50000000;
    public static String LOAI_THE;
    public static String MENH_GIA;
    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;

    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    private static Npc trungLinhThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi Trứng Linh thú cần:\b|7|X99 Hồn Linh Thú + 50K ngọc hồng", "Đổi Trứng\nLinh thú", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 104) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 2029);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ 99 Hồn Linh thú");
                                    } else if (player.inventory.ruby < 5_000) {
                                        this.npcChat(player, "Bạn không đủ 5K hồng ngọc");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.ruby -= 5_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 99);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 2028);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Trứng Linh thú");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc poTaGe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đa vũ trụ song song \b|7|Con muốn gọi con trong đa vũ trụ \b|1|Với giá 5000 hồng ngọc không?", "Gọi Boss\nNhân bản", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Boss oldBossClone = BossManager.gI().getBossById(Util.createIdBossClone((int) player.id));
                                    if (oldBossClone != null) {
                                        this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldBossClone.zone.zoneId);
                                    } else if (player.inventory.ruby < 5_000) {
                                        this.npcChat(player, "Nhà ngươi không đủ 5000 Ngọc Hồng ");
                                    } else {
                                        List<Skill> skillList = new ArrayList<>();
                                        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                            Skill skill = player.playerSkill.skills.get(i);
                                            if (skill.point > 0) {
                                                skillList.add(skill);
                                            }
                                        }
                                        int[][] skillTemp = new int[skillList.size()][3];
                                        for (byte i = 0; i < skillList.size(); i++) {
                                            Skill skill = skillList.get(i);
                                            if (skill.point > 0) {
                                                skillTemp[i][0] = skill.template.id;
                                                skillTemp[i][1] = skill.point;
                                                skillTemp[i][2] = skill.coolDown;
                                            }
                                        }
//                                        BossData bossDataClone = new BossData(
//                                                "Nhân Bản" + player.name,
//                                                player.gender,
//                                                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.getAura(), player.getEffFront()}, player.nPoint.dame,
//                                                new long[]{player.nPoint.hpMax - 1},
//                                                new int[]{140},
//                                                skillTemp,
//                                                new String[]{"|-2|Boss nhân bản đã xuất hiện rồi"}, //text chat 1
//                                                new String[]{"|-1|Ta sẽ chiếm lấy thân xác của ngươi hahaha!"}, //text chat 2
//                                                new String[]{"|-1|Lần khác ta sẽ xử đẹp ngươi"}, //text chat 3
//                                                60
//                                        );

//                                        try {
//                                            new NhanBan(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
                                        //trừ vàng khi gọi boss
                                        player.inventory.ruby -= 5_000;
                                        Service.getInstance().sendMoney(player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc Giuma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 153) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi có muốn tiến vào map để up mảnh vỡ và mảnh hồn bông tai hay không ?", "Dồng Ý", "Đóng");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 153) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapInYard(player, 157, -1, 552);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////NPC Nami///////////////////////////////////////////
    public static Npc nami(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nChopper tôi sẽ đưa cậu đi",
                                "Đi thôi", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                break;
                        }
                    }
                }
            }
        };
    }

    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void chatWithNpc(Player player) {
                String[] chat = {
                    "Là lá la",
                    "La lá là",
                    "Lá là la"
                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 10000, 10000);
            }

            @Override
            public void openBaseMenu(Player player) {
                chatWithNpc(player);
                Item ruacon = InventoryServiceNew.gI().findItemBag(player, 874);
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (ruacon != null && ruacon.quantity >= 1) {
                            this.createOtherMenu(player, 12, "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
                                    "Giao\nRùa con", "Nói chuyện", "SKIP QUEST", "Đi đến\nĐảo Kho Báu");

                        } else {

                            this.createOtherMenu(player, 13, "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
                                    "Nói chuyện", "SKIP QUEST", "Đi đến\nĐảo Kho Báu",
                                    "Đổi quà\nsự kiện",
                                    "Tặng Bọ Cánh Cứng",
                                    "Tặng\nNgài Đêm");

                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == 12) {
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, 5,
                                        "Cảm ơn cậu đã cứu con rùa của ta\n Để cảm ơn ta sẽ tặng cậu món quà.",
                                        "Nhận quà", "Đóng");
                                break;
                            case 1:
                                this.createOtherMenu(player, 6,
                                        "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
                                        "Về khu\nvực bang", "Giải tán\nBang hội", "Kho Báu\ndưới biển");
                                break;
                            case 2:
                                if (TaskService.gI().getIdTask(player) > ConstTask.TASK_18_0) {
                                    Service.gI().sendThongBao(player, "Cày đeeeeeeeeeeeeeeeeee");
                                    return;
                                } else {
                                    TaskService.gI().sendNextTaskMain(player);
                                    break;
                                }
                            case 3:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 177, -1, 1560);
                                break;
//                            case 3:
//                                if (ConstDataEvent.isRunningSK16) {
//                                    if (player.getSession() != null) {
//                                        this.createOtherMenu(player, 9898, "Đổi Quà Sự Kiện"
//                                                + "\n " + ConstDataEvent.slVoOcSK + " vỏ ốc= 1 item vỏ ốc sưk kiện (20%HP)\" +\n"
//                                                + "\n " + ConstDataEvent.slSaoBienSK + " sao biển = 1 item sao biển  sự kiện(20% KI, TNSM)\" +\n"
//                                                + "\n " + ConstDataEvent.slConCuaSK + " Con Cua = 1 item con cua sự kiện (20%sd)\" +\n"
//                                                + " \n Các Item có tác dụng 30p", "Đổi vỏ ốc", "Đổi sao biển", "Đổi con cua");
//                                    }
//                                } else {
//                                    Service.gI().sendThongBao(player, "Đã kết thúc sự kiện");
//                                }
//                                break;
                            case 4:
                                if (ConstDataEvent.isRunningSK16) {
                                    if (player.getSession() != null) {
                                        this.createOtherMenu(player, 9897, "Đổi rương báu vật, được nhiều phần quà hấp dẫn", "Đổi ruương báu vật loại 1", "Đổi ruương báu vật loại 2");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Đã kết thúc sự kiện");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 9898) {
                        switch (select) {
                            case 0:
                                Item voOc = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                                if (voOc != null) {
                                    if (voOc.quantity < ConstDataEvent.slVoOcSK) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vỏ ốc");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, voOc, ConstDataEvent.slVoOcSK);
                                        Item voOcSK = ItemService.gI().createNewItem((short) ConstDataEvent.idVoOc);
                                        InventoryServiceNew.gI().addItemBag(player, voOcSK);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 vỏ ốc sự kiện");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không tim thấy ");
                                }
                                break;
                            case 1:
                                Item saobien = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                                if (saobien != null) {
                                    if (saobien.quantity < ConstDataEvent.slSaoBienSK) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ con cua");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, saobien, ConstDataEvent.slSaoBienSK);
                                        Item saoBienSK = ItemService.gI().createNewItem((short) ConstDataEvent.idSaoBien);
                                        InventoryServiceNew.gI().addItemBag(player, saoBienSK);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 sao biển sự kiện");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không tim thấy ");
                                }
                                break;
                            case 2:
                                Item concua = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                                if (concua != null) {
                                    if (concua.quantity < ConstDataEvent.slConCuaSK) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ con cua");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, concua, ConstDataEvent.slConCuaSK);
                                        Item saoBienSK = ItemService.gI().createNewItem((short) ConstDataEvent.idConCua);
                                        InventoryServiceNew.gI().addItemBag(player, saoBienSK);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 cua sự kiện");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không tìm thấy con cua");
                                }
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == 9897) {
                        switch (select) {
                            case 0:
                                Item voOc = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                                Item saobien = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                                Item concua = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                                if (voOc != null && saobien != null && concua != null) {
                                    int sl = 99;
                                    if (voOc.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ vỏ óc");
                                        return;
                                    }
                                    if (saobien.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ sao biển");
                                        return;
                                    }
                                    if (concua.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ con cua");
                                        return;
                                    }
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, voOc, sl);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, saobien, sl);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, concua, sl);
                                    Item ruong1 = ItemService.gI().createNewItem((short) ConstDataEvent.ruong1);
                                    InventoryServiceNew.gI().addItemBag(player, ruong1);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + ruong1.template.name);
                                    if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 100)) {
                                        Service.gI().sendThongBao(player, "Bạn đã nhậnd được 100 diem");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
                                }
                                break;
                            case 1:
                                Item voOc2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                                Item saobien2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                                Item concua2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                                if (voOc2 != null && saobien2 != null && concua2 != null) {
                                    int sl = 49;
                                    if (voOc2.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ vỏ óc");
                                        return;
                                    }
                                    if (saobien2.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ sao biển");
                                        return;
                                    }
                                    if (concua2.quantity < sl) {
                                        Service.gI().sendThongBao(player, "Không đủ con cua");
                                        return;
                                    }
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, voOc2, sl);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, saobien2, sl);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, concua2, sl);
                                    Item ruong2 = ItemService.gI().createNewItem((short) ConstDataEvent.ruong2);
                                    InventoryServiceNew.gI().addItemBag(player, ruong2);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + ruong2.template.name);
                                    if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 50)) {
                                        Service.gI().sendThongBao(player, "Bạn đã nhậnd được 50 d");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
                                }
                                break;
                        }

                    } else if (player.iDMark.getIndexMenu() == 5) {
                        switch (select) {
                            case 0:
                                try {
                                Item RuaCon = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 874);
                                if (RuaCon != null) {
                                    if (RuaCon.quantity >= 1 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                        int randomItem = Util.nextInt(6); // Random giữa 0 và 1
                                        if (randomItem == 0) {
                                            Item VatPham = ItemService.gI().createNewItem((short) (865));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, 20));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(14, 5));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 7));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
                                        } else if (randomItem == 1) {
                                            Item VatPham = ItemService.gI().createNewItem((short) (865));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, 20));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(14, 5));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 14));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
                                        } else if (randomItem == 2) {
                                            Item VatPham = ItemService.gI().createNewItem((short) (865));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, 20));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, 10));
                                            VatPham.itemOptions.add(new Item.ItemOption(14, 5));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 30));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
                                        } else if (randomItem == 3) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 733);
                                            VatPham.itemOptions.add(new Item.ItemOption(84, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 7));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
                                        } else if (randomItem == 4) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 733);
                                            VatPham.itemOptions.add(new Item.ItemOption(84, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 14));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
                                        } else if (randomItem == 5) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 733);
                                            VatPham.itemOptions.add(new Item.ItemOption(84, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, 14));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
                                        } else {
                                            Item VatPham = ItemService.gI().createNewItem((short) 16);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Ngọc rồng 3 sao", "Ok");
                                        }
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, RuaCon, 1);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                            default:
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 6) {
                        switch (select) {
                            case 0:
                                if (player.getSession().player.nPoint.power >= 80000000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                }
                                break;
                            case 1:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.gI().sendThongBao(player, "Bang phải còn một người");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.gI().sendThongBao(player, "Phải là bảng chủ");
                                            break;
                                        }
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                "Đồng ý", "Từ chối!");
                                    }
                                    break;
                                }
                                Service.gI().sendThongBao(player, "bạn đã có bang hội đâu!!!");
                                break;
                            case 2:
                                if (player.clan != null) {
                                    if (player.clan.BanDoKhoBau != null) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                                "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                                + player.clan.BanDoKhoBau.level + "\nCon có muốn đi theo không?",
                                                "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                                "Đây là bản đồ kho báu x4 tnsm\nCác con cứ yên tâm lên đường\n"
                                                + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                "Chọn\ncấp độ", "Từ chối");
                                    }
                                } else {
                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                    ChangeMapService.gI().goToDBKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                    Input.gI().createFormChooseLevelBDKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                        switch (select) {
                            case 0:
                                BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                break;
                        }
                    }
                }
                if (player.iDMark.getIndexMenu() == 13) {
                    //item sk he
                    Item[] items = new Item[2];
                    items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2127);
                    items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2128);
                    switch (select) {
                        case 0:
                            this.createOtherMenu(player, 7,
                                    "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
                                    "Về khu\nvực bang", "Giải tán\nBang hội", "Kho Báu\ndưới biển", "Đổi Item Sự Kiện", "Đổi Rương Sự Kiện");
                            break;
                        case 1:
                            Service.gI().sendThongBao(player, "Còn Cái Nịt");
                            break;
//                            if (TaskService.gI().getIdTask(player) > ConstTask.TASK_18_0) {
//                                Service.gI().sendThongBao(player, "Cày đeeeeeeeeeeeeeeeeee");
//                                return;
//                            } else {
//                                TaskService.gI().sendNextTaskMain(player);
//                                break;
//                            }
                        case 2:
                            if (player.nPoint.power < 60000000000L) {
                                Service.gI().sendThongBao(player, "Hãy Đạt đủ 60 tỉ sức mạnh trước");
                            } else {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 177, -1, 1560);
                            }
                            break;
                        case 3:
                            this.createOtherMenu(player, ConstNpc.SU_KIEN_HE,
                                    "Con hãy chọn các món quà sau đây\n"
                                    + "1) Hoa hồng vàng hoặc đỏ hạn sử dụng - vĩnh viễn\n"
                                    + "2) Cá chà bá hoặc Cây nắp ấm hạn sử dụng - vĩnh viễn\n"
                                    + "3) Phượng hoàng lửa hoặc Rùa phun lửa hạn sử dụng\n"
                                    + "4) Bong bóng heo hoặc Gậy thượng đế hạn sử dụng\n"
                                    + "5) Pet giới hạn đặc biệt Ngài đêm hoặc pet bọ cánh cứng hạn sử dụng - vĩnh viễn (Tỉ lệ cao)",
                                    "(1)\nx99 Vỏ ốc", "(2)\nx99 Vỏ sò", "(3)\nx99 Con cua", "(4)\nx99 Sao biển", "(5)\nx99 Tất cả\n10 thỏi vàng");
                            break;
                        case 4:
                            int[] itemDos = new int[]{1007, 994, 1000, 1007, 998, 997, 1023};
                            int randomDo = new Random().nextInt(itemDos.length);
                            if (items[0] != null && items[0].quantity >= 99) {
                                Item itemrand = ItemService.gI().createNewItem((short) (itemDos[randomDo]));
                                itemrand.itemOptions.clear();
                                itemrand.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(30, 1));
                                if (Util.isTrue(90, 100)) {
                                    itemrand.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 4)));
                                }
                                InventoryServiceNew.gI().addItemBag(player, itemrand);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + itemrand.template.name);
                            } else {
                                Service.getInstance().sendThongBao(player, "Không tìm thấy vật phẩm");
                            }
                            break;
                        case 5:
                            int[] itemDos1 = new int[]{1007, 994, 1000, 1007, 998, 997, 1023, 2058};
                            int randomDo1 = new Random().nextInt(itemDos1.length);
                            if (items[1] != null && items[1].quantity >= 99) {
                                Item itemrand = ItemService.gI().createNewItem((short) (itemDos1[randomDo1]));
                                itemrand.itemOptions.clear();
                                itemrand.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 13)));
                                itemrand.itemOptions.add(new Item.ItemOption(30, 1));
                                if (Util.isTrue(90, 100)) {
                                    itemrand.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 4)));
                                }
                                InventoryServiceNew.gI().addItemBag(player, itemrand);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + itemrand.template.name);
                            } else {
                                Service.getInstance().sendThongBao(player, "Không tìm thấy vật phẩm");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.SU_KIEN_HE) {
                    Item[] items = new Item[5];
                    items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 695);
                    items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 696);
                    items[2] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 697);
                    items[3] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 698);
                    items[4] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 457);
                    switch (select) {
                        case 0:
                            if (items[0] != null && items[0].quantity >= 99) {
                                Item Hoahongrd = ItemService.gI().createNewItem((short) (Util.nextInt(954, 955)));
                                Hoahongrd.itemOptions.clear();
                                Hoahongrd.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 16)));
                                Hoahongrd.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 13)));
                                Hoahongrd.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 13)));
                                if (Util.isTrue(90, 100)) {
                                    Hoahongrd.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 4)));
                                }
                                Hoahongrd.itemOptions.add(new Item.ItemOption(30, 0));
                                InventoryServiceNew.gI().addItemBag(player, Hoahongrd);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + Hoahongrd.template.name);

                            }
                            break;
                        case 1:
                            if (items[1] != null && items[1].quantity >= 99) {
                                Item itemdl = ItemService.gI().createNewItem((short) (Util.nextInt(2060, 2061)));
                                itemdl.itemOptions.clear();
                                itemdl.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 16)));
                                itemdl.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 13)));
                                itemdl.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 20)));
                                if (Util.isTrue(90, 100)) {
                                    itemdl.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 4)));
                                }
                                itemdl.itemOptions.add(new Item.ItemOption(30, 0));
                                InventoryServiceNew.gI().addItemBag(player, itemdl);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + itemdl.template.name);
                            }
                            break;
                        case 2:
                            int[] itemDos = new int[]{897, 2135};
                            int randomDo = new Random().nextInt(itemDos.length);
                            if (items[2] != null && items[2].quantity >= 99) {
                                Item itembayrd = ItemService.gI().createNewItem((short) (itemDos[randomDo]));
                                itembayrd.itemOptions.clear();
                                itembayrd.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 16)));
                                itembayrd.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 20)));
                                itembayrd.itemOptions.add(new Item.ItemOption(93, Util.nextInt(5, 13)));

                                itembayrd.itemOptions.add(new Item.ItemOption(30, 0));
                                InventoryServiceNew.gI().addItemBag(player, itembayrd);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + itembayrd.template.name);
                            }
                            break;
                        case 3:
                            if (items[3] != null && items[3].quantity >= 99) {
                                Item Hoahongrd = ItemService.gI().createNewItem((short) (Util.nextInt(2136, 2137)));
                                Hoahongrd.itemOptions.clear();
                                Hoahongrd.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 16)));
                                Hoahongrd.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 20)));
                                Hoahongrd.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 20)));

                                Hoahongrd.itemOptions.add(new Item.ItemOption(93, Util.nextInt(4, 5)));

                                Hoahongrd.itemOptions.add(new Item.ItemOption(30, 0));
                                InventoryServiceNew.gI().addItemBag(player, Hoahongrd);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[3], 99);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + Hoahongrd.template.name);
                            }
                            break;
                        case 4:

                            if (items != null && items[0] != null && items[0].quantity >= 99
                                    && items[1] != null && items[1].quantity >= 99
                                    && items[2] != null && items[2].quantity >= 99
                                    && items[3] != null && items[3].quantity >= 99
                                    && items[4] != null && items[4].quantity >= 10) {
                                Item petrd = ItemService.gI().createNewItem((short) (Util.nextInt(2122, 2123)));
                                petrd.itemOptions.clear();
                                petrd.itemOptions.add(new Item.ItemOption(50, Util.nextInt(7, 22)));
                                petrd.itemOptions.add(new Item.ItemOption(77, Util.nextInt(7, 22)));
                                petrd.itemOptions.add(new Item.ItemOption(103, Util.nextInt(7, 22)));

                                if (Util.isTrue(75, 100)) {
                                    petrd.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 5)));
                                }
                                petrd.itemOptions.add(new Item.ItemOption(30, 0));
                                InventoryServiceNew.gI().addItemBag(player, petrd);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 99);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 99);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 99);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[3], 99);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, items[4], 10);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + petrd.template.name);
                                if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 1)) {
                                    Service.gI().sendThongBao(player, "Bạn đã nhậnd được 1 điểm sự kiện");
                                }
                                return;
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ vật phẩm");
                            }

                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 7) {
                    switch (select) {
                        case 0:
                            if (player.getSession().player.nPoint.power >= 80000000000L) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                            }
                            break;
                        case 1:
                            Clan clan = player.clan;
                            if (clan != null) {
                                ClanMember cm = clan.getClanMember((int) player.id);
                                if (cm != null) {
                                    if (clan.members.size() > 1) {
                                        Service.gI().sendThongBao(player, "Bang phải còn một người");
                                        break;
                                    }
                                    if (!clan.isLeader(player)) {
                                        Service.gI().sendThongBao(player, "Phải là bảng chủ");
                                        break;
                                    }
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                            "Đồng ý", "Từ chối!");
                                }
                                break;
                            }
                            Service.gI().sendThongBao(player, "bạn đã có bang hội đâu!!!");
                            break;
                        case 2:
                            if (player.clan != null) {
                                if (player.clan.BanDoKhoBau != null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                            "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                            + player.clan.BanDoKhoBau.level + "\nCon có muốn đi theo không?",
                                            "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                            "Đây là bản đồ kho báu x4 tnsm\nCác con cứ yên tâm lên đường\n"
                                            + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                            "Chọn\ncấp độ", "Từ chối");
                                }
                            } else {
                                this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                    switch (select) {
                        case 0:
                            if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                ChangeMapService.gI().goToDBKB(player);
                            } else {
                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                    switch (select) {
                        case 0:
                            if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                Input.gI().createFormChooseLevelBDKB(player);
                            } else {
                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                    switch (select) {
                        case 0:
                            BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                            break;
                        case 3:
                            if (ConstDataEvent.isRunningSK16) {
                                if (player.getSession() != null) {
                                    this.createOtherMenu(player, 9898, "Đổi Quà Sự Kiện"
                                            + "\n " + ConstDataEvent.slVoOcSK + " vỏ ốc= 1 item vỏ ốc sưk kiện (20%HP)\" +\n"
                                            + "\n " + ConstDataEvent.slSaoBienSK + " sao biển = 1 item sao biển  sự kiện(20% KI, TNSM)\" +\n"
                                            + "\n " + ConstDataEvent.slConCuaSK + " Con Cua = 1 item con cua sự kiện (20%sd)\" +\n"
                                            + " \n Các Item có tác dụng 30p", "Đổi vỏ ốc", "Đổi sao biển", "Đổi con cua");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Đã kết thúc sự kiện");
                            }
                            break;
                        case 4:
                            if (ConstDataEvent.isRunningSK16) {
                                if (player.getSession() != null) {
                                    this.createOtherMenu(player, 9897, "Đổi rương báu vật, được nhiều phần quà hấp dẫn", "Đổi ruương báu vật loại 1", "Đổi ruương báu vật loại 2");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Đã kết thúc sự kiện");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 9898) {
                    switch (select) {
                        case 0:
                            Item voOc = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                            if (voOc != null) {
                                if (voOc.quantity < ConstDataEvent.slVoOcSK) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vỏ ốc");
                                    return;
                                } else {
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, voOc, ConstDataEvent.slVoOcSK);
                                    Item voOcSK = ItemService.gI().createNewItem((short) ConstDataEvent.idVoOc_SK);
                                    InventoryServiceNew.gI().addItemBag(player, voOcSK);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 vỏ ốc sự kiện");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Không tim thấy ");
                            }
                            break;
                        case 1:
                            Item saobien = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                            if (saobien != null) {
                                if (saobien.quantity < ConstDataEvent.slSaoBienSK) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ con cua");
                                    return;
                                } else {
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, saobien, ConstDataEvent.slSaoBienSK);
                                    Item saoBienSK = ItemService.gI().createNewItem((short) ConstDataEvent.idSaoBien_SK);
                                    InventoryServiceNew.gI().addItemBag(player, saoBienSK);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 sao biển sự kiện");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Không tim thấy ");
                            }
                            break;
                        case 2:
                            Item concua = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                            if (concua != null) {
                                if (concua.quantity < ConstDataEvent.slConCuaSK) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ con cua");
                                    return;
                                } else {
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, concua, ConstDataEvent.slConCuaSK);
                                    Item saoBienSK = ItemService.gI().createNewItem((short) ConstDataEvent.idConCua_Sk);
                                    InventoryServiceNew.gI().addItemBag(player, saoBienSK);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa đổi được 1 cua sự kiện");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Không tìm thấy con cua");
                            }
                            break;

                    }
                } else if (player.iDMark.getIndexMenu() == 9897) {
                    switch (select) {
                        case 0:
                            Item voOc = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                            Item saobien = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                            Item concua = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                            if (voOc != null && saobien != null && concua != null) {
                                int sl = 99;
                                if (voOc.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ vỏ óc");
                                    return;
                                }
                                if (saobien.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ sao biển");
                                    return;
                                }
                                if (concua.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ con cua");
                                    return;
                                }
                                InventoryServiceNew.gI().subQuantityItemsBag(player, voOc, sl);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, saobien, sl);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, concua, sl);
                                Item ruong1 = ItemService.gI().createNewItem((short) ConstDataEvent.ruong1);
                                InventoryServiceNew.gI().addItemBag(player, ruong1);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + ruong1.template.name);
                                if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 100)) {
                                    Service.gI().sendThongBao(player, "Bạn đã nhậnd được 100 diem");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
                            }
                            break;
                        case 1:
                            Item voOc2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idVoOc);
                            Item saobien2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idSaoBien);
                            Item concua2 = InventoryServiceNew.gI().findItemBag(player, ConstDataEvent.idConCua);
                            if (voOc2 != null && saobien2 != null && concua2 != null) {
                                int sl = 49;
                                if (voOc2.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ vỏ óc");
                                    return;
                                }
                                if (saobien2.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ sao biển");
                                    return;
                                }
                                if (concua2.quantity < sl) {
                                    Service.gI().sendThongBao(player, "Không đủ con cua");
                                    return;
                                }
                                InventoryServiceNew.gI().subQuantityItemsBag(player, voOc2, sl);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, saobien2, sl);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, concua2, sl);
                                Item ruong2 = ItemService.gI().createNewItem((short) ConstDataEvent.ruong2);
                                InventoryServiceNew.gI().addItemBag(player, ruong2);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhậnd được " + ruong2.template.name);
                                if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 50)) {
                                    Service.gI().sendThongBao(player, "Bạn đã nhậnd được 50 d");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
                            }
                            break;
                    }
                }
            }

        };
    }

    private static Npc nrojinchill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void chatWithNpc(Player player) {
                String[] chat = {
                    "Chào mừng tới với NRO JINCHILL",
                    "Qua npc Satan để đổi VND"

                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 10000, 10000);
            }

            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Mày muốn cái chóaaaaaaaaaaaa gì ",
                                "Đi thôi", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                break;
                        }
                    }
                }
            }
        };
    }

    private static Npc thodaica(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            public void chatWithNpc(Player player) {
                String[] chat = {
                    "Chào mừng tới với NRO ON",
                    "Qua npc Satan để đổi VND"

                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 10000, 10000);
            }

            @Override
            public void openBaseMenu(Player player) {
                chatWithNpc(player);
                Item carot = InventoryServiceNew.gI().findItemBag(player, 192);
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (carot != null && carot.quantity >= 10) {
                            this.createOtherMenu(player, 12, "\b|2|Kiếm đủ cà rốt rồi à ? ta sẽ cho ngươi phần thưởng !",
                                    "Nhận Quà", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "\b|3|Muốn rèn thỏi vàng khóa sang thường hả ?",
                                    "Rèn Vàng Khóa", "ĐÓNG");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == 12) {
                        switch (select) {
                            case 0:
                                this.createOtherMenu(player, 5,
                                        "Cảm ơn cậu đã cứu bụng đói của ta\n Để cảm ơn ta sẽ tặng cậu món quà.",
                                        "Đổi 500000 Ngọc Xanh", "Đổi 10K Hồng Ngọc", "Đổi 5 Bản Đồ Kho Báu");
                                break;
                            case 1:
//                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                        "Mang cho ta 10 cà rốt thì may ra menu đổi quà mới hiện nha bé !!!",
//                                        "Đổi Vàng Khóa");
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == 5) {
                        switch (select) {
                            case 0:

                                //if (!player.gift.gemTanThu) {
                                if (true) {
                                    Item carot = InventoryServiceNew.gI().findItemBag(player, 192);
                                    player.inventory.gem += 500000;
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, carot, 10);
                                    Service.getInstance().sendMoney(player);

                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 500000 ngọc xanh");
                                    player.gift.gemTanThu = true;
                                }
                                break;

                            case 1:
                                if (true) {
                                    Item carot = InventoryServiceNew.gI().findItemBag(player, 192);
                                    player.inventory.ruby += 10000;
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, carot, 10);
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 10K hồng ngọc");
                                    player.gift.gemTanThu = true;
                                }
                                break;
                            case 2:
                                if (true) {
                                    Item carot = InventoryServiceNew.gI().findItemBag(player, 192);
                                    Item bdkb = ItemService.gI().createNewItem((short) 611, (short) 5);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, carot, 10);
                                    InventoryServiceNew.gI().addItemBag(player, bdkb);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhận được 5 Bản Đồ Kho Báu");
                                }
                                break;

                        }

                    }
                    if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                        switch (select) {
                            case 0:
                                if (player.session.actived == 1) {
                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                } else {

//                                    Input.gI().DOITHOI(player);
                                }
//                                break;
//                            case 1:
//                                break;
                        }

//                    } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
//                        Input.gI().createFormNapThe(player, loaiThe, menhGia);
//            
                    }
                }
            }
        };
    }

    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con cố gắng theo %1 học thành tài, đừng lo lắng cho ta."
                                        .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta"),
                                "Đổi mật khẩu", "Nhận 10TR \n ngọc xanh", "GiftCode");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Input.gI().createFormChangePassword(player);
                                break;
                            case 1:
                                if (!player.getSession().nhanngocxanh) {
                                    player.inventory.gem = 10000000;

                                    Service.getInstance().sendMoney(player);

                                    player.getSession().nhanngocxanh = true;
                                    {
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 10000000 ngọc xanh");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Nhận r đừng nhận nữa");
                                }

                                break;
//                            case 2:
//                                if (!player.getSession().nhanvang) {
//                                    Item trungLinhThu = ItemService.gI().createNewItem((short) 457, 300);
//                                    InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
//                                    InventoryServiceNew.gI().sendItemBags(player);
//                                    this.npcChat(player, "Bạn nhận được 300 Thỏi Vàng");
//                                    player.getSession().nhanvang = true;
//                                    PlayerDAO.updatenhanvang(player);
//                                } else {
//                                    this.npcChat(player, "Nhận rồi đừng nhận nữa");
//                                }
//                                break;
//                            case 2:
//                                if (player.pet == null) {
//                                    PetService.gI().createNormalPet(player, player.gender);
//                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
//                                } else {
//                                    this.npcChat(player, "Bú ít thôi con");
//                                }
//                                break;
                            case 2:
                                Input.gI().createFormGiftCode(player);
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                        switch (select) {
                            case 0:
//                                        if (!player.gift.gemTanThu) {
                                if (true) {
                                    player.inventory.gem = 100000;
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 100K ngọc xanh");
                                    player.gift.gemTanThu = true;
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã nhận phần quà này rồi mà",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (nhanVang) {
                                    player.inventory.gold = Inventory.LIMIT_GOLD;
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 2 tỉ vàng");
                                } else {
                                    this.npcChat("");
                                }
                                break;
                            case 2:
                                if (nhanDeTu) {
                                    if (player.pet == null) {
                                        PetService.gI().createMabuPet(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                    } else {
                                        this.npcChat("Con đã nhận đệ tử rồi");
                                    }
                                }

                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                        switch (select) {
                            case 0:
                                ShopServiceNew.gI().opendShop(player, "ITEMS_REWARD", true);
                                break;
                            case 1:
                                if (player.getSession().goldBar > 0) {
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                        int quantity = player.getSession().goldBar;
                                        Item goldBar = ItemService.gI().createNewItem((short) 457, quantity);
                                        InventoryServiceNew.gI().addItemBag(player, goldBar);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Ông đã để " + quantity + " thỏi vàng vào hành trang con rồi đấy");
                                        PlayerDAO.subGoldBar(player, quantity);
                                        player.getSession().goldBar = 0;
                                    } else {
                                        this.npcChat(player, "Con phải có ít nhất 1 ô trống trong hành trang ông mới đưa cho con được");
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
//                        Input.gI().createFormNapThe(player, (byte) select);
                    }
                }
            }
        };
    }

    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    private static Npc KyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, 0,
                            "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                            "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    switch (select) {
                        case 0:
                            if (pl.session.actived == 1) {
                                ShopKyGuiService.gI().openShopKyGui(pl);
                                return;
                            }
                            this.npcChat(pl, "bạn chưa kích hoạt thành viên!!!");
                            Service.gI().sendPopUpMultiLine(pl, tempId, avartar,
                                    "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bChỉ với 50 hồng ngọc\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                            break;
                        case 1:
                            if (pl.session.actived == 1) {
                                ShopKyGuiService.gI().openShopKyGui(pl);
                                return;
                            }
                            this.npcChat(pl, "bạn chưa kích hoạt thành viên!!!");
                            break;
                    }

                }
            }
        };

    }

    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.idNRNM != -1) {
                            if (player.zone.map.mapId == 7) {
//                                this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Chức năng đang bảo trì!!", "Đóng");
                            }
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                }
                                break;
                        }
                    }
//                    else if (player.iDMark.getIndexMenu() == 1) {
//                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
//                            if (player.idNRNM == 353) {
//                                NgocRongNamecService.gI().tOpenNrNamec = System.currentTimeMillis() + 86400000;
//                                NgocRongNamecService.gI().firstNrNamec = true;
//                                NgocRongNamecService.gI().timeNrNamec = 0;
//                                NgocRongNamecService.gI().doneDragonNamec();
//                                NgocRongNamecService.gI().initNgocRongNamec((byte) 1);
//                                NgocRongNamecService.gI().reInitNrNamec((long) 86399000);
//                                SummonDragon.gI().summonNamec(player);
//                            } else {
//                                Service.getInstance().sendThongBao(player, "Anh phải có viên ngọc rồng Namếc 1 sao");
//                            }
//                        }
//                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_FIND_BOSS = 500000;

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(pl);
                                switch (taskId) {
                                    case ConstTask.TASK_19_0:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_1:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_2:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    default:
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                        break;
                                }
                            } else if (this.mapId == 68) {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                            }
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                        break;
                                    }
                                case 1:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_18_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                        break;
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.KUKU);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vang, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gem) + " vang");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                        break;
                                    }
                                case 2:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_18_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                        break;
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.MAP_DAU_DINH);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vang");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                        break;
                                    }
                                case 2:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_18_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                        break;
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.RAMBO);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ ngọc, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vang");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                        break;
                                    }
                                case 2:
                                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_18_0) {
                                        Service.gI().sendThongBao(player, "Hãy làm nhiệm vụ trước");
                                        return;
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                        break;
                                    }
                            }
                        }
                    }
                    if (this.mapId == 68) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                            "Cửa hàng", "Cửa hàng phụ kiện", "MENU VNĐ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);
                                    break;
                                case 1: //tiệm hớt tóc
                                    ShopServiceNew.gI().opendShop(player, "SHOP_NGU_SAC", false);
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 777,
                                            "\b|1|Có tiền rồi đổi thôi! \n \b|7|Bạn đang có :" + player.getSession().vnd + " VNĐ",
                                            "Đổi thỏi vàng", "Đổi Hồng Ngọc", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 777) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 778,
                                            "\b|1|Muốn đổi thỏi vàng à?\n \b|7|Bạn đang có :" + player.getSession().vnd + " VNĐ",
                                            "20k\n20 thỏi vàng", "50k\n50 thỏi vàng", "100k\n100 thỏi vàng", "200k\n200 thỏi vàng");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 780,
                                            "\b|1|Muốn đổi hồng ngọc à?\n \b|7|Bạn đang có " + player.getSession().vnd + " VNĐ",
                                            "20k\n20000 ngọc", "50k\n50000 ngọc", "100k\n100000 ngọc", "200k\n200000 ngọc");
                                    break;
                                case 2:
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 780) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().vnd < 20000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 20k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 20000)) {
                                        player.inventory.ruby += 20000;
                                        Service.getInstance().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 20000 hồng ngọc");
                                    }
                                    break;

                                case 1:
                                    if (player.getSession().vnd < 50000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 50k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 50000)) {

                                        player.inventory.ruby += 50000;
                                        Service.getInstance().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 50000 hồng ngọc");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().vnd < 100000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 100k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 100000)) {

                                        player.inventory.ruby += 100000;
                                        Service.getInstance().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 100000 hồng ngọc");
                                    }
                                    break;
                                case 3:
                                    if (player.getSession().vnd < 200000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 200k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 200000)) {

                                        player.inventory.ruby += 200000;
                                        Service.getInstance().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 200000 hồng ngọc");
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == 778) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().vnd < 20000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 20k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 20000)) {
                                        Item i = ItemService.gI().createNewItem((short) 457, (short) 20);
                                        InventoryServiceNew.gI().addItemBag(player, i);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 20 thỏi vàng");
                                    }
                                    break;

                                case 1:
                                    if (player.getSession().vnd < 50000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 50k VND");
                                        return;
                                    }
                                    if (PlayerDAO.subvnd(player, 50000)) {

                                        Item i2 = ItemService.gI().createNewItem((short) 457, (short) 50);
                                        InventoryServiceNew.gI().addItemBag(player, i2);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 50 thỏi vàng");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().vnd < 100000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 100k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 100000)) {

                                        Item i3 = ItemService.gI().createNewItem((short) 457, (short) 100);
                                        InventoryServiceNew.gI().addItemBag(player, i3);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 100 thỏi vàng");
                                    }
                                    break;
                                case 3:
                                    if (player.getSession().vnd < 200000) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 200k VND");
                                        return;
                                    }

                                    if (PlayerDAO.subvnd(player, 200000)) {

                                        Item i4 = ItemService.gI().createNewItem((short) 457, (short) 200);
                                        InventoryServiceNew.gI().addItemBag(player, i4);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn đã nhận được 200 thỏi vàng");
                                        break;
                                    }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    ///////////////////////////////mở thành viên by JINCHILL/////////////////////////////////
    public static Npc npc70(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    taoMenuCoBan(pl);
                }
            }

            @Override

            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                taoMenuThanhVien(player);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 782) {
                        switch (select) {
                            case 0:
                                moThanhVien(player);
                                break;
                            case 1:
                                // Xử lý lựa chọn số 1 (nếu cần)
                                break;
                        }
                    }
                }
            }

            private void taoMenuCoBan(Player pl) {
                String message = "\b|1|NGỌC RỒNG JINCHILL\nMIỄN LÀ NGƯƠI CÓ TIỀN THÌ HÃY NẠP LẦN ĐẦU ĐI \n\b|7|Bạn đang có :" + pl.getSession().vnd + " VNĐ";
                String[] options = {"Mở thành viên"};
                createOtherMenu(pl, ConstNpc.BASE_MENU, message, options);
            }

            private void taoMenuThanhVien(Player player) {
                String message = "\b|2|Mở thành viên giá 10k \n \b|7|Bạn đang có :" + player.getSession().vnd + " VNĐ";
                String[] options = {"Mở", "Đóng"};
                createOtherMenu(player, 782, message, options);
            }

            private void moThanhVien(Player player) {
                if (player.session.actived == 1) {
                    Service.gI().sendThongBao(player, "Bạn đã mở thành viên rồi");
                } else {
                    if (PlayerDAO.subvnd(player, 10000)) {
                        // Trừ tiền thành công, cập nhật trạng thái thành viên
                        player.session.actived = 1;

                        // Cập nhật trạng thái thành viên trong cơ sở dữ liệu
                        if (PlayerDAO.active(player, 1)) {
                            Service.gI().sendThongBao(player, "Bạn đã mở thành viên thành công");
                        } else {
                            Service.gI().sendThongBao(player, "Đã có lỗi xảy ra khi trừ tiền, vui lòng thử lại sau.");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Đã có lỗi xảy ra khi trừ tiền, vui lòng thử lại sau.");
                    }
                }
            }
        };
    }

    public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Ép sao\ntrang bị",
                                "Pha lê\nhóa\ntrang bị",
                                "Nâng cấp trang bị");
//                                "Pháp sư hoá trang bị",
//                                "Bông tai cấp 3",
//                                "Ép Ấn Trang Bị",
//                                "Thông tin Sự kiện\nHè 2023");

                    } else if (this.mapId == 121) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Về đảo\nrùa");

                    } else {

                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                "Nâng cấp\nBông tai\nPorata", "Mở chỉ số\nBông tai\nPorata",
                                "Nhập\nNgọc Rồng", "Pha lê hóa Linh Thú", "Ép sao hắc hóa\nLinh thú");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                    break;
                                case 2:
                                    this.createOtherMenu(player, ConstNpc.NANG_CAP_TRANG_BI,
                                            "Hãy mang đồ Hủy Diệt đến đây ta sẽ cho con tất cả?",
                                            "Nâng đồ TL\n Lên đồ HD",
                                            "Nâng cấp đồ kích hoạt",
                                            "Nâng cấp\nTrang bị\nKích hoạt VIP");

                                    break;
//                                case 3:
//                                    this.createOtherMenu(player, ConstNpc.NANG_CAP_TRANG_BI1,
//                                            "Tối đa 8 lần pháp sư hóa, có thể tẩy chỉ số pháp sư hóa?",
//                                            "Pháp sư hóa trang bị",
//                                            "Tẩy pháp sư hóa trang bị");
//                                    break;
//                                case 4:
//                                    this.createOtherMenu(player, ConstNpc.NANG_CAP_TRANG_BI2,
//                                            "Biến hình SSJ5?",
//                                            "Nâng cấp bông tai cấp 3",
//                                            "Mở chỉ số bông tai cấp 3");
//                                    break;
//                                case 5:
//                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_AN_TRANG_BI);
//                                    break;
//                                case 6:
//                                    this.createOtherMenu(player, ConstNpc.MENU_DOI_BON_TAM,
//                                            "Lên website xem thông tin sự kiện, mang vpsk đến đây cho bà nha?",
//                                            "Chế tạo\nBồn tắm gỗ",
//                                            "Chế tạo\nBồn tắm vàng");
//                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.PS_HOA_TRANG_BI:
                                case CombineServiceNew.TAY_PS_HOA_TRANG_BI:

                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                case CombineServiceNew.NANG_TL_LEN_HUY_DIET:
                                case CombineServiceNew.NANG_HUY_DIET_LEN_SKH:
                                case CombineServiceNew.NANG_HUY_DIET_LEN_SKH_VIP:
                                case CombineServiceNew.NANG_CAP_BONG_TAI_CAP3:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP3:
                                case CombineServiceNew.EP_AN_TRANG_BI:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.NANG_CAP_TRANG_BI2) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI_CAP3);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP3);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.NANG_CAP_TRANG_BI1) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PS_HOA_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TAY_PS_HOA_TRANG_BI);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.NANG_CAP_TRANG_BI) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_TL_LEN_HUY_DIET);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_HUY_DIET_LEN_SKH);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_HUY_DIET_LEN_SKH_VIP);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DOI_BON_TAM) {
                            Item[] items = new Item[4];
                            items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2116);
                            items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2117);
                            items[2] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2118);
                            items[3] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2119);

                            int quantity1 = (items[0] != null) ? items[0].quantity : 0;
                            int quantity2 = (items[1] != null) ? items[1].quantity : 0;
                            int quantity3 = (items[2] != null) ? items[2].quantity : 0;
                            int quantity4 = (items[3] != null) ? items[3].quantity : 0;
                            int giavang;
                            int giaruby;
                            switch (select) {
                                case 0:
                                    giavang = 50000000;
                                    if (items[0] != null && items[1] != null && items[2] != null && items[3] != null
                                            && items[0].quantity >= 50 && items[1].quantity >= 20 && items[2].quantity >= 20
                                            && items[3].quantity >= 5 && player.inventory.gold >= giavang) {
                                        player.inventory.gold -= giavang;

                                        Item bontamgo = ItemService.gI().createNewItem((short) 2120);
                                        bontamgo.itemOptions.clear();
                                        bontamgo.itemOptions.add(new Item.ItemOption(30, 0));
                                        bontamgo.itemOptions.add(new Item.ItemOption(86, 0));
                                        InventoryServiceNew.gI().addItemBag(player, bontamgo);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 50);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 20);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 20);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[3], 5);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendMoney(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + bontamgo.template.name);

                                    } else {
                                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "|1| Chế tạo bồn tắm gỗ\n|2| Cành khô " + quantity1 + "/50\nNước Suối Tinh Khiết "
                                                + quantity2 + "/20\nGỗ Lớn " + quantity3 + "/20\nQue Đốt " + quantity4 + "/5\n Giá vàng: "
                                                + Util.formatNumber(giavang),
                                                "Từ chối");
                                    }
                                    break;

                                case 1:
                                    giavang = 70000000;
                                    giaruby = 150;
                                    if (items[0] != null && items[1] != null && items[2] != null && items[3] != null
                                            && items[0].quantity >= 50 && items[1].quantity >= 20 && items[2].quantity >= 20
                                            && items[3].quantity >= 5 && player.inventory.gold >= giavang
                                            && player.inventory.ruby >= giaruby) {
                                        player.inventory.gold -= giavang;
                                        Item bontamvang = ItemService.gI().createNewItem((short) 2121);
                                        bontamvang.itemOptions.clear();
                                        bontamvang.itemOptions.add(new Item.ItemOption(30, 0));
                                        bontamvang.itemOptions.add(new Item.ItemOption(86, 0));
                                        InventoryServiceNew.gI().addItemBag(player, bontamvang);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 50);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 20);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 20);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[3], 5);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendMoney(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + bontamvang.template.name);
                                        if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 1)) {
                                            Service.gI().sendThongBao(player, "Bạn đã nhậnd được 1 điểm sự kiện");

                                        }
                                    } else {
                                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "|1| Chế tạo bồn tắm vàng\n|2| Cành khô " + quantity1 + "/50\nNước Suối Tinh Khiết "
                                                + quantity2 + "/20\nGỗ Lớn " + quantity3 + "/20\nQue Đốt " + quantity4 + "/5\n Giá vàng: "
                                                + Util.formatNumber(giavang) + "\nGiá Hồng ngọc: " + giaruby,
                                                "Từ chối");
                                    }
                                    break;
                            }

                        } else if (this.mapId == 112) {
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                        break;
                                }
                            }
                        }
                    } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                    break;
                                case 1:

                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                    break;
                                case 2: //nâng cấp bông tai
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                    break;
                                case 3: //làm phép nhập đá
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                                    break;
                                case 4:

                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                    break;
                                case 5: //pha lê hóa linh thú
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_LINH_THU);
                                    break;
                                case 6: //ép sao linh thú
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_LINH_THU);
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1H", true);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "BUA_8H", true);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI:
                                case CombineServiceNew.NHAP_NGOC_RONG:
                                case CombineServiceNew.PHA_LE_HOA_LINH_THU:
                                case CombineServiceNew.EP_SAO_LINH_THU:

                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn chơi EVENT không ?",
                                "Nâng cấp cải trang", "Đổi trang");

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAI_TRANG);
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAI_TRANG:

                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc duongtank(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (mapId == 0) {

                        this.createOtherMenu(player, 0, "Ngũ Hàng Sơn x2 Tnsm\nHỗ trợ cho Ae Từ\b|1|Dưới 1tr5 SM dến 160 Tỷ SM?", "OK", "Oéo");
                    }
                    if (mapId == 123) {
                        this.createOtherMenu(player, 0, "Bạn Muốn Quay Trở Lại Làng Aru?", "OK", "Từ chối");

                    }
                    if (mapId == 122) {
                        this.createOtherMenu(player, 0, "Ta đang giữ quả trứng truyền thuyết hãy mang đến cho ta 4 loại bùa và quả trứng nhé!!", "Âu kê", "No");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (select) {
                        case 0:
                            if (mapId == 0) {
                                if (player.nPoint.power < 1500000 || player.nPoint.power >= 1600000000000L) {
                                    Service.getInstance().sendThongBao(player, "Sức mạnh bạn không phù hợp để qua map!");
                                    return;
                                }
                                ChangeMapService.gI().changeMapInYard(player, 123, -1, -1);
                            }
                            if (mapId == 123) {
                                ChangeMapService.gI().changeMapInYard(player, 0, -1, -1);
                            }
                            if (mapId == 122) {
                                Item thitheo = null;
                                Item thungnep = null;
                                Item thungdxanh = null;
                                Item ladong = null;
                                Item thoivang = null;

                                try {
                                    thitheo = InventoryServiceNew.gI().findItemBag(player, 537);
                                    thungnep = InventoryServiceNew.gI().findItemBag(player, 538);
                                    thungdxanh = InventoryServiceNew.gI().findItemBag(player, 539);
                                    ladong = InventoryServiceNew.gI().findItemBag(player, 540);
                                    thoivang = InventoryServiceNew.gI().findItemBag(player, 1182);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }
                                if (thitheo == null || thitheo.quantity < 1 || thungnep == null || thungnep.quantity < 1 || thungdxanh == null || thungdxanh.quantity < 1 || ladong == null || ladong.quantity < 1) {
                                    this.npcChat(player, "Bạn không đủ bùa");
                                } else if (thoivang == null || thoivang.quantity < 1) {
                                    this.npcChat(player, "Bạn không có trứng");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                } else {
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 1);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 1);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 1);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 1);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 1);
                                    Service.getInstance().sendMoney(player);

                                    if (Util.isTrue(20, 100)) {
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 1181);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + trungLinhThu.template.name);
                                    } else if (Util.isTrue(10, 100)) {
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 14);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + trungLinhThu.template.name);
                                    } else if (Util.isTrue(40, 100)) {
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 15);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + trungLinhThu.template.name);
                                    } else if (Util.isTrue(88, 100)) {
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 1178);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + trungLinhThu.template.name);
                                    }

                                    InventoryServiceNew.gI().sendItemBags(player);

                                }
                                break;
                            }

                    }
                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        this.mapId = map.mapId;
                        this.cx = Util.nextInt(100, map.mapWidth - 100);
                        this.cy = map.yPhysicInTop(this.cx, 0);
                        this.map = map;
                        this.map.npcs.add(this);
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.getInstance().hideWaitDialog(player);
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
                    Service.getInstance().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?",
                            "Kể\nChuyện", "Quay về\nQuá khứ");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //kể chuyện
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //về quá khứ
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //kể chuyện
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //đến tương lai
//                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                    } else if (this.mapId == 139) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến potaufeu
                                ChangeMapService.gI().goToPotaufeu(player);
                            }
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

//public static Npc Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 149) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "tét", "Gọi nhân bản");
//                    }
//                }
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                   if (select == 0){
//                        BossManager.gI().createBoss(-214);
//                   }
//                }
//            }
//        };
//    }
    public static Npc noibanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14 || this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Hãy đem đến cho ta:\n+x80 thúng nếp\t+x80 thịt heo\t+x80 thúng đậu xanh\n+x80 là dong\t+x3 thỏi vàng"
                            + "\n Ta sẽ nấu cho ngươi bánh tét với tác dụng ngầu nhiên tăng từ 20% chỉ số\nNgoài ra người còn có thể dùng x99 nguyên liệu và 5 thỏi vàng để chế thành 20 thỏi vàng\n(Bánh chưng tăng 30% x99 mỗi loại nguyên liệu đầu vào và x5 thòi vàng)", "Nấu\nbánh tét", "Nấu\nbánh chưng", "Nấu\nThỏi vàng");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item ladong = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 80 || thungnep == null || thungnep.quantity < 80 || thungdxanh == null || thungdxanh.quantity < 80 || ladong == null || ladong.quantity < 80) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
                                    } else if (thoivang == null || thoivang.quantity < 50) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 50);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 752);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh tét");
                                    }
                                    break;
                                }
                                case 1: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item ladong = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 99 || thungnep == null || thungnep.quantity < 99 || thungdxanh == null || thungdxanh.quantity < 99 || ladong == null || ladong.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
                                    } else if (thoivang == null || thoivang.quantity < 5) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 753);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh chưng");
                                    }
                                    break;
                                }
                                case 2: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item ladong = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 99 || thungnep == null || thungnep.quantity < 99 || thungdxanh == null || thungdxanh.quantity < 99 || ladong == null || ladong.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
                                    } else if (thoivang == null || thoivang.quantity < 50) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 457, 200);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 200 thỏi vàng");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc Monaito(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "\b|7|Hãy đem đến cho ta:\n+x10 Cành Khô \n+x10 Nước suối tinh khiết\n+x10 Gỗ lớn\n+x10 Que đốt\n+x5 thỏi vàng\n"
                            //                            + "\b|3|Lên website đọc sự kiện hè nha :3"
                            + "\b|3|Ta sẽ giúp ngươi lấy được hộp quà mở ta hộp quà Cải trang Hit tối đa 150% SĐ Chí Mạng", "Đổi Hộp Quà");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item thitbo = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 2116);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 2117);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 2118);
                                        thitbo = InventoryServiceNew.gI().findItemBag(player, 2119);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 10 || thungnep == null || thungnep.quantity < 10 || thungdxanh == null || thungdxanh.quantity < 10 || thitbo.quantity < 10) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để đổi quà");
                                    } else if (thoivang == null || thoivang.quantity < 5) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 10);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 10);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 10);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitbo, 10);

                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 736);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Hộp quà");
                                        if (PlayerDAO.AddDiemSuKien(player.getSession().uu, 1)) {
                                            Service.gI().sendThongBao(player, "Bạn đã nhậnd được 1 điểm sự kiện");
                                        }
                                        return;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc ongbut(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Hiện tại ta sẽ bán cải trang cho các ngươi", "Cửa hàng");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "SHOP_HUNG_VUONG", false);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                createOtherMenu(player, 0, "Trò chơi Chọn ai đây đang được diễn ra, nếu bạn tin tưởng mình đang tràn đầy may mắn thì có thể tham gia thử", "Thể lệ", "Chọn\nThỏi vàng");
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    String time = ((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                    if (pl.iDMark.getIndexMenu() == 0) {
                        if (select == 0) {
                            createOtherMenu(pl, ConstNpc.IGNORE_MENU, "Thời gian giữa các giải là 5 phút\nKhi hết giờ, hệ thống sẽ ngẫu nhiên chọn ra 1 người may mắn.\nLưu ý: Số thỏi vàng nhận được sẽ bị nhà cái lụm đi 5%!Trong quá trình diễn ra khi đặt cược nếu thoát game mọi phần đặt đều sẽ bị hủy", "Ok");
                        } else if (select == 1) {
                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                        }
                    } else if (pl.iDMark.getIndexMenu() == 1) {
                        if (((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                    break;
                                case 1: {
                                    try {
                                        if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 20) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 20);
                                            InventoryServiceNew.gI().sendItemBags(pl);
                                            pl.goldNormar += 20;
                                            ChonAiDay.gI().goldNormar += 20;
                                            ChonAiDay.gI().addPlayerNormar(pl);
                                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                        }
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;

                                case 2: {
                                    try {
                                        if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 200) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 200);
                                            InventoryServiceNew.gI().sendItemBags(pl);
                                            pl.goldVIP += 200;
                                            ChonAiDay.gI().goldVip += 200;
                                            ChonAiDay.gI().addPlayerVIP(pl);
                                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                        }
                                    } catch (Exception ex) {
//                                            java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc npclytieunuongCLTX(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đưa cho ta thỏi vàng và ngươi sẽ mua đc oto\nĐây không phải chẵn lẻ tài xỉu đâu=)))",
                            "Xỉu", "Tài");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.session.actived == 1) {
                                        Service.getInstance().sendThongBao(player, "You chưa là cư dân của NR ON");
                                        Input.gI().TAI(player);

                                    }
                                    break;

                                case 1:

                                    if (player.session.actived == 1) {

                                        Service.getInstance().sendThongBao(player, "You chưa là cư dân của NR ON");

                                        Input.gI().XIU(player);
                                    }
                                    break;

                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Đến Kaio", "Quay số\nmay mắn");
                    }
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?\nCon đang còn : " + player.pointPvp + " điểm PvP Point", "Đến DHVT", "Đổi Cải trang sự kiên", "Top PVP");
                    }
                    if (this.mapId == 129) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay về");
                    }
                    if (this.mapId == 153) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay về");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 3:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 354);
                                    break; // dao kame
                                case 0:
                                    if (player.session.actived == 1) {
                                        Service.getInstance().sendThongBao(player, "You chưa là cư dân của NR Blue");
                                    } else {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 129, -1, 354);
                                        Service.getInstance().changeFlag(player, Util.nextInt(8));
                                    }
                                    break; // qua dhvt
                                case 1:  // 
                                    this.createOtherMenu(player, 1,
                                            "Bạn có muốn đổi 500 điểm PVP lấy \n|6|Cải trang Mèo Kid Lân với tất cả chỉ số là 30%\n ", "Ok", "Tu choi");
                                    // bat menu doi item
                                    break;

                                case 2:  // 
//                                    Util.showListTop(player, (byte) 3);
                                    // mo top pvp
                                    break;

                            }
                        }
                        if (player.iDMark.getIndexMenu() == 1) { // action doi item
                            switch (select) {
                                case 0: // trade
                                    if (player.pointPvp >= 500) {
                                        player.pointPvp -= 500;
                                        Item item = ItemService.gI().createNewItem((short) (1104));
                                        item.itemOptions.add(new Item.ItemOption(49, 30));
                                        item.itemOptions.add(new Item.ItemOption(77, 30));
                                        item.itemOptions.add(new Item.ItemOption(103, 30));
                                        item.itemOptions.add(new Item.ItemOption(207, 0));
                                        item.itemOptions.add(new Item.ItemOption(33, 0));
//                                      
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Chúc Mừng Bạn Đổi Cải Trang Thành Công !");
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Không đủ điểm bạn còn " + (500 - player.pointPvp) + " Điểm nữa");
                                    }
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 129) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 153) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                            "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                            "Rương phụ\n("
                                            + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa hết\ntrong rương", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                            + "sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }

                    }
                }
            }
        };
    }

//public static Npc berus_dhvt(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "Ngươi muốn đánh nhau sao?", "Đến DHVT", "Bản đồ kho báu");
//                    }
//                  
//                    if (this.mapId == 129) {
//                        this.createOtherMenu(player, 800,
//                                "Ngươi muốn về à?", "Quay về");
//                    }
//                    if (this.mapId == 135) {
//                        this.createOtherMenu(player, 801,
//                                "Ngươi muốn về à?", "Quay về");
//                    }
//                }
//            
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (player.iDMark.isBaseMenu()) {
//                            switch (select) {
//                                case 0:
//                                        ChangeMapService.gI().changeMap(player, 129, -1, 386, 264);
//                                    break; // qua dhvt
//                                case 1:
//                                        ChangeMapService.gI().changeMap(player, 135, -1, 84, 336);
//                                    break;
//                                  
//
//                            }
//                        }
//                    }
//                
//                    if (this.mapId == 129) {
//                        switch (select) {
//                            case 0: // quay ve
//                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 354);
//                                break;
//                        
//                        }if (this.mapId == 135) {
//                        switch (select) {
//                            case 0: // quay ve
//                                ChangeMapService.gI().changeMap(player, 5, -1, 386, 336);
//                                break;    
//                               }
//                            }
//                            
//                        }  
//                        
//                   }
//                 }
//            };
//        }
//    
    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Hiện tại ta sẽ bán cải trang cho các ngươi", "Cửa hàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    //con đường rắn độc
                                    ChangeMapService.gI().changeMap(player, 141, -1, 318, 336);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "SHOP_HUNG_VUONG", false);
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Từ chối");
                    } else if (this.mapId == 135) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn có muốn về đảo kame không??",
                                "Đi chứ\nhehe", "Từ chối");
                    }
                    if (this.mapId == 114) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;

                            }
                        }
                    } else if (this.mapId == 135) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 5, -1, 335, 288);
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else if (this.mapId == 52) {
                        try {
                            MapMaBu.gI().setTimeJoinMapMaBu();
                            if (this.mapId == 52) {
                                long now = System.currentTimeMillis();
                                if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                            + "ngươi có muốn tham gia không?",
                                            "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }

                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu osin");
                        }

                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                            this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Lên Tầng!", "Quay về", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Quay về", "Từ chối");
                        }
                    } else if (this.mapId == 120) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 1:
                                    if (player.nPoint.power >= 60000000000L) {
                                        ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                    } else {
                                        Service.gI().sendThongBao(player, "Đạt đủ 60 tỉ sức mạnh mới có thể vào map ");
                                    }
                                    break;
                            }
                        }
                    } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                            }
                        }
                    } else if (this.mapId == 52) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                                break;
                            case ConstNpc.MENU_OPEN_MMB:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                    ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                }
                                break;
                        }
                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                            if (select == 0) {
                                player.fightMabu.clear();
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            } else if (select == 1) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        } else {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    } else if (this.mapId == 120) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_OPENED_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                + "Thời gian còn lại là "
                                + TimeUtil.getMinLeft(player.clan.timeOpenDoanhTrai, DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + " phút. Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + DoanhTrai.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh trại chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi đã đi trại lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm:ss") + " hôm nay. Người mở\n"
                                + "(" + player.clan.playerOpenDoanhTrai + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                            + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
//                            if (select == 0) {
//                                DoanhTraiService.gI().openBanDoKhoBau(player);
//                            } else if (select == 2) {
//                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
//                            }
//                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.MENU_OPENED_DOANH_TRAI:
//                            if (select == 0) {
//                                ChangeMapService.gI().changeMapInYard(player, 53, player.clan.doanhTrai.id, 60);
//                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.mabuEgg.sendMabuEgg();
                    if (player.mabuEgg.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_EGG:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                            } else if (select == 1) {
                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                    player.mabuEgg.timeDone = 0;
                                    Service.getInstance().sendMoney(player);
                                    player.mabuEgg.sendMabuEgg();
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ vàng để thực hiện, còn thiếu "
                                            + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                }
                            }
                            break;
                        case ConstNpc.CAN_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                            "Bạn có chắc chắn cho trứng nở?\n"
                                            + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                            "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                    break;
                                case 1:
                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                    break;
                                case 2:
                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_DESTROY_EGG:
                            if (select == 0) {
                                player.mabuEgg.destroyEgg();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "Nâng\ngiới hạn\nsức mạnh",
                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vang", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vang", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                                }
                                //giới hạn đệ tử
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                        switch (select) {
                            case 0:
                                OpenPowerService.gI().openPowerBasic(player);
                                break;
                            case 1:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.gI().sendMoney(player);
                                    }
                                } else {
                                    Service.gI().sendThongBao(player,
                                            "Bạn không đủ vang để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vang");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.gI().sendMoney(player);
                                }
                            } else {
                                Service.gI().sendThongBao(player,
                                        "Bạn không đủ vang để mở, còn thiếu "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + "vang ");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                        }
                    } else if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                            }
                        }
                    } else if (this.mapId == 104) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                        + "ngươi có muốn tham gia không?",
                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        String quantily = player.rewardBlackBall.quantilyBlackBall[i] > 1 ? "x" + player.rewardBlackBall.quantilyBlackBall[i] + " " : "";
                                        optionRewards[index] = quantily + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "Từ chối";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ngươi có một vài phần thưởng ngọc "
                                            + "rồng sao đen đây!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu rồng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
//                                if (!player.getSession().actived) {
//                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//
//                                } else
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }

        };
    }


    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                    } else {
                        if (BossManager.gI().existBossOnPlayer(player)
                                || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                                || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối", "Gọi BOSS");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                    "Từ chối"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        } else if (select == 2) {
                            BossManager.gI().callBoss(player, mapId);
                        } else if (select == 1) {
                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.getInstance().sendThongBao(player, "Bạn đã được phù hộ rồi!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 7) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 0) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 146) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 147) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 148) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 49 || this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!", "Hướng Dẫn",
                            "Đổi SKH VIP", "Từ Chối");
                }
                if (this.mapId == 48) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!",
                            "Chế Tạo trang bị thiên sứ", "Đóng");

                }
            }

            //if (player.inventory.gold < 500000000) {
//                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
//                return;
//            }
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu() && this.mapId == 7) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 146, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 14) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 148, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 0) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 147, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 147) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 148) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 14, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 146) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, 450);
                        }
                        if (select == 1) {
                        }

                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 49 || this.mapId == 5) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        }
                        if (select == 1) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }
                    }

                    if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                        if (select == 0) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CHE_TAO_TRANG_BI_TS);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineServiceNew.CHE_TAO_TRANG_BI_TS:

                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player);
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }

                    }
                }
            }

        };
    }

    //    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 48) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn gì nào?" + player.inventory.coupon+, "Đóng");
//                    } else {
//                        super.openBaseMenu(player);
//                    }
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    switch (this.mapId) {
//                        case 48:
//                            switch (player.iDMark.getIndexMenu()) {
//                                case ConstNpc.BASE_MENU:
//                                    if (select == 0) {
//
//                                    }
//                                    break;
//                            }
//                            break;
//                    }
//                }
//            }
//        };
//    }
    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi chỉ cần mang x99 thức ăn mỗi loại đến đây\n Ta sẽ giúp ngươi có được những trang bị\n xịn nhất của ta!",
                            "Shop Đồ Hủy Diệt", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 48:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:

                                    if (select == 0 && player.setClothes.godClothes == 5) {
                                        Item pudding = InventoryServiceNew.gI().findItemBag(player, 663);
                                        Item xucxich = InventoryServiceNew.gI().findItemBag(player, 664);
                                        Item kemdau = InventoryServiceNew.gI().findItemBag(player, 665);
                                        Item mily = InventoryServiceNew.gI().findItemBag(player, 666);
                                        Item sushi = InventoryServiceNew.gI().findItemBag(player, 667);

                                        if (pudding != null && pudding.quantity >= 99
                                                || xucxich != null && xucxich.quantity >= 99
                                                || kemdau != null && kemdau.quantity >= 99
                                                || mily != null && mily.quantity >= 99
                                                || sushi != null && sushi.quantity >= 99) {
                                            ShopServiceNew.gI().opendShop(player, "THUCANHUYDIET", true);
                                            break;//
                                        } else {
                                            this.npcChat(player, "Còn không mau đem x99 thức ăn đến cho ta !!");
                                            break;
                                        }
                                    }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, cậu muốn tôi giúp gì?", "Nhiệm vụ\nhàng ngày", "Từ chối");
                    }
//                    if (this.mapId == 47) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "Xin chào, cậu muốn tôi giúp gì?", "Từ chối");
//                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.sideTask.template != null) {
                                        String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                                + player.playerTask.sideTask.getLevel() + ")"
                                                + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                                + player.playerTask.sideTask.maxCount + " ("
                                                + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                + "sức cậu có thể làm được cái nào?",
                                                "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa ngục", "Từ chối");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc unkow(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (this.mapId == 5) {
                            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                        "Cửa hàng", "Đóng");
                            }
                        }
                    } else if (this.mapId == 109) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "KARIN", false);
                            }
                        }
                    } else if (this.mapId == 109) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|2|Ta Vừa Hắc Mắp Xêm Được Tóp Của Toàn Server\b|7|Người Muốn Xem Tóp Gì?",
                            "Tóp Sức Mạnh", "Top Nhiệm Vụ", "Tóp Nạp", "Top Sự Kiện", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                    if (select == 0) {
                                        Service.gI().showListTop(player, Manager.topSM);
                                        break;
                                    }
                                    if (select == 1) {
                                        Service.gI().showListTop(player, Manager.topNV);
                                        break;
                                    }
                                    if (select == 2) {
                                        Service.gI().sendThongBaoOK(player, TopService.getTopNap());
                                    }
                                    if (select == 3) {
                                        Service.gI().sendThongBaoOK(player, TopService.getTopSK());
                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 80) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart", "Từ chối");
                    } else if (this.mapId == 131) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 80) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 870);
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + biKiep.quantity + " bí kiếp.\n"
                                    + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Học dịch\nchuyển", "Đóng");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            if (biKiep.quantity >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                InventoryServiceNew.gI().addItemBag(player, yardart);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, biKiep, 10000);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        };
    }

    public static Npc Fide(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, Ngươi có muốn kiếm lấy thú nuôi cho riêng mình không?",
                            "Cửa hàng PET");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 20 || this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "FIDE", true);
                                    break;
                                case 1: //tiệm hồng ngọc
                                    ShopServiceNew.gI().opendShop(player, "FIDE", true);
                                    break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc Jiren(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                            "Cửa hàng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "JIREN", true);
                                    break;
                                case 1: //tiệm hồng ngọc
                                    ShopServiceNew.gI().opendShop(player, "JIREN", true);
                                    break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 154) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!",
                            "Thử thách\nTuyệt kĩ đặc biệt", "Nâng Cấp\n Tuyệt kỹ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu() && this.mapId == 154) {
                        Item BiKiepTuyetKy = InventoryServiceNew.gI().findItemBag(player, 2094);
                        switch (select) {
                            case 0:
                                if (player.nPoint.power >= 60_000_000_000L) {
                                    if (BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 1) {
                                        Skill curSkilltd = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                        Skill curSkillxd = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                        Skill curSkillnm = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                        if (curSkilltd == null && player.gender == 0) {
                                            Skill skill = null;
                                            skill = SkillUtil.createSkillLevel0(Skill.SUPER_KAME);
                                            player.playerSkill.skills.add(skill);
                                        }
                                        if (curSkillnm == null && player.gender == 1) {
                                            Skill skillnm = null;
                                            skillnm = SkillUtil.createSkillLevel0(Skill.MA_PHONG_BA);
                                            player.playerSkill.skills.add(skillnm);
                                        }
                                        if (curSkillxd == null && player.gender == 2) {
                                            Skill skillxd = null;
                                            skillxd = SkillUtil.createSkillLevel0(Skill.LIEN_HOAN_CHUONG);
                                            player.playerSkill.skills.add(skillxd);
                                        }
                                        curSkilltd = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                        curSkillxd = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                        curSkillnm = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                        switch (player.gender) {
                                            case 0:
                                                if (curSkilltd.point == 0) {
                                                    this.createOtherMenu(player, 99, "|1|Ta sẽ giúp ngươi học tuyệt kỹ: Super KameJoko \n|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n|2|Giá vàng: 10.000.000\n|2|Giá ngọc: 99",
                                                            "Đồng ý\nHọc", "Từ chối");
                                                    break;
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã học skill mất rồi, hãy nâng cấp skill thêm thôi nhé",
                                                            "Đồng ý", "Từ chối");
                                                    break;
                                                }
                                            case 1:
                                                if (curSkillnm.point == 0) {
                                                    this.createOtherMenu(player, 99, "|1|Ta sẽ giúp ngươi học tuyệt kỹ: Ma Phong Ba \n|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n|2|Giá vàng: 10.000.000\n|2|Giá ngọc: 99",
                                                            "Đồng ý\nHọc", "Từ chối");
                                                    break;
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã học skill mất rồi, hãy nâng cấp skill thêm thôi nhé",
                                                            "Đồng ý", "Từ chối");
                                                    break;
                                                }
                                            case 2:
                                                if (curSkillxd.point == 0) {
                                                    this.createOtherMenu(player, 99, "|1|Ta sẽ giúp ngươi học tuyệt kỹ: Galic Liên Hoàn Chưởng \n|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n|2|Giá vàng: 10.000.000\n|2|Giá ngọc: 99",
                                                            "Đồng ý\nHọc", "Từ chối");
                                                    break;
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã học skill mất rồi, hãy nâng cấp skill thêm thôi nhé",
                                                            "Đồng ý", "Từ chối");
                                                    break;
                                                }
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Hãy luyện tập nhiều hơn để có được bí kiếp tuyệt kỹ đặc biệt");
                                        break;
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Hãy luyện tập nhiều hơn và đạt 60 tỉ sức mạng trước nhé !!!");
                                    break;
                                }
                                break;

                            case 1:
                                if (BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 1) {
                                    this.createOtherMenu(player, 6, "|1|Ta sẽ giúp ngươi nâng cấp tuyệt kỹ cơ bản của ngươi lên một tầm cao mới\n|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n|2|Giá vàng: 10.000.000\n|2|Giá ngọc: 99",
                                            "Super Kame Joko", "Ma Phong Ba", "Calick Liên Hoàn Chưởng");
                                    break;
                                } else {
                                    Service.gI().sendThongBao(player, "Hãy luyện tập để lấy thêm bí kiếp tuyệt kỹ và quay lại đây \n Ta sẽ dạy cho ngươi tuyệt kỹ bí mật");
                                    break;
                                }
                            case 2:
                                switch (player.gender) {
                                    case 0:
                                        Skill skill = null;
                                        skill = SkillUtil.createSkillLevel0(Skill.SUPER_KAME);
                                        player.playerSkill.skills.add(skill);
                                        Service.gI().sendThongBao(player, "Bạn vừa add skill " + skill.template.name);
                                        break;
                                    case 1:
                                        Skill skillnm = null;
                                        skillnm = SkillUtil.createSkillLevel0(Skill.MA_PHONG_BA);
                                        player.playerSkill.skills.add(skillnm);
                                        Service.gI().sendThongBao(player, "Bạn vừa add skill " + skillnm.template.name);
                                        break;
                                    case 2:
                                        Skill skillxd = null;
                                        skillxd = SkillUtil.createSkillLevel0(Skill.LIEN_HOAN_CHUONG);
                                        player.playerSkill.skills.add(skillxd);
                                        Service.gI().sendThongBao(player, "Bạn vừa add skill " + skillxd.template.name);
                                        break;
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 6) {
                        Item BiKiepTuyetKy = InventoryServiceNew.gI().findItemBag(player, 2094);
                        switch (select) {
                            case 0:
                                Message msg;
                                try {
                                    if (player.gender == 0 && BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 99) {
                                        Skill curSkill = SkillUtil.getSkillbyId(player, Skill.SUPER_KAME);
                                        if (curSkill.point == 9) {
                                            Service.gI().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                            return;
                                        }
                                        if (curSkill.point == 0) {
                                            Service.gI().sendThongBao(player, "Hãy luyện tập lấy bí kiếp tuyệt kỹ và học kỹ năng cơ bản trước");
                                            return;
                                        } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            curSkill = SkillUtil.createSkill(Skill.SUPER_KAME, curSkill.point + 1);
                                            SkillUtil.setSkill(player, curSkill);
                                            msg = Service.gI().messageSubCommand((byte) 62);
                                            msg.writer().writeShort(curSkill.skillId);
                                            player.sendMessage(msg);
                                            msg.cleanup();
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Ô ô cái thằng này bấm cái rì đấy hả !!!");
                                    }
                                    break;

                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                try {
                                if (player.gender == 1 && BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 99) {
                                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                                    if (curSkill.point == 10) {
                                        Service.gI().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                        return;
                                    }
                                    if (curSkill.point == 0) {
                                        Service.gI().sendThongBao(player, "Hãy luyện tập lấy bí kiếp tuyệt kỹ và học kỹ năng cơ bản trước");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        curSkill = SkillUtil.createSkill(Skill.MA_PHONG_BA, curSkill.point + 1);
                                        SkillUtil.setSkill(player, curSkill);
                                        msg = Service.gI().messageSubCommand((byte) 62);
                                        msg.writer().writeShort(curSkill.skillId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Ô ô cái thằng này bấm cái rì đấy hả !!!");
                                }
                                break;
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                if (player.gender == 2 && BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 99) {
                                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.LIEN_HOAN_CHUONG);
                                    if (curSkill.point == 10) {
                                        Service.gI().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
                                        return;
                                    }
                                    if (curSkill.point == 0) {
                                        Service.gI().sendThongBao(player, "Hãy luyện tập lấy bí kiếp tuyệt kỹ và học kỹ năng cơ bản trước");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        curSkill = SkillUtil.createSkill(Skill.LIEN_HOAN_CHUONG, curSkill.point + 1);
                                        SkillUtil.setSkill(player, curSkill);
                                        msg = Service.gI().messageSubCommand((byte) 62);
                                        msg.writer().writeShort(curSkill.skillId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Ô ô cái thằng này bấm cái rì đấy hả !!!");
                                }
                                break;

                            } catch (Exception e) {
                            }
                            break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 99) {
                        switch (select) {
                            case 0:
                                Item BiKiepTuyetKy = InventoryServiceNew.gI().findItemBag(player, 2094);
                                if (BiKiepTuyetKy != null && BiKiepTuyetKy.quantity >= 999) {
                                    switch (player.gender) {
                                        case 0:
                                            SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            break;
                                        case 1:
                                            SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            break;
                                        case 2:
                                            SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, BiKiepTuyetKy, 999);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            break;
                                    }
                                    break;
                                } else {
                                    Service.gI().sendThongBao(player, "Hãy luyện tập thêm để nhận được bí kiếp tuyệt kỹ nhé con !");
                                }
                        }
                    }
                }
                ;
            }
        ;

    }

    ;
    }

    public static Npc GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            String[] menuselect = new String[]{};

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 52) {
                        createOtherMenu(pl, 0, DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Giai(pl), "Thông tin\nChi tiết", DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(pl) ? "Đăng ký" : "OK", "Đại Hội\nVõ Thuật\nLần thứ\n23");
                    } else if (this.mapId == 129) {
                        int goldchallenge = pl.goldChallenge;
                        if (pl.levelWoodChest == 0) {
                            menuselect = new String[]{"Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Về\nĐại Hội\nVõ Thuật"};
                        } else {
                            menuselect = new String[]{"Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Nhận thưởng\nRương cấp\n" + pl.levelWoodChest, "Về\nĐại Hội\nVõ Thuật"};
                        }
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào", menuselect, "Từ chối");

                    } else {
                        super.openBaseMenu(pl);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 52) {
                        switch (select) {
                            case 0:
                                Service.gI().sendPopUpMultiLine(player, tempId, avartar, DaiHoiVoThuat.gI().Info());
                                break;
                            case 1:
                                if (DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(player)) {
                                    DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Reg(player);
                                }
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
                                break;
                        }
                    } else if (this.mapId == 129) {
                        int goldchallenge = player.goldChallenge;
                        if (player.levelWoodChest == 0) {
                            switch (select) {
                                case 0:
                                    if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                        if (player.inventory.gold >= goldchallenge) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.gold -= (goldchallenge);
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                            player.goldChallenge += 2000000;
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                    break;
                            }
                        } else {
                            switch (select) {
                                case 0:
                                    if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                        if (player.inventory.gold >= goldchallenge) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.gold -= (goldchallenge);
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                            player.goldChallenge += 2000000;
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    if (!player.receivedWoodChest) {
                                        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item it = ItemService.gI().createNewItem((short) 570);
                                            it.itemOptions.add(new Item.ItemOption(72, player.levelWoodChest));
                                            it.itemOptions.add(new Item.ItemOption(30, 0));
                                            it.createTime = System.currentTimeMillis();
                                            InventoryServiceNew.gI().addItemBag(player, it);
                                            InventoryServiceNew.gI().sendItemBags(player);

                                            player.receivedWoodChest = true;
                                            player.levelWoodChest = 0;
                                            Service.getInstance().sendThongBao(player, "Bạn nhận được rương gỗ");
                                        } else {
                                            this.npcChat(player, "Hành trang đã đầy");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
                                    }
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.NAMI:
                    return nami(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS:
                    return whis(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO:
                    return Giuma(mapId, status, cx, cy, tempId, avatar);
//                case ConstNpc.BERUS_DHVT:
////                 
//                    return berus_dhvt(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.NOI_BANH:
                    return noibanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_BUT:
                    return ongbut(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI:
                    return KyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH:
                    return GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Monaito:
                    return Monaito(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HUNG_VUONG:
                    return HungVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.FIDE:
                    return Fide(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JIREN:
                    return Jiren(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUNG_LINH_THU:
                    return trungLinhThu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return poTaGe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_70:
                    return npc70(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS:
                    return vados(mapId, status, cx, cy, tempId, avatar);
//                case ConstNpc.POTAGE:
//                    return Potage(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG:
                    return npclytieunuong54(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG_CLTX:
                    return npclytieunuongCLTX(mapId, status, cx, cy, tempId, avatar);

                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:

                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.UNKOWN:
                    return unkow(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return duongtank(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THO_DAI_CA:
                    return thodaica(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NRO_JIN_CHILL:
                    return nrojinchill(mapId, status, cx, cy, tempId, avatar);
                

                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class, e, "Lỗi load npc");
            return null;
        }
    }

    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                Item[] items = new Item[4];
                items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2124);
                items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2125);
                items[2] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2129);
                items[3] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2130);
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP: //                        if (player.getSession().actived) 
                    {
                        if (Maintenance.isRuning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;

                    }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        if (select == 0) {
                            IntrinsicService.gI().sattd(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().satnm(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().setxd(player);
                        }
                        break;

                    case ConstNpc.MENU_OPTION_USE_ITEM2000:
                    case ConstNpc.MENU_OPTION_USE_ITEM2001:
                    case ConstNpc.MENU_OPTION_USE_ITEM2002:
                        try {
                        ItemService.gI().OpenSKH(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2085:
                    case ConstNpc.MENU_OPTION_USE_ITEM2086:
                    case ConstNpc.MENU_OPTION_USE_ITEM2087:
                        try {
                        ItemService.gI().openDoHuyDiet(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2003:
                    case ConstNpc.MENU_OPTION_USE_ITEM2004:
                    case ConstNpc.MENU_OPTION_USE_ITEM2005:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM736:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.ACTIVE_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().ActivePlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Activated  " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;

                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                }
                                InventoryServiceNew.gI().sendItemBags(player);
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                } else {
                                    if (player.pet.typePet == 1) {
//                                        PetService.gI().changePicPet(player);
                                    } else if (player.pet.typePet == 2) {
                                        PetService.gI().changeMabuPet(player);
                                    }
                                    PetService.gI().changeBerusPet(player);
                                }
                                break;
                            case 2:
                                if (player.isAdmin()) {
                                    System.out.println(player.name);
//                                PlayerService.gI().baoTri();
                                    Maintenance.gI().start(15);
                                    System.out.println(player.name);
                                }
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 4:
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "BROLY", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ");
                                break;
                            case 5:
                                Input.gI().createFormBuffVNDANDTOTALVND(player);
                                break;
                            case 6:
                                Input.gI().createFormSenditem1(player);
                                break;
//                            case 7:
//                                Input.gI().createFormSenditem2(player);
//                                break;
//                            case 8:
//                                Input.gI().createFormSendGoldBar(player);
//                                break;
//                            case 9:
//                                Input.gI().createFormBuffVND(player);
//                                break;
//                            case 10:
//                                Input.gI().createFormBuffToTalVND(player);
//                                break;
//                            case 11:
//                                Input.gI().createFormSenditem3(player);
//                                break;
//                            case 12:
//                                MaQuaTangManager.gI().checkInfomationGiftCode(player);
//                                break;
                        }
                        break;

                    case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossManager.gI().createBoss(BossID.ANDROID_13);
                                BossManager.gI().createBoss(BossID.ANDROID_14);
                                BossManager.gI().createBoss(BossID.ANDROID_15);
                                BossManager.gI().createBoss(BossID.ANDROID_19);
                                BossManager.gI().createBoss(BossID.DR_KORE);
                                BossManager.gI().createBoss(BossID.KING_KONG);
                                BossManager.gI().createBoss(BossID.PIC);
                                BossManager.gI().createBoss(BossID.POC);
                                break;
                            case 1:
                                BossManager.gI().createBoss(BossID.BLACK);
                                break;
                            case 2:
                                BossManager.gI().createBoss(BossID.BROLY);
                                break;
                            case 3:
                                BossManager.gI().createBoss(BossID.SIEU_BO_HUNG);
                                BossManager.gI().createBoss(BossID.XEN_BO_HUNG);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                BossManager.gI().createBoss(BossID.CHAIEN);
                                BossManager.gI().createBoss(BossID.XEKO);
                                BossManager.gI().createBoss(BossID.XUKA);
                                BossManager.gI().createBoss(BossID.NOBITA);
                                BossManager.gI().createBoss(BossID.DORAEMON);
                                break;
                            case 6:
                                BossManager.gI().createBoss(BossID.FIDE);
                                break;
                            case 7:
                                BossManager.gI().createBoss(BossID.FIDE_ROBOT);
                                BossManager.gI().createBoss(BossID.VUA_COLD);
                                break;
                            case 8:
                                BossManager.gI().createBoss(BossID.SO_1);
                                BossManager.gI().createBoss(BossID.SO_2);
                                BossManager.gI().createBoss(BossID.SO_3);
                                BossManager.gI().createBoss(BossID.SO_4);
                                BossManager.gI().createBoss(BossID.TIEU_DOI_TRUONG);
                                break;
                            case 9:
                                BossManager.gI().createBoss(BossID.KUKU);
                                BossManager.gI().createBoss(BossID.MAP_DAU_DINH);
                                BossManager.gI().createBoss(BossID.RAMBO);
                                break;
                            case 10:
                                BossManager.gI().createBoss(BossID.COOLER_GOLD);
                                BossManager.gI().createBoss(BossID.CUMBER);
                                BossManager.gI().createBoss(BossID.SONGOKU_TA_AC);
                                break;
                        }
                        break;

                    case ConstNpc.menutd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.settltd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settlkaio(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().settlgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().settlson(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.settlnm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settlpico(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().settloctieu(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().settlpiko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.settlxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settlgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().settlcadick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().settlnappa(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.sethdtd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().sethdkaio(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().sethdgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().sethdson(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.sethdnm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().sethdpico(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().sethdoctieu(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().sethdpiko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.sethdxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().sethdcadick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().sethdcadic(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().sethdnappa(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.settstd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.settsnm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.settsxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.getInstance().sendThongBao(player, "Đã giải tán bang hội.");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_ACTIVE:
                        switch (select) {
                            case 0:
                                if (player.getSession().goldBar >= 20) {
                                    if (player.session.actived == 1) {
                                        if (PlayerDAO.subGoldBar(player, 20)) {
                                            Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                                            break;
                                        } else {
                                            this.npcChat(player, "Lỗi vui lòng báo admin...");
                                        }
                                    }
                                }
//                                Service.getInstance().sendThongBao(player, "Bạn không có vàng\n Vui lòng NROGOD.COM để nạp thỏi vàng");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    Input.gI().createFormChangeName(player, p);
                                    break;
                                case 3:
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    break;
                                case 4:
                                    Service.getInstance().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                    break;
                                case 5:
                                    String[] selectss = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.ACTIVE_PLAYER, -1,
                                            "Bạn có chắc chắn muốn mở thành viên cho " + p.name, selectss, p);
                                    break;

                            }
                        }
                        break;
//                    case ConstNpc.MENU_EVENT:
//                        switch (select) {
//                            case 0:
//                                Service.getInstance().sendThongBaoOK(player, "Điểm sự kiện: " + player.inventory.event + " ngon ngon...");
//                                break;
//                            case 1:
//                                Util.showListTop(player, (byte) 2);
//                                break;
//                            case 2:
//                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
////                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_GIAO_BONG, -1, "Người muốn giao bao nhiêu bông...",
////                                        "100 bông", "1000 bông", "10000 bông");
//                                break;
//                            case 3:
//                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
////                                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN, -1, "Con có thực sự muốn đổi thưởng?\nPhải giao cho ta 3000 điểm sự kiện đấy... ",
////                                        "Đồng ý", "Từ chối");
//                                break;
//
//                        }
//                        break;
                    case ConstNpc.MENU_GIAO_BONG:
                        ItemService.gI().giaobong(player, (int) Util.tinhLuyThua(10, select + 2));
                        break;
                    case ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN:
                        if (select == 0) {
                            ItemService.gI().openBoxVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.getInstance().sendMoney(player);
                        }
                        break;
                    case ConstNpc.OPEN_GOI_BOSS_SU_KIEN_HE:
                        switch (select) {
                            case 0:
                                if (!MapService.gI().isMapSkhecallboss(player.zone.map.mapId)) {
                                    Service.getInstance().sendThongBao(player, "Vui lòng đến map được chỉ định");
                                    return;
                                }
                                if (items != null && items[0] != null && items[1] != null && items[2] != null && items[0].quantity >= 20 && items[1].quantity >= 20 && items[2].quantity >= 1) {
                                    List<Skill> skillList = new ArrayList<>();
                                    for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                        Skill skill = player.playerSkill.skills.get(i);
                                        if (skill.point > 0) {
                                            skillList.add(skill);
                                        }
                                    }
                                    int[][] skillTemp = new int[skillList.size()][3];
                                    for (byte i = 0; i < skillList.size(); i++) {
                                        Skill skill = skillList.get(i);
                                        if (skill.point > 0) {
                                            skillTemp[i][0] = skill.template.id;
                                            skillTemp[i][1] = skill.point;
                                            skillTemp[i][2] = skill.coolDown;
                                        }
                                    }

                                    BossData bossDataClone = new BossData(
                                            "Bọ Cánh Cứng",
                                            player.gender,
                                            new short[]{2027, 2028, 2029, -1, -1, -1},
                                            100,
                                            new int[]{500},
                                            new int[]{140},
                                            skillTemp,
                                            new String[]{}, //text chat 1
                                            new String[]{}, //text chat 2
                                            new String[]{}, //text chat 3
                                            60
                                    );
                                    try {
                                        new BoCanhCungVuong(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 20);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 20);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 1);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    CombineServiceNew.gI().sendEffectOpenItem(player, items[2].template.iconID, items[0].template.iconID);
                                } else {
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, "|7| Không đủ vật phẩm!",
                                            "Từ chối");
                                }
                                break;

                        }
                        break;
                    case ConstNpc.OPEN_GOI_BOSS_SU_KIEN_HE_1:
                        switch (select) {
                            case 0:
                                if (!MapService.gI().isMapSkhecallboss(player.zone.map.mapId)) {
                                    Service.getInstance().sendThongBao(player, "Vui lòng đến map được chỉ định");
                                    return;
                                }
                                if (items != null && items[0] != null && items[1] != null && items[2] != null && items[3] != null
                                        && items[0].quantity >= 20 && items[1].quantity >= 20 && items[2].quantity >= 1 && items[3].quantity >= 1) {
                                    List<Skill> skillList = new ArrayList<>();
                                    for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                        Skill skill = player.playerSkill.skills.get(i);
                                        if (skill.point > 0) {
                                            skillList.add(skill);
                                        }
                                    }
                                    int[][] skillTemp = new int[skillList.size()][3];
                                    for (byte i = 0; i < skillList.size(); i++) {
                                        Skill skill = skillList.get(i);
                                        if (skill.point > 0) {
                                            skillTemp[i][0] = skill.template.id;
                                            skillTemp[i][1] = skill.point;
                                            skillTemp[i][2] = skill.coolDown;
                                        }
                                    }

                                    BossData bossDataClone = new BossData(
                                            "Ngài Đêm",
                                            player.gender,
                                            new short[]{2030, 2031, 2032, -1, -1, -1},
                                            100,
                                            new int[]{500},
                                            new int[]{140},
                                            skillTemp,
                                            new String[]{}, //text chat 1
                                            new String[]{}, //text chat 2
                                            new String[]{}, //text chat 3
                                            60
                                    );
                                    try {
                                        new NgaiDem(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    CombineServiceNew.gI().sendEffectOpenItem(player, items[3].template.iconID, items[0].template.iconID);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[0], 20);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 20);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[2], 1);
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, items[3], 1);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                } else {
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, "|7| Không đủ vật phẩm!",
                                            "Từ chối");
                                }
                                break;
                        }
                        break;
                }
            }
        };
    }
}
