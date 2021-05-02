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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Robo;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RoboStaff extends Staff {
    {
        image = ItemSpriteSheet.ROBO_STAFF;
        minionType = Robo.class;
        tier = 4;
        isTanky = true;
        setClass(Minion.MinionClass.DEFENSE);
        table = MinionBalanceTable.ROBOT;
    }

    @Override
    public int getChargeTurns() {
        switch (level()){
            case 0: return 675;
            case 1: return 875;
            case 2: return 1175;
        }
        return 0;
    }
}
