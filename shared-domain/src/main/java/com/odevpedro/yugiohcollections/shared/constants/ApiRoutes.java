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
    public static final String DECKS_FULL = "/decks/{deckId}/full";
    public static final String DECKS_CARDS = "/decks/{deckId}/cards";
    public static final String DECKS_EXPORT = "/decks/{deckId}/export";

    // Custom Cards
    public static final String CUSTOM_CARDS_BASE = "/custom-cards";
    public static final String CUSTOM_CARDS_BY_ID = CUSTOM_CARDS_BASE + ID;

    // Cards
    public static final String CARDS_BASE = "/cards";
    public static final String CARDS_INTERNAL = "/internal";
    public static final String CARDS_INTERNAL_BY_ID = CARDS_INTERNAL + ID;

    // Players
    public static final String PLAYERS_BASE = "/players";
    public static final String PLAYERS_ME_STATUS = PLAYERS_BASE + "/me/status";
    public static final String PLAYERS_NEARBY = PLAYERS_BASE + "/nearby";

    // Challenges
    public static final String CHALLENGES_BASE = "/challenges";
    public static final String CHALLENGES_BY_ID = CHALLENGES_BASE + ID;
    public static final String CHALLENGES_PENDING = CHALLENGES_BASE + "/pending";

    // Proxy
    public static final String PROXY_BASE = "/proxy";
    public static final String PROXY_BY_ID = PROXY_BASE + ID;
}