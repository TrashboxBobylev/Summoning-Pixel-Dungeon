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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.GuaranteedEnchant;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.SyntheticArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities.ArcaneElement;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Weapon extends KindOfWeapon {

	protected static final String AC_DETACH       = "DETACH";

	public float    ACC = 1f;	// Accuracy modifier
	public float	DLY	= 1f;	// Speed modifier
	public int      RCH = 1;    // Reach modifier (only applies to melee hits)

	public enum Augment {
		SPEED   (0.7f, 0.6667f),
		DAMAGE  (1.5f, 1.6667f),
		NONE	(1.0f, 1.0000f);

		private float damageFactor;
		private float delayFactor;

		Augment(float dmg, float dly){
			damageFactor = dmg;
			delayFactor = dly;
		}

		public int damageFactor(int dmg){
			return Math.round(dmg * damageFactor);
		}

		public float delayFactor(float dly){
			return dly * delayFactor;
		}
	}
	
	public Augment augment = Augment.NONE;
	
	private static final int USES_TO_ID = 20;
	private int usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	public Enchantment enchantment;
	public boolean curseInfusionBonus = false;

	public BrokenSeal seal;

	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		
		if (enchantment != null && attacker.buff(MagicImmune.class) == null) {
			damage = enchantment.proc( this, attacker, defender, damage );
		}
		
		if (!levelKnown && attacker == Dungeon.hero && availableUsesToID >= 1) {
			availableUsesToID--;
			usesLeftToID--;
			if (usesLeftToID <= 0) {
				identify();
				GLog.positive( Messages.get(Weapon.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		if (attacker == Dungeon.hero && Dungeon.hero.pointsInTalent(Talent.QUICK_HANDS) > 1 && this instanceof MeleeWeapon){
			int[] targets = new int[2];
			int direction = -1;
			int direction1 = -1, direction2 = -1;
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++){
				if (Actor.findChar(attacker.pos + PathFinder.NEIGHBOURS8[i]) == defender){
					direction = i;
				}
			}
			if (direction != -1) {
				switch (direction) {
					case 0:
						direction1 = 4;
						direction2 = 6;
						break;
					case 1:
					case 6:
						direction1 = 3;
						direction2 = 4;
						break;
					case 2:
						direction1 = 3;
						direction2 = 6;
						break;
					case 3:
					case 4:
						direction1 = 1;
						direction2 = 6;
						break;
					case 5:
						direction1 = 1;
						direction2 = 4;
						break;
					case 7:
						direction1 = 1;
						direction2 = 3;
						break;
				}
				targets[0] = defender.pos + PathFinder.NEIGHBOURS8[direction1];
				targets[1] = defender.pos + PathFinder.NEIGHBOURS8[direction2];
				Talent.QuickHandsWound.hit(defender.pos, 315, 0x7fa9d2);
				if (Dungeon.hero.pointsInTalent(Talent.QUICK_HANDS) > 2) {
					Buff.affect(attacker, Talent.QuickHandsRegenTracker.class, 2);
					Regeneration.regenerate(Dungeon.hero, 1);
				}
				for (int pos: targets){
					Talent.QuickHandsWound.hit(pos, 45, 0x7fa9d2);
					if (Actor.findChar(pos) != null){
						Char ch = Actor.findChar(pos);
						if (ch.alignment != attacker.alignment){
							int dmg = Math.round(damage*0.8f);
							Sample.INSTANCE.play(Assets.Sounds.HIT_STAB, 1f, 0.75f);
							if (enchantment != null && attacker.buff(MagicImmune.class) == null) {
								dmg = enchantment.proc( this, attacker, defender, damage );
							}
							ch.damage(dmg, this);
						}
					}
				}
			}
		}

		return damage;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f && !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN)) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String ENCHANTMENT	    = "enchantment";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String AUGMENT	        = "augment";
	private static final String SEAL            = "seal";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( ENCHANTMENT, enchantment );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( AUGMENT, augment );
		bundle.put( SEAL, seal);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		enchantment = (Enchantment)bundle.get( ENCHANTMENT );
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );

		augment = bundle.getEnum(AUGMENT, Augment.class);
		seal = (BrokenSeal)bundle.get(SEAL);
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			if (seal.level() > 0){
				degrade();
			}
			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!seal.collect()){
				Dungeon.level.drop(seal, hero.pos);
			}
			seal = null;
			Statistics.clothArmorForWarrior = false;
		}
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (seal.level() > 0){
			//doesn't trigger upgrading logic such as affecting curses/glyphs
			level(level()+1);
			Badges.validateItemLevelAquired(this);
		}
	}


	public BrokenSeal checkSeal(){
		return seal;
	}
	
	@Override
	public float accuracyFactor( Char owner ) {
		
		int encumbrance = 0;
		
		if( owner instanceof Hero ){
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		if (hasEnchant(Wayward.class, owner))
			encumbrance = Math.max(2, encumbrance+2);

		float ACC = this.ACC;

		return encumbrance > 0 ? (float)(ACC / Math.pow( 1.5, encumbrance )) : ACC;
	}
	
	@Override
	public float speedFactor( Char owner ) {

		int encumbrance = 0;
		if (owner instanceof Hero) {
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		float DLY = augment.delayFactor(this.DLY);

		if (owner instanceof Hero && ((Hero) owner).hasTalent(Talent.QUICK_HANDS))
			DLY /= 2;

		return (encumbrance > 0 ? (float)(DLY * Math.pow( 1.2, encumbrance )) : DLY);
	}

	@Override
	public int reachFactor(Char owner) {
		int reach = hasEnchant(Projecting.class, owner) ? RCH + 1 : RCH;
		if (owner instanceof Hero) {
			if (((Hero) owner).belongings.armor instanceof SyntheticArmor &&
					((Hero) owner).belongings.armor.level() == 1)
				reach += 1;
			if (owner.buff(Talent.JustOneMoreTileTracker.class) != null)
				reach += 1;

			if (((Hero) owner).pointsInTalent(Talent.JUST_ONE_MORE_TILE) > 2){
				float boost = 0;
				for (Buff buff: owner.buffs()){
					if (buff.type == Buff.buffType.NEGATIVE){
						boost += 1f;
					}
				}
				reach += (int) boost;
			}
		}
		return reach;
	}

	public int STRReq(){
		return STRReq(buffedLvl());
	}

	public abstract int STRReq(int lvl);

	public int strDamageBoost(Hero hero, int damage){
		int exStr = hero.STR() - STRReq();
		if (exStr > 0) {
			damage += Random.IntRange(0, exStr);
		}
		damage*=universalDMGModifier();
		return damage;
	}
	
	//overrides as other things can equip these
	@Override
	public int buffedLvl() {
		if (isEquipped( Dungeon.hero ) || Dungeon.hero.belongings.contains( this )){
			return super.buffedLvl();
		} else {
			return level();
		}
	}
	
	@Override
	public Item upgrade() {
		return upgrade(false);
	}
	
	public Item upgrade(boolean enchant ) {

		if (enchant){
			if (enchantment == null){
				enchant(Enchantment.random());
			}
		} else {
			if (hasCurseEnchant()){
				if (Random.Int(3) == 0) enchant(null);
			} else if (level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
				enchant(null);
			}
		}
		
		cursed = false;

		if (seal != null && seal.level() == 0)
			seal.upgrade();

		return super.upgrade();
	}
	
	@Override
	public String name() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( super.name() ) : super.name();
	}

	@Override
	public Emitter emitter() {
		if (seal == null) return super.emitter();
		Emitter emitter = new Emitter();
		emitter.pos(ItemSpriteSheet.film.width(image)/16f*2f, ItemSpriteSheet.film.height(image)/16f*14f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		//10% chance to be enchanted
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			if (!(this instanceof Wand) || Dungeon.hero.heroClass == HeroClass.MAGE)
				enchant(Enchantment.randomCurse());
			cursed = true;
		} else if (effectRoll >= 0.9f){
			enchant();
		}

		return this;
	}
	
	public Weapon enchant( Enchantment ench ) {
		if (ench == null || !ench.curse()) curseInfusionBonus = false;
		enchantment = ench;
		updateQuickslot();
		return this;
	}

	public Weapon enchant() {

		if (!(this instanceof Wand) || Dungeon.hero.heroClass == HeroClass.MAGE) {

			Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
			Enchantment ench = Enchantment.random(oldEnchantment);

			return enchant(ench);
		} else {
			return this;
		}
	}

	public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) {
		return enchantment != null && enchantment.getClass() == type && owner.buff(MagicImmune.class) == null;
	}
	
	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return enchantment != null && !enchantment.curse();
	}

	public boolean hasCurseEnchant(){
		return enchantment != null && enchantment.curse();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.glowing() : null;
	}

	public static abstract class Enchantment implements Bundlable {
		
		private static final Class<?>[] common = new Class<?>[]{
				Blazing.class, Chilling.class, Kinetic.class, Shocking.class};
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Blocking.class, Blooming.class, Elastic.class,
				Lucky.class, Projecting.class, Unstable.class};
		
		private static final Class<?>[] rare = new Class<?>[]{
				Corrupting.class, Grim.class, Vampiric.class};
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};
		
		private static final Class<?>[] curses = new Class<?>[]{
				Annoying.class, Displacing.class, Exhausting.class, Fragile.class,
				Sacrificial.class, Wayward.class, Polarized.class, Friendly.class
		};
		
			
		public abstract int proc( Weapon weapon, Char attacker, Char defender, int damage );

		public int accountForMissile(Weapon weapon){
			if (weapon instanceof MissileWeapon && Dungeon.hero.hasTalent(Talent.WILD_SORCERY)) return 2;
			return 0;
		}

        public int proc(Minion attacker, Char defender, int damage) {
            Weapon wp = new Weapon() {
                @Override
                public int STRReq(int lvl) {
                    return 8;
                }

                @Override
                public int min(int lvl) {
                    return 0;
                }

                @Override
                public int max(int lvl) {
                    return 0;
                }
            };
            wp.enchant(attacker.enchantment);
            wp.level(attacker.lvl+2);
            if (attacker.buff(GuaranteedEnchant.class) != null) wp.level(12);
            return proc(wp, attacker, defender, damage);
        }

		public float procChanceMultiplier(Char attacker){
			float multi = 1f;
			if (attacker instanceof Hero){
				if (attacker.buff(ArcaneElement.ArcaneTracker.class) != null){
					Buff.detach(attacker, ArcaneElement.ArcaneTracker.class);
					return 9999f;
				}
				if (attacker.buff(ArcaneElement.SecondArcaneTracker.class) != null){
					Buff.detach(attacker, ArcaneElement.SecondArcaneTracker.class);
					return 9999f;
				}
				if (attacker.buff(ArcaneElement.ThirdArcaneTracker.class) != null){
					Buff.detach(attacker, ArcaneElement.ThirdArcaneTracker.class);
					return 2f;
				}
				if (((Hero) attacker).belongings.weapon instanceof SpiritBow.SpiritArrow &&
					!(((SpiritBow.SpiritArrow) ((Hero) attacker).belongings.weapon).hasGoodEnchant())){
					switch (((Hero) attacker).belongings.weapon.level()){
						case 1:
							return 0.75f;
						case 2:
							return 0.50f;
					}
				}
			}
			return multi;
		}

		public String name() {
			if (!curse())
				return name( Messages.get(this, "enchant"));
			else
				return name( Messages.get(Item.class, "curse"));
		}

		public String name( String weaponName ) {
			return Messages.get(this, "name", weaponName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();
		
		@SuppressWarnings("unchecked")
		public static Enchantment random( Class<? extends Enchantment> ... toIgnore ) {
			float[] chances = typeChances;
			if (Dungeon.mode == Dungeon.GameMode.CHAOS) chances = new float[]{1, 1, 1};
			switch(Random.chances(chances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomCommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(common));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomUncommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(uncommon));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomRare( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(rare));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}

		@SuppressWarnings("unchecked")
		public static Enchantment randomCurse( Class<? extends Enchantment> ... toIgnore ){
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(curses));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
	}
}
