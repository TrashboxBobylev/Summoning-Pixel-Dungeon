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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class FoodDebuff extends Buff implements Hero.Doom  {
	
	{
		type = buffType.NEGATIVE;
		announced = true;
		actPriority = HERO_PRIO - 1;
	}

	//food regen always lasts 50 turns
	int left;
	public int fullHP;
    float partialHP;
	
	@Override
	public boolean act() {
		left++;
		if (left > 50){
			detach();
			return true;
		} else {
			partialHP += fullHP / 50f;
			while (partialHP > 1){
				target.damage(1, this);
				partialHP--;
				if (!target.isAlive()){
					Dungeon.hero.die(Dungeon.hero.buff(Hunger.class));
					spend(TICK);
					return true;
				}
			}
		}

		spend(TICK);
		return true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FOOD_DEBUFF;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", fullHP, 51 - left );
	}

	@Override
	public float iconFadePercent() {
		return 1 - (left / 50f);
	}

	@Override
	public void onDeath() {
		Dungeon.fail( getClass() );
		GLog.negative(Messages.get(this, "ondeath"));
	}

	private static final String LEFT = "left";
	private static final String FULLHP = "fullHP";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
		bundle.put(FULLHP, fullHP);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
		fullHP = bundle.getInt(FULLHP);
	}
}
