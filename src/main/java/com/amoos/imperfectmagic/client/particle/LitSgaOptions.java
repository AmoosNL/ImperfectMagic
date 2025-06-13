package com.amoos.imperfectmagic.client.particle;

import com.amoos.imperfectmagic.init.IM_Init;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.Locale;

public class LitSgaOptions extends DustParticleOptionsBase {
    public static final Codec<LitSgaOptions> CODEC = RecordCodecBuilder.create(
            (p_253369_) -> p_253369_.group(ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter((op) -> op.color),
                    ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter((op) -> op.toColor),
                    Codec.FLOAT.fieldOf("scale").forGetter((op) -> op.scale),
                    Codec.INT.fieldOf("lifetime").forGetter((op) -> op.lifetime),
                    Codec.INT.fieldOf("extraLife").forGetter((op) -> op.extraLife),
                    Codec.DOUBLE.fieldOf("sizeChange").forGetter((op) -> op.sizeChange),
                    Codec.DOUBLE.fieldOf("speedChange").forGetter((op) -> op.speedChange),
                    Codec.BOOL.fieldOf("linear").forGetter((op) -> op.linear)).apply(p_253369_, LitSgaOptions::new));
    public static final Deserializer<LitSgaOptions> DESERIALIZER = new Deserializer<>() {
        public LitSgaOptions fromCommand(ParticleType<LitSgaOptions> p_175777_, StringReader reader) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(reader);
            reader.expect(' ');
            float a = reader.readFloat();
            Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(reader);
            reader.expect(' ');
            int lifetime = reader.readInt();
            reader.expect(' ');
            int extraLife = reader.readInt();
            reader.expect(' ');
            if(reader.readBoolean()){
                return new LitSgaOptions(vector3f, vector3f1, a, lifetime);
            }else {
                reader.expect(' ');
                double sizeChange = reader.readDouble();
                reader.expect(' ');
                double speedChange = reader.readDouble();
                reader.expect(' ');
                boolean linear = reader.readBoolean();
                return new LitSgaOptions(vector3f, vector3f1, a,  lifetime, extraLife , sizeChange, speedChange, linear);
            }


        }

        public LitSgaOptions fromNetwork(ParticleType<LitSgaOptions> p_175780_, FriendlyByteBuf packet) {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(packet);
            float a = packet.readFloat();
            Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(packet);
            double size = packet.readDouble();
            int lifetime = packet.readInt();
            int extraLife = packet.readInt();
            double sizeChange = packet.readDouble();
            double speedChange = packet.readDouble();
            boolean linear = packet.readBoolean();
            return new LitSgaOptions(vector3f, vector3f1, a,  lifetime, extraLife, sizeChange, speedChange, linear);

        }
    };

    public void writeToNetwork(FriendlyByteBuf packet) {
        super.writeToNetwork(packet);
        packet.writeFloat(this.toColor.x());
        packet.writeFloat(this.toColor.y());
        packet.writeFloat(this.toColor.z());
        packet.writeInt(this.lifetime);
        packet.writeInt(this.extraLife);
        packet.writeDouble(this.sizeChange);
        packet.writeDouble(this.speedChange);
        packet.writeBoolean(this.linear);
    }
    public final int lifetime;
    public final int extraLife;
    public final double sizeChange;
    public final double speedChange;

    public final boolean linear;
    private final Vector3f toColor;


    /*public LitSgaOptions(Vector3f fromColor, Vector3f toColor, float alpha) {
        super(fromColor, alpha);
        ImperfectMagic.LOGGER.info(fromColor.toString());
        this.toColor = toColor;
        this.size = -1;
        this.lifeTime = -1;

    }*/

    public LitSgaOptions(Vector3f fromColor, Vector3f toColor, float scale, int lifetime, int extraLife, double sizeChange, double speedChange, boolean linear) {
        super(fromColor, scale);
        this.toColor = toColor;
        this.lifetime = lifetime;
        this.extraLife = extraLife;
        this.sizeChange = sizeChange;
        this.speedChange = speedChange;
        this.linear = linear;

    }

    public LitSgaOptions(Vector3f fromColor, Vector3f toColor, float scale, int lifetime) {
        this(fromColor,toColor,scale,lifetime,0);
    }

    public LitSgaOptions(Vector3f fromColor, Vector3f toColor, float scale, int lifetime, int extraLife) {
       // this(fromColor,toColor,scale,lifetime,extraLife,-1.0D/ (lifetime + extraLife), -1.0D/ lifetime, true);
        this(fromColor,toColor,scale,lifetime,extraLife,0, -1.0D/ lifetime, true);
    }


    public Vector3f getFromColor() {
        return this.color;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }


    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
    }



    public ParticleType<LitSgaOptions> getType() {
        return IM_Init.LIT_SGA.get();
    }


}

