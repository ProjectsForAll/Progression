package host.plas.progression.data.stats;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Getter
public class InstancedStat implements Comparable<InstancedStat> {
    @Setter
    private TrackedStat stat;
    private double value;
    @Setter
    private Date lastUpdated;

    public InstancedStat(TrackedStat stat, double value) {
        this.stat = stat;
        this.value = value;
    }

    @Override
    public int compareTo(@NotNull InstancedStat o) {
        return this.getStat().compareTo(o.getStat());
    }

    public double getWeightedValue() {
        return this.getStat().withWeight(value);
    }

    public void setValue(double value) {
//        if (! checkCanBeUpdated()) return;

        lastUpdated = new Date();
        this.value = value;
    }

    public long getCooldownMillis() {
        return 500L; // 500 milliseconds
    }

    public boolean checkCanBeUpdated() {
        if (lastUpdated == null) return true;

        long now = new Date().getTime();
        long last = lastUpdated.getTime();
        return now - last >= getCooldownMillis();
    }

    public InstancedStat add(double value) {
        this.setValue(this.getValue() + value);
        return this;
    }

    public InstancedStat subtract(double value) {
        this.setValue(this.getValue() - value);
        return this;
    }

    public InstancedStat increment() {
        return add(1);
    }

    public InstancedStat decrement() {
        return subtract(1);
    }

    public InstancedStat set(double value) {
        this.setValue(value);
        return this;
    }

    public InstancedStat multiply(double value) {
        this.setValue(this.getValue() * value);
        return this;
    }

    public InstancedStat divide(double value) {
        this.setValue(this.getValue() / value);
        return this;
    }

    public InstancedStat modulo(double value) {
        this.setValue(this.getValue() % value);
        return this;
    }

    public InstancedStat copy() {
        return new InstancedStat(this.getStat(), this.getValue());
    }

    public StatType getType() {
        return this.getStat().getType();
    }
}
