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

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChaosSaberSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ChaosSaber extends NPC {
    {
        spriteClass = ChaosSaberSprite.class;

        alignment = Alignment.ALLY;
        intelligentAlly = true;
        flying = true;
        baseSpeed = 2f;

        WANDERING = new Wandering();
        actPriority = HERO_PRIO;

        HP = HT = 1;
        properties.add(Property.IGNORE_ARMOR);
        immunities.add(PerfumeGas.Affection.class);
    }

    public int level;

    @Override
    public int attackSkill( Char target ) {
        return (int) (target.defenseSkill(this)*1.2f);
    }

    @Override
    public int damageRoll() {
        if (Dungeon.mode == Dungeon.GameMode.DIFFICULT && alignment == Alignment.ENEMY){
            return Random.NormalIntRange(1 + level, 4 + level);
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST))
            return Random.NormalIntRange(1 + level, 3 + level);
        return Random.NormalIntRange(4 + level, 8 + level);
    }

    @Override
    public int attackRolls() {
        return 1+level;
    }

    @Override
    public int defenseRolls() {
        return 1+level;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return (int) (enemy.attackSkill(this)*1.25f);
    }

    {
        immunities.add( ToxicGas.class );
        immunities.add( CorrosiveGas.class );
        immunities.add( Burning.class );
        immunities.add(PerfumeGas.Affection.class);
    }

    private static final String LEVEL	= "level";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, level );
        bundle.put( "align", alignment);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        level = bundle.getInt( LEVEL );
        alignment = bundle.getEnum("align", Alignment.class);
    }

    private class Wandering extends Mob.Wandering{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV){
                Dungeon.hero.sprite.centerEmitter().burst(MagicMissile.WardParticle.UP, 12);
                destroy();
                sprite.die();
                return true;
            } else {
                return super.act(enemyInFOV, justAlerted);
            }
        }

    }
}
