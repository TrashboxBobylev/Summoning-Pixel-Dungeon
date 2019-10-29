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
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.PerfumeGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.MagicImmune;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary.StationaryMinion;
import com.trashboxbobylev.summoningpixeldungeon.items.KindOfWeapon;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfAccuracy;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfTargeting;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public abstract class Minion extends Mob {

    public int minDamage = 0;
    public int maxDamage = 0;

    protected int minDR = 0;
    protected int maxDR = 0;
    public int baseMinDR = 0;
    public int baseMaxDR = 0;

    public int strength = 9;
    public int defendingPos = -1;

    public boolean isTanky = false;
    public Weapon.Enchantment enchantment;
    public int lvl;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("minDamage", minDamage);
        bundle.put("maxDamage", maxDamage);
        bundle.put("minDR", minDR);
        bundle.put("maxDR", maxDR);
        bundle.put("str", strength);
        bundle.put("enchantment", enchantment);
        bundle.put("level", lvl);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        minDamage = bundle.getInt("minDamage");
        maxDamage = bundle.getInt("maxDamage");
        minDR = bundle.getInt("minDR");
        maxDR = bundle.getInt("maxDR");
        strength = bundle.getInt("str");
        lvl = bundle.getInt("lvl");
        enchantment = (Weapon.Enchantment) bundle.get("enchantment");
    }

    public float attunement = 1;

    {
        //all minions are allies and kinda intelligent
        alignment = Alignment.ALLY;
        intelligentAlly = true;

        WANDERING = new Wandering();
        state = WANDERING;

        //before other mobs
        actPriority = MOB_PRIO + 1;

        immunities.add(PerfumeGas.Affection.class);
    }

    @Override
    public String getName() {
        return enchantment != null ? enchantment.name( super.getName() ) : super.getName();
    }

    public void setMaxHP(int hp){
        HP = HT = hp;
    }

    public void setStrength(int str){
        strength = str;
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
        minDR += Math.max(-2, min);
        maxDR += Math.max(-4, max);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(minDR + baseMinDR, maxDR + baseMaxDR);
    }

    @Override
    public Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        int targetPos = defendingPos != -1 ? defendingPos : Dungeon.hero.pos;
        int distance = this instanceof StationaryMinion ? Integer.MAX_VALUE : 8;

        //will never attack something far from their target
        if (enemy != null
                && Dungeon.level.mobs.contains(enemy)
                && (Dungeon.level.distance(enemy.pos, targetPos) <= distance)){
            return enemy;
        }

        return null;
    }

    //same accuracy and dexterity as player
    @Override
    public int defenseSkill(Char enemy) {
        boolean seen = (enemySeen && enemy.invisible == 0);
        if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack()) seen = true;
        if ( seen
                && paralysed == 0
                && !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
            return Dungeon.hero.defenseSkill(enemy);
        } else {
            return 0;
        }
    }

    @Override
    public int attackSkill(Char target) {

        int encumbrance = strength - Dungeon.hero.STR();

        float accuracy = 1;
        accuracy *= RingOfAccuracy.accuracyMultiplier( Dungeon.hero );

        if (encumbrance > 0){
            accuracy /= Math.pow(1.5, encumbrance);
        }

        return (int) (Dungeon.hero.getAttackSkill() * accuracy);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (enchantment != null && buff(MagicImmune.class) == null) {
            damage = enchantment.proc(  this, enemy, damage );
        }
        return super.attackProc(enemy, damage);
    }



    //ported from DriedRose.java
    //minions will always move towards hero if enemies not here
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
                target = defendingPos != -1 ? defendingPos : Dungeon.hero.pos;
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

    @Override
    public String description() {
        String d = super.description();
        return d + "\n\n" + Messages.get(Minion.class, "stats", minDamage, maxDamage, HP, HT, minDR + baseMinDR, maxDR + baseMaxDR);
    }

    public void destroy() {
        Dungeon.hero.usedAttunement -= attunement;
        super.destroy();
    }
}
