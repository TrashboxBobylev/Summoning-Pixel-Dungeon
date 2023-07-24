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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class MomentumBoots extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_MOMENTUM;
        levelCap = 4;
        setArtifactClass(ArtifactClass.UTILITY);
    }

    public static momentumBuff instance = null;

    @Override
    protected ArtifactBuff passiveBuff() {
        momentumBuff buff = new momentumBuff();
        if (!cursed)
            instance = buff;
        return buff;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (target.buff(Momentum.class) != null){
            Momentum momentum = target.buff(Momentum.class);
            momentum.freerunCooldown = Math.max(0, momentum.freerunCooldown-2);
            if (momentum.freerunCooldown <= 0){
                momentum.momentumStacks = Math.min(momentum.momentumStacks+1, momentum.getMaxMomentum());
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if ( isEquipped( Dungeon.hero ) ){
            if (!cursed) {
                desc += "\n\n" + Messages.get(this, "desc_worn");

            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        boolean unequip = super.doUnequip(hero, collect, single);
        Buff.detach(hero, Momentum.class);
        instance = null;
        return unequip;
    }

    public class momentumBuff extends ArtifactBuff{
        public void getExp(float experience){
            exp += experience;
            if (exp >= 60 + itemLevel()*45 && level() < levelCap){
                exp -= 60 + itemLevel()*45;
                upgrade();
                GLog.positive(Messages.get(MomentumBoots.class, "level_up"));
            }
        }
    }
}
