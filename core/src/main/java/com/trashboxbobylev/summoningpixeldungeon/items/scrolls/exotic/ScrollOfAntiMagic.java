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

package com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic;

import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.MagicImmune;
import com.trashboxbobylev.summoningpixeldungeon.effects.Flare;

public class ScrollOfAntiMagic extends ExoticScroll {
	
	{
		initials = 7;
	}
	
	@Override
	public void doRead() {
		
		Invisibility.dispel();
		
		Buff.affect( curUser, MagicImmune.class, 20f );
		new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		
		setKnown();
		
		readAnimation();
	}
}
