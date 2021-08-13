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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;

public class HeroicLeap extends Ability {
    {
        baseChargeUse = 25;
        image = ItemSpriteSheet.HEROIC_LEAP;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public void activate(Ability ability, Hero hero, Integer target ) {
        if (target != null) {

            Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
            int cell = route.collisionPos;

            //can't occupy the same cell as another char, so move back one.
            int backTrace = route.dist-1;
            while (Actor.findChar( cell ) != null && cell != hero.pos) {
                cell = route.path.get(backTrace);
                backTrace--;
            }

            charge -= chargeUse();
            updateQuickslot();

            final int dest = cell;
            hero.busy();
            hero.sprite.jump(hero.pos, cell, new Callback() {
                @Override
                public void call() {
                    hero.move(dest);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();

                    WandOfBlastWave.BlastWave.blast(dest);
                    Camera.main.shake(2, 0.5f);

                    Invisibility.dispel();
                    hero.spendAndNext(Actor.TICK);


//                    if (hero.buff(DoubleJumpTracker.class) != null){
//                        hero.buff(DoubleJumpTracker.class).detach();
//                        if (hero.pointsInTalent(Talent.DOUBLE_JUMP) > 2){
//                            Buff.affect(hero, TripleJumpTracker.class, 5);
//                        }
//                    } else
//                    if (hero.canHaveTalent(Talent.DOUBLE_JUMP) && hero.buff(TripleJumpTracker.class) == null) {
//                        Buff.affect(hero, DoubleJumpTracker.class, 5);
//                    } else if (hero.buff(TripleJumpTracker.class) != null){
//                        hero.buff(TripleJumpTracker.class).detach();
//                    }

                }
            });
        }
    }

    public static class DoubleJumpTracker extends FlavourBuff{};
    public static class TripleJumpTracker extends FlavourBuff{};

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Ropes.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = HeroicLeap.class;
            outQuantity = 1;
        }

    }
}
