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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Cryonic extends CloakGlyph{
    private static ItemSprite.Glowing ICEY = new ItemSprite.Glowing( 0xe0fdff);

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {
        Buff.affect(defender, IceBarrier.class);
    }

    @Override
    public void onUncloaking(CloakOfShadows cloak, Char defender) {
        Buff.detach(defender, IceBarrier.class);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return ICEY;
    }

    public static class IceBarrier extends CounterBuff {
        public float iconFadePercent() { return Math.max(0, 1f - ((count()) / (barrierLimit()))); }
        public String toString() { return Messages.get(this, "name"); }

        public String desc() {
            return Messages.get(this, "desc", Math.round(barrierAmounts()*count()), Math.round(barrierLimit()*barrierAmounts()));
        }

        public int icon() { return BuffIndicator.ARMOR_GEN; }

        public void tintIcon(Image icon) { icon.hardlight(0xe0fdff); }

        public int pos;

        public static int barrierAmounts(){
            return Math.round(4 * efficiency());
        }

        public static int barrierLimit(){
            return Math.round(15 * efficiency());
        }

        @Override
        public boolean act() {
            if (target.isAlive()) {
                spend( TICK );
                if (target.pos != pos){
                    pos = target.pos;
                } else if (count() < barrierLimit()){
                    countUp(1);
                    Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, 3f);
                    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1f, 2f);
                }
            } else {
                detach();
                pos = 0;
            }

            return true;
        }

        @Override
        public void detach() {
            super.detach();
            Sample.INSTANCE.play(Assets.Sounds.SHATTER);
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            Buff.affect(target, Barrier.class).setShield(Math.round(count()*barrierAmounts()));
            Buff.affect(target, Frost.class, count());
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                target.sprite.add(CharSprite.State.SHIELDED);
                target.sprite.add(CharSprite.State.CHILLED);
            }
            else {
                target.sprite.remove(CharSprite.State.SHIELDED);
                target.sprite.remove(CharSprite.State.CHILLED);
            }
        }

        private static final String POS = "pos";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
        }
    }
}
