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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTierInfo;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Ability extends Artifact {

    public int baseChargeUse = 35;

    {
        charge = 0;
        partialCharge = 0;
        chargeCap = 100;
        defaultAction = AC_USE;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && !cursed) actions.add(AC_USE);
        if (level() > 0) actions.add(AC_DOWNGRADE);
        actions.add( AC_TIERINFO );
        return actions;
    }

    public void use( Ability ability, Hero hero ){
        if (targetingPrompt() == null){
            activate(ability, hero, hero.pos);
        } else {
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    activate(ability, hero, cell);
                }

                @Override
                public String prompt() {
                    return targetingPrompt();
                }
            });
        }
    }

    public String targetingPrompt(){
        return null;
    }

    public boolean useTargeting(){
        return targetingPrompt() != null;
    }

    protected abstract void activate( Ability ability, Hero hero, Integer target );

    public float chargeUse(){
        return baseChargeUse;
    }

    @Override
    public String desc() {
        String desc = super.desc();

        float chargeUse = chargeUse();
        desc += " " + Messages.get(this, "charge_use", new DecimalFormat("#.##").format(chargeUse));

        return desc;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Recharge();
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }

    @Override
    public boolean isUpgradable() {
        return level() < 2;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap){
            charge += Math.round(3*amount);
            if (charge >= chargeCap) {
                charge = chargeCap;
                partialCharge = 0;
                GLog.positive( Messages.get(Ability.class, "charged", name()) );
            }
            updateQuickslot();
        }
    }

    @Override
    public String toString() {

        String name = name();
        String tier = "";
        switch (level()){
            case 0: tier = "I"; break;
            case 1: tier = "II"; break;
            case 2: tier = "III"; break;
        }

        name = Messages.format( "%s %s", name, tier  );

        return name;

    }

    @Override
    public String status() {
        if (isIdentified())
        return Messages.format( "%.0f%%", Math.floor(charge) );
        return "";
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_USE)){

            if (!isEquipped( hero )) {
                usesTargeting = false;
                GLog.warning( Messages.get(this, "not_equipped") );
            } else if (charge < chargeUse()) {
                usesTargeting = false;
                GLog.warning(Messages.get(this, "low_charge"));
            } else {
                usesTargeting = useTargeting();
                use(this, hero);
                }
            }else if (action.equals(AC_DOWNGRADE)){
            GameScene.flash(0xFFFFFF);
            Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
            level(level()-1);
            GLog.warning( Messages.get(Staff.class, "lower_tier"));
        } else if (action.equals(AC_TIERINFO)){
            ShatteredPixelDungeon.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    Game.scene().addToFront(new WndTierInfo(Ability.this));
                }
            });
        }

        }

    public class Recharge extends ArtifactBuff {

        @Override
        public boolean act() {

            spend( TICK );

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                //500 turns to a full charge
                partialCharge += (1/5f * RingOfEnergy.artifactChargeMultiplier(target));
                if (partialCharge > 1){
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap){
                        partialCharge = 0f;
                        GLog.positive( Messages.get(Ability.class, "charged", name()) );
                    }
                }
            }

            updateQuickslot();

            return true;
        }
    }
}
