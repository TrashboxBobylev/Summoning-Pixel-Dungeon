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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class PerfumeGas extends Blob {

	@Override
	protected void evolve() {
		super.evolve();

		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0 && (ch = Actor.findChar( cell )) != null) {
					if (!ch.isImmune(this.getClass())) {
						Buff.prolong(ch, Affection.class, 2);
					}
				}
			}
		}
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.PERFUME, true ), 0.3f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	public static class Affection extends FlavourBuff {

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.ALLURED);
            else target.sprite.remove(CharSprite.State.ALLURED);
        }

        @Override
        public int icon() {
            return BuffIndicator.HEART;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (2 - visualcooldown()) / 2);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns());
        }
    }

    public static class Aggression extends FlavourBuff{

        {
            type = buffType.NEGATIVE;
            announced = true;
        }



        @Override
        public int icon() {
            return BuffIndicator.RAGE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.tint(1, 0, 0, 0.5f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (8 - visualcooldown()) / 8);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns());
        }

        {
            immunities.add(Affection.class);
        }
    }
}