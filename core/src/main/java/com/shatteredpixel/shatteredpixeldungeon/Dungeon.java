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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MomentumBoots;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Game;
import com.watabou.utils.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class Dungeon {

	//enum of items which have limited spawns, records how many have spawned
	//could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
	public enum LimitedDrops {
		//limited world drops
		STRENGTH_POTIONS,
		UPGRADE_SCROLLS,
		ARCANE_STYLI,

		//Health potion sources
		//enemies
		SWARM_HP,
		NECRO_HP,
		BAT_HP,
		WARLOCK_HP,
		SPECTRE_RAT,
		//Demon spawners are already limited in their spawnrate, no need to limit their health drops
		//alchemy
		COOKING_HP,
		BLANDFRUIT_SEED,

		//Other limited enemy drops
		SLIME_WEP,
		SKELE_WEP,
		THEIF_MISC,
		GUARD_ARM,
		SHAMAN_WAND,
		DM200_EQUIP,
		GOLEM_EQUIP,
		PHANTOM_EQUIP,

        //guaranted staff
        STAFF,

		//containers
		DEW_VIAL,
		VELVET_POUCH,
		SCROLL_HOLDER,
		POTION_BANDOLIER,
		MAGICAL_HOLSTER;

		public int count = 0;

		//for items which can only be dropped once, should directly access count otherwise.
		public boolean dropped(){
			return count != 0;
		}
		public void drop(){
			count = 1;
		}

		public static void reset(){
			for (LimitedDrops lim : values()){
				lim.count = 0;
			}
		}

		public static void store( Bundle bundle ){
			for (LimitedDrops lim : values()){
				bundle.put(lim.name(), lim.count);
			}
		}

		public static void restore( Bundle bundle ){
			for (LimitedDrops lim : values()){
				if (bundle.contains(lim.name())){
					lim.count = bundle.getInt(lim.name());
				} else {
					lim.count = 0;
				}
				
			}
		}

	}

	public enum GameMode {
		NORMAL("normal", Icons.ENTER),
		SMALL("small", Icons.SHRINKING, 1.1f),
		BIGGER("bigger", Icons.ENLARGEMENT, 1.2f),
		CAVES("caves", Icons.CAVES, 1.09f),
		GAUNTLET("gauntlet", Icons.GAUNTLET, 1.33f),
		EXPLORE( "explore", Icons.EXPLORE, 0f),
		LOL("lol", Icons.GOLD, 0.33f),
		NO_SOU("no_sou", Icons.SOULLESS, 2.0f),
		NO_EXP("no_exp", Icons.NO_EXP, 3.0f),
		HELL("hell", Icons.HELL_CHEST, 4.0f),
		DIFFICULT("oh_my_is_this_eternity_mode", Icons.DARK_AMU, 1.8f),
		REALTIME("realtime", Icons.REAL_TIME, 2.0f),
		CHAOS("chaos", Icons.CHAOS, 1.33f);

		public String saveName;
		public Icons icon;
		public float scoreMod;

		GameMode(String saveName, Icons icon) {
			this.saveName = saveName;
			this.icon = icon;
			this.scoreMod = 1.0f;
		}

		GameMode(String saveName, Icons icon, float scoreMod) {
			this.saveName = saveName;
			this.icon = icon;
			this.scoreMod = scoreMod;
		}

		public String desc(){
			return "_" + toString() + "_: " + Messages.get(Dungeon.class, "mode_desc_" + saveName) + "\n" +
					Messages.get(Dungeon.class, "score", new DecimalFormat("#.##").format(scoreMod));
		}

		public boolean isNormal() {
			return !(this.equals(GAUNTLET));
		}

		@Override
		public String toString() {
			return Messages.get(Dungeon.class, "mode_name_" + saveName);
		}
	}

	public static Conducts.ConductStorage challenges = new Conducts.ConductStorage();

	public static Conducts.Conduct challenge() {
		if (challenges.isConductedAtAll())
			return challenges.getFirst();
		return null;
	}

	public static GameMode mode;
	public static int mobsToChampion;

	public static Hero hero;
	public static Level level;

	public static QuickSlot quickslot = new QuickSlot();
	
	public static int depth;
	public static int gold;
	public static int energy;
	
	public static HashSet<Integer> chapters;

	public static SparseArray<ArrayList<Item>> droppedItems;
	public static SparseArray<ArrayList<Item>> portedItems;

	public static int version;

	public static long seed;
	
	public static void init() {

		version = Game.versionCode;
		challenges = new Conducts.ConductStorage(SPDSettings.challenges());

		seed = DungeonSeed.randomSeed();

		Actor.clear();
		Actor.resetNextID();
		MomentumBoots.instance = null;
		
		Random.pushGenerator( seed );

			Scroll.initLabels();
			Potion.initColors();

			SpecialRoom.initForRun();
			SecretRoom.initForRun();

		Random.resetGenerators();
		
		Statistics.reset();
		Notes.reset();

		quickslot.reset();
		QuickSlotButton.reset();
		
		depth = 0;
		gold = 0;
		mobsToChampion = -1;
		if (Dungeon.mode == GameMode.GAUNTLET){
			gold = 100;
		}

		droppedItems = new SparseArray<>();
		portedItems = new SparseArray<>();

		LimitedDrops.reset();
		
		chapters = new HashSet<>();
		
		Ghost.Quest.reset();
		Wandmaker.Quest.reset();
		Blacksmith.Quest.reset();
		Imp.Quest.reset();

		Generator.fullReset();
		hero = new Hero();
		hero.live();
		
		Badges.reset();
		
		GamesInProgress.selectedClass.initHero( hero );
	}

	public static boolean isChallenged( Conducts.Conduct mask ) {
		return challenges.isConducted(mask);
	}
	
	public static Level newLevel() {
		
		Dungeon.level = null;
		Actor.clear();
		
		depth++;
		if (depth > Statistics.deepestFloor) {
			Statistics.deepestFloor = depth;

			Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
		}
		
		Level level;

		switch(mode){
			case NORMAL: default:
				level = chooseNormalLevel();
			break;
			case GAUNTLET:
				level = new ArenaLevel();
		}
		
		level.create();
		
		Statistics.qualifiedForNoKilling = !bossLevel();
		
		return level;
	}

	public static Level chooseNormalLevel(){
		if (Dungeon.depth > 0 && Dungeon.depth < Dungeon.chapterSize()) {
			return new SewerLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()){
			return new SewerBossLevel();
		}
		if (Dungeon.depth > Dungeon.chapterSize() && Dungeon.depth < Dungeon.chapterSize()*2) {
			return new PrisonLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()*2){
			return new PrisonBossLevel();
		}
		if (Dungeon.depth > Dungeon.chapterSize()*2 && Dungeon.depth < Dungeon.chapterSize()*3) {
			return new CavesLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()*3){
			return new CavesBossLevel();
		}
		if (Dungeon.depth > Dungeon.chapterSize()*3 && Dungeon.depth < Dungeon.chapterSize()*4) {
			return new CityLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()*4){
			return new NewCityBossLevel();
		}
		if (Dungeon.depth > Dungeon.chapterSize()*4 && Dungeon.depth < Dungeon.chapterSize()*5) {
			return new HallsLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()*5){
			return new NewHallsBossLevel();
		}
		if (Dungeon.depth == Dungeon.chapterSize()*5+1){
			return new LastLevel();
		}
		return new AbyssLevel();
	}
	
	public static void resetLevel() {
		
		Actor.clear();
		
		level.reset();
		switchLevel( level, level.entrance );
	}

	public static long seedCurDepth(){
		return seedForDepth(depth);
	}

	public static long seedForDepth(int depth){
		Random.pushGenerator( seed );

			for (int i = 0; i < depth; i ++) {
				Random.Long(); //we don't care about these values, just need to go through them
			}
			long result = Random.Long();

		Random.popGenerator();
		return result;
	}
	
	public static boolean shopOnLevel() {
		if (SPDSettings.bigdungeon()){
			return Dungeon.depth % 5 == 0 && Dungeon.depth != 25 && Dungeon.depth != 30;
		}
		return (Dungeon.depth - 1) % Dungeon.chapterSize() == 0 && Dungeon.depth != Dungeon.chapterSize()*4+1 && Dungeon.depth != 1;
	}
	
	public static boolean bossLevel() {
		return bossLevel( depth );
	}

	public static int chapterSize(){
		int i;
		if (Dungeon.mode == GameMode.GAUNTLET) {
			i = 8;
		}
		else if (SPDSettings.smalldungeon()){
			i = 4;
		}
		else if (SPDSettings.bigdungeon()){
			i = 6;
		}
		else {
			i = 5;
		}
		if (Dungeon.isChallenged(Conducts.Conduct.HUGE)) i *= 1.5f;
		return i;
	}

	public static int chapterNumber(){
		return Math.max(depth / chapterSize(), 0);
	}

	// returns the depth if it is always in normal mode
	public static int scaledDepth(){
		return Math.round(depth * 1f / Dungeon.chapterSize() * 5f);
	}
	
	public static boolean bossLevel( int depth ) {
		return depth % Dungeon.chapterSize() == 0;
	}

	public static void switchLevel( final Level level, int pos ) {
		
		if (pos == -2){
			pos = level.exit;
		} else if (pos < 0 || pos >= level.length()){
			pos = level.entrance;
		}
		
		PathFinder.setMapSize(level.width(), level.height());
		
		Dungeon.level = level;
		Mob.restoreAllies( level, pos );
		Actor.init();

		level.addRespawner();

		hero.pos = pos;
		
		for(Mob m : level.mobs){
			if (m.pos == hero.pos){
				//displace mob
				for(int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(m.pos+i) == null && level.passable[m.pos + i]){
						m.pos += i;
						break;
					}
				}
			}
		}
		
		Light light = hero.buff( Light.class );
		hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );
		
		hero.curAction = hero.lastAction = null;

		observe();
		try {
			saveAll();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
		}
	}

	public static void dropToChasm( Item item ) {
		int depth = Dungeon.depth + 1;
		ArrayList<Item> dropped = Dungeon.droppedItems.get( depth );
		if (dropped == null) {
			Dungeon.droppedItems.put( depth, dropped = new ArrayList<>() );
		}
		dropped.add( item );
	}

	public static boolean posNeeded() {
		if (SPDSettings.bigdungeon()){
			return Dungeon.depth == 2 + Dungeon.chapterSize()*(LimitedDrops.STRENGTH_POTIONS.count/2) ||
					Dungeon.depth == 4 + Dungeon.chapterSize()*(LimitedDrops.STRENGTH_POTIONS.count/2);
		}
		else {
			//2 POS each floor set
			int posLeftThisSet = 2 - (LimitedDrops.STRENGTH_POTIONS.count - (depth / Dungeon.chapterSize()) * 2);
			if (posLeftThisSet <= 0) return false;

			int floorThisSet = (depth % Dungeon.chapterSize());

			//pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
			int targetPOSLeft = Dungeon.chapterSize() / 2 - floorThisSet/2;
			if (floorThisSet % (Dungeon.chapterSize() / 2) == 1 && (Random.Int(2) == 0 && Dungeon.chapterSize() == 4)) targetPOSLeft --;

			return targetPOSLeft < posLeftThisSet;
		}

	}
	
	public static boolean souNeeded() {
		int souLeftThisSet;
		int amount = SPDSettings.bigdungeon() ? 4 : 3;
		//3 SOU each floor set, 1.5 (rounded) on forbidden runes challenge
		souLeftThisSet = amount - (LimitedDrops.UPGRADE_SCROLLS.count - (depth / Dungeon.chapterSize()) * amount);
		if (souLeftThisSet <= 0) return false;

		int floorThisSet = (depth % Dungeon.chapterSize());
		//chance is floors left / scrolls left
		return Random.Int(Dungeon.chapterSize() - floorThisSet) < souLeftThisSet;
	}
	
	public static boolean asNeeded() {
		//1 AS each floor set
		int asLeftThisSet = 1 - (LimitedDrops.ARCANE_STYLI.count - (depth / Dungeon.chapterSize()));
		if (asLeftThisSet <= 0) return false;

		int floorThisSet = (depth % Dungeon.chapterSize());
		//chance is floors left / scrolls left
		return Random.Int(Dungeon.chapterSize() - floorThisSet) < asLeftThisSet;
	}
	
	private static final String VERSION		= "version";
	private static final String SEED		= "seed";
	private static final String CHALLENGES	= "challenges";
	private static final String MODE        = "mode";
	private static final String HERO		= "hero";
	private static final String GOLD		= "gold";
	private static final String ENERGY		= "energy";
	private static final String DEPTH		= "depth";
	private static final String MOBS_TO_CHAMPION	= "mobs_to_champion";
	private static final String DROPPED     = "dropped%d";
	private static final String PORTED      = "ported%d";
	private static final String LEVEL		= "level";
	private static final String LIMDROPS    = "limited_drops";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";
	
	public static void saveGame( int save ) {
		try {
			Bundle bundle = new Bundle();

			version = Game.versionCode;
			bundle.put( VERSION, version );
			bundle.put( SEED, seed );
			challenges.storeInBundle(bundle);
			bundle.put( MOBS_TO_CHAMPION, mobsToChampion );
			bundle.put( HERO, hero );
			bundle.put( GOLD, gold );
			bundle.put( ENERGY, energy );
			bundle.put( DEPTH, depth );
			bundle.put( MODE, mode);

			for (int d : droppedItems.keyArray()) {
				bundle.put(Messages.format(DROPPED, d), droppedItems.get(d));
			}
			
			for (int p : portedItems.keyArray()){
				bundle.put(Messages.format(PORTED, p), portedItems.get(p));
			}

			quickslot.storePlaceholders( bundle );

			Bundle limDrops = new Bundle();
			LimitedDrops.store( limDrops );
			bundle.put ( LIMDROPS, limDrops );
			
			int count = 0;
			int[] ids = new int[chapters.size()];
			for (Integer id : chapters) {
				ids[count++] = id;
			}
			bundle.put( CHAPTERS, ids );
			
			Bundle quests = new Bundle();
			Ghost		.Quest.storeInBundle( quests );
			Wandmaker	.Quest.storeInBundle( quests );
			Blacksmith	.Quest.storeInBundle( quests );
			Imp			.Quest.storeInBundle( quests );
			bundle.put( QUESTS, quests );
			
			SpecialRoom.storeRoomsInBundle( bundle );
			SecretRoom.storeRoomsInBundle( bundle );
			
			Statistics.storeInBundle( bundle );
			Notes.storeInBundle( bundle );
			Generator.storeInBundle( bundle );
			
			Scroll.save( bundle );
			Potion.save( bundle );

			Actor.storeNextID( bundle );
			
			Bundle badges = new Bundle();
			Badges.saveLocal( badges );
			bundle.put( BADGES, badges );
			
			FileUtils.bundleToFile( GamesInProgress.gameFile(save), bundle);
			
		} catch (IOException e) {
			GamesInProgress.setUnknown( save );
			ShatteredPixelDungeon.reportException(e);
		}
	}
	
	public static void saveLevel( int save ) throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, level );
		
		FileUtils.bundleToFile(GamesInProgress.depthFile( save, depth), bundle);
	}
	
	public static void saveAll() throws IOException {
		if (hero != null && hero.isAlive()) {
			
			Actor.fixTime();
			saveGame( GamesInProgress.curSlot );
			saveLevel( GamesInProgress.curSlot );

			GamesInProgress.set( GamesInProgress.curSlot, depth, challenges, mode, hero );

		}
	}
	
	public static void loadGame( int save ) throws IOException {
		loadGame( save, true );
	}
	
	public static void loadGame( int save, boolean fullLoad ) throws IOException {
		
		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.gameFile( save ) );

		version = bundle.getInt( VERSION );

		seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.randomSeed();

		Actor.restoreNextID( bundle );

		quickslot.reset();
		QuickSlotButton.reset();
		challenges.restoreFromBundle(bundle);

		try {
			Dungeon.mode = GameMode.valueOf(bundle.getString(MODE));
		} catch (IllegalArgumentException exception){
			Dungeon.mode = SPDSettings.getBoolean(SPDSettings.KEY_BIGGER, false) ? GameMode.BIGGER :
					(SPDSettings.getBoolean(SPDSettings.KEY_SMALLER, false) ? GameMode.SMALL :
							GameMode.NORMAL);

		}
		Dungeon.mobsToChampion = bundle.getInt( MOBS_TO_CHAMPION );
		
		Dungeon.level = null;
		Dungeon.depth = -1;
		
		Scroll.restore( bundle );
		Potion.restore( bundle );

		quickslot.restorePlaceholders( bundle );
		
		if (fullLoad) {
			
			LimitedDrops.restore( bundle.getBundle(LIMDROPS) );

			chapters = new HashSet<>();
			int[] ids = bundle.getIntArray( CHAPTERS );
			if (ids != null) {
				for (int id : ids) {
					chapters.add( id );
				}
			}
			
			Bundle quests = bundle.getBundle( QUESTS );
			if (!quests.isNull()) {
				Ghost.Quest.restoreFromBundle( quests );
				Wandmaker.Quest.restoreFromBundle( quests );
				Blacksmith.Quest.restoreFromBundle( quests );
				Imp.Quest.restoreFromBundle( quests );
			} else {
				Ghost.Quest.reset();
				Wandmaker.Quest.reset();
				Blacksmith.Quest.reset();
				Imp.Quest.reset();
			}
			
			SpecialRoom.restoreRoomsFromBundle(bundle);
			SecretRoom.restoreRoomsFromBundle(bundle);
		}
		
		Bundle badges = bundle.getBundle(BADGES);
		if (!badges.isNull()) {
			Badges.loadLocal( badges );
		} else {
			Badges.reset();
		}
		
		Notes.restoreFromBundle( bundle );
		
		hero = null;
		hero = (Hero)bundle.get( HERO );
		
		gold = bundle.getInt( GOLD );
		energy = bundle.getInt( ENERGY );
		depth = bundle.getInt( DEPTH );
		
		Statistics.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

		droppedItems = new SparseArray<>();
		portedItems = new SparseArray<>();
		for (int i=1; i <= Dungeon.chapterSize()*5+1; i++) {
			
			//dropped items
			ArrayList<Item> items = new ArrayList<>();
			if (bundle.contains(Messages.format( DROPPED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( DROPPED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				droppedItems.put( i, items );
			}
			
			//ported items
			items = new ArrayList<>();
			if (bundle.contains(Messages.format( PORTED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( PORTED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				portedItems.put( i, items );
			}
		}
	}
	
	public static Level loadLevel( int save ) throws IOException {
		
		Dungeon.level = null;
		Actor.clear();
		
		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.depthFile( save, depth)) ;
		
		Level level = (Level)bundle.get( LEVEL );
		
		if (level == null){
			throw new IOException();
		} else {
			return level;
		}
	}
	
	public static void deleteGame( int save, boolean deleteLevels ) {
		
		FileUtils.deleteFile(GamesInProgress.gameFile(save));
		
		if (deleteLevels) {
			FileUtils.deleteDir(GamesInProgress.gameFolder(save));
		}
		
		GamesInProgress.delete( save );
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.depth = bundle.getInt( DEPTH );
		info.version = bundle.getInt( VERSION );
		info.challenges = new Conducts.ConductStorage();
		info.challenges.restoreFromBundle(bundle);
		try {
			info.mode = GameMode.valueOf(bundle.getString(MODE));
		} catch (IllegalArgumentException exception){
			info.mode = SPDSettings.getBoolean(SPDSettings.KEY_BIGGER, false) ? GameMode.BIGGER :
					(SPDSettings.getBoolean(SPDSettings.KEY_SMALLER, false) ? GameMode.SMALL :
							GameMode.NORMAL);

		}
		Hero.preview( info, bundle.getBundle( HERO ) );
		Statistics.preview( info, bundle );
	}
	
	public static void fail( Class cause ) {
		if (Dungeon.mode != GameMode.EXPLORE) {
			if (hero.belongings.getItem(Ankh.class) == null) {
				if (Dungeon.mode == GameMode.GAUNTLET) {
					Rankings.INSTANCE.submit(false, Gold.class);
				} else Rankings.INSTANCE.submit(false, cause);
			}
		}
	}
	
	public static void win( Class cause ) {

		hero.belongings.identify();

		Rankings.INSTANCE.submit( true, cause );
	}

	//TODO hero max vision is now separate from shadowcaster max vision. Might want to adjust.
	public static void observe(){
		int dist = Math.max(Dungeon.hero.viewDistance, 8);
		if (Dungeon.hero.hasTalent(Talent.SHARP_VISION)){
			dist += 1*Dungeon.hero.pointsInTalent(Talent.SHARP_VISION);
		}
		observe( dist+1 );
	}
	
	public static void observe( int dist ) {

		if (level == null) {
			return;
		}
		
		level.updateFieldOfView(hero, level.heroFOV);

		int x = hero.pos % level.width();
		int y = hero.pos / level.width();
	
		//left, right, top, bottom
		int l = Math.max( 0, x - dist );
		int r = Math.min( x + dist, level.width() - 1 );
		int t = Math.max( 0, y - dist );
		int b = Math.min( y + dist, level.height() - 1 );
	
		int width = r - l + 1;
		int height = b - t + 1;
		
		int pos = l + t * level.width();
	
		for (int i = t; i <= b; i++) {
			BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
			pos+=level.width();
		}
	
		GameScene.updateFog(l, t, width, height);
		
		if (hero.buff(MindVision.class) != null){
			for (Mob m : level.mobs.toArray(new Mob[0])){
				BArray.or( level.visited, level.heroFOV, m.pos - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos, 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos - 1 + level.width(), 3, level.visited );
				//updates adjacent cells too
				GameScene.updateFog(m.pos, 2);
			}
		}
		
		if (hero.buff(Awareness.class) != null){
			for (Heap h : level.heaps.valueList()){
				BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
				GameScene.updateFog(h.pos, 2);
			}
		}

		for (TalismanOfForesight.CharAwareness c : hero.buffs(TalismanOfForesight.CharAwareness.class)){
			if (Dungeon.depth != c.depth) continue;
			Char ch = (Char) Actor.findById(c.charID);
			if (ch == null) continue;
			BArray.or( level.visited, level.heroFOV, ch.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(ch.pos, 2);
		}

		for (TalismanOfForesight.HeapAwareness h : hero.buffs(TalismanOfForesight.HeapAwareness.class)){
			if (Dungeon.depth != h.depth) continue;
			BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(h.pos, 2);
		}

		GameScene.afterObserve();
	}

	//we store this to avoid having to re-allocate the array with each pathfind
	private static boolean[] passable;

	private static void setupPassable(){
		if (passable == null || passable.length != Dungeon.level.length())
			passable = new boolean[Dungeon.level.length()];
		else
			BArray.setFalse(passable);
	}

	public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars){
		return findPassable(ch, pass, vis, chars, chars);
	}

	public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars, boolean considerLarge){

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (considerLarge && Char.hasProp(ch, Char.Property.LARGE)){
			BArray.and( pass, Dungeon.level.openSpace, passable );
		}

		if (chars) {
			for (Char c : Actor.chars()) {
				if (vis[c.pos]) {
					passable[c.pos] = false;
				}
			}
		}

		return passable;
	}

	public static PathFinder.Path findPath(Char ch, int to, boolean[] pass, boolean[] vis, boolean chars) {

		return PathFinder.find( ch.pos, to, findPassable(ch, pass, vis, chars) );

	}
	
	public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars ) {

		if (Dungeon.level.adjacent( ch.pos, to )) {
			return Actor.findChar( to ) == null && (pass[to] || Dungeon.level.avoid[to]) ? to : -1;
		}

		return PathFinder.getStep( ch.pos, to, findPassable(ch, pass, visible, chars) );

	}
	
	public static int flee( Char ch, int from, boolean[] pass, boolean[] visible, boolean chars ) {

		//only consider chars impassable if our retreat path runs into them
		boolean[] passable = findPassable(ch, pass, visible, false, true);
		passable[ch.pos] = true;
		
		int step =  PathFinder.getStepBack( ch.pos, from, passable );
		while (step != -1 && Actor.findChar(step) != null && chars){
			passable[step] = false;
			step = PathFinder.getStepBack( ch.pos, from, passable );
		}
		return step;
		
	}

}
