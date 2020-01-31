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

package com.trashboxbobylev.summoningpixeldungeon.levels;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Challenges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.watabou.noosa.Halo;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.FlameParticle;
import com.trashboxbobylev.summoningpixeldungeon.levels.painters.Painter;
import com.trashboxbobylev.summoningpixeldungeon.levels.painters.PrisonPainter;
import com.trashboxbobylev.summoningpixeldungeon.levels.rooms.Room;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.AlarmTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.BurningTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.ChillingTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.ConfusionTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.FlockTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.GrippingTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.OozeTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.PoisonDartTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.ShockingTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.SummoningTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.TeleportationTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.ToxicTrap;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PrisonLevel extends RegularLevel {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}
	
	@Override
	protected ArrayList<Room> initRooms() {
	    if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) return super.initRooms();
        else return Wandmaker.Quest.spawnRoom(super.initRooms());
	}
	
	@Override
	protected int standardRooms() {
		//6 to 8, average 6.66
		return 6+Random.chances(new float[]{4, 2, 2});
	}
	
	@Override
	protected int specialRooms() {
		//1 to 3, average 1.83
		return 1+Random.chances(new float[]{3, 4, 3});
	}
	
	@Override
	protected Painter painter() {
		return new PrisonPainter()
				.setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_PRISON;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{ ChillingTrap.class, ShockingTrap.class, ToxicTrap.class, BurningTrap.class, PoisonDartTrap.class,
				AlarmTrap.class, OozeTrap.class, GrippingTrap.class,
				ConfusionTrap.class, FlockTrap.class, SummoningTrap.class, TeleportationTrap.class, };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{ 8, 8, 8, 8, 8,
				4, 4, 4,
				2, 2, 2, 2 };
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(PrisonLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(PrisonLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addPrisonVisuals(this, visuals);
		return visuals;
	}

	public static void addPrisonVisuals(Level level, Group group){
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Torch( i ) );
			}
		}
	}
	
	public static class Torch extends Emitter {
		
		private int pos;
		
		public Torch( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 1, p.y + 2, 2, 0 );
			
			pour( FlameParticle.FACTORY, 0.15f );
			
			add( new Halo( 12, 0xFFFFCC, 0.4f ).point( p.x, p.y + 1 ) );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}
}