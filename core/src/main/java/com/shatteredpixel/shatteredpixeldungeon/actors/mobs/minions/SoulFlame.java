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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SoulFlameSprite;
import com.watabou.utils.Random;

public class SoulFlame extends Minion {
    {
        spriteClass = SoulFlameSprite.class;
        viewDistance = 8;
        flying = true;
        properties.add(Property.RANGED);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
    }

    @Override
    protected boolean act() {
        HP--;
        if (!isAlive()) {
            die(null);
        }
        return super.act();
    }

    public static int adjustMaxDamage(int heroLevel){
        return Math.round(heroLevel*1.25f);
    }

    public static int adjustMinDamage(int heroLevel){
        return heroLevel;
    }

    public static int adjustHP(int attunement){
        int i = attunement * 20;
        if (Dungeon.hero.heroClass == HeroClass.CONJURER) i /= 1.5f;
        return i;
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    protected boolean doAttack(Char enemy) {
            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            } else {
                zap();
            }

            return !visible;
    }

    public void zap(){
        spend( 1f );

        if (hit( this, enemy, true )) {
            int dmg = Random.NormalIntRange(minDamage, maxDamage);
            enemy.damage(dmg, this);
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    @Override
    public String description() {
        return Messages.get(this, "desc", minDamage, maxDamage, HP+1);
    }
}
