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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.QuiverMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.abilities.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuiver;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class SilkyQuiver extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_QUIVER;
        levelCap = 5;
        charge = 5;
        chargeCap = 5 + level();
        defaultAction = AC_USE;
        setArtifactClass(ArtifactClass.OFFENSE);
    }

    public static final String AC_USE = "USE";
    public static final float CHARGE_GAIN = 0.25f;

    public static ComboMove selectedMove;

    public enum ComboMove {
        SHOOT(1, 0x99ffff){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                arrow.tier = 1 + curUser.lvl/4;
                curUser.shoot(enemy, arrow);
            }
        },
        KNOCKBACK(2, 0xb3b3b3){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                Ballistica trajectory = new Ballistica(curUser.pos, enemy.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(enemy, trajectory, 4, false);
                enemy.sprite.bloodBurstA( curUser.sprite.center(), 10 );
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, Random.Float(0.87f, 1.15f));
            }

            @Override
            public float turnAmount() {
                return 0f;
            }
        },
        MARK(3, 0xff2828){
            @Override
            public float turnAmount() {
                return 2f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                enemy.sprite.bloodBurstA( curUser.sprite.center(), 10 );
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, Random.Float(0.87f, 1.15f));
                Buff.affect(enemy, QuiverMark.class, 35f);
            }
        },
        BURST(4, 0x66b3ff){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                arrow.tier = 2 + curUser.lvl/4;
                curUser.shoot(enemy, arrow);
            }

            @Override
            public float turnAmount() {
                return 1.5f;
            }
        },
        GRIM(5, 0x090e22){

            @Override
            public float turnAmount() {
                return 2f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                arrow.tier = 1 + curUser.lvl/4;
                SilkyQuiver quiver = (SilkyQuiver) curItem;
                boolean successfulKill = false;
                if (curUser.shoot(enemy, arrow)) {
                    if (enemy.isAlive()) {
                        int enemyHealth = enemy.HP;

                        float maxChance = 0.75f + .05f * (Dungeon.hero.lvl / 3f);
                        float chanceMulti = (float) Math.pow(((enemy.HT - enemyHealth) / (float) enemy.HT), 2);
                        float chance = maxChance * chanceMulti;

                        if (Random.Float() < chance) {

                            enemy.damage(enemy.HP, this);
                            enemy.sprite.emitter().burst(ShadowParticle.UP, 10);

                            successfulKill = true;
                        }
                    } else {
                        successfulKill = true;
                    }
                }
                if (successfulKill){
                    quiver.exp++;
                    if (quiver.exp >= quiver.level() && quiver.level() < quiver.levelCap) {
                        quiver.exp -= quiver.level();
                        quiver.upgrade();
                        GLog.positive(Messages.get(SilkyQuiver.class, "level_up"));
                        quiver.charge += 2;
                        updateQuickslot();
                    }
                }
            }
        };

        public final int cost, color;

        ComboMove(int cost, int color) {
            this.cost = cost;
            this.color = color;
        }

        public void execute(Char enemy, Arrow arrow){ }

        public float turnAmount(){
            return 1f;
        }

        public String desc(){
            return Messages.get(this, name()+"_desc");
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed && charge > 0)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 5 + level();
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 5 + level();
        updateQuickslot();
        return this;
    }

    public boolean isUsable(ComboMove move){
        return charge >= move.cost;
    }

    public void useMove(ComboMove move){
        selectedMove = move;
        GameScene.selectCell(shooter);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){
            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (cursed)                     GLog.i( Messages.get(this, "cursed") );
            else if (charge == 0)        GLog.i( Messages.get(this, "no_charge") );
            else {
                curUser = hero;
                curItem = this;
                GameScene.show(new WndQuiver(this));
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
            else {
                desc += "\n\n" + Messages.get(this, "desc_equipped");
            }
        }

        return desc;
    }

    @Override
    public void charge(Hero target, float amount) {
        target.buff(quiverBuff.class).gainCharge(CHARGE_GAIN);
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new quiverBuff();
    }

    public class quiverBuff extends ArtifactBuff {

        public void gainCharge(){
            gainCharge(CHARGE_GAIN);
        }

        public void gainCharge(float charge_gain){
            if (charge < chargeCap && !cursed){
                partialCharge += charge_gain;
                if (partialCharge >= 1) {
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap){
                        partialCharge = 0f;
                        GLog.positive( Messages.get(SilkyQuiver.class, "charged") );
                    }
                    updateQuickslot();
                }
            }
        }
    }

    public static class Arrow extends MissileWeapon {
        {
            image = ItemSpriteSheet.QUIVER_ARROW;
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.8f, 1.2f) );
        }

        @Override
        public Emitter emitter() {
            Emitter e = new Emitter();
            e.pos(5, 5);
            e.fillTarget = false;
            e.pour(Speck.factory(Speck.DISCOVER), 0.02f);
            return e;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return super.proc(attacker, defender, damage);
        }

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos(user, dst);
            QuickSlotButton.target(Actor.findChar(cell));
            Hunger.adjustHunger(-3 * castDelay(user, dst));
            Invisibility.dispel();
            if (selectedMove == ComboMove.BURST){
                Ballistica b = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);
                final HashSet<Char> targets = new HashSet<>();
                Char enemy = SpectralBlades.findChar(b, curUser, 0, targets);
                if (enemy == null){
                    GLog.warning(Messages.get(SpectralBlades.class, "no_target"));
                    ((SilkyQuiver)curItem).charge += selectedMove.cost;
                    selectedMove = null;
                    Item.updateQuickslot();
                    return;
                }
                targets.add(enemy);
                int degrees = 120;
                ConeAOE cone = new ConeAOE(b, degrees);
                for (Ballistica ray : cone.rays){
                    // 1/3/5/7/9 up from 0/2/4/6/8
                    Char toAdd = SpectralBlades.findChar(ray, curUser, 0, targets);
                    if (toAdd != null && curUser.fieldOfView[toAdd.pos]){
                        targets.add(toAdd);
                    }
                }
                final HashSet<Callback> callbacks = new HashSet<>();
                for (Char ch : targets) {
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            selectedMove.execute(ch, Arrow.this);
                            callbacks.remove( this );
                            if (callbacks.isEmpty()) {
                                Invisibility.dispel();
                                curUser.spendAndNext( selectedMove.turnAmount() );
                                selectedMove = null;
                            }
                        }
                    };

                    MissileSprite m = ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class ));
                    m.reset( curUser.sprite, ch.pos, this, callback );
                    m.alpha(0.8f);
                    throwSound();

                    callbacks.add( callback );
                }

                curUser.sprite.zap( enemy.pos );
                curUser.busy();
            } else {
                user.sprite.zap(cell);
                user.busy();

                throwSound();
                final float delay = selectedMove.turnAmount();
                Char enemy = Actor.findChar(cell);
                QuickSlotButton.target(enemy);
                MissileSprite missileSprite = (MissileSprite) user.sprite.parent.recycle(MissileSprite.class);
                if (enemy != null) {
                    missileSprite.
                            reset(user.sprite,
                                    cell,
                                    this,
                                    () -> {
                                        user.spendAndNext(delay);
                                        selectedMove.execute(enemy, this);
                                        selectedMove = null;
                                    });
                } else {
                    missileSprite.reset(user.sprite,
                            cell,
                            this,
                            () -> {
                                user.spendAndNext(delay);
                                selectedMove = null;
                            });
                }
            }
        }

        @Override
        public int min() {
            if (selectedMove == ComboMove.GRIM){
                return super.min()*2;
            } else {
                return super.min();
            }
        }

        @Override
        public int max() {
            if (selectedMove == ComboMove.GRIM){
                return super.max()*2;
            } else {
                return super.max();
            }
        }

        @Override
        public int STRReq(int lvl) {
            return Dungeon.hero.STR;
        }

        @Override
        public float accuracyFactor(Char owner) {
            if (selectedMove == ComboMove.GRIM){
                return Float.POSITIVE_INFINITY;
            } else {
                return super.accuracyFactor(owner);
            }
        }
    }

    private static final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                Arrow arrow = new Arrow();
                Weapon wep = (Weapon) curUser.belongings.weapon;
                if (wep instanceof MeleeWeapon)
                    arrow.enchant(wep.enchantment);
                arrow.cast(curUser, target);
                ((SilkyQuiver)curItem).charge -= selectedMove.cost;
                updateQuickslot();
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}
