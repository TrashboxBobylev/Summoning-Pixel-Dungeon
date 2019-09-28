/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.stationary;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfWarding;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndOptions;
import com.watabou.utils.Random;

public class StationaryMinion extends Minion {
    {
        properties.add(Property.IMMOVABLE);

        viewDistance = 5;
        state = WANDERING;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
        if (visible) {
            sprite.zap( enemy.pos );
        } else {
            zap();
        }

        return !visible;
    }

    @Override
    protected Char chooseEnemy() {

        return super.chooseEnemy();
    }

    private void zap() {
        spend( 1f );

        if (hit( this, enemy, true )) {
            int dmg = Random.NormalIntRange(minDamage, maxDamage);
            enemy.damage(dmg, this);

            damage(HT / 20, this);
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    public boolean canInteract(Hero h) {
        return true;
    }

    @Override
    public boolean interact() {
        GameScene.show(new WndOptions( Messages.get(this, "dismiss_title"),
                Messages.get(this, "dismiss_body"),
                Messages.get(this, "dismiss_confirm"),
                Messages.get(this, "dismiss_cancel") ){
            @Override
            protected void onSelect(int index) {
                if (index == 0){
                    die(null);
                }
            }
        });
        return true;
    }

    {
        properties.add(Property.IMMOVABLE);
    }
}
