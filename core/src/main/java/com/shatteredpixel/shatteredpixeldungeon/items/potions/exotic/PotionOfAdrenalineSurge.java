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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfAdrenalineSurge extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_ARENSURGE;

		unique = true;
	}
	
	@Override
	public void apply(Hero hero) {
		setKnown();
		SurgeTracker buff = Buff.affect(hero, SurgeTracker.class);
		buff.countUp(1);
		GLog.positive( Messages.get(this, "msg", buff.count() > 4 ? buff.count() / 3 : buff.count()) );
	}

	public static class SurgeTracker extends CounterBuff{
		{
			announced = false;
		}

		@Override
		public int icon() {
			return BuffIndicator.FURY;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", damageAmount());
		}

		public int damageAmount(){
			int damage = Math.round(count()*(1 + count())/2);
			if (damage > 10){
				damage = 10 + Math.round((count()*(1 + count())/2 - 10)/3);
			}
			return damage;
		}
	}
	
}
