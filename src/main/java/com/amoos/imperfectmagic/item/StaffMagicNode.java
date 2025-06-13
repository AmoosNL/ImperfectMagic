package com.amoos.imperfectmagic.item;

import com.amoos.imperfectmagic.utils.IM_Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public abstract class StaffMagicNode extends LevelMagicNode {


    public StaffMagicNode(Rarity rarity, int chantTick) {
        super(rarity, chantTick);
    }

    @Override
    public boolean triggerCondition(Player p, ItemStack is) {
        return p.getMainHandItem().getItem() instanceof Staff;
    }

    @Override
    public Component levelUpConditionString() {
        return Component.translatable("key.imperfect_magic.trigger_condition");
    }

    @Override
    public boolean levelUpCondition(Player p, ItemStack is) {
        return p.experienceLevel >= 10 * LevelMagicNode.getLV(is);
    }

    @Override
    public void trigConsume(LivingEntity se, int lv) {
    }

    @Override
    public boolean trigBehavior(LivingEntity se, ItemStack is) {
        return this.trigBehavior(se, LevelMagicNode.getLV(is));
    }

    public abstract boolean trigBehavior(LivingEntity se, int lv);

    @Override
    public void levelUpConsume(Player p, ItemStack is, boolean success) {
        int consumption = LevelMagicNode.getLV(is);
        if (success) p.giveExperienceLevels(-10 * consumption);
        else {
            p.giveExperienceLevels(-consumption);
            p.displayClientMessage(IM_Component.colorText(ChatFormatting.RED, LevelMagicNode.FAIL_TEXT, LevelMagicNode.COOLDOWN_REMIND), true);
            p.getCooldowns().addCooldown(this, 2400);
        }
    }
}

