package com.marbledhubb.mhs_discord_integration.configuration;

public enum MessageMode {

    NONE,
    SIMPLE,
    STYLED;

    public boolean isDisabled() {
        return this == NONE;
    }

    public boolean isSimple() {
        return this == SIMPLE;
    }

    public boolean isStyled() {
        return this == STYLED;
    }

    public static MessageMode fromString(String value) {
        for (MessageMode mode : values()) {
            if (mode.toString().equals(value))
                return mode;
        }
        return NONE;
    }

}
