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
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Amok;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Terror;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Weakness;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfWarding;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.enchantments.Grim;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.WardingWraithSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.WarlockSprite;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WardingWraith extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= 0.5f;
	
	{
		spriteClass = WardingWraithSprite.class;
		
		HP = HT = 72;
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
    public Char chooseEnemy() {
	    if (buff(Amok.class) != null) return null;
        else return super.chooseEnemy();
    }

    @Override
    public float speed() {
        float speed = super.speed();
        if (buff(Amok.class) != null) speed *= 1.5;
        return speed;
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        Sample.INSTANCE.play(Assets.SND_SPIRIT);
        if (isAlive() && buff(Amok.class) != null){
            Buff.affect(this, Amok.class, 20f);
            if (src instanceof Char) enemy = (Char) src;
        }
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

    {
        immunities.add( Grim.class );
        immunities.add( Terror.class );
        immunities.add( Weakness.class);
    }
}
