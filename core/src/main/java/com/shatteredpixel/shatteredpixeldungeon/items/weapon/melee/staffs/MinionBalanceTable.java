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

public enum MinionBalanceTable {
    FROGGIT(15, 3, 8, 1.0f,
            20, 4, 9, 1.5f,
            25, 6, 11, 2.0f),
    GREY_RAT(35, 5, 12, 1.5f,
             23, 10, 16, 1.5f,
             12, 15, 25, 1.5f),
    SLIME(20, 1, 5, 1.0f,
            30, 0, 3, 1.5f,
            32, 0, 0, 2.0f),
    SHEEP(50, 0, 1, 1f,
            50, 0, 1, 2f,
            50, 0, 1, 3f),
    SKELETON(90, 5, 10, 1.0f,
            45, 6, 15, 1.0f,
            20, 7, 20, 1.0f),
    GNOLL_HUNTER(25, 6, 12, 1.0f,
            40, 7, 12, 2.0f,
            66, 9, 12, 5.0f),
    CHICKEN(2, 4, 8, 0.25f,
            2, 3, 6, 0.5f,
            2, 1, 4, 1.0f),
    MAGIC_MISSILE(85, 12, 24, 1.0f,
            125, 12, 24, 3.0f,
            160, 12, 24, 5.0f),
    FROST_ELEMENTAL(70, 18, 30, 1.5f,
            92, 23, 40, 2.0f,
            123, 28, 50, 3.0f),
    WIZARD(35, 4, 15, 2f,
            28, 2, 8, 1.5f,
            19, 0, 5, 0.75f),
    DARK_ROSE(45, 15, 26, 1.5f,
            110, 20, 32, 2.5f,
            10, 7, 10, 1.0f),
    ROBOT(120, 25, 50, 1.5f,
            175, 30, 55, 2.5f,
            225, 35, 60, 4.25f),
    GOO(70, 20, 35, 2.5f,
            80, 15, 26, 3.0f,
            85, 8, 17, 4.25f),
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
