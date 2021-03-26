/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTierInfo;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public abstract class ConjurerSpell extends Item {

    public static final String AC_ZAP	= "ZAP";
    public static final String AC_DOWNGRADE = "DOWNGRADE";
    public static final String AC_TIERINFO = "TIERINFO";

    public int manaCost;

    {
        defaultAction = AC_ZAP;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return level() < 2;
    }

    public abstract void effect(Ballistica trajectory);

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }


    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = new ArrayList<>();
        actions.add( AC_ZAP );
        if (level() > 0) actions.add(AC_DOWNGRADE);
        actions.add( AC_TIERINFO );

        return actions;
    }

    public int manaCost(){
        return manaCost;
    }

    @Override
    public void execute( final Hero hero, String action ) {

        GameScene.cancel();

        if (action.equals( AC_ZAP )) {

            if (tryToZap(Dungeon.hero)) {
                curUser = Dungeon.hero;
                curItem = this;
                GameScene.selectCell(targeter);
            }

        } else if (action.equals(AC_DOWNGRADE)){
            GameScene.flash(0xFFFFFF);
            Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
            level(level()-1);
            GLog.warning( Messages.get(ConjurerSpell.class, "lower_tier"));
        } else if (action.equals(AC_TIERINFO)){
            ShatteredPixelDungeon.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    Game.scene().addToFront(new WndTierInfo(ConjurerSpell.this));
                }
            });
        }
    }

    public boolean tryToZap( Hero owner){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        if (owner.buff(SoulWeakness.class) != null){
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }

        if ( manaCost() <= (Dungeon.hero.mana)){
            return true;
        } else {
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
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

    private  static CellSelector.Listener targeter = new  CellSelector.Listener(){

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                //FIXME this safety check shouldn't be necessary
                //it would be better to eliminate the curItem static variable.
                final ConjurerSpell curSpell;
                if (curItem instanceof ConjurerSpell) {
                    curSpell = (ConjurerSpell)curItem;
                } else {
                    return;
                }

                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.PROJECTILE);
                int cell = shot.collisionPos;

                curUser.sprite.zap(cell);

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                curUser.busy();

                curUser.mana -= curSpell.manaCost();

                curSpell.fx(shot, new Callback() {
                    public void call() {
                        curSpell.effect(shot);
                        Invisibility.dispel();
                        curSpell.updateQuickslot();
                        curUser.spendAndNext( 1f );
                    }
                });

            }

        }

        @Override
        public String prompt() {
            return Messages.get(ConjurerSpell.class, "prompt");
        }
    };
}
