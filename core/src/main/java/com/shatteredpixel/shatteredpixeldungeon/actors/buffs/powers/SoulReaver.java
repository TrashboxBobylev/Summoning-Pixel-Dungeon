/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2021 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

//dummy buffs is my favorite type of buffs
public class SoulReaver extends DummyBuff {

    public enum Type{
        MELEE,
        DEFENSE,
        MAGIC,
        RANGE
    }

    public Type type;

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo( target )) {
            if (target.sprite != null) target.sprite.emitter().burst(WhiteParticle.UP, 8);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.SACRIFICE;
    }

    @Override
    public void tintIcon(Image icon) {
        switch (type){
            case MELEE:
                icon.tint(1f, 0, 0.1f, 0.5f); break;
            case DEFENSE:
                icon.tint(1f, 0.68f, 0.1f, 0.5f); break;
            case MAGIC:
                icon.tint(0f, 0, 1, 0.5f); break;
            case RANGE:
                icon.tint(0, 1, 0.1f, 0.5f); break;
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("type", type);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        type = bundle.getEnum("type", Type.class);
    }
}
