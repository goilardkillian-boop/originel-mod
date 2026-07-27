package fr.lycania.originel.skill;

public enum SkillType {
    /** Continuously active while unlocked, no manual trigger. */
    PASSIVE,
    /** Triggered on demand via /originel skill use, subject to a cooldown. */
    ACTIVE
}
