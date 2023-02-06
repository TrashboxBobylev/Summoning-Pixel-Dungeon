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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public abstract class Artifact extends EquipableItem {

	protected Buff passiveBuff;
	protected Buff activeBuff;
	public ArtifactClass artifactClass;

	//level is used internally to track upgrades to artifacts, size/logic varies per artifact.
	//already inherited from item superclass
	//exp is used to count progress towards levels for some artifacts
	protected int exp = 0;
	//levelCap is the artifact's maximum level
	protected int levelCap = 0;

	//the current artifact charge
    public int charge = 0;
	//the build towards next charge, usually rolls over at 1.
	//better to keep charge as an int and use a separate float than casting.
	protected float partialCharge = 0;
	//the maximum charge, varies per artifact, not all artifacts use this.
	protected int chargeCap = 0;

	//used by some artifacts to keep track of duration of effects or cooldowns to use.
	protected int cooldown = 0;

	@Override
	public boolean isEquipped(Hero hero) {
		return (hero.belongings.offenseAcc == this && artifactClass == ArtifactClass.OFFENSE) ||
				(hero.belongings.defenseAcc == this && artifactClass == ArtifactClass.DEFENSE) ||
				(hero.belongings.utilityAcc == this && artifactClass == ArtifactClass.UTILITY);
	}

	public void activate(Char ch ) {
		passiveBuff = passiveBuff();
		passiveBuff.attachTo(ch);
	}

	@Override
	public boolean doEquip(Hero hero) {
		detach(hero.belongings.backpack);
		boolean equipSuccessful = false;

		if (artifactClass == ArtifactClass.OFFENSE &&
				(hero.belongings.offenseAcc == null || hero.belongings.offenseAcc.doUnequip( hero, true, false ))) {
			hero.belongings.offenseAcc = this;
			equipSuccessful = true;
		} else if (artifactClass == ArtifactClass.DEFENSE &&
				(hero.belongings.defenseAcc == null || hero.belongings.defenseAcc.doUnequip( hero, true, false ))) {
			hero.belongings.defenseAcc = this;
			equipSuccessful = true;
		} else if (artifactClass == ArtifactClass.UTILITY &&
				(hero.belongings.utilityAcc == null || hero.belongings.utilityAcc.doUnequip( hero, true, false ))) {
			hero.belongings.utilityAcc = this;
			equipSuccessful = true;
		}

		if (equipSuccessful){
			detach( hero.belongings.backpack );

			activate( hero );
			identify();

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.negative( Messages.get(this, "equip_cursed", this) );
			}
			if (!doNotUseTurnForCollect)
				hero.spendAndNext( 1f );

			return true;
		}
		else {

			collect( hero.belongings.backpack );
			return false;

		}
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			if (hero.belongings.offenseAcc == this) {
				hero.belongings.offenseAcc = null;
			} else if (hero.belongings.defenseAcc == this) {
				hero.belongings.defenseAcc = null;
			} else if (hero.belongings.utilityAcc == this){
				hero.belongings.utilityAcc = null;
			}

			passiveBuff.detach();
			passiveBuff = null;

			if (activeBuff != null){
				activeBuff.detach();
				activeBuff = null;
			}

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int visiblyUpgraded() {
		return levelKnown ? Math.round((level()*10)/(float)levelCap): 0;
	}

	@Override
	public int buffedVisiblyUpgraded() {
		return visiblyUpgraded();
	}

	@Override
	public int buffedLvl() {
		//level isn't affected by buffs/debuffs
		return level();
	}

	//transfers upgrades from another artifact, transfer level will equal the displayed level
	public void transferUpgrade(int transferLvl) {
		upgrade(Math.round((transferLvl*levelCap)/10f));
	}

	@Override
	public String info() {
		String description = desc() + "\n\n" + Messages.get(ArtifactClass.class, artifactClass.name() + "_desc");
		if (cursed && cursedKnown && !isEquipped( Dungeon.hero )) {
			return description + "\n\n" + Messages.get(Artifact.class, "curse_known");
			
		} else if (!isIdentified() && cursedKnown && !isEquipped( Dungeon.hero)) {
			return description + "\n\n" + Messages.get(Artifact.class, "not_cursed");
			
		} else {
			return description;
			
		}
	}

	@Override
	public String status() {
		
		//if the artifact isn't IDed, or is cursed, don't display anything
		if (!isIdentified() || cursed){
			return null;
		}

		//display the current cooldown
		if (cooldown != 0)
			return Messages.format( "%d", cooldown );

		//display as percent (ignores LOVE holder)
		if (chargeCap == 100)
			return Messages.format( "%d%%", charge );

		//display as #/#
		if (chargeCap > 0)
			return Messages.format( "%d/%d", charge, chargeCap );

		//if there's no cap -
		//- but there is charge anyway, display that charge
		if (charge != 0)
			return Messages.format( "%d", charge );

		//otherwise, if there's no charge, return null.
		return null;
	}

	@Override
	public Item random() {
		//always +0
		
		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}
		return this;
	}

	@Override
	public int value() {
		int price = 220;
		if (level() > 0)
			price += 40*visiblyUpgraded();
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}


	protected ArtifactBuff passiveBuff() {
		return null;
	}

	protected ArtifactBuff activeBuff() {return null; }

	public void charge(Hero target, float amount){
		//do nothing by default;
	}

	public void setArtifactClass(ArtifactClass artifactClass) {
		this.artifactClass = artifactClass;
		switch (artifactClass){
			case OFFENSE:
				icon = ItemSpriteSheet.Icons.ARTIFACT_OFFENSIVE; break;
			case DEFENSE:
				icon = ItemSpriteSheet.Icons.ARTIFACT_DEFENSIVE; break;
			case UTILITY:
				icon = ItemSpriteSheet.Icons.ARTIFACT_UTILITY; break;
		}
	}

	public enum ArtifactClass {
		OFFENSE,
		DEFENSE,
		UTILITY;
	}

	public class ArtifactBuff extends Buff {

		public int itemLevel() {
			return level();
		}

		public boolean isCursed() {
			return cursed;
		}

		public void charge(Hero target, float amount){
			Artifact.this.charge(target, amount);
		}

	}
	
	private static final String EXP = "exp";
	private static final String CHARGE = "charge";
	private static final String PARTIALCHARGE = "partialcharge";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( EXP , exp );
		bundle.put( CHARGE , charge );
		bundle.put( PARTIALCHARGE , partialCharge );
	}

    @Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		exp = bundle.getInt( EXP );
		if (chargeCap > 0)  charge = Math.min( chargeCap, bundle.getInt( CHARGE ));
		else                charge = bundle.getInt( CHARGE );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
	}
}
