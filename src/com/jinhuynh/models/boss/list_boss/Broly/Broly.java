package com.jinhuynh.models.boss.list_boss.Broly;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
//import com.jinhuynh.models.boss.bdkb.TrungUyXanhLo;
import com.jinhuynh.models.boss.list_boss.NhanBan;
import com.jinhuynh.models.boss.list_boss.Super;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.models.skill.Skill;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.SkillService;
import com.jinchill.services.PetService;
import com.jinchill.services.Service;
import com.jinhuynh.utils.Util;
import java.util.Random;


// Đoạn mã của lớp Broly
public class Broly extends Boss {

    public Broly() throws Exception {
        super(BossID.BROLY, BossesData.BROLY_1, BossesData.BROLY_2, BossesData.BROLY_3);
    }

    @Override
    public void reward(Player plKill) {
        // ... Phần mã xử lý phần thưởng khi tiêu diệt boss Broly ...

        // Kiểm tra xem người chơi đã có đệ tử hay chưa
        if (plKill.pet == null) {
            PetService.gI().createNormalPet(plKill, plKill.gender);
            Service.getInstance().sendThongBao(plKill, "Bạn vừa nhận được đệ tử");
        } else {
            Service.getInstance().sendThongBao(plKill, "Bạn đã tiêu diệt được Broly");
        }
    }

    // Các phương thức và thuộc tính khác của lớp Broly...
}


//public class Broly extends Boss {
//
//    public Broly() throws Exception {
//        super(BossID.BROLY , BossesData.BROLY_1,BossesData.BROLY_2, BossesData.BROLY_3);
//    }
//     @Override
//    public void reward(Player plKill) {
//      int[] itemDos = new int[]{2029};
//        int[] NRs = new int[]{2029};
//        int randomDo = new Random().nextInt(itemDos.length);
//        int randomNR = new Random().nextInt(NRs.length);
//        if (Util.isTrue(90, 100)) {
//            if (Util.isTrue(40, 50)) {
//                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 17, 1, this.location.x, this.location.y, plKill.id));
//                return;
//            }
//            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
//        } else {
//            Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, this.location.y, plKill.id));
//        }
//    }  
//    @Override
//    public void active() {
//        super.active(); //To change body of generated methods, choose Tools | Templates.
//        if(Util.canDoWithTime(st,900000)){
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
//    }
//
//    @Override
//    public void joinMap() {
//        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
//        st= System.currentTimeMillis();
//    }
//    private long st;
//    @Override
//    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
//        if (!this.isDie()) {
//            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
//                this.chat("Xí hụt");
//                return 0;
//            }
//            damage = this.nPoint.subDameInjureWithDeff(damage/2);
//            if (!piercing && effectSkill.isShielding) {
//                if (damage > nPoint.hpMax) {
//                    EffectSkillService.gI().breakShield(this);
//                }
//                damage = damage/2;
//            }
//            this.nPoint.subHP(damage);
//            if (isDie()) {
//                this.setDie(plAtt);
//                die(plAtt);
//            }
//            return damage;
//        } else {
//            return 0;
//        }
//    }
//}