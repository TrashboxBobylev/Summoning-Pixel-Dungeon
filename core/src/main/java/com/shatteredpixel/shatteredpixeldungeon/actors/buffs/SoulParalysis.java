/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class SoulParalysis extends FlavourBuff {
    public static final float DURATION	= 8f;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo( Char target ) {
        if (super.attachTo( target )) {
            target.paralysed++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0)
            target.paralysed--;
    }

    @Override
    public int icon() {
        return BuffIndicator.SOUL_PARALYSIS;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0.5f, 0, 1, 0.5f);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.PARALYSED);
        else target.sprite.remove(CharSprite.State.PARALYSED);
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static float duration( Char ch ) {
        return DURATION;
    }
}
