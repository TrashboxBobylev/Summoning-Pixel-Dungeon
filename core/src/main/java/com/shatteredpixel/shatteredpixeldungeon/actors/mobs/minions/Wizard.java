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

import static com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff.ELEMENT_RESISTS;

public class Wizard extends Minion implements Callback {

	{
		spriteClass = WizardSprite.class;

		properties.add(Property.UNDEAD);
		properties.add(Property.RANGED);

		baseDefense = 8;
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

	@Override
	public int damage(int dmg, Object src) {
		for (Class c : ELEMENT_RESISTS){
			if (c.isAssignableFrom(src.getClass())){
				dmg *= 0.75 - lvl*0.15f;
			}
		}
		return super.damage(dmg, src);
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}
	
	private void zap() {
		spend( attackDelay() );
		
		if (hit( this, enemy, true )) {
			if (Random.Int( 1 ) == 0) {
			    Class<? extends FlavourBuff> buff = (Class<? extends FlavourBuff>) Random.chances(DEBUFFS);
				Buff.prolong( enemy, buff, 8 - lvl*3 );
				if (buff(SupportPower.class) != null) Buff.prolong(enemy, buff, 4);
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
	}

	@Override
	protected float attackDelay() {
		float mod = 0;
		switch (lvl){
			case 0: mod = 1; break;
			case 1: mod = 0.66f; break;
			case 2: mod = 0.5f; break;
		}
		return super.attackDelay() * mod;
	}
}
