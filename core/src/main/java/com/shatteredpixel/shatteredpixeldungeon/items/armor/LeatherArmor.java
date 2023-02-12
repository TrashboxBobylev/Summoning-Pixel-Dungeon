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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LeatherArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_LEATHER;
	}
	
	public LeatherArmor() {
		super( 2 );
	}

	@Override
	public float stealthFactor(Char owner, float stealth) {
		float sth = super.stealthFactor(owner, stealth);
		if (level() == 1)
			sth = Math.max(1.5f, sth*1.5f);
		return sth;
	}

	@Override
	public float evasionFactor(Char owner, float evasion) {
		float eva = super.evasionFactor(owner, evasion);
		if (level() == 1)
			eva *= 1.5;
		if (level() == 2)
			eva /= 2;
		return eva;
	}

	@Override
	public float defenseLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0.4f;
			case 2: return 0f;
		}
		return 0f;
	}
}
