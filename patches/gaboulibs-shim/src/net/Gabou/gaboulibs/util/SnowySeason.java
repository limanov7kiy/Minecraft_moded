package net.Gabou.gaboulibs.util;

import sereneseasons.api.season.Season;

public final class SnowySeason {
    private SnowySeason() {
    }

    public static boolean isSnowySeason(Season.SubSeason season) {
        return season == Season.SubSeason.EARLY_WINTER
            || season == Season.SubSeason.MID_WINTER
            || season == Season.SubSeason.LATE_WINTER;
    }
}
