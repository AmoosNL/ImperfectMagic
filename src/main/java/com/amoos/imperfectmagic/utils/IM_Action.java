package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.helper.IDamageSource;
import com.amoos.imperfectmagic.helper.ILivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IM_Action {

    //public static final EntityDataAccessor<Float> DATA_HEALTH_ID = (EntityDataAccessor<Float>) ReflectionUtil.get(LivingEntity.class, ReflectionUtil.ENTITY_DATA_HEALTH_ID);

    public static DamageSource damageSource(Entity se, Entity ie, ResourceKey<DamageType> dt) {
        return new DamageSource(se.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(dt), ie, se);
    }

    public static DamageSource damageSource(Entity se, ResourceKey<DamageType> dt) {
        return new DamageSource(se.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(dt), se);
    }

    public static DamageSource damageSource(Level level, ResourceKey<DamageType> dt) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(dt));
    }

    public static boolean wind(LivingEntity se, double x, double y, double z, int lv) {
        Vec3 motion = se.getLookAngle().scale(Math.sqrt(lv) * (se.onGround() ? -1 : 1));
        Vec3 fromVec = se.getEyePosition().add(se.getLookAngle());
        if (se.level() instanceof ServerLevel sl) {
            IM_Math.repeat(sl, 5, () -> {
                for (Projectile target : IM_Math.getEntities(Projectile.class, sl, IM_Math.getCentre(se), x, y, z)) {
                    if (getAttitude(target.getOwner(), se) != Attitude.TEAMMATE) target.discard();
                }
                for (LivingEntity target : IM_Math.getEntitiesOnLine(LivingEntity.class,
                        sl, se.getEyePosition(), fromVec,
                        (target) -> {
                            if (shouldAttack(se, target)) {
                                target.hurt(((IDamageSource) damageSource(se, DamageTypes.FALL)).setTag(DamageTypeTags.BYPASSES_COOLDOWN, true).get(), lv);
                                return true;
                            }
                            return false;
                        }, true)) {
                    target.hurt(((IDamageSource) damageSource(se, DamageTypes.FALL)).setTag(DamageTypeTags.BYPASSES_COOLDOWN, true).get(), lv);
                }
                IM_Particle.addParticle(sl, ParticleTypes.SWEEP_ATTACK, IM_Math.getCentre(se));
                //se.addDeltaMovement(se.getLookAngle().scale(Math.sqrt(lv)));
                se.fallDistance = 0;
            });
        } else {
            IM_Math.repeat(se.level(), 5, () -> se.setDeltaMovement(motion));
            return false;
        }
        return true;
    }

    public static boolean purgeRange(LivingEntity se, double x, double y, double z, int lv) {
        boolean flag = false;
        if (!(se.level() instanceof ServerLevel sl)) return false;

        Vec3 pos = IM_Math.getLookPos(se);//.add(0, 2, 0);
        for (LivingEntity target : IM_Math.getEntities(LivingEntity.class, se.level(), pos, x, y, z)) {
            if (purge(se, target, lv)) flag = true;
        }
        /*IM_Math.Vec2List[] va = IM_Math.magicCircle2(5);
        IM_Math.Vec3List vl = va[0].toVec3(IM_Math.Plane.XZ);
        if (flag) IM_Particle.addParticleList(sl, IM_Particle.litForEyes(255, 255, 255, 252, 242, 112, 20),
                vl.addEach(pos),
                vl, 0.05);

        vl = va[1].toVec3(IM_Math.Plane.XZ);

        if (flag) IM_Particle.addParticleList(sl, IM_Particle.litSga(255, 255, 255, 252, 242, 112, 10),
                vl.addEach(pos),
                vl, 0.05);*/


        /*IM_Math.Vec3List vl = IM_Math.ring(5).toVec3(IM_Math.Plane.XZ).scaleEach(0.2);
        if (flag) IM_Particle.addParticleList(sl, IM_Particle.litForEyes(255, 255, 255, 252, 242, 112, 1),
                vl.addEach(vec),
                vl, 0.5);*/

        return flag;
    }

    public static boolean purge(LivingEntity se, LivingEntity e, int lv) {
        if (e == null) return false;
        if (!(e.level() instanceof ServerLevel sl)) return false;
        int i = 0;
        if (getAttitude(se, e) == Attitude.TEAMMATE)
            for (MobEffectInstance effect : new ArrayList<>(e.getActiveEffects())) {
                if (!(effect.getEffect().isBeneficial()) && i < lv) {
                    e.removeEffect(effect.getEffect());
                    ++i;
                }
            }
        e.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, lv - 1), se);
        int a = 2 * (lv - 1) - 1;
        if (a > 0) e.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, a), se);
        return true;
    }

    public static boolean volcanoRange(LivingEntity se, double x, double y, double z, double lv) {
        boolean flag = false;
        Vec3 pos;
        pos = IM_Math.getLookPos(se);
        for (LivingEntity target : IM_Math.getEntities(LivingEntity.class, se.level(), pos, x, y, z)) {
            if (volcano(se, target, lv)) flag = true;
        }
        return flag;
    }

    public static boolean volcano(LivingEntity se, @Nullable LivingEntity e, double lv) {
        if (!(e.level() instanceof ServerLevel sl)) return false;
        for (int i = 0; i < 360; i+=36) {
            double ra = Math.toRadians(i);
            sl.sendParticles(ParticleTypes.LAVA,
                    Math.cos(ra)+e.getX(),e.getY()+0.5,Math.sin(ra)+e.getZ(),0,0,1,0,1);
        }










        if (!shouldAttack(se, e)) return false;
        if (!(e.level() instanceof ServerLevel sl)) return false;
        IM_Math.step = 4;
        IM_Particle.addParticleList(sl, ParticleTypes.LAVA,
                IM_Math.ring(1).toVec3(IM_Math.Plane.XZ).addEach(e.position()),
                new Vec3(0, 1, 0), 1);
        IM_Math.resetStep();
        DamageSource ds = damageSource(se, DamageTypes.LAVA);
        float a = 30.0F * (float) lv;
        if (!e.isInvulnerableTo(ds)) e.hurt(ds, a);
        else e.hurt(damageSource(se, DamageTypes.MOB_ATTACK), a);
        e.setSecondsOnFire(5);
        return true;


    }

    public static boolean iceRange(LivingEntity e, double x, double y, double z, int lv) {
        boolean flag = false;
        for (LivingEntity target : IM_Math.getEntities(LivingEntity.class, e.level(), IM_Math.getCentre(e), x, y, z)) {
            if (ice(e, target, lv)) flag = true;
        }
        return flag;
    }

    public static boolean ice(LivingEntity se, @Nullable LivingEntity e, int lv) {
        if (!shouldAttack(se, e)) return false;
        if (!(e.level() instanceof ServerLevel sl)) return false;
        e.setSharedFlagOnFire(false);
        e.setTicksFrozen(140 + 40 * lv);
        e.setIsInPowderSnow(true);
        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * lv, lv - 1));
        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * lv, lv - 1));
        IM_Particle.addParticleList(sl, ParticleTypes.SNOWFLAKE, IM_Math.getCentre(e), IM_Math.ring(1).toVec3(IM_Math.Plane.XZ).addEach(new Vec3(0, 0.5, 0)), 0.2);
        return true;
    }

    public static boolean sonic(LivingEntity se, int lv) {
        LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se, true);
        if (!(shouldAttack(se, target))) return false;


        if (target.level() instanceof ServerLevel l) {
            target.hurt(damageSource(se, DamageTypes.SONIC_BOOM), 4.0F * lv);
            IM_Particle.addParticleList(l, ParticleTypes.SONIC_BOOM,
                    IM_Math.lineVec(IM_Math.getCentre(se), IM_Math.getCentre(target)),
                    Vec3.ZERO, 0);
            return true;
        }


        return false;
    }

    public static boolean curse(LivingEntity se, int lv) {
        if (se == null) return false;
        //LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se);
        //if (!(shouldAttack(se, target))) return false;


        if (se.level() instanceof ServerLevel l) {
            FakeEntity fe = new FakeEntity(l) {
                final List<LivingEntity> touched = new ArrayList<>();

                private int ticks = 20;

                @Override
                public void onEachMove(int time, int times) {
                    /*if(Math.random() < 0.1) */
                    IM_Particle.addParticle(l, ParticleTypes.SCULK_SOUL, this.getPos());
                }

                @Override
                public void tick() {
                    super.tick();
                    if (--this.ticks < 0) this.discard();
                }

                @Override
                public void touch(LivingEntity target) {
                    super.touch(target);
                    if (shouldAttack(se, target) && !touched.contains(target)) {
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * lv, lv - 1), se);
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * lv, lv - 1), se);
                        if (target.getMobType() == MobType.UNDEAD)
                            target.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, lv - 1), se);
                        else target.addEffect(new MobEffectInstance(MobEffects.HARM, 1, lv - 1), se);
                        touched.add(target);

                    }
                }

                @Override
                protected LivingEntity getHitEntity() {
                    return IM_Math.getEntityOnVec(LivingEntity.class, se, level, this.getPos(), deltaMovement, null, true);
                }
            };
            fe.setPos(se.getEyePosition());
            fe.setDeltaMovement(se.getLookAngle().scale(1));
        }
        return true;
    }

    /*public static boolean curse(LivingEntity se, int lv) {
        if (se == null) return false;
        LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se);
        if (!(shouldAttack(se, target))) return false;

        if (target.level() instanceof ServerLevel l) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * lv, lv - 1), se);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * lv, lv - 1), se);
            if (target.getMobType() == MobType.UNDEAD)
                target.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, lv - 1), se);
            else target.addEffect(new MobEffectInstance(MobEffects.HARM, 1, lv - 1), se);
            IM_Particle.addParticleList(l, ParticleTypes.SCULK_SOUL,
                    IM_Math.lineVec(IM_Math.getCentre(se), IM_Math.getCentre(target)),
                    Vec3.ZERO, 0);
        }
        return true;
    }*/
    public static boolean lightningLink(LivingEntity se, Entity direct,@Nullable LivingEntity target, int lv) {
        if (!(shouldAttack(se, target))) return false;
        if (target.level() instanceof ServerLevel sl) {
            target.hurt(((IDamageSource) damageSource(se, direct, DamageTypes.LIGHTNING_BOLT)).setTag(DamageTypeTags.BYPASSES_COOLDOWN, true).get(), 35 * lv);
            IM_Math.step = 0.05;
            IM_Particle.addParticleList(sl, IM_Particle.PURPLE, IM_Math.lineVecLightning(IM_Math.getCentre(direct), IM_Math.getCentre(target)), Vec3.ZERO, 0);
            IM_Math.resetStep();
        }
        return false;


    }

    public static boolean lightningLinkFirst(LivingEntity se, int lv) {
        LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se, false);
        if (!(shouldAttack(se, target))) return false;

        if (target.level() instanceof ServerLevel sl) {
            List<LivingEntity> history = new ArrayList<>();
            history.add(se);
            history.add(target);
            lightningLink(se,se,target,lv);
            IM_Math.repeat(sl, lv * 5, () -> {
                LivingEntity direct = history.get(history.size() - 1);
                LivingEntity target1 = IM_Math.getEntities(LivingEntity.class, sl, IM_Math.getCentre(direct), 10, 5, 10, (e1)->e1 != null && !history.contains(e1), 0);
                if (target1 != null) {
                    history.add(target1);
                    lightningLink(se, direct, target1, lv);
                }

            });
            return true;
        }
        return false;
        /*LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se, false);
        if (!(shouldAttack(se, target))) return false;

        if (target.level() instanceof ServerLevel sl) {
            List<LivingEntity> history = new ArrayList<>();
            history.add(se);
            history.add(target);

            lightningLink(se,se,target,lv);

            IM_Math.repeat(sl, lv * 5, () -> {
                LivingEntity direct = history.get(history.size() - 1);
                LivingEntity target1 = null;
                List<LivingEntity> lst = IM_Math.getEntities(LivingEntity.class, sl, IM_Math.getCentre(direct), 10, 5, 10);
                for (LivingEntity ei : lst) {
                    if (ei != null && !history.contains(ei)) {
                        target1 = ei;
                        break;
                    }

                }

                if (target1 != null) {
                    history.add(target1);
                    target1.hurt(((IDamageSource) damageSource(se, direct, DamageTypes.LIGHTNING_BOLT)).setTag(DamageTypeTags.BYPASSES_COOLDOWN, true), 35 * lv);

                    IM_Math.step = 0.05;
                    IM_Particle.addParticleList(sl, IM_Particle.PURPLE, IM_Math.lineVecLightning(IM_Math.getCentre(direct), IM_Math.getCentre(target1)), Vec3.ZERO, 0);
                    IM_Math.resetStep();
                    lightningLink(se, direct, target1, lv);
                }

            });
            return true;
        }
        return false;*/
        /*LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se);
        if (!(shouldAttack(se, target))) return false;

        if (target.level() instanceof ServerLevel l) {
            float amount = (target.getAbsorptionAmount() * 0.1F + target.getMaxHealth() * 0.025F + target.getHealth() * 0.075F + 40) * lv;
            Vec3 dir = IM_Math.getCentre(target).subtract(IM_Math.getCentre(se)).normalize();
            Vec3 vec = IM_Math.getCentre(target).add(dir);


            IM_Math.InTick(l, 20, () -> {
                IM_Math.Plane plane = IM_Math.planeVertical(dir);
                IM_Math.Vec3List v = IM_Math.crossEye(12).toVec3(plane);
                target.hurt(damageSource(se, DamageTypes.LIGHTNING_BOLT), amount);
                realDamage(target, se, amount);
                IM_Particle.addParticleList(l, IM_Particle.PURPLE, v.addEach(vec), v, 0.02);
            });

            visualLightning(l, target.position(), 3);
            return true;
        }
        return false;*/

    }

    public static boolean voidEye(LivingEntity se, int lv) {
        if (se == null) return false;
        LivingEntity target = IM_Math.getEntityWatched(LivingEntity.class, se, true);
        if (!(shouldAttack(se, target))) return false;
        if (target.level() instanceof ServerLevel sl) {
            float amount = (realAbsorption(target) * 0.1F + target.getMaxHealth() * 0.025F + realHealth(target) * 0.075F + 40) * lv;
            Vec3 targetPos = target.position();
            IM_Math.repeatAndDelay(sl, 20, () -> target.setPos(targetPos), () -> {
                Vec3 dir = IM_Math.getCentre(target).subtract(IM_Math.getCentre(se)).normalize();
                Vec3 pos = IM_Math.getCentre(target).add(dir);
                IM_Math.Plane plane = IM_Math.planeVertical(dir);
                IM_Math.Vec3List V = IM_Math.crossEye(12).toVec3(plane);

                IM_Particle.addParticleList(sl, IM_Particle.VOID, V.addEach(pos), V, 0.05);

                realDamage(target, se, amount);
            });

            IM_Math.Vec3List v = IM_Math.flowerCircle(10).scaleEach(4).toVec3(IM_Math.Plane.XZ);
            List<Vec3> p = v.addEach(IM_Math.getCentre(target));
            v = v.scaleEach(-1);
            IM_Particle.addParticleList(sl, IM_Particle.VOID, p, v, 0.1);

            return true;
        }/*else{
            IM_Math.repeat(se.level(),20,()-> {
                target.setDeltaMovement(Vec3.ZERO);
            });
        }*/
        return false;
    }


    public static void visualLightning(ServerLevel level, Vec3 pos, double num) {
        for (int i = 0; i < num; i++) {
            LightningBolt l = EntityType.LIGHTNING_BOLT.create(level);
            if (l != null) {
                l.setPos(pos);
                l.setVisualOnly(true);
                level.addFreshEntity(l);
            }
        }
    }


    public static AbstractHurtingProjectile fireball(LivingEntity se, double speed, int lv) {
        if (se.level() instanceof ServerLevel) {
            Vec3 v = se.getLookAngle();
            LargeFireball fb = new LargeFireball(se.level(), se, v.x, v.y, v.z, lv) {
                @Override
                protected void onHitEntity(EntityHitResult result) {
                    if (!this.level().isClientSide) {
                        Entity entity = result.getEntity();
                        if (!shouldAttack(entity, this.getOwner())) {
                            this.discard();
                            return;
                        }
                        if (entity instanceof Ghast) this.setOwner(null);
                    }

                    super.onHitEntity(result);
                }
            };
            fb.setOwner(se);
            fb.setPos(se.getEyePosition());
            fb.shoot(v.x, v.y, v.z, (float) speed, 0);
            //fb.setOwner(null);
            se.level().addFreshEntity(fb);
            //DelayUtil.killEntity(fb, 40);

            return fb;
        }
        return null;
    }


    public static boolean shouldAttack(Entity e1, Entity e2) {
        return getAttitude(e1, e2).allowDamage;
    }

    public static Attitude getAttitude(Entity e1, Entity e2) {
        if (!(e1 instanceof LivingEntity) || !(e2 instanceof LivingEntity) || !e1.isAlive() || !e1.isAlive())
            return Attitude.NULL;
        if (e1 == e2) return Attitude.TEAMMATE;
        if (e1.getTeam() != e2.getTeam() || e1.getTeam() == null) {
            return Attitude.ENEMY;
        }
        if (!e1.getTeam().isAllowFriendlyFire()) return Attitude.TEAMMATE;
        return Attitude.ENEMY;
    }

    public static void realHealthSet(LivingEntity le, float amount) {
        //le.getEntityData().set(DATA_HEALTH_ID, amount);
        ILivingEntity l = (ILivingEntity) le;
        l.directHealthSet(amount);
    }

    public static void realHealthChange(LivingEntity le, float amount) {
        //realHealthSet(le, realHealth(le) + amount);
        ILivingEntity l = (ILivingEntity) le;
        l.directHealthChange(amount);
    }

    public static float realHealth(LivingEntity le) {
        //return le.getEntityData().get(DATA_HEALTH_ID);
        ILivingEntity l = (ILivingEntity) le;
        return l.directHealthGet();
    }

    public static void realAbsorptionSet(LivingEntity le, float amount) {
        //ReflectionUtil.set(le, ReflectionUtil.ENTITY_ABSORPTION, amount);
        ILivingEntity l = (ILivingEntity) le;
        l.directAbsorptionSet(amount);
    }

    public static void realAbsorptionChange(LivingEntity le, float amount) {
        ///realAbsorptionSet(le, realHealth(le) + amount);
        ILivingEntity l = (ILivingEntity) le;
        l.directAbsorptionChange(amount);
    }

    public static float realAbsorption(LivingEntity le) {
        /*Object toReturn = ReflectionUtil.get(le, ReflectionUtil.ENTITY_ABSORPTION);
        if (toReturn != null) return (float) toReturn;
        return 0;*/
        ILivingEntity l = (ILivingEntity) le;
        return l.directAbsorptionGet();
    }


    public static void realDamage(Entity e, LivingEntity se, double amount) {
        float a = (float) amount;
        e.invulnerableTime = 0;
        DamageSource ds = ((IDamageSource) damageSource(se, DamageTypes.FELL_OUT_OF_WORLD)).setTag(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL, false).get();//damageSource(se, DamageTypes.GENERIC_KILL);
        if (e instanceof LivingEntity le) {
            le.setLastHurtByMob(se);
            float n = realAbsorption(le);//le.getAbsorptionAmount();

            if (n > 0) {
                if (n > a) {
                    realAbsorptionSet(le, n - a);
                    le.hurt(ds, Float.MIN_NORMAL);
                    a = 0;
                } else {
                    realAbsorptionSet(le, 0);
                    a -= n;
                }
            }

            if (a > 0) {
                if (a < realHealth(le)) {
                    realHealthChange(le, -a);
                    le.hurt(ds, Float.MIN_NORMAL);
                } else {
                    realHealthSet(le, 1);
                    le.hurt(ds, Float.MAX_VALUE);
                    if (!(le.isDeadOrDying())) realHealthSet(le, 0);
                    /*le.getPersistentData().putFloat("Health",0);
                    le.load(le.getPersistentData());*/
                }
            }
        } else e.discard();
    }

    /*public static class DamageSource extends DamageSource {

        private final HashMap<TagKey<DamageType>, Boolean> tags = new HashMap<>();
        public DamageSource(Holder<DamageType> type, @Nullable Entity source, @Nullable Entity direct, @Nullable Vec3 vec) {
            super(type,source, direct, vec);
        }

        public DamageSource(Holder<DamageType> p_270818_, @Nullable Entity p_270162_, @Nullable Entity p_270115_) {
            this(p_270818_, p_270162_, p_270115_, null);
        }

        public DamageSource(Holder<DamageType> p_270690_, Vec3 p_270579_) {
            this(p_270690_, null, null, p_270579_);
        }

        public DamageSource(Holder<DamageType> p_270811_, @Nullable Entity p_270660_) {
            this(p_270811_, p_270660_, p_270660_);
        }

        public DamageSource(Holder<DamageType> p_270475_) {
            this(p_270475_, null, null, null);
        }

        public DamageSource setTag(TagKey<DamageType> tag, boolean bool){
            tags.put(tag, bool);
            return this;
        }

        @Override
        public boolean is(TagKey<DamageType> tag){
            if(tags.containsKey(tag)) return tags.get(tag);
            else return super.is(tag);
        }

    }*/

    public enum Attitude {
        ENEMY(true, false),
        NULL(false, true),
        TEAMMATE(false, false);

        Attitude(boolean allowDamage, boolean isNull) {


            this.allowDamage = allowDamage;
            this.isNull = isNull;
        }
        public final boolean allowDamage;
        public final boolean isNull;

    }
}

