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

public class SyntheticArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_ADVENTURER;

		bones = false;
	}

	public SyntheticArmor() {
		super( 6 );
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
		if (level() == 1){
			speedFactor /= 2;
		}
		if (level() == 2){
			speedFactor *= 1.15f;
		}
		return speedFactor;
	}



	@Override
	public float evasionFactor(Char owner, float evasion) {
		float evasionFactor = super.evasionFactor(owner, evasion);
		if (level() == 2){
			evasionFactor *= 1.2f;
		}
		return evasionFactor;
	}

	public int DRMax(int lvl){

		int max = 4 + lvl + augment.defenseFactor(lvl);
		if (lvl > max){
			return ((lvl - max)+1)/2;
		} else {
			return max;
		}
	}

	public int DRMin(int lvl){

		int max = DRMax(lvl);
		if (lvl >= max){
			return (lvl + 2 - max);
		} else {
			return lvl + 2;
		}
	}

	public int STRReq(int lvl){
		return 11;
	}

}
