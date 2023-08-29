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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GrayRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GreyRatStaff extends Staff {
    {
        image = ItemSpriteSheet.GREY_RAT_STAFF;
        minionType = GrayRat.class;
        tier = 2;
        setClass(Minion.MinionClass.MELEE);
        table = MinionBalanceTable.GREY_RAT;
    }

    @Override
    public void customizeMinion(Minion minion) {
        switch (level()){
            case 0: minion.baseDefense = 2; break;
            case 1: minion.baseDefense = 8; break;
            case 2: minion.baseDefense = 11; break;
        }
    }
}
