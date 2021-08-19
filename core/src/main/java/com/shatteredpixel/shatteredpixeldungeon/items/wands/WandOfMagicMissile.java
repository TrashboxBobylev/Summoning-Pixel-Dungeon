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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WandOfMagicMissile extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
		isMM = true;
	}

	@Override
	public int magicalmin(int lvl){
		return (int) ((4 + Dungeon.hero.lvl*0.75f));
	}

	@Override
	public int magicalmax(int lvl) {
		return (int) ((6 + Dungeon.hero.lvl*0.75f));
	}
	
	@Override
    public void onZap(Ballistica bolt) {
				
		Char ch = Actor.findChar( bolt.collisionPos );
		if (ch != null) {
			if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) {
				processSoulMark(ch, chargesPerCast());
				ch.damage(damageRoll(), this);
				Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
			}

			ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);

			if (level() > 0 || curUser.buff(MagicCharge.class) != null){
				Buff.prolong(curUser, MagicCharge.class, MagicCharge.DURATION);
			}

		} else {
            Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
	public float powerLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0.25f;
			case 2: return 0.75f;
		}
		return 0f;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0.8f;
			case 2: return 1.5f;
		}
		return 0f;
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		SpellSprite.show(attacker, SpellSprite.CHARGE);
		for (Wand.Charger c : attacker.buffs(Wand.Charger.class)){
			if (c.wand() != this){
				c.gainCharge(0.33f);
			}
		}

	}
	
	protected int initialCharges() {
		return 3;
	}

	public static class MagicCharge extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		public static float DURATION = 4f;

		@Override
		public void detach() {
			super.detach();
			QuickSlotButton.refresh();
		}

		@Override
		public int icon() {
			return BuffIndicator.RECHARGING;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.2f, 0.6f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns());
		}
	}

}
