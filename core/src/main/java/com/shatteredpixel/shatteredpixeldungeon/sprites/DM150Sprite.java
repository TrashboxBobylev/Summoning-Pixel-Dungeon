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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Robo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DM150Sprite extends MobSprite {
    public DM150Sprite() {
        super();

        texture( Assets.Sprites.DM150 );

        TextureFilm frames = new TextureFilm( texture, 22, 20 );

        idle = new Animation( 10, true );
        idle.frames( frames, 0, 1 );

        run = new Animation( 10, true );
        run.frames( frames, 2, 3 );

        attack = new Animation( 15, false );
        attack.frames( frames, 4, 5, 6 );

        zap = attack.clone();

        die = new Animation( 20, false );
        die.frames( frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8 );

        play( idle );
    }

    @Override
    public void onComplete( Animation anim ) {

        super.onComplete( anim );

        if (anim == die) {
            emitter().burst( Speck.factory( Speck.WOOL ), 15 );
        }
        if (anim == zap) {
            idle();
        }
    }

    public void zap( int cell ) {

        turnTo(ch.pos, cell);
        play(zap);
        new Item().throwSound();
        Sample.INSTANCE.play( Assets.Sounds.CHAINS );
        parent.add(new Chains(center(), DungeonTilemap.raisedTileCenterToWorld(cell), new Callback() {
            @Override
            public void call() {
                ((Robo)ch).onZapComplete();
            }
        }));
    }

    @Override
    public int blood() {
        return 0xFFFFFF88;
    }
}
