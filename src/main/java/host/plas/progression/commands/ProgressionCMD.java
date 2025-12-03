package host.plas.progression.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.bou.utils.UuidUtils;
import host.plas.progression.Progression;
import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentSkipListSet;

public class ProgressionCMD extends SimplifiedCommand {
    public ProgressionCMD() {
        super("progression", Progression.getInstance());
    }

    public enum Action1 {
        STARS,
        EXP,
        ;
    }

    public enum Action2 {
        GET,
        SET,
        ADD,
        REMOVE,
        ;
    }

    @Override
    public boolean command(CommandContext ctx) {
        if (! ctx.isArgUsable(0)) {
            ctx.sendMessage("&cUsage: /progression <stars|exp> <get|set|add|remove> [amount] -p:[player]");
            return false;
        }

        String action1 = ctx.getStringArg(0);

        Action1 action1Enum;
        switch (action1.toLowerCase()) {
            case  "stars":
                action1Enum = Action1.STARS;
                break;
            case "exp":
                action1Enum = Action1.EXP;
                break;
            default:
                ctx.sendMessage("&cInvalid action: " + action1);
                return false;
        }

        if (! ctx.isArgUsable(1)) {
            ctx.sendMessage("&cUsage: /progression <stars|exp> <get|set|add|remove> [amount] -p:[player]");
            return false;
        }

        String action2 = ctx.getStringArg(1);
        Action2 action2Enum;
        switch (action2.toLowerCase()) {
            case "get":
                action2Enum = Action2.GET;
                break;
            case "set":
                action2Enum = Action2.SET;
                break;
            case "add":
                action2Enum = Action2.ADD;
                break;
            case "remove":
                action2Enum = Action2.REMOVE;
                break;
            default:
                ctx.sendMessage("&cInvalid action: " + action2);
                return false;
        }

        OfflinePlayer target = ctx.getPlayer().orElse(null);

        for (String arg : ctx.getArgsAsStringArray()) {
            if (arg.startsWith("-p:")) {
                String playerName = arg.substring("-p:".length());

                if (! UuidUtils.isValidPlayerName(playerName)) continue;

                OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
                if (! p.hasPlayedBefore()) continue;

                target = p.getPlayer();
            }
        }

        if (target == null) {
            ctx.sendMessage("&cPlayer not found.");
            return false;
        }

        PlayerData data = PlayerManager.getOrCreatePlayer(target);

        switch (action1Enum) {
            case STARS:
                switch (action2Enum) {
                    case GET:
                        long stars = data.getStars();
                        ctx.sendMessage("&a" + target.getName() + " has " + stars + " stars.");
                        return true;
                    case SET:
                    case ADD:
                    case REMOVE:
                        if (! ctx.isArgUsable(2)) {
                            ctx.sendMessage("&cUsage: /progression stars <get|set|add|remove> [amount] -p:[player]");
                            return false;
                        }

                        int amount;
                        try {
                            amount = Integer.parseInt(ctx.getStringArg(2));
                        } catch (NumberFormatException e) {
                            ctx.sendMessage("&cInvalid amount: " + ctx.getStringArg(2));
                            return false;
                        }

                        switch (action2Enum) {
                            case SET:
                                data.setStars(amount);
                                ctx.sendMessage("&aSet " + target.getName() + "'s stars to " + amount + ".");
                                return true;
                            case ADD:
                                long currentStars = data.getStars();
                                data.setStars(currentStars + amount);
                                ctx.sendMessage("&aAdded " + amount + " stars to " + target.getName() + ".");
                                return true;
                            case REMOVE:
                                long current = data.getStars();
                                data.setStars(Math.max(0, current - amount));
                                ctx.sendMessage("&aRemoved " + amount + " stars from " + target.getName() + ".");
                                return true;
                        }
                }
                break;
            case EXP:
                switch (action2Enum) {
                    case GET:
                        double exp = data.getStats().calculateTotal();
                        ctx.sendMessage("&a" + target.getName() + " has " + exp + " exp.");
                        return true;
                    case SET:
                    case ADD:
                    case REMOVE:
                        if (! ctx.isArgUsable(2)) {
                            ctx.sendMessage("&cUsage: /progression exp <get|set|add|remove> [amount] -p:[player]");
                            return false;
                        }

                        int amount;
                        try {
                            amount = Integer.parseInt(ctx.getStringArg(2));
                        } catch (NumberFormatException e) {
                            ctx.sendMessage("&cInvalid amount: " + ctx.getStringArg(2));
                            return false;
                        }

                        switch (action2Enum) {
//                            case SET:
//                                data.setExp(amount);
//                                ctx.sendMessage("&aSet " + target.getName() + "'s exp to " + amount + ".");
//                                return true;
//                            case ADD:
//                                long currentExp = data.getExp();
//                                data.setExp(currentExp + amount);
//                                ctx.sendMessage("&aAdded " + amount + " exp to " + target.getName() + ".");
//                                return true;
//                            case REMOVE:
//                                long current = data.getExp();
//                                data.setExp(Math.max(0, current - amount));
//                                ctx.sendMessage("&aRemoved " + amount + " exp from " + target.getName() + ".");
//                                return true;
                        }
                }
                break;
        }

        return false;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext ctx) {
        ConcurrentSkipListSet<String> results = new ConcurrentSkipListSet<>();

        if (! ctx.isArgUsable(0)) {
            results.add("stars");
            results.add("exp");
            return results;
        }

        String action1 = ctx.getStringArg(0);
        Action1 action1Enum;
        switch (action1.toLowerCase()) {
            case "stars":
                action1Enum = Action1.STARS;
                break;
            case "exp":
                action1Enum = Action1.EXP;
                break;
            default:
                return results;
        }

        if (! ctx.isArgUsable(1)) {
            results.add("get");
            results.add("set");
            results.add("add");
            results.add("remove");
            return results;
        }

        return results;
    }
}
