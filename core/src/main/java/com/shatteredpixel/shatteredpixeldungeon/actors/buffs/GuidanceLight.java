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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Phantom;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class GuidanceLight extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
	}

	public static final int DISTANCE	= 6;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			if (Dungeon.level != null) {
				target.viewDistance = Math.max( Dungeon.level.viewDistance, DISTANCE );
				Dungeon.observe();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean act() {

		if (target instanceof Hero) {
			ArrayList<Char> visible = new ArrayList<>();
			visible.add(target);
			float chargeAmount = Math.min(0.5f, left);
			if (((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 1 && ((Hero) target).hasTalent(Talent.SUFFERING_AWAY)){
				int count = 0;
				for (Buff b: target.buffs()){
					if (Talent.canSufferAway((Hero) target, b)){
						count++;
					}
				}
				chargeAmount += 0.25f*count;
			}
			partialCharge += chargeAmount;
			if (partialCharge >= 1){
				for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (target.fieldOfView[m.pos] && m.alignment == Char.Alignment.ALLY && (!(m instanceof Phantom))) {
						visible.add(m);
					}
				}
			}
			while (partialCharge >= 1){
				partialCharge -= 1;
				for (Char ch: visible){
					if (ch.HP >= ch.HT){
						Buff.affect(ch, Barrier.class).incShield(1);
					} else {
						ch.HP = Math.min(ch.HP+1, ch.HT);
						ch.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
					}
				}
			}
		}

		left--;
		if (left <= 0){
			detach();
		} else {
			spend(TICK);
		}

		return true;
	}
	
	@Override
	public void detach() {
		target.viewDistance = Dungeon.level.viewDistance;
		Dungeon.observe();
		super.detach();
	}

	@Override
	public int icon() {
		return BuffIndicator.GUIDANCE;
	}
	
	@Override
	public float iconFadePercent() {
		return Math.max(0, (duration() - left) / duration());
	}

	private float left;
	private float partialCharge;

	public GuidanceLight set( float amount ){
		if (left < amount) left = amount;
		return this;
	}

	public GuidanceLight prolong( float amount ){
		left += amount;
		return this;
	}

	public float left(){
		return left;
	}

	public static int duration(){
		return 100*Dungeon.hero.pointsInTalent(Talent.GUIDANCE_FLAME);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left+1));
	}

	private static final String LEFT = "left";
	private static final String PARTIAL_LEFT = "partial_left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LEFT, left );
		bundle.put( PARTIAL_LEFT, partialCharge );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
		partialCharge = bundle.getFloat(PARTIAL_LEFT);
	}
}
