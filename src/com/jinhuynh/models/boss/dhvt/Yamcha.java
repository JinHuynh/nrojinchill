package com.jinhuynh.models.boss.dhvt;

import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.boss.dhvt.BossDHVT;
import com.jinhuynh.models.player.Player;


/**
 *
 * @author BTH fix
 */
public class Yamcha extends BossDHVT {

    public Yamcha(Player player) throws Exception {
        super(BossID.YAMCHA, BossesData.YAMCHA);
        this.playerAtt = player;
    }
}
