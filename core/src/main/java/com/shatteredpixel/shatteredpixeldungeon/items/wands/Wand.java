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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.EnergyOverload;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FinalFroggit;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities.Overload;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.CloakGlyph;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.Sparking;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.FuelContainer;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.SubtilitasSigil;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Tierable;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.*;

import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Wand extends Weapon implements Tierable {

	public static final String AC_ZAP	= "ZAP";

	private static final float TIME_TO_ZAP	= 1f;
	
	public int maxCharges = 3;
	public int curCharges = maxCharges;
	public float partialCharge = 0f;

	protected int chakraGain = 5;
	
	protected Charger charger;
	
	private boolean curChargeKnown = false;
	
	public boolean curseInfusionBonus = false;
	
	private static final int USES_TO_ID = 10;
	protected int usesLeftToID = USES_TO_ID;
	protected float availableUsesToID = USES_TO_ID/2f;

	protected int collisionProperties = Ballistica.FRIENDLY_MAGIC;
	
	{
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;

		defaultAction = AC_ZAP;
		usesTargeting = true;
		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (canZap(hero) || !curChargeKnown) {
			actions.add( AC_ZAP );
		}
		if (hero.heroClass != HeroClass.MAGE && !Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)){
			actions.remove(AC_EQUIP);
		}

		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_ZAP )) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			
		}
	}

	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1))
				);
	}

	@Override
	public int STRReq() {
		return Dungeon.hero != null ? Dungeon.hero.STR() : 10;
	}

	@Override
	public int STRReq(int lvl) {
		return Dungeon.hero != null ? Dungeon.hero.STR() : 10;
	}

	private int power(){
		return Dungeon.hero != null ? (Dungeon.hero.STR() - 10) : 0;
	}

	@Override
	public int min(int lvl) {
		int power = 1 + power();
		if (Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE){
			power += Dungeon.hero.lvl/6;
		}
		return power;
	}

	@Override
	public int max(int lvl) {
		int power = 7 + power() * 2;
		if (Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE){
			power += Dungeon.hero.lvl/3;
		}
		return power;
	}

	public float powerLevel(int level){
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.0f;
			case 2: return 1.0f;
		}
		return 0f;
	}

	public float powerLevel(){
		float powerLevel = powerLevel(level());
		if (Dungeon.hero.buff(Weakness.class) != null && Dungeon.hero.hasTalent(Talent.SUFFERING_AWAY)){
			powerLevel *= 1.25f;
		}
		if (Dungeon.hero.buff(Talent.SniperPatienceTracker.class) != null && Dungeon.hero.pointsInTalent(Talent.THAUMATURGY) > 1 && Dungeon.hero.buff(Talent.ThaumaturgyPatienceCooldown.class) == null){
			if (this instanceof WandOfFireblast || this instanceof WandOfLightning)
				powerLevel *= Talent.SniperPatienceTracker.damageModifier()*0.4f;
			else
				powerLevel *= Talent.SniperPatienceTracker.damageModifier();
		}
		return powerLevel;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE) {
			if (curCharges < maxCharges) partialCharge += 0.5f*rechargeModifier();
			ScrollOfRecharging.charge((Hero)attacker);
			onHit(this, attacker, defender, damage);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public int reachFactor(Char owner) {
		int reach = super.reachFactor(owner);
		if (owner instanceof Hero
				&& this instanceof WandOfBounceBeams
				&& ((Hero)owner).subClass == HeroSubClass.BATTLEMAGE){
			reach += 2;
		}
		return reach;
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return new Ballistica( user.pos, dst, collisionProperties ).collisionPos;
	}

	public abstract void onZap(Ballistica attack);

	public abstract void onHit(Wand wand, Char attacker, Char defender, int damage);

	public boolean canZap(Hero owner){
		if ( curCharges >= (cursed ? 1 : chargesPerCast())){
			return true;
		} else {
			if (owner.buff(FuelContainer.fuelBuff.class) != null){
				return owner.buff(FuelContainer.fuelBuff.class).canUseCharge(this, (cursed ? 1 : chargesPerCast()));
			}
		}
		return false;
	}

	public boolean tryToZap( Hero owner, int target ){

		if (owner.buff(MagicImmune.class) != null || Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
			GLog.warning( Messages.get(this, "no_magic") );
			return false;
		}
		if (owner.buff(SoulWeakness.class) != null){
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }

		if (canZap(owner)){
			return true;
		} else {
			GLog.warning(Messages.get(this, "fizzles"));
			return false;
		}
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				if (container instanceof MagicalHolster)
					charge( container.owner, MagicalHolster.HOLSTER_SCALE_FACTOR);
				else
					charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void gainCharge( float amt ){
		partialCharge += amt*rechargeModifier();
		while (partialCharge >= 1) {
			curCharges = Math.min(maxCharges, curCharges+1);
			partialCharge--;
			updateQuickslot();
		}
	}
	
	public void charge( Char owner ) {
		if (charger == null) charger = new Charger();
		charger.attachTo( owner );
	}

	public void charge( Char owner, float chargeScaleFactor ){
		charge( owner );
		charger.setScaleFactor( chargeScaleFactor );
	}

	@Override
	public void activate(Char ch) {
		charge(ch, 0.75f);
	}

	protected void processSoulMark(Char target, int chargesUsed){
		if (chargesUsed == 0) chargesUsed = 1;
		processSoulMark(target, buffedLvl(), chargesUsed);
	}

	protected static void processSoulMark(Char target, int wandLevel, int chargesUsed){
		if (target != Dungeon.hero &&
				Dungeon.hero.subClass == HeroSubClass.WARLOCK &&
				//standard 1 - 0.92^x chance, plus 7%. Starts at 15%
				Random.Float() > (Math.pow(0.94f, (Dungeon.hero.lvl/3*chargesUsed)+1) - 0.07f)){
			SoulMark.prolong(target, SoulMark.class, SoulMark.DURATION + wandLevel);
		}
	}

	@Override
	public void onDetach( ) {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}

	public void level(int value) {
		super.level( value );
	}
	
	@Override
	public Item identify() {
		
		curChargeKnown = true;
		super.identify();
		
		updateQuickslot();
		
		return this;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		if (!isIdentified() && availableUsesToID <= USES_TO_ID/2f && !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN)) {
			//gains enough uses to ID over 1 level
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID/2f);
		}
	}

	@Override
	public String info() {
		String desc = desc();

		desc += "\n\n" + statsDesc();

		if (charger != null && curCharges < maxCharges){
			desc += "\n\n" + Messages.get(Wand.class, "recharge",
					new DecimalFormat("#.##").format(
							charger.getTurnsToCharge() - partialCharge*charger.getTurnsToCharge()));
		}

		if (Dungeon.hero.heroClass == HeroClass.MAGE || Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)){
			desc += "\n\n" + Messages.get(Wand.class, "melee",
					augment.damageFactor(min()),
					augment.damageFactor(max()));
			desc += "\n\n" + Messages.get(this, "eleblast_desc");
		}

		switch (augment) {
			case SPEED:
				desc += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				desc += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			desc += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			desc += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Wand.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			desc += "\n\n" + Messages.get(Wand.class, "not_cursed");
		}

		if (Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE){
			desc += "\n\n" + Messages.get(this, "bmage_desc");
		}

		return desc;
	}

	public String statsDesc(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}
	
	@Override
	public int level() {
		if (!cursed && curseInfusionBonus){
			curseInfusionBonus = false;
		}
		return super.level();
	}
	
	@Override
	public Item upgrade() {

		if (isUpgradable())
		super.upgrade();

		if (Random.Int(3) == 0) {
			cursed = false;
		}

		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();

		updateQuickslot();
		
		return this;
	}

	@Override
	public int buffedLvl() {
		return super.buffedLvl();
	}

	protected int chargesPerCast() {
		if (Dungeon.hero.buff(Overload.OverloadTracker.class) != null)
			return 0;
		return 1;
	}

	protected int imaginableChargePerCast() {
		return 1;
	}
	
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	public void staffFx( StaffParticle particle ){
		particle.color(0xFFFFFF); particle.am = 0.3f;
		particle.setLifespan( 1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f );
		particle.radiateXY(0.5f);
	}

	@Override
	public Emitter emitter() {
		if (Dungeon.hero == null || !isEquipped(Dungeon.hero)) return null;
		Emitter emitter = new Emitter();
		emitter.pos(11.5f, 1.5f);
		emitter.fillTarget = false;
		emitter.pour(StaffParticleFactory, 0.1f);
		return emitter;
	}

	protected boolean isMM;

	protected void wandUsed(){
		wandUsed(null);
	}

	protected void afterWandUsed(Char target){
		curUser.spendAndNext( TIME_TO_ZAP );
	}

	protected void wandUsed(Char target) {
        Statistics.wandUses++;
		if (!isIdentified() && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.positive( Messages.get(Wand.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		boolean fuelUsed = false;

		int usedCharges = cursed ? 1 : chargesPerCast();
		if (this.curCharges > 0)
			this.curCharges -= usedCharges;
		else if (curUser.buff(FuelContainer.fuelBuff.class) != null){
			fuelUsed = true;
			curUser.buff(FuelContainer.fuelBuff.class).useCharge(this, usedCharges);
		}


		WandOfMagicMissile.MagicCharge buff = curUser.buff(WandOfMagicMissile.MagicCharge.class);
		if (buff != null && !isMM){
			buff.detach();
		}

		Invisibility.dispel();
		if (Dungeon.hero.buff(EnergyOverload.class) != null && !cursed && !fuelUsed) this.curCharges += chargesPerCast();

		if (curUser.heroClass == HeroClass.MAGE) levelKnown = true;
		updateQuickslot();

		if (curUser.subClass == HeroSubClass.GLADIATOR){
			Buff.affect(curUser, Stacks.class).add(1);
		}
		SubtilitasSigil.Recharge sigilCharge = curUser.buff(SubtilitasSigil.Recharge.class);
		if (sigilCharge != null){
			sigilCharge.gainExp(usedCharges);
		}
		if (Dungeon.hero.pointsInTalent(Talent.THAUMATURGY) > 1 && Dungeon.hero.buff(Talent.SniperPatienceTracker.class) != null){
			if (target != null){
				switch (Dungeon.hero.pointsInTalent(Talent.SNIPER_PATIENCE)){
					case 2:
						Buff.affect(target, Slow.class, 4f); break;
					case 3:
						Buff.affect(target, Slow.class, 6f);
						Buff.affect(target, StoneOfAggression.Aggression.class, 10f);
						PathFinder.buildDistanceMap( target.pos, BArray.not( Dungeon.level.solid, null ), 2 );
						for (int i = 0; i < PathFinder.distance.length; i++) {
							if (PathFinder.distance[i] < Integer.MAX_VALUE) {
								Char ch = Actor.findChar(i);
								if (ch != null && ch.alignment == Char.Alignment.ENEMY){
									ch.damage(1 + Dungeon.chapterNumber(), new WandOfBlastWave());
								}
								CellEmitter.get(i).burst(Speck.factory(Speck.SMOKE_DUST, true), Random.Int(4, 8));
							}
						}
						Sample.INSTANCE.play(Assets.Sounds.BLAST);
						break;
				}
			}
			Buff.detach(Dungeon.hero, Talent.SniperPatienceTracker.class);
			Talent.Cooldown.affectHero(Talent.ThaumaturgyPatienceCooldown.class);
		}

		afterWandUsed(target);
	}
	
	@Override
	public Item random() {
		//+0: 66.67% (2/3)
		//+1: 26.67% (4/15)
		//+2: 6.67%  (1/15)
		int n = 0;
		if (Random.Int(3) == 0) {
			n++;
			if (Random.Int(5) == 0){
				n++;
			}
		}
		level(n);
		curCharges = 3;

		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}
		if (Dungeon.isChallenged(Conducts.Conduct.CURSE)){
			cursed = true;
		}

		return this;
	}
	
	@Override
	public int value() {
		int price = 100;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String CUR_CHARGES         = "curCharges";
	private static final String CUR_CHARGE_KNOWN    = "curChargeKnown";
	private static final String PARTIALCHARGE       = "partialCharge";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
		bundle.put( PARTIALCHARGE , partialCharge );
		bundle.put(CURSE_INFUSION_BONUS, curseInfusionBonus );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );

		curCharges = bundle.getInt( CUR_CHARGES );
		if (curCharges > maxCharges) {
			curCharges = maxCharges;
			partialCharge = 0f;
		}
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
		curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
	}

	public int collisionProperties(int target){
		return collisionProperties;
	}

	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			
			if (target != null) {
				
				//FIXME this safety check shouldn't be necessary
				//it would be better to eliminate the curItem static variable.
				final Wand curWand;
				if (curItem instanceof Wand) {
					curWand = (Wand) Wand.curItem;
				} else {
					return;
				}
				if (curWand instanceof WandOfBounceBeams){
					Ballistica.REFLECTION = ((WandOfBounceBeams) curWand).bounceCount(curWand.buffedLvl());
				}
				final Ballistica shot = new Ballistica( curUser.pos, target, curWand.collisionProperties(target));
				int cell = shot.collisionPos;
				
				if (target == curUser.pos || cell == curUser.pos) {
					GLog.i( Messages.get(Wand.class, "self_target") );
					return;
				}

				curUser.sprite.zap(cell);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));
				
				if (curWand.tryToZap(curUser, target)) {
					
					curUser.busy();
					
					if (curWand.cursed){
						if (!curWand.cursedKnown){
							GLog.negative(Messages.get(Wand.class, "curse_discover", curWand.name()));
						}
						CursedWand.cursedZap(curWand,
								curUser,
								new Ballistica(curUser.pos, target, Ballistica.MAGIC_BOLT),
								new Callback() {
									@Override
									public void call() {
										curWand.wandUsed();
									}
								});
					} else {
						if (curUser.buff(CloakOfShadows.cloakStealth.class) != null &&
								curUser.buff(CloakOfShadows.cloakStealth.class).glyph() instanceof Sparking){
							new WandOfLightning().fx(shot, () -> {
								ArrayList<Char> affected = new ArrayList<>();
								for (int n : PathFinder.NEIGHBOURS9) {
									int c = cell + n;
									if (c >= 0 && c < Dungeon.level.length()) {
										if (Dungeon.level.heroFOV[c]) {
											CellEmitter.get(c).burst(SparkParticle.FACTORY, 4);
										}

										GameScene.add(Blob.seed(c, Math.round(12*CloakGlyph.efficiency()), Electricity.class));

										Char ch = Actor.findChar(c);
										if (ch != null && !(ch instanceof Hero)) {
											affected.add(ch);
										}
									}
								}

								for (Char ch: affected){
									int dmg = Math.round(Random.NormalIntRange(5 + Dungeon.scaledDepth(), 10 + Dungeon.scaledDepth()*2)*CloakGlyph.efficiency());

									if (ch.pos != cell){
										dmg = Math.round(dmg*0.8f);
									}

									dmg -= ch.actualDrRoll();

									if (dmg > 0) {
										if (!(Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) || ch.alignment == Char.Alignment.ALLY)
											ch.damage(dmg, this);
									}
								}

								curWand.wandUsed();
							});
						} else
							curWand.fx(shot, new Callback() {
							public void call() {
								curWand.onZap(shot);
								curWand.wandUsed(Actor.findChar(target));
							}
						});
					}
					curWand.cursedKnown = true;
					
				}
				
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(Wand.class, "prompt");
		}
	};

	public float rechargeModifier(int level){
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.0f;
			case 2: return 1.0f;
		}
		return 0f;
	}

	public float rechargeModifier(){
		float rechargeModifier = rechargeModifier(level());
		if (Dungeon.hero.belongings.armor instanceof ClothArmor){
			ClothArmor armor = (ClothArmor) Dungeon.hero.belongings.armor;
			if (armor.level() == 1){
				rechargeModifier *= 0.667f;
			}
		}
		return rechargeModifier;
	}
	
	public class Charger extends Buff {
		
		private static final float BASE_CHARGE_DELAY = 10f;
		private static final float SCALING_CHARGE_ADDITION = 35f;
		private static final float NORMAL_SCALE_FACTOR = 0.88f;

		private static final float CHARGE_BUFF_BONUS = 0.25f;

		float scalingFactor = NORMAL_SCALE_FACTOR;
		
		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			
			return true;
		}
		@Override
		public boolean act() {
			boolean canCharge = true;
			FuelContainer.fuelBuff fuelBuff = target.buff(FuelContainer.fuelBuff.class);
			if (fuelBuff != null && fuelBuff.isCursed())
				canCharge = false;
			if (curCharges < maxCharges && canCharge)
				recharge();
			
			while (partialCharge >= 1 && curCharges < maxCharges) {
				partialCharge--;
				curCharges++;
				updateQuickslot();
			}
			
			if (curCharges == maxCharges){
				partialCharge = 0;
			}
			
			spend( TICK );
			
			return true;
		}

		private void recharge(){
			float turnsToCharge = getTurnsToCharge();

			LockedFloor lock = target.buff(LockedFloor.class);
			if (lock == null || lock.regenOn())
				partialCharge += (1f/turnsToCharge);

			for (Recharging bonus : target.buffs(Recharging.class)){
				if (bonus != null && bonus.remainder() > 0f) {
					partialCharge += CHARGE_BUFF_BONUS * bonus.remainder()/rechargeModifier();
				}
			}

			if (target instanceof Hero && ((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 1 &&
					target.buff(FinalFroggit.Eradication.class) != null){
				int power = target.buff(FinalFroggit.Eradication.class).combo;
				if (((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 2)
					power *= 1.75f;
				partialCharge += CHARGE_BUFF_BONUS * power;
			}
		}

		public float getTurnsToCharge() {
			return getTurnsToCharge(level());
		}

		public float getTurnsToCharge(int level) {
			int missingCharges = maxCharges - curCharges;
			missingCharges = Math.max(0, missingCharges);

			return (float) (BASE_CHARGE_DELAY
					+ (SCALING_CHARGE_ADDITION * Math.pow(scalingFactor, missingCharges)))*rechargeModifier(level);
		}

		public Wand wand(){
			return Wand.this;
		}

		public void gainCharge(float charge){
			if (curCharges < maxCharges) {
				partialCharge += charge/rechargeModifier();
				while (partialCharge >= 1f) {
					curCharges++;
					partialCharge--;
				}
				curCharges = Math.min(curCharges, maxCharges);
				updateQuickslot();
			}
		}

		private void setScaleFactor(float value){
			this.scalingFactor = value;
		}
	}

	public final Emitter.Factory StaffParticleFactory = new Emitter.Factory() {
		@Override
		//reimplementing this is needed as instance creation of new staff particles must be within this class.
		public void emit(Emitter emitter, int index, float x, float y ) {
			StaffParticle c = (StaffParticle)emitter.getFirstAvailable(StaffParticle.class);
			if (c == null) {
				c = new StaffParticle();
				emitter.add(c);
			}
			c.reset(x, y);
		}

		@Override
		//some particles need light mode, others don't
		public boolean lightMode() {
			return !((Wand.this instanceof WandOfCorruption)
					|| (Wand.this instanceof WandOfCorrosion)
					|| (Wand.this instanceof WandOfRegrowth)
					|| (Wand.this instanceof WandOfLivingEarth));
		}
	};

	//determines particle effects to use based on wand the staff owns.
	public class StaffParticle extends PixelParticle {

		private float minSize;
		private float maxSize;
		public float sizeJitter = 0;

		public StaffParticle(){
			super();
		}

		public void reset( float x, float y ) {
			revive();

			speed.set(0);

			this.x = x;
			this.y = y;

			staffFx( this );

		}

		public void setSize( float minSize, float maxSize ){
			this.minSize = minSize;
			this.maxSize = maxSize;
		}

		public void setLifespan( float life ){
			lifespan = left = life;
		}

		public void shuffleXY(float amt){
			x += Random.Float(-amt, amt);
			y += Random.Float(-amt, amt);
		}

		public void radiateXY(float amt){
			float hypot = (float)Math.hypot(speed.x, speed.y);
			this.x += speed.x/hypot*amt;
			this.y += speed.y/hypot*amt;
		}

		@Override
		public void update() {
			super.update();
			size(minSize + (left / lifespan)*(maxSize-minSize) + Random.Float(sizeJitter));
		}
	}
}
