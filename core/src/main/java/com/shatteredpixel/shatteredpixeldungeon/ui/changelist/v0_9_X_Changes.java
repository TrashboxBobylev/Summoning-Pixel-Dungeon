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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class v0_9_X_Changes {

    public static void add_v0_9_0_Changes( ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("v0.9.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.9.0b", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "I'm making some adjustments to sewers loot to give players a bit more control of what gear they get, and to reduce the chance of spawning high tier gear that the player may never get to use:\n" +
                        "_-_ Statues are now killed if a disarming trap triggers under them.\n" +
                        "_-_ Weak shields no longer override stronger ones.\n" +
                        "_-_ Updated translations and translator credits."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                        "Fixed (existed prior to v0.9.0):\n" +
                        "_-_ Tengu's abilities being reset by saving/loading\n" +
                        "_-_ Various cases where game win badges would not appear\n" +
                        "_-_ Force cubes trigger traps before being placed to the floor\n" +
                        "_-_ Beacon of returning rarely teleporting the player into walls\n" +
                        "_-_ Enemies rarely not appearing paralyzed when they are"));

        changes = new ChangeInfo("v0.9.0a", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Updated Translations"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed (caused by v0.9.0):\n" +
                        "_-_ Various crash bugs\n" +
                        "_-_ Incorrect interactions between scrolls and time freeze\n" +
                        "_-_ Unlocked badges rarely showing as locked\n" +
                        "_-_ Burning spider webs destroying some tile types that they shouldn't\n\n" +
                        "Fixed (existed prior to v0.9.0):\n" +
                        "_-_ Cleave being reset when a kill corrupts the enemy\n" +
                        "_-_ Sleeping VFX persisting in cases where it shouldn't"));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released October 5th, 2020\n" +
                        "_-_ 61 days after Shattered v0.8.2\n" +
                        "_-_ 173 days after Shattered v0.8.0\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton(new ChangeButton(Icons.get(Icons.BADGES), "Badge Visuals",
                "The badges screen now shows which badges are locked, rather than just using a generic 'locked badge' visual.\n\n" +
                        "Badges now have different border colors based on their difficulty (bronze, silver, gold, platinum, diamond), and are ordered based on these colors."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), "Existing Challenges",
                "Some existing challenges have been tweaked to reduce the number of items that they remove from the game:\n\n" +
                        "_On Diet_ no longer restricts food, but instead causes all food to be 1/3 as effective at satiating hunger.\n\n" +
                        "_Faith Is My Armor_ no longer restricts the hero to cloth armor, but instead heavily reduces the blocking power of all armor above cloth.\n\n" +
                        "_Pharmacophobia_ no longer removes health potions, but instead makes them poisonous to the player."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                        "_-_ Spider webs are now flammable, and can be shot through by fireblast.\n" +
                        "_-_ The reclaim trap spell can no longer be dropped when a trap is stored in it. This prevents an exploit.\n" +
                        "_-_ Items gained from secret mazes are now known to be uncursed."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Various visual errors\n" +
                        "_-_ Various rare crash bugs\n" +
                        "_-_ Various rare game freeze bugs\n" +
                        "_-_ Back button closing the game in hero select\n" +
                        "_-_ Issues with touch input on Android 11 when gestures are enabled\n" +
                        "_-_ Crystal mimics escaping when they are still visible\n" +
                        "_-_ Shadows buff being cancelled by enemies seen via mind vision\n" +
                        "_-_ Aqua blast occasionally not stunning\n" +
                        "_-_ Errors with turn spending when talisman is used\n" +
                        "_-_ Newborn elemental not dropping its quest item for overlevelled heroes\n" +
                        "_-_ Spinners shooting webs though walls\n" +
                        "_-_ Elastic enchantment closing doors when used with spirit bow\n" +
                        "_-_ Duplicate artifacts in rare cases\n" +
                        "_-_ Custom names not applying to Mage's staff\n" +
                        "_-_ Ring of might not reducing max HP when degraded\n" +
                        "_-_ Rare bugs involving ripper demon leaping\n" +
                        "_-_ Hero unable to cleanse fire with chill when immune to it, and vice-versa\n" +
                        "_-_ DM-201's attacking while stunned"));
    }
}

