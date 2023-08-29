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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Preparation extends Buff implements ActionIndicator.Action {
	
	{
		//always acts after other buffs, so invisibility effects can process first
		actPriority = BUFF_PRIO - 1;
	}
	
	public enum AttackLevel{
		LVL_1( 1,  0.15f, 1),
		LVL_2( 4,  0.30f, 1),
		LVL_3( 7,  0.45f, 2),
		LVL_4( 11, 0.60f, 3);
		
		final int turnsReq;
		final float baseDmgBonus;
		final int damageRolls;

		//1st index is prep level, 2nd is talent level
		private static final float[][] KOThresholds = new float[][]{
				{.04f, .09f, .13f},
				{.10f, .15f, .20f},
				{.25f, .30f, .35f},
				{.40f, .45f, .50f}
		};
		public float KOThreshold(){
			return KOThresholds[ordinal()][Dungeon.hero.pointsInTalent(Talent.ASSASSINATION)];
		}

		//1st index is prep level, 2nd is type.
		private static final int[][] blinkRanges = new int[][]{
				{1, 3, 5},
				{2, 4, 6},
				{4, 6, 8},
				{6, 8, 11}
		};
		public int blinkDistance(){
			return blinkRanges[ordinal()][Dungeon.hero.pointsInTalent(Talent.ASSASSINATION)];
		}
		
		AttackLevel( int turns, float base, int rolls){
			turnsReq = turns;
			baseDmgBonus = base;
			damageRolls = rolls;
		}
		
		public boolean canKO(Char defender){
			if (defender.properties().contains(Char.Property.MINIBOSS)
					|| defender.properties().contains(Char.Property.BOSS)){
				return (defender.HP/(float)defender.HT) < (KOThreshold()/5f);
			} else {
				return (defender.HP/(float)defender.HT) < KOThreshold();
			}
		}
		
		public int damageRoll( Char attacker ){
			int dmg = attacker.damageRoll();
			for( int i = 1; i < damageRolls; i++){
				int newDmg = attacker.damageRoll();
				if (newDmg > dmg) dmg = newDmg;
			}
			return Math.round(dmg * (1f + baseDmgBonus));
		}


		
		public static AttackLevel getLvl(int turnsInvis){
			List<AttackLevel> values = Arrays.asList(values());
			Collections.reverse(values);
			for ( AttackLevel lvl : values ){
				if (turnsInvis >= lvl.turnsReq){
					return lvl;
				}
			}
			return LVL_1;
		}
	}
	
	public int turnsInvis = 0;
	
	@Override
	public boolean act() {
		if (target.invisible > 0){
			turnsInvis++;
			if (AttackLevel.getLvl(turnsInvis).blinkDistance() > 0 && target == Dungeon.hero){
				ActionIndicator.setAction(this);
			}
			spend(TICK);
		} else {
			detach();
		}
		return true;
	}
	
	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}
	
	public int damageRoll( Char attacker ){
		return AttackLevel.getLvl(turnsInvis).damageRoll(attacker);
	}

	public boolean canKO( Char defender ){
		return AttackLevel.getLvl(turnsInvis).canKO(defender);
	}

	public int attackLevel(){
		return AttackLevel.getLvl(turnsInvis).ordinal()+1;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.PREPARATION;
	}
	
	@Override
	public void tintIcon(Image icon) {
		switch (AttackLevel.getLvl(turnsInvis)){
			case LVL_1:
				icon.hardlight(0f, 1f, 0f);
				break;
			case LVL_2:
				icon.hardlight(1f, 1f, 0f);
				break;
			case LVL_3:
				icon.hardlight(1f, 0.6f, 0f);
				break;
			case LVL_4:
				icon.hardlight(1f, 0f, 0f);
				break;
		}
	}

	@Override
	public float iconFadePercent() {
		AttackLevel level = AttackLevel.getLvl(turnsInvis);
		if (level == AttackLevel.LVL_4){
			return 0;
		} else {
			float turnsForCur = level.turnsReq;
			float turnsForNext = AttackLevel.values()[level.ordinal()+1].turnsReq;
			turnsForNext -= turnsForCur;
			float turnsToNext = turnsInvis - turnsForCur;
			return Math.min(1, (turnsForNext - turnsToNext)/(turnsForNext));
		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		
		AttackLevel lvl = AttackLevel.getLvl(turnsInvis);

		desc += "\n\n" + Messages.get(this, "desc_dmg",
				(int)(lvl.baseDmgBonus*100),
				(int)(lvl.KOThreshold()*100),
				(int)(lvl.KOThreshold()*20));
		
		if (lvl.damageRolls > 1){
			desc += " " + Messages.get(this, "desc_dmg_likely");
		}
		
		if (lvl.blinkDistance() > 0){
			desc += "\n\n" + Messages.get(this, "desc_blink", lvl.blinkDistance());
		}
		
		desc += "\n\n" + Messages.get(this, "desc_invis_time", turnsInvis);
		
		if (lvl.ordinal() != AttackLevel.values().length-1){
			AttackLevel next = AttackLevel.values()[lvl.ordinal()+1];
			desc += "\n" + Messages.get(this, "desc_invis_next", next.turnsReq);
		}
		
		return desc;
	}
	
	private static final String TURNS = "turnsInvis";
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turnsInvis = bundle.getInt(TURNS);
		if (AttackLevel.getLvl(turnsInvis).blinkDistance() > 0){
			ActionIndicator.setAction(this);
		}
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TURNS, turnsInvis);
	}
	
	@Override
	public Image getIcon() {
		Image actionIco = Effects.get(Effects.Type.WOUND);
		tintIcon(actionIco);
		return actionIco;
	}
	
	@Override
	public void doAction() {
		GameScene.selectCell(attack);
	}
	
	private CellSelector.Listener attack = new CellSelector.Listener() {
		
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			if (enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC || (Dungeon.isChallenged(Conducts.Conduct.PACIFIST))){
				GLog.warning(Messages.get(Preparation.class, "no_target"));
			} else {
				
				//just attack them then!
				if (Dungeon.hero.canAttack(enemy)){
					Dungeon.hero.curAction = new HeroAction.Attack( enemy );
					Dungeon.hero.next();
					return;
				}
				
				AttackLevel lvl = AttackLevel.getLvl(turnsInvis);
				
				boolean[] passable = Dungeon.level.passable.clone();
				//need to consider enemy cell as passable in case they are on a trap or chasm
				passable[cell] = true;
				PathFinder.buildDistanceMap(Dungeon.hero.pos, passable, lvl.blinkDistance()+1);
				if (PathFinder.distance[cell] == Integer.MAX_VALUE){
					GLog.warning(Messages.get(Preparation.class, "out_of_reach"));
					return;
				}
				
				//we can move through enemies when determining blink distance,
				// but not when actually jumping to a location
				for (Char ch : Actor.chars()){
					if (ch != Dungeon.hero)  passable[ch.pos] = false;
				}
				
				PathFinder.Path path = PathFinder.find(Dungeon.hero.pos, cell, passable);
				int attackPos = path == null ? -1 : path.get(path.size()-2);
				
				if (attackPos == -1 ||
						Dungeon.level.distance(attackPos, Dungeon.hero.pos) > lvl.blinkDistance()){
					GLog.warning(Messages.get(Preparation.class, "out_of_reach"));
					return;
				}
				
				Dungeon.hero.pos = attackPos;
				Dungeon.level.occupyCell(Dungeon.hero);
				//prevents the hero from being interrupted by seeing new enemies
				Dungeon.observe();
				Dungeon.hero.checkVisibleMobs();
				
				Dungeon.hero.sprite.place( Dungeon.hero.pos );
				Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, cell);
				CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );

				Dungeon.hero.curAction = new HeroAction.Attack( enemy );
				Dungeon.hero.next();
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(Preparation.class, "prompt", AttackLevel.getLvl(turnsInvis).blinkDistance());
		}
	};
}
