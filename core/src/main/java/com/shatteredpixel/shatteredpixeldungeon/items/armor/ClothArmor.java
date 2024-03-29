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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ClothArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_CLOTH;

		bones = false; //Finding them in bones would be semi-frequent and disappointing.
	}
	
	public ClothArmor() {
		super( 1 );
	}

	@Override
	public float defenseLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0f;
			case 2: return 0f;
		}
		return 0f;
	}

	@Override
	public float evasionFactor(Char owner, float evasion) {
		float eva = super.evasionFactor(owner, evasion);
		if (level() > 0)
			eva /= 2;
		return eva;
	}

	public static class ArtRechargeTracker extends Buff {

		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			if (target instanceof Hero && target.isAlive() &&
					((Hero) target).belongings.armor instanceof ClothArmor &&
					((Hero) target).belongings.armor.level() == 2) {
				spend( TICK );
				for (Buff b : target.buffs()) {
					if (b instanceof Artifact.ArtifactBuff) {
						if (!((Artifact.ArtifactBuff) b).isCursed()) {
							((Artifact.ArtifactBuff) b).charge((Hero) target, 0.25f);
						}
					}
				}
			} else {
				detach();
			}

			return true;
		}
	}
}
