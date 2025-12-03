package host.plas.progression.runnables;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;

public class PlayerSaver extends BaseRunnable {
    public PlayerSaver() {
        super(20 * 60 * 5); // Every 5 minutes
    }

    @Override
    public void run() {
        PlayerManager.getLoadedPlayers().forEach(PlayerData::save);
    }
}
