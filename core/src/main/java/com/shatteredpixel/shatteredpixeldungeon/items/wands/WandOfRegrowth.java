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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LotusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class WandOfRegrowth extends Wand {

	{
		image = ItemSpriteSheet.WAND_REGROWTH;

		chakraGain = 7;

		collisionProperties = Ballistica.STOP_SOLID;
	}
	
	private int totChrgUsed = 0;

	ConeAOE cone;
	int target;

	@Override
	public boolean tryToZap(Hero owner, int target) {
		if (super.tryToZap(owner, target)){
			this.target = target;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1f;
			case 1: return 3f;
			case 2: return 7f;
		}
		return 0f;
	}

	@Override
	public void onZap(Ballistica bolt) {

		ArrayList<Integer> cells = new ArrayList<>(cone.cells);

		int overLimit = totChrgUsed - chargeLimit(Dungeon.hero.lvl, level()+1);
		float furrowedChance = overLimit > 0 ? (overLimit / (10f + Dungeon.hero.lvl)) : 0;

		int chrgUsed = imaginableChargePerCast();
		int grassToPlace = Math.round((3.67f+(Dungeon.hero.lvl/2)/3f)*chrgUsed);

		//ignore cells which can't have anything grow in them.
		for (Iterator<Integer> i = cells.iterator(); i.hasNext();) {
			int cell = i.next();
			int terr = Dungeon.level.map[cell];
			if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
					terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS)) {
				i.remove();
			} else if (Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE)) {
				i.remove();
			} else if (Dungeon.level.plants.get(cell) != null){
				i.remove();
			} else {
				if (terr != Terrain.HIGH_GRASS && terr != Terrain.FURROWED_GRASS) {
					Level.set(cell, Terrain.GRASS);
					GameScene.updateMap( cell );
				}
				Char ch = Actor.findChar(cell);
				if (ch != null){
					processSoulMark(ch, chargesPerCast());
					Buff.prolong( ch, Roots.class, 4f * chrgUsed );
				}
			}
		}

		Random.shuffle(cells);

		if (imaginableChargePerCast() >= 2){
			Lotus l = new Lotus();
			l.setLevel(Dungeon.hero.lvl/3);
			if (cells.contains(target) && Actor.findChar(target) == null){
				cells.remove((Integer)target);
				l.pos = target;
				GameScene.add(l);
			} else {
				for (int i = bolt.path.size()-1; i >= 0; i--){
					int c = bolt.path.get(i);
					if (cells.contains(c) && Actor.findChar(c) == null){
						cells.remove((Integer)c);
						l.pos = c;
						GameScene.add(l);
						break;
					}
				}
			}
		}

		//places grass along center of cone
		for (int cell : bolt.path){
			if (grassToPlace > 0 && cells.contains(cell)){
				if (Random.Float() > furrowedChance) {
					Level.set(cell, Terrain.HIGH_GRASS);
				} else {
					Level.set(cell, Terrain.FURROWED_GRASS);
				}
				GameScene.updateMap( cell );
				grassToPlace--;
				//moves cell to the back
				cells.remove((Integer)cell);
				cells.add(cell);
			}
		}

		if (!cells.isEmpty() && Random.Float() > furrowedChance &&
				(Random.Int(6) < chrgUsed)){ // 16%/33%/50% chance to spawn a seed pod or dewcatcher
			int cell = cells.remove(0);
			Dungeon.level.plant( Random.Int(2) == 0 ? new Seedpod.Seed() : new Dewcatcher.Seed(), cell);
		}

		if (!cells.isEmpty() && Random.Float() > furrowedChance &&
				(Random.Int(3) < chrgUsed)){ // 33%/66%/100% chance to spawn a plant
			int cell = cells.remove(0);
			Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), cell);
		}

		for (int cell : cells){
			if (grassToPlace <= 0 || bolt.path.contains(cell)) break;

			if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS) continue;

			if (Random.Float() > furrowedChance) {
				Level.set(cell, Terrain.HIGH_GRASS);
			} else {
				Level.set(cell, Terrain.FURROWED_GRASS);
			}
			GameScene.updateMap( cell );
			grassToPlace--;
		}

		if (furrowedChance < 1f) {
			totChrgUsed += chrgUsed;
		}
	}
	
	private int chargeLimit( int heroLvl, int level ){
		if (heroLvl >= 30){
			return Integer.MAX_VALUE;
		} else {
			//8 charges at base, plus:
			//2/3.33/5/7/10/14/20/30/50/110/infinite charges per hero level, based on wand level
			float lvl = 3*(level);
			return Math.round(8 + heroLvl * (2+lvl) * (1f + (lvl/(10 - lvl))));
		}
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		float procChance = (Dungeon.hero.lvl/3+1f)/(Dungeon.hero.lvl/3+3f) * enchantment.procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			boolean secondPlant = Dungeon.hero.lvl/3 > Random.Int(10);
			if (plantGrass(defender.pos)){
				if (secondPlant) secondPlant = false;
				else return;
			}

			ArrayList<Integer> positions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				positions.add(i);
			}
			Random.shuffle( positions );
			for (int i : positions){
				if (plantGrass(defender.pos + i)){
					if (secondPlant) secondPlant = false;
					else return;
				}
			}

		}
	}

	private boolean plantGrass(int cell){
		int t = Dungeon.level.map[cell];
		if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
				|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
				&& Dungeon.level.plants.get(cell) == null){
			Level.set(cell, Terrain.HIGH_GRASS);
			GameScene.updateMap(cell);
			CellEmitter.get( cell ).burst( LeafParticle.LEVEL_SPECIFIC, 4 );
			return true;
		}
		return false;
	}

	public void fx(Ballistica bolt, Callback callback) {

		// 5/7/9 distance
		int maxDist = 2 + 3*imaginableChargePerCast();
		int dist = Math.min(bolt.dist, maxDist);

		cone = new ConeAOE( bolt,
				maxDist,
				20 + 10*imaginableChargePerCast(),
				collisionProperties | Ballistica.STOP_TARGET);

		//cast to cells at the tip, rather than all cells, better performance.
		for (Ballistica ray : cone.rays){
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FOLIAGE_CONE,
					curUser.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		//final zap at half distance, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FOLIAGE_CONE,
				curUser.sprite,
				bolt.path.get(dist/2),
				callback );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected int imaginableChargePerCast() {
		return level()+1;
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1)),
				Math.round((3.67f+(Dungeon.hero.lvl/2)/3f)*(tier)),
				chargeLimit(Dungeon.hero.lvl, tier)
		);
	}

	@Override
	public String statsDesc() {
		return Messages.get(this, "stats_desc", chargeLimit(Dungeon.hero.lvl, level()+1)-totChrgUsed);
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( ColorMath.random(0x004400, 0x88CC44) );
		particle.am = 1f;
		particle.setLifespan(1f);
		particle.setSize( 1f, 1.5f);
		particle.shuffleXY(0.5f);
		float dst = Random.Float(11f);
		particle.x -= dst;
		particle.y += dst;
	}
	
	private static final String TOTAL = "totChrgUsed";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( TOTAL, totChrgUsed );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		totChrgUsed = bundle.getInt(TOTAL);
	}
	
	public static class Dewcatcher extends Plant{

		{
			image = 13;
		}

		@Override
		public void activate( Char ch ) {

			int nDrops = Random.NormalIntRange(3, 6);

			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+i]
						&& pos+i != Dungeon.level.entrance
						&& pos+i != Dungeon.level.exit){
					candidates.add(pos+i);
				}
			}

			for (int i = 0; i < nDrops && !candidates.isEmpty(); i++){
				Integer c = Random.element(candidates);
				Dungeon.level.drop(new Dewdrop(), c).sprite.drop(pos);
				candidates.remove(c);
			}

		}

		//seed is never dropped, only care about plant class
		public static class Seed extends Plant.Seed {
			{
				plantClass = Dewcatcher.class;
			}
		}
	}

	public static class Seedpod extends Plant{

		{
			image = 14;
		}

		@Override
		public void activate( Char ch ) {

			int nSeeds = Random.NormalIntRange(2, 4);

			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+i]
						&& pos+i != Dungeon.level.entrance
						&& pos+i != Dungeon.level.exit){
					candidates.add(pos+i);
				}
			}

			for (int i = 0; i < nSeeds && !candidates.isEmpty(); i++){
				Integer c = Random.element(candidates);
				Dungeon.level.drop(Generator.random(Generator.Category.SEED), c).sprite.drop(pos);
				candidates.remove(c);
			}

		}

		//seed is never dropped, only care about plant class
		public static class Seed extends Plant.Seed {
			{
				plantClass = Seedpod.class;
			}
		}

	}

	public static class Lotus extends NPC {

		{
			alignment = Alignment.ALLY;
			properties.add(Property.IMMOVABLE);

			spriteClass = LotusSprite.class;

			viewDistance = 1;
		}

		private int wandLvl = 0;

		private void setLevel( int lvl ){
			wandLvl = lvl;
			HP = HT = 25 + 3*lvl;
		}

		public boolean inRange(int pos){
			return Dungeon.level.trueDistance(this.pos, pos) <= wandLvl;
		}

		public float seedPreservation(){
			return 0.40f + 0.04f*wandLvl;
		}

		@Override
		public boolean canInteract(Char c) {
			return false;
		}

		@Override
		protected boolean act() {
			super.act();

			if (--HP <= 0){
				destroy();
				sprite.die();
			}

			return true;
		}

		@Override
		public void damage( int dmg, Object src ) {
		}

		@Override
		public void add( Buff buff ) {
		}

		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog(pos, viewDistance+1);
		}

		@Override
		public boolean isInvulnerable(Class effect) {
			return true;
		}

		{
			immunities.add(Corruption.class);
			immunities.add(Doom.class);
		}

		@Override
		public String description() {
			return Messages.get(this, "desc", wandLvl, (int)(seedPreservation()*100), (int)(seedPreservation()*100) );
		}

		private static final String WAND_LVL = "wand_lvl";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAND_LVL, wandLvl);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			wandLvl = bundle.getInt(WAND_LVL);
		}
	}

}
