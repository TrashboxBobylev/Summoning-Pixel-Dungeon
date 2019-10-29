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

package com.trashboxbobylev.summoningpixeldungeon.ui.changelist;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroClass;
import com.trashboxbobylev.summoningpixeldungeon.items.Ankh;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ConjurerArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.food.Blandfruit;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.ArcaneCatalyst;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.ChangesScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.HeroSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.ui.Icons;
import com.trashboxbobylev.summoningpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class SummPDChanges {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo( "Summoning PD", true, "");
		changes.hardlight( Window.TITLE_COLOR);
		changeInfos.add(changes);
		add_Beta_Changes(changeInfos);
		add_General_Changes(changeInfos);
		add_Items_Changes(changeInfos);
		add_Mobs_Changes(changeInfos);
		add_Minor_Changes(changeInfos);
	}

    public static void add_Beta_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("Beta Information", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.CONJURER), "Closed Beta",
                "_-_ Closed beta started October 18th, 2019\n" +
                        "_-_ 42 days after beginning of development\n" +
                        "\n" +
                        "This is pre-release version of Summoning PD. I except to test them for 5-8 days, if nothing major will happen, that version will be released as final."));

        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-2",
                "Fixed:\n" +
                        "_-_ Traps was not correctly revealed, non-hidden traps was crashing the game.\n" +
                        "_-_ LOVE Holder was able to have negative strength.\n" +
                        "_-_ Conjurer's icon was bit out of center\n"+
                        "_-_ Changes buttons was not enough long.\n"+
                        "_-_ Ring of Attunement was displaying incorrect numbers.\n\n"+
                        "Changed:\n" +
                        "_-_ Buffed melee damage for staffs, but lowered recharge rate by 50%."));

        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-3",
                "Fixed:\n" +
                        "_-_ Rendering of cleared Tengu floor was wrong\n" +
                        "_-_ Conjurer was starting with 10 strength\n" +
                        "_-_ Sometimes the new badges were crashing the game\n"+
                        "_-_ Gaster Blaster had 1 attunement requirement, but consumed the 2 attunement on summoning\n"+
                        "_-_ In some cases staff's descriptions were crashing the game\n"+
                        "_-_ Sewers had test tier drop rate list\n\n"+
                        "Changed:\n" +
                        "_-_ New sprites for Conjurer's avatar and froggit\n"+
                        "_-_ Staff's default action were changed to SUMMON"));
        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-4",
                "Fixed:\n" +
                        "_-_ Player was able to use staffs with negative charges\n" +
                        "_-_ Soul Reaver's abilities were reducing minion's life to 1\n" +
                        "_-_ Chicken staff was displaying the bonus HP from robe\n"+
                        "_-_ Froggit were having the wrong frame animations\n"+
                        "_-_ Love Holder doesn't crash on reading +10's description\n"+
                        "_-_ Minions was not able to wake up after magical sleep\n\n"+
                        "Changed:\n" +
                        "_-_ Minions now have the stats description"));
        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-5",
                "Fixed:\n" +
                        "_-_ Froggit was using the death animation for attacking and vice versa\n" +
                        "_-_ Heroes were not able to open heaps standing on them\n" +
                        "_-_ All heroes was having 9 strength\n\n"+
                        "Changed:\n" +
                "_-_ Changes slighty the weight of items, staffs should appear more often.\n\n"+
                "_-_ Reworked the Vial of Perfume: now attracts enemies from whole depth, but attacking them dispells the perfume affection.\n"+
                "_-_ Changed the Containing: chance to collect the mob depends on their HP and EXP, if not successful, the enemy will get damage, equal to 50% current HP.\n"+
                "_-_ Rebalanced most broken or underused staffs.\n"+
                "_-_ Removed the unstable from Worn Shortsword."));

        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-6",
                "Fixed:\n" +
                        "_-_ Staffs were not transmutable\n" +
                        "_-_ Message from stone of aggression was !!!NO TEXT FOUND!!!\n" +
                        "_-_ Conjurer's amor had a wrong prompt on imbuing\n\n"+
                        "Changed:\n" +
                        "_-_ Staffs now decrease strength requirement with every upgrade.\n"+
                        "_-_ Chaos Saber now collect a lot of soul, if hero is Soul Reaver.\n"+
                        "_-_ Necromancer now affect their skeleton with Empowered.\n"+
                        "_-_ Reworked the resistance for monsters: damage now get sqrted rather that halfed.\n"+
                        "_-_ Buffed the Ring of Attunement.\n"+
                        "_-_ Scroll of Attunement now weakens the enemies.\n"+
                        "_-_ Minion show their base DR alongside with additional DR in description.\n"+
                        "_-_ Necromancers now drop the random staff with 1/8 chance.\n"+
                        "_-_ Removed nerfs from Cleaver, but adjusted the chance to behead."));

        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-7",
                "Changed:\n"+
                                        "_-_ Throwing knifes and kunai doesn't consume a durability with right uses\n"+
                                        "_-_ Added the slingshot with removal of throwing stones as standalone weapon\n"+
                                        "_-_ Fixed weird non-opaque spots in graphics\n"+
                                        "_-_ Reworked the runic blade\n"+
                                        "_-_ Changed the visuals of fireball and main menu buttons\n"+
                                        "_-_ Buffed the rattle snake's evasion and damage\n"+
                                        "_-_ Massively adjusted charge rate of most staves; froggit staff will stay with old stats, most of staves recharges in 400 turns, tank staves recharge even longer\n" +
                                        "_-_ Brand new icon for the mod!"
        ));
        changes.addButton( new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), "BETA-7.1",
                "Fixed:\n" +
                        "_-_ Runic Blade's emitter were placed in wrong place and didn't updated\n" +
                        "_-_ On shooting, Runic Blade was able to target something different from target\n\n" +
                        "Changed:\n" +
                        "_-_ New menu button appearance"));
    }

    public static void add_General_Changes(ArrayList<ChangeInfo> changeInfos ){
	    ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "General", false, Window.SHPX_COLOR);
        changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 6), "New Class!",
                "Asriel Dreemur joins the dungeon crawling!\n\n" +
                        "The Conjurer - new class, that are focused on new type of weapons - summon weapons. Thanks to his great soul power, here are able to control more allies and support them by his unique equipment.\n\n"+
                        "Unfortunately, Conjurer's body is composed from dust, so he have lowered physical stats compared to other classes."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GREY_RAT_STAFF, null), "Summon Weapons!",
                "The new type of weapons have been added into dungeon.\n\n" +
                        "_-_ Summon weapons can be used to summon the unique allies, which type depends on type's staff.\n\n" +
                        "_-_ As all weapons, summon weapons are splitted into 5 tiers, with more unique and powerful minions, that can carry enchantment and level of weapon, as you progress.\n" +
                        "_-_ Summon weapons can summon unlimited smount of creatures, but you can control only few."));
        changes.addButton( new ChangeButton(new Image(Assets.BUFFS_LARGE, 112, 32, 16, 16), "Attunement",
                "_Attunement_ - the new hero stat which defines max amount of creatures, that you can control at once.\n\n"+
                        "_-_ Any minion require some amount of attunement (very likely, 1) to be summoned.\n\n"+
                        "_-_ More powerful minions require 2 or 3 attunement.\n\n"+
                        "_-_ Conjurer have 2 max attunement on start and increases his attunement by 1 every 6 levels.\n\n"+
                        "_-_ Other classes can increase their attunement with elixir of attunement."));

    }

    public static void add_Items_Changes(ArrayList<ChangeInfo> changeInfos){
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Items", false, 0x5ba5ff);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RING_AGATE, null), "Ring changes",
                        "_-_ Ring of Tenacity was removed. Their effect are too unnoticable and hard to benefit from them.\n\n" +
                        "_-_ Ring of Elements and Ring of Tenacity were merged into Ring of Resistance, providing the static damage reduction from most sources.\n\n" +
                        "_-_ Added the Ring of Attunement, that increases max attunement and minion's damage."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN, null), "Scroll changes",
                "_-_ Scroll of Terror was removed. It's was useful, but somewhat rare to be decent item.\n\n" +
                        "_-_ Added the Scroll of Attunement, that increases speed and damage reduction for all minions in sight, and inflict Weakness to enemies.\n\n" +
                        "_-_ Replaced the Stone of Affection with Stone of Targeting, that can be used to setup positions for minions.\n\n"+
                        "_-_ Added the Scroll of Soul Energy, that summons powerful invincible minion at cost of all attunement."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_LOVE1, null), "New artifact!",
                "Added the new artifact: LOVE Holder.\n\n" +
                        "_-_ It's unique artifact for Conjurer: you can't get it by other means.\n\n" +
                        "_-_ LOVE Holder allows hero to collect a soul energy, that can be used for healing minions.\n\n"+
                        "_-_ As you heal a minions, artifact will grow in power and obtain the new abilities."));
        changes.addButton( new ChangeButton(new ConjurerArmor(),
                "The new armor item, exclusive for Conjurer.\n\n" +
                        "_-_ It's can't be unequipped by any means, but you can merge any identified armor with Robe to upgrade the robe.\n\n"+
                        "_-_ Deltarune Robe have several skills, two of them are available after getting the subclass.\n\n"+
                        "_-_ First skill is paralysing all enemies in sight for short time, second summons chaos saber, third cast soul spell, which effect depends on choosed subclass.\n\n"+
                        "_-_ With every upgrade it will increase newly summoned minions hp by 10% per level."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ENRAGE, null), "New spells!",
                "_-_ Magical Porter was removed. It's almost useless.\n\n" +
                        "_-_ Added the Enrage spell, that amoks and enrages the target for some time.\n\n" +
                        "_-_ Added the Containing spell, that allows you to capture monsters and use them as temporary allies."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.BREW_PERFUME, null), "New brews!",
                "_-_ Added the Elixir of Attunement, that permamently increases user's attunement.\n\n" +
                        "_-_ Added the Elixir of Rage, that creates a adrenaline shortliving gas.\n\n" +
                        "_-_ Added the Vial of Perfume, that releases a charming and attracting cloud of perfume."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.CLEAVER, null), "New weapons!",
                "_-_ Added the cleaver, that have low damage and accuracy, but can overkill the enemy with small chance."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SLINGSHOT, null), "New missile weapon!",
                "_-_ Warrior now have the slingshot, that can shoot one stone at time, and upgrades with gaining strength."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RUNIC_BLADE, null), "Runic Blade rework",
                "Runic Blade were reworked from 't5, but t4' to totally new weapon!\n" +
                        "_-_ Damage were reduced from 4-20 (+1/+6) to 4-16 (+1/+4)\n" +
                        "_-_ Weapon now can shoot magical copies into enemies, that ignore armor and have very high accuracy\n" +
                        "_-_ After shooting, runic blade need to recharge for some time, that time equals 40 usings of weapon, ignoring the RoF"));

    }

    public static void add_Mobs_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Mobs", false, 0xffc511);
        changes.addButton( new ChangeButton(new Image(Assets.SNAKE, 0, 0, 12, 12), "New monster in Caves",
                "The Caves now have new inhabitant: Rattle Snakes!\n\n"+
                        "_-_ They shoot the deadly darts on range, but are weak in melee.\n"+
                        "_-_ They have resistant to most of controllable magical attacks.\n" +
                        "_-_ Shakes have very high evasion, but low HP.\n"+
                        "_-_ They drop a darts for crossbows."));
        changes.addButton( new ChangeButton(new Image(Assets.NECRO, 0, 0, 16, 16), "Necromancer",
                "Added the necromancer from Shattered 0.7.5. Their stats are buffed compared to Shattered's.\n\n"+
                        "_-_ They have more HP now.\n\n"+
                        "_-_ They buff their skeleton both with Adrenaline and Empowered buffs.\n"+
                        "_-_ Drop: random staff with 12.5% chance."));
        changes.addButton( new ChangeButton(new Image(Assets.TENGU, 0, 0, 14, 16), "New Tengu",
                "Added Tengu fight from Shattered 0.7.5. Nothing are changed."));
    }

    public static void add_Minor_Changes(ArrayList<ChangeInfo> changeInfos){
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Other", false, 0x651f66);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GOLD, null), "Gold balance changes",
                "_-_ Because of summon weapons, gold appear a slighty rarer, but in more quantity.\n\n"+
                                        "_-_ The shops have been extended to hold more valuable items.\n\n"+
                                        "_-_ You can farm gold in Prison by killing the thieves."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), "Healing item changes",
                "Potion of Healing and Elixir of Honeyed Healing now create the clouds of healing gases on shattering.\n\n" +
                        "When PoH cloud heal anything, EoHH are more concentrated and heal only allies."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SWORD, new ItemSprite.Glowing()), "Unstable enchantment",
                "Now have rainbow shining as glowing effect."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.KUNAI, null), "Sneaky weapon changes",
                "Throwing sneaky weapons now doesn't consume a durability, while used with sneak attack"));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered 0.7.5",
                                        "Added the new camera panning from new Shattered."));
        changes.addButton( new ChangeButton(new Image(Assets.BUFFS_LARGE, 64, 16, 16, 16), "Resistance",
                "If monsters are resisting the effect, effect's damage now square-rooted rather that halfing."));
    }

	
	public static void add_v0_1_1_Changes( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo("v0.1.1", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
				"_-_ Released August 15th, 2014\n" +
						"_-_ 10 days after Shattered v0.1.0\n" +
						"\n" +
						"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(new Blandfruit(),
				"Players who chance upon gardens or who get lucky while trampling grass may come across a new plant: the _Blandfruit._\n\n" +
						"As the name implies, the fruit from this plant is pretty unexceptional, and will barely do anything for you on its own. Perhaps there is some way to prepare the fruit with another ingredient..."));
		
		changes.addButton( new ChangeButton(new ItemSprite(new Ankh()), "Revival Item Changes",
				"When the Dew Vial was initially added to Pixel Dungeon, its essentially free revive made ankhs pretty useless by comparison. " +
						"To fix this, both items have been adjusted to combine to create a more powerful revive.\n\n" +
						"Dew Vial nerfed:\n" +
						"_-_ Still grants a full heal at full charge, but grants less healing at partial charge.\n" +
						"_-_ No longer revives the player if they die.\n\n" +
						"Ankh buffed:\n" +
						"_-_ Can now be blessed with a full dew vial, to gain the vial's old revive effect."));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN, null), "Misc Item Changes",
				"Sungrass buffed:\n" +
						"_-_ Heal scaling now scales with max hp.\n\n" +
						"Scroll of Psionic Blast rebalanced:\n" +
						"_-_ Now deals less self damage, and damage is more consistent.\n" +
						"_-_ Duration of self stun/blind effect increased.\n\n" +
						"Scroll of lullaby reworked:\n" +
						"_-_ No longer instantly sleeps enemies, now afflicts them with drowsy, which turns into magical sleep.\n" +
						"_-_ Magically slept enemies will only wake up when attacked.\n" +
						"_-_ Hero is also affected, and will be healed by magical sleep."));
	}
	
	public static void add_v0_1_0_Changes( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo("v0.1.0", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
				"_-_ Released August 5th, 2014\n" +
				"_-_ 69 days after Pixel Dungeon v1.7.1\n" +
				"_-_ 9 days after v1.7.1 source release\n" +
				"\n" +
				"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SEED_EARTHROOT, null), "Seed Changes",
				"_-_ Blindweed buffed, now cripples as well as blinds.\n\n" +
				"_-_ Sungrass nerfed, heal scales up over time, total heal reduced by combat.\n\n" +
				"_-_ Earthroot nerfed, damage absorb down to 50% from 100%, total shield unchanged.\n\n" +
				"_-_ Icecap buffed, freeze effect is now much stronger in water."));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_SILVER, null), "Potion Changes",
				"_-_ Potion of Purity buffed, immunity duration increased to 10 turns from 5, clear effect radius increased.\n\n" +
				"_-_ Potion of Frost buffed, freeze effect is now much stronger in water."));
		
		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN, null), "Scroll Changes",
				"_-_ Scroll of Psionic blast reworked, now rarer and much stronger, but deals damage to the hero.\n\n" +
				"_-_ Scroll of Challenge renamed to Scroll of Rage, now amoks nearby enemies."));
		
	}
	
}
