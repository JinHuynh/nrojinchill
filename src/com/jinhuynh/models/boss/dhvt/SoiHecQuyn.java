package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.player.Player;

/**
 * @author BTH sieu cap vippr0 
 */
public class SoiHecQuyn extends BossDHVT {
    public SoiHecQuyn(Player player) throws Exception {
        super(BossID.SOI_HEC_QUYN, BossesData.SOI_HEC_QUYN);
        this.playerAtt = player;
    }
}
