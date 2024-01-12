package com.jinhuynh.consts;

import com.jinhuynh.utils.Util;

import java.util.Arrays;
import java.util.List;

// su kien 1/6
public class ConstDataEvent {

    public static final int idVoOc = 695;
    public static final int idSaoBien = 698;
    public static final int idConCua = 697;

    public static final int idVoOc_SK = 2135;
    public static final int idSaoBien_SK = 2136;
    public static final int idConCua_Sk = 2134;

    public static final int mayDoSuKien = 2137;
    public static final int ruong1 = 2138;

    public static final int ruong2 = 2139;

    public static final int ruongvip1 = 2143;
    public static final int ruongvip2 = 2144;
    public static final int ruongvip3 = 2145;
    public static final int trumcuoi = 2155;

    public static final int top1sm = 2149;
    public static final int top2sm = 2150;
    public static final int top3sm = 2151;
    public static final int top4sm = 2152;

    public static final int slVoOcSK = 99;

    public static final int slSaoBienSK = 99;

    public static final int slConCuaSK = 99;

    public static List<Integer> listVPSK = Arrays.asList(idConCua, idVoOc, idSaoBien);

    public static int getRandomFromList() {
        int tile_saoBien = 30;
        int tile_ConCua = 50;
        int tile_VoOc = 40;
        int tile = Util.nextInt(0, 100);
        if (tile < tile_ConCua) {
            return listVPSK.get(0);
        } else if (tile < tile_VoOc) {
            return listVPSK.get(1);
        } else if (tile < tile_saoBien) {
            return listVPSK.get(2);
        } else {
            return listVPSK.get(Util.nextInt(listVPSK.size()));
        }
    }

    public static ConstDataEvent gI;

    public static ConstDataEvent gI() {
        if (gI == null) {
            gI = new ConstDataEvent();
        }
        return gI;
    }

    public static final boolean isRunningSK16 = true;
}
