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

package com.trashboxbobylev.summoningpixeldungeon.items.spells;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Paralysis;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.effects.Splash;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.trashboxbobylev.summoningpixeldungeon.levels.Level;
import com.trashboxbobylev.summoningpixeldungeon.levels.Terrain;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class AquaBlast extends TargetedSpell {
	
	{
		image = ItemSpriteSheet.AQUA_BLAST;
	}
	
	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		int cell = bolt.collisionPos;
		
		Splash.at(cell, 0x00AAFF, 10);
		
		for (int i : PathFinder.NEIGHBOURS9){
			if (i == 0 || Random.Int(5) != 0){
				int terr = Dungeon.level.map[cell + i];
				if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
						terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
						terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS ||
						terr == Terrain.EMPTY_DECO) {
					Level.set(cell + i, Terrain.WATER);
					GameScene.updateMap(cell + i);
				}
			}
		}
		
		Char target = Actor.findChar(cell);
		
		if (target != null && target != hero){
			//just enough to skip their current turn
			Buff.affect(target, Paralysis.class, 0f);
		}
	}
	
	@Override
	public int price() {
		//prices of ingredients, divided by output quantity
		return com.trashboxbobylev.summoningpixeldungeon.items.Recipe.calculatePrice(new Recipe()) * quantity;
	}
	
	public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfStormClouds.class, ArcaneCatalyst.class};
			inQuantity = new int[]{1, 1};
			
			cost = 4;
			
			output = AquaBlast.class;
			outQuantity = 12;
		}
		
	}
}
