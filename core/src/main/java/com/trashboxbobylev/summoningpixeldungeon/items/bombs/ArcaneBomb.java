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

package com.trashboxbobylev.summoningpixeldungeon.items.bombs;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.FireKeeper;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.GooWarn;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Miasma;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.DoomCloud;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ElmoParticle;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ShadowParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.Heap;
import com.trashboxbobylev.summoningpixeldungeon.levels.Level;
import com.trashboxbobylev.summoningpixeldungeon.levels.Terrain;
import com.trashboxbobylev.summoningpixeldungeon.levels.features.Chasm;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ArcaneBomb extends Bomb {
	
	{
		image = ItemSpriteSheet.ARCANE_BOMB;
		fuseDelay = 8;
	}

	private static final int RADIUS = 5;

	public static ArrayList<Integer> getAreaOfEffect(int cell){
	    ArrayList<Integer> aoe = new ArrayList<>();
	    int bx = cell % Dungeon.level.width();
	    int by = cell / Dungeon.level.width();
	    for (int i = 0; i < Dungeon.level.map.length; i++){
	        int dx = bx - (i % Dungeon.level.width());
	        int dy = by - (i / Dungeon.level.width());
	        if (dx*dx + dy*dy <= RADIUS*RADIUS) aoe.add(i);
        }
	    return aoe;
    }
	
	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		if (fuse != null){
            ArrayList<Integer> aoe = getAreaOfEffect(cell);
			for (int i : aoe) {
			    GameScene.add(Blob.seed(i, 9, GooWarn.class));
			}
		}
	}
	
	@Override
	public boolean explodesDestructively() {
		return false;
	}
	
	@Override
	public void explode(int cell) {
		super.explode(cell);
		
		ArrayList<Char> affected = new ArrayList<>();

        ArrayList<Integer> aoe = getAreaOfEffect(cell);
        if (!Dungeon.bossLevel()) {
            Level.set(cell, Terrain.CHASM);
            GameScene.updateMap(cell);
            for (int k : PathFinder.NEIGHBOURS4){
                Level.set(cell + k, Terrain.CHASM);
                GameScene.updateMap(cell+k);
            }
        }
		for (int i : aoe) {
				if (Dungeon.level.heroFOV[i]) {
					CellEmitter.get(i).burst(ShadowParticle.UP, 10);
				}

				if (Dungeon.level.solid[i] && !Dungeon.bossLevel()){
				    float chance = 1;
				    if (Dungeon.level.distance(cell, i) > 3) chance = 0.6f;
				    if (Dungeon.level.distance(cell, i) > 4) chance = 0.4f;
				    if (Random.Float() < chance){
                        Level.set( i, Terrain.EMPTY );

                    }
                }
                GameScene.updateMap( i );
				Char ch = Actor.findChar(i);
				if (ch != null){
					affected.add(ch);
				}

                Heap heap = Dungeon.level.heaps.get(i);
                if (heap != null)
                    heap.explodeByNuke();

                GameScene.add(Blob.seed(i, Random.NormalIntRange(1000, 1500), Miasma.class));
			}
		
		for (Char ch : affected){
			// 500%/460%/420%/380%/340%/300% bomb damage based on distance, but pierces armor.
			int damage = Math.round(Random.NormalIntRange( Dungeon.depth*5+18, 35 + Dungeon.depth * 10 ));
			float multiplier = 1f - (.08f*Dungeon.level.distance(cell, ch.pos));
			ch.damage(Math.round(damage*multiplier), this);
			if (ch == Dungeon.hero && !ch.isAlive()){
				Dungeon.fail(ArcaneBomb.class);
			}
		}
        DoomCloud.hit(cell);
		for (int k = 0; k < 20; k++) GameScene.flash(0xFFFFFFFF);

		Dungeon.observe();
        Sample.INSTANCE.play( Assets.SND_BLAST, 9 );
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (35 + 50);
	}
}
