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

package com.trashboxbobylev.summoningpixeldungeon.items.spells;

import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Adrenaline;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Amok;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.items.quest.MetalShard;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRage;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;

public class Enrage extends TargetedSpell {

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        int pos = bolt.collisionPos;
        Char ch = Actor.findChar(pos);
        if (ch != null){
            Buff.affect(ch, Amok.class, 15f);
            Buff.affect(ch, Adrenaline.class, 5f);
        }
    }

    public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfRage.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = Enrage.class;
            outQuantity = 4;
        }

    }
}
