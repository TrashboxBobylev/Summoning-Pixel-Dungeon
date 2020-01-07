/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.trashboxbobylev.summoningpixeldungeon.actors.hero;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Badges;
import com.trashboxbobylev.summoningpixeldungeon.Challenges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.SupportPower;
import com.trashboxbobylev.summoningpixeldungeon.items.BrokenSeal;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.TomeOfMastery;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.Armor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ClassArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ClothArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.CloakOfShadows;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.LoveHolder;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.MagicalHolster;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.PotionBandolier;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.ScrollHolder;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.VelvetPouch;
import com.trashboxbobylev.summoningpixeldungeon.items.food.Blandfruit;
import com.trashboxbobylev.summoningpixeldungeon.items.food.Food;
import com.trashboxbobylev.summoningpixeldungeon.items.food.SmallRation;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.*;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.brews.PerfumeBrew;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.brews.RagingBrew;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.elixirs.ElixirOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.powers.*;
import com.trashboxbobylev.summoningpixeldungeon.items.quest.RatSkull;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.*;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.ScrollOfSoulEnergy;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.Contain;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.Enrage;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfTargeting;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfMagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfStench;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Slingshot;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.SpiritBow;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Unstable;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Vampiric;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop.Jjango;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop.Pike;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop.Stabber;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

	WARRIOR( "warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( "mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( "rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( "huntress", HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
    CONJURER("conjurer", HeroSubClass.SOUL_REAVER, HeroSubClass.OCCULTIST);

	private String title;
	private HeroSubClass[] subClasses;

	HeroClass( String title, HeroSubClass...subClasses ) {
		this.title = title;
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;

            case CONJURER:
                initConjurer( hero );
                break;
		}
		
	}

	private static void initCommon( Hero hero ) {
		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;

		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		if (Dungeon.isChallenged(Challenges.NO_FOOD)){
			new SmallRation().collect();
		}

		hero.attunement = 0;
		
		new ScrollOfIdentify().identify();

	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		Slingshot stones = new Slingshot();
		stones.charge = 1;
		stones.identify().collect();
		Dungeon.quickslot.setSlot(0, stones);

		if (hero.belongings.armor != null){
			hero.belongings.armor.affixSeal(new BrokenSeal());
		}
		
		new PotionBandolier().collect();
		Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
		
		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;
		
		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);
        WandOfStench stench = new WandOfStench();
        stench.identify().collect();
        //new RatSkull().collect();

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollHolder().collect();
		Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		
		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.misc1 = cloak).identify();
		hero.belongings.misc1.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new StoneHammer().identify().collect();
		new Pike().identify().collect();
		new Stabber().identify().collect();
		new Jjango().identify().collect();

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
		
		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
		hero.STR = 18;
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Gloves()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();
		bow.enchant(new Vampiric());

		Dungeon.quickslot.setSlot(0, bow);

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
        TippedDart.randomTipped(2).collect();
        new ScrollOfEnchantment().identify().collect();
		
		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

    private static void initConjurer( Hero hero ) {

        (hero.belongings.weapon = new Knife()).identify();

        new MagicalHolster().collect();

        FroggitStaff staff = new FroggitStaff();
        staff.identify().collect();

        //Dungeon.quickslot.setSlot(1, staff);
        new GreyRatStaff().identify().collect();
        new SheepStaff().identify().collect();
        new SlimeStaff().identify().collect();
        new SkeletonStaff().identify().collect();
        new GnollHunterStaff().identify().collect();
        new MagicMissileStaff().identify().collect();
        new ChickenStaff().identify().collect();
        new FrostElementalStaff().identify().collect();
        new RoseStaff().identify().collect();
        new RoboStaff().identify().collect();
        new WizardStaff().identify().collect();
        new GooStaff().identify().collect();
        new ImpQueenStaff().identify().collect();
        new HacatuStaff().identify().collect();
        new BlasterStaff().identify().collect();
        new WarriorPower().collect();
        new RoguePower().collect();
        new MagePower().collect();
        new RangePower().collect();
        new ConjurerPower().collect();
        new TomeOfMastery().collect();

        hero.belongings.armor = ClassArmor.upgrade(hero, (Armor)(new ClothArmor().identify()));

        LoveHolder cloak = new LoveHolder();
        (hero.belongings.misc1 = cloak).identify();
        hero.belongings.misc1.activate( hero );
        Dungeon.quickslot.setSlot(0, hero.belongings.misc1);

        Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();

        hero.attunement = 2;
        hero.HP = hero.HT = 15;
        hero.STR = 9;

        new PotionOfStrength().identify();
        new ScrollOfAttunement().identify();

    }
	
	public String title() {
		return Messages.get(HeroClass.class, title);
	}
	
	public HeroSubClass[] subClasses() {
		return subClasses;
	}
	
	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.WARRIOR;
			case MAGE:
				return Assets.MAGE;
			case ROGUE:
				return Assets.ROGUE;
			case HUNTRESS:
				return Assets.HUNTRESS;
            case CONJURER:
                return Assets.CONJURER;
		}
	}
	
	public String[] perks() {
		switch (this) {
			case WARRIOR: default:
				return new String[]{
						Messages.get(HeroClass.class, "warrior_perk1"),
						Messages.get(HeroClass.class, "warrior_perk2"),
						Messages.get(HeroClass.class, "warrior_perk3"),
						Messages.get(HeroClass.class, "warrior_perk4"),
						Messages.get(HeroClass.class, "warrior_perk5"),
				};
			case MAGE:
				return new String[]{
						Messages.get(HeroClass.class, "mage_perk1"),
						Messages.get(HeroClass.class, "mage_perk2"),
						Messages.get(HeroClass.class, "mage_perk3"),
						Messages.get(HeroClass.class, "mage_perk4"),
						Messages.get(HeroClass.class, "mage_perk5"),
				};
			case ROGUE:
				return new String[]{
						Messages.get(HeroClass.class, "rogue_perk1"),
						Messages.get(HeroClass.class, "rogue_perk2"),
						Messages.get(HeroClass.class, "rogue_perk3"),
						Messages.get(HeroClass.class, "rogue_perk4"),
						Messages.get(HeroClass.class, "rogue_perk5"),
				};
			case HUNTRESS:
				return new String[]{
						Messages.get(HeroClass.class, "huntress_perk1"),
						Messages.get(HeroClass.class, "huntress_perk2"),
						Messages.get(HeroClass.class, "huntress_perk3"),
						Messages.get(HeroClass.class, "huntress_perk4"),
						Messages.get(HeroClass.class, "huntress_perk5"),
				};
            case CONJURER:
                return new String[]{
                        Messages.get(HeroClass.class, "conjurer_perk1"),
                        Messages.get(HeroClass.class, "conjurer_perk2"),
                        Messages.get(HeroClass.class, "conjurer_perk3"),
                        Messages.get(HeroClass.class, "conjurer_perk4"),
                        Messages.get(HeroClass.class, "conjurer_perk5"),
                };
		}
	}
	
	public boolean isUnlocked(){
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return true;
		
		switch (this){
			case WARRIOR: default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
            case CONJURER:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_CONJURER);
		}
	}
	
	public String unlockMsg() {
		switch (this){
			case WARRIOR: default:
				return "";
			case MAGE:
				return Messages.get(HeroClass.class, "mage_unlock");
			case ROGUE:
				return Messages.get(HeroClass.class, "rogue_unlock");
			case HUNTRESS:
				return Messages.get(HeroClass.class, "huntress_unlock");
            case CONJURER:
                return Messages.get(HeroClass.class, "conjurer_unlock");
		}
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
