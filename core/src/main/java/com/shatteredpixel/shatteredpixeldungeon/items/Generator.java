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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {

	public enum Category {
		WEAPON	( 4,    MeleeWeapon.class),
		WEP_T1	( 0,    MeleeWeapon.class),
		WEP_T2	( 0,    MeleeWeapon.class),
		WEP_T3	( 0,    MeleeWeapon.class),
		WEP_T4	( 0,    MeleeWeapon.class),
		WEP_T5	( 0,    MeleeWeapon.class),
		
		ARMOR	( 3,    Armor.class ),
		
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

		WAND	( 2,    Wand.class ),
		RING	( 1,    Ring.class ),
		ARTIFACT( 1,    Artifact.class),
		
		FOOD	( 0,    Food.class ),
		
		POTION	( 16,   Potion.class ),
		EXOTIC_POTION	( 0,   ExoticPotion.class ),
		SEED	( 2,    Plant.Seed.class ),
		
		SCROLL	( 16,   Scroll.class ),
		EXOTIC_SCROLL	( 0,   ExoticScroll.class ),
		STONE   ( 2,    Runestone.class),
		
		GOLD	( 20,   Gold.class );
		
		public Class<?>[] classes;

		//some item types use a deck-based system, where the probs decrement as items are picked
		// until they are all 0, and then they reset. Those generator classes should define
		// defaultProbs. If defaultProbs is null then a deck system isn't used.
		//Artifacts in particular don't reset, no duplicates!
		public float[] probs;
		public float[] defaultProbs = null;

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

		static {
			normalDistribution();
		}
	}

	// TODO: refactor to reduce copypasta
	public static void normalDistribution(){
		{
			Category.GOLD.classes = new Class<?>[]{
					Gold.class };
			Category.GOLD.probs = new float[]{ 1 };

			Category.POTION.classes = new Class<?>[]{
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
			Category.POTION.defaultProbs = new float[]{ 0, 6, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1 };
			Category.POTION.probs = Category.POTION.defaultProbs.clone();
			Category.EXOTIC_POTION.classes = new Class<?>[]{
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
			Category.EXOTIC_POTION.defaultProbs = Category.POTION.defaultProbs.clone();
			Category.EXOTIC_POTION.probs = Category.POTION.probs.clone();

			Category.SEED.classes = new Class<?>[]{
					Rotberry.Seed.class, //quest item
					Sungrass.Seed.class,
					Fadeleaf.Seed.class,
					Icecap.Seed.class,
					Firebloom.Seed.class,
					Sorrowmoss.Seed.class,
					Swiftthistle.Seed.class,
					Blindweed.Seed.class,
					Stormvine.Seed.class,
					Earthroot.Seed.class,
					Dreamfoil.Seed.class,
					Starflower.Seed.class};
			Category.SEED.defaultProbs = new float[]{ 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2 };
			Category.SEED.probs = Category.SEED.defaultProbs.clone();

			Category.SCROLL.classes = new Class<?>[]{
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
			Category.SCROLL.defaultProbs = new float[]{ 0, 6, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1 };
			Category.SCROLL.probs = Category.SCROLL.defaultProbs.clone();
			Category.EXOTIC_SCROLL.classes = new Class<?>[]{
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
			Category.EXOTIC_SCROLL.probs = Category.SCROLL.probs.clone();
			Category.EXOTIC_SCROLL.defaultProbs = Category.SCROLL.probs.clone();

			Category.STONE.classes = new Class<?>[]{
					StoneOfEnchantment.class,   //1 is guaranteed to drop on floors 6-19
					StoneOfIntuition.class,     //1 additional stone is also dropped on floors 1-3
					StoneOfAggression.class,
					StoneOfTargeting.class,
					StoneOfBlast.class,
					StoneOfBlink.class,
					StoneOfClairvoyance.class,
					StoneOfDeepenedSleep.class,
					StoneOfDisarming.class,
					StoneOfFlock.class,
					StoneOfShock.class,
					StoneOfAugmentation.class,  //1 is sold in each shop
			};
			Category.STONE.defaultProbs = new float[]{ 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0 };
			Category.STONE.probs = Category.STONE.defaultProbs.clone();

			Category.WAND.classes = new Class<?>[]{
					WandOfMagicMissile.class,
					WandOfLightning.class,
					WandOfBounceBeams.class,
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
					WandOfStench.class,
					WandOfConjuration.class};
			Category.WAND.probs = new float[]{ 4, 4, 3, 4, 4, 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, 3 };

			//see generator.randomWeapon
			Category.WEAPON.classes = new Class<?>[]{};
			Category.WEAPON.probs = new float[]{};

			Category.WEP_T1.classes = new Class<?>[]{
					WornShortsword.class,
					Gloves.class,
					Dagger.class
			};
			Category.WEP_T1.probs = new float[]{ 1, 1, 1, 0 };

			Category.WEP_T2.classes = new Class<?>[]{
					Shortsword.class,
					HandAxe.class,
					Spear.class,
					Quarterstaff.class,
					Dirk.class,
					Cleaver.class
			};
			Category.WEP_T2.probs = new float[]{ 5, 5, 5, 4, 4, 4};

			Category.WEP_T3.classes = new Class<?>[]{
					Sword.class,
					Mace.class,
					Scimitar.class,
					RoundShield.class,
					Sai.class,
					Whip.class
			};
			Category.WEP_T3.probs = new float[]{ 6, 5, 5, 4, 4, 4 };

			Category.WEP_T4.classes = new Class<?>[]{
					Longsword.class,
					BattleAxe.class,
					Flail.class,
					RunicBlade.class,
					AssassinsBlade.class,
					Crossbow.class
			};
			Category.WEP_T4.probs = new float[]{ 6, 5, 5, 4, 4, 4 };

			Category.WEP_T5.classes = new Class<?>[]{
					Greatsword.class,
					WarHammer.class,
					Glaive.class,
					Greataxe.class,
					Greatshield.class,
					Gauntlet.class
			};
			Category.WEP_T5.probs = new float[]{ 6, 5, 5, 4, 4, 4 };

			//see Generator.randomArmor
			Category.ARMOR.classes = new Class<?>[]{
					ClothArmor.class,
					LeatherArmor.class,
					MailArmor.class,
					ScaleArmor.class,
					PlateArmor.class };
			Category.ARMOR.probs = new float[]{ 0, 0, 0, 0, 0 };

			//see Generator.randomMissile
			Category.MISSILE.classes = new Class<?>[]{};
			Category.MISSILE.probs = new float[]{};

			Category.MIS_T1.classes = new Class<?>[]{
					ThrowingStone.class,
					ThrowingKnife.class
			};
			Category.MIS_T1.probs = new float[]{ 6, 5 };

			Category.MIS_T2.classes = new Class<?>[]{
					FishingSpear.class,
					ThrowingClub.class,
					Shuriken.class
			};
			Category.MIS_T2.probs = new float[]{ 6, 5, 4 };

			Category.MIS_T3.classes = new Class<?>[]{
					ThrowingSpear.class,
					Kunai.class,
					Bolas.class
			};
			Category.MIS_T3.probs = new float[]{ 6, 5, 4 };

			Category.MIS_T4.classes = new Class<?>[]{
					Javelin.class,
					Tomahawk.class,
					HeavyBoomerang.class
			};
			Category.MIS_T4.probs = new float[]{ 6, 5, 4 };

			Category.MIS_T5.classes = new Class<?>[]{
					Trident.class,
					ThrowingHammer.class,
					ForceCube.class
			};
			Category.MIS_T5.probs = new float[]{ 6, 5, 4 };

			//see Generator.randomMissile
			Category.STAFFS.classes = new Class<?>[]{};
			Category.STAFFS.probs = new float[]{};

			Category.STF_T1.classes = new Class<?>[]{
					FroggitStaff.class,
			};
			Category.STF_T1.probs = new float[]{ 1 };

			Category.STF_T2.classes = new Class<?>[]{
					GreyRatStaff.class,
					SlimeStaff.class,
					SheepStaff.class
			};
			Category.STF_T2.probs = new float[]{ 6, 5, 5};

			Category.STF_T3.classes = new Class<?>[]{
					SkeletonStaff.class,
					GnollHunterStaff.class,
					ChickenStaff.class,
					MagicMissileStaff.class
			};
			Category.STF_T3.probs = new float[]{ 6, 5, 5, 5};

			Category.STF_T4.classes = new Class<?>[]{
					FrostElementalStaff.class,
					WizardStaff.class,
					RoboStaff.class,
					RoseStaff.class
			};
			Category.STF_T4.probs = new float[]{ 5, 4, 4, 3  };

			Category.STF_T5.classes = new Class<?>[]{
					GooStaff.class,
					BlasterStaff.class,
					ImpQueenStaff.class,
					HacatuStaff.class
			};
			Category.STF_T5.probs = new float[]{ 5, 4, 4, 4 };

			Category.FOOD.classes = new Class<?>[]{
					Food.class,
					Pasty.class,
					MysteryMeat.class };
			Category.FOOD.probs = new float[]{ 4, 1, 0 };

			Category.RING.classes = new Class<?>[]{
					RingOfForce.class,
					RingOfMight.class,
					RingOfSharpshooting.class,
					RingOfAttunement.class,
					RingOfWealth.class};
			Category.RING.probs = new float[]{ 1, 1, 1, 1, 1 };

			Category.ARTIFACT.classes = new Class<?>[]{
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
					EtherealChains.class,
					SubtilitasSigil.class,
					MirrorOfFates.class,
					FuelContainer.class,
					ParchmentOfElbereth.class,
					MomentumBoots.class
			};
			Category.ARTIFACT.defaultProbs = new float[]{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1};
			Category.ARTIFACT.probs = Category.ARTIFACT.defaultProbs.clone();
		}
	}

	public static void chaosDistributionInit(){
		{
			Category.GOLD.classes = new Class<?>[]{
					Gold.class };
			Category.GOLD.prob = 1;
			Category.GOLD.probs = new float[]{ 1 };

			Category.POTION.prob = 1;
			Category.POTION.classes = new Class<?>[]{
					PotionOfStrength.class,
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
			Category.POTION.defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			Category.POTION.probs = Category.POTION.defaultProbs.clone();
			Category.EXOTIC_POTION.classes = new Class<?>[]{
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
			Category.EXOTIC_POTION.defaultProbs = Category.POTION.defaultProbs.clone();
			Category.EXOTIC_POTION.probs = Category.POTION.probs.clone();

			Category.SEED.prob = 1;
			Category.SEED.classes = new Class<?>[]{
					Rotberry.Seed.class,
					Sungrass.Seed.class,
					Fadeleaf.Seed.class,
					Icecap.Seed.class,
					Firebloom.Seed.class,
					Sorrowmoss.Seed.class,
					Swiftthistle.Seed.class,
					Blindweed.Seed.class,
					Stormvine.Seed.class,
					Earthroot.Seed.class,
					Dreamfoil.Seed.class,
					Starflower.Seed.class};
			Category.SEED.defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			Category.SEED.probs = Category.SEED.defaultProbs.clone();

			Category.SCROLL.prob = 1;
			Category.SCROLL.classes = new Class<?>[]{
					ScrollOfUpgrade.class,
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
			Category.SCROLL.defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			Category.SCROLL.probs = Category.SCROLL.defaultProbs.clone();
			Category.EXOTIC_SCROLL.classes = new Class<?>[]{
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
			Category.EXOTIC_SCROLL.probs = Category.SCROLL.probs.clone();
			Category.EXOTIC_SCROLL.defaultProbs = Category.SCROLL.probs.clone();

			Category.STONE.prob = 1;
			Category.STONE.classes = new Class<?>[]{
					StoneOfEnchantment.class,
					StoneOfAugmentation.class,
					StoneOfIntuition.class,
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
			Category.STONE.defaultProbs = new float[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
			Category.STONE.probs = Category.STONE.defaultProbs.clone();

			Category.WAND.classes = new Class<?>[]{
					WandOfMagicMissile.class,
					WandOfLightning.class,
					WandOfBounceBeams.class,
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
					WandOfStench.class,
					WandOfConjuration.class};
			Category.WAND.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

			//see generator.randomWeapon
			Category.WEAPON.prob = 1;
			Category.WEAPON.classes = new Class<?>[]{};
			Category.WEAPON.probs = new float[]{};

			Category.WEP_T1.classes = new Class<?>[]{
					WornShortsword.class,
					Gloves.class,
					Dagger.class
			};
			Category.WEP_T1.probs = new float[]{ 1, 1, 1, 1 };

			Category.WEP_T2.classes = new Class<?>[]{
					Shortsword.class,
					HandAxe.class,
					Spear.class,
					Quarterstaff.class,
					Dirk.class,
					Cleaver.class
			};
			Category.WEP_T2.probs = new float[]{ 1, 1, 1, 1, 1, 1};

			Category.WEP_T3.classes = new Class<?>[]{
					Sword.class,
					Mace.class,
					Scimitar.class,
					RoundShield.class,
					Sai.class,
					Whip.class
			};
			Category.WEP_T3.probs = new float[]{ 1, 1, 1, 1, 1, 1 };

			Category.WEP_T4.classes = new Class<?>[]{
					Longsword.class,
					BattleAxe.class,
					Flail.class,
					RunicBlade.class,
					AssassinsBlade.class,
					Crossbow.class
			};
			Category.WEP_T4.probs = new float[]{ 1, 1, 1, 1, 1, 1 };

			Category.WEP_T5.classes = new Class<?>[]{
					Greatsword.class,
					WarHammer.class,
					Glaive.class,
					Greataxe.class,
					Greatshield.class,
					Gauntlet.class
			};
			Category.WEP_T5.probs = new float[]{ 1, 1, 1, 1, 1, 1 };

			//see Generator.randomArmor
			Category.ARMOR.prob = 1;
			Category.ARMOR.classes = new Class<?>[]{
					ClothArmor.class,
					LeatherArmor.class,
					MailArmor.class,
					ScaleArmor.class,
					PlateArmor.class };
			Category.ARMOR.probs = new float[]{ 0, 0, 0, 0, 0 };

			//see Generator.randomMissile
			Category.MISSILE.prob = 1;
			Category.MISSILE.classes = new Class<?>[]{};
			Category.MISSILE.probs = new float[]{};

			Category.MIS_T1.classes = new Class<?>[]{
					ThrowingStone.class,
					ThrowingKnife.class
			};
			Category.MIS_T1.probs = new float[]{ 1, 1 };

			Category.MIS_T2.classes = new Class<?>[]{
					FishingSpear.class,
					ThrowingClub.class,
					Shuriken.class
			};
			Category.MIS_T2.probs = new float[]{ 1, 1, 1 };

			Category.MIS_T3.classes = new Class<?>[]{
					ThrowingSpear.class,
					Kunai.class,
					Bolas.class
			};
			Category.MIS_T3.probs = new float[]{ 1, 1, 1 };

			Category.MIS_T4.classes = new Class<?>[]{
					Javelin.class,
					Tomahawk.class,
					HeavyBoomerang.class
			};
			Category.MIS_T4.probs = new float[]{ 1, 1, 1 };

			Category.MIS_T5.classes = new Class<?>[]{
					Trident.class,
					ThrowingHammer.class,
					ForceCube.class
			};
			Category.MIS_T5.probs = new float[]{ 1, 1, 1 };

			//see Generator.randomMissile
			Category.STAFFS.prob = 1;
			Category.STAFFS.classes = new Class<?>[]{};
			Category.STAFFS.probs = new float[]{};

			Category.STF_T1.classes = new Class<?>[]{
					FroggitStaff.class,
			};
			Category.STF_T1.probs = new float[]{ 1 };

			Category.STF_T2.classes = new Class<?>[]{
					GreyRatStaff.class,
					SlimeStaff.class,
					SheepStaff.class
			};
			Category.STF_T2.probs = new float[]{ 1, 1, 1};

			Category.STF_T3.classes = new Class<?>[]{
					SkeletonStaff.class,
					GnollHunterStaff.class,
					ChickenStaff.class,
					MagicMissileStaff.class
			};
			Category.STF_T3.probs = new float[]{ 1, 1, 1, 1};

			Category.STF_T4.classes = new Class<?>[]{
					FrostElementalStaff.class,
					WizardStaff.class,
					RoboStaff.class,
					RoseStaff.class
			};
			Category.STF_T4.probs = new float[]{ 1, 1, 1, 1  };

			Category.STF_T5.classes = new Class<?>[]{
					GooStaff.class,
					BlasterStaff.class,
					ImpQueenStaff.class,
					HacatuStaff.class
			};
			Category.STF_T5.probs = new float[]{ 1, 1, 1, 1 };

			Category.FOOD.prob = 1;
			Category.FOOD.classes = new Class<?>[]{
					Food.class,
					Pasty.class,
					MysteryMeat.class };
			Category.FOOD.probs = new float[]{ 1, 1, 1 };

			Category.RING.prob = 1;
			Category.RING.classes = new Class<?>[]{
					RingOfForce.class,
					RingOfFuror.class,
					RingOfHaste.class,
					RingOfMight.class,
					RingOfSharpshooting.class,
					RingOfAttunement.class,
					RingOfWealth.class};
			Category.RING.probs = new float[]{ 1, 1, 1, 1, 1, 1, 1 };

			Category.ARTIFACT.prob = 1;
			Category.ARTIFACT.classes = new Class<?>[]{
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
					EtherealChains.class,
					SubtilitasSigil.class,
					MirrorOfFates.class,
					FuelContainer.class,
					ParchmentOfElbereth.class
			};
			Category.ARTIFACT.defaultProbs = new float[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1};
			Category.ARTIFACT.probs = Category.ARTIFACT.defaultProbs.clone();
		}
	}

	private static final float[][] floorSetTierProbs = new float[][] {
			{0, 70, 20,  8,  2},
			{0, 25, 50, 20,  5},
			{0,  0, 40, 50, 10},
			{0,  0, 20, 40, 40},
			{0,  0,  0, 20, 80},
			{0, 1, 1, 1, 1}
	};
	
	private static HashMap<Category,Float> categoryProbs = new LinkedHashMap<>();

	public static void fullReset() {
		if (Dungeon.mode == Dungeon.GameMode.CHAOS){
			chaosDistributionInit();
		} else {
			normalDistribution();
			generalReset();

			for (Category cat : Category.values()) {
				reset(cat);
			}
		}

	}

	public static void generalReset(){
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, cat.prob );
		}
	}

	public static void reset(Category cat){
		if (cat.defaultProbs != null) cat.probs = cat.defaultProbs.clone();
	}

	public static Item random() {
		Category cat = Random.chances( categoryProbs );
		if (cat == null){
			generalReset();
			cat = Random.chances( categoryProbs );
		}
		categoryProbs.put( cat, categoryProbs.get( cat ) - 1);
		return random( cat );
	}
	
	public static Item random( Category cat ) {
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
				int i = Random.chances(cat.probs);
				if (i == -1) {
					reset(cat);
					i = Random.chances(cat.probs);
				}
				if (cat.defaultProbs != null) cat.probs[i]--;
				return ((Item) Reflection.newInstance(cat.classes[i])).random();
		}
	}

	//overrides any deck systems and always uses default probs
	public static Item randomUsingDefaults( Category cat ){
		if (cat.defaultProbs == null) {
			return random(cat); //currently covers weapons/armor/missiles
		} else {
			return ((Item) Reflection.newInstance(cat.classes[Random.chances(cat.defaultProbs)])).random();
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		return Reflection.newInstance(cl).random();
	}

	public static Armor randomArmor(){
		return randomArmor(Dungeon.chapterNumber());
	}
	
	public static Armor randomArmor(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) floorSet = 5;

		Armor a = (Armor)Reflection.newInstance(Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])]);
		a.random();
		return a;
	}

	public static final Category[] wepTiers = new Category[]{
			Category.WEP_T1,
			Category.WEP_T2,
			Category.WEP_T3,
			Category.WEP_T4,
			Category.WEP_T5
	};

	public static MeleeWeapon randomWeapon(){
		return randomWeapon(Dungeon.chapterNumber());
	}
	
	public static MeleeWeapon randomWeapon(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) floorSet = 5;

		Category c = wepTiers[Random.chances(floorSetTierProbs[floorSet])];
		MeleeWeapon w = (MeleeWeapon)Reflection.newInstance(c.classes[Random.chances(c.probs)]);
		w.random();
		return w;
	}
	
	public static final Category[] misTiers = new Category[]{
			Category.MIS_T1,
			Category.MIS_T2,
			Category.MIS_T3,
			Category.MIS_T4,
			Category.MIS_T5
	};
	
	public static MissileWeapon randomMissile(){
		return randomMissile(Dungeon.chapterNumber());
	}
	
	public static MissileWeapon randomMissile(int floorSet) {
		
		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) floorSet = 5;
		
		Category c = misTiers[Random.chances(floorSetTierProbs[floorSet])];
		MissileWeapon w = (MissileWeapon)Reflection.newInstance(c.classes[Random.chances(c.probs)]);
		w.random();
		return w;
	}

    public static final Category[] stfTiers = new Category[]{
            Category.STF_T1,
            Category.STF_T2,
            Category.STF_T3,
            Category.STF_T4,
            Category.STF_T5
    };

    public static Staff randomStaff(){
        return randomStaff(Dungeon.chapterNumber());
    }

    public static Staff randomStaff(int floorSet) {

        floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) floorSet = 5;

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

		Category cat = Category.ARTIFACT;
		int i = Random.chances( cat.probs );

		if (i == -1){
			reset(Category.ARTIFACT);
			return randomArtifact();
		}

		cat.probs[i]--;
		return (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[i]).random();

	}

	public static boolean removeArtifact(Class<?extends Artifact> artifact) {
		Category cat = Category.ARTIFACT;
		for (int i = 0; i < cat.classes.length; i++){
			if (cat.classes[i].equals(artifact)) {
				cat.probs[i] = 0;
				return true;
			}
		}
		return false;
	}

	private static final String GENERAL_PROBS = "general_probs";
	private static final String CATEGORY_PROBS = "_probs";
	
	public static void storeInBundle(Bundle bundle) {
		Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
		float[] storeProbs = new float[genProbs.length];
		for (int i = 0; i < storeProbs.length; i++){
			storeProbs[i] = genProbs[i];
		}
		bundle.put( GENERAL_PROBS, storeProbs);

		for (Category cat : Category.values()){
			if (cat.defaultProbs == null) continue;
			boolean needsStore = false;
			for (int i = 0; i < cat.probs.length; i++){
				if (cat.probs[i] != cat.defaultProbs[i]){
					needsStore = true;
					break;
				}
			}

			if (needsStore){
				bundle.put(cat.name().toLowerCase() + CATEGORY_PROBS, cat.probs);
			}
		}
	}

	public static void restoreFromBundle(Bundle bundle) {
		fullReset();

		if (bundle.contains(GENERAL_PROBS)){
			float[] probs = bundle.getFloatArray(GENERAL_PROBS);
			for (int i = 0; i < probs.length; i++){
				categoryProbs.put(Category.values()[i], probs[i]);
			}
		}

		for (Category cat : Category.values()){
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_PROBS)){
				float[] probs = bundle.getFloatArray(cat.name().toLowerCase() + CATEGORY_PROBS);
				if (cat.defaultProbs != null && probs.length == cat.defaultProbs.length){
					cat.probs = probs;
				}
			}
		}

		//pre-0.8.1
		if (bundle.contains("spawned_artifacts")) {
			for (Class<? extends Artifact> artifact : bundle.getClassArray("spawned_artifacts")) {
				Category cat = Category.ARTIFACT;
				for (int i = 0; i < cat.classes.length; i++) {
					if (cat.classes[i].equals(artifact)) {
						cat.probs[i] = 0;
					}
				}
			}
		}
		
	}
}
