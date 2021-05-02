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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class FoodRegen extends Buff {
	
	{
		type = buffType.POSITIVE;
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
				target.HP = Math.min(target.HP + 1, target.HT);
				partialHP--;
			}
		}
		
		spend(TICK);
		return true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FOOD_REGEN;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", fullHP, 51 - left);
	}

	@Override
	public float iconFadePercent() {
		return 1 - (left / 50f);
	}

	private static final String LEFT = "left";
	private static final String HP = "hp";
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
		//if bundle includes negative leftHP, turn into debuff
		if (bundle.contains(HP)){
			if (bundle.getInt(HP) < 0){
				detach();
				Buff.affect(target, FoodDebuff.class).fullHP = -bundle.getInt(HP);
			}
		}
	}
}
