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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;


import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.CloakGlyph;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.Ethereal;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class CloakOfShadows extends Artifact implements ActionIndicator.Action, Stylus.Inscribable {

	{
		image = ItemSpriteSheet.ARTIFACT_CLOAK;
		setArtifactClass(ArtifactClass.UTILITY);

		exp = 0;
		levelCap = 10;

		charge = Math.min(level()+3, 10);
		partialCharge = 0;
		chargeCap = Math.min(level()+3, 10);

		defaultAction = AC_STEALTH;

		unique = true;
		bones = false;
	}

	private boolean stealthed = false;
	public CloakGlyph glyph;

	public static final String AC_STEALTH = "STEALTH";
	public static final String AC_TELEPORT = "TELEPORT";
	public static final String AC_CHOOSE = "CHOOSE";

	@Override
	public String getDefaultAction() {
		if (charge > 0 && isEquipped(Dungeon.hero) && !cursed){
			return AC_STEALTH;
		}
		return super.getDefaultAction();
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped(hero) && !cursed) {
			if ((charge > 0 || stealthed)) {

				actions.add(AC_STEALTH);
			}
			if (charge > 0 && hero.hasTalent(Talent.HYPERSPACE)){
				actions.add(AC_TELEPORT);
			}
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals( AC_STEALTH )) {

			if (!stealthed){
				if (!isEquipped(hero)) GLog.i( Messages.get(Artifact.class, "need_to_equip") );
				else if (cursed)       GLog.i( Messages.get(this, "cursed") );
				else if (charge <= 0)  GLog.i( Messages.get(this, "no_charge") );
				else {
					stealthed = true;
                    Statistics.cloakUsing++;
					hero.spend( 1f );
					hero.busy();
					Sample.INSTANCE.play(Assets.Sounds.MELD);
					activeBuff = activeBuff();
					activeBuff.attachTo(hero);
					if (hero.sprite.parent != null) {
						hero.sprite.parent.add(new AlphaTweener(hero.sprite, 0.4f, 0.4f));
					} else {
						hero.sprite.alpha(0.4f);
					}
					hero.sprite.operate(hero.pos, () -> {
						if (glyph != null)
							glyph.onCloaking(this, hero);
						hero.sprite.idle();
						hero.onOperateComplete();
					});

				}
			} else {
				stealthed = false;
				activeBuff.detach();
				activeBuff = null;
				if (glyph != null)
					glyph.onUncloaking(CloakOfShadows.this, hero);
				hero.spend( 1f );
				hero.sprite.operate( hero.pos );
			}

		}

		if (action.equals(AC_TELEPORT)){
			GameScene.selectCell(caster);
		}
	}

	private CellSelector.Listener caster = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer target) {
			if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target]) && Dungeon.level.passable[target]){
				int maxDistance = (int) (charge * (0.57f + 0.09f*(Dungeon.hero.pointsInTalent(Talent.HYPERSPACE))));
				if (glyph != null)
					maxDistance /= glyph.chargeModifier(CloakOfShadows.this, Dungeon.hero);
				if (Dungeon.level.distance(target, Dungeon.hero.pos) > maxDistance){
					GLog.warning( Messages.get(CloakOfShadows.class, "cant_reach") );
				} else {
					float chargeCost = maxDistance / (0.57f + 0.09f * (Dungeon.hero.pointsInTalent(Talent.HYPERSPACE)));
					CloakOfShadows.this.charge -= chargeCost;
					ScrollOfTeleportation.teleportToLocation(Dungeon.hero, target);
					//target hero level is 1 + 2*cloak level
					int lvlDiffFromTarget = Dungeon.hero.lvl - (1+level()*2);
					//plus an extra one for each level after 6
					if (level() >= 7){
						lvlDiffFromTarget -= level()-6;
					}
					if (lvlDiffFromTarget >= 0){
						exp += Math.round(10f * Math.pow(1.1f, lvlDiffFromTarget))*chargeCost;
					} else {
						exp += Math.round(10f * Math.pow(0.75f, -lvlDiffFromTarget))*chargeCost;
					}

					if (exp >= (level() + 1) * 50 && level() < levelCap) {
						upgrade();
						exp -= level() * 50;
						GLog.positive(Messages.get(this, "levelup"));
					}
					updateQuickslot();
				}
			}
		}

		@Override
		public String prompt() {
			int maxDistance = (int) (charge * (0.57f + 0.09f*(Dungeon.hero.pointsInTalent(Talent.HYPERSPACE))));
			if (glyph != null)
				maxDistance /= glyph.chargeModifier(CloakOfShadows.this, Dungeon.hero);
			PathFinder.buildDistanceMap( Dungeon.hero.pos, Dungeon.level.passable, maxDistance );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE && !Dungeon.level.solid[i]) {
					Dungeon.hero.sprite.parent.addToBack(new TargetedCell(i, 0xb47ffe));
				}
			}
			return Messages.get(CloakOfShadows.class, "prompt");
		}
	};

	@Override
	public void activate(Char ch){
		super.activate(ch);
		if (stealthed){
			activeBuff = activeBuff();
			activeBuff.attachTo(ch);
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			stealthed = false;
			return true;
		} else
			return false;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new cloakRecharge();
	}

	@Override
	protected ArtifactBuff activeBuff( ) {
		return new cloakStealth();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap) {
			partialCharge += 0.25f*amount;
			if (partialCharge >= 1){
				partialCharge--;
				charge++;
				updateQuickslot();
			}
		}
	}
	
	@Override
	public Item upgrade() {
		chargeCap = Math.min(chargeCap + 1, 10);
		return super.upgrade();
	}

	private static final String STEALTHED = "stealthed";
	private static final String GLYPH			= "glyph";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( STEALTHED, stealthed );
		bundle.put( GLYPH, glyph );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		stealthed = bundle.getBoolean( STEALTHED );
		if (bundle.contains(GLYPH))
			inscribe((CloakGlyph) bundle.get(GLYPH));
	}

	@Override
	public int value() {
		return 0;
	}

	public class cloakRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				LockedFloor lock = target.buff(LockedFloor.class);
				if (!stealthed && (lock == null || lock.regenOn())) {
					float missing = (chargeCap - charge);
					if (level() > 7) missing += 5*(level() - 7)/3f;
					float turnsToCharge = (45 - missing);
					if (target instanceof Hero && ((Hero) target).hasTalent(Talent.ARCANE_CLOAK)){
						if (glyph == null)
							turnsToCharge /= 1.075f + 0.025f * ((Hero) target).pointsInTalent(Talent.ARCANE_CLOAK);
						else
							turnsToCharge /= 1.1f + 0.06f * ((Hero) target).pointsInTalent(Talent.ARCANE_CLOAK);
					}
					partialCharge += (1f / turnsToCharge);
				}

				if (partialCharge >= 1) {
					charge++;
					partialCharge -= 1;
					if (charge == chargeCap){
						partialCharge = 0;
					}

				}
			} else
				partialCharge = 0;

			if (cooldown > 0)
				cooldown --;

			updateQuickslot();
			float chargeMod = 1f;
			if (glyph != null)
				chargeMod = glyph.chargeModifier(CloakOfShadows.this, Dungeon.hero);
			if ((int) (charge * ((0.57f + 0.09f*(Dungeon.hero.pointsInTalent(Talent.HYPERSPACE))) / chargeMod)) >= 1
				&& Dungeon.hero.hasTalent(Talent.HYPERSPACE)){
				ActionIndicator.setAction(CloakOfShadows.this);
			} else {
				ActionIndicator.clearAction(CloakOfShadows.this);
			}

			spend( TICK );

			return true;
		}

		@Override
		public void detach() {
			super.detach();
			ActionIndicator.clearAction(CloakOfShadows.this);
		}
	}

	@Override
	public String name() {
		return glyph != null && cursedKnown ? glyph.name( super.name() ) : super.name();
	}

	@Override
	public String info() {
		String info = desc();

		if (glyph != null  && cursedKnown) {
			info += "\n\n" +  Messages.get(CloakOfShadows.class, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}

		return info;
	}

	public CloakOfShadows inscribe( CloakGlyph glyph ) {
		this.glyph = glyph;
		updateQuickslot();
		return this;
	}

	public CloakOfShadows inscribe() {

		Class<? extends CloakGlyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		CloakGlyph gl = CloakGlyph.random( oldGlyphClass );

		return inscribe( gl );
	}

	@Override
	public boolean hasCurseGlyph() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return cursed;
	}

	public boolean hasGlyph(Class<?extends CloakGlyph> type, Char owner) {
		return glyph != null && glyph.getClass() == type && owner.buff(MagicImmune.class) == null;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null && cursedKnown ? glyph.glowing() : null;
	}

	@Override
	public Image getIcon() {
		Image actionIco = new Image(Assets.Sprites.ITEM_ICONS);
		actionIco.frame(ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TELEPORT));
		actionIco.scale.set(2f);
		actionIco.hardlight(0xc44dd6);
		return actionIco;
	}

	@Override
	public void doAction() {
		execute(Dungeon.hero, AC_TELEPORT);
	}

	@Override
	public boolean usable() {
		float chargeMod = 1f;
		if (glyph != null)
			chargeMod = glyph.chargeModifier(this, Dungeon.hero);
		return (int) (charge * ((0.57f + 0.09f*(Dungeon.hero.pointsInTalent(Talent.HYPERSPACE))) / chargeMod)) >= 1
				&& Dungeon.hero.hasTalent(Talent.HYPERSPACE);
	}

	public class cloakStealth extends ArtifactBuff{
		
		{
			type = buffType.POSITIVE;
		}
		
		int turnsToCost = 0;

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public float iconFadePercent() {
			return (5f - turnsToCost) / 5f;
		}

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				target.invisible++;
//				if (target instanceof Hero && ((Hero) target).subClass == HeroSubClass.ASSASSIN){
//					Buff.affect(target, Preparation.class);
//				}
				return true;
			} else {
				return false;
			}
		}

		public CloakGlyph glyph(){
			return glyph;
		}

		@Override
		public boolean act(){
			turnsToCost--;
			
			if (turnsToCost <= 0){
				charge--;
				if (charge < 0) {
					charge = 0;
					detach();
					GLog.warning(Messages.get(this, "no_charge"));
					((Hero) target).interrupt();
				} else {
					//target hero level is 1 + 2*cloak level
					int lvlDiffFromTarget = ((Hero) target).lvl - (1+level()*2);
					//plus an extra one for each level after 6
					if (level() >= 7){
						lvlDiffFromTarget -= level()-6;
					}
					if (lvlDiffFromTarget >= 0){
						exp += Math.round(10f * Math.pow(1.1f, lvlDiffFromTarget));
					} else {
						exp += Math.round(10f * Math.pow(0.75f, -lvlDiffFromTarget));
					}
					
					if (exp >= (level() + 1) * 50 && level() < levelCap) {
						upgrade();
						exp -= level() * 50;
						GLog.positive(Messages.get(this, "levelup"));
						
					}
					turnsToCost = 5;
				}
				updateQuickslot();
			}
			float wait = TICK;
			if (glyph != null){
				wait /= glyph.chargeModifier(CloakOfShadows.this, Dungeon.hero);
				glyph.proc(CloakOfShadows.this, target, 1);
			}

			spend( wait );

			return true;
		}

		public void dispel(){
			updateQuickslot();
			detach();
		}

		@Override
		public void fx(boolean on) {
			if (on) {
				target.sprite.add( CharSprite.State.INVISIBLE );
				if (glyph instanceof Ethereal){
					target.sprite.add(CharSprite.State.LEVITATING);
				}
			}
			else {
				if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
				if (glyph instanceof Ethereal){
					target.sprite.remove(CharSprite.State.LEVITATING);
				}
			}
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void detach() {
			if (target.invisible > 0)
				target.invisible--;
			stealthed = false;
			if (glyph != null)
				glyph.onDetaching(CloakOfShadows.this, target);

			updateQuickslot();
			super.detach();
		}
		
		private static final String TURNSTOCOST = "turnsToCost";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			
			bundle.put( TURNSTOCOST , turnsToCost);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			
			turnsToCost = bundle.getInt( TURNSTOCOST );
		}
	}
}
