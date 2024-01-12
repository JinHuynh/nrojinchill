package com.jinhuynh.models.boss.list_boss.doanh_trai;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;


public class TrungUyTrang extends Boss{

    public TrungUyTrang() throws Exception{
        super(BossID.TRUNG_UY_TRANG, BossesData.TRUNG_UY_TRANG);
    }
    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, 19, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
        Service.gI().dropItemMap(this.zone, it);
        }
    }
    
}





















