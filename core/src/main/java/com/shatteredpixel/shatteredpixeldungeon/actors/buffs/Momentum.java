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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MomentumBoots;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Momentum extends Buff implements ActionIndicator.Action {

	{
		type = buffType.POSITIVE;

		//acts before the hero
		actPriority = HERO_PRIO+1;
	}

	public int momentumStacks = 0;
	public int freerunTurns = 0;
	public int freerunCooldown = 0;

	private boolean movedLastTurn = true;

	@Override
	public boolean act() {
		if (freerunCooldown > 0){
			freerunCooldown--;
		}

		if (freerunTurns > 0){
				freerunTurns--;
				if(freerunTurns == 0) Item.updateQuickslot();
		} else if (!movedLastTurn){
			momentumStacks = (int) GameMath.gate(0, momentumStacks-1, Math.round(momentumStacks * 0.667f));
			if (momentumStacks <= 0) {
				ActionIndicator.clearAction(this);
				if (freerunCooldown <= 0) detach();
			}
		}
		movedLastTurn = false;
		if (MomentumBoots.instance == null) detach();

		spend(TICK);
		return true;
	}

	public void gainStack(){
		movedLastTurn = true;
		if (freerunCooldown <= 0 && !freerunning()){
			postpone(target.cooldown()+(1/target.speed()));
			momentumStacks = Math.min(momentumStacks + 1, getMaxMomentum());
			ActionIndicator.setAction(this);
		}
	}

	public int getMaxMomentum() {
		if (MomentumBoots.instance == null)
			return 1;
		return 5 + MomentumBoots.instance.itemLevel()*2;
	}

	public boolean freerunning(){
		return freerunTurns > 0 && MomentumBoots.instance != null;
	}

	public float speedMultiplier(){
		if (freerunning()){
			return 1.5f;
		} else {
			return 1;
		}
	}

	public float furorMultiplier(){
		if (freerunning()){
			return 1.5f;
		} else {
			return 1f;
		}
	}

	public int evasionBonus( int heroLvl, int excessArmorStr ){
		if (freerunning()) {
			return heroLvl/2 + MomentumBoots.instance.itemLevel()*3;
		} else {
			return 0;
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.MOMENTUM;
	}

	@Override
	public void tintIcon(Image icon) {
		if (freerunTurns > 0){
			icon.hardlight(1,1,0);
		} else if (freerunCooldown > 0){
			icon.hardlight(0.5f,0.5f,1);
		} else {
			icon.hardlight(1f - (momentumStacks /(getMaxMomentum() * 1f)),1,
					1f - (momentumStacks /(getMaxMomentum() * 1f)));
		}
	}

	@Override
	public float iconFadePercent() {
		if (freerunTurns > 0){
			int duration = (int)Math.ceil(getMaxMomentum()*1.5f);
			return (duration - freerunTurns) / (float)duration;
		} else if (freerunCooldown > 0){
			return (freerunCooldown) / (50f*cooldownScaling());
		} else {
			return (getMaxMomentum() - momentumStacks) / (getMaxMomentum() * 1f);
		}
	}

	@Override
	public String toString() {
		if (freerunTurns > 0){
			return Messages.get(this, "running");
		} else if (freerunCooldown > 0){
			return Messages.get(this, "resting");
		} else {
			return Messages.get(this, "momentum");
		}
	}

	@Override
	public String desc() {
		String cls = Messages.titleCase(Dungeon.hero.heroClass.title());
		if (freerunTurns > 0){
			return Messages.get(this, "running_desc", cls, freerunTurns, 2);
		} else if (freerunCooldown > 0){
			return Messages.get(this, "resting_desc", cls, freerunCooldown);
		} else {
			return Messages.get(this, "momentum_desc", cls, momentumStacks, getMaxMomentum());
		}
	}

	private static final String STACKS =        "stacks";
	private static final String FREERUN_TURNS = "freerun_turns";
	private static final String FREERUN_CD =    "freerun_CD";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, momentumStacks);
		bundle.put(FREERUN_TURNS, freerunTurns);
		bundle.put(FREERUN_CD, freerunCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		momentumStacks = bundle.getInt(STACKS);
		freerunTurns = bundle.getInt(FREERUN_TURNS);
		freerunCooldown = bundle.getInt(FREERUN_CD);
		if ( usable() ){
			ActionIndicator.setAction(this);
		}
		movedLastTurn = false;
	}

	@Override
	public Image getIcon() {
		Image im = new BuffIcon(BuffIndicator.MOMENTUM, true);
		im.hardlight(0x99992E);
		return im;
	}

	public float cooldownScaling(){
		if (MomentumBoots.instance == null)
			return 1f;
		return 1f - MomentumBoots.instance.itemLevel()*0.1f;
	}

	@Override
	public void doAction() {
		if (MomentumBoots.instance == null)
			return;
		// 20 / 24 / 27 / 30 at max.
		freerunTurns = (int)Math.ceil(1.5f*momentumStacks);
		//cooldown is functionally 20+3*stacks when active effect ends
		freerunCooldown = Math.round((20 + 3*momentumStacks + freerunTurns)*cooldownScaling());
		Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
		target.sprite.emitter().burst(Speck.factory(Speck.JET), 5+ momentumStacks);
		momentumStacks = 0;
		Item.updateQuickslot();
		BuffIndicator.refreshHero();
		ActionIndicator.clearAction(this);
	}
	public boolean usable() {
		return momentumStacks > 0 && freerunTurns <= 0;
	}

}
