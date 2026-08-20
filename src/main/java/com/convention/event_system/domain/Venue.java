package com.convention.event_system.domain;

public enum Venue {
    CHAMBER_HALL("4F 체임버홀"),
    GALLERY_HALL("3F 갤러리홀"),
    MAJESTIC_BALLROOM("2F 마제스틱볼룸"),
    LEBEN_HALL("3F 레벤홀"),
    CAFE_TERRACE("3F 카페테라스"),
    BRISE_HALL("2F 브리제홀");

    private final String displayName;

    Venue(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
