package host.plas.progression.config.bits;

import gg.drak.thebase.utils.MathUtils;
import host.plas.progression.Progression;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import org.bukkit.OfflinePlayer;

public class StarLeveling {
    /**
     * Gets the maximum experience needed for a player to level up.
     * @return The maximum experience needed.
     */
    public static double getMaxExperienceForLevelUp() {
        return Progression.getMainConfig().getMaxExpNeeded();
    }

    /**
     * Gets the leveling formula.
     * @return The leveling formula.
     */
    public static String getLevelingFormula() {
        return Progression.getMainConfig().getStarFormula();
    }

    /**
     * Gets the leveling formula with placeholders replaced for the given player.
     * @param player The player.
     * @return The replaced leveling formula.
     */
    public static String getReplacedLevelingFormula(OfflinePlayer player) {
        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        data.waitUntilFullyLoaded();

        return getReplacedLevelingFormula(data);
    }

    /**
     * Gets the leveling formula with placeholders replaced for the given player.
     * @param data The player data.
     * @return The replaced leveling formula.
     */
    public static String getReplacedLevelingFormula(PlayerData data) {
        String formula = getLevelingFormula();
        formula = formula.replace("%stars_current%", String.valueOf(data.getStars()));
        formula = formula.replace("%stars_previous%", String.valueOf(data.getStars() - 1));
        formula = formula.replace("%experience_current%", String.valueOf(getCurrentLevelExperience(data)));

        return formula;
    }

    /**
     * Calculates the leveling formula for the given player.
     * @param player The player.
     * @return The calculated amount of experience needed to level up.
     */
    public static double getCalculatedLevelingFormula(OfflinePlayer player) {
        String replacedFormula = getReplacedLevelingFormula(player);
        return MathUtils.eval(replacedFormula);
    }

    /**
     * Calculates the leveling formula for the given player.
     * @param data The player data.
     * @return The calculated amount of experience needed to level up.
     */
    public static double getCalculatedLevelingFormula(PlayerData data) {
        String replacedFormula = getReplacedLevelingFormula(data);
        return MathUtils.eval(replacedFormula);
    }

    /**
     * Gets the current experience that a player has.
     * @param data The player data.
     * @return The current experience.
     */
    public static double getCurrentLevelExperience(PlayerData data) {
        return data.getTotalExperience() - data.getCountedExperience();
    }

    /**
     * Gets the needed experience for the player to level up.
     * @param data The player data.
     * @return The total needed experience.
     */
    public static double getTotalNeededExperience(PlayerData data) {
        return getCalculatedLevelingFormula(data);
    }

    /**
     * Gets the needed experience for the player to level up.
     * @param data The player data.
     * @return The needed experience left to level up.
     */
    public static double getLeftNeededExperience(PlayerData data) {
        double totalNeeded = getTotalNeededExperience(data);
        double currentExp = getCurrentLevelExperience(data);
        return totalNeeded - currentExp;
    }

    /**
     * Gets the current level progress as a decimal between 0 and 1.
     * @param data The player data.
     * @return The current level progress as a decimal between 0 and 1.
     */
    public static double getCurrentLevelProgress(PlayerData data) {
        double totalNeeded = getTotalNeededExperience(data);
        double currentExp = getCurrentLevelExperience(data);

//        return currentExp / totalNeeded;
        double raw = currentExp / totalNeeded;
        if (raw < 0d) return 0d;
        if (raw > 1d) return 1d;

        // Truncate to 2 decimal places
        double truncated = Math.round(raw * 100d) / 100d;

        return truncated;
    }

    /**
     * Checks if the player can level up, and levels them up if they can.
     * @param data The player data.
     */
    public static void checkCanLevelUp(PlayerData data) {
        while (getLeftNeededExperience(data) <= 0d) {
            onLevelUp(data);
        }
    }

    /**
     * Levels up the player.
     * @param data The player data.
     */
    public static void onLevelUp(PlayerData data) {
        onLevelUp(data, getTotalNeededExperience(data));
    }

    /**
     * Levels up the player.
     * @param data The player data.
     * @param neededExperience The needed experience for the level up.
     */
    public static void onLevelUp(PlayerData data, double neededExperience) {
        data.addCountedExperience(neededExperience);
        data.incrementStars();
    }
}
