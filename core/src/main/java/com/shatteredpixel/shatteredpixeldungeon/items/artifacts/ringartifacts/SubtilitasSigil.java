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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DummyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class SubtilitasSigil extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_SIGIL;
        charge = 100;
        partialCharge = 0;
        chargeCap = 100;
        levelCap = 5;
        defaultAction = AC_USE;
        setArtifactClass(ArtifactClass.OFFENSE);
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && charge > 99 && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){

            curUser = hero;

            if (!isEquipped( hero )) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                QuickSlotButton.cancel();

            } else if (charge < 100) {
                GLog.i( Messages.get(this, "no_charge") );
                QuickSlotButton.cancel();

            } else if (cursed) {
                GLog.warning( Messages.get(this, "cursed") );
                QuickSlotButton.cancel();

            } else {
                GameScene.selectCell(caster);
            }

        }
    }

    public String desc() {
        String desc = super.desc();

        if (isEquipped( Dungeon.hero )){
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else {
                desc += Messages.get(this, "desc_equipped");
                if (level() < levelCap){
                    desc += "\n\n" + Messages.get(this, "desc_hint");
                }
            }
        }
        return desc;
    }

    public CellSelector.Listener caster = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                curUser.spend(1f);
                curUser.sprite.idle();
                curUser.sprite.zap(cell);
                Sample.INSTANCE.play(Assets.Sounds.RAY);

                final Ballistica bolt = new Ballistica(curUser.pos, cell, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

                int maxDist = 3 + level();
                int dist = Math.min(bolt.dist, maxDist);

                final ConeAOE cone = new ConeAOE(bolt, dist, 60, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.IGNORE_SOFT_SOLID);

                //cast to cells at the tip, rather than all cells, better performance.
                for (Ballistica ray : cone.rays) {
                    curUser.sprite.parent.add(
                            new Beam.RedRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(ray.collisionPos)));
                }
                for (int c : cone.cells) {
                    Char ch = Actor.findChar(c);
                    if (ch != null) {

                        Buff.affect(ch, EnrageBuff.class, 3 + level());
                        Buff.affect(ch, Amok.class, 2 + level()*0.75f);
                    }
                }

                curUser.next();
                charge = 0;
            }

        }

        @Override
        public String prompt() {
            return Messages.get(SubtilitasSigil.class, "prompt");
        }
    };

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Recharge();
    }

    public class Recharge extends ArtifactBuff {

        public void gainExp(int exp){
            SubtilitasSigil.this.exp += exp;
            target.sprite.emitter().burst(FlameParticle.FACTORY, 5);
            if (SubtilitasSigil.this.exp > 5 + (level()+1)*8){
                SubtilitasSigil.this.exp = 0;
                GLog.positive( Messages.get(SubtilitasSigil.class, "level_up") );
                upgrade();
                updateQuickslot();
            }
        }

        @Override
        public boolean act() {

            spend( TICK );

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                //300 turns to a full charge
                partialCharge += (1/3f);
                if (partialCharge > 1){
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap){
                        partialCharge = 0f;
                    }
                }
            }

            updateQuickslot();

            return true;
        }
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            charge += 2f * amount;
            if (charge == chargeCap) {
                partialCharge = 0;
            }
        }
    }

    public static class EnrageBuff extends DummyBuff{

        {
            announced = true;
            type = buffType.NEGATIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.FURY;
        }
    }
}
