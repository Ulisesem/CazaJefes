package dev.cazajefes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

final class Languages {
    private static final List<String> IDS=List.of("es","en","pt");
    private static final NamespacedKey LANGUAGE=new NamespacedKey("cazajefes","language");

    record Language(String id,String name,String textureHash,JsonObject file,JsonObject defaults) {}
    private final List<Language> entries;

    Languages(CazaJefesPlugin plugin){
        var loaded=new ArrayList<Language>(IDS.size());
        for(String id:IDS){
            String resource="lenguajes/"+id+".json";
            JsonObject defaults;
            try(var input=plugin.getResource(resource)){
                if(input==null)throw new IOException("Falta "+resource);
                defaults=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();
            }catch(IOException | RuntimeException error){
                throw new IllegalStateException("Idioma incluido inválido: "+id,error);
            }
            var file=plugin.getDataFolder().toPath().resolve(resource);
            try{
                if(Files.notExists(file))plugin.saveResource(resource,false);
                if(Files.size(file)>256*1024)throw new IllegalArgumentException("máximo 256 KiB");
                var data=JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                loaded.add(parse(id,data,defaults));
            }catch(IOException | RuntimeException error){
                plugin.getLogger().warning(resource+": "+error.getMessage()+"; se usa la traducción incluida.");
                loaded.add(parse(id,defaults,defaults));
            }
        }
        entries=List.copyOf(loaded);
    }

    static Language parse(String id,JsonObject data,JsonObject defaults){
        var meta=data.getAsJsonObject("meta");
        if(meta==null||!id.equals(string(meta,"id",null)))throw new IllegalArgumentException("meta.id debe coincidir con "+id);
        String name=string(meta,"name","");
        if(name.isBlank()||name.length()>48)throw new IllegalArgumentException("meta.name requiere entre 1 y 48 caracteres");
        String texture=string(meta,"texture","");
        if(texture.length()>8192)throw new IllegalArgumentException("meta.texture excede 8192 caracteres");
        return new Language(id,name,BossPortraits.textureHash(texture),data,defaults);
    }

    List<Language> all(){return entries;}

    Language of(Player player){
        if(player==null)return entries.getFirst();
        String saved=player.getPersistentDataContainer().get(LANGUAGE,PersistentDataType.STRING);
        if(saved==null)saved=player.locale().getLanguage().toLowerCase(Locale.ROOT);
        for(var language:entries)if(language.id().equals(saved))return language;
        return entries.getFirst();
    }

    void select(Player player,Language language){
        player.getPersistentDataContainer().set(LANGUAGE,PersistentDataType.STRING,language.id());
    }

    String text(Player player,String key,String fallback){
        var language=of(player);
        return string(section(language.file(),"messages"),key,
            string(section(language.defaults(),"messages"),key,fallback));
    }

    String boss(Player player,BossCatalog.Definition boss,String field){
        var language=of(player);
        var configured=section(section(language.file(),"bosses"),boss.id());
        return string(configured,field,field.equals("name")?boss.name():boss.description());
    }

    String attack(Player player,String id,String fallback){
        return string(section(of(player).file(),"attacks"),id,fallback);
    }

    String state(Player player,String state){
        if(state.startsWith("IMPACTO "))return text(player,"attack.impact","IMPACTO")+" "+state.substring(8);
        if(state.startsWith("COMBINACIÓN · "))return text(player,"attack.combo","COMBINACIÓN")+" · "+state.substring(14);
        String key=switch(state){
            case "ALZA EL HACHA" -> "axe";
            case "SAL DEL ABANICO DE PARTÍCULAS" -> "cone";
            case "ROMPE UNA RUNA · BUSCA EL CÍRCULO VERDE" -> "rune";
            case "¡SAL DE LAS CASILLAS MARCADAS!" -> "rain";
            case "PREPARACIÓN" -> "prepare";
            case "DEFINITIVO · ¡ESQUIVA!" -> "ultimate";
            case "ZONA DE PELIGRO" -> "danger";
            case "IMPACTO DE COMBINACIÓN" -> "comboImpact";
            case "LLUVIA · CAMBIA DE CASILLA" -> "moveRain";
            case "SIGUIENTE CORTE · CAMBIA DE LADO" -> "nextSlash";
            case "¡GOLPEA EL HACHA PARA INTERRUMPIR!" -> "interruptAxe";
            default -> null;
        };
        return key==null?state:text(player,"attack."+key,state);
    }

    private static JsonObject section(JsonObject object,String key){
        if(object==null)return null;
        var value=object.get(key);
        return value!=null&&value.isJsonObject()?value.getAsJsonObject():null;
    }

    private static String string(JsonObject object,String key,String fallback){
        if(object==null)return fallback;
        var value=object.get(key);
        if(value==null||!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isString())return fallback;
        String text=value.getAsString();
        return text.length()<=1024?text:fallback;
    }
}
