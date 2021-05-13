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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Hacatu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Wizard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.GasterBlaster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.MagicMissileMinion;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AbyssalSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssalNightmare extends Mob {

	{
		spriteClass = AbyssalSprite.class;

		HP = HT = 250;
		defenseSkill = 30;

		EXP = 40;
		maxLvl = 30;

		flying = true;
		baseSpeed = 0.5f;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see rollToDropLoot()

		properties.add(Property.INORGANIC);
		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
		properties.add(Property.BOSS);
		properties.add(Property.LARGE);
	}

	public AbyssalNightmare() {
		if (SPDSettings.bigdungeon()){
			EXP = 80;
			maxLvl = 64;
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
	protected float attackDelay() {
		return super.attackDelay()*1.6f;
	}

	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		HP = Math.min(HP+6, HT);

		boolean justAlerted = alerted;
		alerted = false;

		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}

		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}

		enemy = chooseEnemy();

		boolean enemyInFOV = enemy != null && enemy.isAlive() && enemy.invisible <= 0;

		return state.act( enemyInFOV, justAlerted );
	}

	@Override
	public boolean canSee(int pos) {
		return true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 60 );
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 16);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 60;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0){
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
				clone.HP = HP;
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;

				Dungeon.level.occupyCell(clone);

				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );
			}
		}
		return super.attackProc(enemy, damage);
	}

	private AbyssalNightmare split() {
		AbyssalNightmare clone = new AbyssalNightmare();
		clone.EXP = EXP/2;
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}

	@Override
	protected Item createLoot(){
		int rolls = 30;
		((RingOfWealth)(new RingOfWealth().upgrade(10))).buff().attachTo(this);
		ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(this, rolls);
		if (bonus != null && !bonus.isEmpty()) {
			for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
			RingOfWealth.showFlareForBonusDrop(sprite);
		}
		return null;
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {

			if (target == pos || Dungeon.level.adjacent(pos, target)) {
				return false;
			}




			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				PathFinder.buildDistanceMap(pos+i, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
				if (PathFinder.distance[pos+i] == Integer.MAX_VALUE){
					continue;
				}
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){

				for (int i : PathFinder.CIRCLE8){
					if ((Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO ||
							Dungeon.level.map[pos+i] == Terrain.DOOR || Dungeon.level.map[pos+i] == Terrain.SECRET_DOOR)){
						Level.set(pos+i, Terrain.EMPTY);
						if (Dungeon.hero.fieldOfView[pos+i]){
							CellEmitter.bottom(pos+i).burst(SmokeParticle.FACTORY, 12);
						}
						GameScene.updateMap(pos+i);
					}
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}

				return true;
			}

			return false;
		}
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
		immunities.add( FireKeeper.class);
		immunities.add( FrostFire.class);
		immunities.add( GonerField.class);
		immunities.add( HealGas.class);
		immunities.add( HoneyGas.class);
		immunities.add( PerfumeGas.class);
		immunities.add( YogWall.class);


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
		immunities.add( Vertigo.class);
		immunities.add( Terror.class);
		immunities.add( Vulnerable.class);
		immunities.add( SoulParalysis.class);
		immunities.add( Slow.class);
		immunities.add( Blindness.class);
		immunities.add( Wet.class);
		immunities.add( Cripple.class);
		immunities.add( DefenseDebuff.class);
		immunities.add( Doom.class);
		immunities.add( Drowsy.class);
		immunities.add( Hex.class);
		immunities.add( Sleep.class);

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
		immunities.add( WandOfBounceBeams.class);

		immunities.add( Shaman.EarthenBolt.class );
		immunities.add( Hacatu.LightningBolt.class );
		immunities.add( Warlock.DarkBolt.class );
		immunities.add( Wizard.DarkBolt.class );
		immunities.add( Eye.DeathGaze.class );
		immunities.add( Yog.BurningFist.DarkBolt.class );
		immunities.add( FinalFroggit.Bolt.class);
		immunities.add( SpectreRat.DarkBolt.class);
		immunities.add( WardingWraith.DarkBolt.class);
		immunities.add(	GasterBlaster.class);
		immunities.add(	MagicMissileMinion.class);

		immunities.add(NewTengu.FireAbility.FireBlob.class);

		immunities.add(Grim.class);
		immunities.add(Kinetic.class);
		immunities.add(Blazing.class);
		immunities.add(Shocking.class);
		immunities.add(Vampiric.class);
	}
}
