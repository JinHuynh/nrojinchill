/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinhuynh.models.boss.list_boss.NgucTu;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossManager;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.models.skill.Skill;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.PetService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;
import java.util.Random;

/**
 *
 * @Stole By JinChill
 */
public class Cumber extends Boss {

    public Cumber() throws Exception {
        super(BossID.CUMBER, BossesData.CUMBER);
    }

    @Override
    public void reward(Player plKill) {
           int[] itemDos = new int[]{555,556,557,558,559,560,561,562,563,564,566,567,565};
        int[] NRs = new int[]{17, 18};
        int randomDo = new Random().nextInt(itemDos.length);
        int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(15, 100)) {
            if (Util.isTrue(1, 50)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 561, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
       } else if (Util.isTrue(50, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    }
@Override
public void leaveMap(){
    super.leaveMap();
    super.dispose();
    BossManager.gI().removeBoss(this);
}
 

}
