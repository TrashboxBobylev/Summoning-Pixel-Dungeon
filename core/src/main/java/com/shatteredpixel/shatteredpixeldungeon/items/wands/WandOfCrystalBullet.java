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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.EnergyOverload;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfCrystalBullet extends DamageWand {

	{
		image = ItemSpriteSheet.CRYSTAL_WAND;

        collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	public ArrayList<Integer> shardPositions = new ArrayList<>();
    private int collisionPos;

	public int min(int lvl){
		return 2+lvl;
	}

	public int max(int lvl){
		return 6+4*lvl;
	}

	public int shards(int lvl){
	    return Math.min(3 + lvl/2,8);
    }
	
	@Override
	protected void onZap( Ballistica bolt ) {
        Dungeon.level.pressCell(bolt.collisionPos);
        Splash.at(bolt.collisionPos, Random.Int(0xFFe380e3, 0xFF9485c9), level()*4);

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

        shardPositions.clear();
        if (!cursed) {
            for (int i: PathFinder.NEIGHBOURS8){
                final int dest = new Ballistica(collisionPos, collisionPos+i, Ballistica.FRIENDLY_MAGIC).collisionPos;
                if (!shardPositions.contains(dest)){
                    if (Actor.findChar(dest) != null && Actor.findChar(dest) != Dungeon.hero) {
                        MagicMissile missile = ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class ));
                        shardPositions.add(dest);
                        missile.reset(MagicMissile.CRYSTAL_SHARDS, collisionPos, dest,   new Callback() {
                            @Override
                            public void call() {
                                Char ch = Actor.findChar( shardPositions.get(shardPositions.indexOf(dest) ));
                                if (ch != null) {

                                    processSoulMark(ch, chargesPerCast());
                                    ch.damage(damageRoll(), new WandOfCrystalBullet());

                                    ch.sprite.burst(Random.Int(0xFFe380e3, 0xFF9485c9), level() + 3);

                                } else {
                                    Dungeon.level.pressCell(dest);
                                }

                                shardPositions.remove(shardPositions.get(shardPositions.indexOf(dest) ));
                                if (shardPositions.size() == 0){
                                    curUser.spendAndNext( 1f );
                                }
                            }
                        });
                        Sample.INSTANCE.play( Assets.Sounds.SHATTER );
                    }
                    if (shardPositions.size() >= shards(level())) break;
                }
            }
        }

        if (shardPositions.size() == 0) curUser.spendAndNext(1f);
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
//        MagicMissile.boltFromChar( curUser.sprite.parent,
//                MagicMissile.SHADOW,
//                curUser.sprite,
//                bolt.collisionPos,
//                callback);
        collisionPos = bolt.collisionPos;
        MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.CRYSTAL, curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		for (int c : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(defender.pos + c);
            if (ch != null && ch != Dungeon.hero){
                ch.damage((int) (damage * Random.Float(0.2f, 0.5f)), attacker);
                ch.sprite.burst(Random.Int(0xFFe380e3, 0xFF9485c9), level() + 3);
            }
        }
	}

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( Random.Int(0xFFe380e3, 0xFF9485c9) );
        particle.am = 0.5f;
        particle.setLifespan(1f);
        particle.speed.polar(Random.Float(PointF.PI2), 2f);
        particle.setSize( 1f, 2f);
        particle.radiateXY( 0.5f);
    }

    @Override
    public String statsDesc() {
        if (!levelKnown)
            return Messages.get(this, "stats_desc", min(0), max(0), 3);
        else
            return Messages.get(this, "stats_desc", min(), max(),  3 + level() / 2);
    }

    @Override
    public int damageRoll() {
        int i = super.damageRoll();
        if (shardPositions.size() == 1) i *= 0.66f;
        return i;
    }
}
