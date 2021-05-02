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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTierInfo;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public abstract class AdHocSpell extends ConjurerSpell {

    @Override
    public void effect(Ballistica trajectory) {
        //do nothing
    }

    abstract public void effect(Hero hero);

    @Override
    public void execute(final Hero hero, String action ) {

        GameScene.cancel();

        if (action.equals( AC_ZAP )) {

            if (tryToZap(Dungeon.hero)) {
                curUser = Dungeon.hero;
                curItem = this;
                effect(curUser);
            }

        } else if (action.equals(AC_DOWNGRADE)){
            GameScene.flash(0xFFFFFF);
            Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
            level(level()-1);
            GLog.warning( Messages.get(ConjurerSpell.class, "lower_tier"));
        } else if (action.equals(AC_TIERINFO)){
            curItem = this;
            ShatteredPixelDungeon.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    Game.scene().addToFront(new WndTierInfo(curItem));
                }
            });
        }
    }
}
