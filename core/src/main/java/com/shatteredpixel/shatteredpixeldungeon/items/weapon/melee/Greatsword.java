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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Greatsword extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GREATSWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier=5;
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		int conservedDamage = 0;
		if (Dungeon.hero.buff(Kinetic.ConservedDamage.class) != null) {
			conservedDamage = Dungeon.hero.buff(Kinetic.ConservedDamage.class).damageBonus();
			Dungeon.hero.buff(Kinetic.ConservedDamage.class).detach();
		}

		if (damage > enemy.HP){
			int extraDamage = (damage - enemy.HP)*2;

			Buff.affect(Dungeon.hero, Kinetic.ConservedDamage.class).setBonus(extraDamage);
		}

		return damage + conservedDamage;
	}

	@Override
	public float warriorDelay(float delay, Char enemy) {
		return speedFactor(Dungeon.hero);
	}
}
