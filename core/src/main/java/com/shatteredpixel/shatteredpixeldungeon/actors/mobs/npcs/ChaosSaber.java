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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LoveHolder;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChaosSaberSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

public class ChaosSaber extends NPC {
    {
        spriteClass = ChaosSaberSprite.class;

        alignment = Alignment.ALLY;
        intelligentAlly = true;
        state = HUNTING;
    }

    private int damage;

    private static final String DAMAGE	= "damage";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( DAMAGE, damage );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        damage = bundle.getInt( DAMAGE );
    }

    public void duplicate( int hp ) {
        damage = hp*2;
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(0, new Object());
    }

    @Override
    public int attackSkill( Char target ) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int damageRoll() {
        return damage;
    }

    @Override
    public int attackProc(Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );

        destroy();
        sprite.die();

        LoveHolder.lul buff = Dungeon.hero.buff(LoveHolder.lul.class);
        if (buff != null && Dungeon.hero.subClass == HeroSubClass.SOUL_REAVER){
            int gain = ((Mob)enemy).EXP;
            gain *= 2;
            int charge = buff.gainCharge(gain);
            if (charge == 0) enemy.sprite.showStatus(CharSprite.NEUTRAL, "+%d♥", gain);
        }

        return damage;
    }

    {
        immunities.add( ToxicGas.class );
        immunities.add( CorrosiveGas.class );
        immunities.add( Burning.class );
        immunities.add(PerfumeGas.Affection.class);
    }
}
