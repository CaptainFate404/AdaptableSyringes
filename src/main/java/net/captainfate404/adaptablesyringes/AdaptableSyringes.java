package net.captainfate404.adaptablesyringes;

import net.captainfate404.adaptablesyringes.item.ModItemGroups;
import net.captainfate404.adaptablesyringes.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptableSyringes implements ModInitializer {
    public static final String MOD_ID = "adaptablesyringes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
    }
}
