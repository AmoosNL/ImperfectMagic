package com.amoos.imperfectmagic.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LitSga extends TextureSheetParticle {

    public final LitSgaOptions op;
    private final Vector3f fromColor;
    private final Vector3f toColor;
    private final SpriteSet spriteSet;
    private final float colorComputeHelper;

    private final Vec3 initSpeed;

    //private final double sizeChange;

    protected LitSga(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, LitSgaOptions op, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.op = op;
        this.spriteSet = spriteSet;
        this.setSize(0f, 0f);
        this.gravity = 0f;
        this.hasPhysics = false;
        this.xd = vx * 1;
        this.yd = vy * 1;
        this.zd = vz * 1;
        this.pickSprite(spriteSet);
        this.fromColor = op.getFromColor();
        this.toColor = op.getToColor();
        this.initSpeed = this.getParticleSpeed();
        if (op.getScale() <= 0)
            this.quadSize = 0.05F;
        else this.quadSize = op.getScale();
        this.lifetime = op.lifetime + op.extraLife;
        this.colorComputeHelper = 1 / (this.lifetime + 1.0F);

    }

    public static LitSgaProvider provider(SpriteSet spriteSet) {
        return new LitSgaProvider(spriteSet);
    }

    public void setSize(double size) {
        this.quadSize = (float) size;
    }

    public float getQuadSize() {
        return this.quadSize;
    }

    public Vec3 getParticleSpeed() {
        return new Vec3(this.xd, this.yd, this.zd);
    }

    public void setParticleSpeed(Vec3 v) {
        this.xd = v.x;
        this.yd = v.y;
        this.zd = v.z;
    }

    public void setColor(float p_107254_, float p_107255_, float p_107256_) {
        this.rCol = p_107254_;
        this.gCol = p_107255_;
        this.bCol = p_107256_;
    }

    public void setColor(Vector3f vector3f) {
        this.rCol = vector3f.x();
        this.gCol = vector3f.y();
        this.bCol = vector3f.z();
    }

    public Vector3f getFromColor() {
        return this.fromColor;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }

    public int getAge() {
        return this.age;
    }

    /*public void lerpColors(float idk) {
        //float f = ((float) this.age + age) / ((float) this.lifetime + 1.0F);
        float m = this.age * colorComputeHelper;
        float f = m > 1 ? 1 : m;
        Vector3f vector3f = (new Vector3f(this.fromColor)).lerp(this.toColor, f);
        this.rCol = vector3f.x();
        this.gCol = vector3f.y();
        this.bCol = vector3f.z();
    }*/
    public void lerpColors() {
        float m = this.age * colorComputeHelper;
        float f = m > 1 ? 1 : m;
        Vector3f vector3f = (new Vector3f(this.fromColor)).lerp(this.toColor, f);
        this.rCol = vector3f.x();
        this.gCol = vector3f.y();
        this.bCol = vector3f.z();
    }

    public void render(VertexConsumer vc, Camera c, float idk) {
        this.lerpColors();
        super.render(vc, c, idk);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {

        super.tick();
        //this.op.tick(this);

        this.setSize(this.op.getScale() * (1 + this.op.sizeChange * this.age));
        if(this.age <= op.lifetime) {
            if (op.linear) this.setParticleSpeed(this.initSpeed.scale(1 + this.op.speedChange * this.age));
            else this.setParticleSpeed(this.initSpeed.scale(Math.abs(this.op.sizeChange)));
        }
        this.removed = this.age >= this.lifetime;
    }

    public static class LitSgaProvider implements ParticleProvider<LitSgaOptions> {
        private final SpriteSet spriteSet;

        public LitSgaProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(LitSgaOptions typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LitSga(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }

    }

}

