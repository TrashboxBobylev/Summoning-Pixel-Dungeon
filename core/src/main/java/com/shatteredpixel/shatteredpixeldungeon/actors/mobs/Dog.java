/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DogSprite;
import com.watabou.utils.Random;

public class Dog extends Mob {

	{
		spriteClass = DogSprite.class;
		
		HP = HT = 10;
		defenseSkill = 8;
		baseSpeed = 2f;
		
		EXP = 2;
		maxLvl = 6;
		if (SPDSettings.bigdungeon()){
			EXP = 3;
			maxLvl = 9;
		}
		
		loot = Generator.random();
		lootChance = 0.1f;
	}

	public Dog() {
		if (SPDSettings.bigdungeon()){
			EXP = 3;
			maxLvl = 9;
		}
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.75f;
    }
}
