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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DruidBag extends Ability {

    {
        baseChargeUse = 25;
        image = ItemSpriteSheet.GRASS_ABILITY;
        setArtifactClass(ArtifactClass.DEFENSE);
    }

    @Override
    public float chargeUse() {
        switch (level()){
            case 1: return 50;
            case 2: return 80;
        }
        return super.chargeUse();
    }

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        hero.sprite.operate(hero.pos, () -> {
            hero.sprite.idle();
            hero.spendAndNext(Actor.TICK);
            ArrayList<Integer> grassCells = new ArrayList<>();
            PathFinder.buildDistanceMap( hero.pos, BArray.not( Dungeon.level.solid, null ), level()+1 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    grassCells.add(i);
                }
            }
            Random.shuffle(grassCells);
            for (int cell : grassCells){
                Char ch = Actor.findChar(cell);
                if (ch != null && ch.alignment == Char.Alignment.ENEMY){
                    int duration = 4-level()*2;
                    Buff.affect(ch, Roots.class, duration);
                }
                if (Dungeon.level.map[cell] == Terrain.EMPTY ||
                        Dungeon.level.map[cell] == Terrain.EMBERS ||
                        Dungeon.level.map[cell] == Terrain.EMPTY_DECO){
                    Level.set(cell, Terrain.GRASS);
                    GameScene.updateMap(cell);
                }
                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            }
            for (int cell : grassCells){
                int t = Dungeon.level.map[cell];
                if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
                        || t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
                        && Dungeon.level.plants.get(cell) == null){
                    Level.set(cell, Terrain.HIGH_GRASS);
                    GameScene.updateMap(cell);
                }
            }
            Hero.arrangeBlast(hero.pos, hero.sprite, MagicMissile.FOLIAGE_CONE);
            Dungeon.observe();
        });
        Sample.INSTANCE.play(Assets.Sounds.DRINK);
        hero.sprite.emitter().burst(LeafParticle.LEVEL_SPECIFIC, 20);
        charge -= chargeUse();
        updateQuickslot();
        Invisibility.dispel();
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{PotionOfHealing.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = DruidBag.class;
            outQuantity = 1;
        }
    }
}
