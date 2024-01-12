package com.jinhuynh.models.mob;

import com.jinhuynh.consts.ConstMap;
import com.jinhuynh.consts.ConstMob;
import com.jinhuynh.consts.ConstTask;
import com.jinhuynh.models.item.Item;
import com.jinhuynh.models.map.ItemMap;

import java.util.List;

import com.jinhuynh.models.map.Zone;
import com.jinhuynh.models.player.Location;
import com.jinhuynh.models.player.Pet;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.models.reward.ItemMobReward;
import com.jinhuynh.models.reward.MobReward;
import com.girlkun.network.io.Message;
import com.jinhuynh.server.Maintenance;
import com.jinhuynh.server.Manager;
import com.jinhuynh.server.ServerManager;
import com.jinchill.services.*;
import com.jinhuynh.utils.Util;

import java.util.ArrayList;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public synchronized void injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124) {
            tiemNang *= 20;
        }
        return tiemNang;
    }

    public void update() {
        if (this.tempId == 71) {
            try {
                Message msg = new Message(102);
                msg.writer().writeByte(5);
                msg.writer().writeShort(this.zone.getPlayers().get(0).location.x);
                Service.gI().sendMessAllPlayerInMap(zone, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }

        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0) && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
//                MobService.gI().mobAttackPlayer(this, pl);
                this.mobAttackPlayer(pl);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    private Player getPlayerCanAttack() {
        int distance = 100;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    //**************************************************************************
    private void mobAttackPlayer(Player player) {
        int dameMob = this.point.getDameAttack();
        if (player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        int dame = player.injured(null, dameMob, false, true);
        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
    }

    private void sendMobAttackMe(Player player, int dame) {
        if (!player.isPet && !player.isNewPet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(dame); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(player.nPoint.hp);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
public static void initMobBanDoKhoBau(Mob mob, byte level) {
        mob.point.dame = level * 3250 * mob.level * 4;
        mob.point.maxHp = level * 12472 * mob.level * 2 + level * 7263 * mob.tempId;
    }

    public static void hoiSinhMob(Mob mob) {
        mob.point.hp = mob.point.maxHp;
        mob.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(mob.id);
            msg.writer().writeByte(mob.tempId);
            msg.writer().writeByte(0); //level mob
            msg.writer().writeInt((mob.point.hp));
            Service.getInstance().sendMessAllPlayerInMap(mob.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    
public void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob);
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    //**************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, int dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(player, item.itemMapId, true);
                    }
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                    }
                }
            }
        }
    }

     private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            if ((!player.isPet && !player.isBoss && !player.isNewPet&& player.setClothes.godClothes == 5&&  (this.zone.map.mapId > 104 && this.zone.map.mapId < 111 || this.zone.map.mapId == 159))) {
                if (Util.isTrue(7, 100)) {
                    Item linhThu = ItemService.gI().createNewItem(Manager.thucan[(Util.nextInt(0,4))]);
                     Service.getInstance().sendThongBao(player, "You received item " + linhThu.template.name);
                     InventoryServiceNew.gI().addItemBag(player, linhThu);
                     InventoryServiceNew.gI().sendItemBags(player);
              }
                 } 
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(this.tempId);
        if (mobReward == null) {
            return list;
        }
        List<ItemMobReward> items = mobReward.getItemReward();
        List<ItemMobReward> golds = mobReward.getGoldReward();
        if (!items.isEmpty()) {
            ItemMobReward item = items.get(Util.nextInt(0, items.size() - 1));
            ItemMap itemMap = item.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (!golds.isEmpty()) {
            ItemMobReward gold = golds.get(Util.nextInt(0, golds.size() - 1));
            ItemMap itemMap = gold.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (player.itemTime.isUseMayDo && Util.isTrue(21, 100) && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
        }// vat phẩm rơi khi user maaáy dò adu hoa r o day ti code choa
         Item item = player.inventory.itemsBody.get(1);
        if (this.zone.map.mapId > 0){
            if(item.isNotNullItem()){
            if (item.template.id == 691){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(1, 2))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        }if (this.zone.map.mapId >= 0){
            if(item.isNotNullItem()){
            if (item.template.id == 692){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(0, 1))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        }if (this.zone.map.mapId > 0){
            if(item.isNotNullItem()){
            if (item.template.id == 693){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(1, 2))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
            }if (this.zone.map.mapId > 0){
            if(item.isNotNullItem()){
            if (item.template.id == 693 || item.template.id == 692 || item.template.id == 691 ){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(2116,2119), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(1, 2))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        // rơi mảnh vỡ bông tai
        if (this.zone.map.mapId == 153 ) {
            if(Util.isTrue(20, 100)){
                Item Manhvobongtai = ItemService.gI().createNewItem((short) (933));
                InventoryServiceNew.gI().addItemBag(player, Manhvobongtai);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Manhvobongtai.template.name);
                            }}
         //Roi Do Than Cold
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Quanthanlinh = ItemService.gI().createNewItem((short) (556));
                Quanthanlinh.itemOptions.add(new Item.ItemOption(22, Util.nextInt(55,65)));
                Quanthanlinh.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Quanthanlinh);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Quanthanlinh.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Quanthanlinhxd = ItemService.gI().createNewItem((short) (560));
                Quanthanlinhxd.itemOptions.add(new Item.ItemOption(22, Util.nextInt(45,55)));
                Quanthanlinhxd.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Quanthanlinhxd);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Quanthanlinhxd.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Quanthanlinhnm = ItemService.gI().createNewItem((short) (558));
                Quanthanlinhnm.itemOptions.add(new Item.ItemOption(22, Util.nextInt(50,60)));
                Quanthanlinhnm.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Quanthanlinhnm);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Quanthanlinhnm.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Aothanlinh = ItemService.gI().createNewItem((short) (555));
                Aothanlinh.itemOptions.add(new Item.ItemOption(47, Util.nextInt(500,600)));
                Aothanlinh.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Aothanlinh);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Aothanlinh.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Aothanlinhxd = ItemService.gI().createNewItem((short) (559));
                Aothanlinhxd.itemOptions.add(new Item.ItemOption(47, Util.nextInt(600,700)));
                Aothanlinhxd.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Aothanlinhxd);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Aothanlinhxd.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Aothanlinhnm = ItemService.gI().createNewItem((short) (557));
                Aothanlinhnm.itemOptions.add(new Item.ItemOption(47, Util.nextInt(400,550)));
                Aothanlinhnm.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Aothanlinhnm);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Aothanlinhnm.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Gangthanlinh = ItemService.gI().createNewItem((short) (562));
                Gangthanlinh.itemOptions.add(new Item.ItemOption(0, Util.nextInt(6000,7000)));
                Gangthanlinh.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Gangthanlinh);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Gangthanlinh.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Gangthanlinhxd = ItemService.gI().createNewItem((short) (566));
                Gangthanlinhxd.itemOptions.add(new Item.ItemOption(0, Util.nextInt(6500,7500)));
                Gangthanlinhxd.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Gangthanlinhxd);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Gangthanlinhxd.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Gangthanlinhnm = ItemService.gI().createNewItem((short) (564));
                Gangthanlinhnm.itemOptions.add(new Item.ItemOption(0, Util.nextInt(5500,6500)));
                Gangthanlinhnm.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Gangthanlinhnm);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Gangthanlinhnm.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 50000)){
                Item Giaythanlinh = ItemService.gI().createNewItem((short) (563));
                Giaythanlinh.itemOptions.add(new Item.ItemOption(23, Util.nextInt(50,60)));
                Giaythanlinh.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Giaythanlinh);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Giaythanlinh.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Giaythanlinhxd = ItemService.gI().createNewItem((short) (567));
                Giaythanlinhxd.itemOptions.add(new Item.ItemOption(23, Util.nextInt(55,65)));
                Giaythanlinhxd.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Giaythanlinhxd);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Giaythanlinhxd.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Giaythanlinhnm = ItemService.gI().createNewItem((short) (565));
                Giaythanlinhnm.itemOptions.add(new Item.ItemOption(23, Util.nextInt(65,75)));
                Giaythanlinhnm.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Giaythanlinhnm);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Giaythanlinhnm.template.name);
                            }}
        if (this.zone.map.mapId >= 105 && this.zone.map.mapId <= 110) {
            if(Util.isTrue(1, 100000)){
                Item Giaythanlinhnm = ItemService.gI().createNewItem((short) (561));
                Giaythanlinhnm.itemOptions.add(new Item.ItemOption(14, Util.nextInt(13,16)));
                Giaythanlinhnm.itemOptions.add(new Item.ItemOption(21, Util.nextInt(15,17)));
                InventoryServiceNew.gI().addItemBag(player, Giaythanlinhnm);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "+Giaythanlinhnm.template.name);
                            }}
        return list;
    }

//        }if (this.zone.map.mapId == 155){
//            if(item.isNotNullItem()){
//             if (item.template.id >= 650 && item.template.id <= 662) {
//        if (Util.isTrue(30, 100)) {    //up bí kíp
//            list.add(new ItemMap(zone, Util.nextInt(1066,1070), 1, x, player.location.y, player.id));}
//        }else if (item.template.id >= 650 && item.template.id <= 662) {
//            if (Util.isTrue(1, 2))
//            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
//        }
//        }
        
//        if (player.itemTime.isUseMayDo2 && Util.isTrue(7, 100) && this.tempId > 1 && this.tempId < 81) {
//            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));// cai nay sua sau nha
//        }
    
       

//        if (player.isPet && player.getSession().actived && Util.isTrue(15, 100)) {
//            list.add(new ItemMap(zone, 610, 1, x, player.location.y, player.id));
//        }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(int dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
}
