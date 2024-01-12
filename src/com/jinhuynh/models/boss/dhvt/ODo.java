package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;

/**
 * @author BTH sieu cap vippr0 
 */
public class ODo extends BossDHVT {

    public ODo(Player player) throws Exception {
        super(BossID.O_DO, BossesData.O_DO);
        this.playerAtt = player;
    }
}
