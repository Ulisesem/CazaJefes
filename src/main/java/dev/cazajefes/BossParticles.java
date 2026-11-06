package dev.cazajefes;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/** Bounded, entity-free effects around an awake boss or a point in its attack. */
record BossParticles(Shape shape,Particle particle,Color color,Color accent,double radius,double height,
                     double speed,int points) {
    enum Shape {
        AUTO, CRATE, RANDOM_CRATE, RING, SPIRAL, PULSE, BEAM, CLOUD, OFF,
        HELIX, SPHERE, HEART, PULSAR, BEACON, TORNADO, VORTEX, SIMPLE
    }

    private static final List<Shape> CRATE_STYLES = List.of(Shape.HELIX, Shape.SPIRAL, Shape.SPHERE,
        Shape.HEART, Shape.PULSAR, Shape.BEACON, Shape.TORNADO, Shape.VORTEX, Shape.SIMPLE);

    boolean crateVisual() { return shape == Shape.CRATE || CRATE_STYLES.contains(shape); }

    static Shape crateStyle(String bossId) {
        // Stable across restarts and identical for every viewer of the same boss.
        int seed = bossId.hashCode();
        return CRATE_STYLES.get(Math.floorMod(seed ^ (seed >>> 16), CRATE_STYLES.size()));
    }

    static BossParticles read(JsonObject options,Shape fallback) {
        if(options==null)options=new JsonObject();
        return new BossParticles(shape(options,fallback),particle(options),color(options,"color"),color(options,"accent"),
            number(options,"radius",0,0,24),number(options,"height",0,0,16),number(options,"speed",1,0,4),
            (int)number(options,"points",36,4,36));
    }

    static JsonObject section(JsonObject parent,String key) {
        return parent!=null&&parent.get(key) instanceof JsonObject object?object:null;
    }

    static BossParticles aura(BossCatalog.Definition boss) {
        var config = read(section(section(boss.custom().options().json(),"particles"),"aura"), Shape.RANDOM_CRATE);
        if (config.shape() != Shape.RANDOM_CRATE) return config;
        return new BossParticles(crateStyle(boss.id()), config.particle(), config.color(), config.accent(),
            config.radius(), config.height(), config.speed(), config.points());
    }

    static BossParticles attack(BossCatalog.Definition boss,String id,String stage,Shape fallback) {
        var profiles=section(section(boss.custom().options().json(),"particles"),"attacks");
        var selected=section(section(profiles,id),stage);
        if(selected==null)selected=section(section(profiles,"default"),stage);
        return read(selected,fallback);
    }

    List<AuraShape.Point> geometry(double defaultRadius,double defaultHeight,double angle,int budget) {
        int total=Math.min(points,Math.max(0,budget));
        if(shape==Shape.OFF||total==0)return List.of();
        var result=new ArrayList<AuraShape.Point>(total);
        double r=radius>0?radius:defaultRadius;
        double h=height>0?height:defaultHeight;
        double time=angle*speed;
        int ring=Math.max(1,total/3);
        for(int i=0;i<total;i++){
            double t=(i+.5)/total;
            double theta=time+2*Math.PI*t;
            double xz=r,y=.16;
            switch(shape){
                case CRATE -> {
                    if(i<ring)theta=time+2*Math.PI*i/ring;
                    else{
                        double strand=(i-ring+.5)/(total-ring);
                        theta=-time+4*Math.PI*strand;
                        y+=h*strand;
                    }
                }
                case HELIX -> {
                    int split = (total + 1) / 2;
                    boolean second = i >= split;
                    double rise = (i % split + .5) / (second ? total - split : split);
                    theta = time + 2 * Math.PI * rise + (second ? Math.PI : 0);
                    y += h * rise;
                }
                case SPHERE -> {
                    double latitude = 2 * t - 1;
                    theta = time + i * 2.399963229728653;
                    xz *= Math.sqrt(1 - latitude * latitude);
                    y += h * t;
                }
                case HEART -> {
                    double a = 2 * Math.PI * t;
                    double horizontal = r * Math.pow(Math.sin(a), 3);
                    double vertical = (13 * Math.cos(a) - 5 * Math.cos(2 * a)
                        - 2 * Math.cos(3 * a) - Math.cos(4 * a) + 17) / 30;
                    result.add(new AuraShape.Point(Math.cos(time) * horizontal, y + h * vertical,
                        Math.sin(time) * horizontal, i % 3 == 0));
                    continue;
                }
                case PULSAR -> {
                    xz *= .7 + .3 * Math.sin(time + (i % 2) * Math.PI);
                    y += h * (i % 2 == 0 ? .25 : .65);
                }
                case BEACON -> {
                    theta = time + (i % 3) * 2 * Math.PI / 3;
                    xz *= .7;
                    y += h * t;
                }
                case TORNADO -> {
                    theta = time + 4 * Math.PI * t;
                    xz *= .45 + .55 * t;
                    y += h * t;
                }
                case VORTEX -> {
                    theta = -time + 4 * Math.PI * t;
                    xz *= .5 + .5 * t;
                    y += h * (.1 + .2 * (1 - t));
                }
                case SIMPLE -> {
                    xz *= .85 + .15 * Math.sin(theta * 3);
                    y += h * .12;
                }
                case SPIRAL -> {theta=time+4*Math.PI*t;y+=h*t;}
                case PULSE -> xz*=.45+.55*(.5+.5*Math.sin(time));
                case BEAM -> {
                    theta=2*Math.PI*i/Math.max(3,total/3);
                    y+=h*(.5+.45*Math.sin(time+2*Math.PI*(i%3)/3));
                }
                case CLOUD -> {
                    theta=time+i*2.399963229728653;
                    xz*=Math.sqrt(t);
                    y+=h*((i*0.61803398875)%1);
                }
                default -> {}
            }
            result.add(new AuraShape.Point(Math.cos(theta)*xz,y,Math.sin(theta)*xz,i%3==0));
        }
        return result;
    }

    void show(Location at,double radius,double height,double angle,Color primary,Color secondary,boolean warning) {
        if(shape==Shape.OFF||!at.isChunkLoaded())return;
        var audience=at.getWorld().getNearbyPlayers(at,32);
        if(audience.isEmpty())return;
        var first=color==null?primary:color;
        var second=accent==null?secondary:accent;
        var dust=new Particle.DustTransition(first,second,1.2f);
        var reverse=new Particle.DustTransition(second,first,1.05f);
        for(var point:geometry(radius,height,angle,24)){
            Location where=at.clone().add(point.x(),point.y(),point.z());
            for(Player viewer:audience){
                var tint=point.accent()?reverse:dust;
                if(warning)CosmeticFX.warning(viewer,where,particle,tint);
                else if(particle==null)CosmeticFX.to(viewer,Particle.DUST_COLOR_TRANSITION,where,1,0,0,0,0,tint);
                else CosmeticFX.to(viewer,particle,where,1,0,0,0,0,null);
            }
        }
    }

    private static Shape shape(JsonObject options,Shape fallback){
        try{return Shape.valueOf(options.get("shape").getAsString().toUpperCase(Locale.ROOT));}
        catch(RuntimeException invalid){return fallback;}
    }

    private static Particle particle(JsonObject options){
        try{
            var particle=Particle.valueOf(options.get("particle").getAsString().toUpperCase(Locale.ROOT));
            return particle.getDataType()==Void.class?particle:null;
        }catch(RuntimeException invalid){return null;}
    }

    private static Color color(JsonObject options,String key){
        try{
            String value=options.get(key).getAsString();
            return value.matches("#[a-fA-F0-9]{6}")?Color.fromRGB(Integer.parseInt(value.substring(1),16)):null;
        }catch(RuntimeException invalid){return null;}
    }

    private static double number(JsonObject options,String key,double fallback,double min,double max){
        try{
            double value=options.get(key).getAsDouble();
            return Double.isFinite(value)?Math.clamp(value,min,max):fallback;
        }catch(RuntimeException invalid){return fallback;}
    }
}
