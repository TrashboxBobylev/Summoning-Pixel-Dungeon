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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments;

import com.trashboxbobylev.summoningpixeldungeon.ShatteredPixelDungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Unstable extends Weapon.Enchantment {

	private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(  );

	private static Class<?extends Weapon.Enchantment>[] randomEnchants = new Class[]{
			Blazing.class,
			Blocking.class,
			Blooming.class,
			Chilling.class,
			Kinetic.class,
			Corrupting.class,
			Elastic.class,
			Grim.class,
			Lucky.class,
			//projecting not included, no on-hit effect
			Shocking.class,
			Vampiric.class
	};

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		
		int conservedDamage = 0;
		if (attacker.buff(Kinetic.ConservedDamage.class) != null) {
			conservedDamage = attacker.buff(Kinetic.ConservedDamage.class).damageBonus();
			attacker.buff(Kinetic.ConservedDamage.class).detach();
		}
		
		try {
			damage = Random.oneOf(randomEnchants).newInstance().proc( weapon, attacker, defender, damage );
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
		}
		
		return damage + conservedDamage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GREY;
	}
}
