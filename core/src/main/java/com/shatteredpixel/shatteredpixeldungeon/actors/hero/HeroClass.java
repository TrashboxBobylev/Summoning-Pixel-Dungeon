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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.Heal;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.Stars;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.Zap;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Slingshot;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.FroggitStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnive2;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroClass {

	WARRIOR( "warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( "mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( "rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( "huntress", HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
    CONJURER("conjurer", HeroSubClass.SOUL_REAVER, HeroSubClass.OCCULTIST),
	ADVENTURER("adventurer", HeroSubClass.NOTHING_1, HeroSubClass.NOTHING_2);

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

			case ADVENTURER:
				initAdventurer(hero);
				break;

		}

		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) hero.HP = hero.HT = 1;

	}

	private static void initCommon( Hero hero ) {
		Item i = new ClothArmor().identify();
		hero.belongings.armor = (ClothArmor)i;

		i = new Food();
		i.collect();

		hero.attunement = 0;

		Talent.initClassTalents(hero);
		Generator.random(Generator.Category.WAND).identify().collect();

		if (hero.heroClass != ADVENTURER) {
			new ScrollOfIdentify().identify();
		}
		new Ropes().quantity(5).collect();
		new DewVial().collect();
		new ScrollOfUpgrade().quantity(2).collect();

		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
			new Amulet().collect();
		}
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

		if (hero.belongings.weapon != null){
			((Weapon)hero.belongings.weapon).affixSeal(new BrokenSeal());
		}

		new PotionBandolier().collect();
		Dungeon.LimitedDrops.POTION_BANDOLIER.drop();

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {

		Wand wand = new WandOfMagicMissile();

		(hero.belongings.weapon = wand).identify();
		hero.belongings.weapon.activate(hero);
		ElementalBlast blast = new ElementalBlast();
		(hero.belongings.artifact = blast).identify();
		hero.belongings.artifact.activate( hero );
		Dungeon.quickslot.setSlot(0, wand);
		Dungeon.quickslot.setSlot(1, blast);
		new WandOfPrismaticLight().identify().collect();
		for (int i = 0; i < 30; i++){
			new PotionOfExperience().apply(hero);
		}

		new ScrollHolder().collect();
		Dungeon.LimitedDrops.SCROLL_HOLDER.drop();

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(1).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Gloves()).identify();
		(hero.belongings.armor = new ScoutArmor()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);
		Dungeon.quickslot.setSlot(1, hero.belongings.armor);

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

    private static void initConjurer( Hero hero ) {

        (hero.belongings.weapon = new Knife()).identify();

		ConjurerBook book = new ConjurerBook();
		book.collect();

		FroggitStaff staff1 = new FroggitStaff();
        staff1.identify().collect();

        Dungeon.quickslot.setSlot(0, book);
        Dungeon.quickslot.setSlot(1, staff1);

        hero.belongings.armor = ClassArmor.upgrade(hero, (Armor)(new ClothArmor().identify()));

//        LoveHolder cloak = new LoveHolder();
//        (hero.belongings.artifact = cloak).identify();
//        hero.belongings.artifact.activate( hero );
//        Dungeon.quickslot.setSlot(0, hero.belongings.artifact);

        hero.attunement = 1;
        hero.HP = hero.HT = 10;

        Stars star = new Stars();
        star.collect();
		Heal heal = new Heal();
		heal.collect();
		new Zap().collect();

        hero.mana = 0;
        hero.maxMana = 20;
        new PotionOfStrength().identify();
        new ScrollOfAttunement().identify();

    }

	private static void initAdventurer( Hero hero ) {
		hero.HP = hero.HT = 30;
		hero.STR = 12;

		(hero.belongings.armor = new SyntheticArmor()).identify();

		(hero.belongings.weapon = new Dagger2()).identify();

		ThrowingKnive2 knives = new ThrowingKnive2();
		knives.quantity(2).collect();
		Dungeon.quickslot.setSlot(0, knives);

		new PotionBandolier().collect();
		Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
		new ScrollHolder().collect();
		Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
		new MagicalHolster().collect();
		Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
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
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case CONJURER:
			return Assets.Sprites.CONJURER;
			case ADVENTURER:
				return Assets.Sprites.ADVENTURER;
		}
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case CONJURER:
				return Assets.Splashes.CONJURER;
			case ADVENTURER:
				return Assets.Splashes.ADVENTURER;
		}
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
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
		return true;
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
