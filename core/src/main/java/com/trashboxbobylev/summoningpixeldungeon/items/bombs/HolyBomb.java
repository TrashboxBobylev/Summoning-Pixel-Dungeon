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
import com.trashboxbobylev.summoningpixeldungeon.effects.Flare;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ShadowParticle;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HolyBomb extends Bomb {
	
	{
		image = ItemSpriteSheet.HOLY_BOMB;
		fuseDelay = 0;
	}

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
	public void explode(int cell) {
		super.explode(cell);
		
		if (Dungeon.level.heroFOV[cell]) {
			new Flare(10, 64).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f);
		}
		
		ArrayList<Char> affected = new ArrayList<>();
		
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 1 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char ch = Actor.findChar(i);
				if (ch != null) {
					affected.add(ch);
				}
			}
		}
		
		for (Char ch : affected){
            int damage = Math.round(Random.NormalIntRange( Dungeon.depth+5, 10 + Dungeon.depth * 2 ) * 0.8f);
			if (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains(Char.Property.DEMONIC)){
				ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
				
				//bomb deals an additional 20% damage to unholy enemies in a 5x5 range
                damage *= 1.2f;
			}
            ch.damage(damage, this);
		}
		
		Sample.INSTANCE.play( Assets.SND_READ );
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (35 + 40);
	}
}
