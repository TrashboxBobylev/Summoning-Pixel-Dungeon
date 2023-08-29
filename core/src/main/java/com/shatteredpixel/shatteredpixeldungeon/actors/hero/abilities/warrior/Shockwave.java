/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Shockwave extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return new Ballistica( user.pos, dst, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET ).collisionPos;
	}

	@Override
	protected void activate(Armor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}
		if (target == hero.pos){
			GLog.warning(Messages.get(this, "self_target"));
			return;
		}
		hero.busy();

		charge -= chargeUse(hero);
		Item.updateQuickslot();

		Ballistica aim = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);

		int maxDist = 8;
		int dist = Math.min(aim.dist, maxDist);

		ConeAOE cone = new ConeAOE(aim,
				dist,
				90,
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

		//cast to cells at the tip, rather than all cells, better performance.
		for (Ballistica ray : cone.outerRays){
			((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FORCE_CONE,
					hero.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		hero.sprite.zap(target);
		Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.5f);
		Camera.main.shake(2, 0.5f);
		//final zap at 2/3 distance, for timing of the actual effect
		MagicMissile.boltFromChar(hero.sprite.parent,
				MagicMissile.FORCE_CONE,
				hero.sprite,
				cone.coreRay.path.get(dist * 2 / 3),
				new Callback() {
					@Override
					public void call() {

						for (int cell : cone.cells){

							Char ch = Actor.findChar(cell);
							if (ch != null && ch.alignment != hero.alignment){
								int scalingStr = hero.STR()-10;
								int damage = Random.NormalIntRange(10 + scalingStr*2, 20 + 4*scalingStr);
								damage -= ch.actualDrRoll();

//								if (hero.pointsInTalent(Talent.STRIKING_WAVE) == 4){
//									Buff.affect(hero, Talent.StrikingWaveTracker.class, 0f);
//								}

								if (Random.Int(10) < 5){
									damage = hero.attackProc(ch, damage);
								}
								ch.damage(damage, hero);
								if (ch.isAlive()){
										Buff.affect(ch, Cripple.class, 5f);
								}

							}
						}

						Invisibility.dispel();
						hero.spendAndNext(Actor.TICK);

					}
				});
	}

	@Override
	public int icon() {
		return HeroIcon.SHOCKWAVE;
	}
}
