package host.plas.progression.events.own;

import gg.drak.thebase.events.components.BaseEvent;
import host.plas.bou.BukkitOfUtils;
import host.plas.progression.Progression;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OwnEvent extends BaseEvent {
    public OwnEvent() {
        super();
    }

    public Progression getPlugin() {
        return Progression.getInstance();
    }

    public BukkitOfUtils getBou() {
        return BukkitOfUtils.getInstance();
    }
}
