package net.trueog.miniplaceholdersessentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.DescParseTickFormat;
import com.google.common.primitives.Ints;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
import net.essentialsx.api.v2.services.BalanceTop;
import net.trueog.utilitiesog.UtilitiesOG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EssentialsHook {

    private String k;
    private String m;
    private String b;
    private String t;
    private String q;
    private final DecimalFormat format = new DecimalFormat("#,###");
    private final DecimalFormat coordsFormat = new DecimalFormat("#.###");

    private Essentials essentials;
    private BalanceTop baltop;

    public EssentialsHook() {

        if (canRegister()) {

            register();

        }

    }

    /**
     * Checks if the Essentials plugin is present and enabled.
     *
     * @return true if Essentials is available and enabled, false otherwise.
     */
    public boolean canRegister() {

        return Bukkit.getPluginManager().getPlugin("Essentials") != null
                && Bukkit.getPluginManager().getPlugin("Essentials").isEnabled();

    }

    /**
     * Registers all MiniPlaceholders related to Essentials.
     *
     * @return true if registration is successful, false otherwise.
     */
    public boolean register() {

        // Initialize formatting suffixes from config
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        // Initialize formatting suffixes from Essentials' config
        k = essentials.getConfig().getString("formatting.thousands", "k");
        m = essentials.getConfig().getString("formatting.millions", "m");
        b = essentials.getConfig().getString("formatting.billions", "b");
        t = essentials.getConfig().getString("formatting.trillions", "t");
        q = essentials.getConfig().getString("formatting.quadrillions", "q");

        // Initialize Essentials and BalanceTop
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials != null && essentials.isEnabled()) {

            baltop = essentials.getBalanceTop();

            baltop.calculateBalanceTopMapAsync();

        } else {

            return false;

        }

        // Register all MiniPlaceholders.
        registerBaltopPlaceholders();
        registerPlayerPlaceholders();
        registerUserStatePlaceholders();
        registerWorldPlaceholders();

        return true;

    }

    /**
     * Registers all BalanceTop related MiniPlaceholders.
     */
    private void registerBaltopPlaceholders() {

        // baltop_balance_fixed:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_balance_fixed", args -> {

            if (args.size() < 1) {

                return "0";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();
            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);
            if (id >= entries.length || id < 0) {

                return "0";

            }

            return String.valueOf(entries[id].getBalance().longValue());

        });

        // baltop_balance_formatted:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_balance_formatted", args -> {

            if (args.size() < 1) {

                return "0";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();

            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);
            if (id >= entries.length || id < 0) {

                return "0";

            }

            return fixMoney(entries[id].getBalance().doubleValue());

        });

        // baltop_balance_commas:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_balance_commas", args -> {

            if (args.size() < 1) {

                return "0";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();
            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);
            if (id >= entries.length || id < 0) {

                return "0";

            }

            return format.format(entries[id].getBalance().doubleValue());

        });

        // baltop_balance:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_balance", args -> {

            if (args.size() < 1) {

                return "0";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();

            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);

            if (id >= entries.length || id < 0) {

                return "0";

            }

            return String.valueOf(entries[id].getBalance().doubleValue());

        });

        // baltop_player:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_player", args -> {

            if (args.size() < 1) {

                return "";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();

            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);

            if (id >= entries.length || id < 0) {

                return "0";

            }

            return entries[id].getDisplayName();

        });

        // baltop_player_stripped:id
        UtilitiesOG.registerGlobalPlaceholder("essentials_baltop_player_stripped", args -> {

            if (args.size() < 1) {

                return "";

            }

            Integer id = Ints.tryParse(args.get(0));
            if (id == null) {

                return "Invalid ID";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();

            BalanceTop.Entry[] entries = baltopCache.values().toArray(new BalanceTop.Entry[0]);
            if (id >= entries.length || id < 0) {

                return "0";

            }

            String displayName = entries[id].getDisplayName();

            return UtilitiesOG.stripFormatting(displayName);

        });

        // baltop_rank (requires player context)
        UtilitiesOG.registerAudiencePlaceholder("essentials_baltop_rank", player -> {

            if (player == null) {

                return "";

            }

            Map<UUID, BalanceTop.Entry> baltopCache = baltop.getBalanceTopCache();
            if (!baltopCache.containsKey(player.getUniqueId())) {

                return "";

            }

            return String.valueOf(new ArrayList<>(baltopCache.keySet()).indexOf(player.getUniqueId()) + 1);

        });

    }

    /**
     * Registers all player-specific MiniPlaceholders.
     */
    private void registerPlayerPlaceholders() {

        // tp_cooldown
        UtilitiesOG.registerAudiencePlaceholder("essentials_tp_cooldown", player -> {

            if (player == null) {

                return "0";

            }

            User user = essentials.getUser(player.getUniqueId());
            double cooldown = essentials.getSettings().getTeleportCooldown();
            long currentTime = System.currentTimeMillis();
            long lastTeleport = user.getLastTeleportTimestamp();
            long diff = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastTeleport);
            if (diff < cooldown) {

                return String.valueOf((int) (cooldown - diff));

            }

            return "0";

        });

        // kit_last_use:kitname
        UtilitiesOG.registerAudiencePlaceholder("essentials_kit_last_use", (player, args) -> {

            if (args.size() < 1) {

                return "Invalid kit name";

            }

            String kitName = args.get(0).toLowerCase();
            Kit kit;
            try {

                kit = new Kit(kitName, essentials);

            } catch (Exception error) {

                return "Invalid kit name";

            }

            long time = essentials.getUser(player.getUniqueId()).getKitTimestamp(kit.getName());
            if (time == 1 || time <= 0) {

                return "1";

            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return dateFormat.format(new Date(time));

        });

        // kit_is_available:kitname
        UtilitiesOG.registerAudiencePlaceholder("essentials_kit_is_available", (player, args) -> {

            if (args.size() < 1) {

                return "false";

            }

            String kitName = args.get(0).toLowerCase();
            Kit kit;
            User user = essentials.getUser(player.getUniqueId());
            try {

                kit = new Kit(kitName, essentials);

            } catch (Exception error) {

                return "false";

            }

            long time;
            try {

                time = kit.getNextUse(user);

            } catch (Exception error) {

                return "false";

            }

            return time == 0 ? "true" : "false";

        });

        // kit_time_until_available:kitname
        UtilitiesOG.registerAudiencePlaceholder("essentials_kit_time_until_available", (player, args) -> {

            if (args.size() < 1) {

                return "-1";

            }

            String kitName = args.get(0).toLowerCase();
            boolean raw = false;
            if (kitName.startsWith("raw_")) {

                raw = true;

                kitName = kitName.substring(4);
                if (kitName.isEmpty()) {

                    return "Invalid kit name";

                }

            }

            Kit kit;
            User user = essentials.getUser(player.getUniqueId());
            try {

                kit = new Kit(kitName, essentials);

            } catch (Exception error) {

                return "Invalid kit name";

            }

            long time;
            try {

                time = kit.getNextUse(user);

            } catch (Exception error) {

                return "-1";

            }

            if (time <= System.currentTimeMillis()) {

                return raw ? "0" : DateUtil.formatDateDiff(System.currentTimeMillis());

            }

            if (raw) {

                return String.valueOf(Instant.now().until(Instant.ofEpochMilli(time), ChronoUnit.MILLIS));

            } else {

                return DateUtil.formatDateDiff(time);

            }

        });

        // has_kit:kitname
        UtilitiesOG.registerAudiencePlaceholder("essentials_has_kit", (player, args) -> {

            if (args.size() < 1) {

                return "false";

            }

            String kit = args.get(0);
            Player oPlayer = player;
            if (oPlayer == null) {

                return "false";

            }

            return oPlayer.hasPermission("essentials.kits." + kit) ? "true" : "false";

        });

        // home:number
        UtilitiesOG.registerAudiencePlaceholder("essentials_home", (player, args) -> {

            if (args.size() < 1) {

                return "";

            }

            Integer homeNumber = Ints.tryParse(args.get(0).replaceAll("\\D+", ""));
            if (homeNumber == null) {

                return "";

            }

            homeNumber -= 1;

            User user = essentials.getUser(player.getUniqueId());
            if (homeNumber >= user.getHomes().size() || homeNumber < 0) {

                return "";

            }

            String homeName = user.getHomes().get(homeNumber);

            // Checks if the identifier matches the pattern home:d
            if (args.size() == 1 && args.get(0).matches("\\w+_\\d+")) {

                return homeName;

            }

            return "";

        });

        // home:number:coord (w/x/y/z)
        UtilitiesOG.registerAudiencePlaceholder("essentials_home_coord", (player, args) -> {

            if (args.size() < 2) {

                return "";

            }

            Integer homeNumber = Ints.tryParse(args.get(0).replaceAll("\\D+", ""));
            if (homeNumber == null) {

                return "";

            }

            homeNumber -= 1;

            User user = essentials.getUser(player.getUniqueId());
            if (homeNumber >= user.getHomes().size() || homeNumber < 0) {

                return "";

            }

            String homeName = user.getHomes().get(homeNumber);

            try {

                Location home = user.getHome(homeName);
                String coord = args.get(1).toLowerCase();

                switch (coord) {

                    case "w":
                        return home.getWorld().getName();
                    case "x":
                        return coordsFormat.format(home.getX());
                    case "y":
                        return String.valueOf((int) home.getY());
                    case "z":
                        return coordsFormat.format(home.getZ());
                    default:
                        return "";

                }

            } catch (Exception error) {

                return "";

            }

        });

        // worth[:material]
        UtilitiesOG.registerAudiencePlaceholder("essentials_worth", (player, args) -> {

            ItemStack item;
            if (args.size() >= 1 && !args.get(0).isEmpty()) {

                Material material = Material.getMaterial(args.get(0).toUpperCase());
                if (material == null) {

                    return "";

                }

                item = new ItemStack(material, 1);

            } else {

                Player oPlayer = player;
                if (oPlayer == null) {

                    return "";

                }

                ItemStack inHand = oPlayer.getInventory().getItemInMainHand();
                if (inHand.getType() == Material.AIR) {

                    return "";

                }

                item = inHand;

            }

            BigDecimal worth = essentials.getWorth().getPrice(essentials, item);
            if (worth == null) {

                return "";

            }

            return String.valueOf(worth.doubleValue());

        });

    }

    /**
     * Registers all user state related MiniPlaceholders.
     */
    private void registerUserStatePlaceholders() {

        // is_clearinventory_confirm
        UtilitiesOG.registerAudiencePlaceholder("essentials_is_clearinventory_confirm", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isPromptingClearConfirm() ? "true" : "false";

        });

        // is_pay_confirm
        UtilitiesOG.registerAudiencePlaceholder("essentials_is_pay_confirm", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isPromptingPayConfirm() ? "true" : "false";

        });

        // is_pay_enabled
        UtilitiesOG.registerAudiencePlaceholder("essentials_is_pay_enabled", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isAcceptingPay() ? "true" : "false";

        });

        // is_teleport_enabled
        UtilitiesOG.registerAudiencePlaceholder("essentials_is_teleport_enabled", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isTeleportEnabled() ? "true" : "false";

        });

        // is_muted
        UtilitiesOG.registerAudiencePlaceholder("essentials_is_muted", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isMuted() ? "true" : "false";

        });

        // vanished
        UtilitiesOG.registerAudiencePlaceholder("essentials_vanished", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isVanished() ? "true" : "false";

        });

        // afk
        UtilitiesOG.registerAudiencePlaceholder("essentials_afk", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isAfk() ? "true" : "false";

        });

        // afk_reason
        UtilitiesOG.registerAudiencePlaceholder("essentials_afk_reason", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());
            if (user.getAfkMessage() == null) {

                return "";

            }

            return UtilitiesOG.trueogExpand(user.getAfkMessage()).content();

        });

        // afk_player_count
        UtilitiesOG.registerGlobalPlaceholder("essentials_afk_player_count", args -> {

            long count = essentials.getUsers().getUserCount();

            return String.valueOf(count);

        });

        // msg_ignore
        UtilitiesOG.registerAudiencePlaceholder("essentials_msg_ignore", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isIgnoreMsg() ? "true" : "false";

        });

        // fly
        UtilitiesOG.registerAudiencePlaceholder("essentials_fly", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.getBase().getAllowFlight() ? "true" : "false";

        });

        // nickname
        UtilitiesOG.registerAudiencePlaceholder("essentials_nickname", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.getNickname() != null ? user.getNickname() : player.getName();

        });

        // nickname_stripped
        UtilitiesOG.registerAudiencePlaceholder("essentials_nickname_stripped", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            String nickname = user.getNickname() != null ? user.getNickname() : player.getName();

            return UtilitiesOG.stripFormatting(nickname);

        });

        // muted_time_remaining
        UtilitiesOG.registerAudiencePlaceholder("essentials_muted_time_remaining", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isMuted() ? DateUtil.formatDateDiff(user.getMuteTimeout()) : "";

        });

        // geolocation
        UtilitiesOG.registerAudiencePlaceholder("essentials_geolocation", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.getGeoLocation() != null ? user.getGeoLocation() : "";

        });

        // godmode
        UtilitiesOG.registerAudiencePlaceholder("essentials_godmode", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isGodModeEnabled() ? "true" : "false";

        });

        // unique
        UtilitiesOG.registerGlobalPlaceholder("essentials_unique", args -> {

            return NumberFormat.getInstance().format(essentials.getUsers());

        });

        // homes_set
        UtilitiesOG.registerAudiencePlaceholder("essentials_homes_set", player -> {

            if (player == null) {

                return "0";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.getHomes().isEmpty() ? "0" : String.valueOf(user.getHomes().size());

        });

        // homes_max
        UtilitiesOG.registerAudiencePlaceholder("essentials_homes_max", player -> {

            if (player == null) {

                return "0";

            }

            User user = essentials.getUser(player.getUniqueId());

            return String.valueOf(essentials.getSettings().getHomeLimit(user));

        });

        // jailed
        UtilitiesOG.registerAudiencePlaceholder("essentials_jailed", player -> {

            if (player == null) {

                return "false";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isJailed() ? "true" : "false";

        });

        // jailed_time_remaining
        UtilitiesOG.registerAudiencePlaceholder("essentials_jailed_time_remaining", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.isJailed() ? user.getFormattedJailTime() : "";

        });

        // pm_recipient
        UtilitiesOG.registerAudiencePlaceholder("essentials_pm_recipient", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            return user.getReplyRecipient() != null ? user.getReplyRecipient().getName() : "";

        });

        // safe_online
        UtilitiesOG.registerGlobalPlaceholder("essentials_safe_online", args -> {

            long count = StreamSupport.stream(essentials.getOnlineUsers().spliterator(), false)
                    .filter(user -> !user.isHidden()).count();

            return String.valueOf(count);

        });

    }

    /**
     * Registers all world-related MiniPlaceholders.
     */
    private void registerWorldPlaceholders() {

        // world_date
        UtilitiesOG.registerAudiencePlaceholder("essentials_world_date", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());
            Locale locale = essentials.getI18n().getCurrentLocale();

            long worldTime = user.getWorld() == null ? 0 : user.getWorld().getFullTime();

            return DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
                    .format(DescParseTickFormat.ticksToDate(worldTime));

        });

        // world_time
        UtilitiesOG.registerAudiencePlaceholder("essentials_world_time", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            long worldTime = user.getWorld() == null ? 0 : user.getWorld().getTime();

            return DescParseTickFormat.format12(worldTime);

        });

        // world_time_24
        UtilitiesOG.registerAudiencePlaceholder("essentials_world_time_24", player -> {

            if (player == null) {

                return "";

            }

            User user = essentials.getUser(player.getUniqueId());

            long worldTime = user.getWorld() == null ? 0 : user.getWorld().getTime();

            return DescParseTickFormat.format24(worldTime);

        });

    }

    /**
     * Formats a double value with English locale and up to two decimal places.
     *
     * @param d The double value to format.
     * @return The formatted string.
     */
    private String format(double d) {

        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);

        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);

        return format.format(d);

    }

    /**
     * Converts a double value into a shortened money format using suffixes.
     *
     * @param d The double value to format.
     * @return The formatted money string.
     */
    private String fixMoney(double d) {

        if (d < 1_000L) {

            return format(d);

        }

        if (d < 1_000_000L) {

            return format(d / 1_000L) + k;

        }

        if (d < 1_000_000_000L) {

            return format(d / 1_000_000L) + m;

        }

        if (d < 1_000_000_000_000L) {

            return format(d / 1_000_000_000L) + b;

        }

        if (d < 1_000_000_000_000_000L) {

            return format(d / 1_000_000_000_000L) + t;

        }

        if (d < 1_000_000_000_000_000_000L) {

            return format(d / 1_000_000_000_000_000L) + q;

        }

        return String.valueOf(d);

    }

}
