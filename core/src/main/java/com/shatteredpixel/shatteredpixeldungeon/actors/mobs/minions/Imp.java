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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfTargeting;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpMinionSprite;
import com.watabou.utils.Bundle;

public class Imp extends Minion {
    {
        spriteClass = ImpMinionSprite.class;
        WANDERING = new Wandering();
        state = WANDERING;
        attunement = 0f;
    }

    int queenPos = -1;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("queenpos", queenPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        queenPos = bundle.getInt("queenpos");
    }

    public void callToQueen(int posisiion){
        queenPos = posisiion;
    }

    @Override
    public void onLeaving() {
        queenPos = -1;
    }

    //imp chooses his queen as target
    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            StoneOfTargeting.Defending defending = buff(StoneOfTargeting.Defending.class);
            if ( enemyInFOV && defending == null ) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                if (defending != null) {
                    defendingPos = defending.position;
                } else defendingPos = -1;

                int oldPos = pos;
                target = defendingPos != -1 ? defendingPos : queenPos;
                if (target == -1) target = Dungeon.level.randomDestination(Imp.this);
                //always move towards the hero when wandering
                if (getCloser( target )) {
                    //moves 2 tiles at a time when returning to the hero
                    if (defendingPos == -1 && !Dungeon.level.adjacent(target, pos)){
                        getCloser( target );
                    }
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }
}
