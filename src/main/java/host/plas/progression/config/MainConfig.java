package host.plas.progression.config;

import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;
import host.plas.progression.Progression;
import host.plas.progression.config.bits.StarLeveling;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", Progression.getInstance(), true);
    }

    @Override
    public void init() {
        getStarLeveling();
    }

    public String getStarFormula() {
        reloadResource();

        return getOrSetDefault("star-leveling.xp.formula", "5 * (%stars_current% ^ 2) + 50 * %stars_current% - (5 * (%stars_previous% ^ 2) + 50 * %stars_previous%) + 100");
    }

    public double getMaxExpNeeded() {
        reloadResource();

        return getOrSetDefault("star-leveling.xp.max-needed", 1000000.0);
    }

    public StarLeveling getStarLeveling() {
        return new StarLeveling(getStarFormula(), getMaxExpNeeded());
    }
}
