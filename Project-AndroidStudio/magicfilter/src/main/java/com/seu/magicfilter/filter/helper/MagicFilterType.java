package com.seu.magicfilter.filter.helper;

public enum MagicFilterType {
    NONE(0),
    FAIRYTALE(1),
    SUNRISE(2),
    SUNSET(3),
    WHITECAT(4),
    BLACKCAT(5),
    SKINWHITEN(6),
    HEALTHY(7),
    SWEETS(8),
    ROMANCE(9),
    SAKURA(10),
    WARM(11),
    ANTIQUE(12),
    NOSTALGIA(13),
    CALM(14),
    LATTE(15),
    TENDER(16),
    COOL(17),
    EMERALD(18),
    EVERGREEN(19),
    CRAYON(20),
    SKETCH(21),
    AMARO(22),
    BRANNAN(23),
    BROOKLYN(24),
    EARLYBIRD(25),
    FREUD(26),
    HEFE(27),
    HUDSON(28),
    INKWELL(29),
    KEVIN(30),
    LOMO(31),
    N1977(32),
    NASHVILLE(33),
    PIXAR(34),
    RISE(35),
    SIERRA(36),
    SUTRO(37),
    TOASTER2(38),
    VALENCIA(39),
    WALDEN(40),
    XPROII(41),
    // adjust
    CONTRAST(42),
    BRIGHTNESS(43),
    EXPOSURE(44),
    HUE(45),
    SATURATION(46),
    SHARPEN(47),
    IMAGE_ADJUST(48);

    //


    MagicFilterType (int action)
    {
        this.action = action;
    }

    private final int action;

    public int getAction() {
        return action;
    }
}
