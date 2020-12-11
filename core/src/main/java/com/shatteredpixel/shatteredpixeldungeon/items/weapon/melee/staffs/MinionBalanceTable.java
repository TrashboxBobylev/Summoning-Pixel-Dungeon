/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs;

public enum MinionBalanceTable {
    FROGGIT(10, 4, 10, 1.0f,
            16, 6, 14, 1.5f,
            21, 9, 18, 2.0f),
    GREY_RAT(28, 3, 13, 1.0f,
             25, 4, 15, 1.0f,
             23, 5, 19, 1.25f),
    SLIME(20, 1, 5, 1.0f,
            30, 0, 3, 1.5f,
            32, 0, 0, 2.0f),
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
    GASTER_BLASTER(80, 12, 15, 5, 50, 15),
    IMP_QUEEN(120, 5, 5, 2, 30, 5),
    HACATU(50, 8, 5, 3, 29, 4);

    public int hp1;
    public int hp2;
    public int hp3;
    public int min1;
    public int min2;
    public int min3;
    public int max1;
    public int max2;
    public int max3;
    public float att1;
    public float att2;
    public float att3;
    MinionBalanceTable(int hp1, int min1, int max1, float att1,
                        int hp2, int min2, int max2, float att2,
                         int hp3, int min3, int max3, float att3){
        this.hp1 = hp1; this.min1 = min1; this.max1 = max1; this.att1 = att1;
        this.hp2 = hp2; this.min2 = min2; this.max2 = max2; this.att2 = att2;
        this.hp3 = hp3; this.min3 = min3; this.max3 = max3; this.att3 = att3;
    }
    MinionBalanceTable(int hp, int hp_grow, int min, int min_grow, int max, int max_grow){
        this.hp1 = hp; this.min1 = min; this.max1 = max; this.att1 = 1;
        this.hp2 = hp + hp_grow; this.min2 = min + min_grow; this.max2 = max; this.att2 = 2;
        this.hp3 = hp + hp_grow * 2; this.min3 = min + min_grow * 2; this.max3 = max + max_grow * 2; this.att3 = 3;
    }
}
