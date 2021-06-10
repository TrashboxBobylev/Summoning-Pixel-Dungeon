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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SupportPower;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WizardSprite;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashMap;

public class Wizard extends Minion implements Callback {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = WizardSprite.class;

		properties.add(Property.UNDEAD);

		baseMaxDR = 5;
		baseMinDR = 1;
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
	}

	private static final HashMap<Class<? extends Buff>, Float> DEBUFFS = new HashMap<>();
    static{
        DEBUFFS.put(Cripple.class,        4f);
        DEBUFFS.put(Weakness.class,       3f);
        DEBUFFS.put(Blindness.class,      4f);
        DEBUFFS.put(Slow.class,           3f);
        DEBUFFS.put(Vertigo.class,        3f);
        DEBUFFS.put(Amok.class,           2f);
    }
	
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
			return super.doAttack( enemy );
			
		} else {
			
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}
			
			return !visible;
		}
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {
			if (Random.Int( 1 ) == 0) {
			    Class<? extends FlavourBuff> buff = (Class<? extends FlavourBuff>) Random.chances(DEBUFFS);
				Buff.prolong( enemy, buff, 4 );
				if (buff(SupportPower.class) != null) Buff.prolong(enemy, buff, 4);
			}
			int minDamage = -1, maxDamage = -1;
			switch (lvl){
				case 1: minDamage = 1; maxDamage = 4; break;
				case 2: minDamage = 2; maxDamage = 6; break;
			}
			
			int dmg = Random.Int( minDamage, maxDamage );
			enemy.damage( dmg, new DarkBolt() );
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
	}

	@Override
	protected float attackDelay() {
		float mod = 0;
		switch (lvl){
			case 0: mod = 1; break;
			case 1: mod = 0.75f; break;
			case 2: mod = 0.66f; break;
		}
		return super.attackDelay() * mod;
	}
}
