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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public abstract class DummyBuff extends FlavourBuff {
    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public int duration;

    private static final String DURATION = "duration";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DURATION, duration);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        duration = bundle.getInt(DURATION);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (duration - visualcooldown()) / duration);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    @Override
    protected void spend(float time) {
        super.spend(time);
        duration = (int) cooldown();
    }
}
