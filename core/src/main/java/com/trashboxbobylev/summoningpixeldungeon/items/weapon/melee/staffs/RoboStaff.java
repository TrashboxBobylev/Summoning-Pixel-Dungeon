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

import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Chicken;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Robo;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class RoboStaff extends Staff {
    {
        image = ItemSpriteSheet.ROBO_STAFF;
        minionType = Robo.class;
        tier = 4;
        isTanky = true;
        chargeTurns = 600;
    }

    @Override
    public int minionMax(int lvl) {
        return  4*(tier+2) +    //24 base
                lvl*(tier-1);   //+3 scaling
    }

    //heavy minion
    @Override
    public float requiredAttunement() {
        return 1.5f;
    }

    public int hp(int lvl){
        return 30*tier + lvl*(tier+10); // from 32 to 120, and +14
    }
}
