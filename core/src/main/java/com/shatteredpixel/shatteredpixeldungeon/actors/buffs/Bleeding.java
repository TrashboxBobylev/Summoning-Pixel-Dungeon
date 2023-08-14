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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

import static com.watabou.utils.Random.NormalFloat;

public class Bleeding extends Buff {

	{
		type = buffType.NEGATIVE;
		severity = buffSeverity.DAMAGING;
		announced = true;
	}
	
	protected float level;

	public float level(){
		return level;
	}
	
	private static final String LEVEL	= "level";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
	}
	
	public void set( float level ) {
		this.level = Math.max(this.level, level);
	}

	public void setForcefully( float level ) {
		this.level = level;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.BLEEDING;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			level = NormalFloat(level / 2f, level);
			int dmg = Math.round(level);
			
			if (dmg > 0) {
				
				target.damage( dmg, this );
				if (target.sprite.visible) {
					Splash.at( target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
							target.sprite.blood(), Math.min( 10 * dmg / target.HT, 10 ) );
				}

				if (target == Dungeon.hero) {
					if (!target.isAlive()) {
						Dungeon.fail(getClass());
						GLog.negative(Messages.get(this, "ondeath"));
					} else if (Dungeon.hero.pointsInTalent(Talent.SUFFERING_AWAY) > 1){
						for (Char ch: Dungeon.level.mobs){
							if (ch.alignment == Char.Alignment.ALLY && Dungeon.hero.fieldOfView[ch.pos]){
								int healing = dmg;
								if (Dungeon.hero.pointsInTalent(Talent.SUFFERING_AWAY) > 2)
									healing *= 1.5f;
								Regeneration.regenerate(ch, healing);
							}
						}
					}
				}
				
				spend( TICK );
			} else {
				detach();
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.round(level));
	}
}
