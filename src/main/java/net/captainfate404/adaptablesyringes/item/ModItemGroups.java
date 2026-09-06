package net.captainfate404.adaptablesyringes.item;

import net.captainfate404.adaptablesyringes.AdaptableSyringes;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup ADAPTABLESYRINGES_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AdaptableSyringes.MOD_ID, "adaptablesyringes"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.adaptablesyringes"))
                    .icon(() -> new ItemStack(ModItems.EMPTY_SYRINGE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.EMPTY_SYRINGE);
                        entries.add(ModItems.NEEDLE);

                    }).build());

    public static void registerItemGroups() {
        AdaptableSyringes.LOGGER.info("Registering Item Groups for " + AdaptableSyringes.MOD_ID);
    }
}
