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
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Inferno;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.BlobImmunity;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Roots;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.BatSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.HellBatSprite;
import com.watabou.utils.Random;

public class HellBat extends Mob {

	{
		spriteClass = HellBatSprite.class;
		
		HP = HT = 80;
		defenseSkill = 32;
		baseSpeed = 2f;
		
		EXP = 16;
		maxLvl = 27;
		
		flying = true;
		
		loot = new PotionOfDragonsBreath();
		lootChance = 0.5f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 36 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 42;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 7);
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
        Buff.affect(enemy, Roots.class, 4);
		
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
	
}
