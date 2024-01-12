package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;

/**
 * @author BTH sieu cap vippr0 
 */
public class JackyChun extends BossDHVT {

    public JackyChun(Player player) throws Exception {
        super(BossID.JACKY_CHUN, BossesData.JACKY_CHUN);
        this.playerAtt = player;
    }
}