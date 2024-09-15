package com.example.myapplication.image;

import com.example.myapplication.R;

public enum ImageEnum {
    APFEL("apfel", R.drawable.apfel),
    BAHN("bahn", R.drawable.bahn),
    BITTE("bitte", R.drawable.bitte),
    BROT("brot", R.drawable.brot),
    BRUDER("bruder", R.drawable.bruder),
    COUSIN("cousin", R.drawable.cousin),
    DANKE("danke", R.drawable.danke),
    EHEFRAU("ehefrau", R.drawable.ehefrau),
    EHEMANN("ehemann", R.drawable.ehemann),
    ENTSCHULDIGUNG("entschuldigung", R.drawable.entschuldigung),
    FLEISCH("fleisch", R.drawable.fleisch),
    FLUGHAFEN("flughafen", R.drawable.flughafen),
    FLUGZEUG("flugzeug", R.drawable.flugzeug),
    FRUCHT("frucht", R.drawable.frucht),
    GEMUSE("gemuse", R.drawable.gemuse),
    GEPACK("gepack", R.drawable.gepack),
    GROBELTERN("grobeltern", R.drawable.grobeltern),
    GUTE_NACHT("gute_nacht", R.drawable.gute_nacht),
    GUTEN_ABEND("guten_abend", R.drawable.guten_abend),
    GUTEN_MORGEN("guten_morgen", R.drawable.guten_morgen),
    HALLO("hallo", R.drawable.hallo),
    HOTEL("hotel", R.drawable.hotel),
    JA("ja", R.drawable.ja),
    KAFFEE("kaffee", R.drawable.kaffee),
    KOFFER("koffer", R.drawable.koffer),
    KUCHEN("kuchen", R.drawable.kuchen),
    MILCH("milch", R.drawable.milch),
    MUTTER("mutter", R.drawable.mutter),
    NEFFE("neffe", R.drawable.neffe),
    NEIN("nein", R.drawable.nein),
    NICHTE("nichte", R.drawable.nichte),
    NUDELN("nudeln", R.drawable.nudeln),
    ONKEL("onkel", R.drawable.onkel),
    PASS("pass", R.drawable.pass),
    REIS("reis", R.drawable.reis),
    REISE("reise", R.drawable.reise),
    REISEPASS("reisepass", R.drawable.reisepass),
    SCHWESTER("schwester", R.drawable.schwester),
    TANTE("tante", R.drawable.tante),
    TSCHUSS("tschuss", R.drawable.tschuss),
    VATER("vater", R.drawable.vater),
    WASSER("wasser", R.drawable.wasser),
    ZUG("zug", R.drawable.zug);

    private final String vocabulary;
    private final int imageResId;

    ImageEnum(String vocabulary, int imageResId) {
        this.vocabulary = vocabulary;
        this.imageResId = imageResId;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public int getImageResId() {
        return imageResId;
    }

    public static int getImageResourceId(String vocabulary) {
        for (ImageEnum item : values()) {
            if (item.getVocabulary().equalsIgnoreCase(vocabulary)) {
                return item.getImageResId();
            }
        }
        return -1; // Không tìm thấy
    }
}
