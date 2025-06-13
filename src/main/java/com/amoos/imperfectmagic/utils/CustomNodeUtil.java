package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.ImperfectMagic;
import com.amoos.imperfectmagic.item.CustomMagicNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;



public class CustomNodeUtil {

    public static final String ENTITY_SELECT_PREDICATE = "EntitySelectPredicate";
    public static CustomMagicNode.Selector<Entity> SELF = new EntitySelector("Self") {
        @Override
        public int cooldown(CompoundTag tag, int time) {
            return 1;
        }

        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            return new IM_Math.LazyArray<>(se);
        }
    };

    public static CustomMagicNode.Selector<Entity> ENTITY_WATCHED = new EntitySelector("EntityWatched") {
        @Override
        public int cooldown(CompoundTag tag, int time) {
            return 1;
        }

        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            Predicate<Entity> predicate = predicate(se, tag, time);
            boolean noClip = noClip(tag,time);
            return new IM_Math.LazyArray<>(IM_Math.getEntityWatched(Entity.class, se, predicate, 0xff, !noClip));
        }

    };

    public static CustomMagicNode.Selector<Entity> ENTITY_AROUND = new EntitySelector("EntitiesAround") {
        @Override
        public int cooldown(CompoundTag tag, int time) {
            double width = selectWidth(tag,time);
            double height = selectHeight(tag,time);
            return (int) (width * Math.sqrt(Math.max(1, height)));
        }

        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            Predicate<Entity> predicate = predicate(se, tag, time);
            Vec3 centre = selectPos(se, tag, time);
            double width = selectWidth(tag,time);
            double height = selectHeight(tag,time);
            return IM_Math.getEntities(Entity.class, se.level(), centre, width, height, width, predicate);
        }

    };

    public static CustomMagicNode.Selector<Entity> ENTITIES_SIGHT = new EntitySelector("EntitiesInLineOfSight") {
        @Override
        public int cooldown(CompoundTag tag, int time) {
            double distance = selectDistance(tag, time);
            return (int) Math.sqrt(distance);
        }

        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            Predicate<Entity> predicate = predicate(se, tag, time);
            double distance = selectDistance(tag, time);
            boolean noClip = noClip(tag,time);
            return IM_Math.getEntitiesOnLine(Entity.class, se.level(), se.getEyePosition(), se.getEyePosition().add(se.getLookAngle().scale(distance)), predicate, !noClip);
        }

    };

    public static CustomMagicNode.Selector<Entity> DITTO_ENTITY = new EntitySelector("DittoEntity") {
        @Override
        public int cooldown(CompoundTag tag, int time) {
            while (time >= 0){
                CustomMagicNode.Selector<Entity> selector = CustomMagicNode.Selector.get(Entity.class, tag.getString("SelectType" + --time));
                if (selector != null) return selector.cooldown(tag,time);
            }
            return 0;

        }

        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            while (time >= 0){
                CustomMagicNode.Selector<Entity> selector = CustomMagicNode.Selector.get(Entity.class, tag.getString("SelectType" + --time));
                if(selector!=null) return selector.get(se,tag,time);
            }
            return new ArrayList<>();

        }

    };

    public static abstract class EntitySelector extends CustomMagicNode.Selector<Entity> {

        protected EntitySelector(String name) {
            super(Entity.class, name);
        }

        @Override
        public Predicate<Entity> predicate(Entity se, CompoundTag tag, int time) {
            return predicateEntity(se, predicateString(tag, time));
        }

        public static String predicateString(CompoundTag tag, int time){
            return tag.getString(ENTITY_SELECT_PREDICATE + time);
        }
    }


    @Nullable
    public static Predicate<Entity> predicateEntity(Entity se, String str) {
        switch (str) {
            case "All" -> {
                return null;
            }
            case "Teammate" -> {
                return (e) -> IM_Action.getAttitude(se, e) == IM_Action.Attitude.TEAMMATE;
            }
            case "Enemy" -> {
                return (e) -> IM_Action.getAttitude(se, e) == IM_Action.Attitude.ENEMY;
            }
            default -> {
                ImperfectMagic.LOGGER.info(String.valueOf(new IllegalStateException("Unexpected value + Predicate: " + str + "but this may not have an effect")));
                return null;
            }
        }
    }

    public static final EntityAction DAMAGE = new EntityAction("Damage") {
        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            return null;
        }

        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public void onServer(Entity se, Entity e, double amplifier) {
            e.hurt(IM_Action.damageSource(se,DamageTypes.MAGIC),(float) amplifier);
        }

        @Override
        public void onClient(Entity se, Entity e, double amplifier) {}
    };

    public static final EntityAction FIREBALL = new EntityAction("Fireball") {
        @Override
        public List<Entity> get(Entity se, CompoundTag tag, int time) {
            return null;
        }

        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public void onServer(Entity se, Entity e, double amplifier) {
            e.hurt(IM_Action.damageSource(se,DamageTypes.MAGIC),(float) amplifier);
        }

        @Override
        public void onClient(Entity se, Entity e, double amplifier) {}
    };

    /*public static int act(LivingEntity se, Entity e, CompoundTag tag, int time) {
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


    }*/

    public static abstract class EntityAction extends CustomMagicNode.Action<Entity> {
        protected EntityAction(String name) {
            super(Entity.class, name);
        }
    }











}
