/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.sprites.BlasterSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Random;

public class GasterBlaster extends StationaryMinion {
    {
        spriteClass = BlasterSprite.class;
        attunement = 2f;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.STOP_TERRAIN);
        for (int c : ballistica.subPath(1, viewDistance)) {
            if (c == enemy.pos) return true;
        }
        return false;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*2f;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        spend(attackDelay());
        boolean rayVisible = false;
        Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.STOP_TERRAIN);
        for (int c : ballistica.subPath(1, viewDistance)) {
            if (Dungeon.level.heroFOV[c]) rayVisible = true;
        }

        if (rayVisible){
            sprite.attack(ballistica.collisionPos);
        } else {
            attack(enemy);
        }
        return !rayVisible;
    }

    @Override
    public boolean attack(Char enemy) {
        Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.STOP_TERRAIN);
        for (int c : ballistica.subPath(1, viewDistance)) {
            Char ch = Actor.findChar(c);
            if (ch == null) continue;
            if (hit(this, ch, true)){
                ch.damage(damageRoll(), this);
                if (fieldOfView[pos] || fieldOfView[enemy.pos]){
                    ch.sprite.flash();
                    CellEmitter.center(c).burst(MagicMissile.WhiteParticle.FACTORY, Random.NormalIntRange(3, 8));
                }
            } else {
                ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
            }
        }
        damage(8, this);
        return true;
    }
}
