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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

public class Roots extends FlavourBuff {

	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		severity = buffSeverity.HARMFUL;
		announced = true;
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (!target.flying && super.attachTo( target )) {
			target.rooted = true;
			if (target instanceof Hero && ((Hero) target).hasTalent(Talent.SUFFERING_AWAY)){
				PathFinder.buildDistanceMap( target.pos, BArray.not( Dungeon.level.solid, null ),
						((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 2 ? 2 : 1 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(i);
						int t = Dungeon.level.map[i];
						if (ch != null){
							if (ch.alignment == Char.Alignment.ENEMY) {
								Buff.affect(ch, Roots.class, TICK*((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY));
							}
						}
						if (t == Terrain.EMPTY ||
								t == Terrain.EMBERS ||
								t == Terrain.EMPTY_DECO){
							Level.set(i, Terrain.HIGH_GRASS);
							GameScene.updateMap(i);
						}
						CellEmitter.get(t).burst(LeafParticle.LEVEL_SPECIFIC, 4);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.rooted = false;
		super.detach();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ROOTS;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
