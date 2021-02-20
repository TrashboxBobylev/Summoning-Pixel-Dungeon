/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SheepStaff extends Staff {
    {
        image = ItemSpriteSheet.WOOLY_STAFF;
        minionType = Sheep.class;
        tier = 2;
        isTanky = true;
        chargeTurns = 500;
        setClass(Minion.MinionClass.DEFENSE);
        table = MinionBalanceTable.SHEEP;
    }

    @Override
    public void customizeMinion(Minion minion) {
        minion.baseMinDR = 1 + level();
        minion.baseMaxDR = 6 + level()*7;
    }
}
