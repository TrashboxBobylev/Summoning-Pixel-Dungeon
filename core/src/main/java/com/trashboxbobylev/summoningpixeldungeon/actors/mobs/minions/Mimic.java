/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.items.Heap;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.MimicSprite;
import com.watabou.noosa.audio.Sample;

public class Mimic extends Minion {
    {
        spriteClass = MimicSprite.class;

        WANDERING = new Wandering();
    }

    @Override
    protected boolean act() {
        boolean a = super.act();

        //teleport any heap into player
        Heap heap = Dungeon.level.heaps.get( pos );
        if (heap != null) {
            heap.pos = Dungeon.hero.pos;
            Item item = heap.pickUp();
            if (item != null) {
                if (item.collect(Dungeon.hero.belongings.backpack)) {
                    GameScene.pickUp(item, Dungeon.hero.pos);
                    Sample.INSTANCE.play(Assets.SND_ITEM);
                    Dungeon.hero.spendAndNext( 0.25f );
                } else {
                    Dungeon.level.drop( item, heap.pos ).sprite.drop();
                }
            }
        }
        return a;
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                enemySeen = true;
                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;
            } else {
                enemySeen = false;
                int oldPos = pos;

                //check for loot in field in view
                boolean lootSeen = false;
                for (int i = 0; i < Dungeon.level.length(); i++) {
                    if (fieldOfView[i] && i != Dungeon.hero.pos) {
                        Heap heap = Dungeon.level.heaps.get(i);
                        if (heap != null) {
                            target = i;
                            lootSeen = true;
                        }
                    }
                }
                if (!lootSeen) target = Dungeon.hero.pos;
                //always move towards the hero when wandering
                if (getCloser(target)) {
                    spend(1 / speed());
                    return moveSprite(oldPos, pos);
                } else {
                    spend(TICK);
                }

            }
            return true;
        }
    }
}
