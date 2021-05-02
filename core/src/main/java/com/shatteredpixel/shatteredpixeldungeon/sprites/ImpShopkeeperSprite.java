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
import com.watabou.noosa.particles.PixelParticle;

public class ImpShopkeeperSprite extends MobSprite {

	private PixelParticle coin;

	public ImpShopkeeperSprite() {
		super();
		
		texture( Assets.Sprites.MERCHANT );
		TextureFilm film = new TextureFilm( texture, 16, 16 );
		
		idle = new Animation( 10, true );
		idle.frames( film, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 );

		die = new Animation( 20, false );
		die.frames( film, 0 );

		run = idle.clone();

		attack = idle.clone();
		
		idle();
	}
}
