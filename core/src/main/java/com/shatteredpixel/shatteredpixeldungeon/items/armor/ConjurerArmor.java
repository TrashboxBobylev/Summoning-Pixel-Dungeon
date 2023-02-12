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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class ConjurerArmor extends Armor {
	
	{
		image = ItemSpriteSheet.ARMOR_CONJURER;
        unique = true;
	}

    public ConjurerArmor() {
        super( 1 );
    }

    @Override
    public float defenseLevel(int level) {
        switch (level){
            case 0: return 1.0f;
            case 1: return 2.0f;
            case 2: return 0f;
        }
        return 0f;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_UNEQUIP);
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        return actions;
    }

//    @Override
//	public void doSpecial() {
//
//		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
//			if (Dungeon.level.heroFOV[mob.pos]
//				&& mob.alignment != Char.Alignment.ALLY) {
//				Buff.prolong( mob, SoulParalysis.class, 7 );
//			}
//		}
//
//		charge -= 75;
//
//		curUser.spend( Actor.TICK );
//		curUser.sprite.operate( curUser.pos );
//		curUser.busy();
//
//		curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
//		Sample.INSTANCE.play( Assets.Sounds.LULLABY );
//	}

    @Override
    public int STRReq(int lvl) {
        return 9;
    }

}