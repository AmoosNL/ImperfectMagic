package com.amoos.imperfectmagic.item;

import com.amoos.imperfectmagic.ImperfectMagic;
import com.amoos.imperfectmagic.utils.IM_Action;
import com.amoos.imperfectmagic.utils.IM_Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class CustomMagicNode extends AbstractMagicNode {

    public CustomMagicNode(Rarity rarity) {
        super(rarity, 0);
    }

    @Nullable
    public static Rarity rarityByValue(int value) {
        switch (value) {
            case 0 -> {
                return Rarity.COMMON;
            }
            case 1 -> {
                return Rarity.UNCOMMON;
            }
            case 2 -> {
                return Rarity.RARE;
            }
            case 3 -> {
                return Rarity.EPIC;
            }
            default -> {
                ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value: " + value + "but this may not have an effect")));
                return null;
            }
        }
    }





    public static int act(LivingEntity se, Entity e, CompoundTag tag, int time) {
        String str = tag.getString("Act" + time);
        double modifier = tag.getDouble("ActModifier" + time);
        if (e instanceof LivingEntity||e ==null) {
            LivingEntity living = ((LivingEntity) e);
            switch (str) {
                case "Damage" -> {
                    if(e!=null) e.hurt(IM_Action.damageSource(se, DamageTypes.MOB_ATTACK), (float) modifier);
                    return 5;
                }
                case "Fireball" -> {
                    if(e!=null) IM_Action.fireball(living, tag.getInt("FireballSpeed" + time), (int) modifier);
                    return 5;
                }
                case "Volcano" -> {
                    IM_Action.volcano(se, living, (int) modifier);
                    return 16;
                }
                case "Ice" -> {
                    IM_Action.ice(se, living, (int) modifier);
                    return 8;
                }
                case "Purge" -> {
                    IM_Action.purge(se, living, (int) modifier);
                    return 10;
                }
                case "LightningLink" -> {
                    IM_Action.lightningLink(se, se, living, (int) modifier);
                    return 16;
                }
            }
        }else switch (str) {
            case "MotionSet" -> {
                if (tag.getString("Vec" + time).equals("")) {
                    e.setDeltaMovement(tag.getDouble("MotionX" + time), tag.getDouble("MotionY" + time), tag.getDouble("MotionZ" + time));
                } else e.setDeltaMovement(vec(e, tag.getString("MotionBy" + time)).scale(modifier));
                return 5;
            }
            case "pass" -> {
                return 0;
            }
            default -> {
                tag.putString("Act" + time, "pass");
                //ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value of Act: " + str + "but this may not have an effect")));
                return 0;
            }
        }
        return 0;


    }


    public static abstract class Action<E>{

        protected Action(Class<E> type, String name) {
            this.type = type;
            REGISTRY.put(name, this);
        }

        public static class Registry extends HashMap<String, Action<?>>{
            @Nullable
            public <T> Action<T> get(Class<T> tClass, String key) {
                Action<?> action = super.get(key);
                if(action!=null&& action.type == tClass) return (Action <T>) action;
                else return null;
            }
        }

        public final Class<E> type;

        private static final Registry REGISTRY = new Registry();

        @Nullable
        public static <T> Action<T> get(Class<T> tClass, String name){
            return REGISTRY.get(tClass, name);
        }

        public abstract List<E> get(Entity se, CompoundTag tag, int time);

        public abstract int getCooldown();

        public void act(Entity se, Entity e, double amplifier){
            Level l = e.level();
            if(l instanceof ServerLevel) onServer(se,e,amplifier);
            if(l instanceof ClientLevel) onClient(se,e,amplifier);
        }

        public abstract void onServer(Entity se, Entity e, double amplifier);
        public abstract void onClient(Entity se, Entity e, double amplifier);
    }



    public static abstract class Selector<E>{

        protected Selector(Class<E> type, String name) {
            this.type = type;
            REGISTRY.put(name, this);
        }

        public static class Registry extends HashMap<String, Selector<?>>{
            @Nullable
            public <T> Selector<T> get(Class<T> tClass, String key) {
                Selector<?> selector = super.get(key);
                if(selector!=null&& selector.type == tClass) return (Selector<T>) selector;
                else return null;
            }
        }

        public final Class<E> type;

        private static final Registry REGISTRY = new Registry();

        public abstract int cooldown(CompoundTag tag, int time);

        public static boolean noClip(CompoundTag tag, int time){return tag.getBoolean(NO_CLIP + time);}

        public abstract Predicate<E> predicate(Entity se, CompoundTag tag, int time);

        public static double selectDistance(CompoundTag tag, int time){
            return tag.getDouble(SELECT_DISTANCE + time);
        }

        public static Vec3 selectPos(Entity se, CompoundTag tag, int time){
            return vec(se, tag.getString("SelectPos"+time));
        }

        public static double selectWidth(CompoundTag tag, int time){
            return tag.getDouble("SelectWidth" + time);
        }

        public static double selectHeight(CompoundTag tag, int time){
            return tag.getDouble("SelectHeight" + time);
        }






        @Nullable
        public static <T> Selector<T> get(Class<T> tClass, String name){
            return REGISTRY.get(tClass, name);
        }

        public abstract List<E> get(Entity se, CompoundTag tag, int time);
    }

    public static final String COOLDOWN_TIMES = "CooldownTickTimes";
    public static final String NO_CLIP = "NoClip";


    public static final String SELECT_DISTANCE = "SelectDistance";

    public static final String CUSTOM_COOLDOWN = "CustomCooldown";


    public static final String SHOULD_COOLDOWN = "ShouldCooldown";

    public static final String VEC = "Vec";

    /*public static List<Entity> entitySelect(LivingEntity se, CompoundTag tag, int time, int realTime) {
        String str = tag.getString("EntitySelectType" + time);
        Level level = se.level();
        switch (str) {
            case "Self" -> {
                tag.putInt(COOLDOWN_TIMES + realTime, 1);
                return new IM_Math.LazyArray<>(se);
            }
            case "EntityWatched" -> {
                tag.putInt(COOLDOWN_TIMES + realTime, 1);
                Predicate<Entity> predicate = predicateEntity(se, tag.getString(ENTITY_SELECT_PREDICATE + time));
                boolean noClip = tag.getBoolean(NO_CLIP + time);
                return new IM_Math.LazyArray<>(IM_Math.getEntityWatched(Entity.class, se, predicate, 0xff, !noClip));
            }
            case "EntitiesInLineOfSight" -> {
                Predicate<Entity> predicate = predicateEntity(se, tag.getString(ENTITY_SELECT_PREDICATE + time));
                double distance = tag.getDouble(SELECT_DISTANCE + time);
                boolean noClip = tag.getBoolean(NO_CLIP + time);
                tag.putInt(COOLDOWN_TIMES + realTime, (int) distance);
                return IM_Math.getEntitiesOnLine(Entity.class, level, se.getEyePosition(), se.getEyePosition().add(se.getLookAngle().scale(distance)), predicate, !noClip);
            }
            case "EntitiesAround" -> {
                Predicate<Entity> predicate = predicateEntity(se, tag.getString(ENTITY_SELECT_PREDICATE + time));
                Vec3 centre = vec(se, tag.getString(VEC + time));
                double width = tag.getDouble("SelectWidth" + time);
                double height = tag.getDouble("SelectHeight" + time);
                tag.putInt(COOLDOWN_TIMES + realTime, (int) Math.sqrt(width * height));
                return IM_Math.getEntities(Entity.class, level, centre, width, height, width, predicate);
            }
            case "Ditto" -> {
                return entitySelect(se, tag, time - 1, realTime);
            }
            default -> {
                tag.putInt(COOLDOWN_TIMES + realTime, 1);
                ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value of EntitySelect: " + str + "but this may not have an effect")));
                return new IM_Math.LazyArray<>(se);
            }
        }
    }
    public static List<Entity> entitySelect(LivingEntity se, CompoundTag tag, int time) {
        return entitySelect(se,tag,time,time);
    }*/



    public static Vec3 vec(Entity se, String str) {
        switch (str) {
            case "LookPos" -> {
                return IM_Math.getLookPos(se);
            }
            case "Self" -> {
                return se.position();
            }
            case "LookVec" -> {
                return se.getLookAngle();
            }
            default -> {
                ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value of Vec: " + str + "but this may not have an effect")));
                return IM_Math.getCentre(se);
            }
        }
    }

    @Override
    public int getCooldownTick(LivingEntity e, ItemStack is) {
        CompoundTag tag = is.getOrCreateTag();
        int custom = tag.getInt(CUSTOM_COOLDOWN);
        if(custom!=0) return custom;
        else{
            int cooldown = 0;
            for (int i = 0; i <= tag.getInt("TheNumberOfNodes"); i++) {
                if (tag.getString("Select" + i).equals("Entity")) {
                    String str = tag.getString("SelectType" + i);
                    Selector<Entity> selector = Selector.get(Entity.class, str);
                    if(selector!=null) cooldown += selector.cooldown(tag, i)*act(null,null,tag, i);
                }
            }
            return cooldown;
        }
        //else return tag.getInt(SHOULD_COOLDOWN);
    }

    @Override
    public boolean triggerCondition(Player p, ItemStack is) {
        return true;
    }

    @Override
    public boolean trigBehavior(LivingEntity se, ItemStack is) {
        AtomicBoolean flag = new AtomicBoolean(false);
        CompoundTag tag = is.getOrCreateTag();
        tag.putInt(SHOULD_COOLDOWN, 0);
        int cooldown = 0;
        for (int i = 0; i <= tag.getInt("TheNumberOfNodes"); i++) {
            if (tag.getString("Select" + i).equals("Entity")) {
                /*int perCool = 0;
                for (Entity e : entitySelect(se, tag, i)) {
                    perCool = act(se, e, tag, i);
                    flag = true;
                }
                tag.putInt(SHOULD_COOLDOWN,tag.getInt(SHOULD_COOLDOWN) + perCool * tag.getInt(COOLDOWN_TIMES + i));
                ImperfectMagic.LOGGER.info(String.valueOf(tag.getInt(SHOULD_COOLDOWN)));*/
                String str = tag.getString("SelectType" + i);
                Selector<Entity> selector = Selector.get(Entity.class, str);
                if(selector==null) ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value of EntitySelect: " + str + "but this may not have an effect")));
                else {
                    List<Entity> entities = selector.get(se, tag, i);
                    for (Entity entity : entities) {
                        act(se, entity, tag, i);
                        flag.set(true);
                    }
                    cooldown += selector.cooldown(tag, i)*act(null,null,tag, i);
                }
            }
        }
        tag.putInt(SHOULD_COOLDOWN,cooldown);
        return flag.get();
    }

    @Override
    public void trigConsume(LivingEntity se, int lv) {
    }


}


