package com.jinhuynh.models.boss.list_boss.fide;

import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossID;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.boss.BossesData;
import com.jinhuynh.models.map.ItemMap;
import com.jinhuynh.models.player.Player;
import com.jinchill.services.Service;
import com.jinchill.services.TaskService;
import com.jinhuynh.utils.Util;
import java.util.Random;


public class Fide extends Boss {

    public Fide() throws Exception {
        super(BossID.FIDE, BossesData.FIDE_DAI_CA_1, BossesData.FIDE_DAI_CA_2, BossesData.FIDE_DAI_CA_3);
    }
    @Override
    public void reward(Player plKill) {
       int[] itemDos = new int[]{233, 237, 241, 245, 249, 253, 257, 261, 265, 269, 273, 277, 281};
        //int[] NRs = new int[]{17,18};
       int randomc12 = new Random().nextInt(itemDos.length);
        //int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(60, 100)) {
            if (Util.isTrue(1, 50)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, Util.nextInt(16,17), 1, this.location.x, this.location.y, plKill.id));
                return;
            }
           Service.gI().dropItemMap(this.zone, Util.RaitiDoc12(zone, itemDos[randomc12], 1, this.location.x, this.location.y, plKill.id));
       // } else if (Util.isTrue(50, 100)) {
         //   Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }
      @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if(Util.canDoWithTime(st,900000)){
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st= System.currentTimeMillis();
    }
    private long st;

}






















