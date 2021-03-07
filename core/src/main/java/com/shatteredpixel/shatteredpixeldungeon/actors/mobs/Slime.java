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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Slime extends Mob {

	{
		spriteClass = SlimeSprite.class;
		
		HP = HT = 80;
		defenseSkill = 23;

		EXP = 12;
		maxLvl = 25;
		if (SPDSettings.bigdungeon()){
			EXP = 28;
			maxLvl = 46;
		}

		loot = new GooBlob();
		lootChance = 0.1f;
		properties.add(Property.ACIDIC);
		properties.add(Property.DEMONIC);
		properties.add(Property.INORGANIC);
	}

	public Slime() {
		if (SPDSettings.bigdungeon()){
			EXP = 28;
			maxLvl = 46;
		}
	}

	private static final float SPLIT_DELAY	= 1f;
	
	int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(18, 23);
	}

    @Override
    public void damage(int dmg, Object src) {
        for (Class c : RingOfElements.RESISTS){
            if (c.isAssignableFrom(src.getClass()) && src instanceof Wand){
                dmg *= 2.5f;
            }
        }
        super.damage(dmg, src);
    }

    @Override
	public int defenseProc( Char enemy, int damage ) {

		if (HP >= damage + 2) {
			ArrayList<Integer> candidates = new ArrayList<>();
			boolean[] solid = Dungeon.level.passable;
			
			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (solid[n] && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}
	
			if (candidates.size() > 0) {
				
				Slime clone = split();
                HP = HT;
				HT *= 0.8f;
				clone.HT = clone.HP = HT;
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;
				Dungeon.level.pressCell(clone.pos);
				
				if (Dungeon.level.map[clone.pos] == Terrain.DOOR) {
					Door.enter( clone.pos );
				}
				
				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );

				HP = HT;
			}
		}
		
		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 38;
	}
	
	private Slime split() {
		Slime clone = new Slime();
		clone.generation = generation + 1;
		clone.EXP = 0;
		if (buff( Burning.class ) != null) {
			Buff.affect( clone, Burning.class ).reignite( clone );
		}
		if (buff( Poison.class ) != null) {
			Buff.affect( clone, Poison.class ).set(2);
		}
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}
}
