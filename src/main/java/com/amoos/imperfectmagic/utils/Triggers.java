package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.init.IM_Init;
import com.amoos.imperfectmagic.item.StaffMagicNode;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class Triggers {
    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        LivingEntity e = event.getEntity();
        Entity se = event.getSource().getEntity();
        Entity ie = event.getSource().getDirectEntity();
        if (!(e.level() instanceof ServerLevel sl)) return;
        if (e instanceof Warden) {
            if (se instanceof Player p && ie == se && se.position().distanceToSqr(e.position()) < 16) {
                addItemEntity(sl, p.position(), IM_Init.SONIC.get());
            }
        }
        if (e instanceof Ghast) {
            if (se instanceof Player p && ie instanceof LargeFireball && sl.dimension() == Level.OVERWORLD) {
                addItemEntity(sl, p.position(), IM_Init.FIREBALL.get());
            }
        }
        if (event.getSource().is(DamageTypeTags.IS_FREEZING) && e.getMaxHealth() >= 50 && e instanceof Monster) {
            addItemEntity(sl, e.position().add(0, 1, 0), new ItemStack(IM_Init.WINTER_FLOWER::get));

        }
        if (e instanceof Phantom && se instanceof Player p && p.isFallFlying()) {
            addItemEntity(sl, p.position(), IM_Init.WIND_POCKET.get());
        }

        if (e instanceof Witch && se == null && event.getSource().is(DamageTypeTags.IS_FIRE)) {
            addItemEntity(sl, e.position().add(0, 2, 0), IM_Init.CURSE_WATCH.get());
        }

        if (e instanceof WitherBoss && se instanceof Player p && event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO) && ie != se) {
            addItemEntity(sl, p.position(), IM_Init.PURGE.get());
        }
        if(se instanceof Player p && se.getY()<-64) {
            addItemEntity(sl, p.position(), IM_Init.VOID_EYE.get());
            p.getAbilities().flying = true;
            p.onUpdateAbilities();
        }


    }/*e instanceof EnderMan &&*/

    @SubscribeEvent
    public static void entityAttack(LivingAttackEvent event) {
        LivingEntity e = event.getEntity();
        Entity se = event.getSource().getEntity();
        if (se == null) return;
        if (!(e.level() instanceof ServerLevel)) return;
        if (!IM_Action.shouldAttack(se, event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent event) {
        LivingEntity e = event.getEntity();
        Entity se = event.getSource().getEntity();
        if (se == null) return;
        if (!(e.level() instanceof ServerLevel sl)) return;
        if (e instanceof Player p && se instanceof Creeper cp && cp.isPowered() && event.getSource().is(DamageTypeTags.IS_EXPLOSION) && cp.getHealth() <= 1 && p.isInLava()) {
            addItemEntity(sl, p.position().add(0, 2.5, 0), IM_Init.BLOOMING_VOLCANO.get()).setNoGravity(true);
        }
        event.setAmount();


    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        Entity e = event.getEntity();
        if (!(e.level() instanceof ServerLevel sl)) return;
        if (e instanceof EyeOfEnder && sl.getLevelData().isThundering()) {
            e.discard();
            if (Math.random() < (double) 1 / 16) {
                sl.getLevelData().setRaining(false);
                addItemEntity(sl, new Vec3(e.position().x, 400, e.position().z), IM_Init.LIGHTNING_LINK.get()).setNoGravity(true);
            }
        }
    }

    public static ItemEntity addItemEntity(ServerLevel sl, Vec3 pos, ItemStack is) {
        ItemEntity ie = new ItemEntity(sl, pos.x, pos.y, pos.z, is);
        if (is.getItem() instanceof StaffMagicNode && !(is.getItem() == IM_Init.LIGHTNING_LINK.get())) {
            ie.setUnlimitedLifetime();
        }
        ie.setPickUpDelay(0);
        sl.addFreshEntity(ie);
        return ie;
    }

    public static ItemEntity addItemEntity(ServerLevel sl, Vec3 pos, Item i) {
        ItemStack is = new ItemStack(i);
        ItemEntity ie = new ItemEntity(sl, pos.x, pos.y, pos.z, is);
        if (i instanceof StaffMagicNode && !(i == IM_Init.LIGHTNING_LINK.get())) {
            ie.setUnlimitedLifetime();
        }
        ie.setPickUpDelay(10);
        sl.addFreshEntity(ie);
        return ie;
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide)
            player.displayClientMessage(Component.translatable("key.imperfect_magic.enter_game"), false);
    }
}
