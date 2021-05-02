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
import com.watabou.noosa.TextureFilm;

public class GrayRatSprite extends MobSprite {

	public GrayRatSprite() {
		super();
		
		texture( Assets.Sprites.RAT );
		
		TextureFilm frames = new TextureFilm( texture, 16, 15 );

        idle = new Animation( 2, true );
        idle.frames( frames, 48, 48, 48, 49 );

        run = new Animation( 10, true );
        run.frames( frames, 54, 55, 56, 57, 58 );

        attack = new Animation( 15, false );
        attack.frames( frames, 50, 51, 52, 53, 48 );

        die = new Animation( 10, false );
        die.frames( frames, 59, 60, 61, 62 );
		
		play( idle );
	}
}
