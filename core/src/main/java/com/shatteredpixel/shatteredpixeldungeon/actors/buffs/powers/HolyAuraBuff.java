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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class HolyAuraBuff extends DummyBuff {
    @Override
    public int icon() {
        return BuffIndicator.CON;
    }

    public int healingRate;
    public int shieldingRate;
    public int minDamage;
    public int maxDamage;
    public int cd;
    public int manaSteal;

    @Override
    public void detach() {
        GameScene.flash( 0xFFFFFF );
        Sample.INSTANCE.play( Assets.Sounds.BLAST );

        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos] && mob.alignment == Char.Alignment.ENEMY) {
                mob.damage(Random.NormalIntRange(minDamage, maxDamage), this);
                if (mob.properties().contains(Char.Property.UNDEAD)) mob.damage(Math.round(Random.NormalIntRange(minDamage, maxDamage)*0.33f), this);
                if (mob.isAlive()) {
                    Buff.prolong(mob, Blindness.class, Math.round(4));
                    Buff.prolong(mob, Terror.class, Math.round(6)).object = target.id();
                }
            }
        }

        Buff.affect(target, HolyAuraCD.class, cd);

        super.detach();
    }

    private static final String HEAL	= "heal";
    private static final String SHIELD	= "shield";
    private static final String MIN_D	= "minDamage";
    private static final String MAX_D	= "maxDamage";
    private static final String CD  	= "cool_down";
    private static final String MANA	= "manaSteal";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEAL, healingRate);
        bundle.put(SHIELD, shieldingRate);
        bundle.put(MIN_D, minDamage);
        bundle.put(MAX_D, maxDamage);
        bundle.put(CD, cd);
        bundle.put(MANA, manaSteal);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        healingRate = bundle.getInt(HEAL);
        shieldingRate = bundle.getInt(SHIELD);
        minDamage = bundle.getInt(MIN_D);
        maxDamage = bundle.getInt(MAX_D);
        cd = bundle.getInt(CD);
        manaSteal = bundle.getInt(MANA);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.AURA);
        else target.sprite.remove(CharSprite.State.AURA);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", healingRate, shieldingRate, manaSteal, minDamage, maxDamage, dispTurns());
    }

}
