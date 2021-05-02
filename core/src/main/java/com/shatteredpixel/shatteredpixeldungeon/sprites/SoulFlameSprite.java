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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.SoulFlame;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class SoulFlameSprite extends MobSprite {

	public SoulFlameSprite() {
		super();
		
		texture( Assets.Sprites.SOUL_FLAME );
		
		TextureFilm frames = new TextureFilm( texture, 12, 14 );
		
		idle = new Animation( 10, true );
		idle.frames( frames, 0, 1 );
		
		run = new Animation( 12, true );
		run.frames( frames, 0, 1, 2, 3 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 4, 5, 6 );

        zap = attack.clone();
		
		die = new Animation( 10, false );
		die.frames( frames, 7, 8, 9, 10, 11, 12, 13 );
		
		play( idle );
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		add( State.FROSTBURNING );
	}
	
	@Override
	public void die() {
		super.die();
		remove( State.FROSTBURNING );
	}

    public void zap( int cell ) {

        turnTo( ch.pos , cell );
        play( zap );

        MagicMissile.boltFromChar( parent,
                MagicMissile.FROST,
                this,
                cell,
                new Callback() {
                    @Override
                    public void call() {
                        ((SoulFlame)ch).onZapComplete();
                    }
                } );
        Sample.INSTANCE.play( Assets.Sounds.GHOST );
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
		return 0xFF8cf6ff;
	}
}
