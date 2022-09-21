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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GonerField;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.FierySlash;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SpikyShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Phantom;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities.Endure;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MirrorOfFates;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.ParchmentOfElbereth;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.*;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cleaver;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndExploreResurrect;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Hero extends Char {

	{
		actPriority = HERO_PRIO;
		
		alignment = Alignment.ALLY;

		immunities.add(PerfumeGas.Affection.class);
		immunities.add(GonerField.class);
		if (Dungeon.mode == Dungeon.GameMode.HELL){
			immunities.add(ShieldBuff.class);
			immunities.add(Healing.class);
		}
	}
	
	public static int MAX_LEVEL = 30;

	public static final int STARTING_STR = 10;
	
	private static final float TIME_TO_REST		    = 1f;
	private static final float TIME_TO_SEARCH	    = 2f;
	private static final float HUNGER_FOR_SEARCH	= 15f;
	
	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;
	
	private int attackSkill = 10;
	private int defenseSkill = 5;

	public boolean ready = false;
	private boolean damageInterrupt = true;
	public HeroAction curAction = null;
	public HeroAction lastAction = null;

	private Char enemy;
	
	public boolean resting = false;
	
	public Belongings belongings;
	
	public int STR;
	
	public float attunement = 1;
	public float usedAttunement;

	public ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();

	public int mana = 0;
	public int maxMana = 0;

	public float attunement(){
	    return attunement + RingOfAttunement.attunementMultiplier(this) + (subClass == HeroSubClass.SOUL_REAVER ? 1 : 0) + (Dungeon.isChallenged(Conducts.Conduct.KING) ? 1 : 0);
    }
	
	public int lvl = 1;
	public int exp = 0;
	public int totalExp = 0;
	
	public int HTBoost = 0;

	public int lastMovPos = -1;

	private ArrayList<Mob> visibleEnemies;

	//This list is maintained so that some logic checks can be skipped
	// for enemies we know we aren't seeing normally, resultign in better performance
	public ArrayList<Mob> mindVisionEnemies = new ArrayList<>();

	public Hero() {
		super();

		HP = HT = 20;
		STR = STARTING_STR;
		
		belongings = new Belongings( this );
		
		visibleEnemies = new ArrayList<>();
	}
	
	public void updateHT( boolean boostHP ){
		int curHT = HT;

		float adjustHT = heroClass == HeroClass.CONJURER ? 10 : 20;
        float adjustScaling = heroClass == HeroClass.CONJURER ? 2 : 5;
        if (heroClass == HeroClass.ADVENTURER){
        	adjustHT = 30;
		}

		HT = (int) ((adjustHT + adjustScaling*(lvl-1)) + HTBoost);
		float multiplier = RingOfMight.HTMultiplier(this);
		HT = Math.round(multiplier * HT);
		
		if (buff(ElixirOfMight.HTBoost.class) != null){
			HT += buff(ElixirOfMight.HTBoost.class).boost();
		}
		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) HT = 1;
		Item.updateQuickslot();

		if (boostHP){
			HP += Math.max(HT - curHT, 0);
		}
		HP = Math.min(HP, HT);
	}

	public int STR() {
		int STR = this.STR;

		STR += RingOfMight.strengthBonus( this );
		
		AdrenalineSurge buff = buff(AdrenalineSurge.class);
		if (buff != null){
			STR += buff.boost();
		}

		return STR;
	}

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String STRENGTH	= "STR";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
	private static final String HTBOOST     = "htboost";
    private static final String ATTUNEMENT		= "attunement";
    private static final String USED_ATTUNEMENT		= "used_attunement";
	private static final String LASTMOVE = "last_move";
	private static final String MANA = "mana";
	private static final String MAX_MANA = "max_mana";
	private static final String TOTAL_EXP = "totalExp";
	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		heroClass.storeInBundle( bundle );
		subClass.storeInBundle( bundle );
		if (heroClass == HeroClass.ROGUE)
		Talent.storeTalentsInBundle( bundle, this );
		
		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );
		
		bundle.put( STRENGTH, STR );
		
		bundle.put( LEVEL, lvl );
		bundle.put(ATTUNEMENT, attunement);
		bundle.put(USED_ATTUNEMENT, usedAttunement);
		bundle.put( EXPERIENCE, exp );
		
		bundle.put( HTBOOST, HTBoost );
		bundle.put( LASTMOVE, lastMovPos);

		bundle.put( MANA, mana);
		bundle.put( MAX_MANA, maxMana);
		bundle.put( TOTAL_EXP, totalExp);

		belongings.storeInBundle( bundle );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );

		heroClass = HeroClass.restoreInBundle( bundle );
		subClass = HeroSubClass.restoreInBundle( bundle );
		if (heroClass == HeroClass.ROGUE)
		Talent.restoreTalentsFromBundle( bundle, this );
		
		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );
		
		STR = bundle.getInt( STRENGTH );

		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );
		totalExp = bundle.getInt( TOTAL_EXP);
		attunement = bundle.getFloat(ATTUNEMENT);
        usedAttunement = bundle.getFloat(USED_ATTUNEMENT);

		HTBoost = bundle.getInt(HTBOOST);

		lastMovPos = bundle.getInt(LASTMOVE);

		mana = bundle.getInt(MANA);
		maxMana = bundle.getInt(MAX_MANA);

		belongings.restoreFromBundle( bundle );
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.level = bundle.getInt( LEVEL );
		info.str = bundle.getInt( STRENGTH );
		info.exp = bundle.getInt( EXPERIENCE );
		info.hp = bundle.getInt( Char.TAG_HP );
		info.ht = bundle.getInt( Char.TAG_HT );
		info.shld = bundle.getInt( Char.TAG_SHLD );
		info.heroClass = HeroClass.restoreInBundle( bundle );
		info.subClass = HeroSubClass.restoreInBundle( bundle );
		Belongings.preview( info, bundle );
	}

	public boolean hasTalent( Talent talent ){
		return pointsInTalent(talent) > 0;
	}

	public int pointsInTalent( Talent talent ){
		for (LinkedHashMap<Talent, Integer> tier : talents){
			for (Talent f : tier.keySet()){
				if (f == talent) return tier.get(f);
			}
		}
		return 0;
	}

	public void upgradeTalent( Talent talent ){
		for (LinkedHashMap<Talent, Integer> tier : talents){
			for (Talent f : tier.keySet()){
				if (f == talent) tier.put(talent, tier.get(talent)+1);
			}
		}
	}

	public int talentPointsSpent(int tier){
		int total = 0;
		for (int i : talents.get(tier-1).values()){
			total += i;
		}
		return total;
	}

	public int talentPointsAvailable(int tier){
		if (heroClass != HeroClass.ROGUE) return 0;
		if (lvl < Talent.tierLevelThresholds[tier]
				|| (tier == 3 && subClass == HeroSubClass.NONE)){
			return 0;
		} else if (lvl >= Talent.tierLevelThresholds[tier+1]){
			return Talent.tierLevelThresholds[tier+1] - Talent.tierLevelThresholds[tier] - talentPointsSpent(tier);
		} else {
			return 1 + lvl - Talent.tierLevelThresholds[tier] - talentPointsSpent(tier);
		}
	}
	
	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title() : subClass.title();
	}

	@Override
	public String name(){
		return className();
	}

	@Override
	public void hitSound(float pitch) {
		if ( belongings.weapon != null ){
			belongings.weapon.hitSound(pitch);
		} else if (RingOfForce.getBuffedBonus(this, RingOfForce.Force.class) > 0) {
			//pitch deepens by 2.5% (additive) per point of strength, down to 75%
			super.hitSound( pitch * GameMath.gate( 0.75f, 1.25f - 0.025f*STR(), 1f) );
		} else {
			super.hitSound(pitch * 1.1f);
		}
	}

	@Override
	public boolean blockSound(float pitch) {
		if ( (belongings.weapon != null && belongings.weapon.defenseFactor(this) >= 4) || buff(Block.class) != null){
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, pitch);
			return true;
		}
		return super.blockSound(pitch);
	}

	public void live() {
		if (!Dungeon.isChallenged(Conducts.Conduct.NO_REGEN))
			Buff.affect( this, Regeneration.class );
		Buff.affect( this, Hunger.class );
	}
	
	public int tier() {
		return belongings.armor == null ? 0 : belongings.armor.tier;
	}
	
	public boolean shoot( Char enemy, MissileWeapon wep ) {

		//temporarily set the hero's weapon to the missile weapon being used
		belongings.stashedWeapon = belongings.weapon;
		belongings.weapon = wep;
		boolean hit = false;
		if (enemy.alignment != Alignment.ALLY || wep.strikeAllies)
			hit = attack( enemy );
		Invisibility.dispel();
		belongings.weapon = belongings.stashedWeapon;
		belongings.stashedWeapon = null;

		return hit;
	}

	//same, but with knife
    //I am lazy to implement interfaces
    public boolean shoot(Char enemy, Knife knife) {

        //temporarily set the hero's weapon to the missile weapon being used
        KindOfWeapon equipped = belongings.weapon;
        belongings.weapon = knife;
        knife.ranged = true;
		boolean hit = false;
		if (enemy.alignment != Alignment.ALLY)
			hit = attack( enemy );
        Invisibility.dispel();
        belongings.weapon = equipped;

        return hit;
    }

	@Override
	public int attackSkill( Char target ) {
		KindOfWeapon wep = belongings.weapon;
		
		float accuracy = 1;
		if (Dungeon.mode == Dungeon.GameMode.EXPLORE) accuracy = 1.2f;
		if (Dungeon.isChallenged(Conducts.Conduct.KING)) accuracy = 1.1f;
		accuracy *= RingOfAccuracy.accuracyMultiplier( this );
		
		if (wep instanceof MissileWeapon || (wep instanceof Knife && ((Knife) wep).ranged)){
			if (Dungeon.level.adjacent( pos, target.pos )) {
				accuracy *= 0.5f;
			} else {
				accuracy *= 1.5f;
			}
		}

		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) accuracy *= 1.25f;

		if (wep != null) {
			return (int)(attackSkill * accuracy * wep.accuracyFactor( this ));
		} else {
			return (int)(attackSkill * accuracy);
		}
	}

	@Override
	public int attackRolls() {
		if ((Dungeon.hero.subClass == HeroSubClass.SNIPER) && (belongings.weapon instanceof MissileWeapon)){
			return 2;
		}
		return super.attackRolls();
	}

	public int getAttackSkill(){
	    return attackSkill;
    }

	@Override
	public int defenseSkill( Char enemy ) {
		
		float evasion = defenseSkill;
		if (Dungeon.mode == Dungeon.GameMode.EXPLORE) evasion *= 1.2f;

		evasion *= RingOfEvasion.evasionMultiplier( this );
		
		if (paralysed > 0) {
			evasion /= 2;
		}

		if (belongings.armor != null) {
			evasion = belongings.armor.evasionFactor(this, evasion);
		}

		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) evasion *= 5;

		if (buff(Block.class) != null) return INFINITE_EVASION;

		return Math.round(evasion);
	}

	@Override
	public int defenseRolls() {
		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)){
			return 2;
		}
		return super.defenseRolls();
	}

	@Override
	public String defenseVerb() {
		return super.defenseVerb();
	}

	@Override
	public int drRoll() {
		int dr = 0;

		if (belongings.armor != null) {
			int armDr = Random.NormalIntRange( belongings.armor.DRMin(), belongings.armor.DRMax());
			if (STR() < belongings.armor.STRReq()){
				armDr -= 2*(belongings.armor.STRReq() - STR());
			}
			if (armDr > 0) dr += armDr;
		}
		if (belongings.weapon != null)  {
			int wepDr = Random.NormalIntRange( 0 , belongings.weapon.defenseFactor( this ) );
			if (STR() < ((Weapon)belongings.weapon).STRReq()){
				wepDr -= 2*(((Weapon)belongings.weapon).STRReq() - STR());
			}
			if (wepDr > 0) dr += wepDr;
		}
		Barkskin bark = buff(Barkskin.class);
		if (bark != null)               dr += Random.NormalIntRange( 0 , bark.level() );

		Blocking.BlockBuff block = buff(Blocking.BlockBuff.class);
		if (block != null)              dr += block.blockingRoll();

		if (Dungeon.isChallenged(Conducts.Conduct.KING)) dr += Random.NormalIntRange(0, Dungeon.hero.lvl/2);
		
		return dr;
	}
	
	@Override
	public int damageRoll() {
		KindOfWeapon wep = belongings.weapon;
		int dmg;

		if (wep != null) {
			dmg = wep.damageRoll( this );
			if (!(wep instanceof MissileWeapon)) dmg += RingOfForce.armedDamageBonus(this);
		} else {
			dmg = RingOfForce.damageRoll(this);
		}
		if (dmg < 0) dmg = 0;
		
		Berserk berserk = buff(Berserk.class);
		if (berserk != null) dmg = berserk.damageFactor(dmg);
		Endure.EndureTracker endure = buff(Endure.EndureTracker.class);
		if (endure != null) {
			dmg = endure.damageFactor(dmg);
		}

		if (buff(SoulWeakness.class) != null) dmg /= 4;
		if (subClass == HeroSubClass.SOUL_REAVER) dmg *= 0.75f;

		return buff( Fury.class ) != null ? (int)(dmg * 1.5f) : dmg;
	}
	
	@Override
	public float speed() {

		float speed = super.speed();

		speed *= RingOfHaste.speedMultiplier(this);
		
		if (belongings.armor != null) {
			speed = belongings.armor.speedFactor(this, speed);
		}
		
		Momentum momentum = buff(Momentum.class);
		if (momentum != null){
			((HeroSprite)sprite).sprint( 1f + 0.05f*momentum.stacks());
			speed *= momentum.speedMultiplier();
		}

		if (Dungeon.isChallenged(Conducts.Conduct.CRIPPLED)) speed/=2;
		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) speed *= 1.25f;
		
		return speed;
		
	}

	public boolean canSurpriseAttack(){
		if (belongings.weapon == null || !(belongings.weapon instanceof Weapon))    return true;
		if (STR() < ((Weapon)belongings.weapon).STRReq())                           return false;
		if (belongings.weapon instanceof Flail || belongings.weapon instanceof Cleaver)                                     return false;

		return true;
	}

	public boolean canAttack(Char enemy){
		if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) return false;
        if (buff(SoulWeakness.class) != null) return false;
		if (enemy == null || pos == enemy.pos) {
			return false;
		}

		//can always attack adjacent enemies
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}

		KindOfWeapon wep = Dungeon.hero.belongings.weapon;



		if (wep != null){
			return wep.canReach(this, enemy.pos);
		} else {
			return false;
		}
	}
	
	public float attackDelay() {
		if (buff(Talent.LethalMomentumTracker.class) != null){
			buff(Talent.LethalMomentumTracker.class).detach();
			return 0;
		}

		float adrenalineMod = 1f;
		if ( buff(Adrenaline.class) != null) adrenalineMod = 1.5f;
		if (belongings.weapon != null) {
			return belongings.weapon.speedFactor( this )/adrenalineMod;
			
		} else {
			//Normally putting furor speed on unarmed attacks would be unnecessary
			//But there's going to be that one guy who gets a furor+force ring combo
			//This is for that one guy, you shall get your fists of fury!
			return RingOfFuror.attackDelayMultiplier(this)/adrenalineMod;
		}
	}

	@Override
	public void spend( float time ) {
		justMoved = false;
		TimekeepersHourglass.TimeFreezing freeze = Dungeon.hero.buff( TimekeepersHourglass.TimeFreezing.class );
		if (freeze != null) {
			freeze.processTime(time);
			return;
		}

		super.spend(time);

		if (time > 0) {
			GameScene.resetTimer();

		}
	}
	
	public void spendAndNext( float time ) {
		busy();
		spend( time );
		next();
	}
	
	@Override
	public boolean act() {
		
		//calls to dungeon.observe will also update hero's local FOV.
		fieldOfView = Dungeon.level.heroFOV;

		if (buff(Endure.EndureTracker.class) != null){
			buff(Endure.EndureTracker.class).endEnduring();
		}
		
		if (!ready) {
			//do a full observe (including fog update) if not resting.
			if (!resting || buff(MindVision.class) != null || buff(Awareness.class) != null) {
				Dungeon.observe();
			} else {
				//otherwise just directly re-calculate FOV
				Dungeon.level.updateFieldOfView(this, fieldOfView);
			}
		}
		
		checkVisibleMobs();
		BuffIndicator.refreshHero();
		
		if (paralysed > 0) {
			
			curAction = null;
			
			spendAndNext( TICK );
            Hunger.adjustHunger(-0.35f  * (buff(Shadows.class) != null ? 0.66f : 1f));
			return false;
		}
		if (!(curAction instanceof HeroAction.Move))
			if (buff(Talent.DirectiveMovingTracker.class) != null) {
				if (buff(Talent.DirectiveMovingTracker.class).count() > -1) {
					Buff.detach(this, Talent.DirectiveMovingTracker.class);
				} else {
					buff(Talent.DirectiveMovingTracker.class).countUp(1);
				}
			}

		boolean actResult;
		if (curAction == null) {
			
			if (resting) {
				spend( TIME_TO_REST );
				Hunger.adjustHunger(-0.35f  * (buff(Shadows.class) != null ? 0.66f : 1f));
				    next();
			} else {
				ready();
			}
			
			actResult = false;
			
		} else {
			
			resting = false;
			
			ready = false;
			
			if (curAction instanceof HeroAction.Move) {
				actResult = actMove( (HeroAction.Move)curAction );
				
			} else if (curAction instanceof HeroAction.Interact) {
				actResult = actInteract( (HeroAction.Interact)curAction );
				
			} else if (curAction instanceof HeroAction.Buy) {
				actResult = actBuy( (HeroAction.Buy)curAction );
				
			}else if (curAction instanceof HeroAction.PickUp) {
				actResult = actPickUp( (HeroAction.PickUp)curAction );
				
			} else if (curAction instanceof HeroAction.OpenChest) {
				actResult = actOpenChest( (HeroAction.OpenChest)curAction );
				
			} else if (curAction instanceof HeroAction.Unlock) {
				actResult = actUnlock((HeroAction.Unlock) curAction);
				
			} else if (curAction instanceof HeroAction.Descend) {
				actResult = actDescend( (HeroAction.Descend)curAction );
				
			} else if (curAction instanceof HeroAction.Ascend) {
				actResult = actAscend( (HeroAction.Ascend)curAction );
				
			} else if (curAction instanceof HeroAction.Attack) {
				actResult = actAttack( (HeroAction.Attack)curAction );
				
			} else if (curAction instanceof HeroAction.Alchemy) {
				actResult = actAlchemy( (HeroAction.Alchemy)curAction );
				
			} else {
				actResult = false;
			}
		}
		
		if( subClass == HeroSubClass.WARDEN && Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
			Buff.affect(this, Barkskin.class).set( lvl + 5, 1 );
		}

        if (heroClass == HeroClass.CONJURER) {
            if (HP <= HT / 2) {
                Buff.affect(this, Attunement.class);
            }
            else if (buff(Attunement.class) != null) buff(Attunement.class).detach();
        }

        if (subClass == HeroSubClass.OCCULTIST && GoatClone.findClone() == null){
        	GoatClone.spawnClone();
		}

		return actResult;
	}
	
	public void busy() {
		ready = false;
		GameScene.timerPaused=true;
	}
	
	private void ready() {
		if (sprite.looping()) sprite.idle();
		curAction = null;
		damageInterrupt = true;
		ready = true;

		AttackIndicator.updateState();


		GameScene.ready();
	}
	
	public void interrupt() {
		if (isAlive() && curAction != null &&
			((curAction instanceof HeroAction.Move && curAction.dst != pos) ||
			(curAction instanceof HeroAction.Ascend || curAction instanceof HeroAction.Descend))) {
			lastAction = curAction;
		}
		curAction = null;
		GameScene.resetKeyHold();
	}
	
	public void resume() {
		curAction = lastAction;
		lastAction = null;
		damageInterrupt = false;
		next();
	}

	private boolean actMove( HeroAction.Move action ) {

		if (getCloser( action.dst )) {
			Hunger.adjustHunger(-1.33f/speed());
			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actInteract( HeroAction.Interact action ) {
		
		Char ch = action.ch;

		if (ch.canInteract(this)) {
			
			ready();
			sprite.turnTo( pos, ch.pos );
			return ch.interact(this);
			
		} else {
			
			if (fieldOfView[ch.pos] && getCloser( ch.pos )) {

				return true;

			} else {
				ready();
				return false;
			}
			
		}
	}
	
	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst) {

			ready();
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndTradeItem( heap ) );
					}
				});
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actAlchemy( HeroAction.Alchemy action ) {
		int dst = action.dst;
		if (Dungeon.level.distance(dst, pos) <= 1) {

			ready();
			
			AlchemistsToolkit.ToolkitBuff kit = buff(AlchemistsToolkit.kitEnergy.class);
			if (kit == null) kit = buff(SoulOfYendor.omniBuff.class);
			if (kit != null && kit.isCursed()){
				GLog.warning( Messages.get(AlchemistsToolkit.class, "cursed"));
				return false;
			}
			
			Alchemy alch = (Alchemy) Dungeon.level.blobs.get(Alchemy.class);
			if (alch != null) {
				alch.alchPos = dst;
				AlchemyScene.setProvider( alch );
			}
			ShatteredPixelDungeon.switchScene(AlchemyScene.class);
			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {
				Item item = heap.peek();
				if (item.doPickUp( this )) {
					heap.pickUp();

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key) {
						//Do Nothing
					} else {

						//TODO make all unique items important? or just POS / SOU?
						boolean important = item.unique && item.isIdentified() &&
								(item instanceof Scroll || item instanceof Potion);
						if (important) {
							GLog.positive( Messages.get(this, "you_now_have", item.name()) );
						} else {
							GLog.i( Messages.get(this, "you_now_have", item.name()) );
						}
					}

					curAction = null;
				} else {

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key) {
						//Do Nothing
					} else {
						//TODO temporary until 0.8.0a, when all languages will get this phrase
						if (Messages.lang() == Languages.ENGLISH) {
							GLog.newLine();
							GLog.negative(Messages.get(this, "you_cant_have", item.name()));
						}
					}

					heap.sprite.drop();
					ready();
				}
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Dungeon.level.adjacent( pos, dst ) || pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Type.HEAP && heap.type != Type.FOR_SALE)) {
				
				if ((heap.type == Type.LOCKED_CHEST && Notes.keyCount(new GoldenKey(Dungeon.depth)) < 1)
					|| (heap.type == Type.CRYSTAL_CHEST && Notes.keyCount(new CrystalKey(Dungeon.depth)) < 1)){

						GLog.warning( Messages.get(this, "locked_chest") );
						ready();
						return false;

				}
				
				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.Sounds.TOMB );
					Camera.main.shake( 1, 0.5f );
					break;
				case SKELETON:
				case REMAINS:
					break;
				default:
					Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				}
				
				sprite.operate( dst );
				
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Dungeon.level.adjacent( pos, doorCell )) {
			
			boolean hasKey = false;
			int door = Dungeon.level.map[doorCell];
			
			if (door == Terrain.LOCKED_DOOR
					&& Notes.keyCount(new IronKey(Dungeon.depth)) > 0) {
				
				hasKey = true;
				
			} else if (door == Terrain.LOCKED_EXIT
					&& Notes.keyCount(new SkeletonKey(Dungeon.depth)) > 0) {

				hasKey = true;
				
			}
			
			if (hasKey) {
				
				sprite.operate( doorCell );
				
				Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				
			} else {
				GLog.warning( Messages.get(this, "locked_door") );
				ready();
			}

			return false;

		} else if (getCloser( doorCell )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actDescend( HeroAction.Descend action ) {
		int stairs = action.dst;

		if (rooted) {
			Camera.main.shake(1, 1f);
			ready();
			return false;
		//there can be multiple exit tiles, so descend on any of them
		//TODO this is slightly brittle, it assumes there are no disjointed sets of exit tiles
		} else if ((Dungeon.level.map[pos] == Terrain.EXIT || Dungeon.level.map[pos] == Terrain.UNLOCKED_EXIT)) {
			
			curAction = null;
			Hunger.adjustHunger(-30/speed());
			lastMovPos = -1;

			TimekeepersHourglass.TimeFreezing timeFreeze = Dungeon.hero.buff( TimekeepersHourglass.TimeFreezing.class );
			if (timeFreeze != null) timeFreeze.detach();
			
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			Game.switchScene( InterlevelScene.class );

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAscend( HeroAction.Ascend action ) {
		int stairs = action.dst;


		if (rooted){
			Camera.main.shake( 1, 1f );
			ready();
			return false;
		//there can be multiple entrance tiles, so descend on any of them
		//TODO this is slightly brittle, it assumes there are no disjointed sets of entrance tiles
		} else if (Dungeon.level.map[pos] == Terrain.ENTRANCE) {
			
			if (Dungeon.depth == 1) {
				
				if (belongings.getItem( Amulet.class ) == null) {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							GameScene.show( new WndMessage( Messages.get(Hero.this, "leave") ) );
						}
					});
					ready();
				} else {
					Badges.silentValidateHappyEnd();
					Dungeon.win( Amulet.class );
					Dungeon.deleteGame( GamesInProgress.curSlot, true );
					Game.switchScene( SurfaceScene.class );
				}
				
			} else {
				
				curAction = null;
				Hunger.adjustHunger(-30/speed());
				lastMovPos = -1;

				TimekeepersHourglass.TimeFreezing timeFreeze = Dungeon.hero.buff( TimekeepersHourglass.TimeFreezing.class );
				if (timeFreeze != null) timeFreeze.detach();

				InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
				Game.switchScene( InterlevelScene.class );
			}

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAttack( HeroAction.Attack action ) {

		enemy = action.target;

		if (enemy.isAlive() && canAttack( enemy ) && !isCharmedBy( enemy )) {
			
			sprite.attack( enemy.pos );


			return false;

		} else {

			if (fieldOfView[enemy.pos] && getCloser( enemy.pos )) {

				return true;

			} else {
				ready();
				return false;
			}

		}
	}

	public Char enemy(){
		return enemy;
	}

	public void setEnemy(Char enemy){
		this.enemy = enemy;
	}

	public void rest( boolean fullRest ) {
		spendAndNext( TIME_TO_REST );
		if (!fullRest && sprite != null) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "wait") );
		}
		resting = fullRest;
	}
	
	@Override
	public int attackProc( final Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		KindOfWeapon wep = belongings.weapon;

		if (wep != null) {
		    damage = wep.proc( this, enemy, damage );
		    if (buff(FierySlash.class) != null) new Blazing().proc((Weapon) wep,this, enemy, damage);
        }

		damage = Talent.onAttackProc(this, enemy, damage);
		
		switch (subClass) {
		case SNIPER:
			if (wep instanceof MissileWeapon && !(wep instanceof SpiritBow.SpiritArrow)) {
				Actor.add(new Actor() {
					
					{
						actPriority = VFX_PRIO;
					}
					
					@Override
					protected boolean act() {
						if (enemy.isAlive()) {
							Buff.prolong(Hero.this, SnipersMark.class, SnipersMark.DURATION).object = enemy.id();
						}
						Actor.remove(this);
						return true;
					}
				});
			}
			break;
		default:
		}



		return damage;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {

	    int initialDamage = damage;

		if (damage > 0 && Dungeon.isChallenged(Conducts.Conduct.BERSERK)){
			Berserk berserk = Buff.affect(this, Berserk.class);
			berserk.damage(damage);
		}
		if (Dungeon.isChallenged(Conducts.Conduct.TRANSMUTATION)){
			Item.doNotUseTurnForCollect = true;
			for (Item item : belongings) {
				if (ScrollOfTransmutation.canTransmute(item) && !Recycle.isRecyclable(item)) {
					Item.curUser = this;
					new ScrollOfTransmutation().onItemSelected(item);
				} else if (Recycle.isRecyclable(item)) {
					Item.curUser = this;
					new Recycle().onItemSelected(item);
				}
				if (item instanceof Bag) {
					Bag bag = (Bag)item;
					for (Item item1: bag){
						if (ScrollOfTransmutation.canTransmute(item1) && !Recycle.isRecyclable(item1)) {
							Item.curUser = this;
							new ScrollOfTransmutation().onItemSelected(item1);
						} else if (Recycle.isRecyclable(item1)) {
							Item.curUser = this;
							new Recycle().onItemSelected(item1);
						}
					}
				}
			}
			Item.doNotUseTurnForCollect = false;
		}

		if (belongings.armor != null) {
			damage = belongings.armor.proc( enemy, this, damage );
		}

		if (buff(SpikyShield.class) != null){
		    damage *= 0.6f;
            Buff.affect( enemy, Bleeding.class).set( damage / 2 );
        }

		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}

		WandOfLivingEarth.RockArmor rockArmor = buff(WandOfLivingEarth.RockArmor.class);
		if (rockArmor != null) {
			damage = rockArmor.absorb(damage);
		}

		return damage;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		if (buff(TimekeepersHourglass.timeStasis.class) != null)
			return;

		if (src instanceof ConjurerSpell){
			return;
		}

		if (buff(MirrorOfFates.mirrorExp.class) != null && buff(MirrorOfFates.mirrorExp.class).isCursed() && Random.Int(5) == 0) {
			ScrollOfTeleportation.teleportHero(Dungeon.hero);
		}

		if (!(src instanceof Hunger || src instanceof Viscosity.DeferedDamage) && damageInterrupt) {
			interrupt();
			resting = false;
		}

		if (this.buff(Drowsy.class) != null){
			Buff.detach(this, Drowsy.class);
			GLog.warning( Messages.get(this, "pain_resist") );
		}

		CapeOfThorns.Thorns thorns = buff( CapeOfThorns.Thorns.class );
		if (thorns != null) {
			dmg = thorns.proc(dmg, (src instanceof Char ? (Char)src : null),  this);
		}

		if (Dungeon.mode == Dungeon.GameMode.EXPLORE){
			dmg *= 0.75f;
		}
		if (buff(ParchmentOfElbereth.parchmentPraying.class) != null){
			dmg *= 0.8f;
		}

		dmg = (int)Math.ceil(dmg * RingOfElements.damageMultiplier( this ));

		//TODO improve this when I have proper damage source logic
		if (belongings.armor != null && belongings.armor.hasGlyph(AntiMagic.class, this)
				&& AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(belongings.armor.buffedLvl());
		}

		int preHP = HP + shielding();
		super.damage( dmg, src );
		int postHP = HP + shielding();
		int effectiveDamage = preHP - postHP;

		if (effectiveDamage <= 0) return;

		//flash red when hit for serious damage.
		float percentDMG = effectiveDamage / (float)preHP; //percent of current HP that was taken
		float percentHP = 1 - ((HT - postHP) / (float)HT); //percent health after damage was taken
		// The flash intensity increases primarily based on damage taken and secondarily on missing HP.
		float flashIntensity = 0.25f * (percentDMG * percentDMG) / percentHP;
		//if the intensity is very low don't flash at all
		if (flashIntensity >= 0.05f){
			flashIntensity = Math.min(1/3f, flashIntensity); //cap intensity at 1/3
			GameScene.flash( (int)(0xFF*flashIntensity) << 16 );
			if (isAlive()) {
				if (flashIntensity >= 1/6f) {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_CRITICAL, 1/3f + flashIntensity * 2f);
				} else {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN, 1/3f + flashIntensity * 4f);
				}
			}
		}
	}
	
	public void checkVisibleMobs() {
		ArrayList<Mob> visible = new ArrayList<>();

		boolean newMob = false;

		Mob target = null;
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (fieldOfView[m.pos] && m.alignment == Alignment.ENEMY && (!(m instanceof Phantom) || m.enemy == Dungeon.hero)) {
				visible.add(m);
				if (!visibleEnemies.contains( m )) {
					newMob = true;
				}

				if (!mindVisionEnemies.contains(m) && QuickSlotButton.autoAim(m) != -1){
					if (target == null){
						target = m;
					} else if (distance(target) > distance(m)) {
						target = m;
					}
				}
			}
		}

		Char lastTarget = QuickSlotButton.lastTarget;
		if (target != null && (lastTarget == null ||
							!lastTarget.isAlive() ||
							lastTarget.alignment == Alignment.ALLY ||
							!fieldOfView[lastTarget.pos]) && !(target instanceof Phantom)){
			QuickSlotButton.target(target);
		}
		
		if (newMob) {
			interrupt();
			if (resting){
				Dungeon.observe();
				resting = false;
			}
		}

		visibleEnemies = visible;
	}
	
	public int visibleEnemies() {
		return visibleEnemies.size();
	}
	
	public Mob visibleEnemy( int index ) {
		return visibleEnemies.get(index % visibleEnemies.size());
	}
	
	private boolean walkingToVisibleTrapInFog = false;
	
	//FIXME this is a fairly crude way to track this, really it would be nice to have a short
	//history of hero actions
	public boolean justMoved = false;

	private boolean getCloser( final int target ) {

		if (target == pos)
			return false;

		if (rooted) {
			Camera.main.shake( 1, 1f );
			return false;
		}
		
		int step = -1;
		
		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null) {
				if (Dungeon.level.pit[target] && !flying && !Dungeon.level.solid[target]) {
					if (!Chasm.jumpConfirmed){
						Chasm.heroJump(this);
						interrupt();
					} else {
						Chasm.heroFall(target);
					}
					return false;
				}
				if (Dungeon.level.passable[target] || Dungeon.level.avoid[target]) {
					step = target;
				}
				if (walkingToVisibleTrapInFog
						&& Dungeon.level.traps.get(target) != null
						&& Dungeon.level.traps.get(target).visible){
					return false;
				}
			}
			
		} else {

			boolean newPath = false;
			if (path == null || path.isEmpty() || !Dungeon.level.adjacent(pos, path.getFirst()))
				newPath = true;
			else if (path.getLast() != target)
				newPath = true;
			else {
				if (!Dungeon.level.passable[path.get(0)] || Actor.findChar(path.get(0)) != null) {
					newPath = true;
				}
			}

			if (newPath) {

				int len = Dungeon.level.length();
				boolean[] p = Dungeon.level.passable;
				boolean[] v = Dungeon.level.visited;
				boolean[] m = Dungeon.level.mapped;
				boolean[] passable = new boolean[len];
				for (int i = 0; i < len; i++) {
					passable[i] = p[i] && (v[i] || m[i]);
				}

				PathFinder.Path newpath = Dungeon.findPath(this, target, passable, fieldOfView, true);
				if (newpath != null && path != null && newpath.size() > 2*path.size()){
					path = null;
				} else {
					path = newpath;
				}
			}

			if (path == null) return false;
			step = path.removeFirst();

		}

		if (step != -1) {

			float speed = speed();

			lastMovPos = pos;
			sprite.move(pos, step);
			move(step);
			if (buff(Talent.DirectiveMovingTracker.class) != null){
				Talent.DirectiveMovingTracker b = buff(Talent.DirectiveMovingTracker.class);
				b.countUp(1);
				if (b.count() >= pointsInTalent(Talent.DIRECTIVE)){
					b.detach();
				}
			}
			else {
				spend(1 / speed);
			}
			justMoved = true;

			search(false);

			if (subClass == HeroSubClass.FREERUNNER){
				Buff.affect(this, Momentum.class).gainStack();
			}
			if (MirrorOfFates.isMirrorActive(this)){
				MirrorOfFates.MirrorShield shield = buff(MirrorOfFates.MirrorShield.class);
				shield.damage(shield.maxPotency / 4);
			}

			return true;

		} else {

			return false;
			
		}

	}
	
	public boolean handle( int cell ) {
		
		if (cell == -1) {
			return false;
		}
		
		Char ch;
		Heap heap;
		
		if (Dungeon.level.map[cell] == Terrain.ALCHEMY && cell != pos) {
			
			curAction = new HeroAction.Alchemy( cell );
			
		} else if (fieldOfView[cell] && (ch = Actor.findChar( cell )) instanceof Mob) {

			if (ch.alignment != Alignment.ENEMY && ch.buff(Amok.class) == null) {
				curAction = new HeroAction.Interact( ch );
			} else {
				curAction = new HeroAction.Attack( ch );
			}

		} else if ((heap = Dungeon.level.heaps.get( cell )) != null
				//moving to an item doesn't auto-pickup when enemies are near...
				&& (visibleEnemies.size() == 0 || cell == pos ||
				//...but only for standard heaps, chests and similar open as normal.
				(heap.type != Type.HEAP && heap.type != Type.FOR_SALE))) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().value() > 0 ?
					new HeroAction.Buy( cell ) :
					new HeroAction.PickUp( cell );
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}
			
		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR || Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {
			
			curAction = new HeroAction.Unlock( cell );

        } else if ((cell == Dungeon.level.exit || Dungeon.level.map[cell] == Terrain.EXIT || Dungeon.level.map[cell] == Terrain.UNLOCKED_EXIT)
                &&  (Dungeon.depth != Dungeon.chapterSize()*5 + 1 || Dungeon.mode == Dungeon.GameMode.GAUNTLET)) {
		        boolean canDo = true;
                if ((Dungeon.depth > Dungeon.chapterSize()*4) && (Dungeon.depth < Dungeon.chapterSize()*5)) {
					if (Dungeon.level.checkForFroggits()) {
						canDo = false;
					}
				}

                if (canDo) curAction = new HeroAction.Descend(cell);
                else GLog.warning(Messages.get(Level.class, "seal"));

        } else if (Dungeon.mode != Dungeon.GameMode.GAUNTLET && (cell == Dungeon.level.entrance || Dungeon.level.map[cell] == Terrain.ENTRANCE && (Dungeon.depth != Dungeon.chapterSize()*5 + 2))) {
			
			curAction = new HeroAction.Ascend( cell );
			
		} else  {
			
			if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]
					&& Dungeon.level.traps.get(cell) != null && Dungeon.level.traps.get(cell).visible) {
				walkingToVisibleTrapInFog = true;
			} else {
				walkingToVisibleTrapInFog = false;
			}
			
			curAction = new HeroAction.Move( cell );
			lastAction = null;
			
		}

		return true;
	}
	
	public void earnExp( int exp, Class source ) {

		if (SPDSettings.bigdungeon()) MAX_LEVEL = 100;
		else MAX_LEVEL = 30;
		if (Dungeon.mode == Dungeon.GameMode.LOL) MAX_LEVEL = Integer.MAX_VALUE;

		this.exp += exp;
		this.totalExp += exp;
		float percent = exp/(float)maxExp();

		EtherealChains.chainsRecharge chains = buff(EtherealChains.chainsRecharge.class);
		if (chains != null) chains.gainExp(percent);

		HornOfPlenty.hornRecharge horn = buff(HornOfPlenty.hornRecharge.class);
		if (horn != null) horn.gainCharge(percent);
		
		AlchemistsToolkit.kitEnergy kit = buff(AlchemistsToolkit.kitEnergy.class);
		if (kit != null) kit.gainCharge(percent);
		
		Berserk berserk = buff(Berserk.class);
		if (berserk != null) berserk.recover(percent);
		
		if (source != PotionOfExperience.class) {
			for (Item i : belongings) {
				i.onHeroGainExp(percent, this);
			}
		}

		if (totalExp >= 100 && Dungeon.mode == Dungeon.GameMode.LOL){
			boolean souAnnounced = false;

			while (totalExp >= 100 ) {
				totalExp -= 100;
				ScrollOfUpgrade sou = new ScrollOfUpgrade();

				if (!sou.collect()){
					Dungeon.level.drop(sou, pos);
				} else if (!souAnnounced){
					GLog.positive( Messages.get(this, "you_now_have", sou.name()) );
					souAnnounced = true;
				}
			}
			new Flare(6, 28).color(0x38FF48, true).show(sprite, 3.67f);
		}
		
		boolean levelUp = false;
		while (this.exp >= maxExp()) {
			this.exp -= maxExp();
			if (lvl < MAX_LEVEL) {
				lvl++;
				levelUp = true;
				
				if (buff(ElixirOfMight.HTBoost.class) != null){
					buff(ElixirOfMight.HTBoost.class).onLevelUp();
				}
				
				updateHT( true );
				attackSkill++;
				defenseSkill++;
                if (heroClass == HeroClass.CONJURER) {
                    if (lvl % 3 == 0) {
                        attunement += 0.5;
                    }
                    maxMana += 5;
                    if (lvl == 5) new Barrier().identify().collectWithAnnouncing();
                    if (lvl == 7) new Wave().identify().collectWithAnnouncing();
                    if (lvl == 9) new Shocker().identify().collectWithAnnouncing();
                    if (lvl == 11) new Necro().identify().collectWithAnnouncing();
                    if (lvl == 15) new Field().identify().collectWithAnnouncing();
                } else if (lvl == 6){
                    attunement++;
                }
				Item.updateQuickslot();

			} else {
				Buff.prolong(this, Bless.class, Bless.DURATION);
				this.exp = 0;

				GLog.positive( Messages.get(this, "level_cap"));
				Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
			}
			
		}
		
		if (levelUp) {
			
			if (sprite != null) {
				GLog.newLine();
				GLog.positive( Messages.get(this, "new_level"), lvl );
				sprite.showStatus( CharSprite.POSITIVE, Messages.get(Hero.class, "level_up") );
				Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
			}

			Item.updateQuickslot();
			
			Badges.validateLevelReached();
		}
	}

	public float expMod(){
		switch (Dungeon.mode){
			case BIGGER:
				return 1.5f;
			case GAUNTLET:
				return 1.25f;
			default:
				return 1.0f;
		}
	}

	public int maxExp() {
		return Math.round(maxExp( lvl )*expMod());
	}
	
	public static int maxExp( int lvl ){
		return 5 + lvl * 5;
	}
	
	public boolean isStarving() {
		return Buff.affect(this, Hunger.class).isStarving();
	}
	
	@Override
	public void add( Buff buff ) {

		if (buff(TimekeepersHourglass.timeStasis.class) != null)
			return;

		super.add( buff );

		if (sprite != null) {
			String msg = buff.heroMessage();
			if (msg != null){
				GLog.warning(msg);
			}

			if (buff instanceof Paralysis || buff instanceof Vertigo) {
				interrupt();
			}

		}
		
		BuffIndicator.refreshHero();
	}
	
	@Override
	public void remove( Buff buff ) {
		super.remove( buff );

		BuffIndicator.refreshHero();
	}
	
	@Override
	public float stealth() {
		float stealth = super.stealth();
		
		if (belongings.armor != null){
			stealth = belongings.armor.stealthFactor(this, stealth);
		}
		if (buff(ParchmentOfElbereth.parchmentCharge.class) != null && buff(ParchmentOfElbereth.parchmentCharge.class).isCursed()){
			stealth = -Float.NEGATIVE_INFINITY;
		}
		
		return stealth;
	}
	
	@Override
	public void die( Object cause  ) {
		
		curAction = null;

		Ankh ankh = null;

		//look for ankhs in player inventory, prioritize ones which are blessed.
		for (Item item : belongings){
			if (item instanceof Ankh) {
				if (ankh == null || ((Ankh) item).isBlessed()) {
					ankh = (Ankh) item;
				}
			}
		}

		if (ankh != null && ankh.isBlessed()) {
			this.HP = HT/4;

			//ensures that you'll get to act first in almost any case, to prevent reviving and then instantly dieing again.
			PotionOfHealing.cure(this);
			Buff.detach(this, Paralysis.class);
			spend(-cooldown());

			new Flare(8, 32).color(0xFFFF66, true).show(sprite, 2f);
			CellEmitter.get(this.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

			ankh.detach(belongings.backpack);

			Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
			GLog.warning( Messages.get(this, "revive") );
			Statistics.ankhsUsed++;

			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayAnkh();
					return;
				}
			}

			return;
		}


		if (Dungeon.mode != Dungeon.GameMode.EXPLORE) {
			Actor.fixTime();
			super.die(cause);

			if (ankh == null) {

				reallyDie(cause);

			} else {

				Dungeon.deleteGame(GamesInProgress.curSlot, false);
				final Ankh finalAnkh = ankh;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndResurrect(finalAnkh, cause));
					}
				});

			}
		}
		else {
			Dungeon.deleteGame(GamesInProgress.curSlot, false);
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndExploreResurrect(cause));
				}
			});
		}
	}

	public void reallyDie(Object cause) {

		if (Dungeon.mode == Dungeon.GameMode.EXPLORE){
			super.die(cause);
		}

		int length = Dungeon.level.length();
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Dungeon.level.discoverable;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					Dungeon.level.discover( i );
				}
			}
		}
		
		Bones.leave();
		
		Dungeon.observe();
		GameScene.updateFog();
				
		Dungeon.hero.belongings.identify();

		int pos = Dungeon.hero.pos;

		ArrayList<Integer> passable = new ArrayList<>();
		for (Integer ofs : PathFinder.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );

		ArrayList<Item> items = new ArrayList<>(Dungeon.hero.belongings.backpack.items);
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}

			Item item = Random.element( items );
			Dungeon.level.drop( item, cell ).sprite.drop( pos );
			items.remove( item );
		}

		GameScene.gameOver();
		
		if (cause instanceof Hero.Doom) {
			((Hero.Doom)cause).onDeath();
		}
		
		Dungeon.deleteGame( GamesInProgress.curSlot, true );
	}

	//effectively cache this buff to prevent having to call buff(Berserk.class) a bunch.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and concurrent modification implications if that method calls buff(Berserk.class)
	private Berserk berserk;

	@Override
	public boolean isAlive() {
		
		if (HP <= 0){
			if (berserk == null) berserk = buff(Berserk.class);
			return berserk != null && berserk.berserking();
		} else {
			berserk = null;
			return super.isAlive();
		}
	}

	@Override
	public void move( int step ) {
		boolean wasHighGrass = Dungeon.level.map[step] == Terrain.HIGH_GRASS;

		super.move( step );
		
		if (!flying) {
			if (isWet()) {
				Sample.INSTANCE.play( Assets.Sounds.WATER, 1, Random.Float( 0.8f, 1.25f ) );
			} else if (Dungeon.level.map[pos] == Terrain.EMPTY_SP) {
				Sample.INSTANCE.play( Assets.Sounds.STURDY, 1, Random.Float( 0.96f, 1.05f ) );
			} else if (Dungeon.level.map[pos] == Terrain.GRASS
					|| Dungeon.level.map[pos] == Terrain.EMBERS
					|| Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
				if (step == pos && wasHighGrass) {
					Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				} else {
					Sample.INSTANCE.play( Assets.Sounds.GRASS, 1, Random.Float( 0.96f, 1.05f ) );
				}
			} else {
				Sample.INSTANCE.play( Assets.Sounds.STEP, 1, Random.Float( 0.96f, 1.05f ) );
			}
		}
	}
	
	@Override
	public void onAttackComplete() {
		
		AttackIndicator.target(enemy);
		
		boolean hit = attack( enemy );
		
		Invisibility.dispel();
		spend( attackDelay() );
        Hunger.adjustHunger(-3.75f*attackDelay());

		curAction = null;

		super.onAttackComplete();
	}
	
	@Override
	public void onMotionComplete() {
		GameScene.checkKeyHold();
	}

	@Override
    public void onOperateComplete() {

        if (curAction instanceof HeroAction.Unlock) {

            int doorCell = ((HeroAction.Unlock)curAction).dst;
            int door = Dungeon.level.map[doorCell];

            if (Dungeon.level.distance(pos, doorCell) <= 1) {
                boolean hasKey = true;
                if (door == Terrain.LOCKED_DOOR) {
                    hasKey = Notes.remove(new IronKey(Dungeon.depth));
                    if (hasKey) Level.set(doorCell, Terrain.DOOR);
                } else {
                    hasKey = Notes.remove(new SkeletonKey(Dungeon.depth));
                    if (hasKey) Level.set(doorCell, Terrain.UNLOCKED_EXIT);
                }

                if (hasKey) {
                    GameScene.updateKeyDisplay();
                    Level.set(doorCell, door == Terrain.LOCKED_DOOR ? Terrain.DOOR : Terrain.UNLOCKED_EXIT);
                    GameScene.updateMap(doorCell);
                    spend(Key.TIME_TO_UNLOCK);
                    Hunger.adjustHunger(-3.75f);
                }
            }

        } else if (curAction instanceof HeroAction.OpenChest) {

            Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst );

            if (Dungeon.level.adjacent(pos, heap.pos) || pos == heap.pos){
                boolean hasKey = true;
                if (heap.type == Type.SKELETON || heap.type == Type.REMAINS) {
                    Sample.INSTANCE.play( Assets.Sounds.BONES );
                } else if (heap.type == Type.LOCKED_CHEST){
                    hasKey = Notes.remove(new GoldenKey(Dungeon.depth));
                } else if (heap.type == Type.CRYSTAL_CHEST){
                    hasKey = Notes.remove(new CrystalKey(Dungeon.depth));
                }

                if (hasKey) {
                    GameScene.updateKeyDisplay();
                    heap.open(this);
                    spend(Key.TIME_TO_UNLOCK);
                    Hunger.adjustHunger(-3.75f);
                }
            }

        }
        curAction = null;

        super.onOperateComplete();
    }

	@Override
	public boolean isImmune(Class effect) {
		if (effect == Electricity.class &&
				hasTalent(Talent.PERDERE_CRIMEN)){
			return true;
		}
		if (effect == Burning.class
				&& belongings.armor != null
				&& belongings.armor.hasGlyph(Brimstone.class, this)){
			return true;
		}
		return super.isImmune(effect);
	}

	public boolean search( boolean intentional ) {
		
		if (!isAlive()) return false;
		
		boolean smthFound = false;

		int distance = heroClass == HeroClass.ROGUE ? 2 : 1;
		
		boolean foresight = buff(Foresight.class) != null;
		
		if (foresight) distance++;
		
		int cx = pos % Dungeon.level.width();
		int cy = pos / Dungeon.level.width();
		int ax = cx - distance;
		if (ax < 0) {
			ax = 0;
		}
		int bx = cx + distance;
		if (bx >= Dungeon.level.width()) {
			bx = Dungeon.level.width() - 1;
		}
		int ay = cy - distance;
		if (ay < 0) {
			ay = 0;
		}
		int by = cy + distance;
		if (by >= Dungeon.level.height()) {
			by = Dungeon.level.height() - 1;
		}

		TalismanOfForesight.Foresight talisman = buff( TalismanOfForesight.Foresight.class );
		boolean cursed = talisman != null && talisman.isCursed();
		
		for (int y = ay; y <= by; y++) {
			for (int x = ax, p = ax + y * Dungeon.level.width(); x <= bx; x++, p++) {

				if (fieldOfView[p] && p != pos) {
					
					if (intentional) {
						GameScene.effectOverFog(new CheckedCell(p, pos));
					}
					
					if (Dungeon.level.secret[p]){

                        Trap trap = Dungeon.level.traps.get( p );
                        if (trap != null && !trap.canBeSearched){
                            continue;
                        }

						float chance;

						//searches aided by foresight always succeed, even if trap isn't searchable
						if (foresight){
							chance = 1f;

						//otherwise if the trap isn't searchable, searching always fails
						} else if (trap != null && !trap.canBeSearched){
							chance = 0f;

						//intentional searches always succeed against regular traps and doors
						} else if (intentional){
							chance = 1f;
						
						//unintentional searches always fail with a cursed talisman
						} else if (cursed) {
							chance = 0f;

						//unintentional trap detection scales from 40% at floor 0 to 30% at floor 25
						} else if (Dungeon.level.map[p] == Terrain.SECRET_TRAP) {
							chance = 0.4f - (Dungeon.chapterNumber()/50f);
							
						//unintentional door detection scales from 20% at floor 0 to 0% at floor 20
						} else {
							chance = 0.2f - (Dungeon.chapterNumber()/20f);
						}
						
						if (Random.Float() < chance) {
						
							int oldValue = Dungeon.level.map[p];
							
							GameScene.discoverTile( p, oldValue );
							
							Dungeon.level.discover( p );
							
							ScrollOfMagicMapping.discover( p );
							
							smthFound = true;
	
							if (talisman != null){
								if (oldValue == Terrain.SECRET_TRAP){
									talisman.charge(2);
								} else if (oldValue == Terrain.SECRET_DOOR){
									talisman.charge(10);
								}
							}
						}
					}
				}
			}
		}

		
		if (intentional) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "search") );
			sprite.operate( pos);

			if (hasTalent(Talent.THE_SANDSTORM)) {
				ConeAOE aoe = arrangeBlast(pos, sprite, MagicMissile.INVISI);
				((MagicMissile)sprite.parent.recycle( MagicMissile.class )).reset(
						MagicMissile.INVISI,
						sprite,
						aoe.rays.get(0).path.get(aoe.rays.get(0).dist),
						() -> {
							for (int i : PathFinder.NEIGHBOURS8){
								CellEmitter.get(pos+i).burst(Speck.factory(Speck.DUST, false), 4);
								Char ch = Actor.findChar(pos+i);
								if (ch != null && ch.alignment != alignment){
									Ballistica trajectory = new Ballistica(ch.pos, ch.pos+i, Ballistica.MAGIC_BOLT);
									if (trajectory.collisionPos != pos+i && !ch.properties().contains(Property.BOSS)) ch.spend(TIME_TO_SEARCH);
									if (pointsInTalent(Talent.THE_SANDSTORM) > 1) ch.damage(1 + Dungeon.chapterNumber(), new WandOfBlastWave());
									WandOfBlastWave.throwChar(ch, trajectory, 1, false, false, true);
								}
							}
							spendAndNext(TIME_TO_SEARCH);
						}
				);
			}
			if (!hasTalent(Talent.THE_SANDSTORM)) spendAndNext(TIME_TO_SEARCH);
			if (!Dungeon.level.locked) {
				if (cursed) {
					GLog.negative(Messages.get(this, "search_distracted"));
					Hunger.adjustHunger((TIME_TO_SEARCH - (2 * HUNGER_FOR_SEARCH)));
				} else {
					Hunger.adjustHunger((TIME_TO_SEARCH - HUNGER_FOR_SEARCH));
				}
			}

		}
		
		if (smthFound) {
			GLog.warning( Messages.get(this, "noticed_smth") );
			Sample.INSTANCE.play( Assets.Sounds.SECRET );
			interrupt();
		}
		
		return smthFound;
	}

	public static ConeAOE arrangeBlast(int pos, CharSprite sprite, int type){
		return arrangeBlast(pos, sprite, type, 1.5f);
	}

	public static ConeAOE arrangeBlast(int pos, CharSprite sprite, int type, float range) {
		Ballistica aim;
		if (pos % Dungeon.level.width() > 10){
			aim = new Ballistica(pos, pos - 1, Ballistica.WONT_STOP);
		} else {
			aim = new Ballistica(pos, pos + 1, Ballistica.WONT_STOP);
		}
		ConeAOE aoe = new ConeAOE(aim, range, 360, Ballistica.FRIENDLY_PROJECTILE);
		if (sprite.visible) {
			for (Ballistica ray : aoe.rays) {
				((MagicMissile) sprite.parent.recycle(MagicMissile.class)).reset(
						type,
						sprite,
						ray.path.get(ray.dist),
						null
				);
			}
		}
		return aoe;
	}

	public void resurrect( int resetLevel ) {

		HP = HT;
		Dungeon.gold = 0;
		exp = 0;

		belongings.resurrect( resetLevel );

		live();
	}

	@Override
	public void next() {
		if (isAlive())
			super.next();
	}

	public static interface Doom {
		public void onDeath();
	}
}
