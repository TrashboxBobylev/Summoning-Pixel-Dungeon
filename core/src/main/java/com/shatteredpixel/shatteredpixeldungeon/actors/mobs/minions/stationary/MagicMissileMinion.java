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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Attunement;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MagicMissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

public class MagicMissileMinion extends StationaryMinion {
    {
        spriteClass = MagicMissileSprite.class;
        baseDefense = 12;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
    }


    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
        if (visible) {
            sprite.zap( enemy.pos );
        } else {
            zap();
        }

        return !visible;
    }

    public void zap() {
        spend(1f);

        if (hit(this, enemy, false)) {
            int dmg = damageRoll();
            if (Dungeon.hero.buff(Attunement.class) != null) dmg *= Attunement.empowering();

            if (lvl > 0) {
                PathFinder.buildDistanceMap(enemy.pos, BArray.not(Dungeon.level.solid, null), lvl);
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        CellEmitter.get(i).burst(MagicMissile.MagicParticle.FACTORY, 8);
                        if (Actor.findChar(i) != null) {
                            Char ch = Actor.findChar(i);
                            if (ch != null && ch != Dungeon.hero && ch != this) {
                                ch.damage(dmg, this);
                            }
                        }
                    }
                }
            } else {
                enemy.damage(dmg, this);
            }
            damage(decay(), this);
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    private int decay(){
        switch (lvl){
            case 1: return 3;
            case 2: return 4;
        }
        return 1;
    }
}
