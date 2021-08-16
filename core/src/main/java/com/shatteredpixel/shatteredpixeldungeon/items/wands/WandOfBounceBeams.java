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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenseDebuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfBounceBeams extends DamageWand{
    {
        image = ItemSpriteSheet.WAND_DISINTEGRATION;

        chakraGain = 7;

        collisionProperties = Ballistica.LASER;
    }

    private Ballistica collisionPos;

    public int bounceCount(int lvl){
        return 4 + Math.round(lvl*1.5f);
    }

    public int min(int lvl){
        return 4 + lvl/3;
    }

    public int max(int lvl){
        return 8 + lvl/2;
    }

    @Override
    public void onZap(Ballistica beam) {
        for (int i : beam.subPath(1, Integer.MAX_VALUE)) {
            affectCell(i);
        }
    }

    private void affectCell(int i) {
        Char ch = Actor.findChar(i);
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            if (ch != Dungeon.hero) {
                ch.damage(damageRoll(), this);
                Buff.prolong(ch, DefenseDebuff.class, 3 + level());
            }
            ch.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
        }
    }

    /*@Override
    protected void wandUsed() {
        Statistics.wandUses++;
        if (!isIdentified() && availableUsesToID >= 1) {
            availableUsesToID--;
            usesLeftToID--;
            if (usesLeftToID <= 0) {
                identify();
                GLog.positive( Messages.get(Wand.class, "identify") );
                Badges.validateItemLevelAquired( this );
            }
        }

        curCharges -= cursed ? 1 : chargesPerCast();
        if (Dungeon.hero.buff(EnergyOverload.class) != null && !cursed) curCharges += chargesPerCast();

        if (curUser.heroClass == HeroClass.MAGE) levelKnown = true;
        updateQuickslot();
        int bounces = bounceCount(level());

        if (!cursed) {
            while (--bounces > 0) {
                int collPos = collisionPos.collisionPos;
                int randomDir = PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
                while (!Dungeon.level.passable[collPos + randomDir]){
                    randomDir = PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
                }
                    final Ballistica dest = new Ballistica(collPos, collPos + randomDir , Ballistica.MAGIC_BOLT);

                    curUser.sprite.parent.add(
                            new Beam.DeathRay(DungeonTilemap.raisedTileCenterToWorld(collPos), DungeonTilemap.raisedTileCenterToWorld(dest.collisionPos)));
                            Char ch = Actor.findChar(dest.collisionPos);
                            if (ch != null) {
                                processSoulMark(ch, chargesPerCast());
                                if (ch != Dungeon.hero) ch.damage(damageRoll(), this);
                                else ch.damage((int) (damageRoll()*0.66f), this);
                                Buff.prolong(ch, DefenseDebuff.class, 3 + level());

                                ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
                            } else {
                                Dungeon.level.pressCell(dest.collisionPos);
                            }
                            collisionPos = dest;
            }
        }

        curUser.spendAndNext(1f);
    }*/

    @Override
    public boolean tryToZap(Hero owner, int target) {
        return super.tryToZap(owner, target);
    }

    @Override
    public void fx(Ballistica beam, Callback callback) {
        if (beam.reflectPositions.size() == 0)
            curUser.sprite.parent.add(
                    new Beam.DeathRay(curUser.sprite.center(),
                         DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));

        else for (int i = 0; i < beam.reflectPositions.size(); i++) {
            curUser.sprite.parent.add(
                    new Beam.DeathRay(i == 0 ? curUser.sprite.center() : DungeonTilemap.raisedTileCenterToWorld(beam.reflectPositions.get(i - 1)),
                            DungeonTilemap.raisedTileCenterToWorld(beam.reflectPositions.get(i))));
        }
        Sample.INSTANCE.play(Assets.Sounds.RAY);
        callback.call();
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

    }

    @Override
    public String statsDesc() {
        if (!levelKnown)
            return Messages.get(this, "stats_desc", min(0), max(0), 2);
        else
            return Messages.get(this, "stats_desc", min(), max(),  bounceCount(level()));
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x220022);
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.acc.set(10, -10);
        particle.setSize( 0.5f, 3f);
        particle.shuffleXY(1f);
    }
}
