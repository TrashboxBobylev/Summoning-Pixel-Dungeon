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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.MagicPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpQueenSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.text.DecimalFormat;

public class ImpQueen extends Minion {
    {
        spriteClass = ImpQueenSprite.class;
        attunement = 3f;
        isTanky = true;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (buff(MorphTimer.class) == null){
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }
        else return super.canAttack(enemy);
    }

    @Override
    protected boolean act() {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (fieldOfView != null && fieldOfView.length == Dungeon.level.length()) {
                if (fieldOfView[mob.pos] && mob instanceof Imp) {
                    ((Imp) mob).callToQueen(pos);
                    if (buff(MagicPower.class) != null && mob.HP < mob.HT) mob.HP++;
                }
            }
        }
        if (buff(MorphTimer.class) != null) sprite.showStatus(CharSprite.NEUTRAL, new DecimalFormat("#").format(buff(MorphTimer.class).cooldown()+1));
        if (buff(MagicPower.class) != null && HP + 2 + lvl < HT) HP += 2 + lvl;
        return super.act();
    }

    @Override
    public void die(Object cause) {
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob instanceof Imp) {
                mob.die(null);
            }
        }
        super.die(cause);
    }

    protected boolean doAttack(Char enemy ) {

        if (buff(MorphTimer.class) != null) {

            return super.doAttack( enemy );

        } else {

            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap( enemy.pos );
            } else {
                zap();
            }

            return !visible;
        }
    }

    private void zap() {
        spend( TICK );

        if (hit( this, enemy, true ) &&
                (!enemy.properties().contains(Char.Property.BOSS)
                && !enemy.properties().contains(Char.Property.MINIBOSS))) {
            float duration = 250f;
            switch (lvl){
                case 1: duration = 150f; break;
                case 2: duration = 75f; break;
            }
            if (buff(MagicPower.class) != null) duration += 100f;
            Buff.append(this, MorphTimer.class, duration);
            int impPosition = enemy.pos;
            enemy.HP = 0;
            enemy.sprite.die();
            Actor.remove( enemy );
            Dungeon.level.mobs.remove( enemy );
            if (Dungeon.hero.isAlive()) {

                if (enemy.alignment == Alignment.ENEMY) {
                    Statistics.enemiesSlain++;
                    Badges.validateMonstersSlain();
                    Statistics.qualifiedForNoKilling = false;
                }
            }
            Sample.INSTANCE.play(Assets.Sounds.CHARMS);

            Imp imp = new Imp();
            imp.callToQueen(pos);
            GameScene.add(imp);
            ScrollOfTeleportation.appear(imp, impPosition);
            CellEmitter.center( impPosition ).burst( MagicMissile.WardParticle.UP, Random.IntRange( 8, 15 ) );
            float impMod = 2.5f;
            switch (lvl){
                case 1: impMod = 1.50f; break;
                case 2: impMod = 0.75f; break;
            }
            imp.setDamage(
                   Math.round(minDamage*impMod),
                    Math.round(maxDamage*impMod));
            imp.strength = strength;
            switch (lvl){
                case 0: impMod = 20; break;
                case 1: impMod = 8; break;
                case 2: impMod = 3; break;
            }
            imp.setMaxHP((int) impMod);
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    public static class MorphTimer extends FlavourBuff {

    }
}
