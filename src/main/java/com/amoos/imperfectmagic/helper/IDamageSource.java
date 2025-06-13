package com.amoos.imperfectmagic.helper;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.HashMap;

public interface IDamageSource {
    HashMap<TagKey<DamageType>, Boolean> tags = new HashMap<>();
    default IDamageSource setTag(TagKey<DamageType> tag, boolean bool){
        tags.put(tag, bool);
        return this;
    }
    default DamageSource get(){
        return (DamageSource) this;
    }
}
