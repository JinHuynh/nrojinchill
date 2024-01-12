package com.jinhuynh.models.boss.list_boss.android;

import com.jinhuynh.consts.ConstMob;
import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossManager;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.mob.Mob;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.PetService;
import com.jinchill.services.Service;
import com.jinchill.services.TaskService;
import com.jinhuynh.utils.Util;
import java.util.Random;

public class SuperAndroid17 extends Boss {

    public SuperAndroid17() throws Exception {
        super(BossID.SUPER_ANDROID_17, BossesData.SUPER_ANDROID_17);
          this.nPoint.defg = (short) (this.nPoint.hpg / 1000);
        if (this.nPoint.defg < 0) {
            this.nPoint.defg = (short) -this.nPoint.defg;
    }
    }
   @Override
    public void reward(Player plKill) {
       int[] itemDos = new int[]{233, 237, 241, 245, 249, 253, 257, 261, 265, 269, 273, 277, 281};
        //int[] NRs = new int[]{17,18};
       int randomc12 = new Random().nextInt(itemDos.length);
        //int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(60, 100)) {
            if (Util.isTrue(1, 50)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, Util.nextInt(16,17), 1, this.location.x, this.location.y, plKill.id));
                return;
            }
           Service.gI().dropItemMap(this.zone, Util.RaitiDoc12(zone, itemDos[randomc12], 1, this.location.x, this.location.y, plKill.id));
       // } else if (Util.isTrue(50, 100)) {
         //   Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage/3);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage/2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

@Override
    public void active() {
        super.active();
    }


    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
 @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
