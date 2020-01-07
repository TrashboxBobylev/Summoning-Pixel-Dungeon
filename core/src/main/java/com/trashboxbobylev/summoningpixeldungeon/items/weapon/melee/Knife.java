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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Cripple;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.FlavourBuff;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroSubClass;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class Knife extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.KNIFE;

		tier = 1;

		bones = false;

	}

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //9 base strength req, down from 10, decreasing with levels even more
        return (7 + tier * 2) - lvl;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +    //5 base, down from 10
                lvl*tier;               //+1 per level, down from +2
    }

    @Override
    public int proc(Char attacker, Char defender, int damage ) {
        Buff.prolong( defender, SoulGain.class, speedFactor(attacker) * 2 );
        return super.proc( attacker, defender, damage );
    }

    @Override
    public float speedFactor(Char owner) {
        float v = super.speedFactor(owner);
        if (((Hero)owner).subClass == HeroSubClass.OCCULTIST) v *= 0.5f;
        return v;
    }

    public static class SoulGain extends FlavourBuff{

    }
}
