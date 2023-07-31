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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfBlastWave extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_BLAST_WAVE;

		collisionProperties = Ballistica.FRIENDLY_PROJECTILE;
	}

	@Override
	public int magicalmin(int lvl){
		return (int) ((1 + Dungeon.hero.lvl*0.25f));
	}

	@Override
	public int magicalmax(int lvl) {
		return (int) (3 + Dungeon.hero.lvl*0.75f);
	}

	@Override
	public float powerLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 2.75f;
			case 2: return 0.75f;
		}
		return 0f;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.55f;
			case 2: return 1.1f;
		}
		return 0f;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		BlastWave.blast(bolt.collisionPos);

		//presses all tiles in the AOE first, with the exception of tengu dart traps
		for (int i : PathFinder.NEIGHBOURS9){
			if (!(Dungeon.level.traps.get(bolt.collisionPos+i) instanceof TenguDartTrap)) {
				Dungeon.level.pressCell(bolt.collisionPos + i);
			}
		}

		//throws other chars around the center.
		for (int i  : PathFinder.NEIGHBOURS8){
                    Char ch = Actor.findChar(bolt.collisionPos + i);

			if (ch != null){
				processSoulMark(ch, chargesPerCast());
				if (ch.alignment != Char.Alignment.ALLY || (Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) ch.damage(damageRoll(), this);

                        if (ch.isAlive() && ch.pos == bolt.collisionPos + i) {
                            Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
                            int strength = 2;
                            switch (level()){
								case 1: strength = 0; break;
								case 2: strength = 4; break;
							}
                            throwChar(ch, trajectory, strength, false);
                        } else if (ch == Dungeon.hero){
							Badges.validateDeathFromFriendlyMagic();
                            Dungeon.fail( getClass() );
                            GLog.negative( Messages.get( this, "ondeath") );
                        }
			}
		}

		//throws the char at the center of the blast
		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch != null){
			processSoulMark(ch, chargesPerCast());
            if (!(ch instanceof Hero)) ch.damage(damageRoll(), this);

			if (ch.isAlive() && bolt.path.size() > bolt.dist+1 && ch.pos == bolt.collisionPos) {
				Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
				int strength = 3;
				switch (level()){
					case 1: strength = 0; break;
					case 2: strength = 6; break;
				}
				throwChar(ch, trajectory, strength, false);
			}
		}
		
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power){
		throwChar(ch, trajectory, power, true, true, false);
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
	                             boolean closeDoors) {
		throwChar(ch, trajectory, power, closeDoors, true, false);
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
								 boolean closeDoors, boolean collideDmg) {
		throwChar(ch, trajectory, power, closeDoors, collideDmg, false);
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
	                             boolean closeDoors, boolean collideDmg, boolean trulyCollideDmg){
		if (ch.properties().contains(Char.Property.BOSS)) {
			power /= 2;
		}

		int dist = Math.min(trajectory.dist, power);

		boolean collided = dist == trajectory.dist;

		if (dist == 0 || ch.properties().contains(Char.Property.IMMOVABLE)) return;

		//large characters cannot be moved into non-open space
		if (Char.hasProp(ch, Char.Property.LARGE)) {
			for (int i = 1; i <= dist; i++) {
				if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
					dist = i-1;
					collided = true;
					break;
				}
			}
		}

		if (Actor.findChar(trajectory.path.get(dist)) != null){
			dist--;
			collided = true;
		}

		if (dist < 0) return;

		final int newPos = trajectory.path.get(dist);

		if (newPos == ch.pos) return;

		final int finalDist = dist;
		final boolean finalCollided = collided && collideDmg;
		final int initialpos = ch.pos;

		Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
			public void call() {
				if (initialpos != ch.pos) {
					//something caused movement before pushing resolved, cancel to be safe.
					ch.sprite.place(ch.pos);
					return;
				}
				int oldPos = ch.pos;
				ch.pos = newPos;
				if (finalCollided && ch.isAlive()) {
					ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), this);
					Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2);
				}
				if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
					Door.leave(oldPos);
				}
				Dungeon.level.occupyCell(ch);
				if (ch == Dungeon.hero){
					//FIXME currently no logic here if the throw effect kills the hero
					Dungeon.observe();
				}
			}
		}), -1);
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		float procChance = (Dungeon.hero.lvl/3+1f)/(Dungeon.hero.lvl/3+5f);
		if (Random.Float() < procChance) {
			//trace a ballistica to our target (which will also extend past them
			Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(defender, trajectory, 2, true);
		}
	}

	@Override
    public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FORCE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( 0x664422 ); particle.am = 0.6f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	public static class BlastWave extends Image {

		private static final float TIME_TO_FADE = 0.2f;

		private float time;

		public BlastWave(){
			super(Effects.get(Effects.Type.RIPPLE));
			origin.set(width / 2, height / 2);
		}

		public void reset(int pos) {
			revive();

			x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
			y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

			time = TIME_TO_FADE;
		}

		@Override
		public void update() {
			super.update();

			if ((time -= Game.elapsed) <= 0) {
				kill();
			} else {
				float p = time / TIME_TO_FADE;
				alpha(p);
				scale.y = scale.x = (1-p)*3;
			}
		}

		public static void blast(int pos) {
			Group parent = Dungeon.hero.sprite.parent;
			BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
			parent.bringToFront(b);
			b.reset(pos);
		}

	}
}
