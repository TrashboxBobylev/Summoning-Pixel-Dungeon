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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LostSpiritSprite;
import com.watabou.utils.Callback;

import java.util.HashSet;

public class LostSpirit extends AbyssalMob implements Callback {

    {
        HP = HT = 145;
        defenseSkill = 72;
        spriteClass = LostSpiritSprite.class;

        EXP = 40;
        maxLvl = 30;

        flying = true;
        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.UNDEAD);
        properties.add(Property.RANGED);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.STOP_TARGET).collisionPos == enemy.pos;
    }

    @Override
    public int attackSkill( Char target ) {
        return 70 + abyssLevel()*3;
    }

    @Override
    public int damage(int dmg, Object src) {
        int distance = Dungeon.level.distance(this.pos, Dungeon.hero.pos) - 1;
        float multiplier = Math.min(0.2f, 1 / (1.32f * (float)Math.pow(1.2f, distance)));
        dmg = Math.round(dmg * multiplier);
        return super.damage(dmg, src);
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos ) && enemy == Dungeon.hero) {

            if (HP > HT/10) {
                //do nothing
            }
            else {
                CellEmitter.bottom(pos).burst(WhiteParticle.UP, 20);
                new SummoningTrap().set( pos ).activate();
                pos = Dungeon.level.randomRespawnCell(this);
            }
            spend(attackDelay());
            return true;

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Badges.validateSpirit();
    }

    private void zap() {
        spend( 1f );

        if (hit( this, enemy, true )) {

            if (enemy.alignment == Alignment.ENEMY){
                    ChampionEnemy.rollForChampion((Mob) enemy);
                    enemy.sprite.emitter().burst( RainbowParticle.BURST, 10);
                    enemy = null;
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }

    {
        immunities.add(Charm.class);
        immunities.add(Terror.class);
        immunities.add(Amok.class);
        immunities.add(Sleep.class);
    }

    public Char chooseEnemy() {

        if (hordeHead != -1 && Actor.findById(hordeHead) != null){
            Mob hordeHead = (Mob) Actor.findById(this.hordeHead);
            if (hordeHead.isAlive()){
                return hordeHead.enemy;
            }
        }

        if ((Dungeon.hero.buff(CloakOfShadows.cloakStealth.class) != null &&
                Dungeon.hero.buff(CloakOfShadows.cloakStealth.class).glyph() instanceof Silent)
        )
            return null;

        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead/missing
        if (enemy != null && enemy.buff(ChampionEnemy.class) != null){
            newEnemy = true;
        }
        else if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
        }
        else if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos)){
            newEnemy = true;
        }

        if ( newEnemy ) {

            HashSet<Char> enemies = new HashSet<>();

                if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
                    return Dungeon.hero;
                }
                //look for ally mobs to attack, ignoring the soul flame
                for (Mob mob :  Dungeon.level.mobs.toArray(new Mob[0]))
                    if (mob.alignment == Alignment.ENEMY && canSee(mob.pos) && mob != this && mob.buff(ChampionEnemy.class) == null)
                        enemies.add(mob);

                return chooseClosest(enemies);

        } else
            return enemy;
    }


}
