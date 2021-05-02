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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class Pike extends MeleeWeapon {
    {
        image = ItemSpriteSheet.SPIKE;
        tier = 3;
        RCH = 2;
        DLY = 1.5f;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(6f*(tier+1)) +    //24 base, up from 20
                lvl*Math.round(1.33f*(tier+1)); //+5 per level, up from +4
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Cripple.class, damage / 9);
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int warriorAttack(int damage, Char enemy) {
        Ballistica tray = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_SOLID);
        ArrayList<Char> affectedChars = new ArrayList<>();
        for (int cell : tray.subPath(1, Integer.MAX_VALUE)){
            Char ch = Actor.findChar(cell);
            if (ch != null) affectedChars.add(ch);
        }
        for (int i = 0; i < affectedChars.size(); i++){
            int dmg = Math.round(damage * (10 - i) * 0.1f);
            dmg -= affectedChars.get(i).drRoll();
            dmg = affectedChars.get(i).defenseProc(Dungeon.hero, dmg);
            dmg = Dungeon.hero.attackProc(affectedChars.get(i), dmg);
            affectedChars.get(i).damage(dmg, Dungeon.hero);
        }
        return 0;
    }
}
