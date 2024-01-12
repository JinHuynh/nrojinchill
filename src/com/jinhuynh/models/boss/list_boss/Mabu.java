/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinhuynh.models.boss.list_boss;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;
import java.util.Random;

/**
 * @@Stole By Jinchill
 */
public class Mabu extends Boss {

    public Mabu() throws Exception {
        super(BossID.MABU, BossesData.MABU);
    }

    @Override
    public void reward(Player plKill) {
        int[] itemCt = new int[]{568};
        int randomDo = new Random().nextInt(itemCt.length);
        if (Util.isTrue(99, 100)) {
            if (Util.isTrue(1, 100)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 568, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.useItem(zone, itemCt[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } 
    }
    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
     
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
    
}
