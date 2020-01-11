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

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Poison;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Terror;
import com.trashboxbobylev.summoningpixeldungeon.items.Heap;
import com.trashboxbobylev.summoningpixeldungeon.items.bombs.Bomb;
import com.trashboxbobylev.summoningpixeldungeon.items.bombs.Flashbang;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.ExplosiveTrap;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ExplodingTNTSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.MissileSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class ExplodingTNT extends Mob {

	{
		spriteClass = ExplodingTNTSprite.class;
		
		HP = HT = 28;
		defenseSkill = 13;
		
		maxLvl = 18;
		EXP = 10;
		FLEEING = new Fleeing();
	}

	//he doesn't attack in melee
    public boolean attack = true;
    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public float speed() {
        float speed = super.speed();
        if (!attack) speed *= 2;
        return speed;
    }

    @Override
	public int attackSkill( Char target ) {
		return 21;
	}

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos && attack;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = Dungeon.level.heroFOV[pos];
	    if (!attack) {
            return super.doAttack(enemy);
        } else {
	        final Ballistica ballistica = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
            Callback call = new Callback() {
                @Override
                public void call() {
                    final Flashbang bomb = new Flashbang();
                    bomb.fuse  = new Bomb.Fuse().ignite(bomb);
                    Actor.addDelayed( bomb.fuse, 0);
                    Heap heap = Dungeon.level.drop( bomb, ballistica.collisionPos );
                    if (!heap.isEmpty()) {
                        heap.sprite.drop( ballistica.collisionPos );
                    }
                    spend(TICK);
                    next();
                    attack = false;
                }
            };
            ((MissileSprite)sprite.parent.recycle(MissileSprite.class)).reset(pos, enemy.pos, new Bomb(), call);
            state = FLEEING;

            return !visible;
        }
    }

    @Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}

    {
        immunities.add(Flashbang.class);
        immunities.add(Bomb.class);
        immunities.add(ExplosiveTrap.class);
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff(Terror.class) == null) {
                state = HUNTING;
                attack = true;
            } else {
                super.nowhereToRun();
            }
        }
    }

    @Override
    protected boolean act() {
        boolean result = super.act();

        if (state == FLEEING && buff( Terror.class ) == null && target == -1) {
            state = HUNTING;
            attack = true;
        }
        return result;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("att", attack);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        attack = bundle.getBoolean("att");
    }
}
