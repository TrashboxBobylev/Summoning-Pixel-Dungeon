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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Invisibility extends FlavourBuff {

	public static final float DURATION	= 20f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.invisible++;
			if (target instanceof Hero && ((Hero) target).hasTalent(Talent.ASSASSINATION)){
				Buff.affect(target, Preparation.class);
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
	}
	
	@Override
	public int icon() {
		return BuffIndicator.INVISIBLE;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.INVISIBLE );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
	}



	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}

	public static void dispel() {
		if (Dungeon.hero.pointsInTalent(Talent.ASSASSINATION) > 1){
			if ((Dungeon.hero.buff(DispelDelayer.class) == null || Dungeon.hero.buff(DispelDelayer.class).tier > 0)
					&& Dungeon.hero.invisible > 0){
				if (Dungeon.hero.buff(DispelDelayer.class) == null)
					Buff.affect(Dungeon.hero, DispelDelayer.class, Dungeon.hero.pointsInTalent(Talent.ASSASSINATION)).tier = Math.max(1, Dungeon.hero.pointsInTalent(Talent.ASSASSINATION) - 1);
				else
					Dungeon.hero.buff(DispelDelayer.class).tier--;
				Preparation preparation = Dungeon.hero.buff(Preparation.class);
				final int attackReduction = Dungeon.hero.pointsInTalent(Talent.ASSASSINATION) > 2 ? 1 : 2;
				if (preparation != null){
					preparation.detach();
					if (preparation.attackLevel() > attackReduction) {
						Preparation newPreparation = Buff.affect(Dungeon.hero, Preparation.class);
						while (newPreparation.attackLevel() != preparation.attackLevel() - attackReduction) {
							newPreparation.turnsInvis++;
						}
					}
				}
			} else {
				actualDispel();
			}
		}
		else {
			actualDispel();
		}
	}

	public static void actualDispel() {
		for ( Buff invis : Dungeon.hero.buffs( Invisibility.class )){
			invis.detach();
		}
		CloakOfShadows.cloakStealth cloakBuff = Dungeon.hero.buff( CloakOfShadows.cloakStealth.class );
		if (cloakBuff != null) {
			cloakBuff.dispel();
		}
		Shadows shadows = Dungeon.hero.buff( Shadows.class );
		if (shadows != null) {
			shadows.dispel();
		}

        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            Buff buff = mob.buff( Invisibility.class );
            if (mob instanceof Minion && buff != null){
                buff.detach();
            }
        }
		
		//these aren't forms of invisibilty, but do dispel at the same time as it.
		TimeFreezing timeFreeze = Dungeon.hero.buff( TimeFreezing.class );
		if (timeFreeze != null) {
			timeFreeze.detach();
		}
		
		Preparation prep = Dungeon.hero.buff( Preparation.class );
		if (prep != null){
			prep.detach();
		}
	}

	public static class DispelDelayer extends FlavourBuff {

		{
			actPriority = BUFF_PRIO + 1;
		}

		public int tier;

		@Override
		public boolean act() {
			Buff.affect(target, CloakOfShadows.BriefRecharge.class).prolong(CloakOfShadows.MIN_CHARGE*3);
			actualDispel();
			detach();
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.SPIRIT);
			else target.sprite.remove(CharSprite.State.SPIRIT);
		}

		private static final String TURNS = "tier";

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tier = bundle.getInt(TURNS);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TURNS, tier);
		}
	}
}
