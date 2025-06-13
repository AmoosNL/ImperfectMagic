package com.amoos.imperfectmagic.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

public class Staff extends TieredItem {


    public static final Item STAFF = new Staff();

    public Staff() {
        super(new Tier() {
            public int getUses() {
                return 50;
            }

            public float getSpeed() {
                return 0;
            }

            public float getAttackDamageBonus() {
                return 0;
            }

            public int getLevel() {
                return 0;
            }

            public int getEnchantmentValue() {
                return 0;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Blocks.AMETHYST_CLUSTER), new ItemStack(Items.COPPER_INGOT));
            }
        }, new Item.Properties());
    }

    public static boolean hurt(ItemStack itemStack, @Nullable Player player) {
        return hurt(itemStack, player, 1);
    }

    public static boolean hurt(ItemStack itemStack, @Nullable Player player, int num) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.isCreative()) return false;
        if (itemStack.hurt(num, RandomSource.create(), serverPlayer)) {
            itemStack.shrink(1);
            itemStack.setDamageValue(0);
            return true;
        }
        return false;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player e, InteractionHand hand) {
        if (e.isShiftKeyDown()) {
            for (int i = 6; i <= 8; i++)
                if (AbstractMagicNode.isCorrectSlot(e, AbstractMagicNode.getInSlot(e, i + 9), i) && !(AbstractMagicNode.isCorrectSlot(e, i))) {
                    AbstractMagicNode.exchangeSlot(e, i, i + 9);
                }
            return super.use(level, e, hand);
        } else {
            e.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, e.getItemInHand(hand));
        }

    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity e, int timeLeft) {
        super.releaseUsing(itemStack, level, e, timeLeft);
        if (!(e instanceof Player player)) return;
        ItemStack is = AbstractMagicNode.getInSlot(e, 8);
        if (AbstractMagicNode.trigByNode(player, 8)) {
            hurt(itemStack, player);
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack itemStack, LivingEntity e) {
        boolean retval = super.onEntitySwing(itemStack, e);
        if (!(e instanceof Player player)) return retval;//ServerPlayer player)) return retval;
        if (player.isUsingItem()) return retval;
        if (e.isShiftKeyDown()) {
            if (AbstractMagicNode.trigByNode(e, 7)) hurt(itemStack, player);
        } else if (AbstractMagicNode.trigByNode(e, 6)) hurt(itemStack, player);
        return retval;
    }

}
