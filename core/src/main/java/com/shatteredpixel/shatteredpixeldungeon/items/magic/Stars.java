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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Stars extends ConjurerSpell {

    {
        image = ItemSpriteSheet.STARS;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null){
            ch.damage(damageRoll(), this);

            for (int i : PathFinder.NEIGHBOURS8){
                ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                        MagicMissile.MAGIC_MISSILE,
                        ch.sprite,
                        ch.pos+i,
                        null
                );
            }
            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );

        }
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 3;
            case 2: return 8;
        }
        return 1;
    }

    private int min(){
        switch (level()){
            case 1: return (int) (5 + Dungeon.hero.lvl/1.5f);
            case 2: return (int) (11 + Dungeon.hero.lvl/0.5f);
        }
        return (int) (2 + Dungeon.hero.lvl / 3f);
    }

    private int max(){
        switch (level()){
            case 1: return (int) (16 + Dungeon.hero.lvl/0.8f);
            case 2: return (int) (35 + Dungeon.hero.lvl/0.33f);
        }
        return (int) (8 + Dungeon.hero.lvl / 2f);
    }

    private int damageRoll() {
        return Random.NormalIntRange(min(), max());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", min(), max(), manaCost());
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        Sample.INSTANCE.play(Assets.Sounds.READ);
        Dungeon.hero.sprite.zap(bolt.collisionPos, new Callback() {
            @Override
            public void call() {
                Dungeon.hero.sprite.idle();
                MissileSprite starSprite = (MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class);
                Item sprite = new ProjectileStar();
                PointF starDest = DungeonTilemap.tileCenterToWorld(bolt.collisionPos);
                PointF starSource = DungeonTilemap.raisedTileCenterToWorld(Dungeon.hero.pos);
                starSource.y -= 200;

                starSprite.reset( starSource, starDest, sprite, callback);
            }
        });
    }

    public static class ProjectileStar extends Item {
        {
            image = ItemSpriteSheet.STARS;
        }

        @Override
        public Emitter emitter() {
            Emitter e = new Emitter();
            e.pos(6, 6);
            e.fillTarget = false;
            e.pour(MagicMissile.YogParticle.FACTORY, 0.03f);
            return e;
        }
    };
}
