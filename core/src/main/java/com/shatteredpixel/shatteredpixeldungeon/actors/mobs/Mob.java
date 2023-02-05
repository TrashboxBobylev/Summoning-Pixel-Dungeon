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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Gravery;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WandOfStenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.SoulFlame;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SoulOfYendor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GoldToken;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public abstract class Mob extends Char {

	{
		actPriority = MOB_PRIO;

		alignment = Alignment.ENEMY;
	}

	private static final String	TXT_DIED	= "You hear something died in the distance";

	protected static final String TXT_NOTICE1	= "?!";
	protected static final String TXT_RAGE		= "#$%^";
	protected static final String TXT_EXP		= "%+dEXP";

	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState FOLLOWING    = new Following();
	public AiState state = SLEEPING;

	public Class<? extends CharSprite> spriteClass;

	protected int target = -1;

	protected int defenseSkill = 0;

	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL;

	public Char enemy;
	public boolean enemySeen;
	protected boolean alerted = false;
	public boolean hordeSpawned = false;
	public int hordeHead = -1;

	protected static final float TIME_TO_WAKE_UP = 1f;

	private static final String STATE	= "state";
	private static final String SEEN	= "seen";
	private static final String TARGET	= "target";
	private static final String MAX_LVL	= "max_lvl";
	private static final String HORDE_HEAD = "hordeHead";
	private static final String HORDE_SPAWNED = "hordeSpawned";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}else if (state == FOLLOWING) {
			bundle.put( STATE, Following.TAG );
		}
		bundle.put( SEEN, enemySeen );
		bundle.put( TARGET, target );
		bundle.put( MAX_LVL, maxLvl );
		bundle.put( HORDE_HEAD, hordeHead);
		bundle.put( HORDE_SPAWNED, hordeSpawned);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		String state = bundle.getString( STATE );
		if (state.equals( Sleeping.TAG )) {
			this.state = SLEEPING;
		} else if (state.equals( Wandering.TAG )) {
			this.state = WANDERING;
		} else if (state.equals( Hunting.TAG )) {
			this.state = HUNTING;
		} else if (state.equals( Fleeing.TAG )) {
			this.state = FLEEING;
		} else if (state.equals( Passive.TAG )) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean( SEEN );

		target = bundle.getInt( TARGET );

		if (bundle.contains(MAX_LVL)) maxLvl = bundle.getInt(MAX_LVL);

		hordeHead = bundle.getInt(HORDE_HEAD);
		hordeSpawned = bundle.getBoolean(HORDE_SPAWNED);
	}

	public CharSprite sprite() {
		return Reflection.newInstance(spriteClass);
	}

	@Override
	protected boolean act() {

		super.act();

		boolean justAlerted = alerted;
		alerted = false;

		if (!hordeSpawned && hordeException() && Random.Int(7) == 0 && !Dungeon.bossLevel() && alignment == Alignment.ENEMY){

			int hordeSize = Math.min(3, Random.IntRange(1, Dungeon.depth / 8));
			for (int i = 0; i < hordeSize; i++) {

				ArrayList<Integer> candidates = new ArrayList<>();
				for (int n : PathFinder.NEIGHBOURS8) {
					if (Dungeon.level.map[pos+n] != Terrain.DOOR
							&& Dungeon.level.map[pos+n] != Terrain.SECRET_DOOR
							&& Dungeon.level.passable[pos+n]
						&& Actor.findChar(pos + n) == null) {
						candidates.add(pos + n);
					}
				}

				if (!candidates.isEmpty()) {
					Mob child = Dungeon.level.createMob();
					child.hordeHead = this.id();
					child.hordeSpawned = true;
					if (state != SLEEPING) {
						child.state = child.WANDERING;
					}
					child.HP = child.HT = child.HT/2;

					child.pos = Random.element(candidates);

					Dungeon.level.occupyCell(child);

					GameScene.add(child);
					if (sprite.visible) {
						Actor.addDelayed(new Pushing(child, pos, child.pos), -1);
					}
				}
			}
			if (!properties.contains(Property.BOSS)) HP = HT = HT*2;
		}
		hordeSpawned = true;

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

		if (buff(Terror.class) != null){
			state = FLEEING;
		}

		if (Dungeon.isChallenged(Conducts.Conduct.REGENERATION)){
			Buff.affect(this, Regeneration.class);
		}

		enemy = chooseEnemy();

		boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

		if (this instanceof Wraith) {
			if (((Wraith) this).parent != null) {
				if (((Wraith) this).parent.fieldOfView != null && !((Wraith) this).parent.fieldOfView[pos]) {
					die(Doom.class);
				}
			}
		}

		if (buff(StenchHolder.class) != null){
			GameScene.add(Blob.seed(pos, 50, WandOfStenchGas.class));
		}

		return state.act( enemyInFOV, justAlerted );
	}

	private boolean hordeException() {
		return EXP > 0 &&
				!(this instanceof Ghoul) && !(this instanceof Slime) &&
				!(this instanceof WardingWraith) && !(this instanceof Necromancer.NecroSkeleton) &&
				!(this instanceof RipperDemon) &&
				!Dungeon.isChallenged(Conducts.Conduct.LIMITED_MONSTERS);
	}

	//FIXME this is sort of a band-aid correction for allies needing more intelligent behaviour
	public boolean intelligentAlly = false;

	public boolean canSee(int pos){
		return fieldOfView[pos];
	}

	public Char chooseEnemy() {

		Terror terror = buff( Terror.class );
		if (terror != null) {
			Char source = (Char)Actor.findById( terror.object );
			if (source != null) {
				return source;
			}
		}

		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(this.hordeHead);
			if (hordeHead.isAlive()){
				return hordeHead.enemy;
			}
		}

		//if we are an enemy, and have no target or current target isn't affected by aggression
		//then auto-prioritize a target that is affected by aggression, even another enemy
		if (alignment == Alignment.ENEMY
				&& (enemy == null || enemy.buff(StoneOfAggression.Aggression.class) == null)) {
			for (Char ch : Actor.chars()) {
				if (ch != this && canSee(ch.pos) &&
						ch.buff(StoneOfAggression.Aggression.class) != null) {
					return ch;
				}
			}
		}

		//find a new enemy if..
		boolean newEnemy = false;
		//we have no enemy, or the current one is dead/missing
		if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
			newEnemy = true;
			//We are an ally, and current enemy is another ally.
		} else if (alignment == Alignment.ALLY && enemy.alignment == Alignment.ALLY) {
			newEnemy = true;
			//We are amoked and current enemy is the hero
		} else if (buff( Amok.class ) != null && enemy == Dungeon.hero) {
			newEnemy = true;
			//We are charmed and current enemy is what charmed us
		} else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id()) {
			newEnemy = true;
			//we aren't amoked, current enemy is invulnerable to us, and that enemy isn't affect by aggression
		} else if (buff( Amok.class ) == null && enemy.isInvulnerable(getClass()) && enemy.buff(StoneOfAggression.Aggression.class) == null) {
			newEnemy = true;
			//we can't attack our enemy and we are slime, give up with 50% chance
		} else if (enemy != null && !canAttack(enemy) && this instanceof Slime && Random.Int(2) == 0){
			newEnemy = true;
		}

		if ( newEnemy ) {

			HashSet<Char> enemies = new HashSet<>();

			//if the mob is amoked...
			if ( buff(Amok.class) != null) {
				//try to find an enemy mob to attack first.
				for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob.alignment == Alignment.ENEMY && mob != this
							&& canSee(mob.pos) && mob.invisible <= 0) {
						enemies.add(mob);
					}

				if (enemies.isEmpty()) {
					//try to find ally mobs to attack second, ignoring the soul flame
					for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
						if (mob.alignment == Alignment.ALLY && mob != this && canSee(mob.pos) && !canBeIgnored(mob))
							enemies.add(mob);
				}

				if (enemies.isEmpty()) {
					//try to find the hero third
					if (canSee(Dungeon.hero.pos) && Dungeon.hero.invisible <= 0) {
						enemies.add(Dungeon.hero);
					}
				}
			}

			//if the mob is an ally...
			else if ( alignment == Alignment.ALLY ) {
				//look for hostile mobs to attack
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob.alignment == Alignment.ENEMY && canSee(mob.pos)
							&& mob.invisible <= 0 && !mob.isInvulnerable(getClass()) && !canBeIgnored(mob))
							enemies.add(mob);

				//if the mob is an enemy...
			} else if (alignment == Alignment.ENEMY) {
				//look for ally mobs to attack, ignoring the soul flame
				for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob.alignment == Alignment.ALLY && canSee(mob.pos) && !canBeIgnored(mob) && !mob.isInvulnerable(getClass()))
						enemies.add(mob);


				//and look for the hero if there is no minions
				for (Char minion : enemies){
					if (minion instanceof Minion){
						if (((Minion) minion).isTanky) return minion;
					}
				}

				if (canSee(Dungeon.hero.pos)) {
					enemies.add(Dungeon.hero);
				}

			}

			Charm charm = buff( Charm.class );
			if (charm != null){
				Char source = (Char)Actor.findById( charm.object );
				if (source != null && enemies.contains(source) && enemies.size() > 1){
					enemies.remove(source);
				}
			}

			//neutral characters in particular do not choose enemies.
			if (enemies.isEmpty()){
				return null;
			} else {

				HashSet<Char> minions = new HashSet<>();
				for (Char enemy : enemies){
					if (enemy instanceof Minion) minions.add(enemy);
				}
				//go after the closest potential enemy, preferring the minion if two are equidistant
				if (minions.size() > 0) return chooseClosest(minions);

				return chooseClosest(enemies);
			}

		} else
			return enemy;
	}

	protected Char chooseClosest(HashSet<Char> enemies){
		Char closest = null;

		for (Char curr : enemies){
			if (closest == null
					|| Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
					|| Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos)){
				closest = curr;
			}
		}
		return closest;
	}

	public ArrayList<Class<? extends Char>> ignoreList(){
		ArrayList<Class<? extends Char>> ignored = new ArrayList<>();
		ignored.add(SoulFlame.class);
		return ignored;
	}

	public boolean canBeIgnored(Char ch){
		ArrayList<Class<? extends Char>> ignored = ignoreList();
		for (Class<? extends Char> clazz : ignored){
			if (ch.getClass().isAssignableFrom(clazz)) return true;
		}
		return false;
	}

	@Override
	public void add( Buff buff ) {
		super.add( buff );
		if (buff instanceof Amok || buff instanceof Corruption) {
			state = HUNTING;
		} else if (buff instanceof Terror) {
			state = FLEEING;
		} else if (buff instanceof Sleep) {
			state = SLEEPING;
			postpone( Sleep.SWS );
		}
	}

	@Override
	public void remove( Buff buff ) {
		super.remove( buff );
		if (buff instanceof Terror) {
			if (enemySeen) {
				sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"));
				state = HUNTING;
			} else {
				state = WANDERING;
			}
		}
	}

	protected boolean canAttack( Char enemy ) {
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			if (buff.canAttackWithExtraReach( enemy )){
				return true;
			}
			if (buff.getClass() == ChampionEnemy.Paladin.class){
				return false;
			}
		}
		return Dungeon.level.adjacent( pos, enemy.pos );
	}

	protected boolean getCloser( int target ) {

		if (rooted || target == pos) {
			return false;
		}

		if (buff(ChampionEnemy.Paladin.class) != null){
			return false;
		}

		int step = -1;

		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null
					&& (Dungeon.level.passable[target] || (flying && Dungeon.level.avoid[target]))
					&& (!Char.hasProp(this, Char.Property.LARGE) || Dungeon.level.openSpace[target])) {
				step = target;
			}

		} else {

			boolean newPath = false;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's extremely inefficient and checking again may result in a much better path
			if (path == null || path.isEmpty()
					|| !Dungeon.level.adjacent(pos, path.getFirst())
					|| path.size() > 2*Dungeon.level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (Dungeon.level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (Dungeon.level.adjacent(target, pos)) {
							path.add(target);
							//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

							//if the new target is closer/same, need to modify end of path
						} else if (Dungeon.level.adjacent(target, path.getLast())) {
							path.add(target);

							//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}

			//checks if the next cell along the current path can be stepped into
			if (!newPath) {
				int nextCell = path.removeFirst();
				if (!Dungeon.level.passable[nextCell]
						|| (!flying && Dungeon.level.avoid[nextCell])
						|| (Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[nextCell])
						|| Actor.findChar(nextCell) != null) {

					newPath = true;
					//If the next cell on the path can't be moved into, see if there is another cell that could replace it
					if (!path.isEmpty()) {
						for (int i : PathFinder.NEIGHBOURS8) {
							if (Dungeon.level.adjacent(pos, nextCell + i) && Dungeon.level.adjacent(nextCell + i, path.getFirst())) {
								if (Dungeon.level.passable[nextCell+i]
										&& (flying || !Dungeon.level.avoid[nextCell+i])
										&& (!Char.hasProp(this, Char.Property.LARGE) || Dungeon.level.openSpace[nextCell+i])
										&& Actor.findChar(nextCell+i) == null){
									path.addFirst(nextCell+i);
									newPath = false;
									break;
								}
							}
						}
					}
				} else {
					path.addFirst(nextCell);
				}
			}

			//generate a new path
			if (newPath) {
				//If we aren't hunting, always take a full path
				PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, true);
				if (state != HUNTING){
					path = full;
				} else {
					//otherwise, check if other characters are forcing us to take a very slow route
					// and don't try to go around them yet in response, basically assume their blockage is temporary
					PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, false);
					if (ignoreChars != null && (full == null || full.size() > 2*ignoreChars.size())){
						//check if first cell of shorter path is valid. If it is, use new shorter path. Otherwise do nothing and wait.
						path = ignoreChars;
						if (!Dungeon.level.passable[ignoreChars.getFirst()]
								|| (!flying && Dungeon.level.avoid[ignoreChars.getFirst()])
								|| (Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[ignoreChars.getFirst()])
								|| Actor.findChar(ignoreChars.getFirst()) != null) {
							return false;
						}
					} else {
						path = full;
					}
				}
			}

			if (path != null) {
				step = path.removeFirst();
			} else {
				return false;
			}
		}
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	protected boolean getFurther( int target ) {
		if (rooted || target == pos) {
			return false;
		}

		int step = Dungeon.flee( this, target, Dungeon.level.passable, fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	public boolean following(Char follow) {
		if (alignment == follow.alignment) {
			Char targetChar = Actor.findChar(this.target);
			return targetChar == follow;
		}
		return false;

	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
				|| Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null
				|| Dungeon.hero.buff(SoulOfYendor.timeFreeze.class) != null)
			sprite.add( CharSprite.State.PARALYSED );
		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
			if (hordeHead.isAlive()){
				sprite.add(CharSprite.State.SHRUNK);
			}
		}
	}

	protected float attackDelay() {
		float delay = 1f;
		if ( buff(Adrenaline.class) != null) delay /= 1.5f;
		if ( buff(Empowered.class) != null) delay /= 1.35f;
		return delay;
	}

	protected boolean doAttack( Char enemy ) {

		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.attack( enemy.pos );
			spend( attackDelay() );
			return false;

		} else {
			attack( enemy );
			spend( attackDelay() );
			return true;
		}
	}

	@Override
	public void onAttackComplete() {
		attack( enemy );
		super.onAttackComplete();
	}

	@Override
	public int defenseSkill( Char enemy ) {
		float modifier =1f;
		if (buff(Chungus.class) != null) modifier *= 0.6f;

		if (buff(Block.class) != null) return INFINITE_EVASION;

		boolean seen = (enemySeen && enemy.invisible == 0);
		if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack()) seen = true;
		if ( seen
				&& paralysed == 0
				&& !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
			return Math.round(this.defenseSkill * modifier);
		} else {
			return 0;
		}
	}

	protected boolean hitWithRanged = false;

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (enemy instanceof Hero && (((Hero) enemy).belongings.weapon instanceof MissileWeapon || ((Hero) enemy).belongings.weapon instanceof SpiritBow.SpiritArrow)){
			hitWithRanged = true;
		}

		if (surprisedBy(enemy)) {
			Statistics.sneakAttacks++;
			//TODO this is somewhat messy, it would be nicer to not have to manually handle delays here
			// playing the strong hit sound might work best as another property of weapon?
			if (Dungeon.hero.belongings.weapon instanceof SpiritBow.SpiritArrow
					|| Dungeon.hero.belongings.weapon instanceof Dart){
				Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_STRONG, 0.125f);
			} else {
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}
			if (enemy.buff(Preparation.class) != null) {
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//if attacked by something else than current target, and that thing is closer, switch targets
		if (this.enemy == null
				|| (enemy != this.enemy && (Dungeon.level.distance(pos, enemy.pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
			aggro(enemy);
			target = enemy.pos;
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP);

			//physical damage that doesn't come from the hero is less effective
			if (enemy != Dungeon.hero){
				restoration = Math.round(restoration * 0.4f);
			}

			Buff.affect(Dungeon.hero, Hunger.class).satisfy(restoration);
			if (Dungeon.mode != Dungeon.GameMode.HELL) {
				Dungeon.hero.HP = (int) Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (restoration * 0.4f)));
				Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
		}

		if (buff(ChampionEnemy.Reflective.class) != null){
			enemy.damage((int) (damage*0.5f), this);
		}

		if (buff(Chungus.class) != null) damage *= 0.6f;

		return damage;
	}

	public boolean surprisedBy( Char enemy ){
		return enemy == Dungeon.hero
				&& (enemy.invisible > 0 || !enemySeen)
				&& ((Hero)enemy).canSurpriseAttack();
	}

	public void aggro( Char ch ) {
		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
			if (hordeHead.isAlive()){
				enemy = hordeHead.enemy;
			}
		}
	}

	public boolean isTargeting( Char ch){
		return enemy == ch;
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == SLEEPING) {
			state = WANDERING;
		}
		if (state != HUNTING) {
			alerted = true;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.hordeHead == id()){
					mob.state = mob.HUNTING;
					mob.alerted = true;
				}
			}
		}
		if (buff(PerfumeGas.Affection.class) != null && src instanceof Char){
			buff(PerfumeGas.Affection.class).detach();
			Buff.affect(this, PerfumeGas.Aggression.class, 8f);
		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass())) lock.addTime(dmg/2);

		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(this.hordeHead);
			if (hordeHead.isAlive() && !(src instanceof Grim)){
					super.damage(dmg/2, src);
					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (mob.hordeHead == id()){
						mob.state = state;
					}
				}
					return;
			}
		}

		super.damage( dmg, src );
	}


	@Override
	public void destroy() {

		super.destroy();

		Dungeon.level.mobs.remove( this );

		if (Dungeon.hero.isAlive()) {

			if (alignment == Alignment.ENEMY) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;
				if (Dungeon.isChallenged(Conducts.Conduct.CURSE)){
					Dungeon.hero.busy();
					CursedWand.cursedZap(null, enemy != null ? enemy : Dungeon.hero, new Ballistica((enemy != null ? enemy : Dungeon.hero).pos, pos, Ballistica.MAGIC_BOLT), Dungeon.hero::ready);
				}

				int exp = Dungeon.hero.lvl <= maxLvl || Dungeon.mode == Dungeon.GameMode.LOL ? EXP : 0;
				if (Dungeon.mode == Dungeon.GameMode.NO_EXP) exp = 0;
				if (hordeHead != -1) exp = 0;
				if (exp > 0) {
					Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
				}
				Dungeon.hero.earnExp(exp, getClass());
			}
		}
	}

	public boolean canBeLethalMomented(){
		if (Dungeon.hero.hasTalent(Talent.LETHAL_MOMENTUM)){
			switch (Dungeon.hero.pointsInTalent(Talent.LETHAL_MOMENTUM)) {
				case 1:
					return surprisedBy(Dungeon.hero);
				case 2:
					if (surprisedBy(Dungeon.hero)) {
						Buff.affect(Dungeon.hero, AdrenalineSurge.class).reset(2, 4);
						return surprisedBy(Dungeon.hero);
					}
			}
		}
		return false;
	}

	@Override
	public void die( Object cause ) {

		if (hitWithRanged){
			Statistics.thrownAssists++;
		}

		if (cause == Chasm.class){
			//50% chance to round up, 50% to round down
			if (EXP % 2 == 1) EXP += Random.Int(2);
			EXP /= 2;
		}

		if ((((cause instanceof Minion || cause instanceof GoatClone) && (Dungeon.hero.heroClass == HeroClass.CONJURER || Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)))
				|| (cause instanceof Hero && buff(Knife.SoulGain.class) != null)) ||
					cause instanceof ConjurerSpell){
			int gain = (int) Math.floor(EXP*(Dungeon.hero.subClass == HeroSubClass.SOUL_REAVER ? 2.25f : 1.5f));
			if (cause instanceof ConjurerSpell) gain /= 2;
			Dungeon.hero.mana = Math.min(Dungeon.hero.mana + gain, Dungeon.hero.maxMana);
		}

		if (alignment == Alignment.ENEMY){
			if (!(cause instanceof Gravery)) rollToDropLoot();

			if ((Dungeon.hero.lvl <= maxLvl + 2 || Dungeon.mode == Dungeon.GameMode.LOL) && cause instanceof Gravery){
				EXP = 0;
				if (!(this instanceof Wraith)){
					Wraith w = Wraith.spawnForcefullyAt(pos);
					if (w != null) {
						Buff.affect(w, Corruption.class);
						if (Dungeon.level.heroFOV[pos]) {
							CellEmitter.get(pos).burst(ShadowParticle.CURSE, 6);
							Sample.INSTANCE.play(Assets.Sounds.CURSED);
						}
					}
				}
			}

			if (cause == Dungeon.hero && canBeLethalMomented()){
				Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 1f);
			}
		}

		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET && alignment == Alignment.ENEMY){
			if (this instanceof Thief){
				if (((Thief) this).item != null) {
					Dungeon.level.drop( ((Thief) this).item, pos ).sprite.drop();
					//updates position
					if (((Thief) this).item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)((Thief) this).item).dropPot( this, pos );
					((Thief) this).item = null;
				}
			}
			boolean mobsAlive = false;
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
				if (mob.isAlive() && mob.alignment == Alignment.ENEMY){
					mobsAlive = true;
				}
			}
			if (!mobsAlive && Dungeon.level.entrance == 0){
				Dungeon.level.drop(new SkeletonKey(Dungeon.depth), Dungeon.hero.pos).sprite.drop();
				Dungeon.level.drop(new Gold().quantity(100 + 5 * Dungeon.depth), Dungeon.hero.pos).sprite.drop();
				Dungeon.level.unseal();
			}
		}

		if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
			GLog.i( Messages.get(this, "died") );
		}
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob.hordeHead == id()){
				mob.state = mob.HUNTING;
				mob.enemy = enemy;
				mob.alerted = true;
			}
		}

		super.die( cause );
	}

	public void rollToDropLoot(){
		if (Dungeon.isChallenged(Conducts.Conduct.EXPLOSIONS)){
			((Bomb)(new Bomb().random())).explode(pos);
		}

		if (Dungeon.hero.lvl > maxLvl + 2 && Dungeon.mode != Dungeon.GameMode.LOL) return;

		float lootChance = this.lootChance;

		MasterThievesArmband.StolenTracker stolen = buff(MasterThievesArmband.StolenTracker.class);
		if (stolen == null || !stolen.itemWasStolen()) {
			if (Random.Float() < lootChance && Dungeon.mode != Dungeon.GameMode.GAUNTLET) {
				Item loot = createLoot();
				if (loot != null) {
					Dungeon.level.drop(loot, pos).sprite.drop();
				}
			}
			int rolls = 1;
			if (properties.contains(Property.BOSS)) rolls = 15;
			else if (properties.contains(Property.MINIBOSS)) rolls = 5;
			for (int i = 0; i < rolls; i++) {
				if (Dungeon.hero.buff(MasterThievesArmband.Thievery.class) != null &&
						Random.Int(15) < Dungeon.hero.buff(MasterThievesArmband.Thievery.class).itemLevel()){
					Dungeon.level.drop(RingOfWealth.genConsumableDrop(Dungeon.hero.buff(MasterThievesArmband.Thievery.class).itemLevel()), pos).sprite.drop();
					RingOfWealth.showFlareForBonusDrop(sprite);
				}
			}
		}

		if (Dungeon.mode == Dungeon.GameMode.LOL && Random.Float() < 0.4f){
			Dungeon.level.drop(Generator.random(), pos).sprite.drop();
		}

		if (this instanceof Monk || this instanceof Warlock){
			if (Random.Float() < 0.5f * RingOfWealth.dropChanceMultiplier( Dungeon.hero ) && Dungeon.mode != Dungeon.GameMode.GAUNTLET){
				Dungeon.level.drop(new GoldToken(), pos).sprite.drop();
			}
		}

		//lucky enchant logic
		if ((Dungeon.hero.lvl <= maxLvl || Dungeon.mode == Dungeon.GameMode.LOL) && buff(Lucky.LuckProc.class) != null){
			Dungeon.level.drop(Lucky.genLoot(), pos).sprite.drop();
			Lucky.showFlare(sprite);
		}
	}

	protected Object loot = null;
	public float lootChance = 0;

	@SuppressWarnings("unchecked")
	public Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.random( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}

	//how many mobs this one should count as when determining spawning totals
	public float spawningWeight(){
		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
			if (hordeHead.isAlive()){
				return 0;
			}
		}
		return 1;
	}

	public boolean reset() {
		return false;
	}

	public void beckon( int cell ) {

		notice();

		if (state != HUNTING && state != FLEEING) {
			state = WANDERING;
		}
		target = cell;
		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
			if (hordeHead.isAlive()){
				target = hordeHead.target;
			}
		}
	}

	public int defenseSkillDesc(){
		return defenseSkill;
	}

	public String description() {
		String desc = Messages.get(this, "desc");
		if (hordeHead != -1 && Actor.findById(hordeHead) != null){
			Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
			if (hordeHead.isAlive()){
				desc += "\n\n" + Messages.get(Mob.class, "horde");
			}
		}
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			String harderDesc = Messages.get(this, "harder");
			if (!harderDesc.equals(""))
				desc += "\n\n" + harderDesc;
		}
		desc += "\n\n" + Messages.get(Mob.class, "stats", HP, HT, attackSkill(Dungeon.hero), defenseSkillDesc());
		for (Buff b : buffs(ChampionEnemy.class)){
			desc += "\n\n_" + Messages.titleCase(b.toString()) + "_\n" + b.desc();
		}
		return desc;
	}

	public void notice() {
		if (sprite != null)
		sprite.showAlert();
	}

	public void yell( String str ) {
		GLog.negative( "%s: \"%s\" ", Messages.titleCase(getName()), str );
	}

	//returns true when a mob sees the hero, and is currently targeting them.
	public boolean focusingHero() {
		return enemySeen && (target == Dungeon.hero.pos);
	}

	public interface AiState {
		boolean act( boolean enemyInFOV, boolean justAlerted );
	}

	protected class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			if (enemyInFOV) {

				float enemyStealth = enemy.stealth();

				if (enemy instanceof Hero && ((Hero) enemy).hasTalent(Talent.LIFE_ON_AXIOM)){
					if (Dungeon.level.distance(pos, enemy.pos) >= 3 - ((Hero) enemy).pointsInTalent(Talent.LIFE_ON_AXIOM)) {
						enemyStealth = Float.POSITIVE_INFINITY;
						if (Dungeon.level.adjacent(pos, enemy.pos)) {
							if (buff(Talent.LifeOnAxiomTracker.class) != null) {
								enemyStealth = -distance(enemy);
								Buff.detach(Mob.this, Talent.LifeOnAxiomTracker.class);
							} else if (((Hero) enemy).pointsInTalent(Talent.LIFE_ON_AXIOM) == 2) {
								sprite.showStatus(CharSprite.NEGATIVE, "sus");
								Buff.affect(Mob.this, Talent.LifeOnAxiomTracker.class, 15);
							}
						}
					}
				}

				if (Random.Float( distance( enemy ) + enemyStealth ) < 1) {
					enemySeen = true;

					notice();
					state = HUNTING;
					target = enemy.pos;

					spend(TIME_TO_WAKE_UP);
					return true;
				}

			}

			PerfumeGas perfume = (PerfumeGas) Dungeon.level.blobs.get(PerfumeGas.class);
				if (perfume != null && !isImmune(PerfumeGas.Affection.class) && buff(PerfumeGas.Aggression.class) == null){
					state = WANDERING;
					spend(TIME_TO_WAKE_UP);
				}
			spend( TICK );

			return true;
		}
	}

	protected class Wandering implements AiState {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Float( distance( enemy ) / 2f + enemy.stealth() ) < 1) && buff(PerfumeGas.Affection.class) == null) {

				return noticeEnemy();

			} else {

				return continueWandering();

			}
		}

		protected boolean noticeEnemy(){
			enemySeen = true;

			notice();
			alerted = true;
			state = HUNTING;
			target = enemy.pos;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.hordeHead == id()){
					mob.state = mob.HUNTING;
					mob.alerted = true;
					mob.beckon(target);
				}
			}

			return true;
		}

		protected boolean continueWandering(){
			enemySeen = false;

			int oldPos = pos;
			if (target != -1 && getCloser( target )) {
				if (Dungeon.level.water[pos] && buff(ChampionEnemy.Flowing.class) != null){
					spend(0.01f / speed());
				}
				else spend( 1 / speed() );
				return moveSprite( oldPos, pos );
			} else {
				PerfumeGas perfume = (PerfumeGas) Dungeon.level.blobs.get(PerfumeGas.class);
				if (perfume != null && perfume.volume > 0) {
					if (!isImmune(PerfumeGas.Affection.class) && buff(PerfumeGas.Aggression.class) == null) {
						Char ch = Actor.findChar(pos);
						do {
							int cell = Dungeon.level.randomDestination(Mob.this);
							if (perfume.volume > 0 && perfume.cur[cell] > 0){

								if (ch instanceof Mob) ((Mob)ch).target = cell;
								Buff.affect(ch, PerfumeGas.Affection.class, 6);
								break;
							}
						} while (true);
					}
				} else {
					target = Dungeon.level.randomDestination(Mob.this);
					if (buff(PerfumeGas.Affection.class) != null) {
						buff(PerfumeGas.Affection.class).detach();
					}
				}
				if (hordeHead != -1 && Actor.findById(hordeHead) != null){
					Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
					if (hordeHead.isAlive()){
						target = hordeHead.target;
					}
				}
				spend( TICK );
			}

			return true;
		}

	}

	protected class Hunting implements AiState {

		public static final String TAG	= "HUNTING";

		//prevents rare infinite loop cases
		private boolean recursing = false;

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (buff(PerfumeGas.Affection.class) != null){
				state = WANDERING;
				return true;
			}
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				target = enemy.pos;
				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					sprite.showLost();
					state = WANDERING;
					target = Dungeon.level.randomDestination( Mob.this );
					if (hordeHead != -1 && Actor.findById(hordeHead) != null){
						Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
						if (hordeHead.isAlive()){
							target = hordeHead.target;
						}
					}
					spend( TICK );
					return true;
				}

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					if (Dungeon.level.water[pos] && buff(ChampionEnemy.Flowing.class) != null){
						spend(0.01f / speed());
					}
					else spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					//if moving towards an enemy isn't possible, try to switch targets to another enemy that is closer
					//unless we have already done that and still can't move toward them, then move on.
					if (!recursing) {
						Char oldEnemy = enemy;
						enemy = null;
						enemy = chooseEnemy();
						if (enemy != null && enemy != oldEnemy) {
							recursing = true;
							boolean result = act(enemyInFOV, justAlerted);
							recursing = false;
							return result;
						}
					}

					spend( TICK );
					if (!enemyInFOV) {
						sprite.showLost();
						if (Dungeon.isChallenged(Conducts.Conduct.SLEEPY)){
							state = SLEEPING;
						}
						else state = WANDERING;
						target = Dungeon.level.randomDestination( Mob.this );
						if (hordeHead != -1 && Actor.findById(hordeHead) != null){
							Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
							if (hordeHead.isAlive()){
								target = hordeHead.target;
							}
						}
					}
					return true;
				}
			}
		}
	}

	//FIXME this works fairly well but is coded poorly. Should refactor
	protected class Fleeing implements AiState {

		public static final String TAG	= "FLEEING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			//loses target when 0-dist rolls a 6 or greater.
			if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6){
				target = -1;

				//if enemy isn't in FOV, keep running from their previous position.
			} else if (enemyInFOV) {
				target = enemy.pos;
			}
			if (hordeHead != -1 && Actor.findById(hordeHead) != null){
				Mob hordeHead = (Mob) Actor.findById(Mob.this.hordeHead);
				if (hordeHead.isAlive()){
					target = hordeHead.target;
				}
			}

			int oldPos = pos;
			if (target != -1 && getFurther( target )) {

				if (Dungeon.level.water[pos] && buff(ChampionEnemy.Flowing.class) != null){
					spend(0.01f / speed());
				}
				else spend( 1 / speed() );
				return moveSprite( oldPos, pos );

			} else {

				spend( TICK );
				nowhereToRun();

				return true;
			}
		}

		protected void nowhereToRun() {
		}
	}

	protected class Passive implements AiState {

		public static final String TAG	= "PASSIVE";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			spend( TICK );
			return true;
		}
	}

	public class Following extends Wandering implements AiState {

		private Char toFollow(Char start) {
			Char toFollow = start;
			boolean[] passable = Dungeon.level.passable;
			PathFinder.buildDistanceMap(pos, passable, Integer.MAX_VALUE);//No limit on distance
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.alignment == alignment && PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] && mob.following(toFollow)) {
					toFollow = toFollow(mob);//If we find a mob already following the target, ensure there is not a mob already following them. This allows even massive chains of allies to traverse corridors correctly.
				}
			}
			return toFollow;
		}

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			//Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
			if ( enemyInFOV && Dungeon.level.distance(pos, enemy.pos) < 6) {

				enemySeen = true;

				notice();
				alerted = true;

				state = HUNTING;
				target = enemy.pos;

			} else {

				enemySeen = false;
				Char toFollow = toFollow(Dungeon.hero);
				int oldPos = pos;
				//always move towards the target when wandering
				if (getCloser( target = toFollow.pos )) {
					if (!Dungeon.level.adjacent(toFollow.pos, pos) && Actor.findChar(pos) == null) {
						getCloser( target = toFollow.pos );
					}
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					spend( TICK );
				}

			}
			return true;
		}

	}

	public static class MagicalAttack {
		public Mob caster;
		public int damage;

		public MagicalAttack(Mob attacker, int damage){
			caster = attacker;
			this.damage = damage;
		}
	}

	private static ArrayList<Mob> heldAllies = new ArrayList<>();

	public static void holdAllies( Level level ){
		heldAllies.clear();
		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			//preserve the ghost no matter where they are
			if (mob instanceof DriedRose.GhostHero) {
				((DriedRose.GhostHero) mob).clearDefensingPos();
				level.mobs.remove( mob );
				heldAllies.add(mob);

				//preserve intelligent allies if they are near the hero
			} else if (mob.alignment == Alignment.ALLY
					&& mob.intelligentAlly){
				level.mobs.remove( mob );
				if (mob instanceof Minion) ((Minion) mob).onLeaving();
				heldAllies.add(mob);
			}
		}
	}

	public static void restoreAllies( Level level, int pos ){
		if (!heldAllies.isEmpty()){

			ArrayList<Integer> candidatePositions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8) {
				if (!Dungeon.level.solid[i+pos] && level.findMob(i+pos) == null){
					candidatePositions.add(i+pos);
				}
			}
			Collections.shuffle(candidatePositions);

			for (Mob ally : heldAllies) {
				level.mobs.add(ally);
				ally.state = ally.WANDERING;

				if (!candidatePositions.isEmpty()){
					ally.pos = candidatePositions.remove(0);
				} else {
					ally.pos = pos;
				}

			}
		}
		heldAllies.clear();
	}

	public static void clearHeldAllies(){
		heldAllies.clear();
	}
}

