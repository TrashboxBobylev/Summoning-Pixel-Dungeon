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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Wealth;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TribeGnollSprite;
import com.watabou.utils.Random;

public class TribeGnoll extends Mob {
	
	{
		spriteClass = TribeGnollSprite.class;
		
		HP = HT = 13;
		defenseSkill = 6;
		
		EXP = 2;
		maxLvl = 6;

        loot = Wealth.genConsumableDrop(-3);
		lootChance = 1f;
		properties.add(Property.ANIMAL);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 13;
	}

	@Override
	public int defenseValue() {
		return 3;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			Buff.affect(enemy, Cripple.class, 3f);
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			if (buff.canAttackWithExtraReach( enemy )){
				return true;
			}
		}
		return Dungeon.level.distance(enemy.pos, pos) < 3;
	}
}
