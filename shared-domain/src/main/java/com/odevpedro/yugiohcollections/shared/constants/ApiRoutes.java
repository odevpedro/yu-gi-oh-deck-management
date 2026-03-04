package com.odevpedro.yugiohcollections.shared.constants;

public final class ApiRoutes {

    private ApiRoutes() {}

    public static final String ID = "/{id}";

    // Auth
    public static final String AUTH_BASE = "/auth";
    public static final String AUTH_REGISTER = AUTH_BASE + "/register";
    public static final String AUTH_LOGIN = AUTH_BASE + "/login";
    public static final String AUTH_ME = AUTH_BASE + "/me";

    // Decks
    public static final String DECKS_BASE = "/decks";
    public static final String DECKS_BY_ID = DECKS_BASE + ID;

    // Custom Cards
    public static final String CUSTOM_CARDS_BASE = "/custom-cards";
    public static final String CUSTOM_CARDS_BY_ID = CUSTOM_CARDS_BASE + ID;

    // Cards
    public static final String CARDS_BASE = "/cards";
    public static final String CARDS_BY_ID = ID;
    public static final String CARDS_INTERNAL = "/internal";
    public static final String CARDS_INTERNAL_BY_ID = CARDS_INTERNAL + ID;
}