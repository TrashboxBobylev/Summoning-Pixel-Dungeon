/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FrostfireParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;

public class ElementalBlast extends Ability {
    private static final HashMap<Class<?extends Wand>, Integer> effectTypes = new HashMap<>();
    static {
        effectTypes.put(WandOfMagicMissile.class,   MagicMissile.MAGIC_MISS_CONE);
        effectTypes.put(WandOfLightning.class,      MagicMissile.SPARK_CONE);
        effectTypes.put(WandOfBounceBeams.class, MagicMissile.PURPLE_CONE);
        effectTypes.put(WandOfFireblast.class,      MagicMissile.FIRE_CONE);
        effectTypes.put(WandOfCorrosion.class,      MagicMissile.CORROSION_CONE);
        effectTypes.put(WandOfBlastWave.class,      MagicMissile.FORCE_CONE);
        effectTypes.put(WandOfLivingEarth.class,    MagicMissile.EARTH_CONE);
        effectTypes.put(WandOfFrost.class,          MagicMissile.FROST_CONE);
        effectTypes.put(WandOfPrismaticLight.class, MagicMissile.RAINBOW_CONE);
        effectTypes.put(WandOfWarding.class,        MagicMissile.WARD_CONE);
        effectTypes.put(WandOfTransfusion.class,    MagicMissile.BLOOD_CONE);
        effectTypes.put(WandOfCorruption.class,     MagicMissile.SHADOW_CONE);
        effectTypes.put(WandOfRegrowth.class,       MagicMissile.FOLIAGE_CONE);
        effectTypes.put(WandOfCrystalBullet.class,  MagicMissile.CRYSTAL_CONE);
        effectTypes.put(WandOfStench.class,         MagicMissile.STENCH_CONE);
        effectTypes.put(WandOfConjuration.class,    MagicMissile.CONJURE_CONE);
        effectTypes.put(WandOfStars.class,          MagicMissile.ABYSS);
    }

    private static final HashMap<Class<?extends Wand>, Float> damageFactors = new HashMap<>();
    static {
        damageFactors.put(WandOfMagicMissile.class,     0.5f);
        damageFactors.put(WandOfLightning.class,        1f);
        damageFactors.put(WandOfBounceBeams.class,   1f);
        damageFactors.put(WandOfFireblast.class,        1f);
        damageFactors.put(WandOfCorrosion.class,        0f);
        damageFactors.put(WandOfBlastWave.class,        0.67f);
        damageFactors.put(WandOfLivingEarth.class,      0.5f);
        damageFactors.put(WandOfFrost.class,            1f);
        damageFactors.put(WandOfPrismaticLight.class,   0.67f);
        damageFactors.put(WandOfWarding.class,          0f);
        damageFactors.put(WandOfTransfusion.class,      0f);
        damageFactors.put(WandOfCorruption.class,       0f);
        damageFactors.put(WandOfRegrowth.class,         0f);
        damageFactors.put(WandOfStench.class,           0f);
        damageFactors.put(WandOfConjuration.class,      0f);
        damageFactors.put(WandOfStars.class,            0f);
        damageFactors.put(WandOfCrystalBullet.class,    0f);
    }

    {
        baseChargeUse = 35;
        image = ItemSpriteSheet.ELEMENTAL_BLAST;
        artifactClass = ArtifactClass.OFFENSE;
    }

    @Override
    public float chargeUse() {
        switch (level()){
            case 1: return 45;
            case 2: return 60;
        }
        return super.chargeUse();
    }

    static int lvl;

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        lvl = level();
        castElementalBlast(hero);
        charge -= chargeUse();
        updateQuickslot();
        lvl = -1;
    }

    public static float powerLevel(){
        switch (lvl){
            case 1: return 0.5f;
            case 2: return 1.5f;
        }
        return 1f;
    }

    public static void castElementalBlast(Hero hero) {
        Ballistica aim;
        //Basically the direction of the aim only matters if it goes outside the map
        //So we just ensure it won't do that.
        if (hero.pos % Dungeon.level.width() > 10){
            aim = new Ballistica(hero.pos, hero.pos - 1, Ballistica.WONT_STOP);
        } else {
            aim = new Ballistica(hero.pos, hero.pos + 1, Ballistica.WONT_STOP);
        }

        if (hero.buff(MagicImmune.class) != null || Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
            GLog.warning( Messages.get(Wand.class, "no_magic") );
            return;
        }

        final Class<? extends Wand>[] wandCls = new Class[]{null};
        int minDamage = 0, maxDamage = 0;
        if (hero.belongings.weapon instanceof Wand) {
            wandCls[0] = (Class<? extends Wand>) hero.belongings.weapon.getClass();
            minDamage = hero.belongings.weapon.min();
            maxDamage = hero.belongings.weapon.max();
        }

        if (wandCls[0] == null){
            return;
        }

        int aoeSize = 4;
        if (lvl == 1) aoeSize = 8;
        if (lvl == 2) aoeSize = 5;

        int projectileProps = Ballistica.STOP_SOLID | Ballistica.STOP_TARGET;

        //### Special Projectile Properties ###
        //*** Wand of Disintegration ***
        if (wandCls[0] == WandOfBounceBeams.class){
            projectileProps = Ballistica.STOP_TARGET;

            //*** Wand of Fireblast ***
        } else if (wandCls[0] == WandOfFireblast.class){
            projectileProps = projectileProps | Ballistica.IGNORE_SOFT_SOLID;

            //*** Wand of Warding ***
        } else if (wandCls[0] == WandOfWarding.class){
            projectileProps = Ballistica.STOP_TARGET;

        }

        ConeAOE aoe = new ConeAOE(aim, aoeSize, 360, projectileProps);

        for (Ballistica ray : aoe.outerRays){
            ((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
                    effectTypes.get(wandCls[0]),
                    hero.sprite,
                    ray.path.get(ray.dist),
                    null
            );
        }

        final float effectMulti = powerLevel();
        final float miscEffectMulti = powerLevel();

        //cast a ray 2/3 the way, and do effects
        final Class<? extends Wand>[] finalWandCls = new Class[]{wandCls[0]};
        int finalMinDamage = minDamage;
        int finalMaxDamage = maxDamage;
        int finalAoe = aoeSize;
        ((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
                effectTypes.get(wandCls[0]),
                hero.sprite,
                aim.path.get(aoeSize / 2),
                new Callback() {
                    @Override
                    public void call() {

                        int charsHit = 0;
                        Freezing freeze = (Freezing)Dungeon.level.blobs.get( Freezing.class );
                        Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
                        for (int cell : aoe.cells) {

                            //### Cell effects ###
                            //*** Wand of Lightning ***
                            if (finalWandCls[0] == WandOfLightning.class){
                                if (Dungeon.level.water[cell]){
                                    GameScene.add( Blob.seed( cell, (int) (4 * miscEffectMulti), Electricity.class ) );
                                }

                                //*** Wand of Fireblast ***
                            } else if (finalWandCls[0] == WandOfFireblast.class){
                                if (Dungeon.level.map[cell] == Terrain.DOOR){
                                    Level.set(cell, Terrain.OPEN_DOOR);
                                    GameScene.updateMap(cell);
                                }
                                if (freeze != null){
                                    freeze.clear(cell);
                                }
                                if (Dungeon.level.flamable[cell]){
                                    GameScene.add( Blob.seed( cell, (int) (4 * miscEffectMulti), Fire.class ) );
                                }

                                //*** Wand of Frost ***
                            } else if (finalWandCls[0] == WandOfFrost.class){
                                if (fire != null){
                                    fire.clear(cell);
                                }

                                //*** Wand of Prismatic Light ***
                            } else if (finalWandCls[0] == WandOfPrismaticLight.class){
                                for (int n : PathFinder.NEIGHBOURS9) {
                                    int c = cell+n;

                                    if (Dungeon.level.discoverable[c]) {
                                        Dungeon.level.mapped[c] = true;
                                    }

                                    int terr = Dungeon.level.map[c];
                                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                                        Dungeon.level.discover(c);

                                        GameScene.discoverTile(c, terr);
                                        ScrollOfMagicMapping.discover(c);

                                    }
                                }

                                //*** Wand of Regrowth ***
                            } else if (finalWandCls[0] == WandOfRegrowth.class){
                                //TODO: spend 3 charges worth of regrowth energy from staff?
                                int t = Dungeon.level.map[cell];
                                if (Random.Float() < 0.33f*effectMulti) {
                                    if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
                                            || t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
                                            && Dungeon.level.plants.get(cell) == null) {
                                        Level.set(cell, Terrain.HIGH_GRASS);
                                        GameScene.updateMap(cell);
                                    }
                                }
                            }

                            //### Deal damage ###
                            Char mob = Actor.findChar(cell);
                            int damage = Math.round(Random.NormalIntRange(finalMinDamage, finalMaxDamage)
                                    * effectMulti
                                    * damageFactors.get(finalWandCls[0]));
                            if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) damage = 0;

                            if (mob != null && damage > 0 && mob.alignment != Char.Alignment.ALLY){
                                mob.damage(damage, Reflection.newInstance(finalWandCls[0]));
                                charsHit++;
                            }

                            //### Other Char Effects ###
                            if (mob != null && mob != hero){
                                //*** Wand of Lightning ***
                                if (finalWandCls[0] == WandOfLightning.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.affect( mob, Paralysis.class, effectMulti* Paralysis.DURATION/2 );
                                    }

                                    //*** Wand of Fireblast ***
                                } else if (finalWandCls[0] == WandOfFireblast.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.affect( mob, Burning.class ).reignite( mob );
                                    }

                                    //*** Wand of Corrosion ***
                                } else if (finalWandCls[0] == WandOfCorrosion.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.affect( mob, Corrosion.class ).set(3, Math.round(6*effectMulti));
                                        charsHit++;
                                    }

                                    //*** Wand of Blast Wave ***
                                } else if (finalWandCls[0] == WandOfBlastWave.class){
                                    if (mob.alignment != Char.Alignment.ALLY) {
                                        Ballistica aim = new Ballistica(hero.pos, mob.pos, Ballistica.WONT_STOP);
                                        int knockback = finalAoe + 1 - (int)Dungeon.level.trueDistance(hero.pos, mob.pos);
                                        knockback *= effectMulti;
                                        WandOfBlastWave.throwChar(mob,
                                                new Ballistica(mob.pos, aim.collisionPos, Ballistica.MAGIC_BOLT),
                                                knockback,
                                                true);
                                    }

                                    //*** Wand of Frost ***
                                } else if (finalWandCls[0] == WandOfFrost.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.affect( mob, Frost.class, effectMulti*Frost.DURATION );
                                    }

                                    //*** Wand of Prismatic Light ***
                                } else if (finalWandCls[0] == WandOfPrismaticLight.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.prolong(mob, Blindness.class, effectMulti*Blindness.DURATION/2);
                                        charsHit++;
                                    }

                                    //*** Wand of Warding ***
                                } else if (finalWandCls[0] == WandOfWarding.class){
                                    if (mob instanceof WandOfWarding.Ward){
                                        ((WandOfWarding.Ward) mob).wandHeal(0);
                                        charsHit++;
                                    }

                                    //*** Wand of Transfusion ***
                                } else if (finalWandCls[0] == WandOfTransfusion.class){
                                    if(mob.alignment == Char.Alignment.ALLY || mob.buff(Charm.class) != null){
                                        int healing = Math.round(10*effectMulti);
                                        int shielding = (mob.HP + healing) - mob.HT;
                                        if (shielding > 0){
                                            healing -= shielding;
                                            Buff.affect(mob, Barrier.class).setShield(shielding);
                                        } else {
                                            shielding = 0;
                                        }
                                        mob.HP += healing;

                                        mob.sprite.emitter().burst(Speck.factory(Speck.HEALING), 4);
                                        mob.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing + shielding);
                                    } else {
                                        if (!mob.properties().contains(Char.Property.UNDEAD)) {
                                            Charm charm = Buff.affect(mob, Charm.class, effectMulti*Charm.DURATION/2f);
                                            charm.object = hero.id();
                                            charm.ignoreNextHit = true;
                                            mob.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 3);
                                        } else {
                                            damage = Math.round(Random.NormalIntRange(finalMinDamage, finalMaxDamage) * effectMulti);
                                            mob.damage(damage, Reflection.newInstance(finalWandCls[0]));
                                            mob.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                                        }
                                    }
                                    charsHit++;

                                    //*** Wand of Corruption ***
                                } else if (finalWandCls[0] == WandOfCorruption.class){
                                    if (mob.isAlive() && mob.alignment != Char.Alignment.ALLY) {
                                        Buff.prolong(mob, Amok.class, effectMulti*5f);
                                        charsHit++;
                                    }

                                    //*** Wand of Regrowth ***
                                } else if (finalWandCls[0] == WandOfRegrowth.class){
                                    if (mob.alignment != Char.Alignment.ALLY) {
                                        Buff.prolong( mob, Roots.class, effectMulti*Roots.DURATION );
                                        charsHit++;
                                    }
                                } else if (finalWandCls[0] == WandOfConjuration.class){
                                    if (mob.alignment != Char.Alignment.ALLY){
                                        Buff.affect( mob, Viscosity.DeferedDamage.class).prolong((int) (Random.NormalIntRange(finalMinDamage, finalMaxDamage) * effectMulti * 3));
                                    }
                                } else if (finalWandCls[0] == WandOfStench.class){
                                    if (mob.alignment != Char.Alignment.ALLY){
                                        StenchHolder buff = Buff.affect(mob, StenchHolder.class, 5*effectMulti);
                                        buff.minDamage = 1 + Dungeon.hero.lvl/5;
                                        buff.maxDamage = 1 + Dungeon.hero.lvl/5;
                                    }
                                } else if (finalWandCls[0] == WandOfStars.class){
                                    for (int i : PathFinder.NEIGHBOURS9) {
                                        CellEmitter.get(cell + i).burst(FrostfireParticle.FACTORY, 8);
                                        Char ch = Actor.findChar(cell + i);
                                        if (ch != null && ch.alignment != null) {
                                            ch.damage((int) (Random.NormalIntRange(finalMinDamage, finalMaxDamage) * effectMulti), this);
                                        }
                                    }
                                }
                            }

                        }

                        //### Self-Effects ###
                        //*** Wand of Magic Missile ***
                        if (finalWandCls[0] == WandOfMagicMissile.class) {
                            Buff.affect(hero, Recharging.class, effectMulti* Recharging.DURATION / 2f);

                            //*** Wand of Living Earth ***
                        } else if (finalWandCls[0] == WandOfLivingEarth.class && charsHit > 0){
                            for (Mob m : Dungeon.level.mobs){
                                if (m instanceof WandOfLivingEarth.EarthGuardian){
                                    ((WandOfLivingEarth.EarthGuardian) m).setInfo(hero, 0, Math.round(effectMulti*charsHit*5));
                                    m.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + charsHit);
                                    break;
                                }
                            }

                            //*** Wand of Frost ***
                        } else if (finalWandCls[0] == WandOfFrost.class){
                            if ((hero.buff(Burning.class)) != null) {
                                hero.buff(Burning.class).detach();
                            }

                            //*** Wand of Prismatic Light ***
                        } else if (finalWandCls[0] == WandOfPrismaticLight.class){
                            Buff.prolong( hero, Light.class, effectMulti*50f);
                        } else if (finalWandCls[0] == WandOfCrystalBullet.class){
                            Buff.affect(hero, ArcaneArmor.class).set(Dungeon.hero.lvl/2, (int) (4*effectMulti));
                        }

                        hero.spendAndNext(Actor.TICK);
                    }
                }
        );

        hero.sprite.operate( hero.pos );
        Invisibility.dispel();
        hero.busy();

        Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
    }
}
