package com.jinhuynh.models.npc;

import com.jinhuynh.models.boss.*;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.map.Zone;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;

/**
 * @Stole By SS
 */
public class NguoiChoi extends Boss {

    public NguoiChoi(int bossID, BossData bossData, Zone zone) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        //vật phẩm rơi khi diệt boss nhân bản
        ItemMap it = new ItemMap(this.zone, Util.nextInt(19, 20), Util.nextInt(3, 4), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it);
    }
    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
