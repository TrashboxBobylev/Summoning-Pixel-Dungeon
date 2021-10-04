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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.knight;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Shards extends AdHocSpell {

    {
        image = ItemSpriteSheet.SPREAD;
    }

    private HashMap<Callback, Mob> targets = new HashMap<>();

    @Override
    public void effect(Hero hero) {
        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.level.distance(curUser.pos, mob.pos) <= 8
                    && Dungeon.level.heroFOV[mob.pos]
                    && mob.alignment != Char.Alignment.ALLY) {

                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        Mob ch = targets.get(this);
                        if (ch != null) {
                            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
                            Buff.affect(ch, Knife.SoulGain.class, buff());
                            if (level() >= 1) {
                                ch.damage(damage(), hero);
                                GoatClone clone = GoatClone.findClone();
                                if (clone != null) {
                                    ch.aggro(clone);
                                }
                                if (level() >= 2){
                                    Buff.prolong(ch, SoulParalysis.class, 1f);
                                }
                            }
                        }
                        targets.remove( this );
                        if (targets.isEmpty()) {
                            Invisibility.dispel();
                            hero.spendAndNext( hero.attackDelay() );
                        }
                    }
                };

                MagicMissile.boltFromChar( curUser.sprite.parent,
                        MagicMissile.CRYSTAL_SHARDS,
                        curUser.sprite,
                        mob.pos,
                        callback);

                targets.put( callback, mob );
            }
        }

        if (targets.size() == 0) {
            GLog.warning( Messages.get(this, "no_enemies") );
            return;
        }

        curUser.sprite.zap( curUser.pos );
        curUser.busy();
    }

    private float buff(){
        switch (level()){
            case 1: return 15.0f;
            case 2: return 30.0f;
        }
        return 9.0f;
    }

    private int damage(){
        switch (level()){
            case 1: return Random.NormalIntRange(5 + Dungeon.hero.lvl/3, 12 + Dungeon.hero.lvl*3/4);
            case 2: return Random.NormalIntRange(8 + Dungeon.hero.lvl/2, 18 + Dungeon.hero.lvl);
        }
        return 0;
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 40;
            case 2: return 60;
        }
        return 15;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#.#").format(buff()), manaCost());
    }

}
