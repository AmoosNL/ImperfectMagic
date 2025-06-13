package com.amoos.imperfectmagic.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class IM_Math {
    public static final RandomSource RANDOM = RandomSource.create();

    public static final Formatter FORMATTER = new Formatter();
    private static final int STEP_LENGTH = 1;
    public static double step = STEP_LENGTH;




    public static double resetStep() {
        step = STEP_LENGTH;
        return step;
    }

    public static double getAllHealth(LivingEntity e) {
        return e.getAbsorptionAmount() + e.getHealth();
    }

    public static double getAllMaxHealth(LivingEntity e) {
        return e.getAbsorptionAmount() + e.getMaxHealth();
    }

    //public static log()




    public static String toPercent(double num) {
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(0);
        return fmt.format(num);
    }

    public static String toPercent(float num) {
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(0);
        return fmt.format(num);
    }

    public static double format2(double num) {
        return Math.round(num * 100) * 0.01;
    }

    public static Vec3 format2Vec(Vec3 v) {
        return new Vec3(format2(v.x), format2(v.y), format2(v.z));
    }

    public static Vec3 intVec(Vec3 v) {
        return new Vec3(Math.round(v.x), Math.round(v.y), Math.round(v.z));
    }

    public static <T> List<T> shortenList(List<T> l, int x) {//x should >= 2
        if (x < 2) return l;
        return IntStream.iterate(0, i -> i < l.size(), i -> i + x).mapToObj(l::get).collect(Collectors.toList());
    }


    public static double getRand(double m1, double m2) {
        return Math.random() * (m2 - m1) + m1;
    }

    public static Vec3 getCentre(Entity e) {
        return new Vec3(e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ());
    }

    public static Plane planeVertical(Vec3 v) {
        Vec3 x = vecVertical(v);
        return new Plane(x, vecUP(v.cross(x)));
    }

    public static Vec3 vecVertical(Vec3 v) {
        return new Vec3(-v.z, 0, v.x).normalize();
    }

    public static Vec3 vecUP(Vec3 v) {
        return v.y >= 0 ? v : v.scale(-1);
    }



    /*
    public static Plane planeVertical(Vec3 v) {
        Vec3 x = new Vec3(-v.z, 0, v.x);
        Vec3 v1 = new Vec3(-v.y, v.x, 0);
        Vec3 v2 = new Vec3(0, -v.z, v.y);
        if(x.normalize().equals(Vec3.ZERO)) return Plane.XZ;
        if (v1.equals(Vec3.ZERO)) return new Plane(x, v2);
        else return new Plane(x, v1);
    }
     */


    public static Plane planeParallel(Vec3 v) {
        return new Plane(vecVertical(v), v.normalize());
    }

    public static double getAngleCos(Vec3 a, Vec3 b) {
        return a.dot(b) / (a.length() * b.length());
    }

    public static Vec2List nAngle(double n, double r, double degree) {
        Vec2List l = Vec2List.get();
        double fromDegree = (n%2==0?0:90) + degree;
        double per = 360.0D/n;
        for(int i = 0; i<n; i++){
            Vec2d pos = Vec2d.byDegree(fromDegree,r);
            fromDegree +=per;
            l.add(pos);
        }
        return lineVec2(l);
    }

    public static Vec2List ring(double r) {
        Vec2List l = Vec2List.get();
        double _step = 360.0D/Math.round((90 / step * Math.max(0.5,r)));
        for (double i = 0; i < 360; i += _step) {
            l.add(Vec2d.byDegree(i,r));
        }
        return l;
    }

    public static Vec2List ringNi(double r) {
        Vec2List l = Vec2List.get();
        double _step = 360.0D/Math.round((90 / step * Math.max(0.5,r)));
        for (double i = 0; i < 2160; i += _step) {
            l.add(Vec2d.byDegree(i,r).add(step * i * 0.01,0));
        }
        return l;
    }


    public static Vec2List flowerCircle(double r) {
        Vec2List l = Vec2List.get();
        double _step = 360.0D/Math.round((90 / step * Math.max(0.5,r)));
        for (double t = 0; t < 720; t += _step) {
            double x = (0.15 * Math.sin(-0.04 * t + 0) + -0.02 * Math.sin(0.07 * t + 0)) * r;
            double y = (0.15 * Math.cos(-0.04 * t + 0) + -0.02 * Math.cos(0.07 * t + 0)) * r;
            l.add(new Vec2d(x,y));
        }
        return l;
    }

    public static Vec2List eye(double xm, double ym) {
        Vec2List l = Vec2List.get();
        double r = Math.abs(Math.max(xm,ym));
        double _step = 360.0D/Math.round((54 / step * Math.max(0.5,r)));//Math.round(5*step/Math.max(1,Math.max(xm,ym)));

        for (double t = 0; t < 360; t += _step) {
            double x = (Math.pow(cos(t), 3)) * xm;
            double y = (Math.pow(sin(t), 3)) * ym;
            l.add(new Vec2d(x,y));
        }
        return l;
    }



    public static Vec2List superEye(double xm, double ym) {//don't make ym>xm it will be strange
        Vec2List l = Vec2List.get();
        l.addAll(eye(xm, ym));
        l.addAll(ring(Math.min(xm, ym) * 0.75));
        l.addAll(eye(ym * ym * 0.75 / xm, ym * 0.75));
        return l;
    }


    public static Vec2List crossEye(double r) {
        Vec2List l = Vec2List.get();
        for (double i = r; i > 3; i -= step * 4) {
            l.addAll(superEye(i, 0.3 * i));
        }
        return l;
    }



    public static Vec2List magicCircle(double r){
        Vec2List l = Vec2List.get();
        l.addAll(ring(r*1.1));
        l.addAll(ring(r*0.9));
        l.addAll(ring(r*0.5));
        l.addAll(ring(r*0.4));
        Vec2List smallRing = ring(r*0.2);
        smallRing.addAll(ring(r*0.3));
        l.addAll(smallRing.addEach(Vec2d.byDegree(90, r)));
        l.addAll(smallRing.addEach(Vec2d.byDegree(210, r)));
        l.addAll(smallRing.addEach(Vec2d.byDegree(330, r)));
        step *= 0.1;
        l.addAll(nAngle(3, r, 0));
        l.addAll(nAngle(3, r*0.7, 0));
        l.addAll(nAngle(3, r*1.2, 180));
        resetStep();
        return l;
    }

    public static Vec2List[] magicCircle2(double r){
        IM_Math.step *= 10;
        Vec2List list = ring(r);
        IM_Math.resetStep();
        list.addAll(nAngle(3,r*0.85, 0));
        return new Vec2List[]{magicCircle(r),list};
    }






    public static Vec3List lineVec(Vec3 v1, Vec3 v2) {
        Vec3List l = Vec3List.get();
        double d = v1.distanceTo(v2);
        int times = (int) Math.round(d/step);
        double per = 1.0D / times;
        for (double i = 1; i < times; i++) {
            l.add(v1.lerp(v2, i * per));
        }
        /*Vec3 dv = v1.vectorTo(v2).normalize();
        double d = v1.distanceTo(v2);
        for (double i = step; i < d; i+=step) {
            l.add(v1.add(dv.scale(i)));
        }*/
        return l;
    }

    public static Vec3List lineVec(Vec3... vec3s){
        return lineVec(new Vec3List(vec3s));
    }

    public static Vec3List lineVec(Collection<Vec3> vec3s){
        Vec3List vec3List = new Vec3List(vec3s);
        Vec3List toReturn = new Vec3List(vec3s);
        for (int i = 0; i < vec3List.size(); i++){
            toReturn.addAll(lineVec(vec3List.get(i),vec3List.get(i + 1)));
        }
        return toReturn;
    }
    public static Vec2List lineVec2(Vec2d v1, Vec2d v2) {
        /*Vec2d dv = v1.vectorTo(v2).normalize();
        double d = v1.distanceTo(v2);
        Vec2List l = Vec2List.get();
        for (double i = step; i < d; i+=step) {
            l.add(v1.add(dv.scale(i)));
        }
        return l;*/

        Vec2List l = Vec2List.get();
        double d = v1.distanceTo(v2);
        int times = (int) Math.round(d/step);
        double per = 1.0D / times;
        for (double i = 1; i < times; i++) {
            l.add(v1.lerp(v2, i * per));
        }

        return l;
    }

    public static Vec2List lineVec2(Vec2d... vec2DS){
        return lineVec2(new Vec2List(vec2DS));
    }

    public static Vec2List lineVec2(Collection<Vec2d> vec2s){
        Vec2List vec2List = new Vec2List(vec2s);
        Vec2List toReturn = new Vec2List(vec2s);
        for (int i = 0; i < vec2List.size(); i++){
            toReturn.addAll(lineVec2(vec2List.get(i),vec2List.get(i + 1)));
        }
        return toReturn;
    }


    public static Vec3List lineVecLightning(Vec3 v1, Vec3 v2) {
        int times = (int) v1.distanceTo(v2);
        double calculate = (double) 1/times;
        Vec3List l = Vec3List.get();
        Vec3List l1 = Vec3List.get();
        for (int i = 0; i <= times; i++) {
            if(i==0||i==times) l.add(v1.lerp(v2, calculate * i));
            else l.add(v1.lerp(v2, calculate * i).add(randomVec(0.4,0.4,0.4)));
        }
        for(int i = 0; i < times; i++){
            l1.addAll(lineVec(l.get(i),l.get(i+1)));
        }
        return l1;
    }



    public static Vec3 randomVec(double x, double y, double z) {
        return new Vec3(getRand(-x, x), getRand(-y, y), getRand(-z, z));
    }

    public static AABB getAABB(Vec3 centrePos, double r) {
        return new AABB(centrePos, centrePos).inflate(r);
    }

    public static AABB getAABB(Vec3 centrePos, double xr, double yr, double zr) {
        return new AABB(centrePos, centrePos).inflate(xr, yr, zr);
    }

    public static <T extends Entity> T getEntities(Class<T> tClass, Level l, Vec3 centrePos, double xr, double yr, double zr, @Nullable Predicate<? super T> predicate, int index) {
        List<T> lst = getEntities(tClass, l, centrePos, getAABB(centrePos, xr, yr, zr),predicate);
        if (lst.size() > index) return lst.get(index);
        return null;
    }

    public static <T extends Entity> List<T> getEntities(Class<T> tClass, Level l, Vec3 centrePos, double xr, double yr, double zr) {
        return getEntities(tClass, l, centrePos, getAABB(centrePos, xr, yr, zr));
    }

    public static <T extends Entity> List<T> getEntities(Class<T> tClass, Level l, Vec3 centrePos, double xr, double yr, double zr,  @Nullable Predicate<? super T> predicate) {
        return getEntities(tClass, l, centrePos, getAABB(centrePos, xr, yr, zr), predicate);
    }

    public static <T extends Entity> List<T> getEntities(Class<T> tClass, Level l, Vec3 centrePos, AABB ab) {
        return getEntities(tClass, l, centrePos, ab, null);
    }

    public static <T extends Entity> List<T> getEntities(Class<T> tClass, Level l, Vec3 centrePos, AABB ab, @Nullable Predicate<? super T> predicate) {
        List<T> lst;
        lst = l.getEntitiesOfClass(tClass, ab, predicate !=null ? predicate : Entity::isAlive/*Entity::canBeCollidedWith*/);
        return lst.stream().sorted((e1, e2) -> (int) (getCentre(e1).distanceToSqr(centrePos) - getCentre(e2).distanceToSqr(centrePos))).toList();
    }

    public static List<Entity> getEntities(@Nullable Entity ignored, Level l, AABB ab, @Nullable Predicate<? super Entity> predicate) {
        return l.getEntities(ignored, ab, predicate !=null?predicate:Entity::isAlive/*Entity::canBeCollidedWith*/);
    }

    @Nullable
    public static <T extends Entity> T getEntityWatched(Class<T> tClass, Entity e, boolean clipByBlock) {
        return getEntityWatched(tClass, e, null,0xff, clipByBlock);
    }

    @Nullable
    public static <T extends Entity> T getEntityWatched(Class<T> tClass, Entity e, @Nullable Predicate<? super T> predicate, double distance, boolean clipByBlock) {
        T e1 = null;
        List<T> lst = getEntitiesOnLine(tClass, e.level(),e.getEyePosition(),e.getEyePosition().add(e.getLookAngle().scale(distance)), predicate, clipByBlock);

        for(T ei:lst){
            if(e!=ei) e1 = ei;
        }
        return e1;
    }

    @Nullable
    public static <T extends Entity> T getEntityOnVec(Class<T> tClass, @Nullable Entity ignored ,Level l, Vec3 start, Vec3 dir, @Nullable Predicate<? super T> predicate, boolean clipByBlock) {
        T e1 = null;
        List<T> lst = getEntitiesOnLine(tClass, l, start, start.add(dir), predicate, clipByBlock);
        for(T ei:lst){
            if(ignored!=ei) e1 = ei;
        }

        return e1;
    }

    public static Vec3 getLookPos(Entity e) {
        /*LivingEntity t = IM_Math.getEntityWatched(LivingEntity.class, e, true);
        if (t != null) return IM_Math.getCentre(t);
        return e.level().clip(new ClipContext(e.getEyePosition(1f), e.getEyePosition(1f).add(e.getViewVector(1f).scale(0xff)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e)).getLocation();*/
        return getLookPos(e.level(), e.getEyePosition(),  e.getEyePosition().add(e.getViewVector(1f).scale(0xff)), null, true);
    }

    public static Vec3 getLookPos(Level level, Vec3 fromVec, Vec3 toVec, @Nullable Predicate<Entity> predicate, boolean clipByBlock) {
        if(clipByBlock) toVec =  level.clip(new ClipContext(fromVec, toVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null)).getLocation();
        double d = fromVec.distanceToSqr(toVec);
        Vec3 toReturn = toVec;
        for (Entity e : getEntities(Entity.class, level, fromVec, new AABB(fromVec, toVec), predicate)) {
            AABB aabb = e.getBoundingBox().inflate(e.getPickRadius());
            Optional<Vec3> optional = aabb.clip(fromVec, toVec);
            if (optional.isPresent()) {
                Vec3 v = optional.get();
                if(v.distanceToSqr(fromVec)<d){
                    d = optional.get().distanceToSqr(fromVec);
                    toReturn = optional.get();
                }
            }
        }
        return toReturn;
    }

    public static Vector3f rgb3f(double r, double g, double b) {
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        return new Vec3(r, g, b).scale(1 / 255.0D).toVector3f();
    }

    public static Vector3f rgbMul(Vector3f vector3f, float scalar) {
        Vector3f v = new Vector3f(vector3f).mul(scalar);
        if (v.x > 255) v.x = 255;
        if (v.y > 255) v.y = 255;
        if (v.z > 255) v.z = 255;
        return v;
    }


    public static <T extends Entity> List<T> getEntitiesOnLine(Class<T> tClass, Level level, Vec3 fromVec, Vec3 toVec, @Nullable Predicate<? super T> predicate, boolean clipByBlock) {
        List<T> lst = new ArrayList<>();
        if(clipByBlock) toVec =  level.clip(new ClipContext(fromVec, toVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null)).getLocation();
        double d = fromVec.distanceToSqr(toVec);
        for (T e : getEntities(tClass, level, fromVec, new AABB(fromVec, toVec), predicate)) {
            AABB aabb = e.getBoundingBox().inflate(e.getPickRadius());
            Optional<Vec3> optional = aabb.clip(fromVec, toVec);
            if (aabb.contains(fromVec) || optional.isPresent()) {
                lst.add(e);
            }
        }
        Vec3 finalToVec = toVec;
        return lst.stream().sorted((e0, e1) -> {
            double d0, d1;
            AABB aabb0 = e0.getBoundingBox().inflate(e0.getPickRadius());
            Optional<Vec3> optional0 = aabb0.clip(fromVec, finalToVec);
            AABB aabb1 = e1.getBoundingBox().inflate(e1.getPickRadius());
            Optional<Vec3> optional1 = aabb1.clip(fromVec, finalToVec);
            if(aabb0.contains(fromVec)) d0 = 0;
            else d0 = optional0.map(vec3 -> vec3.distanceToSqr(fromVec)).orElse(d);
            if(aabb1.contains(fromVec)) d1 = 0;
            else d1 = optional1.map(vec3 -> vec3.distanceToSqr(fromVec)).orElse(d);
            return (int) (d1 - d0);
        }).toList();
    }






    public static double random(double min, double max) {
        //return Math.random() * (max - min) + min;
        return Mth.nextDouble(RANDOM,min,max);
    }

    public static float randFloat(double min, double max) {
        return Mth.nextFloat(RANDOM,(float) min,(float) max);
    }

    public static int randInt(int min, int max) {
        return Mth.nextInt(RANDOM,min, max);
    }

    public static int randInt(int num) {
        return Mth.nextInt(RANDOM,-num, num);
    }





    public static double getDegree(double x, double y) {
        if (x == 0) {
            if (y >= 0) return 180;
            else return -180;
        }
        double atan = atan(y / x);
        if (x > 0) return atan;
        if (y >= 0) return atan + 180;
        else return atan - 180;
    }

    public static double degree(double radian) {
        double m = Math.toDegrees(radian);
        return (m - Math.floor(m / 360) * 360);
    }

    public static double radian(double degree) {
        double m = Math.toRadians(degree);
        return (m - Math.floor(m / Math.PI) * Math.PI);
    }

    public static double cos(double d) {
        return Math.cos(Math.toRadians(d));
    }

    public static double sin(double d) {
        return Math.sin(Math.toRadians(d));
    }

    public static double tan(double d) {
        return Math.tan(Math.toRadians(d));
    }

    public static double atan(double tan) {
        return Math.toDegrees(Math.atan(tan));
    }

    public static class Plane {
        public static final Plane ZERO = new Plane(Vec3.ZERO, Vec3.ZERO);
        public static final Plane XY = new Plane(new Vec3(1, 0, 0), new Vec3(0, 1, 0));
        public static final Plane XZ = new Plane(new Vec3(0, 0, 1), new Vec3(1, 0, 0));
        public static final Plane YZ = new Plane(new Vec3(0, 0, 1), new Vec3(0, 1, 0));
        public final Vec3 x;
        public final Vec3 y;

        public Plane(Vec3 x, Vec3 m) {

            Vec3 a = x.normalize();
            Vec3 b = m.normalize().subtract(x.normalize().scale(getAngleCos(x, m))).normalize();
            if (a.equals(Vec3.ZERO) || b.equals(Vec3.ZERO)) {
                this.x = Vec3.ZERO;
                this.y = Vec3.ZERO;
                return;
            }
            if (false) {
                this.x = IM_Math.format2Vec(a);
                this.y = IM_Math.format2Vec(b);
                return;
            }
            this.x = a;
            this.y = b;
        }

        public boolean equals(Plane a) {
            Vec3 m = a.x.cross(a.y).normalize();
            Vec3 n = this.x.cross(this.y).normalize();
            return m.equals(n) || m.equals(n.scale(-1));
        }

        public Vec3 getVec(double x, double y) {
            return this.x.scale(x).add(this.y.scale(y));
        }

        public Plane normalize() {
            return planeVertical(y.cross(x));
        }

        public Vec2d projection(Vec3 v){return new Vec2d(v.dot(x), v.dot(y));}/*only when the plane is normalised*/
    }

    public static class Vec2d {
        public final double x, y;
        private double degree = -0xffff;


        public Vec2d(double x0, double y0) {
            x = x0;
            y = y0;
        }

        public Vec2d(Vec2 vec2) {
            x = vec2.x;
            y = vec2.y;
        }

        public Vec2d lerp(Vec2d toVec, double value) {
            return new Vec2d(Mth.lerp(value, this.x, toVec.x), Mth.lerp(value, this.y, toVec.y));
        }

        public double degree() {
            if (this.degree == -0xffff)
                degree = getDegree(x, y);
            return degree;
        }

        public Vec2d vectorTo(Vec2d toVec) {return new Vec2d(toVec.x - this.x, toVec.y - this.y);}

        public double distanceTo(Vec2d toVec) {
            double d0 = toVec.x - this.x;
            double d1 = toVec.y - this.y;
            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        public double distanceToSqr(Vec2d toVec) {
            double d0 = toVec.x - this.x;
            double d1 = toVec.y - this.y;
            return d0 * d0 + d1 * d1;
        }

        public Vec2d normalize() {
            double d0 = Math.sqrt(this.x * this.x + this.y * this.y);
            return d0 < 1.0E-4D ? new Vec2d(0,0) : new Vec2d(this.x / d0, this.y / d0);
        }


        public static Vec2d byDegree(double d, double r) {
            return new Vec2d(cos(d) * r, sin(d) * r);
        }

        public Vec3 toVec3(Plane p) {
            return p.x.scale(x).add(p.y.scale(y));
        }

        public Vec2d add(double x0, double y0) {
            return new Vec2d(x + x0, y + y0);
        }

        public Vec2d add(Vec2d v) {
            return new Vec2d(x + v.x, y + v.y);
        }

        public Vec2d scale(double s) {
            return new Vec2d(x * s, y * s);
        }

        public double lengthSqr() {
            return x * x + y * y;
        }

        public double length() {
            return Math.sqrt(x * x + y * y);
        }

        public Vec2d rot(double d) {
            return byDegree(degree() + d, length());
        }
    }

    public static class LazyArray<E> extends ArrayList<E> {
        @SafeVarargs
        public LazyArray(E... ts){
            super(Arrays.stream(ts).toList());
        }

        public LazyArray(Collection<E> ts){super(ts);}
        @SafeVarargs
        public final boolean addAll(E... ts){
            return this.addAll(Arrays.stream(ts).toList());
        }
        @Override
        public E get(int index){
            return super.get(Math.floorMod(index,this.size()));
        }

        public LazyArray<E> getRandom(double chance){
            LazyArray<E> lst = new LazyArray<E>();
            this.forEach(e -> {
                if(Math.random()<chance) lst.add(e);
            });
            return lst;
        }
    }

    public static class Vec3List extends LazyArray<Vec3> {
        public static Vec3List get() {return new Vec3List();}
        public static Vec3List get(Vec3... vec3s) {return new Vec3List(vec3s);}
        public static Vec3List get(Collection<Vec3> vec3s) {return new Vec3List(vec3s);}

        public Vec3List(Vec3... vec3s){super(vec3s);}
        public Vec3List(Collection<Vec3> vec3s){super(vec3s);}

        public Vec3List addEach(Vec3 v) {
            Vec3List vl = get();
            this.forEach(v1 -> vl.add(v.add(v1)));
            //IntStream.rangeClosed(0, size()).mapToObj(i -> this.get(i).add(v)).forEachOrdered(vl::add);
            return vl;
        }

        public Vec3List scaleEach(double s) {
            Vec3List vl = get();
            this.forEach(v1 -> vl.add(v1.scale(s)));
            //IntStream.rangeClosed(0, size()).mapToObj(i -> this.get(i).scale(s)).forEachOrdered(vl::add);
            return vl;
        }

    }

    public static class Vec2List extends LazyArray<Vec2d> {
        public static Vec2List get() {
            return new Vec2List();
        }
        public static Vec2List get(Collection<Vec2d> vec2s) {return new Vec2List(vec2s);}
        public static Vec3List get(Vec3... vec2s) {return new Vec3List(vec2s);}

        public Vec2List(Vec2d... vec2s){super(vec2s);}
        public Vec2List(Collection<Vec2d> vec2s){super(vec2s);}

        public Vec2List addEach(Vec2d v) {
            Vec2List vl = get();
            IntStream.rangeClosed(0, size()).mapToObj(i -> this.get(i).add(v)).forEachOrdered(vl::add);
            return vl;
        }

        public Vec2List scaleEach(double s) {
            Vec2List vl = get();
            IntStream.rangeClosed(0, size()).mapToObj(i -> this.get(i).scale(s)).forEachOrdered(vl::add);
            return vl;
        }

        public Vec3List toVec3(Plane p) {
            Vec3List vl = Vec3List.get();
            this.forEach(v1 -> vl.add(v1.toVec3(p)));
            return vl;
        }
    }
    @Mod.EventBusSubscriber
    public static class InTick {
        public static List<InTick> serverTicks = new ArrayList<>();
        public int ticks;
        public final Level sl;
        public final Runnable end;
        public final Runnable during;

        public InTick(Level sl0, int delayTicks, @Nullable Runnable runnableEnd, @Nullable Runnable runnableDuring){
            ticks = delayTicks;
            sl = sl0;
            end = runnableEnd;
            during = runnableDuring;
            this.register();
        }

        public boolean unregister(){
            return serverTicks.remove(this);
        }

        public boolean register(){
            return serverTicks.add(this);
        }

        public void tick(TickEvent.LevelTickEvent event) {
            if (event.level == sl && event.phase == TickEvent.Phase.START){
                if (during != null) during.run();
                if (--this.ticks <= 0) {
                    if (end != null) end.run();
                    this.unregister();
                    //MinecraftForge.EVENT_BUS.unregister(this);
                }
            }

        }
        @SubscribeEvent
        public static void tickAll(TickEvent.LevelTickEvent event) {
            new ArrayList<>(serverTicks).forEach(st -> st.tick(event));
        }
    }

    public static InTick delay(ServerLevel sl, int delayTicks, Runnable runnable) {
        return new InTick(sl,delayTicks,runnable,null);
    }

    public static InTick repeat(Level sl, int repeatTimes, Runnable runnable) {
        return new InTick(sl,repeatTimes,null, runnable);
    }

    public static InTick repeatAndDelay(Level sl, int repeatTimes, Runnable runnable0, Runnable runnable1) {
        return new InTick(sl,repeatTimes,runnable1,runnable0);
    }

}

