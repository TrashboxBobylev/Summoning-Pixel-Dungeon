/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.Random;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Bestiary {
	
	public static ArrayList<Class<? extends Mob>> getMobRotation( int depth ){
		ArrayList<Class<? extends Mob>> mobs = standardMobRotation( depth );
		addRareMobs(depth, mobs);
		swapMobAlts(mobs);
		Random.shuffle(mobs);

		//remove mobs, that don't give anything
		for (int i = 0; i < mobs.size(); i++){
		    try {
                Mob mob = mobs.get(i).newInstance();
                if (Dungeon.hero.lvl > mob.maxLvl + 2) mobs.remove(i);
            } catch (Throwable e){
                ShatteredPixelDungeon.reportException(e);
            }
        }

		return mobs;
	}
	
	//returns a rotation of standard mobs, unshuffled.
	private static ArrayList<Class<? extends Mob>> standardMobRotation( int depth ){
		switch(depth){
			
			// Sewers
			case 1: default:
				//3x rat, 1x snake
				return new ArrayList<>(Arrays.asList(
                         Rat.class, Rat.class, Rat.class,
                        Snake.class));
			case 2:
				//2x rat, 3x gnoll, 2x dogs
				return new ArrayList<>(Arrays.asList(Rat.class, Rat.class,
						Gnoll.class, Gnoll.class, Gnoll.class, Dog.class, Dog.class));
			case 3:
				//1x rat, 3x gnoll, 2x crab, 1x swarm, 1x dogs
				return new ArrayList<>(Arrays.asList(Rat.class,
						Gnoll.class, Gnoll.class, Gnoll.class, Crab.class,
						Crab.class, Swarm.class, Dog.class));
			case 4: case 5:
				//2x gnoll, 2x crab, 2x swarm, 2x dogs
				return new ArrayList<>(Arrays.asList(
						Gnoll.class, Gnoll.class,
						Crab.class, Crab.class, Swarm.class,
						Swarm.class, Dog.class, Dog.class));
				
			// Prison
			case 6:
				//3x skeleton, 1x thief, 1x swarm
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
						Thief.class,
						Swarm.class));
			case 7:
				//3x skeleton, 1x thief, 1x DM-100, 1x guard
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
						Thief.class,
						DM100.class,
						Guard.class));
			case 8:
				//2x skeleton, 1x thief, 2x DM-100, 1x guard, 1x necromancer
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
						Thief.class,
						DM100.class, DM100.class,
						Guard.class, Necromancer.class,
						Necromancer.class));
			case 9: case 10:
				//1x skeleton, 1x thief, 2x DM-100, 2x guard, 2x necromancer
				return new ArrayList<>(Arrays.asList(Skeleton.class,
						Thief.class,
						DM100.class, DM100.class,
						Guard.class, Guard.class,
						Necromancer.class, Necromancer.class));
				
			// Caves
			case 11:
				//5x bat, 1x brute
				return new ArrayList<>(Arrays.asList(
						Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
						Shaman.random(), Snake.class));
			case 12:
				//2x bat, 2x brute, 1x shaman, 1x spinner
				return new ArrayList<>(Arrays.asList(
						Bat.class, Bat.class,
						Brute.class, Brute.class,
						Shaman.random(),
						Spinner.class));
			case 13:
				//1x bat, 3x brute, 1x shaman, 1x spinner
				return new ArrayList<>(Arrays.asList(
						Bat.class,
						Brute.class, Snake.class,
						Snake.class, Shaman.random(),
						Spinner.class, Spinner.class,
						DM200.class, ExplodingTNT.class));
			case 14: case 15:
				//1x bat, 1x brute, 2x shaman, 2x spinner, 2x DM-300
				return new ArrayList<>(Arrays.asList(
						Bat.class,
						Brute.class,
						Shaman.random(), Shaman.random(), ExplodingTNT.class,
						Spinner.class, Spinner.class,
						DM200.class, DM200.class, Snake.class, Snake.class));
				
			// City
			case 16:
				//2x ghoul, 2x elemental, 1x warlock
				return new ArrayList<>(Arrays.asList(
						Ghoul.class, Ghoul.class,
						Elemental.random(), Elemental.random(),
						Warlock.class));
			case 17:
				//1x ghoul, 2x elemental, 1x warlock, 1x monk
				return new ArrayList<>(Arrays.asList(
						Ghoul.class,
						Elemental.random(), Elemental.random(),
						Warlock.class,
						Monk.class));
			case 18:
				//1x ghoul, 1x elemental, 2x warlock, 2x monk, 1x golem, 1x dwarf guard
				return new ArrayList<>(Arrays.asList(
						Ghoul.class,
						Elemental.random(),
						Warlock.class, Warlock.class,
						Monk.class, Monk.class,
						Golem.class, DwarfGuardMob.class));
			case 19: case 20:
				//1x elemental, 1x warlock, 2x monk, 3x golem
				return new ArrayList<>(Arrays.asList(
						Elemental.random(),
						Warlock.class, Warlock.class,
						Monk.class, Monk.class,
						Golem.class, Golem.class, DwarfGuardMob.class, DwarfGuardMob.class));
				
			// Halls
			case 21: case 22:
				//2x succubus, 2x evil eye, 2x slime
				return new ArrayList<>(Arrays.asList(
						Succubus.class, Succubus.class, Slime.class,
						Eye.class, Eye.class, Slime.class));
			case 23:
				//1x succubus, 3x evil eye, 2x scorpio, 2x slime
				return new ArrayList<>(Arrays.asList(
						Succubus.class, Slime.class,
						Eye.class, Eye.class, Eye.class, Slime.class,
						Scorpio.class, Scorpio.class, HellBat.class));
			case 24: case 25: case 26:
				//2x evil eye, 3x scorpio, 1x slime
				return new ArrayList<>(Arrays.asList(
						Slime.class,
						Eye.class, Eye.class,
						Scorpio.class, Scorpio.class, Scorpio.class, HellBat.class, HellBat.class));
		}
		
	}
	
	//has a chance to add a rarely spawned mobs to the rotation
	public static void addRareMobs( int depth, ArrayList<Class<?extends Mob>> rotation ){
		
		switch (depth){
			
			// Sewers
			default:
				return;
			case 4:
				if (Random.Float() < 0.025f) rotation.add(Thief.class);
				return;
				
			// Prison
			case 9:
				if (Random.Float() < 0.025f) rotation.add(Bat.class);
				return;
				
			// Caves
			case 14:
				if (Random.Float() < 0.025f) rotation.add(Ghoul.class);
				return;
				
			// City
			case 19:
				if (Random.Float() < 0.025f) rotation.add(Succubus.class);
				return;
		}
	}
	
	//switches out regular mobs for their alt versions when appropriate
	private static void swapMobAlts(ArrayList<Class<?extends Mob>> rotation){
		for (int i = 0; i < rotation.size(); i++){
			if (Random.Int( 50 ) == 0) {
				Class<? extends Mob> cl = rotation.get(i);
				if (cl == Rat.class) {
					cl = Albino.class;
				} else if (cl == Thief.class) {
					cl = Bandit.class;
				} else if (cl == Brute.class) {
					cl = ArmoredBrute.class;
				} else if (cl == DM200.class) {
					cl = DM201.class;
				} else if (cl == Monk.class) {
					cl = Senior.class;
				} else if (cl == Scorpio.class) {
					cl = Acidic.class;
				}
				rotation.set(i, cl);
			}
		}
	}
}
