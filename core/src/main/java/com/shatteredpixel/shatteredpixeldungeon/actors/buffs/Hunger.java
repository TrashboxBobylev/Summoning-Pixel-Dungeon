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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Hunger extends Buff implements Hero.Doom {

	private static final float STEP	= 10f;

	public static final float HUNGRY	= 990f;
	public static final float STARVING	= 1100f;

	private float level;
	private float partialDamage;

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}

	@Override
	public boolean act() {

        if (!target.isAlive()) {

            diactivate();

        }
        spend(TICK);

        return true;
	}

	public void satisfy( float energy ) {

		Artifact.ArtifactBuff buff = target.buff( HornOfPlenty.hornRecharge.class );
		if (buff != null && buff.isCursed()){
			energy *= 0.67f;
			GLog.negative( Messages.get(this, "cursedhorn") );
		}

		adjustHunger( energy );
	}

	//directly interacts with hunger, no checks.
	public static void adjustHunger(float energy ) {
        Hunger hunger = Buff.affect(Dungeon.hero, Hunger.class);
        Char target = hunger.target;
        if (Dungeon.level.locked || target.buff(WellFed.class) != null){
            return;
        }
        if (Dungeon.hero.heroClass == HeroClass.WARRIOR && energy != -50 && energy < 0) energy *= 0.75f;
		hunger.level = Math.max(hunger.level - energy, 0);
		switchHungerLevel(energy, hunger, target);
		BuffIndicator.refreshHero();
	}

	private static void switchHungerLevel(float energy, Hunger hunger, Char target) {
		if (hunger.level + 1 > HUNGRY && !hunger.isHungry()){
			GLog.warning(Messages.get(hunger, "onhungry"));
			hunger.level = HUNGRY;
			return;
		}
		if (hunger.level + 1 >= STARVING && !hunger.isStarving()){

			GLog.negative(Messages.get(hunger, "onstarving"));
			Dungeon.hero.resting = false;
			Dungeon.hero.damage(1, hunger);
			Dungeon.hero.interrupt();
			hunger.level = STARVING;
			return;
		}
		if (hunger.isStarving()){
			hunger.level = STARVING;
			hunger.partialDamage += Math.abs(energy) * target.HT/400f;
			if (hunger.partialDamage > 1){
				target.damage( (int)Math.abs(hunger.partialDamage), new Hunger());
				hunger.partialDamage -= (int)hunger.partialDamage;
			}
		}
	}

	public boolean isStarving() {
		return level >= STARVING;
	}

	public boolean isHungry(){
		return level >= HUNGRY;
	}

	public int hunger() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (level < HUNGRY) {
			return BuffIndicator.NONE;
		} else if (level < STARVING) {
			return BuffIndicator.HUNGER;
		} else {
			return BuffIndicator.STARVATION;
		}
	}

	@Override
	public String toString() {
		if (level < STARVING) {
			return Messages.get(this, "hungry");
		} else {
			return Messages.get(this, "starving");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < STARVING) {
			result = Messages.get(this, "desc_intro_hungry");
		} else {
			result = Messages.get(this, "desc_intro_starving");
		}

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

		Badges.validateDeathFromHunger();

		Dungeon.fail( getClass() );
		GLog.negative( Messages.get(this, "ondeath") );
	}
}
