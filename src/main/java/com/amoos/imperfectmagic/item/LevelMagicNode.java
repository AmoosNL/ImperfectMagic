package com.amoos.imperfectmagic.item;

import com.amoos.imperfectmagic.utils.IM_Component;
import com.amoos.imperfectmagic.utils.IM_Math;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class LevelMagicNode extends AbstractMagicNode {
    public static final String LV_NBT = "IM_lv";
    public static final String TIMES_NBT = "IM_fail_times";
    public static final MutableComponent FAIL_TEXT = Component.translatable("key.imperfect_magic.fail_to_lv_up");
    public static final MutableComponent LACK_XP = Component.translatable("key.imperfect_magic.lack_reason_xp_level");
    public static final MutableComponent COOLDOWN_REMIND = Component.translatable("key.imperfect_magic.itemStack_cooldown");
    public static final MutableComponent SUCCESS_TEXT = Component.translatable("key.imperfect_magic.level_up_successful");
    public static final MutableComponent MAX_LEVEL_TEXT = Component.translatable("key.imperfect_magic.already_max_level");
    public static int MAX_LEVEL = 6;
    protected static double SP = 0.1;//success probability

    public LevelMagicNode(Rarity rarity, int chantTick) {
        super(rarity, chantTick);
    }

    public static boolean setSuccessProbability(double sp) {
        if (sp <= 1) SP = sp;
        else return false;
        return true;
    }

    public static int getLV(ItemStack is) {
        int i = is.getOrCreateTag().getInt(LV_NBT);
        if (i < 1) return setLV(is, 1);
        return i;
    }

    public static int setLV(ItemStack is, int value) {
        CompoundTag ct = is.getOrCreateTag();
        ct.putInt(LV_NBT, value);
        return ct.getInt(LV_NBT);
    }

    public static int changeLV(ItemStack is, int changeValue) {
        CompoundTag ct = is.getOrCreateTag();
        ct.putInt(LV_NBT, ct.getInt(LV_NBT) + changeValue);
        return ct.getInt(LV_NBT);
    }

    public static int getTimes(ItemStack is) {
        int i = is.getOrCreateTag().getInt(TIMES_NBT);
        if (i < 1) return setTimes(is, 1);
        return i;
    }

    public static int setTimes(ItemStack is, int value) {
        CompoundTag ct = is.getOrCreateTag();
        ct.putInt(TIMES_NBT, value);
        return ct.getInt(TIMES_NBT);
    }

    public static int changeTimes(ItemStack is, int changeValue) {
        CompoundTag ct = is.getOrCreateTag();
        ct.putInt(TIMES_NBT, ct.getInt(TIMES_NBT) + changeValue);
        return ct.getInt(TIMES_NBT);
    }

    public static int successLevelUp(Player player, ItemStack is) {
        changeLV(is, 1);
        setTimes(is, 1);
        player.displayClientMessage(IM_Component.colorText(ChatFormatting.GREEN, SUCCESS_TEXT), true);
        return getLV(is);
    }

    @Nullable
    public abstract Component levelUpConditionString();

    public abstract boolean levelUpCondition(Player p, ItemStack is);

    public abstract void levelUpConsume(Player p, ItemStack is, boolean success);

    @Override
    public void appendHoverText(ItemStack is, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(is, world, list, flag);
        Component clar = Component.translatable("key.imperfect_magic.magic_lv");
        Component clar1 = Component.translatable("key.imperfect_magic.level_up_probability");
        Component lv = Component.literal(String.valueOf(LevelMagicNode.getLV(is)));
        if (LevelMagicNode.getLV(is) >= LevelMagicNode.MAX_LEVEL) {
            clar = IM_Component.colorText(ChatFormatting.RED, clar, lv);
            clar1 = IM_Component.arrayToComponent(clar1, IM_Component.colorText(ChatFormatting.RED, LevelMagicNode.MAX_LEVEL_TEXT));
        } else {
            clar = IM_Component.arrayToComponent(clar, lv);
            clar1 = Component.literal(clar1.getString() + IM_Math.toPercent(LevelMagicNode.SP * LevelMagicNode.getTimes(is)));
        }
        if (levelUpConditionString() != null) list.add(levelUpConditionString());
        list.add(clar);
        list.add(clar1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return new InteractionResultHolder<>(InteractionResult.FAIL, is);
        if (player.isShiftKeyDown()) {
            if (LevelMagicNode.getLV(is) < LevelMagicNode.MAX_LEVEL) {
                if (player.isCreative()) {
                    LevelMagicNode.successLevelUp(player, is);
                } else {
                    if (this.levelUpCondition(player, is)) {
                        if (Math.random() < LevelMagicNode.SP * LevelMagicNode.getTimes(is)) {
                            this.levelUpConsume(player, is, true);
                            LevelMagicNode.successLevelUp(player, is);
                        } else {
                            this.levelUpConsume(player, is, false);
                            LevelMagicNode.changeTimes(is, 1);
                        }
                    } else
                        player.displayClientMessage(IM_Component.colorText(ChatFormatting.RED, LevelMagicNode.FAIL_TEXT, LevelMagicNode.LACK_XP), true);
                }
            } else
                player.displayClientMessage(IM_Component.colorText(ChatFormatting.RED, LevelMagicNode.MAX_LEVEL_TEXT), true);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        } else return super.use(level, player, hand);
    }
}
