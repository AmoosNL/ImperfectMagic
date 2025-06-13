package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.client.particle.LitParticle;
import com.amoos.imperfectmagic.client.particle.LitParticleOptions;
import com.amoos.imperfectmagic.client.particle.LitSgaOptions;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class IM_Particle {

    //public static DustColorTransitionOptions PURPLE = getDust(123, 104, 238, 230, 230, 250, 1);
    //public static DustColorTransitionOptions PURPLE = getDust(230, 230, 250, 123, 104, 238, 1);
    public static LitParticleOptions PURPLE = litForEyes(216, 191, 216, 123, 104, 238);

    public static LitParticleOptions BLUE = litForEyes(65,105,225, 25,25,112);

    public static LitParticleOptions WHITE = litForEyes( 200,200,200, 100, 100, 100);
    public static LitParticleOptions VOID = litForEyes(49,48,61 ,7,8,15);

    public static <T extends ParticleOptions> void addParticle(ServerLevel level, T p, Vec3 pos, Vec3 v, double speed) {
        //level.sendParticles(p, vec.x, vec.y, vec.z, 0, v.x, v.y, v.z, speed, true);
        for(ServerPlayer player:level.players()){
            level.sendParticles(player,p, true ,pos.x, pos.y, pos.z, 0, v.x, v.y, v.z, speed);
        }
    }

    public static <T extends ParticleOptions> void addParticle(ServerLevel level, T p, Vec3 pos) {
        addParticle(level,p,pos,Vec3.ZERO,0);
    }

    public static <T extends ParticleOptions> void addParticleList(ServerLevel level, T p, List<Vec3> pos, List<Vec3> v, double speed) {
        if (pos.size() != v.size()) {
            if (pos.size() == 1) {
                addParticleList(level, p, pos.get(0), v, speed);
                return;
            }
            if (v.size() == 1) {
                addParticleList(level, p, pos, v.get(0), speed);
                return;
            }
            addParticleList(level, p, pos);
            return;
        }
        for (int i = 0; i < pos.size(); i++) {
            Vec3 pos1 = pos.get(i);
            Vec3 v1 = v.get(i);
            addParticle(level, p, pos1, v1, speed);
        }
    }

    public static <T extends ParticleOptions> void addParticleList(ServerLevel level, T p, Vec3 pos, List<Vec3> v, double speed) {
        for (Vec3 v1 : v) {
            addParticle(level, p, pos, v1, speed);
        }
    }

    public static <T extends ParticleOptions> void addParticleList(ServerLevel level, T p, List<Vec3> pos, Vec3 v, double speed) {
        for (Vec3 pos1 : pos) {
            addParticle(level, p, pos1, v, speed);
        }
    }

    public static <T extends ParticleOptions> void addParticleList(ServerLevel level, T p, List<Vec3> pos) {
        for (Vec3 pos1 : pos) {
            addParticle(level, p, pos1);
        }
    }

    /*public static LitParticleOptions getLit(double r1, double g1, double b1, double r2, double g2, double b2, double a) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new LitParticleOptions(v1, v2, (float) a);
    }*/

    public static LitParticleOptions litForEyes(double r1, double g1, double b1, double r2, double g2, double b2) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new LitParticleOptions(v1, v2, 0.05F, 20)/* {
            @Override
            public void render(LitParticle p){
                if(p.getAge() == 15){
                    p.setColor(IM_Math.rgbMul(p.getFromColor(),1.5F));
                    //p.setSize(0.01F);
                }
                else  p.lerpColors();
            }
            @Override
            public void tick(LitParticle p) {
                if(!(p.getAge() == 15)){
                    p.setSize(this.size * Math.pow(0.93, p.getAge()));
                }//else p.setSize(0.06F);
                p.setParticleSpeed(p.getParticleSpeed().scale(0.9));
            }
        }*/;
    }
    public static LitParticleOptions litForEyes(double r1, double g1, double b1, double r2, double g2, double b2, int randLife) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new LitParticleOptions(v1, v2, 0.05F, 20, randLife);
    }

    public static LitSgaOptions litSga(double r1, double g1, double b1, double r2, double g2, double b2) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new LitSgaOptions(v1, v2, 0.2F, 20);
    }
    public static LitSgaOptions litSga(double r1, double g1, double b1, double r2, double g2, double b2, int randLife) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new LitSgaOptions(v1, v2, 0.2F, 20, randLife);
    }

    public static DustColorTransitionOptions getDust(double r1, double g1, double b1, double r2, double g2, double b2, double a) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        Vector3f v2 = IM_Math.rgb3f(r2, g2, b2);
        return new DustColorTransitionOptions(v1, v2, (float) a);
    }

    public static DustParticleOptions getDust(double r1, double g1, double b1, double a) {//255
        Vector3f v1 = IM_Math.rgb3f(r1, g1, b1);
        return new DustParticleOptions(v1, (float) a);
    }


}
