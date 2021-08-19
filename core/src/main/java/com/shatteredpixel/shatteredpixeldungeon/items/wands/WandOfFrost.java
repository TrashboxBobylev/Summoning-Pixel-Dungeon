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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WandOfFrost extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_FROST;
	}

	@Override
	public int magicalmin(int lvl){
		return 2+Dungeon.hero.lvl;
	}

	@Override
	public int magicalmax(int lvl){
		return (int) (4+Dungeon.hero.lvl*1.25f);
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.33f;
			case 2: return 3.5f;
		}
		return 0f;
	}

	private float chanceToFreeze(){
	    return Math.min(0.25f + 0.05f * level(), 0.33f);
    }

    private float freezeDuration(){
        float baseChance = 2f + level() / 2f;
        if (chanceToFreeze() == 0.33f){
            baseChance = 3f + level();
        }
        return baseChance;
    }

	ConeAOE cone;

	@Override
	public void onZap(Ballistica bolt) {
		if (level() < 2) {

			Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
			if (heap != null) {
				heap.freeze();
			}

			Char ch = Actor.findChar(bolt.collisionPos);
			if (ch != null && level() == 0) {

				int damage = damageRoll();
				boolean freeze = Random.Float() < chanceToFreeze();

				if (ch.buff(Frost.class) != null) {
					return; //do nothing, can't affect a frozen target
				}
				if (ch.buff(Chill.class) != null && !freeze) {
					//6.67% less damage per turn of chill remaining, to a max of 10 turns (50% dmg)
					float chillturns = Math.min(10, ch.buff(Chill.class).cooldown());
					damage = (int) Math.round(damage * Math.pow(0.9633f, chillturns));
				} else if (freeze) {
					ch.sprite.burst(0xFF99CCFF, buffedLvl() * 2);
					Buff.affect(ch, Frost.class, freezeDuration());
				}
				if ((!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
					processSoulMark(ch, chargesPerCast());
					Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, 1.1f * Random.Float(0.87f, 1.15f));
					if (!freeze) {
						ch.damage(damage, this);
					}
				}

				if (ch.isAlive() && !freeze && (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
					if (ch.isWet())
						Buff.affect(ch, FrostBurn.class).reignite(ch, 13f);
					else
						Buff.affect(ch, FrostBurn.class).reignite(ch, 6f);
				}
			}
		}
		if (level() == 1){
			Blizzard gas = Blob.seed(bolt.collisionPos, 70, Blizzard.class);
			CellEmitter.get(bolt.collisionPos).burst(Speck.factory(Speck.BLIZZARD), 10);
			GameScene.add(gas);
			for (int i : PathFinder.NEIGHBOURS9) {
				Char ch = Actor.findChar(bolt.collisionPos + i);
				if (ch != null && (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
					processSoulMark(ch, chargesPerCast());
				}
			}
		}
		if (level() == 2){
			ArrayList<Char> affectedChars = new ArrayList<>();
			for( int cell : cone.cells ){

				//ignore caster cell
				if (cell == bolt.sourcePos){
					continue;
				}

				GameScene.add( Blob.seed(cell, 2, Freezing.class));

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
				ch.sprite.burst(0xFF99CCFF, 7);
				Buff.affect(ch, Frost.class, 10);
			}
		}
		if (Actor.findChar(bolt.collisionPos) == null){
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
	public int collisionProperties(int target) {
		if (level() == 2) return Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID;
		return super.collisionProperties(target);
	}

	@Override
	public String getTierMessage(int tier){
		if (tier == 1){
			return Messages.get(this, "tier" + tier,
					Math.round(magicalmin(tier-1)*powerLevel(tier-1)),
					Math.round(magicalmax(tier-1)*powerLevel(tier-1)),
					new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1))
			);
		}
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1))
		);
	}

    @Override
    public String statsDesc() {
		if (level() == 0) {
			if (!levelKnown)
				return Messages.get(this, "stats_desc", magicalmin(0), magicalmax(0), new DecimalFormat("#.##").format(20f), new DecimalFormat("#.##").format(2f));
			else
				return Messages.get(this, "stats_desc", magicalmin(), magicalmax(), new DecimalFormat("#.##").format(chanceToFreeze() * 100f), new DecimalFormat("#.#").format(freezeDuration()));
		}
		else {
			return Messages.get(this, "stats_desc" + level());
		}
	}

	@Override
    public void fx(Ballistica bolt, Callback callback) {
		if (level() == 2){
			//need to perform flame spread logic here so we can determine what cells to put flames in.
			int maxDist = 12;
			int dist = Math.min(bolt.dist, maxDist);

			cone = new ConeAOE( bolt,
					maxDist,
					180,
					collisionProperties(0) | Ballistica.STOP_TARGET);

			//cast to cells at the tip, rather than all cells, better performance.
			for (Ballistica ray : cone.rays){
				((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
						MagicMissile.ABYSS,
						curUser.sprite,
						ray.path.get(ray.dist),
						null
				);
			}

			//final zap at half distance, for timing of the actual wand effect
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.ABYSS,
					curUser.sprite,
					bolt.path.get(dist/2),
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}
		else{
			MagicMissile.boltFromChar(curUser.sprite.parent,
					MagicMissile.FROST,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play(Assets.Sounds.ZAP);
		}
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		Chill chill = defender.buff(Chill.class);
		if (chill != null && chill.cooldown() >= Chill.DURATION){
			//need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
			new FlavourBuff(){
				{actPriority = VFX_PRIO;}
				public boolean act() {
					Buff.affect(target, Frost.class, Frost.DURATION);
					return super.act();
				}
			}.attachTo(defender);
		}
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color(0x88CCFF);
		particle.am = 0.6f;
		particle.setLifespan(2f);
		float angle = Random.Float(PointF.PI2);
		particle.speed.polar( angle, 2f);
		particle.acc.set( 0f, 1f);
		particle.setSize( 0f, 1.5f);
		particle.radiateXY(Random.Float(1f));
	}

}
