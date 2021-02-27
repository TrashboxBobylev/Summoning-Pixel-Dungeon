/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2021 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Attunement;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooMinionSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GooMinion extends Minion {
    {
        spriteClass = GooMinionSprite.class;
        attunement = 2f;

        properties.add(Property.DEMONIC);
        properties.add(Property.ACIDIC);

    }

    private int pumpedUp = 0;

    @Override
    public int attackSkill( Char target ) {
        int attack = super.attackSkill(target);
        if (pumpedUp > 0) attack *= 2;
        return attack;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return (int)(super.defenseSkill(enemy) * ((HP*2 <= HT)? 1.5 : 1));
    }

    private int chargeTurns(){
        switch (lvl){
            case 1: return 4;
            case 2: return 6;
        }
        return 2;
    }

    @Override
    public boolean act() {

        if (Dungeon.level.water[pos] && HP < HT) {
            sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
            if (HP*2 == HT) {
                ((GooMinionSprite)sprite).spray(false);
            }
            HP++;
        }

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return (pumpedUp > 0) ? distance( enemy ) <= 2 : super.canAttack(enemy);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int( 3 ) == 0) {
            Buff.affect( enemy, Ooze.class ).set( 20f );
            enemy.sprite.burst( 0x000000, 5 );
        }

        if (pumpedUp > 0) {
            Camera.main.shake( 3, 0.2f );
        }

        return damage;
    }

    @Override
    protected boolean doAttack( Char enemy ) {
        if (pumpedUp == 1) {
            ((GooMinionSprite)sprite).pumpUp();
            PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE)
                    GameScene.add(Blob.seed(i, 2, GooWarn.class));
            }
            pumpedUp++;

            spend( attackDelay() );

            return true;
        } else if (pumpedUp >= chargeTurns() || Random.Int( (HP*2 <= HT) ? 2 : 5 ) > 0) {

            boolean visible = Dungeon.level.heroFOV[pos];

            if (visible) {
                if (pumpedUp >= chargeTurns()) {
                    ((GooMinionSprite) sprite).pumpAttack();
                }
                else
                    sprite.attack( enemy.pos );
            } else {
                if (pumpedUp >= chargeTurns()){
                    elementalAttack();
                }
                attack( enemy );
            }

            spend( attackDelay() );

            return !visible;

        } else {

            pumpedUp++;

            ((GooMinionSprite)sprite).pumpUp();

            for (int i=0; i < PathFinder.NEIGHBOURS9.length; i++) {
                int j = pos + PathFinder.NEIGHBOURS9[i];
                if (!Dungeon.level.solid[j]) {
                    GameScene.add(Blob.seed(j, 2, GooWarn.class));
                }
            }

            if (Dungeon.level.heroFOV[pos]) {
                sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "!!!") );
                GLog.negative( Messages.get(this, "pumpup") );
            }

            spend( attackDelay() );

            return true;
        }
    }

    public void elementalAttack(){
        pumpedUp = 0;
        int min = 35, max = 115;
        switch (lvl) {
            case 1:
                min = 60;
                max = 108;
                break;
            case 2:
                min = 90;
                max = 100;
                break;
        }

        float multiplier = 1f;
        if (Dungeon.hero.buff(Attunement.class) != null) multiplier *= Attunement.empowering();

        min *= multiplier; max *= multiplier;

        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {

            if (PathFinder.distance[i] < Integer.MAX_VALUE)
                CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);

            if (Actor.findChar(i) != null){
                Actor.findChar(i).damage(Random.NormalIntRange(min, max), Dungeon.hero);
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.BURNING);

    }

    @Override
    public boolean attack( Char enemy ) {
        boolean result = super.attack( enemy );
        pumpedUp = 0;
        return result;
    }

    @Override
    protected boolean getCloser( int target ) {
        pumpedUp = 0;
        return super.getCloser( target );
    }

    @Override
    public void damage(int dmg, Object src) {
        boolean bleeding = (HP*2 <= HT);
        super.damage(dmg, src);
        if ((HP*2 <= HT) && !bleeding){
            sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
            ((GooMinionSprite)sprite).spray(true);
            yell(Messages.get(this, "gluuurp"));
        }
    }

    private final String PUMPEDUP = "pumpedup";

    @Override
    public void storeInBundle( Bundle bundle ) {

        super.storeInBundle( bundle );

        bundle.put( PUMPEDUP , pumpedUp );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {

        super.restoreFromBundle( bundle );

        pumpedUp = bundle.getInt( PUMPEDUP );
    }
}
