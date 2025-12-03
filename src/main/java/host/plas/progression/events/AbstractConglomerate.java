package host.plas.progression.events;

import gg.drak.thebase.events.BaseEventHandler;
import host.plas.bou.events.ListenerConglomerate;
import host.plas.progression.Progression;
import org.bukkit.Bukkit;

public class AbstractConglomerate implements ListenerConglomerate {
    public AbstractConglomerate() {
        register();
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Progression.getInstance());
        BaseEventHandler.bake(this, Progression.getInstance());
        Progression.getInstance().logInfo("Registered listeners for: &c" + this.getClass().getSimpleName());
    }
}
