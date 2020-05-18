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

package com.trashboxbobylev.summoningpixeldungeon.items.wands;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Badges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.Statistics;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.DefenseDebuff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.powers.EnergyOverload;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroClass;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.PurpleParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MagesStaff;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.Collections;

public class WandOfBounceBeams extends DamageWand{
    {
        image = ItemSpriteSheet.WAND_DISINTEGRATION;

        collisionProperties = Ballistica.MAGIC_BOLT;
    }

    private Ballistica collisionPos;

    public int bounceCount(int lvl){
        int count = 2 + lvl*2;
        return count;
    }

    public int min(int lvl){
        return 5;
    }

    public int max(int lvl){
        return 10;
    }

    @Override
    protected void onZap(Ballistica beam) {
        Char ch = Actor.findChar(beam.collisionPos);
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);

            ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
        } else {
            Dungeon.level.pressCell(beam.collisionPos);
        }
        collisionPos = beam;
    }

    @Override
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
    }

    @Override
    protected void fx( Ballistica beam, Callback callback ) {
        curUser.sprite.parent.add(
                new Beam.DeathRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play( Assets.SND_RAY );
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
