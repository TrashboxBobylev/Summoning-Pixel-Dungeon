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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public class Regeneration extends Buff {
	
	{
		//unlike other buffs, this one acts after the hero and takes priority against other effects
		//healing is much more useful if you get some of it off before taking damage
		actPriority = HERO_PRIO - 1;
	}
	
	private static final float REGENERATION_DELAY = 10;

	public boolean canHeal(){
		if (target instanceof Hero){
			return !((Hero)target).isStarving() && ((Hero) target).subClass != HeroSubClass.OCCULTIST;
		} else if (target.alignment == Char.Alignment.ENEMY && Dungeon.isChallenged(Conducts.Conduct.REGENERATION)){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {

			if (target.HP < regencap() && canHeal()) {
				LockedFloor lock = target.buff(LockedFloor.class);
				if (target.HP > 0 && (lock == null || lock.regenOn())) {
					target.HP += 1;
					if (target.HP == regencap()) {
						if (target instanceof Hero) ((Hero) target).resting = false;
					}
				}
			}

			RegenerationBuff regenBuff = Dungeon.hero.buff( RegenerationBuff.class);

			float delay = REGENERATION_DELAY;
			if (Dungeon.isChallenged(Conducts.Conduct.KING) && target instanceof Hero) delay /= 2.5f;
			if (target instanceof Mob) delay /= 3;
			if (regenBuff != null && target == Dungeon.hero) {
				if (regenBuff.isCursed()) {
					delay *= 1.5f;
				} else {
					delay *= 1f - 0.09f*regenBuff.itemLevel();
				}
			}
			spend( delay );
			
		} else {
			
			diactivate();
			
		}
		
		return true;
	}
	
	public int regencap(){
		return target.HT;
	}
}
