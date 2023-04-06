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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class MirrorImage extends NPC {
	
	{
		spriteClass = MirrorSprite.class;
		
		HP = HT = 1;
		defenseSkill = 1;
		
		alignment = Alignment.ALLY;
		state = HUNTING;
		
		//before other mobs
		actPriority = MOB_PRIO + 1;
		immunities.add(PerfumeGas.Affection.class);
	}
	
	private Hero hero;
	private int heroID;
	public int armTier;
	
	@Override
	protected boolean act() {
		
		if ( hero == null ){
			hero = (Hero)Actor.findById(heroID);
			if ( hero == null ){
				die(null);
				sprite.killAndErase();
				return true;
			}
		}
		
		if (hero.tier() != armTier){
			armTier = hero.tier();
			((MirrorSprite)sprite).updateArmor( armTier );
		}
		
		return super.act();
	}
	
	private static final String HEROID	= "hero_id";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( HEROID, heroID );
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (hero.hasTalent(Talent.TIME_TOGETHER)) {
			int dmg = damageHero(enemy, damage, 1.5f);
			if (dmg >= Dungeon.hero.HP)
				return super.defenseProc(enemy, damage);
			hero.damage(dmg, this);
			return super.defenseProc(enemy, 0);
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int dmg, Object src) {
		if (hero.hasTalent(Talent.TIME_TOGETHER)) {
			if (!(src instanceof Char)) {
				int dm = damageHero(null, dmg, 0.75f);
				if (dm >= Dungeon.hero.HP) {
					super.damage(dmg, src);
					return;
				}
				hero.damage(dm, this);
				return;
			}
		}
		super.damage(dmg, src);
	}

	public int damageHero(Char enemy, int damage, float armorPercent) {
		int dr = Math.round(hero.drRoll() * armorPercent);
		if (enemy != null)
			damage = hero.defenseProc(enemy, damage);
		damage -= dr;
		if (damage < 0) damage = 0;
		if (hero.buff(Vulnerable.class) != null){
			damage *= 1.333f;
		}
		return damage;
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		heroID = bundle.getInt( HEROID );
	}
	
	public void duplicate( Hero hero ) {
		this.hero = hero;
		heroID = this.hero.id();
		Buff.affect(this, MirrorInvis.class, Short.MAX_VALUE);
	}
	
	@Override
	public int damageRoll() {
		int damage;
		if (hero.belongings.weapon != null){
			damage = hero.belongings.weapon.damageRoll(this);
		} else {
			damage = hero.damageRoll(); //handles ring of force
		}
		if (hero.pointsInTalent(Talent.TIME_TOGETHER) > 1) return damage;
		return (damage+1)/2; //half hero damage, rounded up
	}
	
	@Override
	public int attackSkill( Char target ) {
		return hero.attackSkill(target);
	}
	
	@Override
	public int defenseSkill(Char enemy) {
		if (hero != null) {
			int baseEvasion = 4 + hero.lvl;
			int heroEvasion = hero.defenseSkill(enemy);
			
			//if the hero has more/less evasion, 50% of it is applied
			if (hero.pointsInTalent(Talent.TIME_TOGETHER) > 1) return super.defenseSkill(enemy) * (baseEvasion);
			return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
		} else {
			return 0;
		}
	}
	
	@Override
	protected float attackDelay() {
		return hero.attackDelay(); //handles ring of furor
	}
	
	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy) || (hero.belongings.weapon != null && hero.belongings.weapon.canReach(this, enemy.pos));
	}

	@Override
	public int defenseValue() {
		if (hero != null && hero.belongings.weapon != null){
			return hero.belongings.weapon.defenseFactor(this)/
					(hero.pointsInTalent(Talent.TIME_TOGETHER) > 1 ? 1 : 2);
		} else {
			return 0;
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		MirrorInvis buff = buff(MirrorInvis.class);
		if (buff != null){
			buff.detach();
		}
		
		if (enemy instanceof Mob) {
			((Mob)enemy).aggro( this );
		}
		if (hero.belongings.weapon != null){
			damage = hero.belongings.weapon.proc( this, enemy, damage );
			if (!enemy.isAlive() && enemy == Dungeon.hero){
				Dungeon.fail(getClass());
				GLog.negative( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
			}
			return damage;
		} else {
			return damage;
		}
	}
	
	@Override
	public CharSprite sprite() {
		CharSprite s = super.sprite();
		
		hero = (Hero)Actor.findById(heroID);
		if (hero != null) {
			armTier = hero.tier();
		}
		((MirrorSprite)s).updateArmor( armTier );
		return s;
	}
	
	{
		immunities.add( ToxicGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Burning.class );
		immunities.add( Corruption.class );
	}
	
	public static class MirrorInvis extends Invisibility {
		
		{
			announced = false;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}
	}
}