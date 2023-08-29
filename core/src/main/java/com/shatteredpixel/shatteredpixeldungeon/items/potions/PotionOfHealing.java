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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.HealGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfHealing extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_HEALING;

		bones = true;
	}
	
	@Override
	public void apply( Hero hero ) {
		setKnown();
		cure( hero );
		heal( hero );
	}

	public static void heal( Char ch ){
		int amount = (int) (0.8f * ch.HT + 4);
		if (Dungeon.hero.hasTalent(Talent.SUPPORT_POTION) && ch.buff(Talent.SupportPotionCooldown.class) == null){
			float modifier = 1.0f;
			switch (Dungeon.hero.pointsInTalent(Talent.SUPPORT_POTION)){
				case 1: modifier = 0.65f; break;
				case 2: modifier = 0.75f; break;
				case 3: modifier = 0.80f; break;
			}
			amount = Math.min( Math.round(amount*modifier), ch.HT - ch.HP );
			if (amount > 0 && ch.isAlive()) {
				ch.HP += amount;
				ch.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
				ch.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( amount ) );
				Talent.Cooldown.affectHero(Talent.SupportPotionCooldown.class);
				if (Dungeon.hero.pointsInTalent(Talent.SUPPORT_POTION) > 1){
					Buff.affect(ch, Talent.SupportPotionPowerTracker.class);
				}
			}
		} else {
			//starts out healing 20 hp, no longer can heal up to full HP
			Buff.affect(ch, Healing.class).setHeal(amount, 0.2f, 0);
		}
		if (ch == Dungeon.hero){
			GLog.positive( Messages.get(PotionOfHealing.class, "heal") );
		}
	}

	public static void pharmacophobiaProc( Hero hero ){
		// harms the hero for ~40% of their max HP in poison
		Buff.affect( hero, Poison.class).set(4 + hero.lvl/2);
	}
	
	public static void cure( Char ch ) {
		Buff.detach( ch, Poison.class );
		Buff.detach( ch, Cripple.class );
		Buff.detach( ch, Weakness.class );
		Buff.detach( ch, Vulnerable.class );
		Buff.detach( ch, Bleeding.class );
		Buff.detach( ch, Blindness.class );
		Buff.detach( ch, Drowsy.class );
		Buff.detach( ch, Slow.class );
		Buff.detach( ch, Vertigo.class);
	}

    @Override
    public void shatter( int cell ) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash( cell );
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
        }

        GameScene.add( Blob.seed( cell, 500 + 75*Dungeon.hero.pointsInTalent(Talent.TOXIC_RELATIONSHIP), HealGas.class ) );
    }

	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
