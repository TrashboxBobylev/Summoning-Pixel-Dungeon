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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class MirrorOfFates extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_MIRROR;
        levelCap = 10;
        defaultAction = AC_USE;
        setArtifactClass(ArtifactClass.DEFENSE);
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
    public void charge(Hero target, float amount) {
        Buff.detach(target, MirrorCooldown.class);
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

            } else if (isMirrorActive(curUser)) {
                GLog.i( Messages.get(this, "already_used") );
                QuickSlotButton.cancel();

            } else if (cursed) {
                GLog.warning( Messages.get(this, "cursed") );
                QuickSlotButton.cancel();

            } else {
                Buff.affect(curUser, MirrorShield.class).setPotency(
                        (int) (curUser.HT * (0.2f + 0.04f * level())));
                curUser.spendAndNext(1f);
            }
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new mirrorExp();
    }

    public class mirrorExp extends ArtifactBuff {
        public void gainExp(int exp){
            MirrorOfFates.this.exp += exp;
            if (MirrorOfFates.this.exp > 20 + (level()+1)*20){
                MirrorOfFates.this.exp = 0;
                GLog.positive( Messages.get(MirrorOfFates.class, "level_up") );
                upgrade();
                updateQuickslot();
            }
        }
    }

    public String desc() {
        String desc = super.desc();

        if (isEquipped( Dungeon.hero )){
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else {
                desc += Messages.get(this, "desc_equipped");
            }
        }
        return desc;
    }

    public static class MirrorShield extends Buff {

        {
            announced = true;
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
        }

        public int potency;
        public int maxPotency;

        public void setPotency(int p){
            maxPotency = p;
            potency = p;
        }

        private static final String POTENCY = "potency";
        private static final String DURATION = "duration";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POTENCY, potency);
            bundle.put(DURATION, maxPotency);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            potency = bundle.getInt(POTENCY);
            maxPotency = bundle.getInt(DURATION);
        }

        public int damage(int damage){
            potency -= damage;
            mirrorExp exp = target.buff(mirrorExp.class);
            if (exp != null){
                exp.gainExp(damage);
            }
            if (potency <= 0){
                destroy();
                damage = -potency;
            } else {
                damage = 0;
            }
            return damage;
        }

        public void destroy(){
            Splash.at( target.sprite.center(), 0xF09da8bd, 20 );
            Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY);
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
            Buff.affect(target, MirrorCooldown.class, 80f);
            detach();
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxPotency - potency) / 1f / maxPotency);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SHIELDED);
            else target.sprite.remove(CharSprite.State.SHIELDED);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", potency);
        }
    }

    public static class MirrorCooldown extends DummyBuff {
        {
            announced = false;
        }

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
