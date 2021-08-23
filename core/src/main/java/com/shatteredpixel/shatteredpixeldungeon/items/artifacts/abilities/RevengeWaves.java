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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

public class RevengeWaves extends Ability {

    {
        image = ItemSpriteSheet.REVENGE_WAVES;
        baseChargeUse = 50;
    }

    @Override
    public float chargeUse() {
        switch (level()){
            case 1: return 70;
            case 2: return 100;
        }
        return super.chargeUse();
    }

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
        Camera.main.shake(4, 0.5f);
        //throws other chars around the center.
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                if (mob.isAlive()) {
                    //trace a ballistica to our target (which will also extend past them
                    Ballistica trajectory = new Ballistica(Dungeon.hero.pos, mob.pos, Ballistica.WONT_STOP);
                    trajectory = new Ballistica(mob.pos, trajectory.collisionPos, Ballistica.FRIENDLY_MAGIC);
                    WandOfBlastWave.throwChar(mob, trajectory, level() == 2 ? 999 : 3, true, level() != 2);
                    if (level() == 2) Buff.prolong(mob, SoulParalysis.class, 1f);
                    else Buff.prolong(mob, Roots.class, 6f);
                    if (level() == 1) Buff.prolong(hero, Empowered.class, 2.5f);
                }
            }
        }

        charge -= chargeUse();
        updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(Actor.TICK);
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{StoneOfBlast.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = RevengeWaves.class;
            outQuantity = 1;
        }

    }
}
