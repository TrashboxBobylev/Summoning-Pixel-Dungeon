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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.Random;

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

		if (SPDSettings.smalldungeon()){
			switch(depth){

				// Sewers
				case 1:
					return new ArrayList<>(Arrays.asList(
							AbyssalNightmare.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class));
				case 2:
					//4x rat, 3x gnoll, 1x crab, 1x dog, 1x swarm
					return new ArrayList<>(Arrays.asList(Rat.class, Rat.class, Rat.class, Rat.class,
							Gnoll.class, Gnoll.class, Gnoll.class, Crab.class, Dog.class, Swarm.class));
				case 3: case 4:
					//1x rat, 5x gnoll, 2x crab, 3x swarm, 1x dog
					return new ArrayList<>(Arrays.asList(Rat.class,
							Gnoll.class, Gnoll.class, Gnoll.class, Crab.class, Gnoll.class, Gnoll.class, Swarm.class, Swarm.class,
							Crab.class, Swarm.class, Dog.class));

				// Prison
				case 5:
					//2x skeleton, 2x thief, 3x swarm
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
							Thief.class, Thief.class,
							Swarm.class, Swarm.class, Swarm.class));
				case 6:
					//1x skeleton, 3x thief, 2x robot, 1x guard
					return new ArrayList<>(Arrays.asList(Skeleton.class,
							Thief.class, Thief.class, Thief.class,
							DM100.class, DM100.class,
							Guard.class));
				case 7: case 8:
					//4x thief, 4x robot, 2x guard, 1x necro
					return new ArrayList<>(Arrays.asList(
							Thief.class, Thief.class, Thief.class, Thief.class,
							DM100.class, DM100.class, DM100.class, DM100.class,
							Guard.class, Guard.class,
							Necromancer.class));

				// Caves
				case 9:
					//7x bat, 1x shaman
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
							Shaman.random()));
				case 10:
					//4x bat, 2x brute, 1x shaman, 1x snake
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class, Bat.class, Bat.class, Snake.class,
							Brute.class, Brute.class,
							Shaman.random()));
				case 11: case 12:
					//6x bat, 3x brute, 2x dm-200, 1x spinner, 1x shaman, 1x tnt, 1x spinner
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
							Brute.class, Brute.class, Brute.class, DM200.class, DM200.class,
							Shaman.random(), ExplodingTNT.class,
							Spinner.class,
							DM200.class));

				// City
				case 13:
					//5x ghoul, 2x elemental, 1x warlock
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class,
							Elemental.random(), Elemental.random(),
							Warlock.class));
				case 14:
					//5x ghoul, 2x elemental, 1x warlock, 1x monk
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class,
							Elemental.random(), Elemental.random(),
							Warlock.class,
							Monk.class));
				case 15: case 16:
					//4x elemental, 1x monk, 1x golem, 1x guard
					return new ArrayList<>(Arrays.asList(
							Elemental.random(), Elemental.random(), Elemental.random(), Elemental.random(),
							Monk.class,
							Golem.class, DwarfGuardMob.class));

				// Halls
				case 17:
					//2x succubus, 2x evil eye, 4x slime
					return new ArrayList<>(Arrays.asList(
							Succubus.class, Succubus.class, Slime.class, Slime.class, Slime.class, Slime.class,
							Eye.class, Eye.class, Slime.class));
				case 18: case 19:
					//4x succubus, 1x evil eye, 1x scorpio, 3x slime, 1x hell bat
					return new ArrayList<>(Arrays.asList(
							Slime.class, Slime.class, Slime.class, Succubus.class, Succubus.class, Succubus.class, Succubus.class,
							Eye.class,
							Scorpio.class,  HellBat.class));
				default:
					return new ArrayList<>(Arrays.asList(
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class,
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class,
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class
					));
			}
		}

		if (SPDSettings.bigdungeon()){
			switch(depth){

				// Sewers
				case 1:
					//8x rat
					return new ArrayList<>(Arrays.asList(
							Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class));
				case 2:
					//3x rat, 5x gnoll
					return new ArrayList<>(Arrays.asList(Rat.class, Rat.class,
							Rat.class, Gnoll.class, Gnoll.class, Gnoll.class, Gnoll.class));
				case 3:
					//1x rat, 4x gnoll, 3x dogs
					return new ArrayList<>(Arrays.asList(Rat.class,
							Gnoll.class, Gnoll.class, Gnoll.class, Gnoll.class,
							Dog.class, Dog.class, Dog.class));
				case 4:
					//2x gnoll, 3x swarm, 2x dogs
					return new ArrayList<>(Arrays.asList(
							Gnoll.class, Gnoll.class,
							Dog.class, Swarm.class, Swarm.class,
							Swarm.class, Dog.class, Dog.class));
				case 5: case 6:
					//1x gnoll, 2x crab, 2x swarm, 2x dogs
					return new ArrayList<>(Arrays.asList(
							Gnoll.class, Swarm.class,
							Swarm.class, Dog.class, Dog.class,
							Swarm.class, Crab.class, Crab.class));

				// Prison
				case 7:
					//3x skeleton, 2x crab
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
							Crab.class,
							Crab.class));
				case 8:
					//3x skeleton, 3x thief
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
							Thief.class,
							Thief.class,
							Thief.class));
				case 9:
					//2x skeleton, 2x thief, 3x DM-100
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
							Thief.class,
							Thief.class, DM100.class,
							DM100.class, DM100.class));
				case 10:
					//1x skeleton, 2x thief, 2x DM-100, 3x guard
					return new ArrayList<>(Arrays.asList(Skeleton.class,
							Thief.class,
							Thief.class, DM100.class,
							DM100.class, Guard.class,
							Guard.class, Guard.class));
				case 11: case 12:
					//2x thief, 2x DM-100, 2x guard, 2x necromancer
					return new ArrayList<>(Arrays.asList(Thief.class,
							Thief.class,
							DM100.class, DM100.class,
							Guard.class, Guard.class,
							Necromancer.class, Necromancer.class));

				// Caves
				case 13:
					//5x bat, 2x necromancer
					return new ArrayList<>(Arrays.asList(
							Necromancer.class, Necromancer.class, Bat.class, Bat.class, Bat.class,
							Bat.class, Bat.class));
				case 14:
					//3x bat, 3x brute
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class,
							Bat.class, Brute.class,
							Brute.class,
							Brute.class));
				case 15:
					//2x bat, 4x brute, 2x snake, 1x shaman
					return new ArrayList<>(Arrays.asList(
							Bat.class,
							Brute.class, Brute.class,
							Snake.class, Snake.class,
							Brute.class, Brute.class,
							Bat.class, Shaman.random()));
				case 16:
					//1x bat, 1x brute, 4x shaman, 3x snake, 1x DM-200
					return new ArrayList<>(Arrays.asList(
							Bat.class,
							Brute.class,
							Shaman.random(), Shaman.random(), Snake.class,
							Shaman.random(), Shaman.random(),
							Snake.class, Snake.class, DM200.class));
				case 17: case 18:
					//1x brute, 3x shaman, 2x snake, 2x DM-200, 2x spinner, 1x TNT mouse
					return new ArrayList<>(Arrays.asList(
							Brute.class, Shaman.random(), Snake.class,
							Shaman.random(), Shaman.random(),
							Snake.class, Snake.class, DM200.class, DM200.class, Spinner.class, Spinner.class, ExplodingTNT.class));

				// City
				case 19:
					//6x ghoul, 1x TNT mouse
					return new ArrayList<>(Arrays.asList(
							ExplodingTNT.class,
							Ghoul.class, Ghoul.class,
							Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class));
				case 20:
					//3x ghoul, 4x elemental, 1x monk
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class, Ghoul.class,
							Elemental.random(), Elemental.random(),
							Elemental.random(), Elemental.random(),
							Monk.class));
				case 21:
					//1x ghoul, 3x elemental, 3x warlock, 3x monk
					return new ArrayList<>(Arrays.asList(
							Ghoul.class,
							Elemental.random(), Elemental.random(), Elemental.random(),
							Warlock.class, Warlock.class, Warlock.class,
							Monk.class, Monk.class, Monk.class));
				case 22:
					//2x elemental, 4x warlock, 3x monk, 2x golem
					return new ArrayList<>(Arrays.asList(
							Elemental.random(), Elemental.random(),
							Warlock.class, Warlock.class, Warlock.class,
							Monk.class, Monk.class, Warlock.class, Monk.class,
							Golem.class, Golem.class));
				case 23: case 24:
					//1x elemental, 3x warlock, 2x monk, 3x golem, 2x dwarf guards
					return new ArrayList<>(Arrays.asList(
							Elemental.random(),
							Warlock.class, Warlock.class,
							Monk.class, Monk.class, Warlock.class,
							Golem.class, Golem.class, Golem.class, DwarfGuardMob.class, DwarfGuardMob.class));

				// Halls
				case 25:
					//2x dwarf guard, 2x slime
					return new ArrayList<>(Arrays.asList(
							Slime.class,
							Slime.class, DwarfGuardMob.class, DwarfGuardMob.class));
				case 26:
					//3x succubus, 1x slime
					return new ArrayList<>(Arrays.asList(
							Succubus.class, Succubus.class, Succubus.class, Slime.class));
				case 27:
					//2x succubus, 1x slime, 2x eye
					return new ArrayList<>(Arrays.asList(
							Succubus.class, Succubus.class, Slime.class, Eye.class, Eye.class));
				case 28:
					//2x evil eye, 2x succubus, 2x scorpio
					return new ArrayList<>(Arrays.asList(
							Eye.class, Eye.class, Succubus.class, Succubus.class,
							Scorpio.class, Scorpio.class));
				case 29: case 30:
					//2x evil eye, 2x succubus, 2x scorpio, 2x hellbat
					return new ArrayList<>(Arrays.asList(
							Eye.class, Eye.class, Succubus.class, Succubus.class,
							Scorpio.class, Scorpio.class, HellBat.class, HellBat.class));
				default:
					return new ArrayList<>(Arrays.asList(
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class,
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class,
							SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class
					));
			}
		} else
		switch(depth){
			
			// Sewers
			case 1:
				//3x rat, 1x snake
				return new ArrayList<>(Arrays.asList(
                         Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class, Rat.class));
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
			default:
				return new ArrayList<>(Arrays.asList(
						SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class
				));
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
				} else if (cl == SpectreRat.class || cl == GhostChicken.class || cl == DarkestElf.class){
					cl = AbyssalNightmare.class;
				} else if (cl == BlinkingMan.class || cl == Phantom.class){
					cl = Dragon.class;
				}
				rotation.set(i, cl);
			}
		}
	}
}
