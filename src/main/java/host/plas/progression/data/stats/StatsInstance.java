package host.plas.progression.data.stats;

import com.google.common.util.concurrent.AtomicDouble;
import gg.drak.thebase.async.AsyncUtils;
import gg.drak.thebase.objects.Identified;
import host.plas.progression.config.bits.StarLeveling;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

@Getter @Setter
public class StatsInstance implements Identified {
    private String identifier;

    private ConcurrentSkipListSet<InstancedStat> stats = new ConcurrentSkipListSet<>();

    public StatsInstance(String identifier, ConcurrentSkipListSet<InstancedStat> stats) {
        this.identifier = identifier;
        this.stats = stats;
    }

    public StatsInstance(UUID identifier, ConcurrentSkipListSet<InstancedStat> stats) {
        this(identifier.toString(), stats);
    }

    public StatsInstance(String identifier) {
        this(identifier, new ConcurrentSkipListSet<>());
    }

    public StatsInstance(String identifier, boolean fromBlank) {
        this(identifier, new ConcurrentSkipListSet<>());

        if (fromBlank) {
            fromBlank();
        }
    }

    public void fromInstance(StatsInstance instance) {
        this.stats = instance.getStats();
    }

    public void fromBlank() {
        fromInstance(StatsManager.makeBlank());
    }

    public StatsInstance copy() {
        return new StatsInstance(this.identifier, this.stats);
    }

    public StatsInstance put(InstancedStat stat) {
        this.stats.add(stat);
        return this;
    }

    public StatsInstance remove(InstancedStat stat) {
        return remove(stat.getType());
    }

    public StatsInstance remove(StatType type) {
        this.stats.removeIf(s -> s.getType() == type);
        return this;
    }

    public StatsInstance clear() {
        this.stats.clear();
        return this;
    }

    public Optional<InstancedStat> find(StatType type) {
        return this.stats.stream().filter(s -> s.getStat().getType() == type).findFirst();
    }

    public StatsInstance set(InstancedStat stat) {
        return set(stat.getType(), stat.getValue());
    }

    public StatsInstance set(StatType type, double value) {
        find(type).ifPresent(s -> s.setValue(value));
        return this;
    }

    public StatsInstance add(InstancedStat stat) {
        return add(stat.getType(), stat.getValue());
    }

    public StatsInstance add(StatType type, double value) {
        find(type).ifPresent(s -> s.add(value));
        return this;
    }

    public StatsInstance subtract(InstancedStat stat) {
        return subtract(stat.getType(), stat.getValue());
    }

    public StatsInstance subtract(StatType type, double value) {
        find(type).ifPresent(s -> s.subtract(value));
        return this;
    }

    public StatsInstance increment(InstancedStat stat) {
        return increment(stat.getType());
    }

    public StatsInstance increment(StatType type) {
        find(type).ifPresent(InstancedStat::increment);
        return this;
    }

    public StatsInstance decrement(InstancedStat stat) {
        return decrement(stat.getType());
    }

    public StatsInstance decrement(StatType type) {
        find(type).ifPresent(InstancedStat::decrement);
        return this;
    }

    public StatsInstance multiply(InstancedStat stat) {
        return multiply(stat.getType(), stat.getValue());
    }

    public StatsInstance multiply(StatType type, double value) {
        find(type).ifPresent(s -> s.multiply(value));
        return this;
    }

    public StatsInstance divide(InstancedStat stat) {
        return divide(stat.getType(), stat.getValue());
    }

    public StatsInstance divide(StatType type, double value) {
        find(type).ifPresent(s -> s.divide(value));
        return this;
    }

    public StatsInstance modulo(InstancedStat stat) {
        return modulo(stat.getType(), stat.getValue());
    }

    public StatsInstance modulo(StatType type, double value) {
        find(type).ifPresent(s -> s.modulo(value));
        return this;
    }

    public double calculateTotal() {
        AtomicDouble total = new AtomicDouble(0);

        this.stats.forEach(s -> total.addAndGet(s.getWeightedValue()));

        return total.get();
    }

    public void forEach(Consumer<InstancedStat> consumer) {
        getStats().forEach(consumer);
    }

    public boolean isEmpty() {
        return getStats().isEmpty();
    }

    public boolean hasOne() {
        return ! isEmpty();
    }
}
