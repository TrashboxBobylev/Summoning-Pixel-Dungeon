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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class Drill extends Ability {
    {
        baseChargeUse = 40;
        image = ItemSpriteSheet.DRILL;
        setArtifactClass(ArtifactClass.UTILITY);
    }

    @Override
    public float chargeUse() {
        if (level() > 0){
            return 25;
        }
        return super.chargeUse();
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Char mob = Actor.findChar(target);

        if (mob != null || target == hero.pos || !Dungeon.level.heroFOV[target]){
            GLog.warning(Messages.get(this, "no_target"));
            return;
        }

        int tile = Dungeon.level.map[target];
        final int[] newTile = {Terrain.EMPTY};
        if (canMine(tile, target)){
            Dungeon.hero.sprite.zap(target, () -> {
                switch (tile){
                    case Terrain.WALL: case Terrain.STATUE: case Terrain.BARRICADE: case Terrain.BOOKSHELF:
                        newTile[0] = Terrain.EMPTY; break;
                    default:
                        newTile[0] = Terrain.CHASM; break;
                }

                Level.set(target, newTile[0]);
                GameScene.updateMap(target);
                Dungeon.observe();
                CellEmitter.get(target).burst(Speck.factory(Speck.DUST), 4);
                Sample.INSTANCE.play(Assets.Sounds.EVOKE, 1, 0.8f);
                Sample.INSTANCE.play(Assets.Sounds.ROCKS, 0.8f, 0.8f);
                charge -= chargeUse();
                updateQuickslot();
                hero.spendAndNext(Actor.TICK*2);
            });
        } else {
            GLog.warning(Messages.get(this, "no_target"));
        }
    }

    public boolean canMine(int tile, int pos){
        if (Dungeon.level.solid[pos] && level() == 1) return false;
        if (Dungeon.level.passable[pos] && level() == 2) return false;
        if (tile == Terrain.EXIT || tile == Terrain.LOCKED_DOOR
                || tile == Terrain.ENTRANCE || tile == Terrain.AVOID ||
            tile == Terrain.CHASM || tile == Terrain.UNLOCKED_EXIT || tile == Terrain.ALCHEMY) return false;
        if (!(Dungeon.level instanceof RegularLevel)) return false;
        for (int i : PathFinder.NEIGHBOURS4){
            if (Dungeon.level.solid[pos+i] && Dungeon.level.map[pos+i] == Terrain.LOCKED_DOOR) return false;
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            Room room = ((RegularLevel) Dungeon.level).room(pos+i);
            if (room instanceof SpecialRoom) {
                int door = Dungeon.level.pointToCell(
                        new Point(((SpecialRoom) room).entrance().x, ((SpecialRoom) room).entrance().y));
                return Dungeon.level.map[door] != Terrain.LOCKED_DOOR;
            }
        }
        return true;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Bomb.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = Drill.class;
            outQuantity = 1;
        }

    }
}
