package com.amoos.imperfectmagic.helper;

public interface ILivingEntity {
    //default void directHealthSet(float amount) {}
    void directHealthSet(float amount);
    float directHealthGet();
    default void directHealthChange(float amount) {
        directHealthSet(directHealthGet() + amount);
    }

    void directAbsorptionSet(float amount);
    float directAbsorptionGet();
    default void directAbsorptionChange(float amount) {
        directAbsorptionSet(directAbsorptionGet() + amount);
    }

}
