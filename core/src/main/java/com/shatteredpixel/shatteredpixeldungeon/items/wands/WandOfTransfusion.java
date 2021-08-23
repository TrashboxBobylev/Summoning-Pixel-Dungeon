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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfTransfusion extends Wand {

	{
		image = ItemSpriteSheet.WAND_TRANSFUSION;

		collisionProperties = Ballistica.PROJECTILE;
	}

	private boolean freeCharge = false;

	@Override
	public float powerLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 2.0f;
			case 2: return 0f;
		}
		return 0f;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1f;
			case 1: return 1.25f;
			case 2: return 0.8f;
		}
		return 0f;
	}

	@Override
	public void onZap(Ballistica beam) {

		for (int c : beam.subPath(0, beam.dist))
			CellEmitter.center(c).burst( BloodParticle.BURST, 1 );

		int cell = beam.collisionPos;

		Char ch = Actor.findChar(cell);

		if (ch instanceof Mob){
			if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))
				processSoulMark(ch, chargesPerCast());
			
			//this wand does different things depending on the target.
			
			//heals/shields an ally or a charmed enemy while damaging self
			if ((ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null) && level() != 1){
				
				// 10% of max hp
				int selfDmg = Math.round(curUser.HT*0.10f);
				
				int healing = (int) (selfDmg*1.25f);
				if (level() == 2) healing *= 2;
				int shielding = (ch.HP + healing) - ch.HT;
				if (shielding > 0){
					healing -= shielding;
					Buff.affect(ch, Barrier.class).setShield(shielding);
				} else {
					shielding = 0;
				}
				
				ch.HP += healing;
				
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2 + buffedLvl() / 2);
				ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing + shielding);
				
				if (!freeCharge) {
					damageHero(selfDmg);
				} else {
					freeCharge = false;
				}

			//for enemies...
			} else if (level() != 2){

				if (level() != 1) {
					//charms living enemies
					if (!ch.properties().contains(Char.Property.UNDEAD)) {
						Buff.affect(ch, Charm.class, Charm.DURATION / 2f).object = curUser.id();
						ch.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 3 + buffedLvl() / 2);

						//harms the undead
					} else if ((!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
						ch.damage(Random.NormalIntRange(3 + Dungeon.hero.lvl / 5, 6 + Dungeon.hero.lvl / 2), this);
						ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + buffedLvl());
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
					}
				}
				
				//and grants a self shield
				Buff.affect(curUser, Barrier.class).setShield(getShield(level()));

			}
			
		}
		
	}

	public int getShield(int level) {
		return (int) ((5 + Dungeon.hero.lvl*0.8f)*powerLevel(level));
	}

	//this wand costs health too
	private void damageHero(int damage){
		
		curUser.damage(damage, this);

		if (!curUser.isAlive()){
			Dungeon.fail( getClass() );
			GLog.negative( Messages.get(this, "ondeath") );
		}
	}

	@Override
	protected int initialCharges() {
		return 1;
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		// lvl 0 - 10%
		// lvl 1 - 18%
		// lvl 2 - 25%
		if (Random.Int( Dungeon.hero.lvl/3 + 10 ) >= 9){
			//grants a free use of the staff
			freeCharge = true;
			GLog.positive( Messages.get(this, "charged") );
			attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
		}
	}

	@Override
    public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play( Assets.Sounds.RAY );
		callback.call();
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( 0xCC0000 );
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	@Override
	public String statsDesc() {
		int selfDMG = Math.round(Dungeon.hero.HT*0.05f);
		if (levelKnown)
			return Messages.get(this, "stats_desc", selfDMG, (int) (selfDMG*1.25f)*(level() == 2 ? 2 : 1), getShield(level()), 3 + Dungeon.hero.lvl/5, 6 + Dungeon.hero.lvl/2);
		else
			return Messages.get(this, "stats_desc", selfDMG, (int) (selfDMG*1.25f), getShield(0), 3, 6);
	}

	private static final String FREECHARGE = "freecharge";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		freeCharge = bundle.getBoolean( FREECHARGE );
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FREECHARGE, freeCharge );
	}

}
