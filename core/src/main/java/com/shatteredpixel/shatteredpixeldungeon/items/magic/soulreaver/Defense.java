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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.ArmoredShielding;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Defense extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_DEFENSE;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch instanceof Minion) {
            if (ch.buff(ArmoredShielding.class) == null) {
                Buff.affect(ch, ArmoredShielding.class, 1000000f);
                Buff.affect(ch, Barkskin.class).set(defenseEarthValue(), defenseEarthTime());
                Buff.affect(ch, ArcaneArmor.class).set(defenseArcaneValue(), defenseArcaneTime());
            } else {
                ch.buff(ArmoredShielding.class).detach();
                Buff.affect(ch, Barkskin.class).detach();
                Buff.affect(ch, ArcaneArmor.class).detach();
                Dungeon.hero.mana = Math.min(Dungeon.hero.maxMana, Dungeon.hero.mana + manaCost());
            }
        }
    }

    private int defenseEarthTime(){
        switch (level()){
            case 1: return 8;
            case 2: return 100;
        }
        return 30;
    }

    private int defenseArcaneTime(){
        switch (level()){
            case 1: return 12;
            case 2: return 120;
        }
        return 40;
    }

    private int defenseEarthValue(){
        switch (level()){
            case 1: return 10 + Dungeon.hero.lvl;
            case 2: return 5;
        }
        return 6 + Dungeon.hero.lvl*3/4;
    }

    private int defenseArcaneValue(){
        switch (level()){
            case 1: return 7 + Dungeon.hero.lvl/2;
            case 2: return 3;
        }
        return 3 + Dungeon.hero.lvl/3;
    }

    @Override
    public int manaCost(){
        switch (level()){
            case 1: return 35;
            case 2: return 5;
        }
        return 30;
    }

    public String desc() {
        return Messages.get(this, "desc", defenseEarthValue(), defenseArcaneValue(), manaCost());
    }
}
