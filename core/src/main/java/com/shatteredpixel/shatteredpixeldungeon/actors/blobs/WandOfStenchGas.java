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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StenchHolder;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class WandOfStenchGas extends Blob implements Hero.Doom {

    public int minDamage;
    public int maxDamage;
    private static final String MIN_DMG = "minDamage";
    private static final String MAX_DMG = "maxDamage";

	@Override
	protected void evolve() {
		super.evolve();

		int damage = Random.NormalIntRange(minDamage, maxDamage);

		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0 && (ch = Actor.findChar( cell )) != null) {
					if (!ch.isImmune(this.getClass())) {
						ch.damage(damage, this);
                        Buff.affect(ch, Slow.class, 2f);
					} else if (ch.buff(StenchHolder.class) != null) {
					    StenchHolder stench = ch.buff(StenchHolder.class);
					    minDamage = stench.minDamage;
					    maxDamage = stench.maxDamage;
                    }
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.STENCH_WAND ), 0.4f );
	}

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(MAX_DMG, maxDamage);
        bundle.put( MIN_DMG, minDamage );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        minDamage = bundle.getInt(MIN_DMG);
        maxDamage = bundle.getInt( MAX_DMG );
    }
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
	@Override
	public void onDeath() {
		
		Badges.validateDeathFromGas();
		
		Dungeon.fail( getClass() );
		GLog.negative( Messages.get(this, "ondeath") );
	}
}
