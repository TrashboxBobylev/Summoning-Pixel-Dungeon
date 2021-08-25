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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpectralShaman;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class SpectralShamanRoom extends StandardRoom {

	{
		noMobs = true;
	}

	@Override
	public int minWidth() {
		return 7;
	}
	@Override
	public int maxWidth() {
		return 7;
	}
	@Override
	public int minHeight() {
		return 13;
	}
	@Override
	public int maxHeight() {
		return 13;
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{1, 0, 0, 0};
	}

	@Override
	public boolean canMerge(Level l, Point p, int mergeTerrain) {
		return false;
	}

	private static final int TOP			= 2;
	private static final int HALL_WIDTH		= 7;
	private static final int HALL_HEIGHT	= 15;

	private static final int LEFT	= 0;

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		
		for (Door door : connected.values()) {
			door.set( Door.Type.EMPTY );
		}

		Painter.fill( level, this, 1 , Terrain.EMPTY );

		Point start = new Point(left + width()/2, top+1);
		Point end = new Point(left + width()/2, bottom-1);
		Painter.drawLine(level, start, end, Terrain.EMPTY_SP);

		int y = top+1;
		while (y < bottom) {
			Painter.set(level, left+2+width()/2, y, Terrain.STATUE_SP);
			Painter.set(level, left-2+width()/2, y, Terrain.STATUE_SP);
			y += 2;
		}

		int left = pedestal( level, true );
		int right = pedestal( level, false );
		level.map[left] = level.map[right] = Terrain.PEDESTAL;
		for (int i=left+1; i < right; i++) {
			level.map[i] = Terrain.EMPTY_SP;
		}

		SpectralShaman shaman = new SpectralShaman();
		shaman.startPosition = shaman.pos = level.pointToCell(center());
		shaman.state = shaman.PASSIVE;
		level.mobs.add(shaman);
	}

	public int pedestal( Level level, boolean left ) {
		Point point = new Point();
		if (left){
			point.set(this.left + width()/2 - 2, top+height()/2);
		} else {
			point.set(this.left + width()/2 + 2, top+height()/2);
		}

		return level.pointToCell(point);
	}
}
