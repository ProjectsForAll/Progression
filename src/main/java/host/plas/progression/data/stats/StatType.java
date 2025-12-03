package host.plas.progression.data.stats;

import lombok.Getter;

@Getter
public enum StatType {
    BROKEN_BLOCKS(1),
    PLACED_BLOCKS(0.5),
    KILLS(5),
//    DEATHS,
    MOB_KILLS(2),
    FISH_CAUGHT(10),
    ITEMS_ENCHANTED(1.5),
    CHAT_MESSAGES_SENT(0.5),
    ;

    private final double defaultWeight;

    StatType(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    StatType() {
        this(1.0d);
    }
}
