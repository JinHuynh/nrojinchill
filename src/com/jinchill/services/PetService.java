package com.jinchill.services;

import com.jinhuynh.consts.ConstPlayer;
import com.jinhuynh.models.player.NewPet;
import com.jinhuynh.models.player.Pet;
import com.jinhuynh.models.player.Player;
import com.jinhuynh.services.func.ChangeMapService;
import com.jinhuynh.utils.SkillUtil;
import com.jinhuynh.utils.Util;

public class PetService {

    private static PetService i;

    public static PetService gI() {
        if (i == null) {
            i = new PetService();
        }
        return i;
    }

    public void createNormalPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createNormalPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createMabuPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createMabuPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createBerusPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Tề thiên đại thánh đại náo thiên cung...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createBerusPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Tề thiên đại thánh đại náo thiên cung...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createAnubisPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, true);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Thần Anubis hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createAnubisPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, true, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Thần Anubis hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createPicPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Ham ăn háo sắc là thứ mà ta không có hahahaha....");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createPicPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Ham ăn háo sắc là thứ mà ta không có hahahaha....");
            } catch (Exception e) {
            }
        }).start();
    }

    public void changeNormalPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, gender, limitPower);
    }

    public void changeNormalPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, limitPower);
    }

    public void changeMabuPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, limitPower);
    }

    public void changeMabuPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, gender, limitPower);
    }

    public void changeBerusPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBerusPet(player, limitPower);
    }

    public void changeBerusPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBerusPet(player, gender, limitPower);
    }

    public void changeAnubisPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createAnubisPet(player, limitPower);
    }

    public void changeAnubisPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createAnubisPet(player, gender, limitPower);
    }

    public void changePicPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createPicPet(player, limitPower);
    }

    public void changePicPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createPicPet(player, gender, limitPower);
    }
     public void createGoku4Pet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet2(player, true, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }
      public void changeGoku4Pet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createGoku4Pet(player, gender, limitPower);
    }
public void createHeartPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet2(player, false, true, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }
      public void changeHeartPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createHeartPet(player, gender, limitPower);
    }
public void createGojoPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet2(player, false, false, true, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }
      public void changeGojoPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createGojoPet(player, gender, limitPower);
    }
public void createGokuVocucPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet2(player, false, false, false, true, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
            } catch (Exception e) {
            }
        }).start();
    }
      public void changeGokuVocucPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createGokuVocucPet(player, gender, limitPower);
    }

    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryServiceNew.gI().isExistItemBag(player, 400)) {
                Service.gI().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.gI().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.gI().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.name = "$" + name.toLowerCase().trim();
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 400), 1);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.gI().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                }
            }).start();
        } catch (Exception ex) {

        }
    }

    private int[] getDataPetNormal() {
        int[] hpmp = { 1700, 1800, 1900, 2000, 2100, 2200 };
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(20, 45); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetMabu() {
        int[] hpmp = { 1700, 1800, 1900, 2000, 2100, 2200 };
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(50, 120); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetAnubis() {
        int[] hpmp = { 1700, 1800, 1900, 2000, 2100, 2200 };
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(50, 120); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetPic() {
        int[] hpmp = { 1800, 1900, 2000, 2100, 2200, 2300 };
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 115) * 20; // hp
        petData[1] = Util.nextInt(40, 115) * 20; // mp
        petData[2] = Util.nextInt(70, 140); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private void createNewPet(Player player, boolean isMabu, boolean isBerus, boolean isPic, boolean isAnubis,
            byte... gender) {
        int[] data = isAnubis ? isMabu ? isPic ? getDataPetAnubis() : getDataPetMabu() : getDataPetPic()
                : getDataPetNormal();
        Pet pet = new Pet(player);
        pet.name = "$" + (isAnubis ? "Anubis" : isMabu ? "Mabư" : isBerus ? "Ngộ Không" : isPic ? "Bát giới" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = -player.id;
        pet.nPoint.power = isMabu || isBerus || isPic ? 1500000 : 2000;
        pet.typePet = (byte) (isMabu ? 1 : isBerus ? 2 : isPic ? 3 : isAnubis ? 4 : 0);
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 7; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 3; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }
      private void createNewPet2(Player player, boolean isGoku4, boolean isHeart, boolean isGojo, boolean isGokuVocuc,
            byte... gender) {
        int[] data = isGoku4 ? isHeart ? isGojo ? isGokuVocuc ? getDataPetAnubis() : getDataPetMabu() : getDataPetPic()
                : getDataPetNormal() : getDataPetNormal ();
        Pet pet = new Pet(player);
        pet.name = "$" + (isGoku4 ? "Goku SSJ4" : isHeart ? "Heart " : isGojo ? "Gojo" : isGokuVocuc ? "Goku Ultra" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = -player.id;
        pet.nPoint.power = isGoku4 || isHeart || isGojo || isGokuVocuc ? 1500000 : 2000;
        pet.typePet = (byte) (isGoku4 ? 5 : isHeart ? 6 : isGojo ? 7 : isGokuVocuc ? 8 : 0);
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 7; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 3; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    public static void Pet2(Player pl, int h, int b, int l) {
        if (pl.newpet != null && pl.newpet1 != null) {
            pl.newpet.dispose();
            pl.newpet1.dispose();
        }
        pl.newpet = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newpet1 = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newpet.name = "$";
        pl.newpet1.name = "$";
        pl.newpet.gender = pl.gender;
        pl.newpet1.gender = pl.gender;
        pl.newpet.nPoint.tiemNang = 1;
        pl.newpet1.nPoint.tiemNang = 1;
        pl.newpet.nPoint.power = 1;
        pl.newpet1.nPoint.power = 1;
        pl.newpet.nPoint.limitPower = 1;
        pl.newpet1.nPoint.limitPower = 1;
        pl.newpet.nPoint.hpg = 500000000;
        pl.newpet1.nPoint.hpg = 500000000;
        pl.newpet.nPoint.mpg = 500000000;
        pl.newpet1.nPoint.mpg = 500000000;
        pl.newpet.nPoint.hp = 500000000;
        pl.newpet.nPoint.mp = 500000000;
        pl.newpet.nPoint.dameg = 1;
        pl.newpet.nPoint.defg = 1;
        pl.newpet.nPoint.critg = 1;
        pl.newpet.nPoint.stamina = 1;
        pl.newpet.nPoint.setBasePoint();
        pl.newpet.nPoint.setFullHpMp();
        pl.newpet1.nPoint.hp = 500000000;
        pl.newpet1.nPoint.mp = 500000000;
        pl.newpet1.nPoint.dameg = 1;
        pl.newpet1.nPoint.defg = 1;
        pl.newpet1.nPoint.critg = 1;
        pl.newpet1.nPoint.stamina = 1;
        pl.newpet1.nPoint.setBasePoint();
        pl.newpet1.nPoint.setFullHpMp();
    }

    // --------------------------------------------------------------------------
}
