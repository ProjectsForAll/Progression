package host.plas.progression.config;

import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;
import host.plas.progression.Progression;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", Progression.getInstance(), false);
    }

    @Override
    public void init() {

    }
}
