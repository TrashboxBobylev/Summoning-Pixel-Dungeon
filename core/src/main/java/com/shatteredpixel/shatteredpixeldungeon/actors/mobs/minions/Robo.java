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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM150Sprite;
import com.watabou.utils.Callback;

public class Robo extends Minion{
    {
        spriteClass = DM150Sprite.class;
        isTanky = true;
        baseMinDR = 12;
        baseMaxDR = 22;
    }

    @Override
    protected float attackDelay() {
        float mod = 0;
        switch (lvl){
            case 0: mod = 3.5f; break;
            case 1: mod = 2.5f; break;
            case 2: mod = 1.5f; break;
        }
        return super.attackDelay() * mod;
    }

    @Override
    protected boolean getCloser(int target) {
        for (Mob mob : Dungeon.level.mobs) {
            if (mob.paralysed <= 0
                    && Dungeon.level.distance(pos, mob.pos) <= 3
                    && mob.state != mob.HUNTING && mob != this) {
                mob.beckon( pos );
            }
        }
        return super.getCloser(target);
    }

    private void chain(int target){
        if (enemy.properties().contains(Property.IMMOVABLE))
            return;

        Ballistica chain = new Ballistica(pos, target, Ballistica.FRIENDLY_PROJECTILE);

        if (chain.collisionPos == enemy.pos
                && chain.path.size() >= 2
                && !Dungeon.level.pit[chain.path.get(1)]) {
                    int newPos = -1;
                    for (int i : chain.subPath(1, chain.dist)){
                        if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                            newPos = i;
                            break;
                        }
                    }

            if (newPos != -1) {
                final int newPosFinal = newPos;
                this.target = newPos;
                Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
                    public void call() {
                        enemy.pos = newPosFinal;
                        Dungeon.level.occupyCell(enemy);
                        ((Mob)enemy).aggro(Robo.this);
                    }
                }), -1);
            }

        }
        next();
    }




    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE ).collisionPos == enemy.pos;
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else if (enemy.properties().contains(Property.RANGED)){

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
        return true;
    }

    private void zap() {
        spend( 1f );
        chain(enemy.pos);
    }

    public void onZapComplete() {
        zap();
        next();
    }
}
