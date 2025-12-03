package host.plas.progression.data.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class StatsManager {
    @Getter @Setter
    private static ConcurrentSkipListSet<TrackedStat> stats = new ConcurrentSkipListSet<>();

    public static void load(TrackedStat stat) {
        unload(stat);

        getStats().add(stat);
    }

    public static boolean unload(TrackedStat stat) {
        return getStats().removeIf(s -> s.getType() == stat.getType());
    }

    public static Optional<TrackedStat> get(StatType type) {
        return getStats().stream().filter(s -> s.getType() == type).findFirst();
    }

    public static boolean has(StatType type) {
        return getStats().stream().anyMatch(s -> s.getType() == type);
    }

    public static StatsInstance makeBlank() {
        return makeBlank("blank");
    }

    public static StatsInstance makeBlank(String identifier) {
        StatsInstance instance = new StatsInstance(identifier);

        getStats().forEach(ts -> {
            instance.put(new InstancedStat(ts, 0d));
        });

        return instance;
    }

    public static void init() {
        Arrays.stream(StatType.values()).forEach(statType -> {
            new TrackedStat(statType, statType.getDefaultWeight()).load();
        });
    }
}
