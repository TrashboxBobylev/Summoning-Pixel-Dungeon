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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Divine extends CloakGlyph {
    private static ItemSprite.Glowing DIVINY = new ItemSprite.Glowing( 0xffff00);

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {

    }

    @Override
    public void onCloaking(CloakOfShadows cloak, Char defender) {
        int totalCharge = cloak.charge + 1;
        cloak.charge = 0;
        new Flare(16, 64).color(0xFFFF00, true).show(defender.sprite, 2.5f);
        Sample.INSTANCE.play( Assets.Sounds.BLAST, 2f );
        CloakOfShadows.cloakStealth cloakBuff = defender.buff( CloakOfShadows.cloakStealth.class );
        if (cloakBuff != null) {
            cloakBuff.dispel();
        }
        PathFinder.buildDistanceMap( defender.pos, BArray.not( Dungeon.level.solid, null ), (int) (2 + totalCharge / 2.5f));
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE && i != defender.pos) {
                Char mob = Actor.findChar(i);
                if (mob instanceof Mob && mob.alignment == Char.Alignment.ENEMY){
                    int dmg = Math.round(Random.NormalIntRange(3 + Dungeon.scaledDepth()/2, 6 + Dungeon.scaledDepth())*CloakGlyph.efficiency()*totalCharge);
                    float debuffDuration = (1f + efficiency()) * (1f + totalCharge / 6f);
                    if (mob.properties().contains(Char.Property.UNDEAD) || mob.properties().contains(Char.Property.DEMONIC)) {
                        mob.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                        Sample.INSTANCE.play(Assets.Sounds.BURNING);
                        dmg *= 1.2f;
                        debuffDuration *= 1.2f;
                    }
                    mob.damage(dmg, new HolyBomb());
                    Buff.affect(mob, SoulParalysis.class, debuffDuration);
                    Buff.affect(mob, Weakness.class, debuffDuration*2);
                    Buff.affect(mob, Vulnerable.class, debuffDuration*2);
                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
                    new Flare(12, 32).color(0xFFFF66, true).show(mob.sprite, 2.5f);
                }
            }
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return DIVINY;
    }
}
