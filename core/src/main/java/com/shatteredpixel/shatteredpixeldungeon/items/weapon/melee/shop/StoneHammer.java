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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class StoneHammer extends MeleeWeapon {
    {
        image = ItemSpriteSheet.STONE_HAMMER;
        tier = 2;
        DLY = 3.2f;
        ACC = 0.7f;
    }

    @Override
    public int min(int lvl) {
        return  tier*2 +  //2
                lvl*2;    //+2
    }

    @Override
    public int max(int lvl) {
        return  13*(tier+1) +    //39
                lvl*(tier+1);   //+3
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        defender.sprite.centerEmitter().start( Speck.factory( Speck.KIT ), 0.05f, 10 );
        if (Random.Float() < 0.5f) Buff.prolong(defender, Paralysis.class, 5f);
        Sample.INSTANCE.play( Assets.Sounds.EVOKE );
        Camera.main.shake( 5, 0.7f );
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int warriorAttack(int damage, Char enemy) {
        if (!enemy.properties().contains(Char.Property.BOSS)
                && !enemy.properties().contains(Char.Property.MINIBOSS)){
            ((Mob)enemy).EXP = 0;
            enemy.alignment = Char.Alignment.ALLY;
            enemy.die(Dungeon.hero);
        }
        return 0;
    }

    @Override
    public float warriorDelay(float delay, Char enemy) {
        return super.warriorDelay(delay, enemy);
    }
}
