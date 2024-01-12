package com.jinhuynh.models.boss.list_boss.NRD;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;


public class Rong2Sao extends Boss {

    public Rong2Sao() throws Exception {
        super(Util.randomBossId(), BossesData.Rong_2Sao);
    }

    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 373, 1, this.location.x, this.location.y, -1);
        Service.gI().dropItemMap(this.zone, it);
    }
@Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage/3);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                  damage = damage/2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}


