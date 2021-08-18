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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ArcaneElement extends Ability {
    {
        baseChargeUse = 25;
        image = ItemSpriteSheet.ARCANE_ELEMENT;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse() {
        if (Dungeon.hero.belongings.weapon instanceof MeleeWeapon &&
                ((MeleeWeapon) Dungeon.hero.belongings.weapon).hasEnchant(Grim.class, Dungeon.hero))
            return 100;
        return super.chargeUse();
    }

    public static class ArcaneTracker extends Buff{};

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Char mob = Actor.findChar(target);

        if (mob == null || !Dungeon.hero.canAttack(mob)){
            GLog.warning(Messages.get(this, "no_target"));
            return;
        }

        Buff.affect(hero, ArcaneTracker.class);

        Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
        hero.sprite.emitter().burst(Speck.factory(Speck.CONFUSION), 10);
        hero.setEnemy(mob);
        hero.sprite.attack(target);

        charge -= chargeUse();
        updateQuickslot();
        Invisibility.dispel();
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{StoneOfEnchantment.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = ArcaneElement.class;
            outQuantity = 1;
        }

    }
}
