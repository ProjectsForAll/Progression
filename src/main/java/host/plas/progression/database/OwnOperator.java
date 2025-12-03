package host.plas.progression.database;

import gg.drak.thebase.async.AsyncUtils;
import host.plas.bou.sql.DBOperator;
import host.plas.bou.sql.DatabaseType;
import host.plas.progression.Progression;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.stats.StatType;
import host.plas.progression.data.stats.StatsInstance;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class OwnOperator extends DBOperator {
    public OwnOperator() {
        super(Progression.getDatabaseConfig().getConnectorSet(), Progression.getInstance());
    }

    @Override
    public void ensureTables() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_TABLES, getConnectorSet());

        execute(s1, stmt -> {});
    }

    @Override
    public void ensureDatabase() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_DATABASE, getConnectorSet());

        execute(s1, stmt -> {});
    }

    public void putPlayer(PlayerData playerData) {
        putPlayer(playerData, true);
    }

    public void putPlayer(PlayerData playerData, boolean async) {
        if (async) {
            putPlayerThreaded(playerData);
        } else {
            putPlayerThreaded(playerData).join();
        }
    }

    public CompletableFuture<Void> putPlayerThreaded(PlayerData playerData) {
        return AsyncUtils.executeAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PUSH_PLAYER_MAIN, getConnectorSet());

            execute(s1, stmt -> {
                try {
                    AtomicInteger i = new AtomicInteger(1);

                    stmt.setString(i.getAndIncrement(), playerData.getIdentifier());
                    stmt.setString(i.getAndIncrement(), playerData.getName());
                    stmt.setLong(i.getAndIncrement(), playerData.getFirstJoinTimestamp());
                    stmt.setLong(i.getAndIncrement(), playerData.getLastJoinTimestamp());
                    stmt.setLong(i.getAndIncrement(), playerData.getStars());

                    if (getType() == DatabaseType.MYSQL) {
                        stmt.setString(i.getAndIncrement(), playerData.getName());
                        stmt.setLong(i.getAndIncrement(), playerData.getFirstJoinTimestamp());
                        stmt.setLong(i.getAndIncrement(), playerData.getLastJoinTimestamp());
                        stmt.setLong(i.getAndIncrement(), playerData.getStars());
                    }
                } catch (Throwable e) {
                    Progression.getInstance().logWarning("Failed to set values for statement: " + s1, e);
                }
            });

            String s2 = Statements.getStatement(Statements.StatementType.PUSH_STAT_MAIN, getConnectorSet());

            playerData.getStats().forEach(stat -> {
                execute(s2, stmt -> {
                    try {
                        AtomicInteger i = new AtomicInteger(1);

                        stmt.setString(i.getAndIncrement(), playerData.getIdentifier());
                        stmt.setString(i.getAndIncrement(), stat.getType().name());
                        stmt.setDouble(i.getAndIncrement(), stat.getValue());

                        if (getType() == DatabaseType.MYSQL) {
                            stmt.setDouble(i.getAndIncrement(), stat.getValue());
                        }
                    } catch (Throwable e) {
                        Progression.getInstance().logWarning("Failed to set values for statement: " + s2, e);
                    }
                });
            });
        });
    }

    public CompletableFuture<Optional<PlayerData>> pullPlayerThreaded(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PULL_PLAYER_MAIN, getConnectorSet());

            AtomicReference<Optional<PlayerData>> ref = new AtomicReference<>(Optional.empty());

            executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (Throwable e) {
                    Progression.getInstance().logWarning("Failed to set values for statement: " + s1, e);
                }
            }, rs -> {
                try {
                    if (rs.next()) {
                        String name = rs.getString("Name");
                        long firstJoin = rs.getLong("FirstJoinTimestamp");
                        long lastJoin = rs.getLong("LastJoinTimestamp");
                        long stars = rs.getLong("Stars");

                        PlayerData playerData = new PlayerData(uuid, name);

                        playerData.setFirstJoinTimestamp(firstJoin);
                        playerData.setLastJoinTimestamp(lastJoin);
                        playerData.setStars(stars);

                        ref.set(Optional.of(playerData));
                    }
                } catch (Throwable e) {
                    Progression.getInstance().logWarning("Failed to get values from result set for statement: " + s1, e);
                }
            });

            Optional<PlayerData> optional = ref.get();

            if (optional.isEmpty()) {
                return Optional.empty();
            }

            PlayerData data = optional.get();
            if (data.getStats().isEmpty()) {
                data.getStats().fromBlank(); // Load default stats.
            }

            String s2 = Statements.getStatement(Statements.StatementType.PULL_STAT_MAIN, getConnectorSet());

            data.getStats().forEach(stat -> {
                executeQuery(s2, stmt -> {
                    try {
                        stmt.setString(1, uuid);
                    } catch (Throwable e) {
                        Progression.getInstance().logWarning("Failed to set values for statement: " + s1, e);
                    }
                }, rs -> {
                    try {
                        if (rs.next()) {
                            String typeStr = rs.getString("Type");
                            double value = rs.getDouble("Value");

                            StatType type = null;
                            try {
                                type = StatType.valueOf(typeStr);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                return;
                            }

                            data.getStats().set(type, value);
                        }
                    } catch (Throwable e) {
                        Progression.getInstance().logWarning("Failed to get values from result set for statement: " + s1, e);
                    }
                });
            });

            return Optional.of(data);
        });
    }
}
