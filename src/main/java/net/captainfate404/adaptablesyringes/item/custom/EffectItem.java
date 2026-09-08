package net.captainfate404.adaptablesyringes.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;


public class EffectItem extends Item {
    public EffectItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        //FORMATTING
        if (stack.hasNbt() && stack.getNbt().contains("StoredEffects", 9)) {
            NbtList nbtEffectsList = stack.getNbt().getList("StoredEffects", 10);

            tooltip.add(Text.literal("Loaded Contents:").formatted(Formatting.GRAY));

            for (int i = 0; i < nbtEffectsList.size(); i++) {
                NbtCompound effectNbt = nbtEffectsList.getCompound(i);
                StatusEffectInstance effect = StatusEffectInstance.fromNbt(effectNbt);

                if (effect != null) {
                    var effectName = Text.translatable(effect.getEffectType().getTranslationKey());

                    int level = effect.getAmplifier() + 1;
                    String levelString = level > 1 ? " " + level : "";

                    int durationTicks = effect.getDuration();
                    int totalSeconds = durationTicks / 20;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;

                    String durationString = String.format(" (%d:%02d)", minutes, seconds);

                    Formatting color = effect.getEffectType().isBeneficial() ? Formatting.BLUE : Formatting.RED;

                    tooltip.add(Text.literal("- ").formatted(Formatting.DARK_GRAY)
                            .append(effectName).append(levelString).append(durationString).formatted(color));
                }
            }
        } else {
            tooltip.add(Text.literal("Empty Syringe").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack mainHandStack = user.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND) {
            //check if the player is sneaking and if they already have effects in their syringe
            if (user.isSneaking()) {
                if (mainHandStack.hasNbt() && mainHandStack.getNbt().contains("StoredEffects", 9)) {
                    NbtList nbtEffectsList = mainHandStack.getNbt().getList("StoredEffects", 10);

                    for (int i = 0; i < nbtEffectsList.size(); i++) {
                        NbtCompound effectNbt = nbtEffectsList.getCompound(i);
                        StatusEffectInstance effect = StatusEffectInstance.fromNbt(effectNbt);
                        if (effect != null) {
                            user.addStatusEffect(effect);
                        }
                    }
                    mainHandStack.getNbt().remove("StoredEffects");
                    mainHandStack.getNbt().remove("OriginalPotionData");
                    return TypedActionResult.success(mainHandStack, world.isClient());
                }
                return TypedActionResult.fail(mainHandStack);
            }

            ItemStack offHandStack = user.getOffHandStack();

            //check if the player has a potion in offhand and add effects to syringe
            if (offHandStack.isOf(Items.POTION) && !mainHandStack.hasNbt() && (mainHandStack.getNbt() == null || !mainHandStack.getNbt().contains("StoredEffects", 9))) {
                var effects = PotionUtil.getPotionEffects(offHandStack);

                if (!effects.isEmpty()) {
                    if (!world.isClient()) {
                        NbtList nbtEffectsList = new NbtList();

                        for (StatusEffectInstance effect : effects) {
                            NbtCompound effectNbt = new NbtCompound();
                            effect.writeNbt(effectNbt);
                            nbtEffectsList.add(effectNbt);
                        }

                        NbtCompound mainNbt = mainHandStack.getOrCreateNbt();
                        mainNbt.put("StoredEffects", nbtEffectsList);

                        if (offHandStack.hasNbt()) {
                            mainNbt.put("OriginalPotionData", offHandStack.getNbt().copy());
                        }


                        if (!user.getAbilities().creativeMode) {
                            offHandStack.decrement(1);

                            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);

                            if (offHandStack.isEmpty()) {
                                user.setStackInHand(Hand.OFF_HAND, emptyBottle);
                            } else {
                                user.getInventory().offerOrDrop(emptyBottle);
                            }
                        }
                    }

                    return TypedActionResult.success(mainHandStack, world.isClient());
                }
            } else if (offHandStack.isOf(Items.GLASS_BOTTLE) && mainHandStack.hasNbt() && mainHandStack.getNbt().contains("StoredEffects", 9)) {
                if (!world.isClient()) {
                    ItemStack customPotion = new ItemStack(Items.POTION);

                    if (mainHandStack.getNbt().contains("OriginalPotionData", 10)) {
                        customPotion.setNbt(mainHandStack.getNbt().getCompound("OriginalPotionData").copy());
                    } else {
                        NbtList nbtEffectsList = mainHandStack.getNbt().getList("StoredEffects", 10);

                        java.util.List<StatusEffectInstance> effectsToTransfer = new java.util.ArrayList<>();
                        for (int i = 0; i < nbtEffectsList.size(); i++) {
                            StatusEffectInstance effect = StatusEffectInstance.fromNbt(nbtEffectsList.getCompound(i));
                            if (effect != null) {
                                effectsToTransfer.add(effect);
                            }
                        }

                        PotionUtil.setCustomPotionEffects(customPotion, effectsToTransfer);
                    }

                    mainHandStack.getNbt().remove("StoredEffects");
                    mainHandStack.getNbt().remove("OriginalPotionData");

                    if (!user.getAbilities().creativeMode) {
                        offHandStack.decrement(1);
                        if (offHandStack.isEmpty()) {
                            user.setStackInHand(Hand.OFF_HAND, customPotion);
                        } else {
                            user.getInventory().offerOrDrop(customPotion);
                        }
                    } else {
                        user.getInventory().offerOrDrop(customPotion);
                    }
                }

                return TypedActionResult.success(mainHandStack, world.isClient());
            }
            //Empty syringe if it's full and player isn't injecting
            else if (mainHandStack.hasNbt() && mainHandStack.getNbt().contains("StoredEffects", 9)) {
                if (!world.isClient()) {
                    mainHandStack.getNbt().remove("StoredEffects");
                    mainHandStack.getNbt().remove("OriginalPotionData");
                }

                return TypedActionResult.success(mainHandStack, world.isClient());
            }
        }
        return TypedActionResult.pass(mainHandStack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            //get StoredEffects list and apply effects from it to the target on hit
            if (stack.hasNbt() && stack.getNbt().contains("StoredEffects", 9)) {
                if (!user.getWorld().isClient()) {
                    NbtList nbtEffectsList = stack.getNbt().getList("StoredEffects", 10);


                    for (int i = 0; i < nbtEffectsList.size(); i++) {
                        NbtCompound effectNbt = nbtEffectsList.getCompound(i);
                        StatusEffectInstance effect = StatusEffectInstance.fromNbt(effectNbt);

                        if (effect != null) {
                            target.addStatusEffect(effect);
                        }
                    }

                    stack.getNbt().remove("StoredEffects");
                    stack.getNbt().remove("OriginalPotionData");
                }

                return ActionResult.success(user.getWorld().isClient());

            }
        }
        return ActionResult.PASS;
    }
}