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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.AdditionalDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.FierySlash;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.GuaranteedEnchant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RoguePower extends Power {
    {
        playerBuff = FierySlash.class;
        playerBuffDuration = 12f;
        basicBuff = AdditionalDamage.class;
        basicBuffDuration = 10;
        classBuff = GuaranteedEnchant.class;
        classBuffDuration = 5;
        featuredClass = Minion.MinionClass.MELEE;
        image = ItemSpriteSheet.HEROIC_LEAP;
    }

    @Override
    protected void affectDungeon() {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                if (Dungeon.hero.belongings.weapon != null){
                    Dungeon.hero.belongings.weapon.proc(Dungeon.hero, mob, Dungeon.hero.belongings.weapon.damageRoll(Dungeon.hero)*2);
                }
            }
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfMirrorImage.class, ScrollOfEnchantment.class, ScrollOfTransmutation.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 15;

            output = RoguePower.class;
            outQuantity =1;
        }

    }
}
