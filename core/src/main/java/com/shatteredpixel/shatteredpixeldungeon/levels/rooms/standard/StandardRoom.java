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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;

public abstract class StandardRoom extends Room {
	
	public enum SizeCategory {

		NORMAL(4, 10, 1),
		LARGE(10, 14, 2),
		GIANT(14, 18, 3),
		EXCESSIVE(18, 26, 4);

		public final int minDim26, maxDim26;
		public final int roomValue;
		
		SizeCategory(
					 int min26, int max26,
					 int val){
			minDim26 = min26;
			maxDim26 = max26;
			roomValue = val;
		}
		
		public int connectionWeight(){
			if (Dungeon.mode == Dungeon.GameMode.CHAOS) return Random.IntRange(1, roomValue*roomValue);
			return roomValue*roomValue;
		}
		
	}
	
	public SizeCategory sizeCat;
	{ setSizeCat(); }
	
	//Note that if a room wishes to allow itself to be forced to a certain size category,
	//but would (effectively) never roll that size category, consider using Float.MIN_VALUE
	public float[] sizeCatProbs(){
		//always normal by default
		return new float[]{1, 0, 0, 0};
	}
	
	public boolean setSizeCat(){
		return setSizeCat(0, SizeCategory.values().length-1);
	}
	
	//assumes room value is always ordinal+1
	public boolean setSizeCat( int maxRoomValue ){
		return setSizeCat(0, maxRoomValue-1);
	}
	
	//returns false if size cannot be set
	public boolean setSizeCat( int minOrdinal, int maxOrdinal ) {
		float[] probs = sizeCatProbs();
		SizeCategory[] categories = SizeCategory.values();
		
		if (probs.length != categories.length) return false;
		
		for (int i = 0; i < minOrdinal; i++)                    probs[i] = 0;
		for (int i = maxOrdinal+1; i < categories.length; i++)  probs[i] = 0;
		
		int ordinal = Random.chances(probs);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS){
			ordinal = Random.chances(new float[]{4, 2, 1, 1});
		}

		if (ordinal != -1){
			sizeCat = categories[ordinal];
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int minWidth() {
		return sizeCat.minDim26;
	}
	public int maxWidth() {
		return sizeCat.maxDim26;
	}

	@Override
	public boolean canMerge(Level l, Point p, int mergeTerrain) {
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) return true;
		int cell = l.pointToCell(pointInside(p, 1));
		return (Terrain.flags[l.map[cell]] & Terrain.SOLID) == 0;
	}

	public int minHeight() {
		return sizeCat.minDim26;
	}
	public int maxHeight() {
		return sizeCat.maxDim26;
	}

	private static HashMap<Class<?extends StandardRoom>, Float> sewerSet = new HashMap<>();
	static {
		sewerSet.put(EmptyRoom.class, 15f);
		sewerSet.put(SewerPipeRoom.class, 10f);
		sewerSet.put(RingRoom.class, 10f);
		sewerSet.put(CircleBasinRoom.class, 5f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> prisonSet = new HashMap<>();
	static {
		prisonSet.put(EmptyRoom.class, 15f);
		prisonSet.put(SegmentedRoom.class, 10f);
		prisonSet.put(PillarsRoom.class, 10f);
		prisonSet.put(CellBlockRoom.class, 5f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> cavesSet = new HashMap<>();
	static {
		cavesSet.put(EmptyRoom.class, 20f);
		cavesSet.put(CaveRoom.class, 10f);
		cavesSet.put(CavesFissureRoom.class, 10f);
		cavesSet.put(CirclePitRoom.class, 5f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> dwarvesSet = new HashMap<>();
	static {
		dwarvesSet.put(EmptyRoom.class, 15f);
		dwarvesSet.put(HallwayRoom.class, 10f);
		dwarvesSet.put(StatuesRoom.class, 10f);
		dwarvesSet.put(SegmentedLibraryRoom.class, 5f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> hallsSet = new HashMap<>();
	static {
		hallsSet.put(EmptyRoom.class, 15f);
		hallsSet.put(RuinsRoom.class, 10f);
		hallsSet.put(ChasmRoom.class, 10f);
		hallsSet.put(SkullsRoom.class, 5f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> chaosSet = new HashMap<>();
	static {
		chaosSet.put(EmptyRoom.class, 1f);

		chaosSet.put(SewerPipeRoom.class, 1f);
		chaosSet.put(RingRoom.class, 1f);
		chaosSet.put(CircleBasinRoom.class, 1f);

		chaosSet.put(SegmentedRoom.class, 1f);
		chaosSet.put(PillarsRoom.class, 1f);
		chaosSet.put(CellBlockRoom.class, 1f);

		chaosSet.put(CaveRoom.class, 1f);
		chaosSet.put(CavesFissureRoom.class, 1f);
		chaosSet.put(CirclePitRoom.class, 1f);

		chaosSet.put(HallwayRoom.class, 1f);
		chaosSet.put(StatuesRoom.class, 1f);
		chaosSet.put(SegmentedLibraryRoom.class, 1f);

		chaosSet.put(RuinsRoom.class, 1f);
		chaosSet.put(ChasmRoom.class, 1f);
		chaosSet.put(SkullsRoom.class, 1f);
	}

	private static HashMap[] allSets = {sewerSet, prisonSet, cavesSet, dwarvesSet, hallsSet, chaosSet};
	static {
		for (HashMap<Class<?extends StandardRoom>, Float> set: allSets){
			set.put(PlantsRoom.class, 1f);
			set.put(AquariumRoom.class, 1f);
			set.put(PlatformRoom.class, 1f);
			set.put(BurnedRoom.class, 1f);
			set.put(FissureRoom.class, 1f);
			set.put(GrassyGraveRoom.class, 1f);
			set.put(StripedRoom.class, 1f);
			set.put(StudyRoom.class, 1f);
			set.put(SuspiciousChestRoom.class, 1f);
			set.put(MinefieldRoom.class, 1f);
		}
	}

	private static HashMap<Class<?extends StandardRoom>, Float> firstDepthSet = new HashMap<>();
	static {
		firstDepthSet.put(EmptyRoom.class, 15f);
		firstDepthSet.put(SewerPipeRoom.class, 10f);
		firstDepthSet.put(RingRoom.class, 10f);
		firstDepthSet.put(CircleBasinRoom.class, 5f);
		firstDepthSet.put(PlantsRoom.class, 1f);
		firstDepthSet.put(PlatformRoom.class, 1f);
		firstDepthSet.put(FissureRoom.class, 1f);
		firstDepthSet.put(StripedRoom.class, 1f);
		firstDepthSet.put(StudyRoom.class, 1f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> bossSet = new HashMap<>();
	static {
		bossSet.put(EmptyRoom.class, 15f);
		bossSet.put(SewerPipeRoom.class, 10f);
		bossSet.put(RingRoom.class, 10f);
	}

	private static HashMap<Class<?extends StandardRoom>, Float> getFloorRooms(int depth){
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) return chaosSet;
		if (depth == 1) return firstDepthSet;
		if (depth == Dungeon.chapterSize()) return bossSet;
		if (depth > 0 && depth <= Dungeon.chapterSize()){
			return sewerSet;
		}
		else if (depth <= Dungeon.chapterSize()*2){
			return prisonSet;
		}
		else if (depth <= Dungeon.chapterSize()*3){
			return cavesSet;
		}
		else if (depth <= Dungeon.chapterSize()*4){
			return dwarvesSet;
		}
		else if (depth <= Dungeon.chapterSize()*5){
			return hallsSet;
		}
		else {
			return chaosSet;
		}
	}
	
	
	public static StandardRoom createRoom(){
		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
			return Reflection.newInstance(EmptyRoom.class);
		}

		HashMap<Class<?extends StandardRoom>, Float> rooms = getFloorRooms(Dungeon.depth);
		return Reflection.newInstance(Random.chances(rooms));
	}
	
}
