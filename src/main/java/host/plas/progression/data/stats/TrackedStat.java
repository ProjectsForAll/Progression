package host.plas.progression.data.stats;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class TrackedStat implements Comparable<TrackedStat> {
    private StatType type;
    private double weight;

    public TrackedStat(StatType type, double weight) {
        this.type = type;
        this.weight = weight;
    }

    public void load() {
        StatsManager.load(this);
    }

    public void unload() {
        StatsManager.unload(this);
    }

    @Override
    public int compareTo(@NotNull TrackedStat o) {
        return this.getType().compareTo(o.getType());
    }

    public double withWeight(double value) {
        return value * weight;
    }
}
