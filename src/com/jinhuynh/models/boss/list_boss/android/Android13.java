package com.jinhuynh.models.boss.list_boss.android;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.Service;
import com.jinchill.services.TaskService;
import com.jinhuynh.utils.Util;
import java.util.Random;


public class Android13 extends Boss {

    public Android13() throws Exception {
        super(BossID.ANDROID_13, BossesData.ANDROID_13);
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
    public void doneChatS() {
        if (this.parentBoss == null) {
            return;
        }
        if (this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.ANDROID_15 && !boss.isDie()) {
                boss.changeToTypePK();
                break;
            }
        }
        this.parentBoss.changeToTypePK();
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (damage >= this.nPoint.hp) {
            boolean flag = true;
            if (this.parentBoss != null) {
                if (this.parentBoss.bossAppearTogether != null && this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] != null) {
                    for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
                        if (boss.id == BossID.ANDROID_15 && !boss.isDie()) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag && !this.parentBoss.isDie()) {
                    flag = false;
                }
            }
            if (!flag) {
                return 0;
            }
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
