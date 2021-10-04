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

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class RegularLevel extends Level {
	
	protected ArrayList<Room> rooms;
	
	protected Builder builder;
	
	protected Room roomEntrance;
	protected Room roomExit;
	
	public int secretDoors;

	@Override
	protected boolean build() {
		
		builder = builder();

		if (Dungeon.mode == Dungeon.GameMode.BIGGER && builder instanceof RegularBuilder){
			((RegularBuilder) builder).setExtraConnectionChance(1f);
		}

		ArrayList<Room> initRooms = initRooms();
		Random.shuffle(initRooms);
		
		do {
			for (Room r : initRooms){
				r.neigbours.clear();
				r.connected.clear();
			}
			rooms = builder.build((ArrayList<Room>)initRooms.clone());
		} while (rooms == null);
		
		return painter().paint(this, rooms);
		
	}
	
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		if (Dungeon.mode == Dungeon.GameMode.CAVES) {
			initRooms.add(roomEntrance = new CaveEntrance());
			initRooms.add(roomExit = new CaveExit());
		} else {
			initRooms.add(roomEntrance = new EntranceRoom());
			initRooms.add(roomExit = new ExitRoom());
		}

		//force max standard rooms and multiple by 1.5x for large levels
		int standards = standardRooms(feeling == Feeling.LARGE || SPDSettings.bigdungeon());
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) standards = Random.Int(1, standards * 2);
		if (feeling == Feeling.LARGE && !SPDSettings.smalldungeon()) {
			standards = (int) Math.ceil(standards * 1.5f);
		}
		if (Dungeon.mode == Dungeon.GameMode.CAVES){
			standards *= 1.33f;
		}
		for (int i = 0; i < standards; i++) {
			StandardRoom s;
			boolean roomSizeCon;
			do {
				s = StandardRoom.createRoom();
				if (SPDSettings.bigdungeon()) {
					roomSizeCon = !s.setSizeCat(1, (int) (standards * 1.25f) - i);
				} else {
					roomSizeCon = !s.setSizeCat(standards - i);
				}
			} while (roomSizeCon);
			i += s.sizeCat.roomValue - 1;
			initRooms.add(s);
		}

		if (Dungeon.shopOnLevel())
			initRooms.add(new ShopRoom());

		//force max special rooms and add one more for large levels
		int specials = specialRooms(feeling == Feeling.LARGE || SPDSettings.bigdungeon());
		if (feeling == Feeling.LARGE) {
			specials++;
		}
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) specials = Random.Int(1, specials);
		SpecialRoom.initForFloor();
		if (Dungeon.mode != Dungeon.GameMode.CAVES && !Dungeon.isChallenged(Conducts.Conduct.NO_LOOT)){
			for (int i = 0; i < specials; i++) {
				SpecialRoom s = SpecialRoom.createRoom();
				if (s instanceof PitRoom) specials++;
				initRooms.add(s);
			}

		int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
		//one additional secret for secret levels
		if (feeling == Feeling.SECRETS) secrets++;
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) secrets = Random.Int(1, secrets);
		for (int i = 0; i < secrets; i++) {
			initRooms.add(SecretRoom.createRoom());
		}
	}

		return initRooms;
	}
	
	protected int standardRooms(boolean forceMax){
		return 0;
	}
	
	protected int specialRooms(boolean forceMax){
		return 0;
	}
	
	protected Builder builder(){
		int tryn = Random.Int(3);
		if (tryn == 0 || Dungeon.mode == Dungeon.GameMode.CHAOS){
			return new LoopBuilder()
					.setLoopShape( 3 ,
							Random.Float(0f, 0.9f),
							Random.Float(0f, 0.7f));
		}
		else if (tryn == 1){
			return new FigureEightBuilder()
					.setLoopShape( 3 ,
							Random.Float(0.2f, 1.5f),
							0f);
		} else if (tryn == 2 || Dungeon.mode == Dungeon.GameMode.CAVES){
			return new ClumpyLoopBuilder()
					.setLoopShape( 3 ,
							Random.Float(0f, 0.4f),
							Random.Float(0f, 0.6f));
		}
		return null;
	}
	
	protected abstract Painter painter();

	protected int nTraps() {
		if (SPDSettings.bigdungeon())
			return Random.NormalIntRange( 8, 12 + (Dungeon.depth) );

		if (SPDSettings.smalldungeon())
			return Random.NormalIntRange( 1, 3 + (Dungeon.depth/5) );

		int i = Random.NormalIntRange(3, 5 + (Dungeon.depth / 3));
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) i = Random.Int(1, i);
		return i;
	}
	
	protected Class<?>[] trapClasses(){
		return new Class<?>[]{WornDartTrap.class};
	}

	protected float[] trapChances() {
		return new float[]{1};
	}
	
	@Override
	public int nMobs() {
		if (Dungeon.depth <= 1 && Dungeon.mode != Dungeon.GameMode.GAUNTLET) return 0;

		int mobs = Math.round(5 + Dungeon.depth*0.7f + Random.Int(4));
		if (feeling == Feeling.LARGE){
			mobs = (int)Math.ceil(mobs * 2f);
		}
		if (SPDSettings.bigdungeon()) mobs *= 1.5f;
		if (SPDSettings.smalldungeon()) mobs /= 3;
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) mobs = Random.Int(0, mobs);

		return mobs;
	}
	
	@Override
	protected void createMobs() {
		//on floor 1, 8 pre-set mobs are created so the player can get level 2.
		int mobsToSpawn = Dungeon.depth == 1 ? 10 : nMobs();
		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET) mobsToSpawn = nMobs();

		ArrayList<Room> stdRooms = new ArrayList<>();
		for (Room room : rooms) {
			if (room instanceof StandardRoom && room != roomEntrance && !((StandardRoom) room).noMobs) {
				for (int i = 0; i < ((StandardRoom) room).sizeCat.roomValue; i++) {
					stdRooms.add(room);
				}
			}
		}
		Random.shuffle(stdRooms);
		Iterator<Room> stdRoomIter = stdRooms.iterator();

		while (mobsToSpawn > 0) {
			Mob mob = createMob();
			Room roomToSpawn;

			if (!stdRoomIter.hasNext()) {
				stdRoomIter = stdRooms.iterator();
			}
			roomToSpawn = stdRoomIter.next();

			int tries = 50;
			do {
				mob.pos = pointToCell(roomToSpawn.random());
				tries--;
			} while (tries >= 0 && (findMob(mob.pos) != null || !passable[mob.pos] || mob.pos == exit
					|| (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

			if (tries >= 0) {
				mobsToSpawn--;
				mobs.add(mob);

				//chance to add a second mob to this room, except on floor 1
				if (Dungeon.depth > 1 && mobsToSpawn > 0 && Random.Int(4) == 0){
					mob = createMob();

					tries = 30;
					do {
						mob.pos = pointToCell(roomToSpawn.random());
						tries--;
					} while (tries >= 0 && (findMob(mob.pos) != null || !passable[mob.pos] || mob.pos == exit
							|| (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

					if (tries >= 0) {
						mobsToSpawn--;
						mobs.add(mob);
					}
				}
			}
		}

		for (Mob m : mobs){
			if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
				map[m.pos] = Terrain.GRASS;
				losBlocking[m.pos] = false;
			}

		}

	}

	@Override
	public int randomRespawnCell( Char ch ) {
		int count = 0;
		int cell = -1;

		while (true) {

			if (++count > 50) {
				return -1;
			}

			Room room = randomRoom( StandardRoom.class );
			if (room == null || room == roomEntrance) {
				continue;
			}

			cell = pointToCell(room.random(1));
			if (!heroFOV[cell]
					&& Actor.findChar( cell ) == null
					&& passable[cell]
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])
					&& room.canPlaceCharacter(cellToPoint(cell), this)
					&& cell != exit) {
				return cell;
			}

		}
	}
	
	@Override
	public int randomDestination( Char ch ) {
		
		int count = 0;
		int cell = -1;
		
		while (true) {
			
			if (++count > 50) {
				return -1;
			}
			
			Room room = Random.element( rooms );
			if (room == null) {
				continue;
			}
			
			cell = pointToCell(room.random());
			if (passable[cell] && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])) {
				return cell;
			}
			
		}
	}
	
	@Override
	protected void createItems() {

		int nItems = 3 + Random.chances(new float[]{6, 3, 1});

		if (feeling == Feeling.LARGE){
			nItems += 6;
		}
		if (SPDSettings.bigdungeon()) {
			nItems *= 2;
			if (Dungeon.hero.heroClass == HeroClass.ADVENTURER) nItems *= 1.5f;
		}
		else if (Dungeon.hero.heroClass == HeroClass.ADVENTURER) nItems *= 2f;
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT) nItems *= 1.5f;

		if (Dungeon.mode == Dungeon.GameMode.CHAOS) nItems = Random.Int(1 ,nItems);
		if (Dungeon.isChallenged(Conducts.Conduct.NO_LOOT)){
			nItems = 1;
			if (Dungeon.hero.heroClass == HeroClass.ADVENTURER) nItems = 2;
		}

		for (int i=0; i < nItems; i++) {

			Item toDrop = Generator.random();
			if (toDrop == null) continue;

			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}

			Heap.Type type = null;
			switch (Random.Int( 20 )) {
			case 0:
				type = Heap.Type.SKELETON;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				type = Heap.Type.CHEST;
				break;
			case 5:
				if (Dungeon.depth > 1 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop));
					continue;
				}
				type = Heap.Type.CHEST;
				break;
			default:
				type = Heap.Type.HEAP;
				break;
			}

			if ((toDrop instanceof Artifact && Random.Int(2) == 0) ||
					(toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0)){

				if (Dungeon.depth > 1 && Random.Int(10) == 0 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop, GoldenMimic.class));
				} else {
					Heap dropped = drop(toDrop, cell);
					if (heaps.get(cell) == dropped) {
						dropped.type = Heap.Type.LOCKED_CHEST;
						addItemToSpawn(new GoldenKey(Dungeon.depth));
					}
				}
			} else {
				Heap dropped = drop( toDrop, cell );
				dropped.type = type;
				if (type == Heap.Type.SKELETON){
					dropped.setHauntedIfCursed();
				}
			}
			
		}

		if (Random.Int(10) < 2) itemsToSpawn.add(new Ropes().quantity(Random.Int(1, 3)));

		for (Item item : itemsToSpawn) {
			int cell = randomDropCell();
			drop( item, cell ).type = Heap.Type.HEAP;
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
		}

		//use a separate generator for this to prevent held items and meta progress from affecting levelgen
		Random.pushGenerator( Dungeon.seedCurDepth() );

		Item item = Bones.get();
		if (item != null) {
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( item, cell ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}

		DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
		if (rose != null && rose.isIdentified() && !rose.cursed){
			//aim to drop 1 petal every 2 floors
			int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

			for (int i=1; i <= petalsNeeded; i++) {
				//the player may miss a single petal and still max their rose.
				if (rose.droppedPetals < 11) {
					item = new DriedRose.Petal();
					int cell = randomDropCell();
					drop( item, cell ).type = Heap.Type.HEAP;
					if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
						map[cell] = Terrain.GRASS;
						losBlocking[cell] = false;
					}
					rose.droppedPetals++;
				}
			}
		}

		if (Dungeon.hero.hasTalent(Talent.SPECIAL_DELIVERY)){
			Talent.SpecialDeliveryCount dropped = Buff.affect(Dungeon.hero, Talent.SpecialDeliveryCount.class);
			if (dropped.count() < 1 + Dungeon.hero.pointsInTalent(Talent.SPECIAL_DELIVERY)){
				int cell;
				do {
					cell = randomDropCell(SpecialRoom.class);
				} while (room(cell) instanceof SecretRoom);
				if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
					map[cell] = Terrain.GRASS;
					losBlocking[cell] = false;
				}
				// give anything that is not gold
				Item droppy;
				do {
					droppy = Generator.random();
				} while (droppy instanceof Gold);
				drop( droppy, cell).type = Heap.Type.CHEST;
				dropped.countUp(1);
			}
		}

		//guide pages
		Collection<String> allPages = Document.ADVENTURERS_GUIDE.pageNames();
		ArrayList<String> missingPages = new ArrayList<>();
		for ( String page : allPages){
			if (!Document.ADVENTURERS_GUIDE.isPageFound(page)){
				missingPages.add(page);
			}
		}

		//a total of 6 pages drop randomly, the rest are specially dropped or are given at the start
		missingPages.remove(Document.GUIDE_SEARCHING);

		//chance to find a page is 0/25/50/75/100% for floors 1/2/3/4/5+
		float dropChance = 0.25f*(Dungeon.depth-1);
		if (!missingPages.isEmpty() && Random.Float() < dropChance){
			GuidePage p = new GuidePage();
			p.page(missingPages.get(0));
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( p, cell );
		}

		Random.popGenerator();

	}
	
	public ArrayList<Room> rooms() {
		return new ArrayList<>(rooms);
	}
	
	//FIXME pit rooms shouldn't be problematic enough to warrant this
	public boolean hasPitRoom(){
		for (Room r : rooms) {
			if (r instanceof PitRoom) {
				return true;
			}
		}
		return false;
	}
	
	protected Room randomRoom( Class<?extends Room> type ) {
		Random.shuffle( rooms );
		for (Room r : rooms) {
			if (type.isInstance(r)) {
				return r;
			}
		}
		return null;
	}
	
	public Room room( int pos ) {
		for (Room room : rooms) {
			if (room.inside( cellToPoint(pos) )) {
				return room;
			}
		}
		
		return null;
	}

	protected int randomDropCell(){
		return randomDropCell(StandardRoom.class);
	}

	protected int randomDropCell( Class<?extends Room> roomType ) {
		int tries = 100;
		while (tries-- > 0) {
			Room room = randomRoom( roomType );
			if (room != null && room != roomEntrance) {
				int pos = pointToCell(room.random());
				if (passable[pos] && !solid[pos]
						&& pos != exit
						&& heaps.get(pos) == null
						&& findMob(pos) == null) {

					Trap t = traps.get(pos);

					//items cannot spawn on traps which destroy items
					if (t == null ||
							! (t instanceof BurningTrap || t instanceof BlazingTrap
									|| t instanceof ChillingTrap || t instanceof FrostTrap
									|| t instanceof ExplosiveTrap || t instanceof DisintegrationTrap)) {

						return pos;
					}
				}
			}
		}
		return -1;
	}
	
	@Override
	public int fallCell( boolean fallIntoPit ) {
		if (fallIntoPit) {
			for (Room room : rooms) {
				if (room instanceof PitRoom) {
					int result;
					do {
						result = pointToCell(room.random());
					} while (traps.get(result) != null
							|| findMob(result) != null
							|| heaps.get(result) != null);
					return result;
				}
			}
		}
		
		return super.fallCell( false );
	}



	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( "rooms", rooms );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		rooms = new ArrayList<>( (Collection<Room>) ((Collection<?>) bundle.getCollection( "rooms" )) );
		for (Room r : rooms) {
			r.onLevelLoad( this );
			if (r instanceof EntranceRoom ){
				roomEntrance = r;
			} else if (r instanceof ExitRoom ){
				roomExit = r;
			}
		}
	}
	
}
