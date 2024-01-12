package com.jinhuynh.models.pvp_old;

//package com.jinhuynh.models.pvp_old;
//
//import com.jinhuynh.consts.ConstPlayer;
//import com.jinhuynh.models.player.Player;
//import com.jinchill.services.PlayerService;
//import com.jinchill.services.Service;
//import com.jinhuynh.utils.Util;
//
///**
// *
// * @Stole By Jinchill 💖
// *
// */
//public class RevengePVP extends PVP {
//
//    private static final int TIME_WAIT = 3000;
//
//    private boolean changeTypePk;
//    public long lastTimeGoToMapEnemy;
//
//    public RevengePVP(Player player, Player enemy) {
//        this.player1 = player;
//        this.player2 = enemy;
//        this.typePVP = TYPE_PVP_REVENGE;
//    }
//
//    @Override
//    public void update() {
//        if (!changeTypePk && Util.canDoWithTime(lastTimeGoToMapEnemy, TIME_WAIT)) {
//            changeTypePk = true;
//            if (player1.zone.equals(player2.zone)) {
//                Service.getInstance().chat(player1, "Mau đền tội");
//                Service.getInstance().sendThongBao(player2, "Có người tìm bạn trả thù");
//                super.start();
//                PlayerService.gI().changeAndSendTypePK(this.player1, ConstPlayer.PK_PVP);
//                PlayerService.gI().changeAndSendTypePK(this.player2, ConstPlayer.PK_PVP);
//            } else {
//                PVPServcice.gI().removePVP(this);
//                return;
//            }
//        }
//        super.update();
//    }
//
//    @Override
//    public void sendResultMatch(Player winer, Player loser, byte typeWin) {
//        switch (typeWin) {
//            case PVP.TYPE_DIE:
//                Service.getInstance().chat(winer, "Chừa nha " + loser.name);
//                Service.getInstance().chat(loser, "Cay quá");
//                break;
//            case PVP.TYPE_LEAVE_MAP:
//                Service.getInstance().chat(winer, loser.name + " suy cho cùng cũng chỉ là con thỏ đế");
//                break;
//        }
//    }
//
//    @Override
//    public void reward(Player plWin) {
//    }
//
//}
