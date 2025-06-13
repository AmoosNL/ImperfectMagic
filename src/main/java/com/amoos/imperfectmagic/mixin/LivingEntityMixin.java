package com.amoos.imperfectmagic.mixin;

import com.amoos.imperfectmagic.helper.IDamageSource;
import com.amoos.imperfectmagic.helper.ILivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity {



    /*@Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void injected(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
        if(this.tags.containsKey(tag)){
            cir.setReturnValue(this.tags.get(tag));
        }
    }*/
    @Final
    @Shadow
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;
    @Shadow private float absorptionAmount;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void directHealthSet(float amount) {
        //((LivingEntity)(Object) this).
        this.entityData.set(DATA_HEALTH_ID,amount);
    }


    @Override
    public float directHealthGet(){
        return this.entityData.get(DATA_HEALTH_ID);
    }

    @Override
    public void directAbsorptionSet(float amount){
        this.absorptionAmount = amount;
    }
    @Override
    public float directAbsorptionGet(){
        return this.absorptionAmount;
    }



}
