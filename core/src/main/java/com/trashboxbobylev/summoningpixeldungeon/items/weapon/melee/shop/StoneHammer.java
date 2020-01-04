/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop;

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Paralysis;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class StoneHammer extends MeleeWeapon {
    {
        image = ItemSpriteSheet.STONE_HAMMER;
        tier = 2;
        DLY = 3.2f;
        ACC = 0.7f;
    }

    @Override
    public int min(int lvl) {
        return  tier*2 +  //2
                lvl*2;    //+2
    }

    @Override
    public int max(int lvl) {
        return  20*(tier) +    //40
                lvl*(tier+5);   //+7
    }

    public int STRReq(int lvl){
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return (9 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Paralysis.class, 4f);
        return super.proc(attacker, defender, damage);
    }
}
