/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2021 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Hacatu;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class HacatuSprite extends MobSprite {

    ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    public HacatuSprite() {
        super();

        texture( Assets.Sprites.ROGUE );

        SmartTexture texture = TextureCache.get( Assets.Sprites.ROGUE );
        TextureFilm tiers = new TextureFilm(texture, texture.width, 15);

        TextureFilm film = new TextureFilm( tiers, 6, 12, 15 );

        idle = new Animation( 1, true );
        idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

        run = new Animation( 20, true );
        run.frames( film, 2, 3, 4, 5, 6, 7 );

        die = new Animation( 20, false );
        die.frames( film, 8, 9, 10, 11, 12, 11 );

        attack = new Animation( 15, false );
        attack.frames( film, 13, 14, 15, 0 );

        zap = attack.clone();

        play( idle );
    }

    private void arc( Char che ) {

        ((Hacatu)ch).affected.add( che );

        int dist;
        if (che.isWet() && !che.flying)
            dist = 2;
        else
            dist = 1;

        PathFinder.buildDistanceMap( che.pos, BArray.not( Dungeon.level.solid, null ), dist );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE){
                Char n = Actor.findChar( i );
                if (n == ch && PathFinder.distance[i] > 1)
                    //the minion is only zapped if they are adjacent
                    continue;
                else if (n != null && !((Hacatu)ch).affected.contains( n )) {
                    arcs.add(new Lightning.Arc(che.sprite.center(), n.sprite.center()));
                    arc(n);
                }
            }
        }
    }

    public void zap( int pos ) {

       /* Char enemy = Actor.findChar(pos);

        if (enemy != null) {
            parent.add(new Lightning(center(), enemy.sprite.destinationCenter(), (Shaman) ch));
        } else {
            parent.add(new Lightning(center(), pos, (Shaman) ch));
        }*/
        ((Hacatu)ch).affected.clear();
        arcs.clear();

        Char che = Actor.findChar(pos);
        if (che != null) {
            arcs.add( new Lightning.Arc(center(), che.sprite.center()));
            arc(che);
        } else {
            arcs.add( new Lightning.Arc(center(), DungeonTilemap.raisedTileCenterToWorld(pos)));
            CellEmitter.center(pos).burst( SparkParticle.FACTORY, 3 );
        }

        //don't want to wait for the effect before processing damage.
        parent.addToFront( new Lightning( arcs, null ) );

        turnTo( ch.pos, pos );
        play( zap );
    }
}
