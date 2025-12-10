package host.plas.progression.placeholders;

import host.plas.bou.compat.papi.expansion.BetterExpansion;
import host.plas.bou.compat.papi.expansion.PlaceholderContext;
import host.plas.progression.Progression;
import host.plas.progression.config.bits.StarLeveling;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class ProgressionPAPI extends BetterExpansion {
    public ProgressionPAPI() {
        super(
                Progression.getInstance(),
                () -> "progression",
                () -> Progression.getInstance().getDescription().getAuthors().get(0),
                () -> Progression.getInstance().getDescription().getVersion()
        );
    }

    @Override
    public @Nullable String replace(PlaceholderContext ctx) {
        OfflinePlayer player = ctx.getPlayer();

        PlayerData playerData = PlayerManager.getOrGetPlayer(player.getUniqueId().toString()).orElse(null);
        if (playerData == null) return "null";

        String args = ctx.getRawParams();

        String lower = args.toLowerCase();

        switch (lower) {
            case "first_join_timestamp":
                return String.valueOf(playerData.getFirstJoinTimestamp());
            case "last_join_timestamp":
                return String.valueOf(playerData.getLastJoinTimestamp());
            case "stars_count":
                return String.valueOf(playerData.getStars());
            case "stars_previous":
                return String.valueOf(Math.max(0, playerData.getStars() - 1));
            case "stars_formula":
                return StarLeveling.getLevelingFormula();
            case "stars_max_required":
                return String.valueOf(StarLeveling.getMaxExperienceForLevelUp());
            case "exp_total":
                return String.valueOf(playerData.getTotalExperience());
            case "exp_till_next":
                return String.valueOf(StarLeveling.getLeftNeededExperience(playerData));
            case "exp_current":
                return String.valueOf(StarLeveling.getCurrentLevelExperience(playerData));
            case "exp_progress_current":
                return String.valueOf(StarLeveling.getCurrentLevelProgress(playerData));
        }

        return null;
    }
}
