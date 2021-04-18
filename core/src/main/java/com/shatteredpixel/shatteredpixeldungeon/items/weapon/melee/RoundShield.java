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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Block;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RoundShield extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ROUND_SHIELD;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 3;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //10 base, down from 20
				lvl*(tier-1);                   //+2 per level, down from +4
	}


	@Override
	public int defenseFactor( Char owner ) {
		return 4+2*buffedLvl();     //4 extra defence, plus 2 per level;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 4+2*buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 4);
		}
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		if (Dungeon.hero.lastMovPos != -1 &&
				Dungeon.level.distance(Dungeon.hero.lastMovPos, enemy.pos) >
						Dungeon.level.distance(Dungeon.hero.pos, enemy.pos) && Dungeon.hero.buff(Block.class) == null){
			Dungeon.hero.lastMovPos = -1;
			//knock out target and get blocking
			Ballistica trajectory = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_TARGET);
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
			WandOfBlastWave.throwChar(enemy, trajectory, 1, true);
			Buff.affect(Dungeon.hero, Block.class, 8f);
		}
		return 0;
	}

	@Override
	public float warriorDelay(float delay, Char enemy) {
		return 1f;
	}
}