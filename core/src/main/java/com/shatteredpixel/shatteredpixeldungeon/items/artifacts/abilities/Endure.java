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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Endure extends Ability {
    {
        baseChargeUse = 50;
        image = ItemSpriteSheet.ENDURE;
        setArtifactClass(ArtifactClass.DEFENSE);
    }

    @Override
    public float chargeUse() {
        switch (level()){
            case 1: return 30;
            case 2: return 85;
        }
        return super.chargeUse();
    }

    static boolean endureEX = false;

    @Override
    protected void activate(Ability ability, Hero hero, Integer target) {
        int duration = 13;
        switch (level()){
            case 1: duration = 3; break;
            case 2: duration = 17; endureEX = true; break;
        }

        Buff.prolong(hero, EndureTracker.class, duration).setup(hero);

        hero.sprite.operate(hero.pos);

        charge -= chargeUse();
        updateQuickslot();
        Invisibility.dispel();
        int waiting = 3;
        if (level() == 2) waiting = 10;
        hero.spendAndNext(waiting);
    }

    public static class EndureTracker extends FlavourBuff {

        public boolean enduring;

        public int damageBonus;
        public int maxDmgTaken;
        public int hitsLeft;

        @Override
        public int icon() {
            return enduring ? BuffIndicator.NONE : BuffIndicator.AMOK;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (10f - visualcooldown()) / 10f);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", damageBonus, hitsLeft);
        }

        public void setup(Hero hero){
            enduring = true;
            maxDmgTaken = (int) (hero.HT * Math.pow(0.707f, 1));
            damageBonus = 0;
            hitsLeft = 0;
        }

        public int adjustDamageTaken(int damage){
            if (enduring) {
                damageBonus += damage/4;
                return damage/2;
            }
            return damage;
        }

        public int enforceDamagetakenLimit(int damage){
            if (damage >= maxDmgTaken) {
                damage = maxDmgTaken;
                maxDmgTaken = 0;
            } else {
                maxDmgTaken -= damage;
            }
            return damage;
        }

        public void endEnduring(){
            if (!enduring){
                return;
            }

            enduring = false;
            damageBonus *= 1f;

            int nearby = 0;
            for (Char ch : Actor.chars()){
                if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(target.pos, ch.pos) <= 2){
                    nearby ++;
                }
            }
//          if (endureEx)  damageBonus *= 1f + (nearby*0.05f*Dungeon.hero.pointsInTalent(Talent.EVEN_THE_ODDS, Talent.BLOODFLARE_SKIN));

            hitsLeft = endureEX ? 5 : 1;

            if (damageBonus > 0) {
                target.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
                Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
            } else {
                detach();
            }

            endureEX = false;
        }

        public int damageFactor(int damage){
            if (enduring){
                return damage;
            } else {
                int bonusDamage = damageBonus;
                hitsLeft--;

                if (hitsLeft <= 0){
                    detach();
                }
                return damage + bonusDamage;
            }
        }

        public static String ENDURING       = "enduring";
        public static String DAMAGE_BONUS   = "damage_bonus";
        public static String MAX_DMG_TAKEN  = "max_dmg_taken";
        public static String HITS_LEFT      = "hits_left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ENDURING, enduring);
            bundle.put(DAMAGE_BONUS, damageBonus);
            bundle.put(MAX_DMG_TAKEN, maxDmgTaken);
            bundle.put(HITS_LEFT, hitsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            enduring = bundle.getBoolean(ENDURING);
            damageBonus = bundle.getInt(DAMAGE_BONUS);
            maxDmgTaken = bundle.getInt(ENDURING);
            hitsLeft = bundle.getInt(HITS_LEFT);
        }
    };

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfRage.class, ScrollOfUpgrade.class};
            inQuantity = new int[]{1, 1};

            cost = 7;

            output = Endure.class;
            outQuantity = 1;
        }

    }
}
