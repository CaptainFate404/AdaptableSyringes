package captainfate404.adaptablesyringes.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.captainfate404.adaptablesyringes.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class AdaptablesyringesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                if (stack.hasNbt() && stack.getNbt().contains("StoredEffects", 9)) {
                    NbtList nbtEffectsList = stack.getNbt().getList("StoredEffects", 10);
                    List<StatusEffectInstance> customEffects = new ArrayList<>();

                    for (int i = 0; i < nbtEffectsList.size(); i++) {
                        net.minecraft.nbt.NbtCompound effectNbt = nbtEffectsList.getCompound(i);
                        StatusEffectInstance effect = StatusEffectInstance.fromNbt(effectNbt);
                        if (effect != null) {
                            customEffects.add(effect);
                        }
                    }
                    return PotionUtil.getColor(customEffects);
                }
                return 0xFFFFFFFF;
            }
            return -1;
        }, ModItems.SYRINGE);

        ModelPredicateProviderRegistry.register(ModItems.SYRINGE, Identifier.of("adaptablesyringes", "filled"),
                (stack, world, entity, seed) -> {
                    return (stack.hasNbt() && stack.getNbt().contains("StoredEffects", 9)) ? 1.0F : 0.0F;
        });
    }
}
