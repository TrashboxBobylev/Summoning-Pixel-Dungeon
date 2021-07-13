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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ChaosSaber;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

public class SwordStorage extends CounterBuff {

    @Override
    public int icon() {
        return BuffIndicator.WEAPON;
    }

    @Override
    public boolean act() {

        Hero hero = (Hero)target;

        Mob closest = null;
        int v = hero.visibleEnemies();
        for (int i=0; i < v; i++) {
            Mob mob = hero.visibleEnemy( i );
            if ( mob.isAlive() && mob.state != mob.PASSIVE && mob.state != mob.WANDERING && mob.state != mob.SLEEPING && !hero.mindVisionEnemies.contains(mob)
                    && (closest == null || Dungeon.level.distance(hero.pos, mob.pos) < Dungeon.level.distance(hero.pos, closest.pos))) {
                closest = mob;
            }
        }

        if (closest != null && count() >= 1){
            //spawn sword
            int bestPos = -1;
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = closest.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                    if (bestPos == -1 || Dungeon.level.trueDistance(p, closest.pos) < Dungeon.level.trueDistance(bestPos, closest.pos)){
                        bestPos = p;
                    }
                }
            }
            if (bestPos != -1) {
                ChaosSaber sword = new ChaosSaber();
                sword.state = sword.HUNTING;
                GameScene.add(sword, -1);
                ScrollOfTeleportation.appear(sword, bestPos);
                countDown(1);

                if (count() <= 0) detach();
            }
        }

        spend(TICK);

        return true;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1f, 1f, 2f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", count());
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.add(CharSprite.State.SWORDS);
        }
        else {
            target.sprite.remove(CharSprite.State.SWORDS);
        }
    }
}
