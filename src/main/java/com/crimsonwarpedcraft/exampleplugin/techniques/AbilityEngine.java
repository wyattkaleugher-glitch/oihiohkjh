package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.entity.Player;

public class AbilityEngine {

    public static void useAbility(Player p, PlayerProfile prof, int slot) {
        if (prof.getTechnique() == null) return;
        
        String moveKey = "ability" + slot;
        if (prof.getCooldown(moveKey) > 0) {
            p.sendMessage("§cCooldown: " + prof.getCooldown(moveKey) + "s");
            return;
        }

        // Points directly to your existing files from the screenshot
        switch (prof.getTechnique()) {
            case GOJO -> GojoKit.execute(p, prof, slot);
            case SUKUNA -> SukunaKit.execute(p, prof, slot);
            case MAHITO -> MahitoKit.execute(p, prof, slot);
            case MEGUMI -> MegumiKit.execute(p, prof, slot);
            case YUJI -> YujiKit.execute(p, prof, slot);
            case INUMAKI -> InumakiKit.execute(p, prof, slot);
            case MAHORAGA -> {
                // If you have a MahoragaKit.java, call it here. 
                // Otherwise, it uses the base buffs from the profile.
            }
        }
    }
}
