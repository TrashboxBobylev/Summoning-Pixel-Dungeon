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
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.WandOfStenchGas;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class StenchHolder extends FlavourBuff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public int minDamage = 0;
    public int maxDamage = 0;
    private static final String MIN_DMG = "minDamage";
    private static final String MAX_DMG = "maxDamage";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(MAX_DMG, maxDamage);
        bundle.put( MIN_DMG, minDamage );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        minDamage = bundle.getInt(MIN_DMG);
        maxDamage = bundle.getInt( MAX_DMG );
    }
	
	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
	}

    {
        immunities.add(WandOfStenchGas.class);
    }
	
	@Override
	public int icon() {
		return BuffIndicator.OOZE;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
