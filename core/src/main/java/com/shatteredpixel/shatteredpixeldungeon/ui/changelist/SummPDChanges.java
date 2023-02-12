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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CleanWater;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EnchantParchment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfConjuration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfStench;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class SummPDChanges {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		
		//ChangeInfo changes = new ChangeInfo( "1.1", true, "");
		//changes.hardlight( Window.TITLE_COLOR);
		//changeInfos.add(changes);
        add_1_2_4_Changes(changeInfos);
        add_1_1_7_Changes(changeInfos);
        add_1_1_5_Changes(changeInfos);
        add_1_1_4_Changes(changeInfos);
        add_1_1_3_Changes(changeInfos);
        add_1_1_2_Changes(changeInfos);
        add_1_1_1_Changes(changeInfos);
        add_1_1_Changes(changeInfos);
        add_1_0_1_Changes(changeInfos);
        ChangeInfo changes = new ChangeInfo( "1.0", true, "");
        changes.hardlight( Window.TITLE_COLOR);
        changeInfos.add(changes);
		add_General_Changes(changeInfos);
		add_Items_Changes(changeInfos);
		add_Mobs_Changes(changeInfos);
		add_Minor_Changes(changeInfos);
	}

    public static void add_1_2_4_Changes(ArrayList<ChangeInfo> changeInfos){
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "1.2.4", true, Window.TITLE_COLOR);

        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released February 12th, 2022\n" +
                        "_-_ 132 days after 1.2.3"
        ));
        changes = ChangesScene.createChangeInfo(changeInfos, "New Content", false, 0x10bb00);
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARMOR_MAIL, null), "Armor ranking",
                "Implemented ranking mechanic for armor.\n\n" +
                        "_-_ Just like other tiered items, there are 3 tiers for each armor.\n\n" +
                        "_-_ Armor items are gaining upgrade level every 6 hero's levels."
        ));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.CROWN, null), "Armor abilities",
                "Implemented Shattered's armor abilities, but with nuance.\n\n" +
                        "_-_ Class armor items are removed; the crown instead empowers existing armor with armor ability.\n\n" +
                        "_-_ Only two armor abilities for each class are implemented; ones that already exist as ability items were not implemented.\n\n" +
                        "_-_ Armor abilities that were implemented were significantly buffed."
        ));

        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, 0xd1bb00);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ELIXIR_ICY, null), "Frost",
                "_-_ Reworked VFX of frostburn and frost potions to be less performance-taxing."
        ));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Rewritten the description of rattlesnakes.\n" +
                        "_-_ Minions return faster to hero and can be collected from any distance when descending.\n" +
                        "_-_ Graveyard now affects undead minibosses and bosses, inflicting Doomed on them.\n" +
                        "_-_ Minions are now affected by augmentation.\n" +
                        "_-_ Added all ring-based artifacts into catalog.\n" +
                        "_-_ Removed Hell Bat for being too annoying.\n" +
                        "_-_ Removed rings from codebase altogether.\n" +
                        "_-_ Excessive Underground Paradises are getting deleted.\n" +
                        "_-_ Updated Conjurer's description.\n" +
                        "_-_ Removed Gold Tokens due to ghouls already giving gold.\n" +
                        "_-_ Subtilitas Sigil's upgrading now scales with wand's recharge speed.\n" +
                        "_-_ Bombs with custom exploding behavior now get properly disabled when frozen."
        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Bombs lying inside solid blocks in special rooms\n" +
                        "_-_ Tiering actions doing nothing on conjurer's spells\n" +
                        "_-_ Ability artifacts not being \"upgradable\"\n" +
                        "_-_ Uncommon cursed wand effects being more common than other effects\n" +
                        "_-_ Log spam on Soul of Yendor crafting failing\n" +
                        "_-_ Crazy thieves dropping rings\n" +
                        "_-_ Parchment of Elbereth overflowing its charge above 100%%\n" +
                        "_-_ Mirror of Fates not upgrading\n" +
                        "_-_ Minions showing 0 as their evasion in their description\n" +
                        "_-_ Containment spell not working properly\n" +
                        "_-_ Attack roll functionality not working properly\n" +
                        "_-_ Knight's Concentration spell being spammable\n" +
                        "_-_ Toy Knife not working properly with degrade magic\n" +
                        "_-_ Wand of Stars not working properly\n" +
                        "_-_ Slingshot disappearing on resurrection"
        ));
    }

    public static void add_1_1_7_Changes(ArrayList<ChangeInfo> changeInfos) {

        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "1.2.3", true, Window.TITLE_COLOR);

        changes = ChangesScene.createChangeInfo(changeInfos, "1.2.3a", false, Window.WHITE);
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Crash from equipping certain artifacts.\n" +
                        "_-_ Crash from invalid positions for Special Delivery.\n" +
                        "_-_ Crash for instances of Momentum existing separately from Momentum Boots.\n" +
                        "_-_ Crash for missing sprite of falling monster.\n" +
                        "_-_ Inability to view rank descriptions for Conjurer's spells.\n" +
                        "_-_ Final Froggit and Possessed Rodent not being fully affected by magical protection.\n" +
                        "_-_ Grammar mistakes for Wand of Shadow Beams.\n" +
                        "_-_ Instances of prison cell room being too small to generate.\n" +
                        "_-_ Level cap being too high on Large Enlargement."));

        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released October 4th, 2022\n" +
                        "_-_ 379 days after 1.2.2\n\n" +
                        ""
        ));
        changes = ChangesScene.createChangeInfo(changeInfos, "New Content", false, 0x10bb00);
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_MIRROR, null), "New Artifacts",
                "Following the future plans for more integration of ranking mechanic, rings have been replaced by new and already existing artifacts!\n\n" +
                        "   _-_ Ring of Accuracy -> _Subtilitas Sigil_\n" +
                        "   _-_ Ring of Evasion -> _Mirror of Fates_\n" +
                        "   _-_ Ring of Energy -> _Fuel Container_\n" +
                        "   _-_ Ring of Haste/Furor -> _Momentum Boots_\n" +
                        "   _-_ Ring of Endurance -> _Parchment of Elbereth_\n" +
                        "   _-_ Ring of Force -> _Badge of Bravery_\n" +
                        "   _-_ Ring of Might -> _Guardian's Stone_\n" +
                        "   _-_ Ring of Sharpshooting -> _Silky Quiver_\n" +
                        "   _-_ Ring of Attunement -> _Dried Rose_\n" +
                        "   _-_ Ring of Wealth -> _Master Thieves' Armband_"
        ));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.PERFUME_ABILITY, null), "Abilities",
                "Added new abilities: \n\n" +
                        "_-_ _Perfume Blast_ - replaces the old Brew of Perfume, spreads the perfume gas that distracts enemies."
        ));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, 0xd1bb00);
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_ARMBAND, null), "Artifacts",
            "_- Master Thieves' Armband_ was reworked to its Shattered equivalent (no longer picking up charge from gold, allows to steal items from enemies)\n\n" +
                    "_- Dried Rose_ increases max attunement at rate of 0.5 atu per level and can be used in Gauntlet mode."
        ));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Conducts",
                "_-_ Readjusted score modifiers for most of conducts.\n\n" +
                        "_-_ Tweaked _Enlightened One_ to no longer use amnesia effect.\n\n" +
                        "_-_ _TNT Plague_ explosions only deal 50% damage to the hero.\n\n" +
                        "_-_ Hordes no longer spawn in _Impending Doom_.\n\n" +
                        "_-_ Reimplemented _Axiom Nexus Inside Out_."
        ));
        changes.addButton( new ChangeButton(Icons.get(Icons.DEPTH), "Level gen",
                "_-_ Reduced level sizes to be less tedious to explore (up to 50% reduction in Large Enlargement).\n\n" +
                        "_-_ Prison's cells are no longer allowed to spawn in famous glitchy manner.\n\n" +
                        "_-_ Demon Halls's bomb mazes generate like all other mazes and no longer only feature nukes."
        ));
        changes.addButton( new ChangeButton(Icons.get(Icons.ENERGY), "Alchemical energy",
                "_-_ Implemented Shattered's second alchemy rework.\n\n" +
                        "_-_ Abilities have cost of 18 alchemical energy."
        ));
        changes.addButton( new ChangeButton(new Image(new RatSprite()), "Hordes",
                "_-_ Smaller horde members no longer grant XP.\n\n" +
                        "_-_ Reduced the chance of spawning from 17% to 14%."
        ));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Buffed Conjurer's max HP from 10(+2) to 13(+3).\n\n" +
                        "_-_ Tweaked ranking descriptions for every rankable item to be more streamlined and feature less repetition.\n\n" +
                        "_-_ Super-shots deal more base damage and do it more consistently.\n\n" +
                        "_-_ Nerfed Firebomb's fire field duration by 60%.\n\n" +
                        "_-_ Added guide page about ranking mechanic, thanks to @Zackary4536.\n\n" +
                        "_-_ Minion staves now can recharge when their respective minion is alive, but at 33% efficiency.\n\n" +
                        "_-_ Soul of Yendor shows icons of artifacts it can use as abilities.\n\n" +
                        "_-_ Miasma no longer deals damage over time.\n\n" +
                        "_-_ Super-shots no longer invoke targeting when unable to shoot.\n\n" +
                        "_-_ Conjurer's spells are considered unique for revival purposes.\n\n" +
                        "_-_ Added placeholder surface sprite for Adventurer.\n\n" +
                        "_-_ Adjusted Hacatu's power scaling to account for reduced max attunement.\n\n" +
                        "_-_ Nerfed Wand of Corrosion's rank II.\n\n" +
                        "_-_ Added third slot for artifacts.\n\n" +
                        "_-_ Rewritten the descriptions of certain items to sound better, thanks to @Zackary4536."
        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Lightning Explosive dealing no damage.\n" +
                        "_-_ Elixirs not producing any particles when thrown into the ground.\n" +
                        "_-_ Wands showing their rank when unidentified.\n" +
                        "_-_ Penetration debuff not scaling with Wand of Shadow Beams's ranks.\n" +
                        "_-_ Broken Seal mentioning armor in inventory prompt.\n" +
                        "_-_ Stationary staves consuming attunement even when their normal counterparts do not.\n" +
                        "_-_ Toy Knife's displayed name not changing with enchantments or curses."
        ));

        changes = ChangesScene.createChangeInfo(changeInfos, "1.2.2", true, Window.TITLE_COLOR);

        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released September 20th, 2021\n" +
                        "_-_ 73 days after 1.2.1\n\n" +
                        ""));
        changes = ChangesScene.createChangeInfo(changeInfos, "New Content", false, 0x10bb00);
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Conducts",
                "_Conducts_ are replacing challenges as new way of setting run modifiers.\n\n" +
                        "_-_ You can choose only one conduct.\n\n" +
                        "_-_ They can not only increase the difficulty, but also be beneficial to your character.\n\n" +
                        "_-_ There is 15 conducts to try out right now!"));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ELEMENTAL_BLAST, null), "Abilities",
                "_Abilities_ are new Scroll of Upgrade-based artifacts, acting like active ability.\n\n" +
                        "_-_ They have charge cost and recharge to full in _500 turns_.\n\n" +
                        "_-_ Their effects can be modified with tiering mechanic.\n\n" +
                        "_-_ There is 13 available abilities, 12 of which are made with alchemy."));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null), "Wands rework",
                "Overhauled wands to make them compatible with tiering and more variative overall:\n\n" +
                        "_-_ Now have tiers, like abilities, bow and summoning staves.\n" +
                        "_-_ The max charge is always 3, regardless of wand or tier of it.\n" +
                        "_-_ Can be used as melee weapon on Mage, their melee damage scales with hero's strength.\n" +
                        "_-_ Reworked magical effects to scale from player's level.\n" +
                        "_-_ _Battlemage_ have been changed to scale with hero's level.\n" +
                        "_-_ _Warlock_ have been changed to have much greater soul mark chance that scales with hero's level, as wands do not upgrade traditionally anymore.\n" +
                        "_-_ _Removed_ Mage's Staff. All existing instances of staff will be replaced with corresponding wand."));
        changes.addButton(new ChangeButton(Icons.get(Icons.ENTER), "New game modes",
                "Added three new game modes:\n\n" +
                        "_-_ _Increased Difficulty_ makes every monster stronger by giving them various abilities and attack overhauls.\n\n" +
                        "_-_ _Proficient Polygon Catacombs_ turns the game into slightly-grindy RPG: you can grind XP and loot forever, without most of limits.\n\n" +
                        "_-_ _Subterranean Diversity_ uses cave-like level generator, creating the feel of more traditional roguelikes."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.ROGUE, 0, 90, 12, 15), "Rogue rework (part 1)",
                "Added tier 1 and tier 2 Rogue talents!\n\n" +
                        "_-_ This makes him the only hero that can recieve talents, as his gameplay mechanic.\n\n" +
                        "_-_ Tier 1 includes 6 talents, and tier 2 introduces whooping 12 talents! Choose your own style of gameplay.\n\n" +
                        "_-_ The balance issues are possible, so please report about them."));
        changes.addButton( new ChangeButton(Icons.get(Icons.DEPTH), "Level gen",
                "_-_ Added _5 new thematic rooms_, one for each stage!\n\n" +
                        "_-_ Added new level generator that replaces tunnels with more regular rooms."));

        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, 0xd1bb00);
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), "Potion of Healing",
                "_-_ Reduced base healing by 10 points (up to _-20%_ depending on hero's level).\n" +
                        "_-_ Reduced healing speed by _20%_.\n" +
                        "_-_ Enemy damage nerfs:\n" +
                        "   Skeleton: 2-10 -> 2-9 (melee), 6-12 -> 6-10 (explosion)\n" +
                        "   Prison Guard: 4-12 -> 3-11\n" +
                        "   Crazy Thief: 1-10 -> 0-10\n" +
                        "   DM100: 3-10 -> 2-9 (lightning)\n" +
                        "   Necromancer: 2-10 -> 1-9 (squashing)\n" +
                        "   Vampire Bat: 5-18 -> 4-16\n" +
                        "   Gnoll Brute: 5-25 -> 4-23, 15-40 -> 12-36 (berserker)\n" +
                        "   Rattlesnake: 8-18 -> 5-16 (ranged)\n" +
                        "   Gnoll Shaman: 4-10 -> 4-9 (melee), 6-15 -> 5-13 (magic)\n" +
                        "   DM200: 10-25 -> 7-22\n" +
                        "   Cave Spinner: 5-15 -> 4-13\n" +
                        "   Dwarven Ghoul: 16-22 -> 14-20\n" +
                        "   Elementals: 20-25 -> 17-22\n" +
                        "   Monk: 12-25 -> 11-20\n" +
                        "   Warlock: 12-18 -> 11-16 (melee), 12-18 -> 10-16 (magic)\n" +
                        "   Golem: 25-30 -> 21-26\n" +
                        "   Armored Vessel: 10-18 -> 8-16\n" +
                        "   Attunement Spirit: 13-18 -> 11-16\n" +
                        "   Dark Matter Slime: 18-23 -> 15-20\n" +
                        "   Succubus: 24-34 -> 21-29\n" +
                        "   Evil Eye: 18-33 -> 13-28 (melee), 40-65 -> 35-57 (deathgaze)\n" +
                        "   Hell Bat: 16-17 -> 13-15\n" +
                        "   Scorpio: 30-40 -> 25-33\n" +
                        "   Ripper Demon: 15-25 -> 13-22"));
        changes.addButton( new ChangeButton(new Image(new RatSprite()), "Hordes",
                "_-_ Fixed some bugs making horde members extra stupid.\n\n" +
                        "_-_ Hordes cannot spawn around creatures that already fight in masses."
        ));
        changes.addButton(new ChangeButton(Icons.get(Icons.ENTER), "Game Mode Changes",
                "_-_ Greatly improved the stability for Project Paradox.\n\n" +
                        "_-_ Removed a lot of limitations for Gauntlet Mode and removed traps from it.\n" +
                        "   - Floor is longer getting locked.\n" +
                        "   - You can't ascend.\n" +
                        "   - You can sell everything.\n" +
                        "   - You can get class-oriented weapons and bags.\n" +
                        "_-_ Changed the order in game mode list."));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.MASTERY, null), "Subclasses",
                "_-_ Buffed Sniper: now has less chance to miss and can use thrown weapons to proc enchantments from the bow.\n\n" +
                        "_-_ Buffed Robinson: now gets extra damage with specials."));
        changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "ShatteredPD",
                "_-_ Ported many technical changes from ShPD 1.0.1."));
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Adjusted gold spawning again.\n\n" +
                        "_-_ Wands now show their recharge time in description.\n\n" +
                        "_-_ Abyss shops get sandbags.\n\n" +
                        "_-_ Re-added GitHub updates.\n\n" +
                        "_-_ Added unique sprites for Adventurer starter items.\n\n" +
                        "_-_ Game modes affect the score now.\n\n" +
                        "_-_ Minions follow hero at hero's speed.\n\n" +
                        "_-_ Games in progress are getting ordered by their Summoning's score formula.\n\n" +
                        "_-_ Increased the hunger rates, but decreased the starving rates.\n\n" +
                        "_-_ Massively decreased the amount of items generated in dungeon.\n\n" +
                        "_-_ Skeleton explosions can be dodged.\n\n" +
                        "_-_ Enchant parchments with same enchantments can stack.\n\n" +
                        "_-_ Changed the look for buttons again.\n\n"+
                        "_-_ Adjusted the chance for hordes to 1/6 at any stage and limited max horde members to 3. This should make endgame massively less spammy.\n\n" +
                        "_-_ Slightly buffed wand recharge.\n\n" +
                        "_-_ Changed how minions act and follow the hero.\n\n" +
                        "_-_ Changed how potion of strength is generated in Fast Adventure.\n\n" +
                        "_-_ Added experimental toggle to allow multiple conducts. Trust me, it's not balanced.\n\n" +
                        "_-_ Conducts window's info buttons show correct descriptions for conducts.\n\n" +
                        "_-_ Selecting a null conduct deselects all the other for convenience.\n\n" +
                        "_-_ Goldfish Memory properly now blocks any identification.\n\n" +
                        "_-_ Endless Potential characters can gather mana now.\n\n" +
                        "_-_ Added new conduct that minimizes the loot from dungeon.\n\n" +
                        "_-_ Entropy Drive doesn't consume additional hunger anymore, converts progression items or uses transmutation animation.\n\n" +
                        "_-_ Added new conduct that makes monsters a limited resource.\n\n" +
                        "_-_ Increased the score modifier for certain conducts."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Rat bombs not working half of the time.\n" +
                        "_-_ Frequent freezes with chaotic level gen.\n" +
                        "_-_ Change windows not being scrollable.\n" +
                        "_-_ Wrong icon for Ring of Endurance.\n" +
                        "_-_ Stone of Targeting being not useful.\n" +
                        "_-_ Some minions falling despite levitating in their appearance.\n" +
                        "_-_ Goo minion killing allies.\n" +
                        "_-_ Getting stuck in wall if you used amulet in Gauntlet in certain way.\n" +
                        "_-_ Goo minion not being able to attack after unsuccessful pump attack.\n" +
                        "_-_ Gauntlet mode mobs dropping additional loot when rest of them have died already.\n" +
                        "_-_ Soul of Yendor yielding disproportional numbers for sandals and armband components.\n" +
                        "_-_ Majestic Guard's room spawning items in walls.\n" +
                        "_-_ Softlock for Erupting Darkness.\n" +
                        "_-_ Debug string gathering for conducts.\n" +
                        "_-_ Crash for Warrior's special attacks.\n" +
                        "_-_ Crashes for dungeon generation.\n" +
                        "_-_ Crash for t3 chicken.\n" +
                        "_-_ Crash for wraiths that have rose as parent.\n" +
                        "_-_ Crash with Special Delivery spawning items out of bounds.\n" +
                        "_-_ Conjurer's Armor being removable by any means.\n" +
                        "_-_ Majestic Guard's room spawning items in walls.\n" +
                        "_-_ Innate Rage crashing when player is dying.\n"+
                        "_-_ Crash when game couldn't read conducts from save file.\n" +
                        "_-_ Crash for one of Knight's spell.\n" +
                        "_-_ Crash with Electrical Explosive and made it charged from beginning.\n" +
                        "_-_ Error with attunement consumption for Soul Flame.\n" +
                        "_-_ Crash for some of Battlemage effects.\n" +
                        "_-_ Crash for certain level builders.\n" +
                        "_-_ Items in walls for pylon room.\n" +
                        "_-_ Issue when Stars t2 couldn't consume charges.\n" +
                        "_-_ Rare crash for transmutations.\n" +
                        "_-_ Electrical explosive's damage.\n" +
                        "_-_ Exploit with ropes and chests.\n" +
                        "_-_ Some exploits and issues with chalice regen.\n" +
                        "_-_ Issue with 4/3 wands.\n" +
                        "_-_ Issue with TNT mouse and NPCs.\n" +
                        "_-_ Some Increased Difficulty enemies.\n" +
                        "_-_ More crashes for Project Paradox.\n"+
                        "_-_ Missiles being stuck in a wall in rare cases for gnoll tribe room.\n" +
                        "_-_ Crash for Spectral Shaman."));



        changes = ChangesScene.createChangeInfo(changeInfos, "1.2.1", true, Window.TITLE_COLOR);

        changes = ChangesScene.createChangeInfo(changeInfos, "1.2.1b", false, Window.WHITE);
        changes.addButton(new ChangeButton(new EnchantParchment(),
                "Added enchantment parchment from legacy era.\n\n" +
                        "_-_ Allows to transfer enchantments and glyphs between items."));
        changes.addButton(new ChangeButton(new WandOfConjuration(),
                "Changed the Wand of Conjuration:\n" +
                        "\n" +
                        "_-_ Buffed damage from _2-5_ to _4-8_\n" +
                        "_-_ Reduced max amount of swords by 1\n" +
                        "_-_ Reduced max charges to 3\n" +
                        "_-_ Removed additional damage from enemy's armor\n" +
                        "This should make it more consistent and less broken in endgame."));
        changes.addButton(new ChangeButton(new Bomb(),
                "_-_ Added damage description to every harmful bomb.\n\n" +
                        "_-_ Buffed bomb's damage by _25%_."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ The crash when trying to equip any weapon as anybody but Warrior.\n" +
                        "_-_ Inconsistent dynamic chapter size math.\n" +
                        "_-_ Conjuration wand allies disappeariance.\n" +
                        "_-_ Ghost Chickens dealing way more damage than they should."));


        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released July 9th, 2021\n" +
                        "_-_ 30 days after 1.2\n\n" +
                        "This update was firstly a quick experiment about adding game modes but eventually got into one big 1.2 wrap-up project, so we could move forward and beyond. Thanks for waiting and testing!"));

        changes = ChangesScene.createChangeInfo(changeInfos, "New Content", false, 0x10bb00);
        changes.addButton( new ChangeButton(Icons.get(Icons.ENTER), "Game Modes",
                "Added game modes, that modify game's rules in some way:\n\n" +
                        "_-_ Moved old Dungeon Size modifier to game modes.\n\n" +
                        "_-_ Some of modes are gimmicky, some of modes are super challenges.\n\n" +
                        "_-_ Check by yourself, there is a lot of interesting stuff!\n\n" +
                        "_-_ Older runs will be converted into size mode that was set before update."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.HUNTRESS, 0, 15, 12, 15), "Huntress changes",
                "The Huntress recieved brand new starting armor to make her more stylish and strong.\n\n" +
                        "_-_ With new armor, she can use _Super-Shots_ to shoot stronger arrow which does more damage with distance.\n\n" +
                        "_-_ This ability has 20 turns cooldown.\n\n" +
                        "_-_ The armor works as +1 cloak armor, but still can be viable in endgame."));

        changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_ABYSS, 0, 64, 16, 16 ), "Abyss and Endless mode",
                "Changed Abyss levels to be less annoying and more interesting:\n\n" +
                        "_-_ Abyssal mobs no longer rush at you because of amulet.\n\n" +
                        "_-_ All abyssal mobs are demons and undead and recieved HP nerf, but get stronger as depth gets lower.\n\n"+
                        "_-_ Significantly decreased the size of abyss levels.\n\n"+
                        "_-_ Abyss no longer multiplies the amount of monsters on each level.\n\n"+
                        "_-_ Added extra food item to each floor; torches last 3x longer.\n\n"+
                        "_-_ Abyssal Spawner no longer spam mobs and recieve more damage.\n\n"+
                        "_-_ Added Pelsjagers, who corner you with traps.\n\n"+
                        "_-_ Reworked Abyssal Nightmares: now are large hefty enemies, who are immune to everything but Amok and melee, and can see their enemies and eliminate all obstacles while trying to reach them.\n\n" +
                        "_-_ Ghost Chickens deal less damage and die at 0 HP instead of -1.\n\n" +
                        "_-_ Blinking Mans deal less damage and less knockback, but are willing to blink more often and will retreat slower than player walks.\n\n" +
                        "_-_ Darkest Elf now cast Dark Matter Slimes instead of gaster blasters.\n\n" +
                        "_-_ Added Abyssal Dragons, who breath fire, spawn smaller dragons and attack with sweeping.\n\n" +
                        "_-_ Added Lost Spirit, who empower your enemies with champion abilities.\n\n"+
                        "_-_ Abyssal bosses can spawn in big amounts on levels before shop.\n\n"+
                        "Also tweaked the endgame of Shattered:\n\n" +
                        "_-_ Yog cast lasers 2.2x less frequently, abyssal flame walls stay 2 turns longer to compensate."));

        changes.addButton( new ChangeButton(new Image(new RatSprite()), "Hordes",
                "_-_ Added mob hordes, that can spawn infrequently replacing normal monsters.\n\n" +
                        "_-_ Horde members are acting as one team, with leaders and followers.\n" +
                        "_-_ Leaders have double health.\n" +
                        "_-_ Followers have 50% health, but recieve less damage when leader is alive."
        ));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, 0xd1bb00);

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Added mouse targeting for Desktop builds.\n" +
                        "_-_ Added quick item use while holding inventory's button.\n" +
                        "_-_ Reworked the visuals for frostburn and webbing objects.\n" +
                        "_-_ Massively reduced the amount of gold on floors, but also reduced the price in shops from 5x to 2x, making selling more viable as money source.\n" +
                        "_-_ Player and allies can shoot projectiles through other ally characters.\n" +
                        "_-_ Adjusted an unintentionally strong Piercing Shot on a bow.\n" +
                        "_-_ Hunger damage no longer interrupts you.\n" +
                        "_-_ Replaced Potion of Stamina with Potion of Adrenaline.\n" +
                        "_-_ Ported subclass choosing and hero info windows from Shattered 0.9.3.\n" +
                        "_-_ Ported quick-use inventory from Shattered 0.9.3.\n" +
                        "_-_ Merged some of elemental potions.\n" +
                        "_-_ Buffed warrior: now identifies stuff on equip and can equip things from quickslots.\n" +
                        "_-_ Massively decreased the chakra decay rate, making monk more viable."));

        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 112, 32, 16, 16), "Tiering",
                "Fixed a huge issue with minion tier being reset at reload."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.STARS, null), "Conjurer buffs",
                "_-_ Buffed _Star Blazing_ and overhauled its visuals:\n\n" +
                        "   _Tier I:_ +33% max damage, +5% scaling\n" +
                        "   _Tier II:_ +25% min damage, +33% max damage, +~28% scaling\n" +
                        "   _Tier III:_ +38% min damage, +46% max damage, +~45% scaling, mana cost: _7_ -> _8_\n\n" +
                        "_-_ Buffed tier I and II and nerfed tier III of _Energizing Renewal:_\n\n" +
                        "   _Tier I:_ +66% flat heal, +34% percentage heal\n" +
                        "   _Tier II:_ +66% flat heal, +32% percentage heal, mana cost: _3_ -> _4_\n" +
                        "   _Tier III:_ +75% flat heal, -67% percentage heal\n\n" +
                        "_-_ Buffed tier II and changed tier III of _Beam of Affection:_\n\n" +
                        "   _Tier II:_ +85% mana gain, replaced speed boost for enemies by shaman buffs, mana cost: _9_ -> _11_\n" +
                        "   _Tier III:_ -1 turn of speed boost for minions, mana cost: _15_ -> _14_\n\n" +
                        "_-_ Reworked _Runic Shell_:\n\n" +
                        "   _Tier I:_ -75% flat shielding, added 25% of max HP shielding\n" +
                        "   _Tier II:_ +25% flat shielding, -100% percentage shielding, mana cost: _7_ -> _15_\n" +
                        "   _Tier III:_ -100% shielding, gives Temporary Block buff instead, mana cost: _16_ -> _20_\n\n" +
                        "_-_ Reworked _Pushing Waveform_:\n\n" +
                        "   _-_ Knockback strength is always at 3 instead of scaling with tiering.\n" +
                        "   _-_ Minions are teleported to you instead of being pushed away.\n" +
                        "   _-_ Tiering increases the cone's width, increased the range and decreased mana cost for every tier.\n\n" +
                        "_-_ Halfed the cost of _Bridge of Artemis_."

        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Goo minion being stuck in place if enemy died before elemental attack.\n" +
                        "_-_ Shock explosives crashed because of heap issues.\n" +
                        "_-_ Magic missile sentries being useless.\n" +
                        "_-_ Abyss descend not working properly.\n" +
                        "_-_ Shrinking bomb crashing on boss floors.\n" +
                        "_-_ Instant use spells not consuming mana.\n" +
                        "_-_ Blinking Man crashing the game when trying to teleport.\n"+
                        "_-_ Abyssal spawners spawning obscene amount of monsters.\n" +
                        "_-_ Attunement spirits dying instantly in some cases.\n" +
                        "_-_ Phantoms giving EXP up to level 100.\n" +
                        "_-_ Ghost Chickens being not invisible if summoned.\n" +
                        "_-_ Fixed crash with Stone of Intuition and rings.\n"+
                        "_-_ Soul of Yendor being overcharged from gold.\n" +
                        "_-_ Removed softlock protection.\n" +
                        "_-_ Project Paradox's item generator affecting all subsequent runs until restart.\n" +
                        "_-_ Broken Seal extra upgrade infinitely transfering over items.\n" +
                        "_-_ Broken Seal particle lingering after weapon's hotswap.\n" +
                        "_-_ Runestones in scroll decomposition being identified even if scroll is unknown."));

        changes = ChangesScene.createChangeInfo(changeInfos, "Minion Rebalancing", false, 0x5ed8ff);

        changes.addButton(new ChangeButton(new FroggitStaff(),
                "While offensive power of froggit was somewhat alright, he was too squishy to survive multiple mobs or skeleton explosions even at tier III.\n\n" +
                        "_-_ Average damage reduced by _25%/41%/42%_.\n" +
                        "_-_ HP increased to _15/20/25_ from _10/17/23_.\n" +
                        "_-_ Armor is fixed value of _2/2/4_ instead of 0-1 at all tiers.\n" +
                        "_-_ Increased recharge to 400 turns."));

        changes.addButton(new ChangeButton(new GreyRatStaff(),
                "Gray Rats at T1 and T2 were too strong against sewers and prison enemies, so I decided to transfer this power to more vulnerable state:\n\n" +
                        "_-_ Average damage adjusted by _-19%/-4%/8%_.\n" +
                        "_-_ HP adjusted by _6%/-15%/-40%_.\n" +
                        "_-_ Attunement cost is 1.5 on all tiers now.\n" +
                        "_-_ Armor roll have been rebalanced:\n\n" +
                        "   _Tier I:_ 0-3 -> 0\n" +
                        "   _Tier II:_ 2-5 -> 2-7\n" +
                        "   _Tier III:_ 4-10 -> 3-12"));

        changes.addButton(new ChangeButton(new SlimeStaff(),
                "_-_ Slimes are able to switch to other targets if current can't be attacked for some reason. This allows Slimes to stall _several enemies at once._"));

        changes.addButton(new ChangeButton(new SheepStaff(),
                "Sheep's armor on higher tier was much lower than excepted, and higher tiers were underpowered at the best:\n\n" +
                        "_-_ Attunement cost adjusted to _1/2/3_ from _1/2.25/3.5_.\n" +
                        "_-_ Armor roll have been rebalanced (rank info was incorrect):\n\n" +
                        "   _Tier II:_ 2-13 -> 6-12\n" +
                        "   _Tier III:_ 3-20 -> 11-18"));

        changes.addButton(new ChangeButton(new SkeletonStaff(),
                "Skeleton's power on higher tiers is too inconsistent to be balanced, and it just feels counterintuitive.\n\n" +
                        "_-_ Damage roll have been rebalanced:\n\n" +
                        "   _Tier I:_ 4-8 -> _5-10_\n" +
                        "   _Tier II:_ 2-6 -> _6-15_\n" +
                        "   _Tier III:_ 1-4 -> _7-20_\n" +
                        "_-_ Increased HP by _20%_ on all tiers.\n" +
                        "_-_ Increased armor from 0-4 to 3-5.\n" +
                        "_-_ Adjusted recharge to _600 turns_ on all tiers.\n" +
                        "_-_ Now attacks once per two turns and aggros every mob in 5x5 area.\n" +
                        "_-_ Adjusted attunement to 1 on all tiers."));

        changes.addButton(new ChangeButton(new ChickenStaff(),
                "With Runic Shell being greatly nerfed for fragile minions, I can make chickens fun:\n\n" +
                        "_-_ Average damage decreased by _62%/66%/69%_.\n" +
                        "_-_ Increased evasion by 3x.\n" +
                        "_-_ Attunement cost adjusted to _0.25/0.5/1_, from _0.5/0.75/1.25_.\n" +
                        "_-_ Recharge adjusted to _33/100/250_, from _90/45/20_.\n" +
                        "_-_ Chickens move and attack at 2x speed.\n" +
                        "_-_ Tier II chickens inflict Crippled.\n" +
                        "_-_ Tier III chickens inflict Terrified."));

        changes.addButton(new ChangeButton(new MagicMissileStaff(),
                "Magic Missile minion is pretty strong, but can get quickly destroyed by monsters, which puts it at disadvantage to Wand of Warding.\n\n" +
                        "_-_ HP increased by _150%/247%/300%_.\n" +
                        "_-_ Decay changed to _1/3/4_, from _2/2/1_.\n" +
                        "_-_ Can be placed at any spot in hero's FOV, not just adjacent tiles.\n" +
                        "_-_ Fixed the bug with MM's damage being not affected by Soul Sparking."));

        changes.addButton(new ChangeButton(new GnollHunterStaff(),
                "Gnoll Hunter is also pretty powerful unit, he just needs some boost in late game.\n\n" +
                        "_-_ HP increased by _0%/25%/78%_.\n" +
                        "_-_ Now properly ignores armor on any tier.\n" +
                        "_-_ Now properly can outrun any enemy."));

        changes.addButton(new ChangeButton(new FrostElementalStaff(),
                "As lategame minion, Frost Elemental only needs toughness boost, honestly.\n\n" +
                        "_-_ HP increased by _10%_.\n" +
                        "_-_ Armor increased to _4-13_, from 2-7."));

        changes.addButton(new ChangeButton(new WizardStaff(),
                "T2 and T3 Wizards were very hindered by their lack of toughness and huge recharge, so I decided to make tiering a proper cost reduce:\n\n" +
                        "_-_ HP increased by _10_ on all tiers.\n" +
                        "_-_ Armor increased to _2-10_, from 1-5.\n" +
                        "_-_ Attunement cost adjusted to _2/1.5/0.75_, from _1.5/1/0.75_.\n" +
                        "_-_ Recharge adjusted to _700/500/300_, from _500/900/1400_.\n" +
                        "_-_ Adjusted attack rate to _1/1.5/2_, from _1/1.25/1.5_, made it work with magic (bruh)\n"+
                        "_-_ Removed magic damage from tiering.\n" +
                        "_-_ Added debuff duration stat.\n" +
                        "_-_ Added magic resistance stat."));

        changes.addButton(new ChangeButton(new RoseStaff(),
                "Rose Wraith also doesn't need a much of buffs.\n\n" +
                        "_-_ HP increased by _50%/22%/0%_.\n" +
                        "_-_ Increased evasion by 25%.\n" +
                        "_-_ Wraiths die when out of rose wraith's FOV.\n" +
                        "_-_ Can be placed at any spot in hero's FOV, not just adjacent tiles.\n" +
                        "_-_ Fixed wrong stats in rank info."));

        changes.addButton(new ChangeButton(new RoboStaff(),
                "While DM-150 is a good wall, it was stupid to make them just die over magical attacks:\n\n" +
                        "_-_ HP decreased by _20%/14%/19%_.\n" +
                        "_-_ Armor increased to _12-22_, from 10-18.\n" +
                        "_-_ Damage increased by _5-10_ for all tiers.\n" +
                        "_-_ Attunement cost adjusted to _1.5/2.5/4.25_, from _1.5/2.25/4_.\n" +
                        "_-_ Adjusted attack rate to _0.29/0.4/0.66_, from _0.33/0.5/1_.\n"+
                        "_-_ Removed staying still mechanic.\n" +
                        "_-_ Added mob aggro with 3 tiles radius.\n"+
                        "_-_ Now can grab ranged enemies to force them fight in melee range."));

        changes.addButton(new ChangeButton(new GooStaff(),
                "Goo minion suffers a lot from its undesirable toughness, especially for T2 and T3:\n\n" +
                        "_-_ HP decreased by _36%/31%/23%_.\n" +
                        "_-_ Charged damage adjusted by _-27%/7%/26%_.\n" +
                        "_-_ Attunement cost adjusted to _1.75/3/5_, from _2.5/3/4.25_.\n" +
                        "_-_ Now defers the half of damage done to it (and 3x damage when charging up).\n" +
                        "_-_ Armor roll have been buffed:\n\n" +
                        "   _Tier I:_ 5-8 -> 9-14\n" +
                        "   _Tier II:_ 9-13 -> 14-21\n" +
                        "   _Tier III:_ 13-18 -> 19-28"));

        changes.addButton(new ChangeButton(new BlasterStaff(),
                "Gaster Blaster follows the same buffs as other stationary minions.\n\n" +
                        "_-_ HP increased by _50%/77%/100%_.\n" +
                        "_-_ Increased attack rate by _14%/4%/15%_.\n" +
                        "_-_ Attunement cost adjusted to _2/2.5/3.25_, from _2/2.25/2.75_.\n" +
                        "_-_ Can be placed at any spot in hero's FOV, not just adjacent tiles."));

        changes.addButton(new ChangeButton(new ImpQueenStaff(),
                "For being tanky, Imp Queen was pretty fragile, and imps can get in a way:\n\n" +
                        "_-_ HP increased to _200/300/400_, from _120/60/20_.\n" +
                        "_-_ Adjusted magic attack cooldown to _300/200/150_, from _250/150/75_.\n" +
                        "_-_ Attunement cost adjusted to _3/3.5/4_, from _3/2/1_.\n" +
                        "_-_ Decreased imp's average damage by _32%/36%/100%_.\n" +
                        "_-_ Adjusted imp's HP to _50/25_, from _20/8_.\n" +
                        "_-_ Tier III imp queen no longer spawns imps."));

        changes.addButton(new ChangeButton(new HacatuStaff(),
                "Hacatu is good enough, but it is quite anti-party with its tremendous collateral damage.\n\n" +
                        "_-_ No longer deals damage to you and other allies,\n" +
                        "_-_ Armor increased to _0-12_, from 0-6."));


        changes = new ChangeInfo("1.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released May 9th, 2021\n" +
                        "_-_ 148 days after 1.1.5b\n\n" +
                        "Those delays are getting longer and longer, huh.\n\n" +
                        "Thanks everyone for supporting me in such difficult time for everyone. This release was originally planned to be much less, but unfortunate circumstances have ruined the estimates. But life goes on, and we have a ton content of explore.\n\n" +
                        "Stay tuned for 1.3 and enjoy 1.2!"));

        changes = ChangesScene.createChangeInfo(changeInfos, "Overhauls", false, 0x5ed8ff);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.FROGGIT_STAFF, null), "Staves rework",
                "_-_ Introducing ranking system! Use SoUs on your weapons to change their stats and abilities. Not all of them are direct upgrades, so beware (you can downgrade staff if you don't like its current rank!).\n\n" +
                        "_-_ Base state of most staves is buffed to be viable at regular stages.\n\n" +
                        "_-_ The things are subjective with time, as balance is not properly tested out."
        ));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
                "The rework for goat boy:\n\n" +
                        "_-_ Love Holder is removed.\n\n" +
                        "_-_ Added Conjurer's Book as holder for new items.\n\n" +
                        "_-_ Introducing conjurer's mana spells! They can be obtained by leveling up. At start you have 3 base spells.\n\n" +
                        "_-_ Spells also have rankings with different use cases!\n\n" +
                        "_-_ Subclasses have been completely reworked to have exclusive set of spells, suited for various playstyles.\n\n" +
                        "_-_ As results of big buff, Conjurer's HP is reduced to 10 (+2), and his deltarune robe now works like cloth all time."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.ROGUE, 0, 90, 12, 15), "Rogue",
                "Rogue is undergoing some global changes. Assassin and Freerunner are still in the game, but will be replaced in 1.3 patch."));

        changes = ChangesScene.createChangeInfo(changeInfos, "New Content", false, Window.SHPX_COLOR);

        changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_HALLS, 0, 64, 16, 16 ), "Dynamic Dungeon Size",
                "To make game suit for every kind of player, the dungeon was renovated to have 3 different sizes!\n\n" +
                        "_-_ You can choose between 21 floors, 26 floors and 31 floors.\n\n" +
                        "_-_ 21 floors is for shorter runs, with minimal amount of rooms and clumped generation.\n\n" +
                        "_-_ 26 floors is normal mode, the levels have been increased in size compared to previous releases.\n\n" +
                        "_-_ 31 floors is for long steady runs, with 20 scrolls of upgrade, exclusive mob rotation and increased amount of everything."));

        changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_ABYSS, 0, 64, 16, 16 ), "Abyss and Endless mode",
                "As optional challenge, the bottomless abyss awaits you!\n\n" +
                        "_-_ Use amulet in third way to descend into floor 27. Once you enter, there is no way out.\n\n" +
                        "_-_ Several new monsters and new challenges are residing in Abyss. Mobs are getting respawned by abyssal spawner, destroying which will award you a Scroll of Upgrade.\n\n" +
                        "_-_ Every room appears with same chance in Abyss's cartography, making really unique shape of levels.\n\n" +
                        "_-_ Your goal in Abyss in collecting Chaosstones - the mysterious unstable gems, giving incredible power to wielder (and a truck load of score). If you feel like you can't descend anymore, you can sumbit the amount of found chaos stones into rankings and finish the run."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_OMNI, null), "Soul of Yendor",
                "_-_ Added the artifact, that is 8 other artifacts at once.\n\n" +
                        "_-_ This item combines properties of Horn of Plenty, Alchemical Toolkit, Ethereal Chains, Chalice of Blood, Sandals of Nature, Master Thieves\' Armband, Timekeeper\'s Hourglass and Unstable Spellbook.\n\n" +
                        "_-_ To make it, use cursed wand in some way to combine all artifacts together."
        ));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ROPES, null), "Morshu's wares",
                "_-_ Added ropes, that can be used as consumable version of chains, with ability to pick up items and activate traps.\n" +
                        "_-_ You get 5 ropes at start, and additional ropes can be found in dungeon or bought in shops."
        ));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_CONJURATION, null), "New wand",
                "_-_ Added the Wand of Conjuration, reminiscent of a staff with same name from _Brogue._\n\n" +
                        "_-_ This wand casts spectre swords above you, that will aid in battles.\n\n" +
                        "_-_ The strength of this new wand comes from it being spammable and ability of swords being overwhelming in numbers."
        ));

        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, 0xc65c0c);

        changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Shattered PD Port",
                "_-_ Ported QoL changes and bugfixes from Shattered PD 0.9.1 and 0.9.2.\n\n" +
                        "Talents are gonna be introduced in 1.3 as exclusive for Rogue."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Fixed rare crash on Sad Ghost.\n" +
                        "_-_ Fixed options screen having empty choices.\n" +
                        "_-_ Fixed overfilling HP with Saturation buff.\n" +
                        "_-_ Fixed crash related to Soul Reaver's powers.\n" +
                        "_-_ Fixed crash with stationary minion interactions.\n" +
                        "_-_ Staffs no longer recharge, if respective minion is present.\n" +
                        "_-_ Soul Sparking effect is visible in descriptions.\n"+
                        "_-_ No more stray attunement usage!\n" +
                        "_-_ Added wet debuff, that does same thing as being in water. Inflicted by Storm Clouds and Aqua Blast.\n" +
                        "_-_ Added ergonomic inventory setting, that moves slots to bottom of screen.\n" +
                        "_-_ Added new attacks for Yog, buffed his summon attack to include more monsters."));

        changes = ChangesScene.createChangeInfo(changeInfos, "v1.2.0a & 1.2.0b", false, Window.WHITE);

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Debuff large icons being incorrect.\n\n" +
                        "_-_ Frost Elemental minion's animation were incorrect.\n\n"+
                        "_-_ Demon Halls was undescenable if Final Froggits were dead.\n\n" +
                        "_-_ Stationary minions displayed missing text when interacted with.\n\n\n" +
                        "_-_ Added failsafe for potential softlocks, which will give control to player if he gets stuck for too long.\n\n" +
                        "_-_ Reduced lag from Yog's eradicating wall attack."));
        changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_HALLS, 0, 64, 16, 16 ), "Dynamic Dungeon Size",
                "_-_ Reduced amount of rooms by 25-35% in every dungeon mode.\n\n" +
                        "_-_ Reduced amount of monsters in 21 floors mode.\n\n" +
                        "_-_ Fixed view distance in demon halls.\n\n" +
                        "_-_ Increased amount of mobs, but made it more consistent."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RATION, null), "Food",
                "_-_ Food satisfies for 10% more energy. Heroes take 10% more time to get hungry and starving.\n\n" +
                        "_-_ Food appears in every floor now instead of the chance."
        ));
    }

    public static void add_1_1_5_Changes(ArrayList<ChangeInfo> changeInfos) {

        ChangeInfo changes = new ChangeInfo("1.1.5", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = ChangesScene.createChangeInfo(changeInfos, "v1.1.5b", false, Window.WHITE);

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Fixed crash on Dwarf King.\n\n" +
                        "_-_ Removed ShPD support prompt."));

        changes = ChangesScene.createChangeInfo(changeInfos, "v1.1.5a", false, Window.WHITE);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.BOMB, null), "Bomb changes",
                "_-_ Explosion damage have been significantly increased across all game stages to be fatal for most of enemies in radius.\n\n" +
                        "_-_ Most of enchanced bombs now explode instantly.\n\n" +
                        "_-_ Added two new enchanced bomb: Supply Station (the stationary healing potion) and Webbomb (7x7 web spread, great stalling tool).\n\n" +
                        "_-_ Bombs are encountered in more places, also there is 1/15 chance to encounter enhanced bomb instead of regular one."
        ));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Fixed crash with Sad Ghost quest.\n" +
                        "_-_ Removed class unlocks. You still need to beat the game for challenges.\n" +
                        "_-_ Fixed critical issue with text on Android version.\n" +
                        "_-_ Fixed issue with beam sounds being unintentionally more loud."));


        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released November 2th, 2020\n" +
                        "_-_ 96 days after 1.1.4\n\n" +
                        "It was a long ago, isn't it?"));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.WARRIOR, 0, 15, 12, 15), "Warrior Rework!",
                "The big boy rework for sure.\n\n" +
                        "_-_ The new mechanic: special attacks! With spending a bit of satiety, Warrior can unleash unique attacks on enemies!\n" +
                        "Each weapon has one, so don't be afraid to experiment.\n\n" +
                        "_-_ Broken Seal now is used for weapons and enables ability to do the special attack with sealed weapon.\n\n" +
                        "_-_ Warrior lost all food related abilities, but his satiety deplets slower."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.WARRIOR, 0, 90, 12, 15), "Warrior Subclass Rework!",
                "Warrior have completely new subclasses!\n\n" +
                        "_Monk_ is replacement for Gladiator and promotes aggressive style with juggling various items during the run. Any usable item gives energy to Monk, which can be used for special attacks.\n\n" +
                        "_Robinson_ is replacement for Berserker and gives player a tactical way to use his specials. Slingshot can use special attacks as ranger ability, but Robinson needs more satiety to use attacks in melee."));

        changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Shattered PD Port",
                "_-_ Ported practically everything from Shattered PD 0.8.X.\n" +
                        "_-_ Ported QoL changes and bugfixes from Shattered PD 0.9.X.\n\n" +
                        "I want to wait for 0.9.1 before porting talents and other stuff."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RATION, null), "Food",
                "Food now is more powerful and every bug is fixed."
        ));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Floor crashes should be fixed with ShPD 0.8 port.\n" +
                        "_-_ Nerfed ExplodingTNT reference; now it uses bomb only once.\n" +
                        "_-_ Conjurer gets healed from food.\n\n" +
                        "_-_ As this is fairly big update, all saves before it have been deprecated. Sorry about that."));

    }


    public static void add_1_1_4_Changes(ArrayList<ChangeInfo> changeInfos) {

        ChangeInfo changes = new ChangeInfo("1.1.4.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released June 29th, 2020\n" +
                        "_-_ 99 days after 1.1.3\n\n" +
                        "YEEEEEEEEEE"));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RATION, null), "Reworked hunger",
                        "The hunger have been reworked.\n\n" +
                                "Instead of depleting at fixed pace, now it can be spent by different actions.\n" +
                                "Staying still spends a far less hunger than running or fighting monsters.\n\n" +
                                "Some food, like cooked meat, might give you regeneration effect. As result, natural regeneration rate have been significantly slowed.\n\n" +
                                "There will be more food types in future, but I want some feedback over mechanic in general.\n\n" +
                                "You can check your hunger in hero's info window."
            ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_DISINTEGRATION, null), "New wand",
                "Replaced Wand of Disintegration with Wand of Shadow Beams, that bounces off the cells, increasing vulnerability of hit targets."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Soul Wielder's buff crashing, when reloaded from save file.\n\n" +
                        "_-_ Soul refound from augmenting minions.\n\n"+
                        "_-_ Wand of Stars not saving shot from detonation.\n\n" +
                        "_-_ Arcane Bomb destroying level's in/out tiles."));
    }
public static void add_1_1_3_Changes(ArrayList<ChangeInfo> changeInfos) {

    ChangeInfo changes = new ChangeInfo("1.1.3", true, "");
    changes.hardlight(Window.TITLE_COLOR);
    changeInfos.add(changes);

    changes = new ChangeInfo("v1.1.3c", false, null);
    changes.hardlight( Window.TITLE_COLOR );
    changeInfos.add(changes);

    changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
            "Removed dedicated weapon and armor from shops, as it was too unstable. Instead, shops have additional Scroll of Identity and missile weapon stack."));

    changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null), "Wand Changes",
                    "_-_ Wand of Frost is reworked: nerfed damage from 5-7 (+2/+3) to 2-5 (+1/+2), but added utility in form of increasing freezing chance and Frostburn debuff.\n\n" +
                    "_-_ Wand of Living Earth is nerfed: decreased guardian's HP to 20 (+8), decreased his DR to 1-4 (+0/+0.25).\n\n" +
                    "_-_ Wand of Blast Wave is buffed: it doesn't directly damage hero at all."));

    changes = new ChangeInfo("v1.1.3b", false, null);
    changes.hardlight( Window.TITLE_COLOR );
    changeInfos.add(changes);

    changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
            "Removed gold condition for shop weapons."));

    changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null), "Wand Changes",
            "_-_ Wand of Magic Missile is buffed from 2-8 (+1/+2) damage to 4-6 (+2/+2) damage.\n\n" +
                    "_-_ Wand of Lightning is changed: nerfed damage from 5-10 (+1/+5) to 2-6 (+2/+2), but zaps into water will do double damage (4-12 (+4/+4)).\n\n" +
                    "_-_ Wand of Frost is changed: nerfed damage from 2-8 (+1/+5) to 5-7 (+2/+3), but increased chill duration.\n\n" +
                    "_-_ Wand of Living Earth is nerfed: increased guardian's HP to 30 (+15), but decreased his DR, damage and evasion.\n\n" +
                    "_-_ Wand of Stench is buffed: added Slowness effect for a blob."));

    changes = new ChangeInfo("v1.1.3a", false, null);
    changes.hardlight( Window.TITLE_COLOR );
    changeInfos.add(changes);

    changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
            "_-_ Going to shop level will not softlock game.\n\n" +
                    "_-_ Buffed crabs and sewer minibosses.\n\n" +
                    "_-_ Bosses will instantly rise hero's level instead of giving expeirence."));

    changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
    changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
            "_-_ Released March 22th, 2020\n" +
                    "_-_ 35 days after 1.1.2\n\n" +
                    "Global rework will come as soon as needed, enjoy this bugfix update."));
    changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
            "The goat boy recieved some buffs:\n\n" +
                    "_-_ Starting strength is raised from 9 to 10, and max HP scaling is increased from 3 to 4.\n\n" +
                    "_-_ Buffed knife's damage from 1-5 (+1/+1) to 1-9 (+1/+2), but removed strength bonus (nerf in late game).\n\n" +
                    "_-_ Buffed froggit's damage from 1-10 (+1/+2) to 4-10 (+2/+3), basically same damage with gray rat when leveled.\n\n" +
                    "_-_ Reworked Knight: now have to throw his knife to get soul from enemies, offensive ability is rebalanced, heal is buffed.\n\n" +
                    "_-_ Changed Soul Wielder: his empowerment lasts much longer."));
    changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
            "_-_ Buffed the Ring of Endurance\n\n" +
                    "_-_ Changed crabs to be slime-like enemies, should be less annoying for allies.\n\n" +
                    "_-_ Changed TNT mouse's retreat, less annoying now.\n\n" +
                    "_-_ Shop's equipment shouldn't feature curses\n\n" +
                    "_-_ Minions are now able to attack Yog.\n\n" +
                    "_-_ Piranhas and minions are neutral to each other."));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
            "Fixed:\n" +
                    "_-_ Lack of hero message, when eradicated.\n\n" +
                    "_-_ Shops crashing the game or oftenly not loading after all\n\n" +
                    "_-_ TNT mouses being able to crash the game\n\n" +
                    "_-_ Lack of wandmaker reaction for conjurer\n\n" +
                    "_-_ Cursed Wand of Crystal Bullets crashing the game and having wrong type of damage\n\n" +
                    "_-_ Wand of Stars spending charge on detonating\n\n" +
                    "_-_ Slingshot's stone crashing the game\n\n" +
                    "_-_ Cave Spinner being able to damage with web\n\n" +
                    "_-_ Frost bomb crashing the game on non-regular floors"));

}

    public static void add_1_1_2_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("1.1.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released February 16th, 2020\n" +
                        "_-_ 6 days after 1.1.1\n\n" +
                        "Sorry for such small updates, I will make more drastical changes with more feedback and better time management."));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
                "_-_ AoE healing should work properly."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.CRYSTAL_WAND, null), "Changes to WoCB",
                "Wand of Crystal Bullet have been patched to be more usable:\n\n" +
                        "_-_ Shards can't go to empty tiles and to player.\n\n" +
                        "_-_ Shards can catch moving enemies now."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.STAR_WAND, null), "New Wand",
                "Added the new wand: Wand of Stars:\n\n" +
                        "This wand shoots star mines, that can be exploded by zapping into them."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Lack of fizzle string for staves"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Gnoll hunter can gain distance better.\n\n" +
                        "_-_ Increased amount of craftable Stones of Targeting from 3 to 5.\n\n" +
                        "_-_ 1 soul strength's zap heals 2 hp.\n\n" +
                        "_-_ Added funny quotes for each wand."));
    }

public static void add_1_1_1_Changes(ArrayList<ChangeInfo> changeInfos) {
    ChangeInfo changes = new ChangeInfo("1.1.1", true, "");
    changes.hardlight(Window.TITLE_COLOR);
    changeInfos.add(changes);
    changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
    changes.addButton(new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
            "_-_ Released February 10th, 2020\n" +
                    "_-_ 2 days after 1.1.0"));
    changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
            "Have obtained the new ability: Soul Sparkling, that increases damage of your allies, when are heavily wounded."));
    changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.CRYSTAL_WAND, null), "New Wand",
            "Added the new wand: Wand of Crystal Bullet:\n\n" +
                    "This wand shoots splitting crystals, that can deal damage for several trajectories."));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
            "Fixed:\n" +
                    "_-_ Slingshot's stone crashing without slingshot\n" +
                    "_-_ Lacking string for cave spinner\n" +
                    "_-_ Crash for level 16\n" +
                    "_-_ Flashbang breaking the game, when pickuped after mouse's attack\n" +
                    "_-_ Spirit Bow being a not usable with sniper special and no augment"));
    changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
            "_-_ Replaced donation button with settings button.\n\n" +
                                    "_-_ Removed upgraded missiles from shops.\n\n" +
                                    "_-_ Adjusted attunement gain from level 10 to level 6.\n\n" +
                    "_-_ Edited sentry's summon prompt to be less confusing."));
}
    public static void add_1_1_Changes(ArrayList<ChangeInfo> changeInfos){
        ChangeInfo changes = new ChangeInfo( "1.1", true, "");
        changes.hardlight( Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton( new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released February 8th, 2020\n" +
                        "_-_ 99 days after 1.0.1a\n" +
                        "_-_ 101 days after 1.0.0"));
        changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
                "Have recieved a rework:\n\n" +
                        "_-_ Has a new melee weapon instead of using staff in melee: toy knife! It's usable to get all the soul and play with new subclass.\n\n" +
                        "_-_ Deltarune robe can't be upgraded, but gains levels from attunement.\n\n" +
                        "_-_ Now is one character with starting attunement.\n\n" +
                        "_-_ Froggit Staff have rebalanced to include decrease of start attunement.\n\n" +
                        "_-_ Have recieved redesigned mastery:\n\n\n" +
                        "   _-_ Soul Wielder: in exchange of melee damage his healing abilities can buff summons, and they can tweak minion's stats. Offensive action will form a controllable boosts for minions.\n\n" +
                        "   _-_ Knight: in exchange of healing ability recieves a soul blast spell, the damaging magic bolt that consumes soul energy, the healing spell, and can collect a soul on killing the enemies with toy knife."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GREY_RAT_STAFF, null), "Summon weapon rework",
                "This type of weapons also have recieved a rework:\n\n" +
                        "_-_ Summon weapons can't be equipped now, melee damage is also removed.\n\n" +
                        "_-_ Alongside with tiers, staves have assigned to 5 different classes: defense, melee, magic, range and support. This stat affects an effect of new category of items on the minions.\n\n" +
                        "_-_ A bunch of balance changes, accounting for different minions.\n\n" +
                        "_-_ A new minion have been added: the slime, that stuns enemies.\n\n" +
                        "_-_ Gnoll hunter have been moved to tier 3.\n\n" +
                        "_-_ Mimic staff have been removed from game.\n\n" +
                        "_-_ Charge mechanic of staves should be more stable that before.\n\n" +
                        "_-_ Minions go invisible on drinking an invisibility potion."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARCANE_BOMB, null), "Bomb rework",
                "Enchanced bombs have recieved a rework, making them a powerful AoE consumables. Most of them doesn't deal damage anymore.\n\n" +
                        "_-_ Frost Bomb now freezes every mob in room for 15 turns.\n\n" +
                        "_-_ Firebomb produces long-lasting field of fire.\n\n" +
                        "_-_ Flashbang forces enemies to lose memories about their targets.\n\n" +
                        "_-_ Woolly Bomb have been replaced by Shrinking Bomb, that decreases size and stats of every enemy in room.\n\n" +
                        "_-_ Noisemaker doesn't explode in proximity of monster, instead it alerts monsters for 20 turns, and then explodes with knockback.\n\n" +
                        "_-_ Shock Bomb have been redesigned into Electrical Explosive, the charging bomb, that electrocutes creatures in 3x3 area. This bomb can break after long using..\n\n" +
                        "_-_ Holy bombs have been redesigned into Holy Grenades, that can be used as small-AoE ranged weapon with medium damage.\n\n" +
                        "_-_ Arcane Bomb have been reworked into Arcane Nuke, which explode in 11x11 with tremendous damage, destroys obstacles and leaves a harmful cloud of miasma."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.POWER_HOLDER, null), "Power ups!",
                "5 different powers have added as ultimate spells. At cost of transumation scroll and other goodies you can make a very powerful item, that affects your enemies, minions and character.\n\n" +
                        "Each power up have different set of effects, so check for alchemy guide!"
        ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.STONE_HAMMER, null), "Shop weapons",
                "4 different weapons have been added into shops across all dungeon. They have unique properties and can be used for different synergies.\n\n" +
                        "That weapon can appear in shop with 50% chance."
                ));
        changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_HALLS, 0, 64, 16, 16 ), "Floor 21 rework!",
        "Floor 21 recieved a big changes, making them more memorable and valuable.\n\n" +
                "_-_ It's now full depth, featuring many rooms, not just shop.\n\n" +
                "_-_ It always contain 3 scrolls of passage, allowing you to rest after exploring a demon halls.\n\n" +
                "_-_ The imp shopkeeper have replaced by new mysterious merchant, that serves same purpose."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.DOG, 0, 0, 15, 15), "New mob and changes",
                "_-_ Added a new monster into the sewers: sewer dog. He is fast, dexterious and can be a problem. It drops random items with 10% chance.\n\n" +
                        "_-_ Crabs have been reworked into very tough enemy with low speed and heavy punch. You should dispatch that guys from range.\n\n" +
                        "_-_ Added a new monster into Demon Halls: inferno bat! It attacks you with fire, forcing you staying in inferno cloud.\n\n" +
                        "_-_ All Demon Halls enemies have buffed, partially because of depth 21 rework.\n\n" +
                        "_-_ Added a new monster into caves: TNT mouse! It will blind and explode you, while trying to keep distance. It can be quite devastating enemy, if you don't have any range.\n\n" +
                        "_-_ Cave Spinner have been reworked into support enemy, which usually doesn't fight in melee, but spits up a web trap around you every few turns.\n\n" +
                        "_-_ Added a new monster into Metropolis: armored vessel. He is durable, but not very powerful guard, but it releases two-three attunement spirits, which are immensely powerful, but doesn't attack first."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SWORD, new ItemSprite.Glowing( 0x660022 )), "Vampiric rework",
                "Vampiric enchantment have been reworked to be more viable with low-damage weapons and more consistent.\n\n" +
                        "_-_ Now proc always.\n\n" +
                        "_-_ Gives a heal in form of Healing buff, which scales with weapon's level and damage roll."
        ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_STENCH, null), "Wand of Stench rework",
                "Wand of Stench have been reworked to be more strategic and viable.\n\n" +
                        "_-_ Instead of infusing enemies, it creates a unique creature, that spreads the stench gas.\n\n" +
                        "_-_ This stench gas deals constant damage, that scales with wand's level.\n\n" +
                        "_-_ Stench creature have limited lifespan and explodes on collision with monsters or expiring."
        ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCIMITAR, null), "Scimitar rework",
                "Scimitar have been reworked to move from 'sword but faster' to charging hits.\n\n" +
                        "_-_ Damage have been reduced to 3-15 (+0.5/3).\n\n" +
                        "_-_ Every 4th strike a damage will be increased to 6-30 (+1/+6).\n\n"
        ));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "New challenge",
                "Swarm Intelligence have been replaced by more powerful challenge.\n\n" +
                        "_-_ All NPCs will completely diappear.\n\n" +
                        "_-_ Shops will be not accessible.\n\n" +
                        "_-_ Each floor have one additional mob, but mobs don't respawn at all.\n\n" +
                        "_-_ You can't achieve happy end, and there will be unique ending.\n\n"));

        changes.addButton( new ChangeButton(Icons.get(Icons.WARNING), "Crash report system",
                "Added a simple crash handler, that allows to capture stacktraces.\n\n" +
                                        "This feature will be disabled for Google Play version."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Pickaxes are melee weapons now, that can be enchanted or upgraded.\n\n" +
                                        "_-_ Fixed bug, when Imp Queen's minions can crash a game.\n\n" +
                                        "_-_ Final Froggits's level seal were working incorrectly.\n\n" +
                                        "_-_ Fixed sneaky weapons.\n\n" +
                                        "_-_ Fixed dark slime's splits, less annoying now.\n\n" +
                                        "_-_ Fixed bugs with Perfume Brew.\n\n" +
                                        "_-_ Fixed clean water being not identified and having no readable drink option.\n\n" +
                                        "_-_ Changed sprites of some bombs.\n\n" +
                                        "_-_ Removed Russian and Chinese, because I can't support it properly.\n\n" +
                                        "_-_ Added a altar locked room, which contain cursed staff that can be upgraded.\n\n" +
                                        "_-_ Reworked sniper's shot from accelerating arrow to piercing javelin.\n\n" +
                                        "_-_ Changed descriptions of some wands to be more detailed."));
    }

public static void add_1_0_1_Changes(ArrayList<ChangeInfo> changeInfos ) {
    ChangeInfo changes = new ChangeInfo( "1.0.1", true, "");
    changes.hardlight( Window.TITLE_COLOR);
    changeInfos.add(changes);
    changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
    changes.addButton( new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
            "_-_ Released November 2nd, 2019\n" +
                    "_-_ 2 days after 1.0.0"));
    changes = ChangesScene.createChangeInfo(changeInfos, "Changes", false, Window.SHPX_COLOR);
    changes.addButton( new ChangeButton(new Gold(),
            "_-_ Adjusted gold drops from enemies and floor\n" +
                    "_-_ Increased cost of most items\n" +
                    "_-_ Gold Token is sellable now"));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
               "_-_ Slingshot's stone didn't saved properly\n" +
                       "_-_ Blasted enemies were able to levitate\n" +
                       "_-_ New statistics number weren't saved properly\n" +
                       "_-_ Sneaky missiles didn't get consumed even if player can't surprise attack\n" +
                       "_-_ Badges about completing the game didn't accounted for Conjurer"));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.GHOST, 0, 0, 14, 15), "Sad Ghost quest",
            "_-_ Now shows rewards names\n" +
                    "_-_ Now can give a staff\n"));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.KEEPER, 0, 0, 14, 14), "Shops",
            "_-_ Adjusted shop's equipment choice to account hero's strength\n" +
                    "_-_ Guaranted wand and ring are always uncursed and upgraded; wand is also always have damage stat, so you can have weapon for Dark Matter Slimes\n" +
                    "_-_ You can find +10 artifact in imp shop"));
    changes.addButton( new ChangeButton(new CleanWater(),
            "Added a mineral water. It heals full HP, but is pretty expensive. Can be found only in shops. Very useful for Pharmacophobia challenge."));
    changes.addButton( new ChangeButton(new WandOfStench(),
            "Added a Wand of Stench. It's imbue the target's with toxic energy, allowing them to release damaging gas. Essentially it's more tricky, but more effective variant of Wand of Corrosion."));
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.CONJURER, 0, 90, 12, 15), "Conjurer",
            "_-_ New avatar sprite for conjurer\n" +
                    "_-_ Energy storm now takes 25% current HP and 12.5% max HP"));
    changes.addButton( new ChangeButton(Icons.get(Icons.LANGS), Messages.get(ChangesScene.class, "language"),
            "Improved the English translation"));
    changes = ChangesScene.createChangeInfo(changeInfos, "1.0.1a", false, CharSprite.DEFAULT);
    changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
            "Fixed infinity loop in Sad Ghost' quest, which prevented you from descending to new sewers depths"));
	}

    public static void add_General_Changes(ArrayList<ChangeInfo> changeInfos ){
	    ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Dev", false, Window.TITLE_COLOR);
        changes.addButton( new ChangeButton(Icons.get(Icons.TRASHBOXBOBYLEV), "Developer Information",
                "_-_ Released October 31nd, 2019\n" +
                        "_-_ 55 days after beginning of development\n" +
                        "_-_ 105 days after Shattered 0.7.4"));
        changes = ChangesScene.createChangeInfo(changeInfos, "General", false, Window.SHPX_COLOR);
        changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 6), "New Class!",
                "Asriel Dreemur joins the dungeon crawling!\n\n" +
                        "The Conjurer - new class, that are focused on new type of weapons - summon weapons. Thanks to his great soul power, here are able to control more allies and support them by his unique equipment.\n\n"+
                        "Unfortunately, Conjurer's body is composed from dust, so he have lowered physical stats compared to other classes.\n\n" +
                        "Conjurer have two subclasses:\n" +
                        "_-_ Soul Reaver gain 2x more soul and can tune minion stats. His offensive ability consumes soul for damaging enemies.\n" +
                        "_-_ Occultist collects HATE on killing enemies and spend them on next attack to corrupt the enemy. His offensive ability uses precise amount of HATE to corrupt enemies."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GREY_RAT_STAFF, null), "Summon Weapons!",
                "The new type of weapons have been added into dungeon.\n\n" +
                        "_-_ Summon weapons can be used to summon the unique allies, which type depends on type's staff.\n\n" +
                        "_-_ As all weapons, summon weapons are splitted into 5 tiers, with more unique and powerful minions, that can carry enchantment and level of weapon, as you progress.\n" +
                        "_-_ Summon weapons can summon unlimited smount of creatures, but you can control only few."));
        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 112, 32, 16, 16), "Attunement",
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
                        "_-_ After shooting, runic blade need to recharge for some time, that time equals 40 usings of weapon"));

    }

    public static void add_Mobs_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Mobs", false, 0xffc511);
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SNAKE, 0, 0, 12, 12), "New monster in Caves",
                "The Caves now have new inhabitant: Rattle Snakes!\n\n"+
                        "_-_ They shoot the deadly darts on range, but are weak in melee.\n"+
                        "_-_ They have resistant to most of controllable magical attacks.\n" +
                        "_-_ Shakes have very high evasion, but low HP.\n"+
                        "_-_ They drop a darts for crossbows."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.NECRO, 0, 0, 16, 16), "Necromancer",
                "Added the necromancer from Shattered 0.7.5. Their stats are buffed compared to Shattered's.\n\n"+
                        "_-_ They have more HP now.\n\n"+
                        "_-_ They buff their skeleton both with Adrenaline and Empowered buffs.\n"+
                        "_-_ Drop: random staff with 12.5% chance."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.TENGU, 0, 0, 14, 16), "New Tengu",
                "Added Tengu fight from Shattered 0.7.5. Nothing are changed."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SLIME, 0, 0, 14, 12), "New monster in Demon Halls",
                "The Demon Halls now have new inhabitant: Dark Matter Slimes!\n\n"+
                        "_-_ Their stats are pretty low for stage, but...\n"+
                        "_-_ Slimes can split! While splitting, dark matter slimes fully heal, but lose 25% of max HP.\n" +
                        "_-_ Damage wands are 2.5x more effective against slimes.\n"+
                        "_-_ They rarely drop a goo blobs."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.FINAL_FROGGIT, 0, 0, 16, 16), "Final Froggits",
                "Prepare for exit stairs defenders in Demon Halls - Final Froggits!\n\n"+
                        "_-_ They shoot a eradication bolts, that have small damage.\n"+
                        "_-_ But that bolts inflict Eradication debuff.\n" +
                        "_-_ Eradication debuff exponentially increases Final Froggit's damage, so do not stay for too long around them.\n"+
                        "_-_ They drop random items."));
    }

    public static void add_Minor_Changes(ArrayList<ChangeInfo> changeInfos){
        ChangeInfo changes = ChangesScene.createChangeInfo(changeInfos, "Other", false, 0x651f66);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GOLD, null), "Gold balance changes",
                "_-_ Gold appear a slighty rarer, but in a lot more quantity.\n\n"+
                                        "_-_ The shops have been extended to hold more valuable items, like upgraded items or exotic consumables.\n" +
                                        "_-_ If you do not have good equipment, shops will help by providing a better weapon and armor.\n"+
                                        "_-_ You can farm gold in Prison by killing the thieves.\n" +
                                        "_-_ You can farm gold in City by collecting the tokens."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), "Healing item changes",
                "Potion of Healing and Elixir of Honeyed Healing now create the clouds of healing gases on shattering.\n\n" +
                        "When PoH cloud heal anything, EoHH are more concentrated and heal only allies."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SWORD, new ItemSprite.Glowing()), "Unstable enchantment",
                "Now have rainbow shining as glowing effect."));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.KUNAI, null), "Sneaky weapon changes",
                "Throwing sneaky weapons now doesn't consume a durability, while used with sneak attack"));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered 0.7.5",
                                        "Added the new camera panning from new Shattered."));
        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 64, 16, 16, 16), "Resistance",
                "If monsters are resisting the effect, effect's damage now square-rooted rather that halfing."));
        changes.addButton( new ChangeButton(Icons.get(Icons.RANKINGS), "Rankings",
                "Completely changed the way, how game places run in rankings scene.\n\nMore info is on ranking scene."));
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
