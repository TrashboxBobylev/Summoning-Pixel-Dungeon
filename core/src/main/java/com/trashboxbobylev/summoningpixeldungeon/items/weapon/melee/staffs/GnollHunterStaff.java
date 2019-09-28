/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs;

import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.GnollHunter;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.GrayRat;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class GnollHunterStaff extends Staff{
    {
        image = ItemSpriteSheet.GNOLL_HUNTER_STAFF;
        minionType = GnollHunter.class;
        tier = 2;
    }

    @Override
    public int minionMax(int lvl) {
        return  (4*(tier)) +    //8 base, down from 12
                lvl*tier;               //+2
    }

    public int hp(int lvl){
        return 4*tier + lvl*(tier) - 1; // from 15 to 8, and scaling unchanged
    }
}
