/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public abstract class ConjurerSpell extends Item {

    public static final String AC_ZAP	= "ZAP";
    public static final String AC_DOWNGRADE = "DOWNGRADE";

    public int manaCost;

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return level() < 2;
    }

    public abstract void effect(Ballistica trajectory);



    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
            actions.add( AC_ZAP );
            actions.add( AC_DOWNGRADE );

        return actions;
    }

    public boolean tryToZap( Hero owner, int target ){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        if (owner.buff(SoulWeakness.class) != null){
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }

        if ( manaCost >= (cursed ? 1 : 1)){
            return true;
        } else {
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }
    }

    @Override
    public String toString() {

        String name = name();
        String tier = "";
        switch (level()){
            case 0: tier = "I"; break;
            case 1: tier = "II"; break;
            case 2: tier = "III"; break;
        }

        name = Messages.format( "%s %s", name, tier  );

        return name;

    }
}
