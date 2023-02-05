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

package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ArcaneCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.noosa.Visual;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

//the class for ring of wealth's helper methods, as the ring is removed from the code
public class Wealth {
    //used for visuals
    // 1/2/3 used for low/mid/high tier consumables
    // 3 used for +0-1 equips, 4 used for +2 or higher equips
    private static int latestDropTier = 0;

    public static void showFlareForBonusDrop(Visual vis ){
        if (vis == null || vis.parent == null) return;
        switch (latestDropTier){
            default:
                break; //do nothing
            case 1:
                new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
                break;
            case 2:
                new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
                break;
            case 3:
                new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
                break;
            case 4:
                new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
                break;
        }
        latestDropTier = 0;
    }

    public static Item genConsumableDrop(int level) {
        float roll = Random.Float();
        //60% chance - 4% per level. Starting from +15: 0%
        if (roll < (0.6f - 0.04f * level)) {
            latestDropTier = 1;
            return genLowValueConsumable();
        //30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
        } else if (roll < (0.9f - 0.02f * level)) {
            latestDropTier = 2;
            return genMidValueConsumable();
        //10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
        } else {
            latestDropTier = 3;
            return genHighValueConsumable();
        }
    }

    private static Item genLowValueConsumable(){
        switch (Random.Int(4)){
            case 0: default:
                Item i = new Gold().random();
                return i.quantity(i.quantity()/2);
            case 1:
                return Generator.random(Generator.Category.STONE);
            case 2:
                return Generator.random(Generator.Category.POTION);
            case 3:
                return Generator.random(Generator.Category.SCROLL);
        }
    }

    private static Item genMidValueConsumable(){
        switch (Random.Int(6)){
            case 0: default:
                Item i = genLowValueConsumable();
                return i.quantity(i.quantity()*2);
            case 1:
                i = Generator.randomUsingDefaults(Generator.Category.POTION);
                return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
            case 2:
                i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
            case 3:
                return Random.Int(2) == 0 ? new ArcaneCatalyst() : new AlchemicalCatalyst();
            case 4:
                return new Bomb();
            case 5:
                return new Honeypot();
        }
    }

    private static Item genHighValueConsumable(){
        switch (Random.Int(4)){
            case 0: default:
                Item i = genMidValueConsumable();
                if (i instanceof Bomb){
                    return new Bomb.DoubleBomb();
                } else {
                    return i.quantity(i.quantity()*2);
                }
            case 1:
                return new StoneOfEnchantment();
            case 2:
                return new PotionOfExperience();
            case 3:
                return new ScrollOfTransmutation();
        }
    }

    public static Item genEquipmentDrop(int level){
        Item result;
        //each upgrade increases depth used for calculating drops by 1
        int floorset = (Dungeon.depth* 5 / Dungeon.chapterSize() + level)/5;
        switch (Random.Int(5)){
            default: case 0: case 1:
                Weapon w = Generator.randomWeapon(floorset);
                if (!w.hasGoodEnchant() && Random.Int(10) < level)      w.enchant();
                else if (w.hasCurseEnchant())                           w.enchant(null);
                result = w;
                break;
            case 2:
                Armor a = Generator.randomArmor(floorset);
                if (!a.hasGoodGlyph() && Random.Int(10) < level)        a.inscribe();
                else if (a.hasCurseGlyph())                             a.inscribe(null);
                result = a;
                break;
            case 3:
                result = Generator.random(Generator.Category.WAND);
                break;
            case 4:
                result = Generator.random(Generator.Category.ARTIFACT);
                break;
        }
        //minimum level is 1/2/3/4/5/6 when ring level is 1/3/6/10/15/21
        if (result.isUpgradable()){
            int minLevel = (int)Math.floor((Math.sqrt(8*level + 1)-1)/2f);
            if (result.level() < minLevel){
                result.level(minLevel);
            }
        }
        result.cursed = false;
        result.cursedKnown = true;
        if (result.level() >= 2) {
            latestDropTier = 4;
        } else {
            latestDropTier = 3;
        }
        return result;
    }
}
