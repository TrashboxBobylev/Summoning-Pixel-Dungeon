/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Wave extends ConjurerSpell {

    {
        image = ItemSpriteSheet.CAMOUFLAGE;
    }

    ConeAOE cone;

    @Override
    public void effect(Ballistica bolt) {

        ArrayList<Char> affectedChars = new ArrayList<>();
        for( int cell : cone.cells ){

            //ignore caster cell
            if (cell == bolt.sourcePos){
                continue;
            }

            Char ch = Actor.findChar( cell );
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        for (int cell : cone.cells){
            Char ch = Actor.findChar( cell );
            if (ch != null) {
                affectedChars.add(ch);
            }
        }
        for (Char ch : affectedChars){
            WandOfBlastWave.throwChar(ch, new Ballistica(bolt.sourcePos, ch.pos, Ballistica.PROJECTILE), 2);
        }
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        Camera.main.shake( 3, 0.7f );
    }

    @Override
    protected void fx( Ballistica bolt, Callback callback ) {
        //need to perform flame spread logic here so we can determine what cells to put flames in.

        // unlimited distance
        int d = 3 + level();
        int dist = Math.min(bolt.dist, d);

        cone = new ConeAOE( bolt,
                d,
                70,
                Ballistica.MAGIC_BOLT);

        //cast to cells at the tip, rather than all cells, better performance.
        for (Ballistica ray : cone.rays){
            ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                    MagicMissile.MAGIC_MISSILE,
                    curUser.sprite,
                    ray.path.get(ray.dist),
                    null
            );
        }

        //final zap at half distance, for timing of the actual wand effect
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.path.get(dist/2),
                callback );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
    }

//    @Override
//    public int manaCost() {
//        switch (level()){
//            case 1: return 12;
//            case 2: return 15;
//        }
//        return 10;
//    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 3 + level(), manaCost());
    }

}
