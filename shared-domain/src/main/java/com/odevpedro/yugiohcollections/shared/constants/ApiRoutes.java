package com.odevpedro.yugiohcollections.shared.constants;

public final class ApiRoutes {

    private ApiRoutes() {}

    public static final String ID = "/{id}";

    // Auth
    public static final String AUTH_BASE = "/auth";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_ME = "/me";

    // Decks
    public static final String DECKS_BASE = "/decks";
    public static final String DECKS_BY_ID = "/{deckId}";
    public static final String DECKS_FULL = "/{deckId}/full";
    public static final String DECKS_CARDS = "/{deckId}/cards";
    public static final String DECKS_IMPORT = "/import";
    public static final String DECKS_EXPORT = "/{deckId}/export";

    // Custom Cards
    public static final String CUSTOM_CARDS_BASE = "/custom-cards";
    public static final String CUSTOM_CARDS_BY_ID = ID;

    // Cards
    public static final String CARDS_BASE = "/cards";
    public static final String CARDS_INTERNAL = "/internal";
    public static final String CARDS_INTERNAL_BY_ID = CARDS_INTERNAL + ID;

    // Players
    public static final String PLAYERS_BASE = "/players";
    public static final String PLAYERS_ME_STATUS = "/me/status";
    public static final String PLAYERS_NEARBY = "/nearby";

    // Challenges
    public static final String CHALLENGES_BASE = "/challenges";
    public static final String CHALLENGES_BY_ID = ID;
    public static final String CHALLENGES_PENDING = "/pending";

    // Proxy
    public static final String PROXY_BASE = "/proxy";
    public static final String PROXY_BY_ID = ID;
}
