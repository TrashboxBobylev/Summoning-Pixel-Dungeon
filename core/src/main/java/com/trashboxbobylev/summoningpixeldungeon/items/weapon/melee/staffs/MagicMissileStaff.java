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

import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Sheep;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary.MagicMissileMinion;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class MagicMissileStaff extends StationaryStaff {
    {
        image = ItemSpriteSheet.MAGIC_MISSILE_STAFF;
        minionType = MagicMissileMinion.class;
        tier = 3;
    }

    @Override
    public int hp(int lvl) {
        return 20*tier + lvl*(tier+9); //60 hp and +12 for every tier
    }
}
