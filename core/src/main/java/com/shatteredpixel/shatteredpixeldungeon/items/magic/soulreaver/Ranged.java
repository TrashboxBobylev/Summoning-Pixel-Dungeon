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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class Ranged extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_RANGED;
        collision = Ballistica.STOP_TARGET;
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        callback.call();
    }

    @Override
    public boolean validateCell(int pos){
        Char ch = Actor.findChar(pos);
        if (!(ch instanceof Minion)){
            GLog.i( Messages.get(this, "no_minion"));
            return false;
        } else {
            int chPos = ch.pos;
            for (int dir: PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(chPos + dir) != null ||
                        !Dungeon.level.passable[chPos + dir]) {
                    GLog.i( Messages.get(this, "solid"));
                    return false;
                } else if (Dungeon.level.distance(chPos + dir, Dungeon.hero.pos) > distance()){
                    GLog.i( Messages.get(this, "too_far"));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch instanceof Minion){
            int pos = ch.pos;
            for (int dir: PathFinder.NEIGHBOURS8){
                if (Actor.findChar(pos + dir) == null &&
                        Dungeon.level.passable[pos+dir]    &&
                        Dungeon.level.distance(pos + dir, Dungeon.hero.pos) <= distance()){
                    curUser.busy();
                    curUser.sprite.emitter().burst(WhiteParticle.UP, 8);
                    curUser.sprite.operate(curUser.pos, new Callback() {
                        @Override
                        public void call() {
                            curUser.sprite.parent.add(
                                    new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(pos+dir)));
                            ScrollOfTeleportation.appear(Dungeon.hero, pos+dir);
                            Dungeon.hero.sprite.idle();
                            Dungeon.hero.pos = pos+dir;
                            Dungeon.observe();
                            GameScene.updateFog();
                        }
                    });
                }
            }
        }
    }

    private int distance(){
        switch (level()){
            case 1: return 13;
            case 2: return Integer.MAX_VALUE;
        }
        return 6;
    }

    @Override
    public int manaCost(){
        switch (level()){
            case 1: return 12;
            case 2: return 25;
        }
        return 7;
    }

    public String desc() {
        return Messages.get(this, "desc", distance(), manaCost());
    }
}
