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
        getStarFormula();
        getMaxExpNeeded();
    }

    public String getStarFormula() {
        reloadResource();

        return getOrSetDefault("star-leveling.xp.formula", "(%stars_current% * 250) + 500");
    }

    public double getMaxExpNeeded() {
        reloadResource();

        return getOrSetDefault("star-leveling.xp.max-needed", 5000.0);
    }
}
