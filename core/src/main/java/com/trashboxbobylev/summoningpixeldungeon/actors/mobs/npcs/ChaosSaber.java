/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.CorrosiveGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.PerfumeGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ChaosSaberSprite;
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

        return damage;
    }

    {
        immunities.add( ToxicGas.class );
        immunities.add( CorrosiveGas.class );
        immunities.add( Burning.class );
        immunities.add(PerfumeGas.Affection.class);
    }
}
