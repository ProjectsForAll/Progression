package host.plas.progression.runnables;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import org.bukkit.Bukkit;

public class PlayerRunner extends BaseRunnable {
    public PlayerRunner() {
        super(20);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(PlayerManager::getOrCreatePlayer);

        PlayerManager.getLoadedPlayers().forEach(PlayerData::tick);
    }
}
