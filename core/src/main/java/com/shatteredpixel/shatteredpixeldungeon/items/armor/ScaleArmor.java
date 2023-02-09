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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class ScaleArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_SCALE;
	}
	
	public ScaleArmor() {
		super( 4 );
	}

	@Override
	public float defenseLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0.6f;
			case 2: return 0.2f;
		}
		return 0f;
	}

	@Override
	public float evasionFactor(Char owner, float evasion) {
		float eva = super.evasionFactor(owner, evasion);
		if (level() == 1)
			eva *= 0.66f;
		if (level() == 2)
			eva *= 0.33f;
		return eva;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int proc = super.proc(attacker, defender, damage);
		if (level() == 1) {
			for (int n : PathFinder.NEIGHBOURS8) {
				int cell = defender.pos + n;
				Char ch = Actor.findChar(cell);
				if (ch != null){
					ch.damage(Math.round(proc * 0.75f), defender);
					MagicMissile.boltFromChar(defender.sprite.parent,
							MagicMissile.EARTH,
							defender.sprite,
							cell,
							new Callback() {
								@Override
								public void call() {}
							});
				}
			}
		}
		return proc;
	}
}
