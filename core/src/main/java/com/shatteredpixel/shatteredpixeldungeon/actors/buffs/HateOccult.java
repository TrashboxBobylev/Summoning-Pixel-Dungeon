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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class HateOccult extends Buff implements Hero.Doom{

    {
        actPriority = VFX_PRIO;
    }

    @Override
    public int icon() {
        return BuffIndicator.HATE;
    }

    public float power = 0;
    public boolean justAdded = true;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("power", power);
        bundle.put("justAdded", justAdded);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        power = bundle.getFloat("power");
        justAdded = bundle.getBoolean("justAdded");
    }

    @Override
    public boolean act() {
        //every 2 turns 20% of hate go away and damage the hero
        //the max speed is 10 hate/period and min speed is 1 hate/period
        if (justAdded){
            justAdded = false;
            spend(TICK*3);
            return true;
        } else {
            float lostPower = GameMath.gate(1f, power/5f, 100f);
            power -= lostPower;
            target.damage(Math.round(lostPower*2f), this);
            target.sprite.emitter().burst(ShadowParticle.UP, 8);
            if (lostPower > 0) target.sprite.showStatus(CharSprite.DEFAULT, "-%s HATE", lostPower);

            Sample.INSTANCE.play(Assets.Sounds.TOMB);

            if (power <= 0){
                detach();
            }
            BuffIndicator.refreshHero();
            spend(TICK*2);
            return true;
        }
    }

    @Override
    public void tintIcon(Image icon) {
        //TODO: hate visual change
    }

    public void gainHate(float n){
        power += n;
        BuffIndicator.refreshHero();
    }

    @Override
    public void onDeath() {
            Dungeon.fail( getClass() );

            GLog.negative( Messages.get(this, "ondeath") );
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", power);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
}
