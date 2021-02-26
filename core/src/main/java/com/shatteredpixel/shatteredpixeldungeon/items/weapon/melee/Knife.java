/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.WhiteWound;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Knife extends MeleeWeapon {

    public boolean ranged;
	
	{
		image = ItemSpriteSheet.KNIFE;

		tier = 1;

		bones = false;

		defaultAction = AC_THROW;

	}

    @Override
    public int max(int lvl) {
        return  8*(tier) +    //8 base, down from 10
                (lvl+1)*tier;               //same scaling
    }

    @Override
    public int proc(Char attacker, Char defender, int damage ) {
	    int modifier = ranged ? 7 : 4;
        Buff.prolong( defender, SoulGain.class, speedFactor(attacker) * modifier );
        WhiteWound.hit(defender);
        return super.proc( attacker, defender, damage );
    }

    @Override
    public void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null || enemy == curUser || curUser.buff(SoulWeakness.class) != null) {
            super.onThrow(cell);
        } else {
            if (!curUser.shoot(enemy, this)) {
                super.onThrow(cell);
            } else {
                Dungeon.level.drop( this, cell ).sprite.drop();
            }
        }
    }

    @Override
    public float speedFactor(Char owner) {
        float v = super.speedFactor(owner);
        if (Dungeon.hero.subClass == HeroSubClass.OCCULTIST) v *= 0.7f;
        return v;
    }

    public static class SoulGain extends FlavourBuff{
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SPIRIT);
            else target.sprite.remove(CharSprite.State.SPIRIT);
        }
    }
}
