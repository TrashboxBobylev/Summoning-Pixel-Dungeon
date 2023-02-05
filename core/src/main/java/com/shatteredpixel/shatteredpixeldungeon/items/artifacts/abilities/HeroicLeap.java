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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class HeroicLeap extends Ability {
    {
        baseChargeUse = 25;
        image = ItemSpriteSheet.HEROIC_LEAP;
        setArtifactClass(ArtifactClass.OFFENSE);
    }

    @Override
    public float chargeUse() {
        switch (level()){
            case 1: return 35;
            case 2: return 65;
        }
        return super.chargeUse();
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public void activate(Ability ability, Hero hero, Integer target ) {
        if (target != null) {

            Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
            int cell = route.collisionPos;

            //can't occupy the same cell as another char, so move back one.
            int backTrace = route.dist-1;
            while (Actor.findChar( cell ) != null && cell != hero.pos) {
                cell = route.path.get(backTrace);
                backTrace--;
            }

            charge -= chargeUse();
            updateQuickslot();

            final int dest = cell;
            hero.busy();
            hero.sprite.jump(hero.pos, cell, new Callback() {
                @Override
                public void call() {
                    hero.move(dest);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();

                    WandOfBlastWave.BlastWave.blast(dest);
                    Camera.main.shake(2, 0.5f);

                    Invisibility.dispel();
                    hero.spendAndNext(Actor.TICK);
                    for (int i : PathFinder.NEIGHBOURS8) {
                        Char mob = Actor.findChar(hero.pos + i);
                        if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                            if (level() == 1){
                                int damage = hero.drRoll();
                                damage = (int) (Math.round(damage)*1.5f);
                                mob.damage(damage, hero);
                            }
                        }
                    }
                    if (level() == 2){
                        Ballistica aim = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);

                        int maxDist = 3;
                        int dist = Math.min(aim.dist, maxDist);

                        ConeAOE cone = new ConeAOE(aim,
                                dist,
                                360,
                                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

                        //cast to cells at the tip, rather than all cells, better performance.
                        for (Ballistica ray : cone.outerRays){
                            ((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
                                    MagicMissile.FORCE_CONE,
                                    hero.sprite,
                                    ray.path.get(ray.dist),
                                    null
                            );
                        }

                        hero.sprite.zap(target);
                        Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.5f);
                        Camera.main.shake(2, 0.5f);
                        //final zap at 2/3 distance, for timing of the actual effect
                        MagicMissile.boltFromChar(hero.sprite.parent,
                                MagicMissile.FORCE_CONE,
                                hero.sprite,
                                cone.coreRay.path.get(dist * 2 / 3),
                                new Callback() {
                                    @Override
                                    public void call() {

                                        for (int cell : cone.cells){

                                            Char ch = Actor.findChar(cell);
                                            if (ch != null && ch.alignment != hero.alignment){
                                                int scalingStr = hero.STR()-10;
                                                int damage = Random.NormalIntRange(7 + scalingStr*2, 13 + 5*scalingStr);
                                                damage -= ch.drRoll();

                                                ch.damage(damage, hero);
                                                if (ch.isAlive()){
                                                    Buff.affect(ch, Cripple.class, 5f);
                                                }

                                            }
                                        }

                                        Invisibility.dispel();
                                        hero.spendAndNext(Actor.TICK);

                                    }
                                });
                    }


//                    if (hero.buff(DoubleJumpTracker.class) != null){
//                        hero.buff(DoubleJumpTracker.class).detach();
//                        if (hero.pointsInTalent(Talent.DOUBLE_JUMP) > 2){
//                            Buff.affect(hero, TripleJumpTracker.class, 5);
//                        }
//                    } else
//                    if (hero.canHaveTalent(Talent.DOUBLE_JUMP) && hero.buff(TripleJumpTracker.class) == null) {
//                        Buff.affect(hero, DoubleJumpTracker.class, 5);
//                    } else if (hero.buff(TripleJumpTracker.class) != null){
//                        hero.buff(TripleJumpTracker.class).detach();
//                    }

                }
            });
        }
    }

    public static class DoubleJumpTracker extends FlavourBuff{};
    public static class TripleJumpTracker extends FlavourBuff{};

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Ropes.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = HeroicLeap.class;
            outQuantity = 1;
        }

    }
}
