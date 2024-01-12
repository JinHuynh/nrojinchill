package com.jinhuynh.models.boss.list_boss.Mabu12h;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.server.Manager;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;

import java.util.Random;

public class Yacon extends Boss {

    public Yacon() throws Exception {
        super(Util.randomBossId(), BossesData.YACON);
    }

    @Override
    public void reward(Player plKill) {
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        byte randomc12 = (byte) new Random().nextInt(Manager.itemDC12.length -1);

        if (Util.isTrue(1, 200)) {
            if (Util.isTrue(1, 200)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 561, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else
        if (Util.isTrue(1, 200)) {
            Service.gI().dropItemMap(this.zone,new ItemMap (Util.RaitiDoc12(zone, Manager.itemDC12[randomc12], 1, this.location.x, this.location.y, plKill.id)));
            return;
        }
        else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id));
        }
        plKill.fightMabu.changePoint((byte) 20);
    }

//    @Override
//    public void active() {
//        super.active(); //To change body of generated methods, choose Tools | Templates.
//        if (Util.canDoWithTime(st, 300000)) {
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
//    }
//
//    @Override
//    public void joinMap() {
//        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
//        st = System.currentTimeMillis();
//    }
//    private long st;

}





















