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
import com.trashboxbobylev.summoningpixeldungeon.Badges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.LockedFloor;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Paralysis;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Terror;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ElmoParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.DriedRose;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.LloydsBeacon;
import com.trashboxbobylev.summoningpixeldungeon.items.keys.SkeletonKey;
import com.trashboxbobylev.summoningpixeldungeon.items.quest.MetalShard;
import com.trashboxbobylev.summoningpixeldungeon.levels.Level;
import com.trashboxbobylev.summoningpixeldungeon.levels.Terrain;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.DM300Sprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.BossHealthBar;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM300 extends Mob {
	
	{
		spriteClass = DM300Sprite.class;
		
		HP = HT = 200;
		EXP = 0;
		defenseSkill = 18;
		

		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 20, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 10);
	}
	
	@Override
	public boolean act() {
		
		GameScene.add( Blob.seed( pos, 30, ToxicGas.class ) );
		
		return super.act();
	}
	
	@Override
	public void move( int step ) {
		super.move( step );
		
		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && HP < HT) {
			
			HP += Random.Int( 1, HT - HP );
			sprite.emitter().burst( ElmoParticle.FACTORY, 5 );
			
			if (Dungeon.level.heroFOV[step] && Dungeon.hero.isAlive()) {
				GLog.negative( Messages.get(this, "repair") );
			}
		}
		
		int[] cells = {
			step-1, step+1, step-Dungeon.level.width(), step+Dungeon.level.width(),
			step-1-Dungeon.level.width(),
			step-1+Dungeon.level.width(),
			step+1-Dungeon.level.width(),
			step+1+Dungeon.level.width()
		};
		int cell = cells[Random.Int( cells.length )];
		
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.get( cell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
			Camera.main.shake( 3, 0.7f );
			Sample.INSTANCE.play( Assets.SND_ROCKS );
			
			if (Dungeon.level.water[cell]) {
				GameScene.ripple( cell );
			} else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
				Level.set( cell, Terrain.EMPTY_DECO );
				GameScene.updateMap( cell );
			}
		}

		Char ch = Actor.findChar( cell );
		if (ch != null && ch != this) {
			Buff.prolong( ch, Paralysis.class, 2 );
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass())) lock.addTime(dmg*1.5f);
	}

	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth  ), pos ).sprite.drop();
        Dungeon.hero.earnExp( Dungeon.hero.maxExp(), getClass() );
		
		//60% chance of 2 shards, 30% chance of 3, 10% chance for 4. Average of 2.5
		int shards = Random.chances(new float[]{0, 0, 6, 3, 1});
		for (int i = 0; i < shards; i++){
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop( new MetalShard(), pos + ofs ).sprite.drop( pos );
		}
		
		Badges.validateBossSlain();

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(this, "defeated") );
	}
	
	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					GLog.negative("\n");
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}
	
	{
		immunities.add( ToxicGas.class );
		immunities.add( Terror.class );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
	}
}
