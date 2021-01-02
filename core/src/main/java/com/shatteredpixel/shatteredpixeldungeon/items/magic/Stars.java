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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Stars extends ConjurerSpell {

    {
        image = ItemSpriteSheet.STARS;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null){
            ((MissileSprite) Dungeon.hero.sprite.parent.recycle( MissileSprite.class )).
                    reset( Dungeon.hero.sprite, ch.sprite, new Stars(), new Callback() {
                        @Override
                        public void call() {
                            ch.damage(damageRoll(), this);
                            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );

                            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
                        }
                    } );
        }
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 2;
            case 2: return 4;
        }
        return 1;
    }

    private int min(){
        switch (level()){
            case 1: return (int) (2 + Dungeon.hero.lvl/2.5f);
            case 2: return (int) (6 + Dungeon.hero.lvl/2f);
        }
        return (int) (1 + Dungeon.hero.lvl / 5f);
    }

    private int max(){
        switch (level()){
            case 1: return (int) (8 + Dungeon.hero.lvl/1.2f);
            case 2: return (int) (12 + Dungeon.hero.lvl/0.8f);
        }
        return (int) (5 + Dungeon.hero.lvl / 2.5f);
    }

    private int damageRoll() {
        return Random.NormalIntRange(min(), max());
    }

    @Override
    public String desc() {
        return Messages.get(this, "stats_desc", min(), max(), manaCost());
    }
}
