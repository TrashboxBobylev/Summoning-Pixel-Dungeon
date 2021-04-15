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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class ConnectionRoom extends Room {
	
	@Override
	public int minWidth() { return 3; }
	public int maxWidth() { return 10; }
	
	@Override
	public int minHeight() { return 3; }
	public int maxHeight() { return 10; }
	
	@Override
	public int minConnections(int direction) {
		if (direction == ALL)   return 2;
		else                    return 0;
	}
	
	@Override
	public boolean canPlaceTrap(Point p) {
		//traps cannot appear in connection rooms on floor 1
		return super.canPlaceTrap(p) && Dungeon.depth > 1;
	}
	
	//FIXME this is a very messy way of handing variable connection rooms
	private static ArrayList<Class<?extends ConnectionRoom>> rooms = new ArrayList<>();
	static {
		rooms.add(TunnelRoom.class);
		rooms.add(BridgeRoom.class);
		
		rooms.add(PerimeterRoom.class);
		rooms.add(WalkwayRoom.class);
		
		rooms.add(RingTunnelRoom.class);
		rooms.add(RingBridgeRoom.class);
	}
	
	private static float[][] chances = new float[31][];
	static void setChances() {
		for (int i = 0; i <= Dungeon.chapterSize(); i++){
			chances[i] = new float[]{20, 1, 0, 2, 2, 1};
		}
		for (int i = Dungeon.chapterSize()+1; i <= Dungeon.chapterSize()*2; i++){
			chances[i] = new float[]{0, 0, 22, 3, 0, 0};
		}
		for (int i = Dungeon.chapterSize()*2+1; i <= Dungeon.chapterSize()*3; i++){
			chances[i] = new float[]{12, 0, 0, 5, 5, 3};
		}
		for (int i = Dungeon.chapterSize()*3+1; i <= Dungeon.chapterSize()*4; i++){
			chances[i] = new float[]{0, 0, 18, 3, 3, 1};
		}
		for (int i = Dungeon.chapterSize()*4+1; i <= Dungeon.chapterSize()*5+1; i++){
			chances[i] = new float[]{15, 4, 0, 2, 3, 2};
		}
		chances[1] = new float[]{20, 1, 0, 2, 2, 1};
		chances[Dungeon.chapterSize()] = new float[]{20, 0, 0, 0, 0, 0};
	}

	static {
		setChances();
	}
	
	public static ConnectionRoom createRoom(){
		setChances();
		return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
	}
}
