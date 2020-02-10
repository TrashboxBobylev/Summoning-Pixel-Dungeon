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

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Attunement;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.MagicMissileSprite;
import com.watabou.utils.Random;

public class MagicMissileMinion extends StationaryMinion {
    {
        spriteClass = MagicMissileSprite.class;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
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

    public void zap(){
        spend( 1f );

        if (hit( this, enemy, false )) {
            int dmg = Random.NormalIntRange(minDamage, maxDamage);
            if (Dungeon.hero.buff(Attunement.class) != null) dmg *= Attunement.empowering();
            enemy.damage(dmg, this);

            damage(2, this);
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }
}
