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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Crumbling extends CloakGlyph{
    private static ItemSprite.Glowing EARTHY = new ItemSprite.Glowing( 0x87471d);

    {
        resistances.add(Paralysis.class);
        resistances.add(Cripple.class);
    }

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {

    }

    @Override
    public void onUncloaking(CloakOfShadows cloak, Char defender) {
        PathFinder.buildDistanceMap( defender.pos, BArray.not( Dungeon.level.solid, null ), 2 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE && i != defender.pos) {
                Char mob = Actor.findChar(i);
                if (mob instanceof Mob && mob.alignment == Char.Alignment.ENEMY){
                    mob.damage(Math.round(1.5f*Dungeon.chapterNumber()*efficiency()), new WandOfBlastWave());
                    Buff.affect(mob, ZeroDefense.class, 7.5f * efficiency());
                    Buff.affect(mob, Paralysis.class, 1f + efficiency());
                }
                CellEmitter.get( i ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
            }
        }
        Camera.main.shake( 3, 0.7f );
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return EARTHY;
    }

    public static class ZeroDefense extends DummyBuff{
        {
            type = buffType.NEGATIVE;
        }

        @Override
        public int icon() {return BuffIndicator.VULNERABLE;}

        @Override
        public void tintIcon(Image icon) {icon.hardlight(2f, 0f, 0f);}
    }
}
