package net.captainfate404.adaptablesyringes.item;

import net.captainfate404.adaptablesyringes.AdaptableSyringes;
import net.captainfate404.adaptablesyringes.item.custom.EffectItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {
    public static final EffectItem SYRINGE = (EffectItem) registerItem("syringe", new EffectItem(new FabricItemSettings()));
    public static final Item NEEDLE = registerItem("needle", new Item(new FabricItemSettings()));

    private static void addItemsToToolsItemGroup(FabricItemGroupEntries entries) {
        entries.add(SYRINGE);
        entries.add(NEEDLE);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AdaptableSyringes.MOD_ID, name), item);
    }

    public static void registerModItems(){
        AdaptableSyringes.LOGGER.info("Registering Mod Items for " + AdaptableSyringes.MOD_ID);
    }
}
