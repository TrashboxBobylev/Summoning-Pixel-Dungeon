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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class FuelContainer extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_FUEL;
        levelCap = 5;
        defaultAction = AC_USE;
        charge = 0;
        chargeCap = 40 + level()*10;
        setArtifactClass(ArtifactClass.UTILITY);
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)){
            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (charge == chargeCap)        GLog.i( Messages.get(this, "full_charge") );
            else if (cursed)                     GLog.i( Messages.get(this, "cursed") );
            else {
                GameScene.selectItem(itemSelector);
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
            else {
                desc += "\n\n" + Messages.get(this, "desc_equipped");
            }
        }

        return desc;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            charge += 2f * amount;
                if (charge == chargeCap) {
                    GLog.positive(Messages.get(FuelContainer.class, "full_charge"));
                    partialCharge = 0;
                }
        }
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 40 + level()*10;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 40 + level()*10;
        return this;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new fuelBuff();
    }

    public class fuelBuff extends ArtifactBuff {
        public boolean canUseCharge(Wand wand, int charges){
            return charge >= wand.rechargeModifier() * charges * 10;
        }

        public void useCharge(Wand wand, int charges){
            float usedCharge = wand.rechargeModifier() * charges * 10;
            charge -= usedCharge;
            exp += charge;
            if (exp >= 40 + level()*20 && level() < levelCap){
                exp -= 40 + level()*20;
                upgrade();
                GLog.positive(Messages.get(FuelContainer.class, "level_up"));
            }
        }
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(FuelContainer.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return MagicalHolster.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Wand && ((Wand) item).curCharges > 0;
        }

        @Override
        public void onSelect(Item item) {
            if (itemSelectable(item)){
                Wand wand = (Wand)item;
                Hero hero = Dungeon.hero;
                hero.sprite.operate( hero.pos );
                hero.busy();
                hero.spend( 2f );
                charge = Math.min(chargeCap, charge + Math.round(wand.curCharges*10*wand.rechargeModifier()));
                wand.curCharges = 0;
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                ScrollOfRecharging.charge(hero);
            }
        }
    };
}
