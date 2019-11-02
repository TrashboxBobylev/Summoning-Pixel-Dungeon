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

package com.trashboxbobylev.summoningpixeldungeon.items.wands;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.BlobImmunity;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Ooze;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.StenchHolder;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.CorrosionParticle;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.StenchParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.curses.Stench;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MagesStaff;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class WandOfStench extends Wand {

    {
        image = ItemSpriteSheet.WAND_STENCH;
    }

    @Override
    protected void onZap(Ballistica attack) {
        Char ch = Actor.findChar( attack.collisionPos );
        if (ch != null) {

            processSoulMark(ch, chargesPerCast());
            StenchHolder buff = Buff.affect(ch, StenchHolder.class, level()*4);
            buff.minDamage = level();
            buff.maxDamage = 3 + level()*2;

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
        Sample.INSTANCE.play(Assets.SND_ZAP);
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
        particle.color( ColorMath.random( 0x0bb34d, 0x1d4636) );
        particle.am = 0.75f;
        particle.setLifespan( 1.2f );
        particle.acc.set(0, 30);
        particle.setSize( 0.5f, 3f );
        particle.shuffleXY( 1f );
    }
}
