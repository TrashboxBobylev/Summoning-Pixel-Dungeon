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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HellBatSprite;
import com.watabou.utils.Random;

public class HellBat extends Mob {

	{
		spriteClass = HellBatSprite.class;
		
		HP = HT = 55;
		defenseSkill = 32;
		baseSpeed = 2f;
		
		EXP = 15;
		maxLvl = 27;
		
		flying = true;
		
		loot = new PotionOfDragonsBreath();
		lootChance = 0.5f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 13, 15 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 42;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 6);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);

		new Firebomb().explode(pos);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		int reg = Math.min( damage, HT - HP );
		
		if (reg > 0) {
			HP += reg;
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
		}
		if (enemy.buff(BlobImmunity.class) != null) enemy.buff(BlobImmunity.class).detach();
        if (enemy.buff(FireImbue.class) != null) enemy.buff(FireImbue.class).detach();
        Buff.affect(enemy, Roots.class, 4);
        Buff.affect(enemy, BurnInDespair.class, 4);

		
		return damage;
	}

    @Override
    public int defenseProc(Char enemy, int damage) {
        GameScene.add(Blob.seed(pos, 40, Inferno.class));
        return super.defenseProc(enemy, damage);
    }

    {
        immunities.add(Burning.class);
        immunities.add(Inferno.class);
    }

    public static class BurnInDespair extends FlavourBuff{
        {
            immunities.add(FireImbue.class);
            immunities.add(BlobImmunity.class);
        }
    }
	
}
