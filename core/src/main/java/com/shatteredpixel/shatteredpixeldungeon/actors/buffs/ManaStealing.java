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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class ManaStealing extends Buff {

    {
        actPriority = HERO_PRIO+1;
    }

    @Override
    public boolean act() {
        if (target.buff(HolyAuraBuff.class) != null) {
            boolean[] FOV = new boolean[Dungeon.level.length()];
            Point c = Dungeon.level.cellToPoint(target.pos);
            ShadowCaster.castShadow(c.x, c.y, FOV, Dungeon.level.losBlocking, 8);
            ArrayList<Char> affected = new ArrayList<>();

            for (int i = 0; i < FOV.length; i++) {
                if (FOV[i]) {
                    Char ch = Actor.findChar(i);
                    if (ch != null){
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected){
                if (ch.alignment == Char.Alignment.ENEMY) {
                    target.sprite.parent.add(new Beam.LightRay(target.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(ch.pos)));
                    Dungeon.hero.mana = Math.min(Dungeon.hero.mana + 1, Dungeon.hero.maxMana);
                    target.sprite.showStatus( CharSprite.DEFAULT, Integer.toString(1 ));
                }
            }

            spend( target.buff(HolyAuraBuff.class).manaSteal );

        } else {

            detach();

        }

        return true;
    }
}
