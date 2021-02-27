/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

import java.text.DecimalFormat;

public class SoulCache extends Buff {

    {
        announced = false;
        type = buffType.POSITIVE;
    }

    private float health;
    private float soul;

    private final String HEALTH = "health";
    private final String SOUL = "soul";

    public static void investHealth(float health){
        Buff.affect(Dungeon.hero, SoulCache.class).health += health;
    }

    public static void investSoul(float soul){
        Buff.affect(Dungeon.hero, SoulCache.class).soul += soul;
    }

    @Override
    public int icon() {
        return BuffIndicator.CON;
    }

    @Override
    public boolean act() {
        if (health == 0 && soul == 0){
            detach();
            return true;
        } else {
            while (health >= 1){
                health -= 1;
                target.HP = Math.min(target.HT, target.HP+1);
                target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
            }
            while (soul >= 1){
                soul -= 1;
                Dungeon.hero.mana = Math.min(Dungeon.hero.maxMana, Dungeon.hero.mana+1);
                target.sprite.emitter().burst(Speck.factory(Speck.STEAM), 6);
            }
        }

        spend(TICK);
        return true;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#.##").format(health), new DecimalFormat("#.##").format(soul));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEALTH, health);
        bundle.put(SOUL, soul);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        health = bundle.getFloat(HEALTH);
        soul = bundle.getFloat(SOUL);
    }
}
