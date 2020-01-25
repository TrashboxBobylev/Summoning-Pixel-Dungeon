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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.items.ArmorKit;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRage;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.ArcaneCatalyst;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.Enrage;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Unstable;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
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
        return true;
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
        return 20;
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
            damage *= 3;
            defender.sprite.showStatus(CharSprite.WARNING, "crit!");
            strikes = 0;
        }
        new Unstable().proc(this, attacker, defender, damage);
        new Unstable().proc(this, attacker, defender, damage);
        new Unstable().proc(this, attacker, defender, damage);
        return super.proc(attacker, defender, damage);
    }

    public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{WornShortsword.class, ArmorKit.class};
            inQuantity = new int[]{1, 1};

            cost = 16;

            output = Broadsword.class;
            outQuantity = 1;
        }

    }
}
