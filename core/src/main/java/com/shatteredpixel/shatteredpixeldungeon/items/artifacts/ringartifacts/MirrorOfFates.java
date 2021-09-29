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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class MirrorOfFates extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_MIRROR;
        levelCap = 10;
        defaultAction = AC_USE;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    public static boolean isMirrorActive(Char ch){
        return ch.buff(MirrorShield.class) != null;
    }

    public static boolean isMirrorDown(Char ch){
        return ch.buff(MirrorCooldown.class) != null;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){

            curUser = hero;

            if (!isEquipped( hero )) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                QuickSlotButton.cancel();

            } else if (isMirrorDown(curUser)) {
                GLog.i( Messages.get(this, "no_charge") );
                QuickSlotButton.cancel();

            } else if (cursed) {
                GLog.warning( Messages.get(this, "cursed") );
                QuickSlotButton.cancel();

            } else {
                Buff.affect(curUser, MirrorShield.class);
                curUser.spendAndNext(1f);
            }
        }
    }

    public static class MirrorShield extends Buff {
        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
        }

        public int potency;

        private static final String POTENCY = "potency";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POTENCY, potency);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            potency = bundle.getInt(POTENCY);
        }

        public int damage(int damage){
            return potency - damage;
        }

        public void destroy(){

        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }

    public static class MirrorCooldown extends DummyBuff {
        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0f, 0f);
        }
    }
}
