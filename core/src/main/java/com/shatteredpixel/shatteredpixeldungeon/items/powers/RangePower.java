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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.AdditionalEvasion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.RootingOnShots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SpeedyShots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStamina;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfConfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class RangePower extends Power {
    {
        playerBuff = SpeedyShots.class;
        playerBuffDuration = 10f;
        basicBuff = AdditionalEvasion.class;
        basicBuffDuration = 8;
        classBuff = RootingOnShots.class;
        classBuffDuration = 8;
        featuredClass = Minion.MinionClass.RANGE;
        image = ItemSpriteSheet.RANGE_POWER;
    }

    @Override
    protected void affectDungeon() {
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
        //throws other chars around the center.
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                mob.damage(Math.round(Random.NormalIntRange(1, 6)), this);

                if (mob.isAlive()) {
                    //trace a ballistica to our target (which will also extend past them
                    Ballistica trajectory = new Ballistica(mob.pos, Dungeon.hero.pos, Ballistica.STOP_TARGET);
                    //trim it to just be the part that goes past them
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                    WandOfBlastWave.throwChar(mob, trajectory, 4);
                    Buff.prolong(mob, Roots.class, 5f);
                }
            }
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfConfusion.class, PotionOfStamina.class, ScrollOfTransmutation.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 15;

            output = RangePower.class;
            outQuantity =1;
        }

    }
}
