/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
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

package com.trashboxbobylev.summoningpixeldungeon.sprites;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;

public class BlasterSprite extends MobSprite {
    private int attackPos;

    public BlasterSprite() {
        super();

        texture( Assets.BLASTER );

        TextureFilm frames = new TextureFilm( texture, 15, 16 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0 );

        run = idle.clone();

        attack = new Animation( 2, false );
        attack.frames( frames, 1, 2, 3 );

        die = idle.clone();

        play( idle );
    }

    @Override
    public void attack(int cell) {
        attackPos = cell;
        super.attack(cell);
    }

    @Override
    public void onComplete(Animation anim) {
        super.onComplete(anim);

        if (anim == attack){
            if (Dungeon.level.heroFOV[ch.pos] || Dungeon.level.heroFOV[attackPos]){
                parent.add(new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(attackPos)));
            }
        }
    }
}