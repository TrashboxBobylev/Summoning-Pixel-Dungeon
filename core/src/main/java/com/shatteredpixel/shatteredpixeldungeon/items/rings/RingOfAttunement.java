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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfAttunement extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_TENACITY;
	}
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", new DecimalFormat("#.#").format((0.5f * soloBuffedBonus())), new DecimalFormat("#.##").format(100f * (Math.pow(1.08f, soloBonus()) - 1)));
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.#").format(0.5f), new DecimalFormat("#.##").format(8f));
		}
	}

	@Override
	protected RingBuff buff( ) {
		return new Attunement();
	}
	
	public static float damageMultiplier( Char t ){
		return (float)Math.pow(1.08f, getBonus( t, Attunement.class));
	}

	public static float attunementMultiplier( Char t){
        return (float)(0.5f * getBonus(t, Attunement.class));
    }



	public class Attunement extends RingBuff {
	}
}

