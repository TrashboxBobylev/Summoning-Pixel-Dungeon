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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Hacatu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Wizard;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AbyssalSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssalNightmare extends Mob {

	{
		spriteClass = AbyssalSprite.class;

		HP = HT = 500;
		defenseSkill = 30;

		EXP = 40;
		maxLvl = 30;

		flying = true;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see rollToDropLoot()
	}

	public AbyssalNightmare() {
		if (SPDSettings.bigdungeon()){
			EXP = 80;
			maxLvl = 100;
		}
	}

	private static final float SPLIT_DELAY	= 1f;
	
	int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}

	@Override
	protected boolean act() {
		HP = Math.min(HP+8, HT);

		if (Random.Int(20) == 0 && generation < 2){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				if (Dungeon.level.passable[pos+n] && Actor.findChar( pos+n ) == null) {
					candidates.add( pos+n );
				}
			}

			if (!candidates.isEmpty()) {
				AbyssalNightmare spawn = new AbyssalNightmare();

				spawn.pos = Random.element( candidates );
				spawn.state = spawn.HUNTING;
				spawn.generation = generation+1;

				Dungeon.level.occupyCell(spawn);
				Level.set(spawn.pos, Terrain.CHASM);
				GameScene.updateMap(spawn.pos);

				GameScene.add( spawn, 1 );
				if (sprite.visible) {
					Actor.addDelayed(new Pushing(spawn, pos, spawn.pos), -1);
				}
			}
			Random.pushGenerator();
		}
		return super.act();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 24, 54 );
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(5, 25);
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (HP >= damage + 2) {
			ArrayList<Integer> candidates = new ArrayList<>();
			boolean[] solid = Dungeon.level.solid;
			
			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (!solid[n] && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}
	
			if (candidates.size() > 0) {
				
				AbyssalNightmare clone = split();
				clone.HP = (HP - damage) / 2;
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;
				
				Dungeon.level.occupyCell(clone);
				
				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );
				
				HP -= clone.HP;
			}
		}
		
		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 60;
	}
	
	private AbyssalNightmare split() {
		AbyssalNightmare clone = new AbyssalNightmare();
		clone.generation = generation + 1;
		clone.EXP = 0;
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}
	
	@Override
	public void rollToDropLoot() {
		lootChance = 1f/(6 * (generation+1) );
		lootChance *= (5f - Dungeon.LimitedDrops.SWARM_HP.count) / 5f;
		super.rollToDropLoot();
	}
	
	@Override
	protected Item createLoot(){
		Dungeon.LimitedDrops.SWARM_HP.count++;
		return super.createLoot();
	}

	{
		immunities.add( Blizzard.class );
		immunities.add( ConfusionGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Electricity.class );
		immunities.add( Fire.class );
		immunities.add( Freezing.class );
		immunities.add( Inferno.class );
		immunities.add( ParalyticGas.class );
		immunities.add( Regrowth.class );
		immunities.add( SmokeScreen.class );
		immunities.add( StenchGas.class );
		immunities.add( WandOfStenchGas.class);
		immunities.add( StormCloud.class );
		immunities.add( ToxicGas.class );
		immunities.add( Web.class );

		immunities.add( Burning.class );
		immunities.add( Charm.class );
		immunities.add( Chill.class );
		immunities.add( Frost.class );
		immunities.add( Ooze.class );
		immunities.add( Paralysis.class );
		immunities.add( Poison.class );
		immunities.add( Corrosion.class );
		immunities.add( Weakness.class );
		immunities.add( FrostBurn.class);
		immunities.add( Shrink.class);
		immunities.add( TimedShrink.class);
		immunities.add( MagicalSleep.class);

		immunities.add( DisintegrationTrap.class );
		immunities.add( GrimTrap.class );

		immunities.add( WandOfBlastWave.class );
		immunities.add( WandOfDisintegration.class );
		immunities.add( WandOfFireblast.class );
		immunities.add( WandOfFrost.class );
		immunities.add( WandOfLightning.class );
		immunities.add( WandOfLivingEarth.class );
		immunities.add( WandOfMagicMissile.class );
		immunities.add( WandOfPrismaticLight.class );
		immunities.add( WandOfTransfusion.class );
		immunities.add( WandOfWarding.Ward.class );
		immunities.add( WandOfCrystalBullet.class);
		immunities.add( WandOfStars.Star.class);

		immunities.add( Shaman.EarthenBolt.class );
		immunities.add( Hacatu.LightningBolt.class );
		immunities.add( Warlock.DarkBolt.class );
		immunities.add( Wizard.DarkBolt.class );
		immunities.add( Eye.DeathGaze.class );
		immunities.add( Yog.BurningFist.DarkBolt.class );
		immunities.add( FinalFroggit.Bolt.class);

		immunities.add(NewTengu.FireAbility.FireBlob.class);

		immunities.add(Grim.class);
	}
}
