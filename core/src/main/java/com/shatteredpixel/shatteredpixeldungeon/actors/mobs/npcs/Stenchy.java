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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WandOfStenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StenchHolder;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RoseWraithSprite;
import com.watabou.noosa.audio.Sample;

public class Stenchy extends NPC {
    {
        spriteClass = NecroSkeletonSprite.class;

        alignment = Alignment.ALLY;
        intelligentAlly = true;
        state = HUNTING;
        WANDERING = new Wandering();
        properties.add(Property.BLOB_IMMUNE);
        properties.add(Property.INORGANIC);
        properties.add(Property.ACIDIC);
    }

    public static class NecroSkeletonSprite extends RoseWraithSprite {

        public NecroSkeletonSprite(){
            super();
            brightness(0.25f);
            hardlight(0x1d4636);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            brightness(0.25f);
            hardlight(0x1d4636);
        }

        @Override
        public int blood() {
            return 0x8b009548;
        }
    }

    @Override
    public void damage(int dmg, Object src) {

    }

    @Override
    public int attackSkill( Char target ) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int damageRoll() {
        return -1;
    }

    private class Wandering extends Mob.Wandering{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (buff(StenchHolder.class) == null){
                destroy();
                CellEmitter.get(pos).start( Speck.factory(Speck.STENCH_WAND), 0.02f, 20 );
                sprite.die();
                Sample.INSTANCE.play( Assets.Sounds.BLAST );
                return true;
            } else {
                return super.act(enemyInFOV, justAlerted);
            }
        }

    }

    @Override
    public int attackProc(Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );

        destroy();
        sprite.die();
        CellEmitter.get(pos).start( Speck.factory(Speck.STENCH_WAND), 0.02f, 20 );
        GameScene.add(Blob.seed(pos, 50, WandOfStenchGas.class));
        Sample.INSTANCE.play( Assets.Sounds.BLAST );

        return damage;
    }

    {
        immunities.add(PerfumeGas.Affection.class);
    }
}
