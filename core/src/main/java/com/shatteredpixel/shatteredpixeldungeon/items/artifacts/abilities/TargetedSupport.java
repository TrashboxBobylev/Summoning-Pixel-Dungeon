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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class TargetedSupport extends Ability {
    {
        image = ItemSpriteSheet.TARGET_SUPPORT;
        baseChargeUse = 25;
    }

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Char minion = Actor.findChar(target);

        if (!(minion instanceof Minion)){
            GLog.warning(Messages.get(this, "no_target"));
            return;
        }

        Class<? extends FlavourBuff> buff = null;
        switch (((Minion) minion).minionClass){
            case DEFENSE:
                buff = TankHeal.class; break;
            case RANGE:
                buff = RootingOnShots.class; break;
            case MELEE:
                buff = AdditionalDamage.class; break;
            case MAGIC:
                buff = MagicPower.class; break;
            case SUPPORT:
                buff = SupportPower.class; break;
        }

        Class<? extends FlavourBuff> finalBuff = buff;
        MagicMissile.boltFromChar(hero.sprite.parent,
                MagicMissile.BEACON,
                hero.sprite,
                minion.pos,
                () -> {
                    Buff.affect(minion, finalBuff, 10);
                });
        Sample.INSTANCE.play(Assets.Sounds.ZAP);

        charge -= chargeUse();
        Item.updateQuickslot();
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfAttunement.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = TargetedSupport.class;
            outQuantity = 1;
        }

    }
}
