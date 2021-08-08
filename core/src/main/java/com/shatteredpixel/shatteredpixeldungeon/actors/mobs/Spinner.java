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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpinnerSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Spinner extends Mob {

	{
		spriteClass = SpinnerSprite.class;

		HP = HT = 50;
		defenseSkill = 12;

		EXP = 9;
		maxLvl = 17;

		loot = new MysteryMeat();
		lootChance = 0.125f;

		FLEEING = new Fleeing();
		HUNTING = new Hunting();
		properties.add(Property.RANGED);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(4, 13);
	}

	@Override
	public int attackSkill(Char target) {
		return 18;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

	@Override
	protected boolean act() {
        if (buff(RoseWraith.Timer.class) != null) sprite.showStatus(CharSprite.DEFAULT, String.valueOf(Math.round(buff(RoseWraith.Timer.class).cooldown()+1)));
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
			Buff.affect(enemy, Poison.class).set(Random.Int(8, 12) );
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

	private int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}
	
	private int right(int direction){
		return direction == 7 ? 0 : direction+1;
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
                                Sample.INSTANCE.play(Assets.Sounds.PUFF);
                                sprite.zap(cell);
                                for (int i : PathFinder.NEIGHBOURS8) {
                                    if (Dungeon.level.passable[cell + i]) GameScene.add(Blob.seed(cell + i, Random.Int(5, 10), Web.class));
                                    CellEmitter.get(cell + i).burst(MagicMissile.ForceParticle.FACTORY, 15);
                                }
                                Dungeon.hero.interrupt();

                            }
                        } );
                Sample.INSTANCE.play(Assets.Sounds.BADGE);
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
