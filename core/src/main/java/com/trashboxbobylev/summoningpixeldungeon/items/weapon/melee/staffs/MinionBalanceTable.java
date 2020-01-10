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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs;

public enum MinionBalanceTable {
    FROGGIT(10, 2, 1, 1,10, 2),
    GREY_RAT(28, 4, 3, 1, 13, 2),
    SLIME(25, 4, 2, 1, 8, 3),
    SHEEP(50, 10, 0, 0, 1, 0),
    SKELETON(70, -10, 3, 1, 6, 1),
    GNOLL_HUNTER(12, 2, 3, 1, 15, 3),
    CHICKEN(2, 1, 3, 1, 20, 3),
    MAGIC_MISSILE(34, 8, 3, 1, 18, 3),
    FROST_ELEMENTAL(64, 10, 4, 2, 30, 7),
    WIZARD(25, 4, 4, 1, 15, 4),
    DARK_ROSE(30, 5, 4, 1, 26, 4),
    ROBOT(140, 30, 4, 4, 25, 4),
    GOO(70, 10, 8, 3, 35, 7),
    GASTER_BLASTER(110, 20, 15, 4, 48, 12),
    IMP_QUEEN(80, 10, 5, 2, 24, 6),
    HACATU(19, 4, 5, 2, 24, 6);

    public int hp;
    public int hp_grow;
    public int min;
    public int min_grow;
    public int max;
    public int max_grow;
    MinionBalanceTable(int hp, int hp_grow, int min, int min_grow, int max, int max_grow){
        this.hp = hp;
        this.hp_grow = hp_grow;
        this.min = min;
        this.max = max;
        this.min_grow = min_grow;
        this.max_grow = max_grow;
    }
}
