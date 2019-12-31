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

package com.trashboxbobylev.summoningpixeldungeon.items.powers;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Amok;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.AdditionalDefense;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.SpikyShield;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.TankHeal;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRage;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.ArcaneCatalyst;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.Enrage;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;

public class WarriorPower extends Power {

    {
        playerBuff = SpikyShield.class;
        playerBuffDuration = 20f;
        basicBuff = AdditionalDefense.class;
        basicBuffDuration = 10;
        classBuff = TankHeal.class;
        classBuffDuration = 1;
        featuredClass = Minion.MinionClass.DEFENSE;
        image = ItemSpriteSheet.WARRIOR_POWER;
    }

    @Override
    protected void affectDungeon() {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.prolong(mob, Amok.class, 5f);
                mob.beckon(Dungeon.hero.pos);
            }
        }
    }

    public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfRage.class, PotionOfHealing.class, ScrollOfTransmutation.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 15;

            output = WarriorPower.class;
            outQuantity =1;
        }

    }
}
