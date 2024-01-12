package com.jinhuynh.models.boss.list_boss.Sukienhe;

import com.jinhuynh.models.boss.*;
import com.jinhuynh.models.item.Item;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.map.Zone;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;

/**
 * @author Khoa
 */
public class BoCanhCungVuong extends Boss {

    public BoCanhCungVuong(int bossID, BossData bossData, Zone zone) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {

        ItemMap it = new ItemMap(this.zone, 2127, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        it.options.clear();
        it.options.add(new Item.ItemOption(30, 0));
        Service.getInstance().dropItemMap(this.zone, it);
    }
   @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(30, 100)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (damage >= 20) {
                damage = 20;
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
