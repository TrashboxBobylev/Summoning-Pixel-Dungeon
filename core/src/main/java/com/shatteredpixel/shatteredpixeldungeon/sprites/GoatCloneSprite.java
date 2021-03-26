/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

public class GoatCloneSprite extends MobSprite {

	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;

	public GoatCloneSprite() {
		super();
		
		texture(Assets.Sprites.CONJURER );

		TextureFilm film = new TextureFilm( texture, FRAME_WIDTH, FRAME_HEIGHT );

		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

		run = new Animation( 2, true );
		run.frames( film, 0, 0, 0, 0, 0, 0, 0, 1 );

		die = new Animation( 20, false );
		die.frames( film, 0 );

		attack = new Animation( 40, false );
		attack.frames( film, 13, 14, 15, 0 );

		idle();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (flashTime <= 0){
			float interval = (Game.timeTotal % 12 ) /3f;
			tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
					interval > 1 ? Math.max(0, 2-interval): interval,
					interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
		}
	}
	
}
