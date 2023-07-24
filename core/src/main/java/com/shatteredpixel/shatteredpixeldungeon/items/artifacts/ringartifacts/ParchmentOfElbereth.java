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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ParchmentOfElbereth extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_PARCHMENT;
        defaultAction = AC_USE;
        charge = 100;
        chargeCap = 100;
        levelCap = 5;
        setArtifactClass(ArtifactClass.DEFENSE);
    }

    public static final String AC_USE = "USE";

    public static final String AC_PRAY = "PRAY";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed) {
            if (charge == chargeCap)
                actions.add(AC_USE);
            if (level() < levelCap){
                actions.add(AC_PRAY);
            }
        }
        return actions;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new parchmentCharge();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if ( isEquipped( Dungeon.hero ) ){
            if (!cursed) {
                desc += "\n\n" + Messages.get(this, "desc_worn");

            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            charge += 4f * amount;
            if (charge >= chargeCap) {
                charge = chargeCap;
                GLog.positive(Messages.get(ParchmentOfElbereth.class, "full_charge"));
                partialCharge = 0;
            }
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){
            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (charge != chargeCap)        GLog.i( Messages.get(this, "no_charge") );
            else if (cursed)                     GLog.i( Messages.get(this, "cursed") );
            else {
                charge = 0;
                ((HeroSprite)curUser.sprite).read();
                Sample.INSTANCE.play( Assets.Sounds.BLAST, 2f, 2f );
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                Sample.INSTANCE.play(Assets.Sounds.READ);
                GameScene.flash( 0xCCCCCC, false );
                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    if (Dungeon.level.heroFOV[mob.pos]) {
                        Ballistica trajectory = new Ballistica(Dungeon.hero.pos, mob.pos, Ballistica.WONT_STOP);
                        trajectory = new Ballistica(mob.pos, trajectory.collisionPos, Ballistica.FRIENDLY_MAGIC);
                        WandOfBlastWave.throwChar(mob, trajectory, 2 + level()/3, true, false);
                        Buff.affect(mob, Terror.class, 3f + level()/2f).object = hero.id();
                        Buff.affect(mob, Speed.class, 3f + level()/2f);
                    }
                }
                GLog.positive(Messages.get(this, "usage"));
                hero.spendAndNext(Actor.TICK);
                updateQuickslot();
                Invisibility.dispel();
            }
        } else if (action.equals(AC_PRAY)){
            boolean monstersHere = false;
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
                if (mob.alignment == Char.Alignment.ENEMY) {
                    monstersHere = true;
                    break;
                }
            }
            if (!monstersHere)
                GLog.warning(Messages.get(this, "no_way"));
            else {
                int len = Dungeon.level.length();
                boolean[] p = Dungeon.level.passable;
                boolean[] passable = new boolean[len];
                System.arraycopy(p, 0, passable, 0, len);

                PathFinder.Path newpath = Dungeon.findPath(hero, Dungeon.level.randomDestination(hero), passable, Dungeon.level.heroFOV, true);
                if (newpath != null) {
                    Sample.INSTANCE.play(Assets.Sounds.READ);
                    Camera.main.shake(3f, 2f);
                    hero.sprite.operate(hero.pos, () -> {
                        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
                            if (mob.alignment == Char.Alignment.ENEMY) mob.aggro(hero);
                        }
                        Buff.affect(hero, parchmentPraying.class, 20f + 10f * level());
                        hero.spendAndNext(20f + 10f * level());
                        if (hero.isAlive()) {
                            upgrade();
                            GLog.positive(Messages.get(ParchmentOfElbereth.class, "level_up"));
                        }
                    });
                } else {
                    GLog.warning(Messages.get(this, "no_way"));
                }
            }
        }
    }

    public static class parchmentPraying extends FlavourBuff {
    }

    public class parchmentCharge extends ArtifactBuff {
        @Override
        public boolean act() {

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                //fully charges in 500 turns at +0, scaling to 250 turns at +10.
                float chargeGain = (0.2f+(level()*0.08f));
                partialCharge += chargeGain;

                if (partialCharge > 1 && charge < chargeCap) {
                    partialCharge--;
                    charge++;
                    updateQuickslot();
                } else if (charge >= chargeCap) {
                    partialCharge = 0;
                    GLog.positive( Messages.get(ParchmentOfElbereth.class, "full_charge") );
                }
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }
    }
}
