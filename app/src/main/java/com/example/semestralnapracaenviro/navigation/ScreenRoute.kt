package com.example.semestralnapracaenviro.navigation

/**
 * Enum trieda reprezentujúca jednotlivé cesty (routes) v navigácii aplikácie.
 *
 * Každá hodnota enumu obsahuje reťazec, ktorý slúži ako identifikátor pre navigačnú cestu.
 */
enum class ScreenRoute(val route: String) {
    /** Cesta na registračnú obrazovku */
    REGISTRATION("registration"),

    /** Cesta na prihlasovaciu obrazovku */
    LOGIN("login"),

    /** Cesta na úvodnú obrazovku (welcome screen) */
    WELCOME("welcome"),

    /** Cesta na hlavnú obrazovku aplikácie */
    MAINSCREEN("mainscreen"),

    /** Cesta na obrazovku nahlásenia skládky */
    REPORT("reportDump"),

    /** Cesta na obrazovku mapy */
    MAP("map"),

    /** Cesta na obrazovku profilu používateľa */
    PROFILE("profile")
}