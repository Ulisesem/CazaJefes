package dev.cazajefes.ai.attack;

import com.google.gson.JsonParser;
import dev.cazajefes.ai.AttackCategory;
import dev.cazajefes.ai.AttackMotion;
import dev.cazajefes.ai.BossAttack;
import dev.cazajefes.ai.CustomBoss;
import dev.cazajefes.ai.Geometry;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

public final class MeteoriteAoEAttack extends BossAttack {
    private final double height,speed;
    private final Material material;
    private final String skin;

    public MeteoriteAoEAttack(String id,String name,AttackCategory category,int weight,long cooldown,
                              double radius,double damage,int warningTicks,double height,double speed,String material,String texture){
        super(id,name,category,weight,cooldown,radius,damage,warningTicks);
        this.height=height;
        this.speed=speed;
        this.material=Material.matchMaterial(material);
        if(this.material==null||!this.material.isItem()||this.material.isAir())throw new IllegalArgumentException("material de meteorito inválido: "+id);
        skin=texture.isBlank()?null:skin(texture);
    }

    private static String skin(String base64){
        if(base64.length()>8192)throw new IllegalArgumentException("textura demasiado larga");
        var document=JsonParser.parseString(new String(Base64.getDecoder().decode(base64),StandardCharsets.UTF_8)).getAsJsonObject();
        String url=document.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        if(!url.matches("https?://textures\\.minecraft\\.net/texture/[a-f0-9]{60,64}"))
            throw new IllegalArgumentException("la textura debe pertenecer a textures.minecraft.net");
        return url.replaceFirst("^http:","https:");
    }

    private ItemStack helmet(){
        if(skin==null)return new ItemStack(material);
        var stack=new ItemStack(Material.PLAYER_HEAD);
        var meta=(SkullMeta)stack.getItemMeta();
        var profile=Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(skin.getBytes(StandardCharsets.UTF_8)));
        var textures=profile.getTextures();
        try{textures.setSkin(URI.create(skin).toURL());}
        catch(java.net.MalformedURLException invalid){throw new IllegalArgumentException(invalid);}
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        var aim=target.getLocation().add(0,.12,0);
        if(!Geometry.loaded(aim,0)||!boss.environment().mayMove(boss,aim))return AttackMotion.complete();
        double rise=Math.min(height,aim.getWorld().getMaxHeight()-aim.getY()-1);
        if(rise<=.1)return AttackMotion.complete();
        // Limit the spawn point under low ceilings. A second sweep finds the first floor hit.
        var ceiling=aim.getWorld().rayTraceBlocks(aim,new Vector(0,1,0),rise,FluidCollisionMode.NEVER,true);
        if(ceiling!=null)rise=Math.max(.1,ceiling.getHitPosition().getY()-aim.getY()-.12);
        var from=aim.clone().add(0,rise,0);
        var view=boss.environment().visuals().meteor(from,helmet());
        return new AttackMotion(){
            Location position=from;
            int age;
            @Override public boolean tick(){
                var next=position.clone().subtract(0,speed,0);
                if(++age>100||!Geometry.loaded(next,0))return true;
                var hit=position.getWorld().rayTraceBlocks(position,new Vector(0,-1,0),speed,FluidCollisionMode.NEVER,true);
                if(hit!=null){
                    impact(boss,hit.getHitPosition().toLocation(position.getWorld()).add(0,.04,0));
                    return true;
                }
                position=next;
                view.move(position);
                return false;
            }
            @Override public void close(){view.close();}
        };
    }

    private void impact(CustomBoss boss,Location center){
        if(!boss.environment().mayMove(boss,center))return;
        boss.environment().impact(boss,this,center);
        double range=radius();
        if(!Geometry.loaded(center,range))return;
        var origin=center.toVector();
        for(var entity:center.getWorld().getNearbyEntities(center,range,range,range)){
            if(!(entity instanceof LivingEntity victim)||!boss.environment().mayHit(boss,victim)
                ||!Geometry.sphereIntersects(origin,range,victim.getBoundingBox()))continue;
            var direction=victim.getBoundingBox().getCenter().subtract(origin);
            double distance=direction.length();
            if(distance>.15&&center.getWorld().rayTraceBlocks(center,direction.normalize(),distance-.12,
                FluidCollisionMode.NEVER,true)!=null)continue;
            boss.environment().damage(boss,victim,damage());
        }
    }
}
