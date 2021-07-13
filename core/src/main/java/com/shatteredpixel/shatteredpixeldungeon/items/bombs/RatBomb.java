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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExplodingTNT;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RatBomb extends Bomb{
    {
        image = ItemSpriteSheet.FLASHBANG;
        fuseDelay = 2;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        Level l = Dungeon.level;
        for (Char ch : Actor.chars()){
            if (ch.fieldOfView != null && ch.fieldOfView[cell]){
                int power = 25 - 8*l.distance(ch.pos, cell);
                if (power > 0){
                    if (ch instanceof Mob && !(ch instanceof ExplodingTNT)){
                        Buff.prolong(ch, Blindness.class, power);
                        Buff.prolong(ch, Cripple.class, power);
                        ((Mob) ch).enemy = null;
                        ((Mob) ch).enemySeen = false;
                        ((Mob) ch).state = ((Mob) ch).WANDERING;
                    }
                }
                if (ch == Dungeon.hero){
                    GameScene.flash(0xFFFFFF);
                }
            }
        }

    }

    @Override
    public String desc() {
        String desc_fuse = Messages.get(this, "desc",
                Math.round(minDamage()*0.8*0.4), Math.round(maxDamage()*0.8*0.4))+ "\n\n" + Messages.get(this, "desc_fuse");
        if (fuse != null){
            desc_fuse = Messages.get(this, "desc",
                    Math.round(minDamage()*0.8*0.4), Math.round(maxDamage()*0.8*0.4)) + "\n\n" + Messages.get(this, "desc_burning");
        }

        return desc_fuse;
    }

    @Override
    public boolean doPickUp(Hero hero) {
        return false;
    }
}
