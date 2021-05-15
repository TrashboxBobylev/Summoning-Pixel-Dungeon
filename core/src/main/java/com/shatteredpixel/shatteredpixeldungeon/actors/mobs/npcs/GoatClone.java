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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GoatCloneSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class GoatClone extends NPC {
    private int defendingPos;

    {
        spriteClass = GoatCloneSprite.class;
        alignment = Alignment.ALLY;
        intelligentAlly = true;
        WANDERING = new Wandering();
        baseSpeed = 2f;
        actPriority = MOB_PRIO + 1;
        state = WANDERING;
    }

    @Override
    public Char chooseEnemy() {

        boolean newEnemy = false;
        if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
        } else if (enemy.alignment == Alignment.ALLY) {
            newEnemy = true;
        } else if (enemy.isInvulnerable(getClass()) && enemy.buff(StoneOfAggression.Aggression.class) == null) {
            newEnemy = true;
        } else if (enemy.buff(Knife.SoulGain.class) == null) {
            newEnemy = true;
        }

        if (newEnemy){
            HashSet<Char> enemies = new HashSet<>();

            for (Mob mob : Dungeon.level.mobs)
                if (mob.alignment == Alignment.ENEMY && fieldOfView[mob.pos] && mob.buff(Knife.SoulGain.class) != null) {
                        enemies.add(mob);
                    }
            return chooseClosest(enemies);
        } else {
            return enemy;
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(Dungeon.hero.lvl / 3, Dungeon.hero.lvl);
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return true;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.3f;
    }

    @Override
    public int attackSkill(Char target) {
        return Dungeon.hero.attackSkill(target);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Dungeon.mode != Dungeon.GameMode.HELL)
        Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP+1);
        damage = super.attackProc(enemy, damage);
        if (Dungeon.hero.belongings.weapon != null){
            return Dungeon.hero.belongings.weapon.proc( enemy, this, damage );
        } else {
            return damage;
        }
    }

    public static GoatClone findClone(){
        GoatClone clone = null;

        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob instanceof GoatClone){
                clone = (GoatClone) mob;
            }
        }

        return clone;
    }

    public static void spawnClone(){
        ArrayList<Integer> respawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
            int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS9[i];
            if ((Actor.findChar( p ) == null || Actor.findChar( p ) == Dungeon.hero) && Dungeon.level.passable[p]) {
                respawnPoints.add( p );
            }
        }
        int index = 1;
        if (!respawnPoints.isEmpty())
            index = Random.index( respawnPoints );

        GoatClone clone = new GoatClone();

        GameScene.add( clone );
        ScrollOfTeleportation.appear( clone, respawnPoints.get( index ) );
    }

    {
        immunities.add(Terror.class);
        immunities.add(Amok.class);
        immunities.add(Charm.class);
    }

    public class Wandering extends Mob.Wandering {

        private Char toFollow(Char start) {
            Char toFollow = start;
            boolean[] passable = Dungeon.level.passable;
            PathFinder.buildDistanceMap(pos, passable, 8*2);//No limit on distance
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.alignment == alignment && PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] && mob.following(toFollow)) {
                    toFollow = toFollow(mob);//If we find a mob already following the target, ensure there is not a mob already following them. This allows even massive chains of allies to traverse corridors correctly.
                }
            }
            return toFollow;
        }

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            //Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
            if ( enemyInFOV && Dungeon.level.distance(pos, enemy.pos) < 8) {

                enemySeen = true;

                notice();
                alerted = true;

                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                Char toFollow = toFollow(Dungeon.hero);
                int oldPos = pos;
                //always move towards the target when wandering
                if (getCloser( target = toFollow.pos )) {
                    if (!Dungeon.level.adjacent(toFollow.pos, pos) && Actor.findChar(pos) == null) {
                        getCloser( target = toFollow.pos );
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
