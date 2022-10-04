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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.WhiteWound;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Knife extends MeleeWeapon {

    public boolean ranged;
	
	{
		image = ItemSpriteSheet.KNIFE;

		tier = 1;

		bones = false;

		defaultAction = AC_THROW;
		usesTargeting = true;

	}

    @Override
    public int max(int lvl) {
        return  7*(tier) + ((1 + (level()+1)) / 2)*level();

    }

    @Override
    public int image() {
	    switch (buffedLvl()){
            case 0: case 1: case 2: case 3:
                return ItemSpriteSheet.KNIFE;
            case 4: case 5: case 6:
                return ItemSpriteSheet.KNIVE_MK2;
            case 7: case 8: case 9:
                return ItemSpriteSheet.KNIVE_MK3;
            default:
                return ItemSpriteSheet.KNIVE_MK4;
        }
    }

    @Override
    public String name() {
        String name;
        switch (buffedLvl()){
            case 0: case 1: case 2: case 3:
                name = Messages.get(this, "name");
                break;
            case 4: case 5: case 6:
                name = Messages.get(this, "name2");
                break;
            case 7: case 8: case 9:
                name = Messages.get(this, "name3");
                break;
            default:
                name = Messages.get(this, "name4");
                break;
        }
        return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( name ) : name;
    }

    @Override
    public String desc() {
        switch (buffedLvl()){
            case 0: case 1: case 2: case 3:
                return Messages.get(this, "desc");
            case 4: case 5: case 6:
                return Messages.get(this, "desc2");
            case 7: case 8: case 9:
                return Messages.get(this, "desc3");
            default:
                return Messages.get(this, "desc4");
        }
    }



    @Override
    public int proc(Char attacker, Char defender, int damage ) {
	    int modifier = ranged ? 7 : 4;
        Buff.prolong( defender, SoulGain.class, speedFactor(attacker) * modifier );
        WhiteWound.hit(defender);
        return super.proc( attacker, defender, damage );
    }

    @Override
    public void onThrow(int cell) {
	    Dungeon.quickslot.convertToPlaceholder(this);
        Char enemy = Actor.findChar(cell);
        if (enemy == null || enemy == curUser || curUser.buff(SoulWeakness.class) != null) {
            super.onThrow(cell);
        } else {
            if (!curUser.shoot(enemy, this)) {
                super.onThrow(cell);
            } else {
                Dungeon.level.drop( this, cell ).sprite.drop();
            }
        }
    }

    @Override
    public float speedFactor(Char owner) {
        float v = super.speedFactor(owner);
        if (Dungeon.hero.subClass == HeroSubClass.OCCULTIST) v *= 0.7f;
        return v;
    }

    public static class SoulGain extends FlavourBuff{
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SPIRIT);
            else target.sprite.remove(CharSprite.State.SPIRIT);
        }
    }
}
