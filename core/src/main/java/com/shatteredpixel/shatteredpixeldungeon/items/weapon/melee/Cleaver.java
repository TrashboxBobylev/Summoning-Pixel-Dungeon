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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Cleaver extends MeleeWeapon {

	{
		image = ItemSpriteSheet.CLEAVER;

		tier = 2;
		ACC = 0.66f; //0.66x accuracy
		//also cannot surprise attack, see Hero.canSurpriseAttack
	}

    @Override
    public int min(int lvl) {
        return tier - 1 + lvl / 2;
    }

    @Override
	public int max(int lvl) {
		return  Math.round(5*(tier)) +        //10 base, up from 15
				lvl*3;
	}

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            int level = Math.max( 0, level() );
            int dmg = super.damageRoll(owner);
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            int enemyHealth = enemy.HP - dmg;
            if (enemyHealth <= 0) return dmg; //no point in proccing if they're already dead.

            //scales from 0 - 40% based on how low hp the enemy is, plus 5% per level
            float maxChance = 0.4f + .05f*level;
            float chanceMulti = (float)Math.pow( ((enemy.HT - enemyHealth) / (float)enemy.HT), 2);
            float chance = maxChance * chanceMulti;
            if (enemy instanceof Mob && Random.Float() < chance && !(enemy.isImmune(Grim.class))) {
                hero.spendAndNext(Actor.TICK*2);
                enemy.oneShottedByCleaver = true;
                if (!enemy.isResist(Grim.class)) return enemy.HT + dmg;
                else return Math.round(dmg*2.5f);
            }
        }
        return super.damageRoll(owner);
    }

    @Override
    public int warriorAttack(int damage, Char enemy) {
	    damage = new Grim().proc(this, Dungeon.hero, enemy, damage);
	    damage = new Grim().proc(this, Dungeon.hero, enemy, damage);
        return 0;
    }
}
