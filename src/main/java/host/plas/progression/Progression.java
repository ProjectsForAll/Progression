package host.plas.progression;

import host.plas.bou.BetterPlugin;
import host.plas.progression.commands.ProgressionCMD;
import host.plas.progression.config.DatabaseConfig;
import host.plas.progression.config.MainConfig;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import host.plas.progression.data.stats.StatsManager;
import host.plas.progression.database.OwnOperator;
import host.plas.progression.events.MainListener;
import host.plas.progression.placeholders.ProgressionPAPI;
import host.plas.progression.runnables.PlayerRunner;
import host.plas.progression.runnables.PlayerSaver;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class Progression extends BetterPlugin {
    @Getter @Setter
    private static Progression instance;
    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static DatabaseConfig databaseConfig;

    @Getter @Setter
    private static OwnOperator database;

    @Getter @Setter
    private static MainListener mainListener;

    @Getter @Setter
    private static ProgressionPAPI papiExpansion;

    @Getter @Setter
    private static PlayerRunner playerRunner;
    @Getter @Setter
    private static PlayerSaver playerSaver;

    @Getter @Setter
    private static ProgressionCMD progressionCMD;

    public Progression() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setMainConfig(new MainConfig());
        setDatabaseConfig(new DatabaseConfig());

        setDatabase(new OwnOperator());

        StatsManager.init();

        setMainListener(new MainListener());

        setPapiExpansion(new ProgressionPAPI());

        setPlayerRunner(new PlayerRunner());
        setPlayerSaver(new PlayerSaver());

        setProgressionCMD(new ProgressionCMD());
    }

    @Override
    public void onBaseDisable() {
        if (getPlayerRunner() != null) {
            getPlayerRunner().cancel();
        }

        // Plugin shutdown logic
        PlayerManager.getLoadedPlayers().forEach(PlayerData::saveAndUnload);
    }
}
