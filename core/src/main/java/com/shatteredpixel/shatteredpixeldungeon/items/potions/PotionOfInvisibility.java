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

package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfInvisibility extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_INVIS;
	}

	@Override
	public void apply( Hero hero ) {
		setKnown();
		if (hero.pointsInTalent(Talent.SPEEDY_STEALTH) > 1 && hero.hasTalent(Talent.ARCANE_CLOAK)){
			float amount = hero.pointsInTalent(Talent.SPEEDY_STEALTH)*4;
			for (Buff b : hero.buffs()) {
				if (b instanceof CloakOfShadows.cloakStealth) {
					if (!((CloakOfShadows.cloakStealth) b).isCursed()) {
						((CloakOfShadows.cloakStealth) b).charge(hero, amount);
					}
				}
			}
			GLog.i(Messages.get(this, "cloak_charge"));
		} else {
			Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob instanceof Minion && hero.fieldOfView[mob.pos]) {
					Buff.affect(mob, Invisibility.class, Invisibility.DURATION);
				}
			}
			GLog.i(Messages.get(this, "invisible"));
		}
		Sample.INSTANCE.play( Assets.Sounds.MELD );
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}

}
