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

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Thief;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.food.FrozenCarpaccio;
import com.trashboxbobylev.summoningpixeldungeon.items.food.MysteryMeat;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.Potion;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfStrength;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Frost extends FlavourBuff {

	private static final float DURATION	= 5f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			
			target.paralysed++;
			Buff.detach( target, Burning.class );
			Buff.detach( target, Chill.class );

			if (target instanceof Hero) {

				Hero hero = (Hero)target;
				ArrayList<Item> freezable = new ArrayList<>();
				//does not reach inside of containers
				for (Item i : hero.belongings.backpack.items){
					if ((i instanceof Potion && !(i instanceof PotionOfStrength))
						|| i instanceof MysteryMeat){
						freezable.add(i);
					}
				}
				
				if (!freezable.isEmpty()){
					Item toFreeze = Random.element(freezable).detach( hero.belongings.backpack );
					GLog.w( Messages.get(this, "freezes", toFreeze.toString()) );
					if (toFreeze instanceof Potion){
						((Potion) toFreeze).shatter(hero.pos);
					} else if (toFreeze instanceof MysteryMeat){
						FrozenCarpaccio carpaccio = new FrozenCarpaccio();
						if (!carpaccio.collect( hero.belongings.backpack )) {
							Dungeon.level.drop( carpaccio, target.pos ).sprite.drop();
						}
					}
				}
				
			} else if (target instanceof Thief) {

				Item item = ((Thief) target).item;

				if (item instanceof Potion && !(item instanceof PotionOfStrength)) {
					((Potion) ((Thief) target).item).shatter(target.pos);
					((Thief) target).item = null;
				} else if (item instanceof MysteryMeat){
					((Thief) target).item = new FrozenCarpaccio();;
				}

			}

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0)
			target.paralysed--;
		if (Dungeon.level.water[target.pos])
			Buff.prolong(target, Chill.class, 4f);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FROST;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.FROZEN);
		else target.sprite.remove(CharSprite.State.FROZEN);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}

	public static float duration( Char ch ) {
		return DURATION;
	}
}
