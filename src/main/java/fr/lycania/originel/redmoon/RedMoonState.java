package fr.lycania.originel.redmoon;

/**
 * Whether the Lune Rouge event is currently active. A minimal flag for now;
 * fleshed out into the full celestial event in a later step.
 */
public final class RedMoonState {

    private static volatile boolean active = false;

    private RedMoonState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean value) {
        active = value;
    }
}
