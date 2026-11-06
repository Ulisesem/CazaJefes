package dev.cazajefes;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

final class CavePassages {
    private record Saved(UUID boss,UUID world,int x,int y,int z,BlockData data) {
        Block block(World world){return world.getBlockAt(x,y,z);}
    }
    private final CazaJefesPlugin plugin;
    private final Path journal;
    private final List<Saved> blocks=new ArrayList<>();
    private final Map<UUID,LivingEntity> active=new HashMap<>();
    private final BukkitTask task;
    private boolean available=true;

    CavePassages(CazaJefesPlugin plugin){
        this.plugin=plugin;
        journal=plugin.getDataFolder().toPath().resolve("cave-passages.tsv");
        try{
            if(Files.exists(journal)){
                if(Files.size(journal)>512*1024)throw new IOException("registro demasiado grande");
                for(String line:Files.readAllLines(journal)){
                    String[] row=line.split("\t",6);
                    blocks.add(new Saved(UUID.fromString(row[0]),UUID.fromString(row[1]),Integer.parseInt(row[2]),
                        Integer.parseInt(row[3]),Integer.parseInt(row[4]),Bukkit.createBlockData(row[5])));
                }
            }
        }catch(IOException|RuntimeException error){
            available=false;
            plugin.getLogger().warning("No se pudo recuperar el registro de cuevas: "+error.getMessage());
        }
        task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::restore,20,20);
    }

    void open(LivingEntity boss,Player target,Location attempted){
        if(!available||blocks.size()>=512||!attempted.isChunkLoaded()||boss.isInWater())return;
        var world=attempted.getWorld();
        if(target.getWorld()!=world||!Boolean.TRUE.equals(world.getGameRuleValue(GameRule.MOB_GRIEFING))
            ||!ArenaBoundary.allowsTerrain(boss,attempted))return;
        var origin=boss.getLocation();
        boolean vertical=attempted.getBlockX()==origin.getBlockX()&&attempted.getBlockZ()==origin.getBlockZ()
            &&Math.abs(attempted.getY()-origin.getY())>.5;
        boolean underRoof=attempted.getY()<world.getSeaLevel()
            &&world.getHighestBlockYAt(attempted)>attempted.getBlockY()+boss.getHeight();
        var id=boss.getUniqueId();
        var batch=new ArrayList<Saved>();
        if(!vertical&&underRoof){
            var box=boss.getBoundingBox().shift(attempted.toVector().subtract(origin.toVector())).expand(-.001);
            for(int x=(int)Math.floor(box.getMinX());x<=Math.floor(box.getMaxX());x++)
                for(int y=(int)Math.floor(box.getMinY());y<=Math.floor(box.getMaxY());y++)
                    for(int z=(int)Math.floor(box.getMinZ());z<=Math.floor(box.getMaxZ());z++){
                        if(y<world.getMinHeight()||y>=world.getMaxHeight()||!world.isChunkLoaded(x>>4,z>>4))return;
                        var block=world.getBlockAt(x,y,z);
                        if(!block.isPassable()&&batch.size()<24)collect(boss,block,batch);
                    }
        }
        if(batch.isEmpty()){
            var eye=boss.getEyeLocation();
            var direction=target.getEyeLocation().toVector().subtract(eye.toVector());
            double distance=Math.min(6,direction.length());
            if(distance>.01&&dev.cazajefes.ai.Geometry.loaded(eye,distance)){
                var obstruction=world.rayTraceBlocks(eye,direction.normalize(),distance,FluidCollisionMode.NEVER,true);
                if(obstruction!=null)collect(boss,obstruction.getHitBlock(),batch);
                else if(vertical&&target.getY()>origin.getY()&&target.isOnGround()
                    &&target.getLocation().distanceSquared(origin)<=36){
                    var support=target.getLocation().subtract(0,.16,0).getBlock();
                    collect(boss,support,batch);
                }
            }
        }
        if(batch.isEmpty()||batch.size()+blocks.size()>512
            ||blocks.stream().filter(saved->saved.boss().equals(id)).count()+batch.size()>96)return;
        blocks.addAll(batch);
        if(!save()){
            blocks.subList(blocks.size()-batch.size(),blocks.size()).clear();
            return;
        }
        active.put(id,boss);
        for(var saved:batch){
            var block=saved.block(world);
            block.setType(Material.CAVE_AIR,false);
            world.spawnParticle(Particle.BLOCK,block.getLocation().add(.5,.5,.5),4,.25,.25,.25,0,saved.data());
        }
        world.playSound(attempted,Sound.BLOCK_STONE_BREAK,1,.7f);
    }

    private static void collect(LivingEntity boss,Block block,List<Saved> batch){
        if(block==null||!natural(block.getType())||!ArenaBoundary.allowsTerrain(boss,block.getLocation()))return;
        var data=block.getBlockData();
        if(data instanceof org.bukkit.block.data.Waterlogged waterlogged&&waterlogged.isWaterlogged())return;
        if(!new EntityChangeBlockEvent(boss,block,Material.CAVE_AIR.createBlockData()).callEvent()
            ||!block.getBlockData().equals(data))return;
        batch.add(new Saved(boss.getUniqueId(),block.getWorld().getUID(),block.getX(),block.getY(),block.getZ(),data));
    }

    private static boolean natural(Material material){
        return switch(material){
            case STONE, DEEPSLATE, ANDESITE, DIORITE, GRANITE, TUFF, CALCITE, DRIPSTONE_BLOCK,
                 DIRT, ROOTED_DIRT, COARSE_DIRT, CLAY, NETHERRACK, BASALT, BLACKSTONE,
                 COBBLESTONE, COBBLED_DEEPSLATE, STONE_BRICKS, DEEPSLATE_BRICKS,
                 OAK_PLANKS, SPRUCE_PLANKS, BIRCH_PLANKS, JUNGLE_PLANKS, ACACIA_PLANKS,
                 DARK_OAK_PLANKS, MANGROVE_PLANKS, CHERRY_PLANKS, PALE_OAK_PLANKS -> true;
            default -> false;
        };
    }

    void release(UUID id){active.remove(id);}

    private void restore(){
        if(!available)return;
        active.values().removeIf(boss->!boss.isValid()||boss.isDead());
        boolean changed=false;
        for(var it=blocks.iterator();it.hasNext();){
            var saved=it.next();
            if(active.containsKey(saved.boss()))continue;
            var world=Bukkit.getWorld(saved.world());
            if(world==null||!world.isChunkLoaded(saved.x()>>4,saved.z()>>4))continue;
            var block=saved.block(world);
            if(block.getType()==Material.CAVE_AIR){
                var box=BoundingBox.of(block);
                if(!world.getNearbyEntities(box,entity->entity instanceof LivingEntity&&!entity.isDead()).isEmpty())continue;
                block.setBlockData(saved.data(),false);
            }
            it.remove();
            changed=true;
        }
        if(changed)save();
    }

    private boolean save(){
        try{
            var rows=blocks.stream().map(s->s.boss()+"\t"+s.world()+"\t"+s.x()+"\t"+s.y()+"\t"+s.z()+"\t"+s.data().getAsString()).toList();
            Path temp=journal.resolveSibling("cave-passages.tmp");
            Files.write(temp,rows);
            try{Files.move(temp,journal,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
            catch(AtomicMoveNotSupportedException unsupported){Files.move(temp,journal,StandardCopyOption.REPLACE_EXISTING);}
            return true;
        }catch(IOException failure){
            plugin.getLogger().warning("No se pudo guardar el registro de cuevas: "+failure.getMessage());
            return false;
        }
    }

    void stop(){task.cancel();active.clear();restore();}
}
