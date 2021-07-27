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

import java.util.ArrayList;

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

	//FIXME this is a very messy way of handing variable standard rooms
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		rooms.add(EmptyRoom.class);


		rooms.add(SewerPipeRoom.class);
		rooms.add(RingRoom.class);
		rooms.add(CircleBasinRoom.class);

		rooms.add(SegmentedRoom.class);
		rooms.add(PillarsRoom.class);
		rooms.add(CellBlockRoom.class);

		rooms.add(CaveRoom.class);
		rooms.add(CavesFissureRoom.class);
		rooms.add(CirclePitRoom.class);

		rooms.add(HallwayRoom.class);
		rooms.add(StatuesRoom.class);
		rooms.add(SegmentedLibraryRoom.class);

		rooms.add(RuinsRoom.class);
		rooms.add(ChasmRoom.class);
		rooms.add(SkullsRoom.class);


		rooms.add(PlantsRoom.class);
		rooms.add(AquariumRoom.class);
		rooms.add(PlatformRoom.class);
		rooms.add(BurnedRoom.class);
		rooms.add(FissureRoom.class);
		rooms.add(GrassyGraveRoom.class);
		rooms.add(StripedRoom.class);
		rooms.add(StudyRoom.class);
		rooms.add(SuspiciousChestRoom.class);
		rooms.add(MinefieldRoom.class);
	}
	
	private static float[][] chances = new float[1000][];
	static void setChances() {
		for (int i = 0; i <= Dungeon.chapterSize(); i++){
			chances[i] = new float[]{15, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
		}
		for (int i = Dungeon.chapterSize()+1; i <= Dungeon.chapterSize()*2; i++){
			chances[i] = new float[]{15, 0, 0, 0, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
		}
		for (int i = Dungeon.chapterSize()*2+1; i <= Dungeon.chapterSize()*3; i++){
			chances[i] = new float[]{20, 0, 0, 0, 0, 0, 0, 10, 10, 5, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
		}
		for (int i = Dungeon.chapterSize()*3+1; i <= Dungeon.chapterSize()*4; i++){
			chances[i] = new float[]{15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 10, 5, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
		}
		for (int i = Dungeon.chapterSize()*4+1; i <= Dungeon.chapterSize()*5+1; i++){
			chances[i] = new float[]{15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 10, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
		}
		chances[1] = new float[]{15, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0};
		chances[Dungeon.chapterSize()] = new float[]{15, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
	static {
		setChances();
	}
	
	
	public static StandardRoom createRoom(){
		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
			return Reflection.newInstance(EmptyRoom.class);
		}
		setChances();
		if (Dungeon.depth >= Dungeon.chapterSize()*5 || Dungeon.mode == Dungeon.GameMode.CHAOS){
			float[] chance = new float[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
			return Reflection.newInstance(rooms.get(Random.chances(chance)));
		}
		else return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
	}
	
}
