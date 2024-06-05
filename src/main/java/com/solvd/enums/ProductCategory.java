package com.solvd.enums;

public enum ProductCategory {
    // TODO: consider moving urls to configuration
    WOMEN_TOPS("women/tops-women.html"),
    WOMEN_BOTTOMS("women/bottoms-women.html"),
    MEN_TOPS("men/tops-men.html"),
    MEN_BOTTOMS("men/bottoms-men.html"),
    GEAR_BAGS("gear/bags.html"),
    GEAR_FITNESS_EQUIPMENT("gear/fitness-equipment.html");

    private final String relativeUrl;

    private ProductCategory(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    /**
     * Return url relative to page root url
     * (ie for foo.com/bar.html will return bar.html)
     */
    public String getRelativeUrl() {
        return this.relativeUrl;
    }
}
