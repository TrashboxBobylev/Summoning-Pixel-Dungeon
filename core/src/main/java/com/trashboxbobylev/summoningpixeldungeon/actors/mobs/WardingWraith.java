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

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.SoulFlame;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfAggression;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfWarding;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Grim;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.WardingWraithSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.WarlockSprite;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class WardingWraith extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= 0.5f;
	public boolean enraged = false;
	
	{
		spriteClass = WardingWraithSprite.class;
		
		HP = HT = 65;
		defenseSkill = 27;
		
		EXP = 8;
		maxLvl = 23;
		
		loot = new ScrollOfAttunement();
		lootChance = 1f;

		properties.add(Property.INORGANIC);

		Buff.affect(this, RoseWraith.Timer.class, 20f);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos && enraged;
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
	public static class DarkBolt{}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {
			
			int dmg = Random.Int( 13, 18 );
			enemy.damage( dmg, new DarkBolt() );
			
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

    @Override
    public boolean reset() {
	    state = WANDERING;
        return true;
    }

    public Char chooseEnemy() {
	    if (enraged) {
            //find a new enemy if..
            boolean newEnemy = false;
            //we have no enemy, or the current one is dead
            if ( enemy == null || !enemy.isAlive() || state == WANDERING)
                newEnemy = true;
            //We are charmed and current enemy is what charmed us
            else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id())
                newEnemy = true;
            if ( newEnemy ) {

                HashSet<Char> enemies = new HashSet<>();
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ENEMY && mob != this && fieldOfView[mob.pos])
                        enemies.add(mob);

                if (enemies.isEmpty()) {
                    //try to find ally mobs to attack second, ignoring the soul flame
                    for (Mob mob : Dungeon.level.mobs)
                        if (mob.alignment == Alignment.ALLY && mob != this && fieldOfView[mob.pos] && !(mob instanceof SoulFlame))
                            enemies.add(mob);

                    if (enemies.isEmpty()) {
                        //try to find the hero third
                        if (fieldOfView[Dungeon.hero.pos]) {
                            enemies.add(Dungeon.hero);
                        }
                    }
                    }
                    Char closest = null;
                    for (Char curr : enemies){
                        if (closest == null
                                || Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
                                || Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos)){
                            closest = curr;
                        }
                    }
                    return closest;
            }
        }
	    return null;
    }

    @Override
    public float speed() {
        float speed = super.speed();
        if (buff(Amok.class) != null) speed *= 1.5;
        return speed;
    }

    @Override
    public void damage( int dmg, Object src ) {

        if (!enraged) enraged = true;
        Sample.INSTANCE.play(Assets.SND_DEGRADE);
        if (Dungeon.level.heroFOV[pos]) {
            sprite.showStatus( CharSprite.NEGATIVE, Messages.get(Brute.class, "enraged") );
        }

        super.damage( dmg, src );
    }



    @Override
    public void die(Object cause) {
        if (cause == null) EXP = 0;
        super.die(cause);
    }

    @Override
    protected Item createLoot() {
        if (EXP != 0){
            return (Item) loot;
        }
        return null;
    }

    @Override
    protected boolean act() {
        boolean act = super.act();
        if (buff(RoseWraith.Timer.class) == null) die(null);
        return act;
    }

    private final String CHAINSUSED = "chainsused";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHAINSUSED, enraged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        enraged = bundle.getBoolean(CHAINSUSED);
    }

    {
        immunities.add( Grim.class );
        immunities.add( Terror.class );
        immunities.add( Weakness.class);
        immunities.add( Charm.class);
    }
}
