package com.amoos.imperfectmagic.item;

import com.amoos.imperfectmagic.ImperfectMagic;
import com.amoos.imperfectmagic.utils.IM_Component;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractMagicNode extends Item {

    private static final HashMap<Rarity,List<Item>> COOLDOWN= new HashMap<>(){
        @Override
        public List<Item> get(Object rarity){
            if (!this.containsKey(rarity)) this.put((Rarity) rarity,new ArrayList<>());
            return super.get(rarity);
        }
    };

    /*private static final List<Item> UNCOMMON = new ArrayList<>();
    private static final List<Item> RARE = new ArrayList<>();
    private static final List<Item> EPIC = new ArrayList<>();*/
    public final Rarity rarity;
    private final int cooldownTick;

    public AbstractMagicNode(Rarity rarity, int cooldownTick) {
        super(new Properties().stacksTo(1).rarity(rarity));
        this.rarity = rarity;
        this.cooldownTick = cooldownTick;
        COOLDOWN.get(rarity).add(this);

        /*switch (rarity) {
            case UNCOMMON -> UNCOMMON.add(this);
            case RARE -> RARE.add(this);
            case EPIC -> EPIC.add(this);
        }*/
    }

    public static ItemStack getInSlot(Entity e, int slotId) {
        AtomicReference<ItemStack> is = new AtomicReference<>(ItemStack.EMPTY);
        e.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                .ifPresent(capability -> is.set(capability.getStackInSlot(slotId).copy()));
        return is.get();
    }

    public static void setSlot(Entity e, int slotId, ItemStack is) {
        e.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
            if (capability instanceof IItemHandlerModifiable modHandler)
                modHandler.setStackInSlot(slotId, is);
        });
    }

    public static void exchangeSlot(Entity e, int slotId1, int slotId2) {
        ItemStack slot1 = getInSlot(e, slotId1);
        setSlot(e, slotId1, getInSlot(e, slotId2));
        setSlot(e, slotId2, slot1);
    }

    public static boolean isCorrectSlot(Player e, ItemStack is, int slotId) {
        if (!(is.getItem() instanceof AbstractMagicNode mi)) return false;
        if (!mi.triggerCondition(e, is)) return false;

        Rarity rarity = is.getRarity();
        if (slotId == 6 && rarity == Rarity.UNCOMMON) return true;
        if (slotId == 7 && rarity == Rarity.RARE) return true;
        return slotId == 8 && rarity == Rarity.EPIC;
    }

    public static boolean isCorrectSlot(Player e, int slotId) {
        ItemStack is = getInSlot(e, slotId);
        return isCorrectSlot(e, is, slotId);
    }

    public static boolean trigByNode(LivingEntity e, int slotId) {
        if (!(e instanceof Player player)) return false;
        ItemStack is = getInSlot(e, slotId);
        if (isCorrectSlot(player, is, slotId)) return ((AbstractMagicNode) is.getItem()).trig(player, is);
        return false;
    }

    public int getCooldownTick(LivingEntity e, ItemStack is) {
        return cooldownTick;
    }

    public abstract boolean triggerCondition(Player p, ItemStack is);


    public abstract boolean trigBehavior(LivingEntity se, ItemStack is);

    public abstract void trigConsume(LivingEntity se, int lv);

    @Override
    public void appendHoverText(ItemStack is, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(is, world, list, flag);
        list.add(IM_Component.arrayToComponent(
                Component.translatable("key.imperfect_magic.cooldown_tick"),
                Component.literal(String.valueOf(this.getCooldownTick(null,is)))));
    }

    public boolean trig(LivingEntity se, ItemStack is) {
        if (!(se instanceof Player player)) return false;
        if (player.getCooldowns().isOnCooldown(this)) return false;
        if (trigBehavior(se, is)) {
            /*switch (rarity) {
                case UNCOMMON -> UNCOMMON.forEach((node) -> player.getCooldowns().addCooldown(node, getCooldownTick(se, is)));
                case RARE -> RARE.forEach((node) -> player.getCooldowns().addCooldown(node, getCooldownTick(se, is)));
                case EPIC -> EPIC.forEach((node) -> player.getCooldowns().addCooldown(node, getCooldownTick(se, is)));
            }*/
            COOLDOWN.get(rarity).forEach((node) -> player.getCooldowns().addCooldown(node, getCooldownTick(se, is)));
            return true;
        }
        return false;
    }

    @Override
    public Rarity getRarity(ItemStack is) {
        return this.rarity;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return new InteractionResultHolder<>(InteractionResult.FAIL, is);
        if (player.isCreative()) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        } else return super.use(level, player, hand);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity e, int timeLeft) {
        super.releaseUsing(itemStack, level, e, timeLeft);
        trig(e, itemStack);
    }
}

