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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

public class Shadows extends Buff {
	
	protected float left;
	
	private static final String LEFT	= "left";

	{
		announced = false;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		left = bundle.getFloat( LEFT );
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (Dungeon.level != null) {
			for (Mob m : Dungeon.level.mobs) {
				if (Dungeon.level.adjacent(m.pos, target.pos) && m.alignment != target.alignment) {
					return false;
				}
			}
		}
		if (super.attachTo( target )) {
			target.invisible++;
			if (target instanceof Hero && ((Hero) target).hasTalent(Talent.ASSASSINATION)){
				Buff.affect(target, Preparation.class);
			}
			if (Dungeon.level != null) {
				Sample.INSTANCE.play( Assets.Sounds.MELD );
				Dungeon.observe();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		if (target.invisible > 0)
			target.invisible--;
		super.detach();
		Dungeon.observe();
	}

	public void dispel(){
		updateQuickslot();
		detach();
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			spend( TICK );
			
			if (--left <= 0) {
				detach();
			}

			for (Mob m : Dungeon.level.mobs){
				if (Dungeon.level.adjacent(m.pos, target.pos) && m.alignment != target.alignment){
					detach();
				}
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.INVISIBLE );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
	}
	
	public void prolong() {
		left = 2;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.SHADOWS;
	}

	@Override
	public float iconFadePercent() {
		return 0;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}
