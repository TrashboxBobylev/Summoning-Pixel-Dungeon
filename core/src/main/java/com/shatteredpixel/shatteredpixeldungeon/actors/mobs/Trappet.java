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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrappetSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Trappet extends AbyssalMob implements Callback {

	private static final float TIME_TO_ZAP	= 1f;

	{
		spriteClass = TrappetSprite.class;

		HP = HT = 125;
		defenseSkill = 36;
		viewDistance = Light.DISTANCE;

		EXP = 20;
		maxLvl = 30;

		loot = new Gold().goldFromEnemy();
		lootChance = 0.45f;

		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 34 + abyssLevel();
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0 + abyssLevel()*3, 7 + abyssLevel()*11);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {
		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.zap( enemy.pos );
			return false;
		} else {
			zap();
			return true;
		}
	}

	protected Class[] traps = new Class[]{
			FrostTrap.class, StormTrap.class, CorrosionTrap.class, CalamityTrap.class, DisintegrationTrap.class,
			RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, AbyssFlameTrap.class,
			DisarmingTrap.class, WraithTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {
			ArrayList<Integer> points = Level.getSpawningPoints(enemy.pos);
			if (!points.isEmpty()){
				Trap t = ((Trap)Reflection.newInstance(Random.element(traps)));
				Dungeon.level.setTrap(t, Random.element(points));
				Dungeon.level.map[t.pos] = t.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
				t.reveal();
			} else {
				Trap t = ((Trap)Reflection.newInstance(Random.element(traps)));
				Dungeon.level.setTrap(t, enemy.pos);
				t.activate();
			}
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Dungeon.fail( getClass() );
				GLog.negative( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}
}
