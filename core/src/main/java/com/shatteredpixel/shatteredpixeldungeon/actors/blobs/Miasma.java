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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.watabou.utils.Random;

import java.util.HashMap;

public class Miasma extends Blob {

    private static final HashMap<Class<? extends FlavourBuff>, Float> MINOR_DEBUFFS = new HashMap<>();
    static{
        MINOR_DEBUFFS.put(Weakness.class,       1f);
        MINOR_DEBUFFS.put(Cripple.class,        1f);
        MINOR_DEBUFFS.put(Blindness.class,      1f);
        MINOR_DEBUFFS.put(Terror.class,         1f);

        MINOR_DEBUFFS.put(Chill.class,          1f);
        MINOR_DEBUFFS.put(Roots.class,          1f);
        MINOR_DEBUFFS.put(Vertigo.class,        1f);
        MINOR_DEBUFFS.put(Paralysis.class, 1f);
        MINOR_DEBUFFS.put(Slow.class, 1f);
    }

	@Override
	protected void evolve() {

		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

				if (off[cell] > 0) {

					volume += off[cell];

					Char ch = Actor.findChar( cell );

					if (ch != null && !ch.isImmune(this.getClass())) {
                        Class<?extends FlavourBuff> debuffCls = Random.chances(MINOR_DEBUFFS);
                        Buff.affect(ch, debuffCls, 5);
                        if (Random.Float() < 0.75) {
                            switch(Random.Int(4)) {
                                case 0: Buff.affect(ch, Poison.class).set(3 + (Dungeon.depth / 3) * 5 / Dungeon.chapterSize()); break;
                                case 1: Buff.affect( ch, Burning.class ).reignite( ch ); break;
                                case 2: Buff.affect(ch, Ooze.class).set( 20f ); break;
                                case 3: Buff.affect(ch, Corrosion.class).set(10f, Dungeon.depth/3 * 5 / Dungeon.chapterSize()); break;
                            }
                        }
                    }
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( GooSprite.GooParticle.FACTORY, 0.06f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
