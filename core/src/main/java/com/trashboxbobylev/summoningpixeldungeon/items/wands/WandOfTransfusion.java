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

package com.trashboxbobylev.summoningpixeldungeon.items.wands;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Barrier;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Charm;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.BloodParticle;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ShadowParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MagesStaff;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
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
	protected void onZap(Ballistica beam) {

		for (int c : beam.subPath(0, beam.dist))
			CellEmitter.center(c).burst( BloodParticle.BURST, 1 );

		int cell = beam.collisionPos;

		Char ch = Actor.findChar(cell);

		if (ch instanceof Mob){
			
			processSoulMark(ch, chargesPerCast());
			
			//this wand does different things depending on the target.
			
			//heals/shields an ally or a charmed enemy while damaging self
			if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null){
				
				// 10% of max hp
				int selfDmg = Math.round(curUser.HT*0.10f);
				
				int healing = selfDmg + 3*level();
				int shielding = (ch.HP + healing) - ch.HT;
				if (shielding > 0){
					healing -= shielding;
					Buff.affect(ch, Barrier.class).setShield(shielding);
				} else {
					shielding = 0;
				}
				
				ch.HP += healing;
				
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2 + level() / 2);
				ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing + shielding);
				
				if (!freeCharge) {
					damageHero(selfDmg);
				} else {
					freeCharge = false;
				}

			//for enemies...
			} else {
				
				//charms living enemies
				if (!ch.properties().contains(Char.Property.UNDEAD)) {
					Buff.affect(ch, Charm.class, 5).object = curUser.id();
					ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 3 + level()/2 );
				
				//harms the undead
				} else {
					ch.damage(Random.NormalIntRange(3 + level()/2, 6+level()), this);
					ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
					Sample.INSTANCE.play(Assets.SND_BURNING);
				}
				
				//and grants a self shield
				Buff.affect(curUser, Barrier.class).setShield((5 + 2*level()));

			}
			
		}
		
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
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// lvl 0 - 10%
		// lvl 1 - 18%
		// lvl 2 - 25%
		if (Random.Int( level() + 10 ) >= 9){
			//grants a free use of the staff
			freeCharge = true;
			GLog.positive( Messages.get(this, "charged") );
			attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
		}
	}

	@Override
	protected void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play( Assets.SND_RAY );
		callback.call();
	}

    @Override
    public String statsDesc() {
        if (!levelKnown)
            return Messages.get(this, "stats_desc", 5, Dungeon.hero.HT / 10, 3, 6);
        else
            return Messages.get(this, "stats_desc", 5 + 2*level(), Dungeon.hero.HT / 10 + 3*level(), 3 + level()/2, 6 + level());
    }

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0xCC0000 );
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
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
