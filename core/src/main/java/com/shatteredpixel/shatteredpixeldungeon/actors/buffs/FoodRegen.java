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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class FoodRegen extends Buff {
	
	{
		type = buffType.POSITIVE;
		announced = true;
		actPriority = 1;
	}

	//food regen always lasts 50 turns
	int left;
    public int leftHP;
	
	@Override
	public boolean act() {
		left --;
		if (left < 0){
			detach();
			return true;
		} else if (left % (50 / Math.abs(leftHP)) == 0){
			target.HP = Math.min(target.HT, target.HP + (leftHP > 0 ? 1 : -1));
			if (target.HP == 0){
			    Dungeon.hero.die(Hunger.class);
            }
		}
		
		spend(TICK);
		return true;
	}

	public void reset(){
	    left = 52;
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
