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

package com.trashboxbobylev.summoningpixeldungeon.items;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.ShatteredPixelDungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Slime;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.Armor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ClothArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.LeatherArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.MailArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.PlateArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ScaleArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.Artifact;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.CapeOfThorns;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.CloakOfShadows;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.DriedRose;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.EtherealChains;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.HornOfPlenty;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.LloydsBeacon;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.MasterThievesArmband;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.SandalsOfNature;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.TalismanOfForesight;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.UnstableSpellbook;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.Bag;
import com.trashboxbobylev.summoningpixeldungeon.items.food.Food;
import com.trashboxbobylev.summoningpixeldungeon.items.food.MysteryMeat;
import com.trashboxbobylev.summoningpixeldungeon.items.food.Pasty;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.Potion;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfExperience;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfFrost;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHaste;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfInvisibility;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfLevitation;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfMindVision;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfParalyticGas;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfPurity;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfStrength;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfToxicGas;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.exotic.*;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.Ring;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfAccuracy;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfElements;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfEnergy;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfEvasion;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfForce;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfFuror;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfHaste;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfMight;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfSharpshooting;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfWealth;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.Scroll;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRage;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.*;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.*;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Bolas;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.FishingSpear;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ForceCube;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Javelin;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Kunai;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Shuriken;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Tomahawk;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Trident;
import com.trashboxbobylev.summoningpixeldungeon.plants.Blindweed;
import com.trashboxbobylev.summoningpixeldungeon.plants.Dreamfoil;
import com.trashboxbobylev.summoningpixeldungeon.plants.Earthroot;
import com.trashboxbobylev.summoningpixeldungeon.plants.Fadeleaf;
import com.trashboxbobylev.summoningpixeldungeon.plants.Firebloom;
import com.trashboxbobylev.summoningpixeldungeon.plants.Icecap;
import com.trashboxbobylev.summoningpixeldungeon.plants.Plant;
import com.trashboxbobylev.summoningpixeldungeon.plants.Rotberry;
import com.trashboxbobylev.summoningpixeldungeon.plants.Sorrowmoss;
import com.trashboxbobylev.summoningpixeldungeon.plants.Starflower;
import com.trashboxbobylev.summoningpixeldungeon.plants.Stormvine;
import com.trashboxbobylev.summoningpixeldungeon.plants.Sungrass;
import com.trashboxbobylev.summoningpixeldungeon.plants.Swiftthistle;
import com.trashboxbobylev.summoningpixeldungeon.sprites.BlasterSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {

	public enum Category {
		WEAPON	( 7,    MeleeWeapon.class),
		WEP_T1	( 0,    MeleeWeapon.class),
		WEP_T2	( 0,    MeleeWeapon.class),
		WEP_T3	( 0,    MeleeWeapon.class),
		WEP_T4	( 0,    MeleeWeapon.class),
		WEP_T5	( 0,    MeleeWeapon.class),
		
		ARMOR	( 5,    Armor.class ),
		
		MISSILE ( 5,    MissileWeapon.class ),
		MIS_T1  ( 0,    MissileWeapon.class ),
		MIS_T2  ( 0,    MissileWeapon.class ),
		MIS_T3  ( 0,    MissileWeapon.class ),
		MIS_T4  ( 0,    MissileWeapon.class ),
		MIS_T5  ( 0,    MissileWeapon.class ),

        STAFFS ( 4,    Staff.class ),
        STF_T1  ( 0,    Staff.class ),
        STF_T2  ( 0,    Staff.class ),
        STF_T3  ( 0,    Staff.class ),
        STF_T4  ( 0,    Staff.class ),
        STF_T5  ( 0,    Staff.class ),
		
		WAND	( 4,    Wand.class ),
		RING	( 2,    Ring.class ),
		ARTIFACT( 2,    Artifact.class),
		
		FOOD	( 0,    Food.class ),
		
		POTION	( 20,   Potion.class ),
        EXOTIC_POTION	( 0,   ExoticPotion.class ),
		SEED	( 0,    Plant.Seed.class ), //dropped by grass
		
		SCROLL	( 20,   Scroll.class ),
        EXOTIC_SCROLL	( 0,   ExoticScroll.class ),
		STONE   ( 3,    Runestone.class),
		
		GOLD	( 12,   Gold.class );
		
		public Class<?>[] classes;
		public float[] probs;
		
		public float prob;
		public Class<? extends Item> superClass;
		
		private Category( float prob, Class<? extends Item> superClass ) {
			this.prob = prob;
			this.superClass = superClass;
		}
		
		public static int order( Item item ) {
			for (int i=0; i < values().length; i++) {
				if (values()[i].superClass.isInstance( item )) {
					return i;
				}
			}
			
			return item instanceof Bag ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
		}
		
		private static final float[] INITIAL_ARTIFACT_PROBS = new float[]{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1};
		
		static {
			GOLD.classes = new Class<?>[]{
					Gold.class };
			GOLD.probs = new float[]{ 1 };
			
			POTION.classes = new Class<?>[]{
					PotionOfStrength.class, //2 drop every chapter, see Dungeon.posNeeded()
					PotionOfHealing.class,
					PotionOfMindVision.class,
					PotionOfFrost.class,
					PotionOfLiquidFlame.class,
					PotionOfToxicGas.class,
					PotionOfHaste.class,
					PotionOfInvisibility.class,
					PotionOfLevitation.class,
					PotionOfParalyticGas.class,
					PotionOfPurity.class,
					PotionOfExperience.class};
            EXOTIC_POTION.classes = new Class<?>[]{
                    PotionOfAdrenalineSurge.class, //2 drop every chapter, see Dungeon.posNeeded()
                    PotionOfShielding.class,
                    PotionOfMagicalSight.class,
                    PotionOfSnapFreeze.class,
                    PotionOfDragonsBreath.class,
                    PotionOfCorrosiveGas.class,
                    PotionOfStamina.class,
                    PotionOfShroudingFog.class,
                    PotionOfStormClouds.class,
                    PotionOfEarthenArmor.class,
                    PotionOfCleansing.class,
                    PotionOfHolyFuror.class};
			POTION.probs = new float[]{ 0, 6, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1 };
			EXOTIC_POTION.probs = POTION.probs;
			
			SEED.classes = new Class<?>[]{
					Rotberry.Seed.class, //quest item
					Blindweed.Seed.class,
					Dreamfoil.Seed.class,
					Earthroot.Seed.class,
					Fadeleaf.Seed.class,
					Firebloom.Seed.class,
					Icecap.Seed.class,
					Sorrowmoss.Seed.class,
					Stormvine.Seed.class,
					Sungrass.Seed.class,
					Swiftthistle.Seed.class,
					Starflower.Seed.class};
			SEED.probs = new float[]{ 0, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 1 };
			
			SCROLL.classes = new Class<?>[]{
					ScrollOfUpgrade.class, //3 drop every chapter, see Dungeon.souNeeded()
					ScrollOfIdentify.class,
					ScrollOfRemoveCurse.class,
					ScrollOfMirrorImage.class,
					ScrollOfRecharging.class,
					ScrollOfTeleportation.class,
					ScrollOfLullaby.class,
					ScrollOfMagicMapping.class,
					ScrollOfRage.class,
					ScrollOfRetribution.class,
					ScrollOfAttunement.class,
					ScrollOfTransmutation.class
			};
            EXOTIC_SCROLL.classes = new Class<?>[]{
                    ScrollOfEnchantment.class, //3 drop every chapter, see Dungeon.souNeeded()
                    ScrollOfDivination.class,
                    ScrollOfAntiMagic.class,
                    ScrollOfPrismaticImage.class,
                    ScrollOfMysticalEnergy.class,
                    ScrollOfPassage.class,
                    ScrollOfAffection.class,
                    ScrollOfForesight.class,
                    ScrollOfConfusion.class,
                    ScrollOfPsionicBlast.class,
                    ScrollOfSoulEnergy.class,
                    ScrollOfPolymorph.class
            };
			SCROLL.probs = new float[]{ 0, 5, 4, 3, 3, 3, 2, 2, 2, 2, 3, 1 };
			EXOTIC_SCROLL.probs = SCROLL.probs;
			
			STONE.classes = new Class<?>[]{
					StoneOfEnchantment.class,   //1 is guaranteed to drop on floors 6-19
					StoneOfAugmentation.class,  //1 is sold in each shop
					StoneOfIntuition.class,     //1 additional stone is also dropped on floors 1-3
					StoneOfAggression.class,
					StoneOfTargeting.class,
					StoneOfBlast.class,
					StoneOfBlink.class,
					StoneOfClairvoyance.class,
					StoneOfDeepenedSleep.class,
					StoneOfDisarming.class,
					StoneOfFlock.class,
					StoneOfShock.class
			};
			STONE.probs = new float[]{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

			WAND.classes = new Class<?>[]{
					WandOfMagicMissile.class,
					WandOfLightning.class,
					WandOfDisintegration.class,
					WandOfFireblast.class,
					WandOfCorrosion.class,
					WandOfBlastWave.class,
                    WandOfCrystalBullet.class,
                    WandOfStars.class,
					WandOfLivingEarth.class,
					WandOfFrost.class,
					WandOfPrismaticLight.class,
					WandOfWarding.class,
					WandOfTransfusion.class,
					WandOfCorruption.class,
					WandOfRegrowth.class,
                    WandOfStench.class};
			WAND.probs = new float[]{ 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
			
			//see generator.randomWeapon
			WEAPON.classes = new Class<?>[]{};
			WEAPON.probs = new float[]{};
			
			WEP_T1.classes = new Class<?>[]{
					WornShortsword.class,
					Gloves.class,
					Dagger.class,
					MagesStaff.class
			};
			WEP_T1.probs = new float[]{ 1, 1, 1, 0 };
			
			WEP_T2.classes = new Class<?>[]{
					Shortsword.class,
					HandAxe.class,
					Spear.class,
					Quarterstaff.class,
					Dirk.class,
                    Cleaver.class
			};
			WEP_T2.probs = new float[]{ 5, 5, 5, 4, 4, 4};
			
			WEP_T3.classes = new Class<?>[]{
					Sword.class,
					Mace.class,
					Scimitar.class,
					RoundShield.class,
					Sai.class,
					Whip.class
			};
			WEP_T3.probs = new float[]{ 6, 5, 5, 4, 4, 4 };
			
			WEP_T4.classes = new Class<?>[]{
					Longsword.class,
					BattleAxe.class,
					Flail.class,
					RunicBlade.class,
					AssassinsBlade.class,
					Crossbow.class
			};
			WEP_T4.probs = new float[]{ 6, 5, 5, 4, 4, 4 };
			
			WEP_T5.classes = new Class<?>[]{
					Greatsword.class,
					WarHammer.class,
					Glaive.class,
					Greataxe.class,
					Greatshield.class,
					Gauntlet.class
			};
			WEP_T5.probs = new float[]{ 6, 5, 5, 4, 4, 4 };
			
			//see Generator.randomArmor
			ARMOR.classes = new Class<?>[]{
					ClothArmor.class,
					LeatherArmor.class,
					MailArmor.class,
					ScaleArmor.class,
					PlateArmor.class };
			ARMOR.probs = new float[]{ 0, 0, 0, 0, 0 };
			
			//see Generator.randomMissile
			MISSILE.classes = new Class<?>[]{};
			MISSILE.probs = new float[]{};
			
			MIS_T1.classes = new Class<?>[]{
					ThrowingStone.class,
					ThrowingKnife.class
			};
			MIS_T1.probs = new float[]{ 6, 5 };
			
			MIS_T2.classes = new Class<?>[]{
					FishingSpear.class,
					ThrowingClub.class,
					Shuriken.class
			};
			MIS_T2.probs = new float[]{ 6, 5, 4 };
			
			MIS_T3.classes = new Class<?>[]{
					ThrowingSpear.class,
					Kunai.class,
					Bolas.class
			};
			MIS_T3.probs = new float[]{ 6, 5, 4 };
			
			MIS_T4.classes = new Class<?>[]{
					Javelin.class,
					Tomahawk.class,
					HeavyBoomerang.class
			};
			MIS_T4.probs = new float[]{ 6, 5, 4 };
			
			MIS_T5.classes = new Class<?>[]{
					Trident.class,
					ThrowingHammer.class,
					ForceCube.class
			};
			MIS_T5.probs = new float[]{ 6, 5, 4 };

            //see Generator.randomMissile
            STAFFS.classes = new Class<?>[]{};
            STAFFS.probs = new float[]{};

            STF_T1.classes = new Class<?>[]{
                    FroggitStaff.class,
            };
            STF_T1.probs = new float[]{ 1 };

            STF_T2.classes = new Class<?>[]{
                    GreyRatStaff.class,
                    SlimeStaff.class,
                    SheepStaff.class
            };
            STF_T2.probs = new float[]{ 6, 5, 5};

            STF_T3.classes = new Class<?>[]{
                    SkeletonStaff.class,
                    GnollHunterStaff.class,
                    ChickenStaff.class,
                    MagicMissileStaff.class
            };
            STF_T3.probs = new float[]{ 6, 5, 5, 5};

            STF_T4.classes = new Class<?>[]{
                    FrostElementalStaff.class,
                    WizardStaff.class,
                    RoboStaff.class,
                    RoseStaff.class
            };
            STF_T4.probs = new float[]{ 5, 4, 4, 3  };

            STF_T5.classes = new Class<?>[]{
                    GooStaff.class,
                    BlasterStaff.class,
                    ImpQueenStaff.class,
                    HacatuStaff.class
            };
            STF_T5.probs = new float[]{ 5, 4, 4, 4 };
			
			FOOD.classes = new Class<?>[]{
					Food.class,
					Pasty.class,
					MysteryMeat.class };
			FOOD.probs = new float[]{ 4, 1, 0 };
			
			RING.classes = new Class<?>[]{
					RingOfAccuracy.class,
					RingOfEvasion.class,
					RingOfElements.class,
					RingOfForce.class,
					RingOfFuror.class,
					RingOfHaste.class,
					RingOfEnergy.class,
					RingOfMight.class,
					RingOfSharpshooting.class,
					RingOfAttunement.class,
					RingOfWealth.class};
			RING.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			
			ARTIFACT.classes = new Class<?>[]{
					CapeOfThorns.class,
					ChaliceOfBlood.class,
					CloakOfShadows.class,
					HornOfPlenty.class,
					MasterThievesArmband.class,
					SandalsOfNature.class,
					TalismanOfForesight.class,
					TimekeepersHourglass.class,
					UnstableSpellbook.class,
					AlchemistsToolkit.class,
					DriedRose.class,
					LloydsBeacon.class,
					EtherealChains.class
			};
			ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone();
		}
	}

	private static final float[][] floorSetTierProbs = new float[][] {
			{0, 70, 20,  8,  2},
			{0, 25, 50, 20,  5},
			{0, 10, 40, 40, 10},
			{0,  5, 20, 50, 25},
			{0,  2,  8, 20, 70}
	};
	
	private static HashMap<Category,Float> categoryProbs = new LinkedHashMap<>();
	
	public static void reset() {
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, cat.prob );
		}
	}
	
	public static Item random() {
		Category cat = Random.chances( categoryProbs );
		if (cat == null){
			reset();
			cat = Random.chances( categoryProbs );
		}
		categoryProbs.put( cat, categoryProbs.get( cat ) - 1);
		return random( cat );
	}
	
	public static Item random( Category cat ) {
		try {
			
			switch (cat) {
			case ARMOR:
				return randomArmor();
			case WEAPON:
				return randomWeapon();
			case MISSILE:
				return randomMissile();
                case STAFFS:
                    return randomStaff();
			case ARTIFACT:
				Item item = randomArtifact();
				//if we're out of artifacts, return a ring instead.
				return item != null ? item : random(Category.RING);
			default:
				return ((Item)cat.classes[Random.chances( cat.probs )].newInstance()).random();
			}
			
		} catch (Exception e) {

			ShatteredPixelDungeon.reportException(e);
			return null;
			
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		try {
			
			return ((Item)cl.newInstance()).random();
			
		} catch (Exception e) {

			ShatteredPixelDungeon.reportException(e);
			return null;
			
		}
	}

	public static Armor randomArmor(){
		return randomArmor(Dungeon.depth / 5);
	}
	
	public static Armor randomArmor(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		try {
			Armor a = (Armor)Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])].newInstance();
			a.random();
			return a;
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			return null;
		}
	}

	public static final Category[] wepTiers = new Category[]{
			Category.WEP_T1,
			Category.WEP_T2,
			Category.WEP_T3,
			Category.WEP_T4,
			Category.WEP_T5
	};

	public static MeleeWeapon randomWeapon(){
		return randomWeapon(Dungeon.depth / 5);
	}
	
	public static MeleeWeapon randomWeapon(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		try {
			Category c = wepTiers[Random.chances(floorSetTierProbs[floorSet])];
			MeleeWeapon w = (MeleeWeapon)c.classes[Random.chances(c.probs)].newInstance();
			w.random();
			return w;
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			return null;
		}
	}
	
	public static final Category[] misTiers = new Category[]{
			Category.MIS_T1,
			Category.MIS_T2,
			Category.MIS_T3,
			Category.MIS_T4,
			Category.MIS_T5
	};
	
	public static MissileWeapon randomMissile(){
		return randomMissile(Dungeon.depth / 5);
	}
	
	public static MissileWeapon randomMissile(int floorSet) {
		
		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		
		try {
			Category c = misTiers[Random.chances(floorSetTierProbs[floorSet])];
			MissileWeapon w = (MissileWeapon)c.classes[Random.chances(c.probs)].newInstance();
			w.random();
			return w;
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			return null;
		}
	}

    public static final Category[] stfTiers = new Category[]{
            Category.STF_T1,
            Category.STF_T2,
            Category.STF_T3,
            Category.STF_T4,
            Category.STF_T5
    };

    public static Staff randomStaff(){
        return randomStaff(Dungeon.depth / 5);
    }

    public static Staff randomStaff(int floorSet) {

        floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

        try {
            Category c = stfTiers[Random.chances(floorSetTierProbs[floorSet])];
            Staff w = (Staff) c.classes[Random.chances(c.probs)].newInstance();
            w.random();
            return w;
        } catch (Exception e) {
            ShatteredPixelDungeon.reportException(e);
            return null;
        }
    }

	//enforces uniqueness of artifacts throughout a run.
	public static Artifact randomArtifact() {

		try {
			Category cat = Category.ARTIFACT;
			int i = Random.chances( cat.probs );

			//if no artifacts are left, return null
			if (i == -1){
				return null;
			}
			
			Class<?extends Artifact> art = (Class<? extends Artifact>) cat.classes[i];

			if (removeArtifact(art)) {
				Artifact artifact = art.newInstance();
				
				artifact.random();
				
				return artifact;
			} else {
				return null;
			}

		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			return null;
		}
	}

	public static boolean removeArtifact(Class<?extends Artifact> artifact) {
		if (spawnedArtifacts.contains(artifact))
			return false;

		Category cat = Category.ARTIFACT;
		for (int i = 0; i < cat.classes.length; i++)
			if (cat.classes[i].equals(artifact)) {
				if (cat.probs[i] == 1){
					cat.probs[i] = 0;
					spawnedArtifacts.add(artifact);
					return true;
				} else
					return false;
			}

		return false;
	}

	//resets artifact probabilities, for new dungeons
	public static void initArtifacts() {
		Category.ARTIFACT.probs = Category.INITIAL_ARTIFACT_PROBS.clone();
		spawnedArtifacts = new ArrayList<>();
	}

	private static ArrayList<Class<?extends Artifact>> spawnedArtifacts = new ArrayList<>();
	
	private static final String GENERAL_PROBS = "general_probs";
	private static final String SPAWNED_ARTIFACTS = "spawned_artifacts";
	
	public static void storeInBundle(Bundle bundle) {
		Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
		float[] storeProbs = new float[genProbs.length];
		for (int i = 0; i < storeProbs.length; i++){
			storeProbs[i] = genProbs[i];
		}
		bundle.put( GENERAL_PROBS, storeProbs);
		
		bundle.put( SPAWNED_ARTIFACTS, spawnedArtifacts.toArray(new Class[0]));
	}

	public static void restoreFromBundle(Bundle bundle) {
		if (bundle.contains(GENERAL_PROBS)){
			float[] probs = bundle.getFloatArray(GENERAL_PROBS);
			for (int i = 0; i < probs.length; i++){
				categoryProbs.put(Category.values()[i], probs[i]);
			}
		} else {
			reset();
		}
		
		initArtifacts();
		
		for ( Class<?extends Artifact> artifact : bundle.getClassArray(SPAWNED_ARTIFACTS) ){
			removeArtifact(artifact);
		}
		
	}
}
