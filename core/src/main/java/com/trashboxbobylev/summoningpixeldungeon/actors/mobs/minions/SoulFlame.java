/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.SoulFlameSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SoulFlame extends Minion {
    {
        spriteClass = SoulFlameSprite.class;
        viewDistance = 6;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    public static int adjustMinDamage(int heroLevel){
        return heroLevel;
    }

    public static int adjustMaxDamage(int heroLevel){
        return heroLevel*2;
    }

    public static int adjustHP(int attunement){
        return attunement*10;
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent( pos, enemy.pos )) {
            Sample.INSTANCE.play( Assets.SND_GHOST );
            return super.doAttack( enemy );

        } else {
            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            } else {
                zap();
            }


            return !visible;
        }
    }

    public void zap(){
        spend( 1f );

        if (hit( this, enemy, true )) {
            int dmg = Random.NormalIntRange(minDamage/2, maxDamage/2);
            enemy.damage(dmg, this);
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }
}
