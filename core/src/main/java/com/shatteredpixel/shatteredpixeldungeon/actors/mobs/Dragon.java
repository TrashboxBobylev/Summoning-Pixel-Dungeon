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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DragonSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Dragon extends Mob{
    {
        HP = HT = 210;
        defenseSkill = 40;

        EXP = 40;
        maxLvl = 30;
        if (SPDSettings.bigdungeon()){
            EXP = 21;
            maxLvl = 34;
        }

        flying = true;
        properties.add(Property.BOSS);
        properties.add(Property.FIERY);
        properties.add(Property.DEMONIC);
        properties.add(Property.LARGE);
    }

    public Dragon() {
        if (SPDSettings.bigdungeon()){
            EXP = 80;
            maxLvl = 64;
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 26, 49 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 64;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(16, 36);
    }

    private int rangedCooldown = Random.NormalIntRange( 3, 5 );
    private boolean doingSweepAttack = false;

    @Override
    protected boolean act() {
        if (state == HUNTING){
            rangedCooldown--;
        }

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        if (rangedCooldown <= 0) {
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
        } else {
            return super.canAttack( enemy );
        }
    }

    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        meleeProc( enemy, damage );

        return damage;
    }

    private void zap() {
        spend( 1f );

        if (hit( this, enemy, true )) {

            rangedProc( enemy );

        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }

        rangedCooldown = Random.NormalIntRange( 3, 5 );
    }

    public void onZapComplete() {
        zap();
        next();
    }

    protected void meleeProc( Char enemy, int damage ) {
        if (Random.Int( 2 ) == 0 && !enemy.isWet()) {
            Buff.affect( enemy, FrostBurn.class ).reignite( enemy, 10f );
            Splash.at( enemy.sprite.center(), sprite.blood(), 5);
        }

        int enemyDirection = 0;
        if (!doingSweepAttack) {
            spend(-cooldown());
            doingSweepAttack = true;
            for (int i : PathFinder.NEIGHBOURS9) {
                if (pos + i == enemy.pos) {
                    enemyDirection = i;
                }
                swipeAttack(enemyDirection, 0, 1, 3);
                swipeAttack(enemyDirection, 1, 0, 2);
                swipeAttack(enemyDirection, 2, 1, 5);
                swipeAttack(enemyDirection, 3, 0, 6);
                swipeAttack(enemyDirection, 5, 2, 8);
                swipeAttack(enemyDirection, 6, 3, 7);
                swipeAttack(enemyDirection, 7, 6, 8);
                swipeAttack(enemyDirection, 8, 5, 7);
                doingSweepAttack = false;
            }
        }
    }

    @Override
    public void onAttackComplete() {
    }

    protected void swipeAttack(int direct, int checkedDir, int adjacentDir1, int adjacentDir2){
        if (direct == PathFinder.NEIGHBOURS9[checkedDir]){
            Char ch = Actor.findChar(pos + PathFinder.NEIGHBOURS9[adjacentDir1]);
            if (ch != null && ch.alignment != alignment){
                sprite.attack(ch.pos, new Callback() {
                    @Override
                    public void call() {
                        doAttack(ch);
                        spend(-cooldown());
                        Char ch = Actor.findChar(pos + PathFinder.NEIGHBOURS9[adjacentDir2]);
                        if (ch != null){
                            sprite.attack(ch.pos);
                            spend(attackDelay());
                            next();
                        }
                    }
                });
            }
        }
    }

    protected void rangedProc( Char enemy ) {
        if (!enemy.isWet()) {
            Buff.affect( enemy, FrostBurn.class ).reignite( enemy, 8f );
        }

        Splash.at( enemy.sprite.center(), sprite.blood(), 5);

        ArrayList<Integer> candidates = new ArrayList<>();
        boolean[] solid = Dungeon.level.solid;

        int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
        for (int n : neighbours) {
            if (!solid[n] && Actor.findChar( n ) == null) {
                candidates.add( n );
            }
        }

        if (candidates.size() > 0) {

            SmallDragon clone = spawn();
            clone.pos = Random.element( candidates );
            clone.state = clone.HUNTING;

            Dungeon.level.occupyCell(clone);

            GameScene.add( clone, 1f );
            clone.sprite.jump(pos, clone.pos, new Callback() {
                @Override
                public void call() {
                    Actor.addDelayed( new Pushing( clone, pos, clone.pos ), 1f );
                }
            });
        }
    }

    private SmallDragon spawn() {
        SmallDragon clone = new SmallDragon();
        if (buff(Corruption.class ) != null) {
            Buff.affect( clone, Corruption.class);
        }
        return clone;
    }

    private static final String COOLDOWN = "cooldown";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( COOLDOWN, rangedCooldown );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        if (bundle.contains( COOLDOWN )){
            rangedCooldown = bundle.getInt( COOLDOWN );
        }
    }

    public class SmallDragon extends Mob {

        {
            spriteClass = SmallDragonSprite.class;

            HP = HT = 60;
            defenseSkill = 40;
            viewDistance = Light.DISTANCE;

            EXP = 5;
            maxLvl = -2;

            properties.add(Property.DEMONIC);
            properties.add(Property.FIERY);
        }

        public class SmallDragonSprite extends DragonSprite{
            public SmallDragonSprite() {
                super();
                scale.x = 0.5f;
                scale.y = 0.5f;
            }
        }

        @Override
        public int attackSkill( Char target ) {
            return 70;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 19, 36 );
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(6, 20);
        }

    }
}
