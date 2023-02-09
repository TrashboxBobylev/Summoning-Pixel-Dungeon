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

public class MailArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_MAIL;
	}
	
	public MailArmor() {
		super( 3 );
	}

	@Override
	public float defenseLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.5f;
			case 2: return 0.5f;
		}
		return 0f;
	}

	@Override
	public float speedFactor(Char owner, float speed) {
		float speedFactor = super.speedFactor(owner, speed);
		if (level() == 2)
			speedFactor *= 0.7f;
		return speedFactor;
	}
}
