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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SwordStorage;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ChaosSaber;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.Arrays;

public class WandOfConjuration extends Wand {

	{
		image = ItemSpriteSheet.WAND_CONJURATION;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 0.5f;
			case 1: return 0.75f;
			case 2: return 1.25f;
		}
		return 0f;
	}
	
	@Override
	public void onZap(Ballistica bolt) {

			Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1, Random.Float(0.87f, 1.15f) );

			Dungeon.hero.sprite.burst(0x007bdb, 10);
			Dungeon.hero.sprite.burst(0xff61ac, 10);

		SwordStorage swordStorage = Buff.affect(Dungeon.hero, SwordStorage.class);
		swordStorage.countUp(1);
		swordStorage.level = level();
	}

    @Override
    public void fx(Ballistica bolt, Callback callback) {
		callback.call();
	}

	public int swordCount(int lvl){
		return 3;
	}

	@Override
	public boolean tryToZap(Hero owner, int target) {
		if (Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
			GLog.warning( Messages.get(this, "no_magic") );
			return false;
		}
		int swordCount = 0;
		for (Char ch : Actor.chars()){
			if (ch instanceof ChaosSaber){
				swordCount += 1;
			}
		}
		SwordStorage swords = Buff.affect(Dungeon.hero, SwordStorage.class);
		swordCount += swords.count();
		if (swordCount >= swordCount(buffedLvl())){
			GLog.warning( Messages.get(this, "no_more_swords"));
			return false;
		}

		return super.tryToZap(owner, target);
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		SwordStorage swords = Buff.affect(Dungeon.hero, SwordStorage.class);
		if (swords.count() - 1 < swordCount(buffedLvl())) {
			Dungeon.hero.sprite.burst(0x007bdb, 10);
			Dungeon.hero.sprite.burst(0xff61ac, 10);

			Buff.affect(Dungeon.hero, SwordStorage.class).countUp(0.75f);
		}
	}

	@Override
	public String statsDesc() {
		if (!levelKnown)
			return Messages.get(this, "stats_desc", 3, 4, 8);
		else
			return Messages.get(this, "stats_desc", 3, 4 + level(), 8 + level());
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1)),
				3 + tier,
				7 + tier
		);
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( Random.element(Arrays.asList(0xff61ac, 0x007bdb, 0xffffff)));
		particle.am = 1f;
		particle.setLifespan(2f);
		particle.setSize( 1f, 1.5f);
		particle.shuffleXY(0.5f);
		float dst = Random.Float(8f);
		particle.x -= dst;
		particle.y += dst;
	}

}
