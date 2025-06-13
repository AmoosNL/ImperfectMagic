package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.ImperfectMagic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;


public class FakeEntity {

    public final IEventBus bus;
    public final ServerLevel level;
    private final AABB bb = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private final Boolean physics;
    public Vec3 deltaMovement;
    private Vec3 pos = Vec3.ZERO;
    public final BlockPos blockPosition = BlockPos.containing(this.pos);


    /***
     * @param bus 事件总线
     * @param level 所在的世界
     */
    public FakeEntity(IEventBus bus, ServerLevel level) {
        this.level = level;
        this.bus = bus;
        this.deltaMovement = Vec3.ZERO;
        this.physics = false;

        bus.register(this);
    }

    public FakeEntity(ServerLevel level) {
        this(MinecraftForge.EVENT_BUS, level);
    }

    public void discard() {
        bus.unregister(this);
    }


    @SubscribeEvent
    public void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && level == event.level) {
            this.tick();
        }
    }

    public void tick() {
        /*double motionLength = this.getDeltaMovement().length();
        int times = (int) Math.ceil(motionLength * 10);
        int time = 0;
        while (motionLength > 0.1) {
            this.changePos(this.getDeltaMovement().normalize().scale(0.1));
            if (this.physics && !(this.level.getBlockState(this.blockPosition).getBlock() instanceof AirBlock)) {
                this.deltaMovement = Vec3.ZERO;
                break;
            }
            this.onEachMove(++time, times);
            motionLength -= 0.1;
            List<LivingEntity> lst = IM_Math.getEntities(LivingEntity.class, this.level, this.vec, this.getPosBB());
            if(lst.size() > 0) this.touch(lst.get(0));
        }
        this.onEachMove(++time, times);
        this.changePos(this.getDeltaMovement().normalize().scale(motionLength));*/
        this.onEachMove(0,0);
        this.touch(this.getHitEntity());
        this.changePos(this.getDeltaMovement());
        /*LivingEntity target = this.getHitEntity();
        if(target!=null) this.touch(target);

        this.vec = this.vec.add(this.deltaMovement);*/

    }

    public void onEachMove(int time, int times) {
    }

    public void touch(@Nullable LivingEntity e) {}

    public void changePos(Vec3 v) {
        this.pos = this.pos.add(v);
    }

    public Vec3 getPos() {
        return this.pos;
    }

    public void setPos(Vec3 v) {
        this.pos = v;
    }

    public Vec3 getDeltaMovement() {
        return deltaMovement;
    }

    public void setDeltaMovement(Vec3 deltaMovement) {
        this.deltaMovement = deltaMovement;
    }

    public AABB getBB() {
        return bb;
    }

    public AABB inflateBB() {
        return this.bb.inflate(1.0D, 0.5D, 1.0D);
    }

    public AABB getPosBB() {
        return new AABB(this.bb.minX + this.pos.x,
                this.bb.minY + this.pos.y,
                this.bb.minZ + this.pos.z,
                this.bb.maxX + this.pos.x,
                this.bb.maxY + this.pos.y,
                this.bb.maxZ + this.pos.z);
    }

    @Nullable
    protected LivingEntity getHitEntity() {
        //return IM_Math.getEntitiesOnLine(level, vec, vec.add(deltaMovement));
        return null;//IM_Math.getEntityOnVec(LivingEntity.class, null, level, vec, deltaMovement, null);
    }


}
