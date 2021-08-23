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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
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
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WandOfFireblast extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_FIREBOLT;

		chakraGain = 7;

		collisionProperties = Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID;
	}

	//1x/2x/3x damage
	@Override
	public int magicalmin(int lvl){
		return (1+Dungeon.hero.lvl) * imaginableChargePerCast();
	}

	//1x/2x/3x damage
	@Override
	public int magicalmax(int lvl){
		return (int) ((4+Dungeon.hero.lvl*1.75f) * imaginableChargePerCast());
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.33f;
			case 1: return 3f;
			case 2: return 7f;
		}
		return 0f;
	}

	ConeAOE cone;

	@Override
	public void onZap(Ballistica bolt) {

		ArrayList<Char> affectedChars = new ArrayList<>();
		ArrayList<Integer> adjacentCells = new ArrayList<>();
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

			//only ignite cells directly near caster if they are flammable
			if (Dungeon.level.adjacent(bolt.sourcePos, cell) && !Dungeon.level.flamable[cell]){
				adjacentCells.add(cell);
			} else {
				GameScene.add( Blob.seed( cell, 1+imaginableChargePerCast(), Fire.class ) );
			}

			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affectedChars.add(ch);
			}
		}

		//ignite cells that share a side with an adjacent cell, are flammable, and are further from the source pos
		//This prevents short-range casts not igniting barricades or bookshelves
		for (int cell : adjacentCells){
			for (int i : PathFinder.NEIGHBOURS4){
				if (Dungeon.level.trueDistance(cell+i, bolt.sourcePos) > Dungeon.level.trueDistance(cell, bolt.sourcePos)
						&& Dungeon.level.flamable[cell+i]
						&& Fire.volumeAt(cell+i, Fire.class) == 0){
					GameScene.add( Blob.seed( cell+i, imaginableChargePerCast()*2, Fire.class ) );
				}
			}
		}

		for ( Char ch : affectedChars ){
			if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) {
				processSoulMark(ch, imaginableChargePerCast());
				ch.damage(damageRoll(), this);
				if (ch.isAlive()) {
					Buff.affect(ch, Burning.class).reignite(ch);
					switch (imaginableChargePerCast()) {
						case 1:
							break; //no effects
						case 2:
							Buff.affect(ch, Cripple.class, 4f);
							break;
						case 3:
							Buff.affect(ch, Paralysis.class, 4f);
							break;
					}
				}
			}
		}
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		//acts like blazing enchantment
		float procChance = (Dungeon.hero.lvl/3+1f)/(Dungeon.hero.lvl/3+3f) * enchantment.procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			if (defender.buff(Burning.class) != null){
				Buff.affect(defender, Burning.class).reignite(defender, 8f);
				int burnDamage = Random.NormalIntRange( 1, 3 + Dungeon.depth/4 * 5 / Dungeon.chapterSize() );
				defender.damage( Math.round(burnDamage * 0.67f), this );
			} else {
				Buff.affect(defender, Burning.class).reignite(defender, 8f);
			}

			defender.sprite.emitter().burst( FlameParticle.FACTORY, Dungeon.hero.lvl/3 + 1 );

		}
	}

	@Override
    public void fx(Ballistica bolt, Callback callback) {
		//need to perform flame spread logic here so we can determine what cells to put flames in.

		// 5/7/9 distance
		int maxDist = 2 + 3*imaginableChargePerCast();
		int dist = Math.min(bolt.dist, maxDist);

		cone = new ConeAOE( bolt,
				maxDist,
				30 + 20*chargesPerCast(),
				collisionProperties | Ballistica.STOP_TARGET);

		//cast to cells at the tip, rather than all cells, better performance.
		for (Ballistica ray : cone.rays){
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FIRE_CONE,
					curUser.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		//final zap at half distance, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FIRE_CONE,
				curUser.sprite,
				bolt.path.get(dist/2),
				callback );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
	}

	@Override
	protected int chargesPerCast() {
		//consumes 30% of current charges, rounded up, with a minimum of one.
		return 1;
	}

	@Override
	protected int imaginableChargePerCast() {
		return level()+1;
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				Math.round(magicalmin(0)/imaginableChargePerCast()*(tier)),
				Math.round(magicalmax(0)/imaginableChargePerCast()*(tier)),
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1))
		);
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", magicalmin(), magicalmax());
		else
			return Messages.get(this, "stats_desc", magicalmin(0), magicalmax(0));
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( 0xEE7722 );
		particle.am = 0.5f;
		particle.setLifespan(0.6f);
		particle.acc.set(0, -40);
		particle.setSize( 0f, 3f);
		particle.shuffleXY( 1.5f );
	}

}
