package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;
/**
 * @author BTH sieu cap vippr0 
 */
public class ChanXu extends BossDHVT {

    public ChanXu(Player player) throws Exception {
        super(BossID.CHAN_XU, BossesData.CHAN_XU);
        this.playerAtt = player;
    }
}