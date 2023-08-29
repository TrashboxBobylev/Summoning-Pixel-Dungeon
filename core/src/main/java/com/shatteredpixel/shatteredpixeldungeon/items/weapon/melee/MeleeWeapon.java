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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class MeleeWeapon extends Weapon {
	
	public int tier;
	public boolean ranged = false;

	@Override
	public int min(int lvl) {
		return  tier +  //base
				lvl;    //level scaling
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //base
				lvl*(tier+1);   //level scaling
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public String getDefaultAction() {
		if (Dungeon.hero.heroClass == HeroClass.WARRIOR && Dungeon.hero.belongings.weapon != this)
			return AC_EQUIP;
		return defaultAction;
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			damage = strDamageBoost((Hero) owner, damage);

			if (ranged) damage *= 1.33f;

			if (((Hero) owner).hasTalent(Talent.SUFFERING_AWAY) && owner.buff(Cripple.class) != null){
				int tries = ((Hero) owner).pointsInTalent(Talent.SUFFERING_AWAY) > 2 ? 2 : 1;
				for (int i = 0; i < tries; i++){
					int newDamage = augment.damageFactor(super.damageRoll( owner ));
					if (newDamage > damage) damage = newDamage;
				}
			}
		}
		
		return damage;
	}
	
	@Override
	public String info() {

		String info = desc();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.hero.STR() > STRReq()){
			    float additional_damage = Dungeon.hero.STR() - STRReq();
			    if (this instanceof Knife) additional_damage *= 1.5f;
				additional_damage *= universalDMGModifier();
				info += " " + Messages.get(Weapon.class, "excess_str", Math.round(additional_damage));
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, Math.round(min(0)*universalDMGModifier()), Math.round(max(0)*universalDMGModifier()), STRReq(0));
			if (STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		if ((Dungeon.hero.heroClass == HeroClass.WARRIOR || Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) && !Messages.get(this, "warrior_spec").equals("")) info += "\n\n" + Messages.get(MeleeWeapon.class, "warrior") + Messages.get(this, "warrior_spec");

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		}  else if (seal != null) {
			info += "\n\n" + Messages.get(Weapon.class, "seal_attached");
		}else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public int value() {
		if (seal != null) return 0;
		int price = 30 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public int warriorAttack(int damage, Char enemy){
		return damage;
	}

	public float warriorDelay(float delay, Char enemy){
		return delay*1.5f;
	}

	@Override
	protected void onThrow(int cell) {
		if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST) ||
				!(Dungeon.hero.belongings.armor instanceof MailArmor &&
						Dungeon.hero.belongings.armor.level() == 2)) {
			super.onThrow(cell);
		} else {
			Char enemy = Actor.findChar( cell );
			if ((enemy == null || enemy == curUser || curUser.buff(SoulWeakness.class) != null)) {
				super.onThrow( cell );
			} else {
				if (curUser.shoot(enemy, this)) {
					Dungeon.level.drop(this, cell).sprite.drop();
				}
			}
		}
	}
}
