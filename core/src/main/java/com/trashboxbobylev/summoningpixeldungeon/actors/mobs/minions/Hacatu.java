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

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.SparkParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.HacatuSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ShamanSprite;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Hacatu extends Minion implements Callback {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = HacatuSprite.class;
		attunement = 2f;
		
		properties.add(Property.ELECTRIC);
		baseMaxDR = 15;
		baseMinDR = 1;
		baseSpeed = 1.6f;
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return !Dungeon.level.adjacent( pos, enemy.pos ) && new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

    //run away when getting closer
    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }
	
	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt{}

    public ArrayList<Char> affected = new ArrayList<>();
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.distance( pos, enemy.pos ) <= 1) {
			
			return super.doAttack( enemy );
			
		} else {
			
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			}
			
			spend( TIME_TO_ZAP );
			next();
			
			if (hit( this, enemy, true )) {

                //lightning deals less damage per-target, the more targets that are hit.
                float multipler = 0.4f + (0.6f/affected.size());
                //if the main target is in water, all affected take full damage
                if (Dungeon.level.water[enemy.pos]) multipler = 1f;

                int min = minDamage;
                int max = maxDamage;

                for (Char ch : affected){
                    ch.damage(Math.round(damageRoll() * multipler), this);

                    if (ch == Dungeon.hero) Camera.main.shake( 2, 0.3f );
                    ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
                    ch.sprite.flash();
                }

                if (!Dungeon.hero.isAlive()) {
                    Dungeon.fail( getClass() );
                    GLog.negative(Messages.get(Char.class, "kill", name));
                }
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			return !visible;
		}
	}
	
	@Override
	public void call() {
		next();
	}
	
}