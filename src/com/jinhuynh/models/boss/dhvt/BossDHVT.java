package com.jinhuynh.models.boss.dhvt;

import com.girlkun.network.io.Message;
import com.jinhuynh.consts.ConstRatio;
import com.jinhuynh.models.boss.Boss;
import com.jinhuynh.models.boss.BossData;
import com.jinhuynh.models.boss.BossManager;
import com.jinhuynh.models.boss.BossStatus;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.models.skill.Skill;
import com.jinchill.services.EffectSkillService;
import com.jinchill.services.PlayerService;
import com.jinchill.services.SkillService;
import com.jinhuynh.services.func.ChangeMapService;
import com.jinhuynh.utils.SkillUtil;
import com.jinhuynh.utils.Util;

/**
 * @author BTH sieu cap vippr0 
 */
public abstract class BossDHVT extends Boss {

    protected Player playerAtt;
    protected long timeJoinMap;

    public BossDHVT(byte id, BossData data) throws Exception {
        super(id, data);
        this.bossStatus = BossStatus.RESPAWN;
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    @Override
    public void attack() {
        try {
            if (Util.canDoWithTime(timeJoinMap, 10000)) {
                if (playerAtt.location != null && playerAtt != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone)) {
                    if (this.isDie()) {
                        return;
                    }
                    this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                    if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)), Util.nextInt(10) % 2 == 0 ? playerAtt.location.y : playerAtt.location.y - Util.nextInt(0, 50), false);
                        }
                        SkillService.gI().useSkill(this, playerAtt, null,null);
                        checkPlayerDie(playerAtt);
                    } else {
                        goToPlayer(playerAtt, false);
                    }
                } else {
                    this.leaveMap();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 435, 264);
        }
    }

    protected void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }
    

    @Override
    public void update() {
        try {
            switch (this.bossStatus) {
                case RESPAWN:
                    this.respawn();
                    this.changeStatus(BossStatus.JOIN_MAP);
                case JOIN_MAP:
                    joinMap();
                    if (this.zone != null) {
                        changeStatus(BossStatus.ACTIVE);
                        timeJoinMap = System.currentTimeMillis();
                        this.immortalMp();
                        this.typePk = 3;
                    }
                    break;
                case ACTIVE:
                    if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze || this.playerSkill.prepareQCKK) {
                        break;
                    } else {
                        this.attack();
                    }
                    break;
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void notifyPlayeKill(Player player) {

    }
    // HỒI SINH SKILL KHI TIÊU DIỆT BOSS DHVT
//    @Override
//    public void die(Player plKill) {
//        // Hồi lại skill cho người chơi khi boss bị tiêu diệt
//        releaseCooldownSkill(plKill);
//        super.die(plKill);
//    }
//    
//    public void releaseCooldownSkill(Player pl) {
//        Message msg;
//        try {
//            msg = new Message(-94);
//            for (Skill skill : pl.playerSkill.skills) {
//                skill.coolDown = 0;
//                msg.writer().writeShort(skill.skillId);
//                int leftTime = (int) (skill.lastTimeUseThisSkill + skill.coolDown - System.currentTimeMillis());
//                if (leftTime < 0) {
//                    leftTime = 0;
//                }
//                msg.writer().writeInt(leftTime);
//            }
//            pl.sendMessage(msg);
//            pl.nPoint.setMp(pl.nPoint.mpMax);
//            PlayerService.gI().sendInfoHpMpMoney(pl);
//            msg.cleanup();
//
//        } catch (Exception e) {
//        }
//    }
    @Override
    public void die(Player plKill) {
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
