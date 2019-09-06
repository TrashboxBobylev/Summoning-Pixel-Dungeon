/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.Random;

public abstract class Minion extends Mob {

    protected int minDamage = 0;
    protected int maxDamage = 0;
    protected int minDR = 0;
    protected int maxDR = 0;
    public float attunement = 1;

    {
        //all minions are allies and kinda intelligent
        alignment = Alignment.ALLY;
        intelligentAlly = true;

        WANDERING = new Wandering();
        state = WANDERING;

        //before other mobs
        actPriority = MOB_PRIO + 1;
    }

    public void setMaxHP(int hp){
        HP = HT = hp;
    }

    public void setDamage(int min, int max){
        minDamage = min;
        maxDamage = max;
    }

    public void adjustDamage(int min, int max){
        minDamage += min;
        maxDamage += max;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(minDamage, maxDamage);
    }

    public void setDR(int min, int max){
        minDR = min;
        maxDR = max;
    }

    public void adjustDR(int min, int max){
        minDR += min;
        maxDR += max;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(minDR, maxDR);
    }

    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        int targetPos = Dungeon.hero.pos;

        //will never attack something far from their target
        if (enemy != null
                && Dungeon.level.mobs.contains(enemy)
                && (Dungeon.level.distance(enemy.pos, targetPos) <= 8)){
            return enemy;
        }

        return null;
    }

    //ported from DriedRose.java
    //minions will always move towards hero if enemies not here
    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if ( enemyInFOV) {
                enemySeen = true;
                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;
            } else {
                enemySeen = false;

                int oldPos = pos;
                target = Dungeon.hero.pos;
                //always move towards the hero when wandering
                if (getCloser( target )) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    @Override
    public void die(Object cause) {
        Dungeon.hero.usedAttunement -= attunement;
        super.die(cause);
    }
}
