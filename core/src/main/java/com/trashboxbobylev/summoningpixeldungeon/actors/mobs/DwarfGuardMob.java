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

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Cripple;
import com.trashboxbobylev.summoningpixeldungeon.effects.Chains;
import com.trashboxbobylev.summoningpixeldungeon.effects.Pushing;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.Armor;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfAggression;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.DwarfGuard;
import com.trashboxbobylev.summoningpixeldungeon.sprites.GuardSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class DwarfGuardMob extends Mob {

	//they can only use their chains once
	private boolean chainsUsed = false;
	public boolean stasis = false;

	{
		spriteClass = DwarfGuard.class;

		HP = HT = 100;
		defenseSkill = 20;

		EXP = 13;
		maxLvl = 23;

		loot = null;    //see createloot.
		lootChance = 0.25f;

		properties.add(Property.UNDEAD);
		
		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 27);
	}

	private boolean chain(int target){
		if (chainsUsed || enemy.properties().contains(Property.IMMOVABLE))
			return false;

		Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

		if (chain.collisionPos != enemy.pos
				|| chain.path.size() < 6
				|| Dungeon.level.pit[chain.path.get(1)])
			return false;
		else {
			int newPos = -1;
			for (int i : chain.subPath(1, chain.dist)){
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
				sprite.parent.add(new Chains(sprite.center(), enemy.sprite.center(), new Callback() {
					public void call() {
						Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
							public void call() {
								enemy.pos = newPosFinal;
                                Dungeon.level.occupyCell(enemy);
								Cripple.prolong(enemy, Cripple.class, 4f);
								if (enemy == Dungeon.hero) {
									Dungeon.hero.interrupt();
									Dungeon.observe();
									GameScene.updateFog();
								}
							}
						}), -1);
						next();
					}
				}));
			}
		}
		chainsUsed = true;
		return true;
	}

    @Override
    public void damage( int dmg, Object src ) {
        if (!stasis) super.damage( dmg, src );

        if (isAlive() && !stasis && HP < HT / 2) {
            stasis = true;
            spend( TICK );
            if (Dungeon.level.heroFOV[pos]) {
                sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "stasis") );
            }
        }
    }

    @Override
    protected boolean act() {
        boolean result = super.act();
        if (stasis){
                for (Char ch : Actor.chars()) {
                    if (ch instanceof WardingWraith && fieldOfView[ch.pos] && ch.HP < ch.HT) {
                        HP = Math.min(HT, HP + 8);
                        sprite.emitter().burst(Speck.factory(Speck.HEALING), 4);
                        if (HP > HT * 0.5f){
                            stasis = false;
                            spend(TICK);
                            return result;
                        }
                    }
                }
            }
        return result;
    }

    @Override
    protected boolean canAttack(Char enemy) {
	    if (stasis) return false;
        return super.canAttack(enemy);
    }

    @Override
    protected boolean getCloser(int target) {
	    if (stasis) return false;
        return super.getCloser(target);
    }

    @Override
    protected boolean getFurther(int target) {
	    if (stasis) return false;
        return super.getFurther(target);
    }

    @Override
    public void add(Buff buff) {
        if (!stasis) super.add(buff);
    }

    @Override
	public int attackSkill( Char target ) {
		return 30;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(4, 15);
	}

	@Override
	protected Item createLoot() {
		//first see if we drop armor, overall chance is 1/8
		if (Random.Int(2) == 0){
			Armor loot;
			do{
				loot = Generator.randomArmor();
				//50% chance of re-rolling tier 4 or 5 items
			} while (loot.tier <= 4 && Random.Int(2) == 0);
			loot.level(0);
			return loot;
		}

		return null;
	}

	private final String CHAINSUSED = "chainsused";
    private final String STASIS = "stasis";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAINSUSED, chainsUsed);
		bundle.put(STASIS, stasis);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chainsUsed = bundle.getBoolean(CHAINSUSED);
		stasis = bundle.getBoolean(STASIS);
	}
	
	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			
			if (!chainsUsed
					&& enemyInFOV
					&& !isCharmedBy( enemy )
					&& !canAttack( enemy )
					&& Dungeon.level.distance( pos, enemy.pos ) < 8
					&& Random.Int(3) == 0
					
					&& chain(enemy.pos) && !stasis){
				return false;
			} else {
				return super.act( enemyInFOV, justAlerted );
			}
			
		}
	}
}
