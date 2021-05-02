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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WandOfStenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StenchHolder;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Stenchy;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.StenchParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class WandOfStench extends Wand {

    {
        image = ItemSpriteSheet.WAND_STENCH;
        collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
    }

    @Override
    protected void onZap(Ballistica attack) {
        Char ch = Actor.findChar( attack.collisionPos );
        if (ch == null) {
            ch = new Stenchy();
            ch.pos = attack.collisionPos;
            GameScene.add((Mob) ch);
            GameScene.add(Blob.seed(ch.pos, 30 + level()*5, WandOfStenchGas.class));
            StenchHolder buff = Buff.affect(ch, StenchHolder.class, 4 + level());
            buff.minDamage = 3 + level();
            buff.maxDamage = 3 + level();

            ch.sprite.burst(0xFF1d4636, level() / 2 + 2);

        } else {
            Dungeon.level.pressCell(attack.collisionPos);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.STENCH,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        if (Random.Int( level() + 3 ) >= 2) {

            Buff.affect( attacker, BlobImmunity.class, Random.IntRange(0, level()) );
            CellEmitter.center(defender.pos).burst(StenchParticle.FACTORY, 5 );

        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( ColorMath.random( 0x04733a, 0x009548) );
        particle.am = 0.75f;
        particle.setLifespan( 1.25f );
        particle.acc.set(0, -30);
        particle.setSize( 0.5f, 5f );
        particle.shuffleXY( 1f );
    }
}
