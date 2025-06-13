package com.amoos.imperfectmagic.mixin;

import com.amoos.imperfectmagic.helper.IDamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IDamageSource {


    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void injected(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
        if(this.tags.containsKey(tag)){
            cir.setReturnValue(this.tags.get(tag));
        }
    }

}
