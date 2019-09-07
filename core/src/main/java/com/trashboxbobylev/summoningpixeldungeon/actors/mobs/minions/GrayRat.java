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

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.summoningpixeldungeon.sprites.FroggitSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.GrayRatSprite;
import com.watabou.utils.Random;

public class GrayRat extends Minion {
    {
        spriteClass = GrayRatSprite.class;
    }

    //his fur are invulnerable to common hazards
    {
        immunities.add( Burning.class );
        immunities.add( Blizzard.class );
        immunities.add( CorrosiveGas.class );
        immunities.add( Fire.class );
        immunities.add( Freezing.class );
        immunities.add( Inferno.class );
        immunities.add( StenchGas.class );
        immunities.add( ToxicGas.class );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }
}
