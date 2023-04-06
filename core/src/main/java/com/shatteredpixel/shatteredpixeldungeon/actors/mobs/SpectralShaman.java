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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBounceBeams;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SpectralShaman extends Mob {
	
	{
		spriteClass = ShamanSprite.Spectral.class;
		
		HP = HT = 125;
		EXP = 12;
		defenseSkill = 16;
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			defenseSkill = 22;
		}
		baseSpeed = 2f;

		loot = Generator.Category.WAND;
		lootChance = 1f;

		properties.add(Property.UNDEAD);
	}
	
	private boolean nextPedestal = true;
	public int startPosition;
	
	private static final String PEDESTAL = "pedestal";
	private static final String START    = "startPos";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( PEDESTAL, nextPedestal );
		bundle.put( START, startPosition);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		nextPedestal = bundle.getBoolean( PEDESTAL );
		startPosition = bundle.getInt( START);
	}
	
	@Override
	public int damageRoll() {
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			return Random.NormalIntRange( 7, 20 );
		}
		return Random.NormalIntRange( 4, 11 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			return 40;
		}
		return 20;
	}

	@Override
	public int defenseValue() {
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			return 13;
		}
		return 8;
	}

	public int pedestal(boolean left){
		if (left){
			return startPosition - 2;
		} else {
			return startPosition + 2;
		}
	}
	
	@Override
	protected boolean getCloser( int target ) {
		return canTryToSummon() ?
			super.getCloser( pedestal( nextPedestal ) ) :
			super.getCloser( target );
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
		}

		super.damage( dmg, src );
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return canTryToSummon() ?
				pos == pedestal( nextPedestal ) :
				Dungeon.level.adjacent( pos, enemy.pos );
	}

	protected boolean canTryToSummon() {
		if (paralysed <= 0) {
			Char ch = Actor.findChar( pedestal( nextPedestal ) );
			return ch == this || ch == null;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean act() {
		if (canTryToSummon() && pos == pedestal( nextPedestal )) {
			summon();
			return true;
		} else {
			if (enemy != null && canTryToSummon() && Actor.findChar( pedestal( nextPedestal ) ) == enemy) {
				nextPedestal = !nextPedestal;
			}
			return super.act();
		}
	}

	@Override
	public void aggro(Char ch) {
		super.aggro(ch);
		for (Mob mob : Dungeon.level.mobs){
			if (mob instanceof Wraith){
				mob.aggro(ch);
			}
		}
	}
	
	private void summon() {

		nextPedestal = !nextPedestal;
		
		sprite.centerEmitter().start( Speck.factory( Speck.SMOKE ), 0.01f, 10 );
		Sample.INSTANCE.play( Assets.Sounds.CURSED );
		if (enemy != null)
			sprite.turnTo(pos, enemy.pos);
		
		boolean[] passable = Dungeon.level.passable.clone();
		for (Char c : Actor.chars()) {
			passable[c.pos] = false;
		}
		
		int undeadsToSummon = 1;

		PathFinder.buildDistanceMap( pos, passable, undeadsToSummon );
		PathFinder.distance[pos] = Integer.MAX_VALUE;
		int dist = 1;
		
	undeadLabel:
		for (int i=0; i < undeadsToSummon; i++) {
			do {
				for (int j=0; j < Dungeon.level.length(); j++) {
					if (PathFinder.distance[j] == dist) {
						
						Wraith undead = Wraith.spawnForcefullyAt(j);
						
						ScrollOfTeleportation.appear( undead, j );
						new Flare( 3, 32 ).color( 0x000000, false ).show( undead.sprite, 2f ) ;
						
						PathFinder.distance[j] = Integer.MAX_VALUE;
						
						continue undeadLabel;
					}
				}
				dist++;
			} while (dist < undeadsToSummon);
		}
		spend( TICK );
	}
	
	{
		resistances.add( WandOfBounceBeams.class );
		resistances.add( ToxicGas.class );
		resistances.add( Burning.class );
	}
	
	{
		immunities.add( Paralysis.class );
		immunities.add( Vertigo.class );
		immunities.add( Blindness.class );
		immunities.add( Terror.class );
	}

}
