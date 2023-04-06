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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Timer;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Flashbang;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RatBomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ExplodingTNTSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ExplodingTNT extends Mob {

	{
		spriteClass = ExplodingTNTSprite.class;
		
		HP = HT = 48;
		defenseSkill = 13;
		
		maxLvl = 18;
		EXP = 10;
		FLEEING = new Fleeing();
		properties.add(Property.RANGED);
	}

    //he doesn't attack in melee
    public boolean attack = true;
    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 8, 13 );
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
    public void die(Object cause) {
        super.die(cause);
        if (Dungeon.mode == Dungeon.GameMode.DIFFICULT) {
            Item.curUser = null;
            new Noisemaker().cast(this, pos);
        }
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = Dungeon.level.heroFOV[pos];
	    if (!attack) {
            return super.doAttack(enemy);
        } else {
            final ExplodingTNT mouse = this;
            Item.curUser = null;
//            Callback call = new Callback() {
//                @Override
//                public void call() {
                    new RatBomb().cast(mouse, enemy.pos);
//                }
//            };
//            ((MissileSprite)sprite.parent.recycle(MissileSprite.class)).reset(pos, enemy.pos, new RatBomb(), call);
            attack = false;

            Buff.affect(mouse, Timer.class, 10f);

            return !visible;
        }
    }

    @Override
    protected boolean getCloser(int target) {
        if (buff(Timer.class) != null){
            return enemySeen && getFurther(target);
        }
        else return super.getCloser(target);
    }

    @Override
    public int defenseValue() {
        return 1;
    }

    {
        immunities.add(Flashbang.class);
        immunities.add(Bomb.class);
        immunities.add(ExplosiveTrap.class);
    }

    @Override
    protected boolean act() {
        boolean result = super.act();

        if (buff(Timer.class) == null) {
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
