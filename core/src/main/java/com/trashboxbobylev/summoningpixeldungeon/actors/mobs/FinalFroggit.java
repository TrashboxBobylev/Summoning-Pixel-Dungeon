/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfPrismaticLight;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Grim;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.FinalFroggitSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.WarlockSprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class FinalFroggit extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = FinalFroggitSprite.class;
		
		HP = HT = 90;
		defenseSkill = 20;
		
		EXP = 15;
		maxLvl = 30;
		
		loot = Generator.random();
		lootChance = 1f;

		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 18, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 8);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {
			
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}
			
			return !visible;
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class Bolt{}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {

			Eradication eradication = enemy.buff(Eradication.class);
			float multiplier = 1f;
			if (eradication != null){
			    multiplier = (float) (Math.pow(1.15f, eradication.combo));
            }
			
			int dmg = Math.round(Random.Int( 4, 10 ) * multiplier);


			Buff.prolong( enemy, Eradication.class, Eradication.DURATION ).combo++;

			enemy.damage( dmg, new Bolt() );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Dungeon.fail( getClass() );
				GLog.negative( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}

	{
		resistances.add( Grim.class );
		immunities.add(WandOfPrismaticLight.class);
		immunities.add(Blindness.class);
		immunities.add(Vertigo.class);
	}

    public static class Eradication extends FlavourBuff {

        public static final float DURATION = 4f;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        public int combo;

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("combo", combo);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            combo = bundle.getInt("combo");
        }

        @Override
        public int icon() {
            return BuffIndicator.ERADICATION;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String heroMessage() {
            return Messages.get(this, "heromsg");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(), (float)Math.pow(1.2f, combo));
        }
    }
}
