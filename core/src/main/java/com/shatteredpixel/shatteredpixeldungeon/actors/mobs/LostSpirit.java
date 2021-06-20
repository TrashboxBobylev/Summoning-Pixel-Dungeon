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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LostSpiritSprite;

import java.util.HashSet;

public class LostSpirit extends AbyssalMob {

    {
        HP = HT = 300;
        defenseSkill = 72;
        spriteClass = LostSpiritSprite.class;

        EXP = 40;
        maxLvl = 30;

        flying = true;
        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.UNDEAD);
    }

    public Char chooseEnemy() {

        Terror terror = buff( Terror.class );
        if (terror != null) {
            Char source = (Char) Actor.findById( terror.object );
            if (source != null) {
                return source;
            }
        }

        if (hordeHead != -1 && Actor.findById(hordeHead) != null){
            Mob hordeHead = (Mob) Actor.findById(this.hordeHead);
            if (hordeHead.isAlive()){
                return hordeHead.enemy;
            }
        }

        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead/missing
        if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
        } else if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos)){
            newEnemy = true;
        }

        if ( newEnemy ) {

            HashSet<Char> enemies = new HashSet<>();

            if (alignment == Alignment.ENEMY) {
                //look for ally mobs to attack, ignoring the soul flame
                for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
                    if (mob.alignment == Alignment.ENEMY && canSee(mob.pos) && !canBeIgnored(mob))
                        enemies.add(mob);

                //and look for the hero if there is no minions
                for (Char minion : enemies){
                    if (minion instanceof Minion){
                        if (((Minion) minion).isTanky) return minion;
                    }
                }

                if (canSee(Dungeon.hero.pos)) {
                    if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
                        enemies.clear();
                        enemies.add(Dungeon.hero);
                    }
                }
            }

            return chooseClosest(enemies);

        } else
            return enemy;
    }
}
