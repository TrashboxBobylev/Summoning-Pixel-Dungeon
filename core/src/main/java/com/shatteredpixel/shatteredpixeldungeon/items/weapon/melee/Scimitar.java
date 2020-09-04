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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Scimitar extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SCIMITAR;

		tier = 3;
		DLY = 0.9f; //1.11x speed
	}

	public int strikes;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("strikes", strikes);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        strikes = bundle.getInt("strikes");
    }

    @Override
    public int min(int lvl) {
        return tier + lvl/2;
    }

    @Override
	public int max(int lvl) {
		return  5*(tier) +    //15 base, down from 20
				lvl*(tier);   //+3 instead of +4
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (++strikes == 4) {
            damage *= 2;
            defender.sprite.showStatus(CharSprite.WARNING, "crit!");
            strikes = 0;
        }

        return super.proc(attacker, defender, damage);
    }
}
