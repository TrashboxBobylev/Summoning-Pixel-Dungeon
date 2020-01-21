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
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Web;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Poison;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Terror;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.effects.Splash;
import com.trashboxbobylev.summoningpixeldungeon.effects.WhiteWound;
import com.trashboxbobylev.summoningpixeldungeon.items.food.MysteryMeat;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.SpinnerSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Spinner extends Mob {

	{
		spriteClass = SpinnerSprite.class;

		HP = HT = 50;
		defenseSkill = 14;

		EXP = 9;
		maxLvl = 17;

		loot = new MysteryMeat();
		lootChance = 0.125f;

		FLEEING = new Fleeing();
		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10, 25);
	}

	@Override
	public int attackSkill(Char target) {
		return 20;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 6);
	}

	@Override
	protected boolean act() {
        if (buff(RoseWraith.Timer.class) != null) sprite.showStatus(CharSprite.DEFAULT, String.valueOf(Math.round(buff(RoseWraith.Timer.class).cooldown()+1)));
        if (enemy != null && state == HUNTING && buff(RoseWraith.Timer.class) == null && enemySeen) {
            final Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
            final Char spinner = this;
            if (Dungeon.level.heroFOV[pos]) sprite.zap(enemy.pos);
            MagicMissile.boltFromChar( sprite.parent,
                    MagicMissile.SPINNER,
                    sprite,
                    enemy.pos,
                    new Callback() {
                        @Override
                        public void call() {
                            int cell = ballistica.collisionPos;
                            Sample.INSTANCE.play(Assets.SND_PUFF);
                            for (int i : PathFinder.NEIGHBOURS8) {
                                if (Dungeon.level.passable[cell + i]) GameScene.add(Blob.seed(cell + i, Random.Int(5, 10), Web.class));
                                CellEmitter.get(cell + i).burst(MagicMissile.ForceParticle.FACTORY, 15);
                            }
                            Buff.affect(spinner, RoseWraith.Timer.class, 10f);
                            return;
                        }
                    } );
            Sample.INSTANCE.play(Assets.SND_BADGE);
        }
		return super.act();
	}

    @Override
    protected boolean getCloser( int target ) {
        if (enemy != null) {
            if (Dungeon.level.distance(pos, enemy.pos) >= 3) {
                return false;
            } else return enemySeen && getFurther(target);
        } else {
            return super.getCloser(target);
        }
    }

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int(2) == 0) {
			Buff.affect(enemy, Poison.class).set(Random.Int(7, 9) );
		}

		return damage;
	}

	@Override
	public void move(int step) {
		int curWeb = Blob.volumeAt(pos, Web.class);
		if (state == FLEEING && curWeb < 5) {
			GameScene.add(Blob.seed(pos, Random.Int(5, 7) - curWeb, Web.class));
		}
		super.move(step);
	}

	{
		resistances.add(Poison.class);
	}
	
	{
		immunities.add(Web.class);
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}

	private boolean trap(int target){
	    if (enemy.properties().contains(Property.IMMOVABLE) || buff (RoseWraith.Timer.class) != null) return false;

	    final Ballistica path = new Ballistica(pos, target, Ballistica.PROJECTILE);
	    if (path.collisionPos != enemy.pos) return false;
	    else {
            int newPos = -1;
            for (int i : path.subPath(1, path.dist)){
                if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                    newPos = i;
                    break;
                }
            }

            if (newPos == -1){
                return false;
            } else {
                final int newPosFinal = newPos;
                this.target = newPos;
                yell( Messages.get(this, "scorpion") );
                final Char spinner = this;
                if (Dungeon.level.heroFOV[pos]) sprite.zap(enemy.pos);
                MagicMissile.boltFromChar( sprite.parent,
                        MagicMissile.SPINNER,
                        sprite,
                        enemy.pos,
                        new Callback() {
                            @Override
                            public void call() {
                                int cell = path.collisionPos;
                                Sample.INSTANCE.play(Assets.SND_PUFF);
                                for (int i : PathFinder.NEIGHBOURS8) {
                                    if (Dungeon.level.passable[cell + i]) GameScene.add(Blob.seed(cell + i, Random.Int(5, 10), Web.class));
                                    CellEmitter.get(cell + i).burst(MagicMissile.ForceParticle.FACTORY, 15);
                                }
                                Dungeon.hero.interrupt();

                            }
                        } );
                Sample.INSTANCE.play(Assets.SND_BADGE);
                next();
            }
        }
        Buff.affect(this, RoseWraith.Timer.class, 10f);
	    return true;
    }

    private class Hunting extends Mob.Hunting{
        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;

            if (buff(RoseWraith.Timer.class) == null
                    && enemyInFOV
                    && !isCharmedBy( enemy )
                    && trap(enemy.pos)){
                return false;
            } else {
                return super.act( enemyInFOV, justAlerted );
            }

        }
    }
}
