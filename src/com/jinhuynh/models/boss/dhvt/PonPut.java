package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;

/**
 * @author BTH sieu cap vippr0 
 */
public class PonPut extends BossDHVT {

    public PonPut(Player player) throws Exception {
        super(BossID.PON_PUT, BossesData.PON_PUT);
        this.playerAtt = player;
    }
}