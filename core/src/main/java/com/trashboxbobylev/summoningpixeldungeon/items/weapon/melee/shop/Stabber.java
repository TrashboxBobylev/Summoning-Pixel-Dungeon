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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop;

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.effects.Wound;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Grim;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Stabber extends MeleeWeapon {
    {
        tier = 4;
        image = ItemSpriteSheet.STABBER;
    }

    @Override
    public int min(int lvl) {
        return tier - 3 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 3 * (tier - 2) + (tier - 2) * lvl; //6, +2
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).state != ((Mob) enemy).HUNTING && ((Mob) enemy).surprisedBy(owner)) {
                //grims non-hunting targets
                Weapon weapon = new Weapon() {
                    @Override
                    public int STRReq(int lvl) {
                        return 0;
                    }

                    @Override
                    public int min(int lvl) {
                        return 0;
                    }

                    @Override
                    public int max(int lvl) {
                        return 0;
                    }
                };
                weapon.upgrade(90);
                weapon.enchant(new Grim());
                weapon.proc(curUser, enemy, enemy.HP - 1);
                Wound.hit(enemy);
                return 0;
            }
        }
        return super.damageRoll(owner);
    }
}
