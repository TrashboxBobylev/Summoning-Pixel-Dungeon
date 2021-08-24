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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TribeGnoll;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class GnollTribeRoom extends StandardRoom {

	{
		noMobs = true;
	}
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 7);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 7);
	}
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 2, 0, 0};
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.BARRICADE );
		Painter.fill( level, this, 1 , Terrain.EMPTY_SP );
		
		for (Door door : connected.values()) {
			Painter.drawInside(level, this, door, 2, Terrain.EMPTY_SP);
			door.set( Door.Type.REGULAR );
		}
		
		Point center = center();
		Painter.set( level, center, Terrain.EMBERS );
		int obstacles = Random.Int(4 * sizeCat.roomValue, 12 * sizeCat.roomValue);
		for (int i = 0; i < obstacles; i++){
			Painter.set(level, level.pointToCell(random(1)), Terrain.BARRICADE);
		}
		int tribemans = Random.Int(2, 4);
		for (int i = 0; i < tribemans; i++){
			TribeGnoll npc = new TribeGnoll();
			do {
				npc.pos = level.pointToCell(random());
			} while (level.map[npc.pos] != Terrain.EMPTY_SP || level.findMob(npc.pos) != null);
			npc.state = npc.SLEEPING;
			level.mobs.add(npc);
		}

		for (int i = 0; i < 2; i++) {
			Item prize = Generator.randomMissile();
			prize.quantity((int) (prize.quantity()));

			level.drop(prize, (center.x + center.y * level.width()));
		}
	}
}
