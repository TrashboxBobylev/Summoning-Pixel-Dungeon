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
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Chaosstone;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.ArenaPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ArenaLevel extends RegularLevel {

    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;
    }

    protected Painter painter() {
        return new ArenaPainter()
                .setWater( 0.35f, 4 )
                .setGrass( 0.25f, 3 )
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    private int arenaDoor;
    private boolean enteredArena = false;
    private boolean keyDropped = false;
    private int stairs = 0;

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{
                FrostTrap.class, SummoningTrap.class, StormTrap.class, CorrosionTrap.class, CalamityTrap.class, ConfusionTrap.class,
                RockfallTrap.class, FlashingTrap.class, WornDartTrap.class, GuardianTrap.class, AbyssFlameTrap.class, ExplosiveTrap.class,
                DisarmingTrap.class, WraithTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1,
                1, 1, 1, 1 };
    }

    private static final String DOOR	= "door";
    private static final String ENTERED	= "entered";
    private static final String DROPPED	= "droppped";
    private static final String STAIRS	= "stairs";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( DOOR, arenaDoor );
        bundle.put( ENTERED, enteredArena );
        bundle.put( DROPPED, keyDropped );
        bundle.put( STAIRS, stairs );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        arenaDoor = bundle.getInt( DOOR );
        enteredArena = bundle.getBoolean( ENTERED );
        keyDropped = bundle.getBoolean( DROPPED );
        stairs = bundle.getInt( STAIRS );
        roomExit = roomEntrance;
    }

    @Override
    protected int nTraps() {
        return (int) (super.nTraps());
    }

    @Override
    public int nMobs() {
        return (int) (super.nMobs()/2.2f);
    }

    @Override
    protected Builder builder() {
        return new LoopBuilder()
                .setLoopShape( 2 ,
                        Random.Float(0f, 0.6f),
                        Random.Float(0f, 0.6f));
    }

    public Actor addRespawner() {
        return null;
    }

    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();
        initRooms.add ( roomEntrance = new EntranceRoom());
        initRooms.add( new ShopRoom());
        initRooms.add( roomExit = new SewerBossExitRoom());

        for (int i = 0; i < 2 + Dungeon.depth * 2 / Dungeon.chapterSize(); i++) {
            StandardRoom s = StandardRoom.createRoom();
            initRooms.add(s);
        }

        return initRooms;
    }

    @Override
    public void pressCell(int cell) {
        super.pressCell(cell);

        for (Heap heap : Dungeon.level.heaps.valueList().toArray(new Heap[0])){
            if (heap.pos == cell){
                for (Item item : heap.items){
                    if (item instanceof SkeletonKey){
                        keyDropped = true;
                    }
                }
            }
        }
    }

    @Override
    public void create() {

        for (int i = 0; i < (Dungeon.depth - Dungeon.chapterSize()*5 + 1) / Dungeon.chapterSize(); i++){
            addItemToSpawn(new Chaosstone());
            if (Random.Int(2) == 0) addItemToSpawn(new Chaosstone());
        }
        if (Dungeon.depth % 2 == 0) addItemToSpawn(new ChargrilledMeat());

        super.create();
    }

    @Override
    protected void createItems() {
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
            int pos;
            do {
                pos = pointToCell(roomEntrance.random());
            } while (pos == entrance);
            drop( item, pos ).setHauntedIfCursed().type = Heap.Type.REMAINS;
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

        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell( Char ch ) {
        int cell;
        do {
            cell = pointToCell( roomEntrance.random() );
        } while (!passable[cell]
                || (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
                || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    public void seal() {
        if (entrance != 0) {

            super.seal();

            set( entrance, Terrain.CHASM );
            GameScene.updateMap( entrance );
            GameScene.ripple( entrance );

            stairs = entrance;
            entrance = 0;
        }
    }

    @Override
    public void occupyCell(Char ch) {
        super.occupyCell(ch);

        if (ch == Dungeon.hero && !roomEntrance.inside(cellToPoint(ch.pos)) && !locked && !keyDropped){
            seal();
        }
    }

    @Override
    public void unseal() {
        if (stairs != 0) {

            super.unseal();

            entrance = stairs;
            stairs = 0;

            set( entrance, Terrain.ENTRANCE );
            GameScene.updateMap( entrance );
        }
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(CityLevel.class, "water_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CityLevel.class, "high_grass_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CityLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(CityLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(CityLevel.class, "sp_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(CityLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CityLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    @Override
    public Group addVisuals( ) {
        super.addVisuals();
        CityLevel.addCityVisuals(this, visuals);
        if (map[exit-1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit-1));
        if (map[exit+1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit+1));
        return visuals;
    }
}
