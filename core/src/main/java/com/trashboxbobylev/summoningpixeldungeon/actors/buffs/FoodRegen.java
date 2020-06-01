/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.trashboxbobylev.summoningpixeldungeon.actors.buffs;

import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class FoodRegen extends Buff {
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}

	//food regen always lasts 50 turns
	int left;
    int leftHP;
	
	@Override
	public boolean act() {
		left --;
		if (left < 0){
			detach();
			return true;
		} else if (left % (50 / leftHP) == 0){
			target.HP = Math.min(target.HT, target.HP + 1);
		}
		
		spend(TICK);
		return true;
	}

	public void reset(){
	    left = 50;
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
		return Messages.get(this, "desc", left + 1, leftHP);
	}
	
	private static final String LEFT = "left";
	private static final String HP = "hp";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
		bundle.put(HP, leftHP);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
		leftHP = bundle.getInt(HP);
	}
}
