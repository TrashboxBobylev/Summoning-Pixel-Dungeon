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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Broadsword extends MeleeWeapon {

	{
		image = ItemSpriteSheet.BROADSWORD;

		tier = 6;
		
		bones = false;

		DLY = 0.8f;
		ACC = 1.2f;

	}

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing( 0xFFFF00, 4f );
    }

    @Override
    public int STRReq() {
        return Dungeon.hero.STR();
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq();
    }

    @Override
    public boolean collect() {
	    identify();
        return super.collect();
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }



    @Override
    public int min(int lvl) {
        return 7;
    }

    @Override
    public int max(int lvl) {
        return 30;
    }

    @Override
    public int level() {
        return 6;
    }

    @Override
    public int visiblyUpgraded() {
        return 0;
    }

    @Override
    public int defenseFactor( Char owner ) {
        return 4;	//4 extra defence
    }

    public int strikes;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("strikes", strikes);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        strikes = bundle.getInt("strikes");
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (++strikes == 3) {
            damage *= 3.5f;
            defender.sprite.showStatus(CharSprite.WARNING, "crit!");
            strikes = 0;
        }
        new Unstable().proc(this, attacker, defender, damage);
        new Unstable().proc(this, attacker, defender, damage);
        new Unstable().proc(this, attacker, defender, damage);
        return super.proc(attacker, defender, damage);
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{WornShortsword.class, ArmorKit.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = Broadsword.class;
            outQuantity = 1;
        }

    }
}
