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
    enum Shape { AUTO, CRATE, RING, SPIRAL, PULSE, BEAM, CLOUD, OFF }

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
        return read(section(section(boss.custom().options().json(),"particles"),"aura"),Shape.CRATE);
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
