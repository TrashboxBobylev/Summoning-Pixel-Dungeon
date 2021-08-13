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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashSet;

public class SpectralBlades extends Ability {

    {
        baseChargeUse = 35;
        image = ItemSpriteSheet.RANGE_POWER;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        if (Actor.findChar(target) == hero){
            GLog.warning(Messages.get(this, "self_target"));
            return;
        }

        Ballistica b = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        final HashSet<Char> targets = new HashSet<>();

        int wallPenetration = 1;
        Char enemy = findChar(b, hero, wallPenetration, targets);

        if (enemy == null){
            GLog.warning(Messages.get(this, "no_target"));
            return;
        }

        targets.add(enemy);
            int degrees = 30;
            ConeAOE cone = new ConeAOE(b, degrees);
            for (Ballistica ray : cone.rays){
                // 1/3/5/7/9 up from 0/2/4/6/8
                Char toAdd = findChar(ray, hero, wallPenetration, targets);
                if (toAdd != null && hero.fieldOfView[toAdd.pos]){
                    targets.add(toAdd);
                }
            }

        charge -= chargeUse();
        Item.updateQuickslot();

        Item proto = new Shortsword();

        final HashSet<Callback> callbacks = new HashSet<>();

        for (Char ch : targets) {
            Callback callback = new Callback() {
                @Override
                public void call() {
                    hero.attack( ch );
                    callbacks.remove( this );
                    if (callbacks.isEmpty()) {
                        Invisibility.dispel();
                        hero.spendAndNext( hero.attackDelay() );
                    }
                }
            };

            MissileSprite m = ((MissileSprite)hero.sprite.parent.recycle( MissileSprite.class ));
            m.reset( hero.sprite, ch.pos, proto, callback );
            m.hardlight(0.6f, 1f, 1f);
            m.alpha(0.8f);

            callbacks.add( callback );
        }

        hero.sprite.zap( enemy.pos );
        hero.busy();
    }

    private Char findChar(Ballistica path, Hero hero, int wallPenetration, HashSet<Char> existingTargets){
        for (int cell : path.path){
            Char ch = Actor.findChar(cell);
            if (ch != null){
                if (ch == hero || existingTargets.contains(ch)){
                    continue;
                } else if (ch.alignment != Char.Alignment.ALLY && !(ch instanceof NPC)){
                    return ch;
                } else {
                    return null;
                }
            }
            if (Dungeon.level.solid[cell]){
                wallPenetration--;
                if (wallPenetration < 0){
                    return null;
                }
            }
        }
        return null;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Shuriken.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = SpectralBlades.class;
            outQuantity = 1;
        }

    }
}
