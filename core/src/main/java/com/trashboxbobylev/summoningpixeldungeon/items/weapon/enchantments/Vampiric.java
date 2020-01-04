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

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Healing;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Vampiric extends Weapon.Enchantment {

	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0x660022 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {

	    //vampiric
		//chance to heal scales from 5%-30% based on missing HP
		float missingPercent = (attacker.HT - attacker.HP) / (float)attacker.HT;
        //heals 20% of max HP, but intensifies with levels
        //int healAmt = Math.round(damage * 0.5f);
        //healAmt = Math.min( healAmt, attacker.HT - attacker.HP );
			
        if (attacker.isAlive()) {

            Buff.affect(attacker, Healing.class).setHeal(damage / 4 + weapon.level()*2 + 3, 0.25f, 0);
            //attacker.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );
        }

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return RED;
	}
}
