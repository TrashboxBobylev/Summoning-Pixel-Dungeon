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

package com.shatteredpixel.shatteredpixeldungeon.items.powers;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.EnergyOverload;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.MagicPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.MagicalResistance;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MagePower extends Power {
    {
        playerBuff = EnergyOverload.class;
        playerBuffDuration = 10f;
        basicBuff = MagicalResistance.class;
        basicBuffDuration = 8;
        classBuff = MagicPower.class;
        classBuffDuration = 8;
        featuredClass = Minion.MinionClass.MAGIC;
        image = ItemSpriteSheet.SMOKE_BOMB;
    }

    @Override
    protected void affectDungeon() {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                Buff.prolong(mob, Blindness.class, 30f);
                Buff.prolong(mob, Vertigo.class, 30f);
            }
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfRecharging.class, PotionOfShroudingFog.class, ScrollOfTransmutation.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 15;

            output = MagePower.class;
            outQuantity =1;
        }

    }
}
