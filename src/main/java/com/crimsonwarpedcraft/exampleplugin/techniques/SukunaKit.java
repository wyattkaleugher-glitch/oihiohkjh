package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.entity.Player;

public class SukunaKit {
    public static void castDismantle(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 15) {
            player.sendMessage("§cNot enough Cursed Energy (15 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 15);
        player.sendMessage("§7§lDismantle! §7(Flying invisible laceration sent...)");
    }

    public static void castCleave(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 30) {
            player.sendMessage("§cNot enough Cursed Energy (30 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 30);
        player.sendMessage("§4§lCleave! §7(Target adjusted to durability...)");
    }
}
