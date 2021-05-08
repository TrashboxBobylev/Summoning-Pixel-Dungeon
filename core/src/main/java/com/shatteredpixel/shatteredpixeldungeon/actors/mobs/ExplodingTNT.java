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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Flashbang;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ExplodingTNTSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ExplodingTNT extends Mob {

	{
		spriteClass = ExplodingTNTSprite.class;
		
		HP = HT = 48;
		defenseSkill = 13;
		
		maxLvl = 18;
		EXP = 10;
        if (SPDSettings.bigdungeon()){
            EXP = 18;
            maxLvl = 30;
        }
		FLEEING = new Fleeing();
	}

    public ExplodingTNT() {
        if (SPDSettings.bigdungeon()){
            EXP = 18;
            maxLvl = 30;
        }
    }

    //he doesn't attack in melee
    public boolean attack = true;
    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 8, 13 );
    }

    @Override
	public int attackSkill( Char target ) {
		return 21;
	}

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos && attack;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = Dungeon.level.heroFOV[pos];
	    if (!attack) {
            return super.doAttack(enemy);
        } else {
	        final Ballistica ballistica = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
	        final ExplodingTNT mouse = this;
            Callback call = new Callback() {
                @Override
                public void call() {
                    final Bomb bbbomb = new Bomb(){

                        {
                            image = ItemSpriteSheet.FLASHBANG;
                        }

                        @Override
                        public void explode(int cell) {
                            this.fuse = null;
                            ArrayList<Char> affected = new ArrayList<>();

                            boolean terrainAffected = false;
                            for (int n : PathFinder.NEIGHBOURS9) {
                                int c = cell + n;
                                if (c >= 0 && c < Dungeon.level.length()) {

                                    Char ch = Actor.findChar(c);
                                    if (ch != null) {
                                        affected.add(ch);
                                    }

                                    for (Char cher : affected){

                                        //if they have already been killed by another bomb
                                        if(!cher.isAlive()){
                                            continue;
                                        }

                                        int dmg = /*damageRoll() / 3*/1;
                                        if (buff(Shrink.class) != null || enemy.buff(TimedShrink.class) != null) dmg *= 0.6f;

                                        dmg -= cher.drRoll();

                                        if (dmg > 0) {
                                            cher.damage(dmg, this);
                                        }

                                        if (cher == Dungeon.hero && !cher.isAlive()) {
                                            Dungeon.fail(Bomb.class);
                                        }

                                        int power = 25 - 8*Dungeon.level.distance(cher.pos, cell);
                                        if (power > 0){
                                            if (cher instanceof Mob && !(cher instanceof ExplodingTNT)){
                                                Buff.prolong(cher, Blindness.class, power);
                                                Buff.prolong(cher, Cripple.class, power);
                                                ((Mob) cher).enemy = null;
                                                ((Mob) cher).enemySeen = false;
                                                ((Mob) cher).state = ((Mob) cher).WANDERING;
                                            }
                                        }
                                        if (cher == Dungeon.hero){
                                            GameScene.flash(0xFFFFFF);
                                        }
                                    }
                                }
                            }
                        }
                    };
                    bbbomb.fuse  = new Bomb.Fuse().ignite(bbbomb);
                    Actor.addDelayed( bbbomb.fuse, 1);
                    Heap heap = Dungeon.level.drop( bbbomb, ballistica.collisionPos );
                    if (!heap.isEmpty()) {
                        heap.sprite.drop( ballistica.collisionPos );
                    }
                    spend(TICK);
                    next();

                }
            };
            ((MissileSprite)sprite.parent.recycle(MissileSprite.class)).reset(pos, enemy.pos, new Bomb(), call);
            attack = false;

            Buff.affect(mouse, Timer.class, 10f);

            return !visible;
        }
    }

    @Override
    protected boolean getCloser(int target) {
        if (buff(Timer.class) != null){
            return enemySeen && getFurther(target);
        }
        else return super.getCloser(target);
    }

    @Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}

    {
        immunities.add(Flashbang.class);
        immunities.add(Bomb.class);
        immunities.add(ExplosiveTrap.class);
    }

    @Override
    protected boolean act() {
        boolean result = super.act();

        if (buff(Timer.class) == null) {
            attack = true;
        }
        return result;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("att", attack);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        attack = bundle.getBoolean("att");
    }
}
