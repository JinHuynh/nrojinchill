package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;

/**
 * @author BTH sieu cap vippr0 
 */
public class Xinbato extends BossDHVT {

    public Xinbato(Player player) throws Exception {
        super(BossID.XINBATO, BossesData.XINBATO);
        this.playerAtt = player;
    }
}