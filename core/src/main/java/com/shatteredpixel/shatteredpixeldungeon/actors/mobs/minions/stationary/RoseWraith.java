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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.MagicPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RoseWraithSprite;

public class RoseWraith extends StationaryMinion {
    {
        spriteClass = RoseWraithSprite.class;
        baseDefense = 2;
    }

    @Override
    public int defenseSkill(Char enemy) {
        int round = Math.round(super.attackSkill(enemy) * 2.25f);
        if (buff(MagicPower.class) != null) round = Math.round(super.attackSkill(enemy) * 1f);
        return round;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = Dungeon.level.heroFOV[pos];

        Timer timer = buff(Timer.class);
        if (timer == null){
            int timing = 6;
            switch (lvl){
                case 1: timing = 5; break;
                case 2: timing = 0; break;
            }
            if (buff(MagicPower.class) != null) timing /= 3;
            if (timing != 0) Buff.affect(this, Timer.class, timing*attackDelay());
            Wraith.summonAt(this);
            this.damage(1, this);
        }

        spend(attackDelay());
        next();

        return !visible;
    }

    @Override
    protected boolean act() {
        if (buff(Timer.class) != null) sprite.showStatus(CharSprite.DEFAULT, String.valueOf(buff(Timer.class).cooldown()+1));
        return super.act();
    }

    public static class Timer extends FlavourBuff {

    }


}
