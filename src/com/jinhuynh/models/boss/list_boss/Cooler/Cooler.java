package com.jinhuynh.models.boss.list_boss.Cooler;

//package com.jinhuynh.models.boss.list_boss.Cooler;
//
//import com.jinhuynh.models.boss.Boss;
//import com.jinhuynh.models.boss.BossID;
//import com.jinhuynh.models.boss.BossStatus;
//import com.jinhuynh.models.boss.BossesData;
//import com.jinhuynh.models.map.ItemMap;
//import com.jinhuynh.models.player.Player;
//import com.jinhuynh.server.Manager;
//import com.jinhuynh.services.EffectSkillService;
//import com.jinhuynh.services.Service;
//import com.jinhuynh.utils.Util;
//
//import java.util.Random;
//
//public class Cooler extends Boss {
//
//    public Cooler() throws Exception {
//        super(BossID.COOLER, BossesData.COOLER_1,BossesData.COOLER_2);
//    }
//
//    @Override
//    public void reward(Player plKill) {
//        if (this.currentLevel == 0 ){
//                  int[] itemDos = new int[]{233, 237, 241,245, 249, 253,257, 261, 265,269, 273, 277,281};
//        int[] itemtime = new int[]{381,382,383,384,385};
//        int randomDo = new Random().nextInt(itemDos.length);
//        if (Util.isTrue(10, 100)) {
//            if (Util.isTrue(1, 5)) {
//                Service.gI().dropItemMap(this.zone, Util.RaitiDoc12(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
//                
//            }
//                      Service.gI().dropItemMap(this.zone, new ItemMap(zone, 2094, Util.nextInt(3), this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
//        } else {
//            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 16, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
//        }
//        }
//        else {
//                  byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
//        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
//        int[] itemDos = new int[]{555,556,557};
//            if (Util.isTrue(10, 100)) {
//                Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
//        } else {
//            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
//        }
//        }
//    }
//
//
//    @Override
//    public void active() {
//        super.active(); //To change body of generated methods, choose Tools | Templates.
//        if (Util.canDoWithTime(st, 300000)) {
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
//    }
//    @Override
//    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
//        if (!this.isDie()) {
//            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
//                this.chat("Xí hụt");
//                return 0;
//            }
//            damage = this.nPoint.subDameInjureWithDeff(damage/3);
//            if (!piercing && effectSkill.isShielding) {
//                if (damage > nPoint.hpMax) {
//                    EffectSkillService.gI().breakShield(this);
//                }
//                damage = damage;
//            }
//            this.nPoint.subHP(damage);
//            if (isDie()) {
//                this.setDie(plAtt);
//                die(plAtt);
//            }
//            return damage;
//        } else {
//            return 0;
//        }
//    }
//    @Override
//    public void joinMap() {
//        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
//        st = System.currentTimeMillis();
//    }
//    private long st;
//
//}
