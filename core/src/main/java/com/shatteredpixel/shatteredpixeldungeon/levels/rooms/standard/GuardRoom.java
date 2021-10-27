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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MajesticGuard;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class GuardRoom extends StandardRoom {

	{
		noMobs = true;
	}

	@Override
	public int minWidth() {
		return Math.max(7, super.minWidth());
	}

	@Override
	public int minHeight() {
		return Math.max(7, super.minHeight());
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{8, 4, 2, 1};
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );

		Painter.fillEllipse( level, this, 1 , Terrain.EMPTY );
		Painter.fillEllipse( level, this, 2 , Terrain.EMPTY_SP );

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
			if (door.x == left || door.x == right){
				Painter.drawInside(level, this, door, width()/2, Terrain.EMPTY_SP);
			} else {
				Painter.drawInside(level, this, door, height()/2, Terrain.EMPTY_SP);
			}
		}

		Point center = center();
		Painter.set( level, center, Terrain.PEDESTAL );

		MajesticGuard guard = new MajesticGuard();
		guard.state = guard.HUNTING;
		guard.pos = level.pointToCell(center);
		level.mobs.add(guard);

		Item prize = level.findPrizeItem();
		if (prize != null){
			int pos;
			do {
				pos = level.pointToCell(random());
			} while (level.distance(pos, guard.pos) == Terrain.EMPTY_SP || level.solid[pos]);
			level.drop( prize, pos );
		}
		int pos;
		do {
			pos = level.pointToCell(random());
		} while (!level.adjacent(pos, guard.pos));
		level.drop( new ScrollOfAffection(), pos );
	}
}
