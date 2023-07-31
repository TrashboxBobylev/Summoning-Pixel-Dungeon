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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FinalFroggitSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class FinalFroggit extends AbyssalMob implements Callback {
	
	private static final float TIME_TO_ZAP	= Dungeon.mode == Dungeon.GameMode.DIFFICULT ? 0.5f : 1f;
	
	{
		spriteClass = FinalFroggitSprite.class;
		
		HP = HT = 90;
		defenseSkill = 20;
		
		EXP = 20;
		maxLvl = 30;
		
		loot = Generator.random();
		lootChance = 1f;

		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
		properties.add(Property.RANGED);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 18, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30 + abyssLevel()*10;
	}

	@Override
	public int defenseValue() {
		return 8 + abyssLevel()*15;
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
	public static class Bolt extends MagicalAttack{
		public Bolt(Mob attacker, int damage) {
			super(attacker, damage);
		}
	}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {

			Eradication eradication = enemy.buff(Eradication.class);
			float multiplier = 1f;
			if (eradication != null){
			    multiplier = (float) (Math.pow(1.2f, eradication.combo));
            }
			int damage = Random.Int( 4 + abyssLevel()*4, 10 + abyssLevel()*8 );
			if (buff(Shrink.class) != null|| enemy.buff(TimedShrink.class) != null) damage *= 0.6f;
			
			int dmg = Math.round(damage * multiplier);


			Buff.prolong( enemy, Eradication.class, Eradication.DURATION ).combo++;

			enemy.damage( dmg, new Bolt(this, damage) );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
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
        public String desc() {
            return Messages.get(this, "desc", dispTurns(), (float)Math.pow(1.2f, combo));
        }
    }
}
