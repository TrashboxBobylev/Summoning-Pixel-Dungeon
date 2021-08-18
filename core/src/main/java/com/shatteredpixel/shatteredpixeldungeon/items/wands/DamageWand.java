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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

import java.text.DecimalFormat;

//for wands that directly damage a target
//wands with AOE effects count here (e.g. fireblast), but wands with indrect damage do not (e.g. venom, transfusion)
public abstract class DamageWand extends Wand{

	public int magicalmin(){
		int round = Math.round(magicalmin(buffedLvl())*powerLevel());
		if (!(this instanceof WandOfMagicMissile) && charger != null
				&& charger.target.buff(WandOfMagicMissile.MagicCharge.class) != null){
			round *= 1.25f;
		}
		return round;
	}

	public abstract int magicalmin(int lvl);

	public int magicalmax(){
		int round = Math.round(magicalmax(buffedLvl())*powerLevel());
		if (!(this instanceof WandOfMagicMissile) && charger != null
				&& charger.target.buff(WandOfMagicMissile.MagicCharge.class) != null){
			round *= 1.25f;
		}
		return round;
	}

	public abstract int magicalmax(int lvl);

	public int damageRoll(){
		return Random.NormalIntRange(magicalmin(), magicalmax());
	}

	public int damageRoll(int lvl){
		return Random.NormalIntRange(magicalmin(lvl), magicalmax(lvl));
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", magicalmin(), magicalmax());
		else
			return Messages.get(this, "stats_desc", magicalmin(0), magicalmax(0));
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				Math.round(magicalmin(tier-1)*powerLevel(tier-1)),
				Math.round(magicalmax(tier-1)*powerLevel(tier-1)),
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1))
		);
	}
}
