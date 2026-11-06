package dev.cazajefes.ai;

import org.bukkit.entity.Player;

public abstract class BossAttack {
    private final String id, name;
    private final AttackCategory category;
    private final int weight, warningTicks;
    private final long cooldown;
    private final double radius, damage;

    protected BossAttack(String id,String name,AttackCategory category,int weight,long cooldown,
                         double radius,double damage,int warningTicks) {
        if (id == null || !id.matches("[a-z0-9_]{1,48}") || name == null || name.isBlank() || name.length() > 80
            || category == null || weight < 0 || weight > 10000 || cooldown < 1 || cooldown > 72000
            || !Double.isFinite(radius) || radius < 0 || radius > 24 || !Double.isFinite(damage) || damage < 0 || damage > 500
            || warningTicks < 1 || warningTicks > 200) throw new IllegalArgumentException("Ataque inválido: "+id);
        this.id=id;
        this.name=name;
        this.category=category;
        this.weight=weight;
        this.cooldown=cooldown;
        this.radius=radius;
        this.damage=damage;
        this.warningTicks=warningTicks;
    }

    public String id(){return id;}
    public String name(){return name;}
    public AttackCategory category(){return category;}
    public int weight(){return weight;}
    public long cooldown(){return cooldown;}
    public double radius(){return radius;}
    public double damage(){return damage;}
    public int warningTicks(){return warningTicks;}

    public boolean inRange(CustomBoss boss,Player target,double distance){return category.includes(distance);}

    public abstract AttackMotion execute(CustomBoss boss, Player target);
}
