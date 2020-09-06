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
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

public class ChaosSaberSprite extends MobSprite {
    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;

    public ChaosSaberSprite() {
        super();

        texture(Assets.CHAOS_SABER);
        TextureFilm film = new TextureFilm(texture, 12, 12);

        idle = new Animation(1, true);
        idle.frames(film, 0);

        run = idle.clone();

        die = new Animation(20, false);
        die.frames(film, 4);

        attack = new Animation(4, false);
        attack.frames(film, 1, 2, 3, 4);

        idle();

    }
}
