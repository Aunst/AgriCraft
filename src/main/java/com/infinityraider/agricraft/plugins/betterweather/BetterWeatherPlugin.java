package com.infinityraider.agricraft.plugins.betterweather;

import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.function.BiFunction;

@AgriPlugin
@SuppressWarnings("unused")
public class BetterWeatherPlugin extends SeasonPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded(this.getId());
    }

    @Override
    public String getId() {
        return Names.Mods.BETTER_WEATHER;
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    protected BiFunction<World, BlockPos, AgriSeason> getSeasonGetter() {
        return BetterWeatherSeasonGetter.getInstance();
    }
}
