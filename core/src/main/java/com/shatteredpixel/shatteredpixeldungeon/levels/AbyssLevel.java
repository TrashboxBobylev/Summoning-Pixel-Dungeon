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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Chaosstone;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.AbyssalSpawnerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.Halo;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssLevel extends RegularLevel {

	{
		color1 = 0x232424;
		color2 = 0x3e4040;
		viewDistance = 2;
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> rooms = super.initRooms();

		rooms.add(new AbyssalSpawnerRoom());

		return rooms;
	}

	@Override
	public void create() {
		addItemToSpawn(Generator.random(Generator.Category.FOOD));
		addItemToSpawn( new com.shatteredpixel.shatteredpixeldungeon.items.Torch() );
		for (int i = 0; i < GameMath.gate(1, Dungeon.depth / Dungeon.chapterSize() - 5
				- (Dungeon.depth % Dungeon.chapterSize() == 0 ? 1 : 0), Integer.MAX_VALUE); i++){
			addItemToSpawn(new Chaosstone());
			if (Random.Int(2) == 0) addItemToSpawn(new Chaosstone());
		}
		super.create();
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		if (SPDSettings.smalldungeon()) return 6;
		if (forceMax) return 35;
		//23 to 28
		return 17+Random.chances(new float[]{3, 2, 1, 1, 1});
	}

	@Override
	protected int specialRooms(boolean forceMax) {
		if (SPDSettings.smalldungeon()) return 1;
		if (forceMax) return 3;
		//2 to 4, average 2.5
		return 2 + Random.chances(new float[]{1, 1});
	}

	public Actor addRespawner() {
		return null;
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
		return Assets.Environment.TILES_ABYSS;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_ABYSS;
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				FrostTrap.class, StormTrap.class, CorrosionTrap.class, CalamityTrap.class, DisintegrationTrap.class,
				RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, AbyssFlameTrap.class,
				DisarmingTrap.class, WraithTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				2, 2, 2, 2, 2,
				2, 2, 2,
				1, 1, 1, 1 };
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(AbyssLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		return Messages.get(AbyssLevel.class, "not_recognizable");
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
			
			pour( ShadowParticle.UP, 0.15f );
			
			add( new Halo( 12, 0x6b6b6b, 0.4f ).point( p.x, p.y + 1 ) );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}
}