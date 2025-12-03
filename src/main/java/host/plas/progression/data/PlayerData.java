package host.plas.progression.data;

import gg.drak.thebase.objects.Identifiable;
import host.plas.bou.utils.SenderUtils;
import host.plas.progression.Progression;
import host.plas.progression.data.stats.StatsInstance;
import host.plas.progression.events.own.PlayerCreationEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter @Setter
public class PlayerData implements Identifiable {
    private String identifier;
    private AtomicBoolean fullyLoaded;

    private String name;

    private long firstJoinTimestamp;
    private long lastJoinTimestamp;

    private long stars;

    private StatsInstance stats;

    public PlayerData(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;

        loadDefaults();

        this.fullyLoaded = new AtomicBoolean(false);
    }

    public PlayerData(OfflinePlayer player) {
        this(player.getUniqueId().toString(), player.getName());
    }

    public PlayerData(String uuid) {
        this(uuid, "");
    }

    public void loadDefaults() {
        this.firstJoinTimestamp = System.currentTimeMillis();
        this.lastJoinTimestamp = System.currentTimeMillis();

        this.stars = 0;

        this.stats = new StatsInstance(this.identifier, true);
    }

    public Optional<Player> asPlayer() {
        try {
            return Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(identifier)));
        } catch (Throwable e) {
            Progression.getInstance().logWarning("Failed to get player from identifier: " + identifier, e);

            return Optional.empty();
        }
    }

    public Optional<OfflinePlayer> asOfflinePlayer() {
        try {
            return Optional.of(Bukkit.getOfflinePlayer(UUID.fromString(identifier)));
        } catch (Throwable e) {
            Progression.getInstance().logWarning("Failed to get offline player from identifier: " + identifier, e);

            return Optional.empty();
        }
    }

    public boolean isOnline() {
        return asPlayer().isPresent();
    }

    public void load() {
        PlayerManager.loadPlayer(this);
    }

    public void unload() {
        PlayerManager.unloadPlayer(this);
    }

    public void save() {
        PlayerManager.savePlayer(this);
    }

    public void save(boolean async) {
        PlayerManager.savePlayer(this, async);
    }

    public void augment(CompletableFuture<Optional<PlayerData>> future, boolean isGet) {
        fullyLoaded.set(false);

        future.whenComplete((data, error) -> {
            if (error != null) {
                Progression.getInstance().logWarning("Failed to augment player data", error);

                this.fullyLoaded.set(true);
                return;
            }

            if (data.isPresent()) {
                PlayerData newData = data.get();

                this.name = newData.getName();
                this.firstJoinTimestamp = newData.getFirstJoinTimestamp();
                this.lastJoinTimestamp = newData.getLastJoinTimestamp();

                this.stars = newData.getStars();

                this.stats = newData.getStats();
            } else {
                if (! isGet) {
                    new PlayerCreationEvent(this).fire();
                    this.save();
                }
            }

            this.fullyLoaded.set(true);
        });
    }

    public boolean isFullyLoaded() {
        return fullyLoaded.get();
    }

    public void saveAndUnload(boolean async) {
        save(async);
        unload();
    }

    public void saveAndUnload() {
        saveAndUnload(true);
    }

    public PlayerData waitUntilFullyLoaded() {
        while (! isFullyLoaded()) {
            Thread.onSpinWait();
        }
        return this;
    }

    public AtomicBoolean isTicking = new AtomicBoolean(false);

    public void tick() {
        if (isTicking.get()) return;
        isTicking.set(true);

        waitUntilFullyLoaded();

        if (getStats().canLevelUp(getStars())) {
            levelUp();
        }

        isTicking.set(false);
    }

    public void incrementStars() {
        this.stars += 1;
    }

    public void levelUp() {
        incrementStars();

        playLevelUpEffects();
    }

    public void playLevelUpEffects() {
        playLevelUpMessage();
        playLevelUpSound();
    }

    public void playLevelUpMessage() {
        asPlayer().ifPresent(player -> SenderUtils.getSender(player).sendMessage("&f&lLEVEL UP&7! &7&oYou are now &e⭐ &a" + getStars() + " &7&o!"));
    }

    public void playLevelUpSound() {
        asPlayer().ifPresent(player -> player.playSound(player.getLocation(), "entity.player.levelup", 1.0f, 1.0f));
    }
}
