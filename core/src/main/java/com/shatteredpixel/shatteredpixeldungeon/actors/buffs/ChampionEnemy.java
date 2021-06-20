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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class ChampionEnemy extends Buff {

    {
        type = buffType.POSITIVE;
    }

    public int color;

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(color);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    public void onAttackProc(Char enemy) {

    }

    public boolean canAttackWithExtraReach(Char enemy) {
        return false;
    }

    public float meleeDamageFactor() {
        return 1f;
    }

    public float damageTakenFactor() {
        return 1f;
    }

    public float evasionAndAccuracyFactor() {
        return 1f;
    }

    public static void rollForChampion(Mob m) {

        switch (Random.Int(12)) {
            case 0:
            default:
                Buff.affect(m, Blazing.class);
                break;
            case 1:
                Buff.affect(m, Projecting.class);
                break;
            case 2:
                Buff.affect(m, AntiMagic.class);
                break;
            case 3:
                Buff.affect(m, Giant.class);
                break;
            case 4:
                Buff.affect(m, Blessed.class);
                break;
            case 5:
                Buff.affect(m, Growing.class);
                break;
            case 6:
                Buff.affect(m, Cursed.class);
                break;
            case 7:
                Buff.affect(m, Splintering.class);
                break;
            case 8:
                Buff.affect(m, Stone.class);
                break;
            case 9:
                Buff.affect(m, Flowing.class);
                break;
            case 10:
                Buff.affect(m, Voodoo.class);
                break;
            case 11:
                Buff.affect(m, Explosive.class);
                break;
        }

        m.state = m.WANDERING;
    }



    public static class Blazing extends ChampionEnemy {

        {
            color = 0xFF8800;
        }

        @Override
        public void onAttackProc(Char enemy) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }

        @Override
        public void detach() {
            for (int i : PathFinder.NEIGHBOURS9){
                if (!Dungeon.level.solid[target.pos+i]){
                    GameScene.add(Blob.seed(target.pos+i, 2, Fire.class));
                }
            }
            super.detach();
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        {
            immunities.add(Burning.class);
        }
    }

    public static class Cursed extends ChampionEnemy {

        {
            color = 0x181212;
        }

        @Override
        public void onAttackProc(Char enemy) {
            CursedWand.cursedEffect(null, target, enemy);
        }
    }

    public static class Projecting extends ChampionEnemy {

        {
            color = 0x8800FF;
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        @Override
        public boolean canAttackWithExtraReach( Char enemy ) {
            return target.fieldOfView[enemy.pos]; //if it can see it, it can attack it.
        }
    }

    public static class Splintering extends ChampionEnemy {

        {
            color = 0xbfba72;
        }

        @Override
        public float damageTakenFactor() {
            if (target.HP >= 2) {
                ArrayList<Integer> candidates = new ArrayList<>();
                boolean[] solid = Dungeon.level.solid;

                int[] neighbours = {target.pos + 1, target.pos - 1, target.pos + Dungeon.level.width(), target.pos - Dungeon.level.width()};
                for (int n : neighbours) {
                    if (!solid[n] && Actor.findChar( n ) == null) {
                        candidates.add( n );
                    }
                }

                if (candidates.size() > 0) {

                    Mob clone = (Mob) Reflection.newInstance(target.getClass());
                    clone.HP = target.HP/ 2;
                    clone.pos = Random.element( candidates );
                    clone.state = clone.HUNTING;

                    Dungeon.level.occupyCell(clone);

                    GameScene.add( clone, 1f );
                    Actor.addDelayed( new Pushing( clone, target.pos, clone.pos ), -1 );

                    target.HP -= clone.HP;
                }
            }
            return 1f;
        }

    }

    public static class AntiMagic extends ChampionEnemy {

        {
            color = 0x00FF00;
        }

        @Override
        public float damageTakenFactor() {
            return 0.75f;
        }

        {
            immunities.addAll(com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
        }

    }

    public static class Explosive extends ChampionEnemy {

        {
            color = 0xff4400;
        }

        @Override
        public void detach() {
            for (int i : PathFinder.NEIGHBOURS4){
                if (!Dungeon.level.solid[target.pos+i]){
                    new Bomb().explode(target.pos+i);
                }
            }
            super.detach();
        }
    }

    public static class Voodoo extends ChampionEnemy {

        {
            color = 0x3d0082;
        }

        @Override
        public float damageTakenFactor() {
            return 1.25f;
        }

        @Override
        public void detach() {
            ArrayList<Class<?extends Mob>> mobsToSpawn;

            mobsToSpawn = Bestiary.getMobRotation(Dungeon.depth);

            Mob clone = Reflection.newInstance(mobsToSpawn.remove(0));
            ChampionEnemy.rollForChampion(clone);
            clone.HP = clone.HT = Math.round(clone.HT * 2.5f);
            clone.pos = target.pos;
            clone.state = clone.HUNTING;

            Dungeon.level.occupyCell(clone);
            GameScene.add( clone );
            super.detach();
        }
    }

    public static class Stone extends ChampionEnemy {
        {
            color = 0x727272;
        }

        @Override
        public float damageTakenFactor() {
            return Math.max(0.1f, (target.HP * 1f /target.HT));
        }
    }

    public static class Flowing extends ChampionEnemy {
        {
            color = 0xb7f5ff;
        }
    }

    //Also makes target large, see Char.properties()
    public static class Giant extends ChampionEnemy {

        {
            color = 0x0088FF;
        }

        @Override
        public float damageTakenFactor() {
            return 0.25f;
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            if (Dungeon.level.distance( target.pos, enemy.pos ) > 2){
                return false;
            } else {
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                for (Char ch : Actor.chars()) {
                    if (ch != target) passable[ch.pos] = false;
                }

                PathFinder.buildDistanceMap(enemy.pos, passable, 2);

                return PathFinder.distance[target.pos] <= 2;
            }
        }
    }

    public static class Blessed extends ChampionEnemy {

        {
            color = 0xFFFF00;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return 3f;
        }
    }

    public static class Growing extends ChampionEnemy {

        {
            color = 0xFF0000;
        }

        private float multiplier = 1.19f;

        @Override
        public boolean act() {
            multiplier += 0.01f;
            spend(3*TICK);
            return true;
        }

        @Override
        public float meleeDamageFactor() {
            return multiplier;
        }

        @Override
        public float damageTakenFactor() {
            return 1f/multiplier;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return multiplier;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int)(100*(multiplier-1)), (int)(100*(1 - 1f/multiplier)));
        }

        private static final String MULTIPLIER = "multiplier";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MULTIPLIER, multiplier);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            multiplier = bundle.getFloat(MULTIPLIER);
        }
    }

}
