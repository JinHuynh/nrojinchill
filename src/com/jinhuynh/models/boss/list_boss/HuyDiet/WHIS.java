package com.jinhuynh.models.boss.list_boss.HuyDiet;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.item.Item;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.server.Manager;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;
import java.util.Random;

public class WHIS extends Boss {

    public WHIS() throws Exception {
        super(Util.randomBossId(), BossesData.WHIS);
    }

     @Override
    public void reward(Player plKill) {
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        byte randomHD = (byte) new Random().nextInt(Manager.itemHD.length-1);
        ItemMap itemMap;
        if (Util.isTrue(8, 100)) {
            if (Util.isTrue(10, 100)) {
//                itemMap = Util.ratiItem(zone,1142, 1, this.location.x, this.location.y, plKill.id);
                             itemMap = Util.raitilHD(zone, Manager.itemHD[randomHD], 1, this.location.x, this.location.y, plKill.id);
            } else {
                itemMap = Util.ratiItem(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id);
            }
        } else {
            itemMap = Util.ratiItem(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id);
        }
        Service.gI().dropItemMap(this.zone, itemMap);
    }
    
}
