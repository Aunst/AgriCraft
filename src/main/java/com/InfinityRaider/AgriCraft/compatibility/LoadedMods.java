package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Loader;

public class LoadedMods {
    public static boolean mcMultipart;

    public static void init() {
        mcMultipart = Loader.isModLoaded(Names.Mods.mcMultipart);
    }
}
