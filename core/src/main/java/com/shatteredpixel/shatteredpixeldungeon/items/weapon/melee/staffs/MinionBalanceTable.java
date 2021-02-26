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
    FROGGIT(10, 5, 10, 1.0f,
            17, 8, 14, 1.5f,
            23, 11, 18, 2.0f),
    GREY_RAT(33, 6, 15, 1.0f,
             27, 9, 17, 1.5f,
             20, 13, 24, 2.25f),
    SLIME(20, 1, 5, 1.0f,
            30, 0, 3, 1.5f,
            32, 0, 0, 2.0f),
    SHEEP(50, 0, 1, 1.0f,
            50, 0, 1, 2.25f,
            50, 0, 1, 3.5f),
    SKELETON(75, 4, 8, 1.0f,
            35, 2, 6, 1.25f,
            10, 1, 4, 2f),
    GNOLL_HUNTER(25, 4, 12, 1.0f,
            30, 4, 12, 2.0f,
            35, 4, 12, 5.0f),
    CHICKEN(2, 3, 20, 0.50f,
            2, 1, 17, 0.75f,
            2, 1, 10, 1.25f),
    MAGIC_MISSILE(34, 3, 18, 1.0f,
            36, 3, 25, 3.0f,
            40, 4, 36, 4.0f),
    FROST_ELEMENTAL(64, 4, 30, 1.5f,
            84, 6, 40, 2.0f,
            112, 8, 50, 3.0f),
    WIZARD(25, 4, 15, 1.5f,
            18, 2, 8, 1.0f,
            9, 0, 5, 0.75f),
    DARK_ROSE(30, 4, 26, 1.5f,
            90, 6, 32, 2.5f,
            10, 1, 10, 1.0f),
    ROBOT(150, 4, 40, 1.5f,
            200, 5, 45, 2.25f,
            275, 6, 50, 4f),
    GOO(70, 8, 35, 2.0f,
            80, 4, 26, 2.0f,
            85, 1, 17, 2.25f),
    GASTER_BLASTER(80, 15, 60, 2,
            90, 12, 40, 2.25f,
            100, 10, 34, 2.75f),
    IMP_QUEEN(120, 5, 30, 3.0f,
            60, 2, 23, 2.0f,
            20, 1, 8, 1.0f),
    HACATU(50, 6, 30, 2.0f,
            90, 12, 60, 5.0f,
            125, 24, 120, 12.0f);

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
