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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfLightning extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_LIGHTNING;
	}
	
	private ArrayList<Char> affected = new ArrayList<>();

	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override
	public int magicalmin(int lvl){
		return 2+Dungeon.hero.lvl/2;
	}

	@Override
	public int magicalmax(int lvl){
		return 6+Dungeon.hero.lvl/2;
	}

	@Override
	public float powerLevel(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 2.5f;
			case 2: return 1.33f;
		}
		return 0f;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.6f;
			case 2: return 2.2f;
		}
		return 0f;
	}
	
	@Override
	public void onZap(Ballistica bolt) {

		//lightning deals less damage per-target, the more targets that are hit.
		float multipler = 0.4f + (0.6f/affected.size());
		//if the main target is in water, all affected take 2x more damage
		if (Actor.findChar(bolt.collisionPos) != null && Actor.findChar(bolt.collisionPos).isWet()) {
			if (level() != 1) multipler *= 2f;
			if (level() == 2) multipler *= 2f;
		}

		for (Char ch : affected){

			if ((!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
				processSoulMark(ch, chargesPerCast());
				if (ch == curUser) {
					ch.damage(Math.round(damageRoll() * multipler * 0.5f), this);
				} else {
					ch.damage(Math.round(damageRoll() * multipler), this);
				}
				if (ch == Dungeon.hero) Camera.main.shake( 2, 0.3f );
				ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
				ch.sprite.flash();

				if (ch != curUser && ch.alignment == curUser.alignment && ch.pos != bolt.collisionPos){
					continue;
				}
			}
		}

		if (!curUser.isAlive()) {
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( getClass() );
			GLog.negative(Messages.get(this, "ondeath"));
		}
	}

	private ArrayList<Char> enchaffected = new ArrayList<>();

	private ArrayList<Lightning.Arc> encharcs = new ArrayList<>();

	public static void encharc( Char attacker, Char defender, int dist, ArrayList<Char> affected, ArrayList<Lightning.Arc> arcs ) {

		affected.add(defender);

		defender.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
		defender.sprite.flash();

		PathFinder.buildDistanceMap( defender.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char n = Actor.findChar(i);
				if (n != null && n != attacker && !affected.contains(n)) {
					arcs.add(new Lightning.Arc(defender.sprite.center(), n.sprite.center()));
					encharc(attacker, n, (n.isWet() && !n.flying) ? 2 : 1, affected, arcs);
				}
			}
		}
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		//acts like shocking enchantment
		float procChance = (Dungeon.hero.lvl/3+1f)/(Dungeon.hero.lvl/3+4f);
		if (Random.Float() < procChance) {

			enchaffected.clear();
			encharcs.clear();

			encharc(attacker, defender, 2, enchaffected, encharcs);

			enchaffected.remove(defender); //defender isn't hurt by lightning
			if (attacker instanceof Minion) enchaffected.remove(attacker);
			for (Char ch : enchaffected) {
				if (ch.alignment != attacker.alignment) {
					ch.damage(Math.round(damage * 0.4f), this);
				}
			}

			attacker.sprite.parent.addToFront( new Lightning( encharcs, null ) );
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

		}
	}

	private void arc( Char ch ) {

		int dist = (ch.isWet() && !ch.flying) ? 2 : 1;
		if (level() == 2) dist *= 3;

		ArrayList<Char> hitThisArc = new ArrayList<>();
		PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE){
				Char n = Actor.findChar( i );
				if (n == Dungeon.hero && PathFinder.distance[i] > 1)
					//the hero is only zapped if they are adjacent
					continue;
				else if (n != null && !affected.contains( n )) {
					hitThisArc.add(n);
				}
			}
		}

		affected.addAll(hitThisArc);
		for (Char hit : hitThisArc){
			arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
			arc(hit);
		}
	}
	
	@Override
    public void fx(Ballistica bolt, Callback callback) {

		affected.clear();
		arcs.clear();

		int cell = bolt.collisionPos;

		Char ch = Actor.findChar( cell );
		if (ch != null) {
			affected.add( ch );
			arcs.add( new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
			if (level() != 1) arc(ch);
		} else {
			arcs.add( new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
			CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
		}

		//don't want to wait for the effect before processing damage.
		curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		callback.call();
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color(0xFFFFFF);
		particle.am = 0.6f;
		particle.setLifespan(0.6f);
		particle.acc.set(0, +10);
		particle.speed.polar(-Random.Float(3.1415926f), 6f);
		particle.setSize(0f, 1.5f);
		particle.sizeJitter = 1f;
		particle.shuffleXY(1f);
		float dst = Random.Float(1f);
		particle.x -= dst;
		particle.y += dst;
	}
	
}
