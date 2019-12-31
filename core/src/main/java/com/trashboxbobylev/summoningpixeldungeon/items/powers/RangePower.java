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

package com.trashboxbobylev.summoningpixeldungeon.items.powers;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Blindness;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Roots;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Vertigo;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfBlastWave;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RangePower extends Power {
    {
        playerBuff = SpeedyShots.class;
        playerBuffDuration = 10f;
        basicBuff = AdditionalEvasion.class;
        basicBuffDuration = 8;
        classBuff = RootingOnShots.class;
        classBuffDuration = 8;
        featuredClass = Minion.MinionClass.RANGE;
        image = ItemSpriteSheet.RANGE_POWER;
    }

    @Override
    protected void affectDungeon() {
        Sample.INSTANCE.play(Assets.SND_BLAST);
        WandOfBlastWave.BlastWave.blast(Dungeon.hero.pos);
        //throws other chars around the center.
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                mob.damage(Math.round(Random.NormalIntRange(1, 6)), this);

                if (mob.isAlive()) {
                    Ballistica trajectory = new Ballistica(mob.pos, Dungeon.hero.pos, Ballistica.MAGIC_BOLT);
                    WandOfBlastWave.throwChar(mob, trajectory, 8);
                    Buff.prolong(mob, Roots.class, 5f);
                }
            }
        }
    }
}
