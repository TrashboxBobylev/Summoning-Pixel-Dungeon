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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;

public class Longsword extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.LONGSWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 4;
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		GameScene.selectCell(leaper);
		return damageRoll(Dungeon.hero)/2;
	}

	@Override
	public float warriorDelay(float delay, Char enemy) {
		return 0;
	}

	protected CellSelector.Listener leaper = new  CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {
			if (target != null && target != curUser.pos) {

				Ballistica route = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
				int cell = route.collisionPos;

				//can't occupy the same cell as another char, so move back one.
				if (Actor.findChar( cell ) != null && cell != curUser.pos)
					cell = route.path.get(route.dist-1);

				final int dest = cell;
				Dungeon.hero.busy();
				curUser.sprite.jump(Dungeon.hero.pos, cell, new Callback() {
					@Override
					public void call() {
						Dungeon.hero.move(dest);
						Dungeon.level.occupyCell(Dungeon.hero);
						Dungeon.observe();
						GameScene.updateFog();

						Invisibility.dispel();
						curUser.spendAndNext(speedFactor(Dungeon.hero)*2);
					}
				});
			}
		}

		@Override
		public String prompt() {
			return Messages.get(WarriorArmor.class, "prompt");
		}
	};
}
