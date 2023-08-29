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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Jjango extends MeleeWeapon {
    {
        image = ItemSpriteSheet.JJANGO;
        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return 4*(tier - 1) +
                lvl*(tier-2); //16, +3
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect( defender, Bleeding.class ).set( Math.round(damage*0.85f) );
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int warriorAttack(int damage, Char enemy) {
        //steal health, if char is bleeding
        if (enemy.buff(Bleeding.class) != null){
            Bleeding blood = enemy.buff(Bleeding.class);
            float bloodAmount = 0;
            while (Math.round(blood.level()) > 0){
                float amt = Math.round(Random.NormalFloat(blood.level() * 0.5f, blood.level()*0.75f));
                blood.setForcefully(amt);
                bloodAmount += amt;
            }
            blood.detach();
            if (Dungeon.mode != Dungeon.GameMode.HELL) {
                Regeneration.regenerate(Dungeon.hero, (int) bloodAmount);
                Dungeon.hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
            }
        }
        return 0;
    }
}
