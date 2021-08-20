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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StenchHolder;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Stenchy;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.StenchParticle;
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
    public float powerLevel(int level) {
        switch (level){
            case 0: return 1.0f;
            case 1: return 2.0f;
            case 2: return 3.0f;
        }
        return 0f;
    }

    @Override
    public float rechargeModifier(int level) {
        switch (level){
            case 0: return 1f;
            case 1: return 4f;
            case 2: return 2f;
        }
        return 0f;
    }

    @Override
    public void onZap(Ballistica attack) {
        Char ch = Actor.findChar( attack.collisionPos );
        if (ch == null) {
            ch = new Stenchy();
            if (level() == 1) ch.HP = ch.HT = 3;
            ch.pos = attack.collisionPos;
            GameScene.add((Mob) ch);
            StenchHolder buff = Buff.affect(ch, StenchHolder.class, 4*powerLevel());
            buff.minDamage = 1 + Dungeon.hero.lvl/5;
            buff.maxDamage = 1 + Dungeon.hero.lvl/5;
            if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)){
                buff.minDamage = 1;
                buff.maxDamage = 1;
            }

            ch.sprite.burst(0xFF1d4636, level() / 2 + 2);

        } else {
            Dungeon.level.pressCell(attack.collisionPos);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.STENCH,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void onHit(Wand wand, Char attacker, Char defender, int damage) {
        if (Random.Int( level() + 3 ) >= 2) {

            Buff.affect( attacker, BlobImmunity.class, Random.IntRange(0, level()) );
            CellEmitter.center(defender.pos).burst(StenchParticle.FACTORY, 5 );

        }
    }

    @Override
    public void staffFx(StaffParticle particle) {
        particle.color( ColorMath.random( 0x04733a, 0x009548) );
        particle.am = 0.75f;
        particle.setLifespan( 1.25f );
        particle.acc.set(0, -30);
        particle.setSize( 0.5f, 5f );
        particle.shuffleXY( 1f );
    }
}
