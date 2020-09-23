package net.cydhra.technocracy.foundation.data.config;

import net.cydhra.technocracy.foundation.TCFoundation;
import net.minecraftforge.common.config.Config;

@Config(modid = TCFoundation.MODID)
public class RenderConfig {
    @Config.Comment("Determines if facades should be cached aggressively, can break ctm of facades")
    @Config.Name("Aggressive facade caching")
    public static boolean aggressiveFacadeCaching = false;

    @Config.Name("Render Facades")
    public static boolean renderFacades = true;
}
