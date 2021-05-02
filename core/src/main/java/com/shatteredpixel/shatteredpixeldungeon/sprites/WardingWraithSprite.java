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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WardingWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class WardingWraithSprite extends MobSprite {

	public WardingWraithSprite() {
		super();

        texture( Assets.Sprites.ATTUNEMENT_SPIRIT );

        TextureFilm frames = new TextureFilm( texture, 12, 14 );

        idle = new Animation( 8, true );
        idle.frames( frames, 0, 0, 0, 0, 0, 0,0, 1, 2 );

        run = new Animation( 12, true );
        run.frames( frames, 0, 3 );

        attack = new Animation( 12, false );
        attack.frames( frames, 4, 5, 6 );

        zap = attack.clone();

        die = new Animation( 15, false );
        die.frames( frames, 7, 8, 9, 10, 11, 12, 13, 12 );

        play( idle );
	}
	
	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );

		MagicMissile.boltFromChar( parent,
				MagicMissile.BEACON,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((WardingWraith)ch).onZapComplete();
					}
				} );
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

    @Override
    public int blood() {
        return 0xFFFFFFFF;
    }

    @Override
    public void link( Char ch ) {
        super.link( ch );
        add( State.SPIRIT );
    }

    @Override
    public void die() {
        super.die();
        remove( State.SPIRIT );
    }
}
