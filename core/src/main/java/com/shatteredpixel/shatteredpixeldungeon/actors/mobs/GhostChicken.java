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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostChickenSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GhostChicken extends Mob {

	{
		spriteClass = GhostChickenSprite.class;

		HP = HT = 4;
		defenseSkill = 35;
		baseSpeed = 3f;

		EXP = 20;
		maxLvl = 30;
		if (SPDSettings.bigdungeon()){
			EXP = 40;
			maxLvl = 100;
		}

		loot = Generator.random();
		lootChance = 0.1f;
	}

	public GhostChicken() {
		if (SPDSettings.bigdungeon()){
			EXP = 40;
			maxLvl = 100;
		}
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage += enemy.drRoll();
		Buff.prolong(enemy, TimedShrink.class, 2.5f);
		return super.attackProc(enemy, damage);
	}

	@Override
	public int attackSkill( Char target ) {
		return 42;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		HP--;
		if (HP <= -1) die(Dungeon.hero);
		return true;
	}

	@Override
	public void hitSound(float pitch) {
		Sample.INSTANCE.play(Assets.Sounds.PUFF, 1, pitch);
	}

	@Override
    protected float attackDelay() {
        return super.attackDelay()*0.25f;
    }
}