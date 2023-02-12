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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class SoulParalysisAbility extends ArmorAbility {

    {
        baseChargeUse = 75f;
    }

    @Override
    protected void activate(Armor armor, Hero hero, Integer target) {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]
				&& mob.alignment != Char.Alignment.ALLY) {
				Buff.prolong( mob, SoulParalysis.class, 7 );
			}
		}

		charge -= chargeUse(hero);

		hero.sprite.operate( hero.pos );
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);

		hero.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
		Sample.INSTANCE.play( Assets.Sounds.LULLABY );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
    }

	@Override
	public int icon() {
		return HeroIcon.SOUL_PARALYSIS;
	}
}
