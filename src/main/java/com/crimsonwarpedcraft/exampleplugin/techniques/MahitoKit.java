package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.entity.Player;

public class MahitoKit {
    public static void castSoulStrike(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 30) {
            player.sendMessage("§cNot enough Cursed Energy (30 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 30);
        player.sendMessage("§d§lIdle Transfiguration! §7(Direct soul shape manipulation damage...)");
    }

    public static void castPolymorphicIsomer(Player player, PlayerProfile profile) {
        if (profile.getCursedEnergy() < 70) {
            player.sendMessage("§cNot enough Cursed Energy (70 required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - 70);
        player.sendMessage("§5§lPolymorphic Isomer! §7(Combining transfigured souls into an absolute creature...)");
    }
}
