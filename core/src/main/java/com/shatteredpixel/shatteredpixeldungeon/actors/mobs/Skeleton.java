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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Block;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Skeleton extends Mob {
	
	{
		spriteClass = SkeletonSprite.class;
		
		HP = HT = 25;
		defenseSkill = 9;
		
		EXP = 5;
		maxLvl = 10;

		loot = Generator.Category.WEAPON;
		lootChance = 0.1667f; //by default, see rollToDropLoot()

		properties.add(Property.UNDEAD);
		properties.add(Property.INORGANIC);
	}

	{
		immunities.add(ChampionEnemy.Explosive.class);
		immunities.add(ChampionEnemy.Reflective.class);
		immunities.add(ChampionEnemy.AntiMagic.class);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 9 );
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		if (cause == Chasm.class) return;
		
		boolean heroKilled = false;
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
			if (ch != null){
			if (hit(this, ch, true)) {
				if (ch.isAlive()){
					int damage = Random.NormalIntRange(6, 10);
					damage = Math.max(0, damage - (ch.drRoll() + ch.drRoll()));
					ch.damage(damage, this);
					if (ch == Dungeon.hero && !ch.isAlive()) {
						heroKilled = true;
					}
				}
			}
			} else if (ch !=null){
				ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.BONES );
		}
		
		if (heroKilled) {
			Dungeon.fail( getClass() );
			GLog.negative( Messages.get(this, "explo_kill") );
		}
	}

	@Override
	protected boolean act() {
		boolean act = super.act();
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT) {
			if (Dungeon.level.distance(pos, Dungeon.hero.pos) > 1 && Dungeon.hero == enemy){
				Buff.affect(this, Block.class, 1f);
				SpellSprite.show(this, SpellSprite.SHIELD);
				spend(1 / speed());
			}
		}
		return act;
	}

	@Override
	public void rollToDropLoot() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/6, 1/12, 1/24, 1/48, etc.
		lootChance *= Math.pow(1/2f, Dungeon.LimitedDrops.SKELE_WEP.count);
		super.rollToDropLoot();
	}

	@Override
    public Item createLoot() {
		Dungeon.LimitedDrops.SKELE_WEP.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

}
