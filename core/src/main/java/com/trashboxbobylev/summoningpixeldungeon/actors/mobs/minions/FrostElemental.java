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

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Fire;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.FrostBurn;
import com.trashboxbobylev.summoningpixeldungeon.sprites.FroggitSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.NewbornElementalSprite;
import com.watabou.utils.Random;

public class FrostElemental extends Minion {
    {
        spriteClass = NewbornElementalSprite.class;
        attunement = 1.5f;
        properties.add(Property.FIERY);
        properties.add(Property.INORGANIC);
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*1.5f;
    }

    @Override
    public int attackProc(Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int( 2 ) == 0) {
            Buff.affect( enemy, FrostBurn.class ).reignite( enemy );
        }

        return damage;
    }

    @Override
    public void add( Buff buff ) {
        if (buff instanceof Burning) {
                damage( Random.NormalIntRange( 1, HT * 2 / 3 ), buff );
        } else {
            super.add( buff );
        }
    }
}
