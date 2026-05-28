package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.entity.Player;

public class InumakiKit {
    public static void castDontMove(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 40) {
            player.sendMessage("§cNot enough Cursed Energy (40 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 40);
        player.sendMessage("§7§l[Cursed Speech] §f§l\"Don't Move!\" §7(Targets paralyzed. Throat damage calculated...)");
    }

    public static void castPlunge(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 60) {
            player.sendMessage("§cNot enough Cursed Energy (60 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 60);
        player.sendMessage("§7§l[Cursed Speech] §8§l\"Plunge!\" §7(Gravitational pressure crushing target area...)");
    }
}
