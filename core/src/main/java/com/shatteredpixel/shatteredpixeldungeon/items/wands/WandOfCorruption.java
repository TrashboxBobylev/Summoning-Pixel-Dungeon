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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class WandOfCorruption extends Wand {

	{
		image = ItemSpriteSheet.WAND_CORRUPTION;

		chakraGain = 4;
	}

	ConeAOE cone;
	
	//Note that some debuffs here have a 0% chance to be applied.
	// This is because the wand of corruption considers them to be a certain level of harmful
	// for the purposes of reducing resistance, but does not actually apply them itself
	
	private static final float MINOR_DEBUFF_WEAKEN = 1/4f;
	private static final HashMap<Class<? extends Buff>, Float> MINOR_DEBUFFS = new HashMap<>();
	static{
		MINOR_DEBUFFS.put(Weakness.class,       2f);
		MINOR_DEBUFFS.put(Vulnerable.class,     2f);
		MINOR_DEBUFFS.put(Cripple.class,        1f);
		MINOR_DEBUFFS.put(Blindness.class,      1f);
		MINOR_DEBUFFS.put(Terror.class,         1f);
		
		MINOR_DEBUFFS.put(Chill.class,          0f);
		MINOR_DEBUFFS.put(Ooze.class,           0f);
		MINOR_DEBUFFS.put(Roots.class,          0f);
		MINOR_DEBUFFS.put(Vertigo.class,        0f);
		MINOR_DEBUFFS.put(Drowsy.class,         0f);
		MINOR_DEBUFFS.put(Bleeding.class,       0f);
		MINOR_DEBUFFS.put(Burning.class,        0f);
		MINOR_DEBUFFS.put(Poison.class,         0f);
	}
	
	private static final float MAJOR_DEBUFF_WEAKEN = 1/2f;
	private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS = new HashMap<>();
	static{
		MAJOR_DEBUFFS.put(Amok.class,           3f);
		MAJOR_DEBUFFS.put(Slow.class,           2f);
		MAJOR_DEBUFFS.put(Hex.class,            2f);
		MAJOR_DEBUFFS.put(Paralysis.class,      1f);
		
		MAJOR_DEBUFFS.put(Charm.class,          0f);
		MAJOR_DEBUFFS.put(MagicalSleep.class,   0f);
		MAJOR_DEBUFFS.put(SoulMark.class,       0f);
		MAJOR_DEBUFFS.put(Corrosion.class,      0f);
		MAJOR_DEBUFFS.put(Frost.class,          0f);
		MAJOR_DEBUFFS.put(Doom.class,           0f);
	}
	
	@Override
	public void onZap(Ballistica bolt) {
		if (level() < 2) {
			Char ch = Actor.findChar(bolt.collisionPos);

			if (ch != null) {

				performCorruption(ch);

			} else {
				Dungeon.level.pressCell(bolt.collisionPos);
			}
		} else {
			ArrayList<Char> affectedChars = new ArrayList<>();
			for( int cell : cone.cells ){

				//ignore caster cell
				if (cell == bolt.sourcePos){
					continue;
				}

				//knock doors open
				if (Dungeon.level.map[cell] == Terrain.DOOR){
					Level.set(cell, Terrain.OPEN_DOOR);
					GameScene.updateMap(cell);
				}

				Char ch = Actor.findChar( cell );
				if (ch != null) {
					affectedChars.add(ch);
				}
			}

			for ( Char ch : affectedChars ){
				if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) {
					performCorruption(ch);
				}
			}
		}
	}

	public void performCorruption(Char ch) {
		if (!(ch instanceof Mob)){
			return;
		}

		Mob enemy = (Mob) ch;

		float corruptingPower = getCorruptingPower();

		//base enemy resistance is usually based on their exp, but in special cases it is based on other criteria
		float enemyResist = getEnemyResist(ch, enemy);

		if (Dungeon.hero.pointsInTalent(Talent.WITCHING_STRIKE) < 3) {
			//100% health: 3x resist   75%: 2.1x resist   50%: 1.5x resist   25%: 1.1x resist
			enemyResist *= 1 + 2 * Math.pow(enemy.HP / (float) enemy.HT, 2);
		}

		//debuffs placed on the enemy reduce their resistance
		for (Buff buff : enemy.buffs()){
			if (MAJOR_DEBUFFS.containsKey(buff.getClass()))         enemyResist *= (1f-MAJOR_DEBUFF_WEAKEN);
			else if (MINOR_DEBUFFS.containsKey(buff.getClass()))    enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
			else if (buff.type == Buff.buffType.NEGATIVE)           enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
		}

		//cannot re-corrupt or doom an enemy, so give them a major debuff instead
		if(enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null){
			corruptingPower = enemyResist - 0.001f;
		}

		if (corruptingPower > enemyResist && level() != 1){
			corruptEnemy(this, enemy );
		} else {
			float debuffChance = corruptingPower / enemyResist;
			if (Random.Float() < debuffChance){
				debuffEnemy( enemy, MAJOR_DEBUFFS);
			} else {
				debuffEnemy( enemy, MINOR_DEBUFFS);
			}
		}

		processSoulMark(ch, chargesPerCast());
		Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 0.8f * Random.Float(0.87f, 1.15f) );
	}

	public int getCorruptingPower() {
		return (int) (7 + Dungeon.hero.lvl/2.5f);
	}

	public static float getEnemyResist(Char ch, Mob enemy) {
        float enemyResist = 1 + enemy.EXP;
        if (ch instanceof Mimic || ch instanceof Statue){
            enemyResist = 1 + Dungeon.chapterNumber();
        } else if (ch instanceof Piranha || ch instanceof Bee) {
            enemyResist = 1 + Dungeon.depth/2f * 5 / Dungeon.chapterSize();
        } else if (ch instanceof Wraith) {
            //this is so low because wraiths are always at max hp
            enemyResist = 0.5f + Dungeon.depth/8f * 5 / Dungeon.chapterSize();
			if (Dungeon.hero.pointsInTalent(Talent.WITCHING_STRIKE) > 2)
				enemyResist *= 3;
        } else if (ch instanceof Yog.BurningFist || ch instanceof Yog.RottingFist) {
            enemyResist = 1 + 30;
        } else if (ch instanceof Yog.Larva){
            enemyResist = 1 + 5;
        } else if (ch instanceof Swarm){
            //child swarms don't give exp, so we force this here.
            enemyResist = 1 + 3;
        }
		if (Dungeon.hero.pointsInTalent(Talent.WITCHING_STRIKE) > 2)
			enemyResist *= 2;
        return enemyResist;
    }

    private void debuffEnemy( Mob enemy, HashMap<Class<? extends Buff>, Float> category ){
		
		//do not consider buffs which are already assigned, or that the enemy is immune to.
		HashMap<Class<? extends Buff>, Float> debuffs = new HashMap<>(category);
		for (Buff existing : enemy.buffs()){
			if (debuffs.containsKey(existing.getClass())) {
				debuffs.put(existing.getClass(), 0f);
			}
		}
		for (Class<?extends Buff> toAssign : debuffs.keySet()){
			 if (debuffs.get(toAssign) > 0 && enemy.isImmune(toAssign)){
			 	debuffs.put(toAssign, 0f);
			 }
		}
		
		//all buffs with a > 0 chance are flavor buffs
		Class<?extends FlavourBuff> debuffCls = (Class<? extends FlavourBuff>) Random.chances(debuffs);
		
		if (debuffCls != null){
			Buff.append(enemy, debuffCls, 8);
		} else {
			//if no debuff can be applied (all are present), then go up one tier
			if (category == MINOR_DEBUFFS)          debuffEnemy( enemy, MAJOR_DEBUFFS);
			else if (category == MAJOR_DEBUFFS)     corruptEnemy(this, enemy );
		}
	}
	
	public static void corruptEnemy(WandOfCorruption wandOfCorruption, Mob enemy){
		//cannot re-corrupt or doom an enemy, so give them a major debuff instead
		if(enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null){
			GLog.warning( Messages.get(wandOfCorruption, "already_corrupted") );
			return;
		}
		
		if (!enemy.isImmune(Corruption.class)){
			enemy.HP = enemy.HT;
			for (Buff buff : enemy.buffs()) {
				if (buff.type == Buff.buffType.NEGATIVE
						&& !(buff instanceof SoulMark)) {
					buff.detach();
				} else if (buff instanceof PinCushion){
					buff.detach();
				}
			}
			if (enemy.alignment == Char.Alignment.ENEMY){
				enemy.rollToDropLoot();
			}
			
			Buff.affect(enemy, Corruption.class);
			if (Dungeon.hero.pointsInTalent(Talent.WITCHING_STRIKE) > 2)
				Buff.affect(enemy, Talent.WitchingStrikeCorruptionDelay.class);
			
			Statistics.enemiesSlain++;
			Badges.validateMonstersSlain();
			Statistics.qualifiedForNoKilling = false;
			if (Dungeon.mode == Dungeon.GameMode.NO_EXP || (enemy.EXP > 0 && (curUser.lvl <= enemy.maxLvl || Dungeon.mode == Dungeon.GameMode.LOL))) {
				curUser.sprite.showStatus(CharSprite.POSITIVE, Messages.get(enemy, "exp", enemy.EXP));
				curUser.earnExp(enemy.EXP, enemy.getClass());
			} else {
				curUser.earnExp(0, enemy.getClass());
			}
		} else {
			Buff.affect(enemy, Doom.class);
		}
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1)),
				getCorruptingPower()
		);
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 0.5f;
			case 2: return 5f;
		}
		return 0f;
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		// lvl 0 - 25%
		// lvl 1 - 40%
		// lvl 2 - 50%
		if (Random.Int( Dungeon.hero.lvl/3 + 4 ) >= 3){
			Buff.prolong( defender, Amok.class, 4+ buffedLvl()*2);
		}
	}

	@Override
	public int collisionProperties(int target) {
		if (level() == 2) return Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID;
		return super.collisionProperties(target);
	}

	@Override
    public void fx(Ballistica bolt, Callback callback) {
		if (level() == 2){
			//need to perform flame spread logic here so we can determine what cells to put flames in.
			int maxDist = 7;
			int dist = Math.min(bolt.dist, maxDist);

			cone = new ConeAOE( bolt,
					maxDist,
					60,
					collisionProperties(0) | Ballistica.STOP_TARGET);

			//cast to cells at the tip, rather than all cells, better performance.
			for (Ballistica ray : cone.rays){
				((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
						MagicMissile.SHADOW_CONE,
						curUser.sprite,
						ray.path.get(ray.dist),
						null
				);
			}

			//final zap at half distance, for timing of the actual wand effect
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.SHADOW_CONE,
					curUser.sprite,
					bolt.path.get(dist/2),
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		} else {
			MagicMissile.boltFromChar(curUser.sprite.parent,
					MagicMissile.SHADOW,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play(Assets.Sounds.ZAP);
		}
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( 0 );
		particle.am = 0.6f;
		particle.setLifespan(2f);
		particle.speed.set(0, 5);
		particle.setSize( 0.5f, 2f);
		particle.shuffleXY(1f);
	}

}
