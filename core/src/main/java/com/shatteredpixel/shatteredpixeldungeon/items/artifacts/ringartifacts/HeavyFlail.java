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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class HeavyFlail extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_FLAIL;
        levelCap = 10;
        defaultAction = AC_LAUNCH;
        setArtifactClass(ArtifactClass.DEFENSE);
    }

    public static final String AC_LAUNCH = "LAUNCH";
    public static final String AC_REFORGE = "REFORGE";

    public class Sprite extends Item {

        {
            image = ItemSpriteSheet.ARTIFACT_FLAIL;
        }

        @Override
        public void onMissileCreate(Visual vis, PointF from, PointF to, PointF distance) {
            vis.parent.add(new Chains(hero.sprite.center(), to, () -> {},
                    Effects.get(Effects.Type.HEAVY_CHAIN), distance.length()/160f));
        }

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos( user, dst );
            QuickSlotButton.target(Actor.findChar(cell));
            user.sprite.zap( cell );
            user.busy();
            Hunger.adjustHunger(-3*castDelay(user, dst));
            Invisibility.dispel();

            throwSound();
            final float delay = castDelay(user, dst);
            WarriorShield shielding = user.buff(WarriorShield.class);
            MissileSprite missileSprite = (MissileSprite) user.sprite.parent.recycle(MissileSprite.class);
            missileSprite.
                    reset(user.sprite,
                            cell,
                            this,
                            () -> {
                                WandOfBlastWave.BlastWave.blast(cell);
                                user.spendAndNext(delay);
                                Dungeon.level.pressCell(cell);
                                Sample.INSTANCE.play( Assets.Sounds.BLAST );
                                int distance = 1 + HeavyFlail.this.level()/3;
                                TargetedCell targetedCell = new TargetedCell(cell, 0xb85d00);
                                user.sprite.parent.addToBack(targetedCell);
                                Ballistica aim;
                                if (cell % Dungeon.level.width() > 10){
                                    aim = new Ballistica(cell, cell - 1, Ballistica.WONT_STOP);
                                } else {
                                    aim = new Ballistica(cell, cell + 1, Ballistica.WONT_STOP);
                                }
                                ConeAOE aoe = new ConeAOE(aim, distance*1.5f, 360, Ballistica.FRIENDLY_PROJECTILE);
                                if (targetedCell.visible) {
                                    for (Ballistica ray : aoe.rays) {
                                        ((MagicMissile) missileSprite.parent.recycle(MagicMissile.class)).reset(
                                                MagicMissile.EARTH_CONE,
                                                targetedCell,
                                                ray.path.get(ray.dist),
                                                null
                                        );
                                    }
                                }
                                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), distance );
                                for (int i = 0; i < PathFinder.distance.length; i++) {
                                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                        Char ch = Actor.findChar(i);
                                        if (ch != null && ch.alignment != Char.Alignment.ALLY){
                                            ch.damage(
                                                    Math.round(Random.NormalIntRange(
                                                            shielding.shielding()*2,
                                                            shielding.shielding()*4)*
                                                            Math.max(0.2f, 1f - Dungeon.level.distance(cell, i)*0.25f)), user);
                                            Buff.affect(ch, DefenseDebuff.class, 5f);
                                        }
                                    }
                                }
                                shielding.absorbDamage(shielding.shielding());
                                Camera.main.shake( 4, 0.25f );
                            });
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed) {
            WarriorShield sealBuff = hero.buff(WarriorShield.class);
            if (sealBuff != null && sealBuff.shielding() > 0)
                actions.add(AC_LAUNCH);
            if (level() < levelCap)
                actions.add(AC_REFORGE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_LAUNCH)){
            if (!isEquipped(hero)) GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (cursed)       GLog.i( Messages.get(this, "cursed") );
            else {
                WarriorShield sealBuff = hero.buff(WarriorShield.class);
                if (sealBuff == null || sealBuff.shielding() <= 0)
                    GLog.i( Messages.get(this, "no_charge") );
                else {
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
        if (action.equals(AC_REFORGE)){
            curItem = this;
            curUser = hero;
            GameScene.selectItem(itemSelector);
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if ( isEquipped( Dungeon.hero ) ){
            if (!cursed) {
                WarriorShield sealBuff = hero.buff(WarriorShield.class);
                if (sealBuff != null){
                    desc += "\n\n" + Messages.get(this, "desc_worn", maxShield(level()), Math.round(sealBuff.rechargeRate()));
                    if (sealBuff.shielding() > 0)
                        desc += Messages.get(this, "desc_action", sealBuff.shielding()*2, sealBuff.shielding()*4, 3+(level()/3)*2);
                }
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_upgrade");

            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    public class heavyBuff extends ArtifactBuff {

    }

    @Override
    public void charge(Hero target, float amount) {
        WarriorShield shield = target.buff(WarriorShield.class);
        if (shield != null){
            if (shield.shielding() < shield.maxShield()){
                shield.partialShield += 1/(shield.rechargeRate()*6);
                while (shield.partialShield >= 1){
                    shield.incShield();
                    shield.partialShield--;
                }
            }
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new heavyBuff();
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        Buff.affect(ch, WarriorShield.class).setSource(this);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            WarriorShield sealBuff = hero.buff(WarriorShield.class);
            if (sealBuff != null) sealBuff.setSource(null);
            return true;
        } else {
            return false;
        }
    }

    public static int maxShield(int level){
        return Math.round(3 + level*2f);
    }

    public static class WarriorShield extends ShieldBuff {

        private HeavyFlail source;
        float partialShield;

        public float rechargeRate(){
            return 30f - source.level()*2f;
        }

        @Override
        public synchronized boolean act() {
            if (shielding() < maxShield()) {
                partialShield += 1/rechargeRate();
            }

            while (partialShield >= 1){
                incShield();
                partialShield--;
            }

            if (shielding() <= 0 && maxShield() <= 0){
                detach();
            }

            spend(TICK);
            return true;
        }

        public synchronized void supercharge(int maxShield){
            if (maxShield > shielding()){
                setShield(maxShield);
            }
        }

        public synchronized void setSource(HeavyFlail arm){
            source = arm;
        }

        public synchronized int maxShield() {
            if (source != null && source.isEquipped((Hero)target)) {
                return HeavyFlail.maxShield(source.level());
            } else {
                return 0;
            }
        }

        @Override
        //logic edited slightly as buff should not detach
        public int absorbDamage(int dmg) {
            if (shielding() >= dmg){
                decShield(dmg);
                dmg = 0;
            } else {
                dmg -= shielding();
                setShield(0);
            }
            return dmg;
        }
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                new Sprite().cast(curUser, target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(HeavyFlail.class, "prompt");
        }
    };

    protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(HeavyFlail.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            boolean condition = item instanceof Armor && ((Armor) item).tier > 1 && !(item instanceof ConjurerArmor);
            if (item.isEquipped(curUser) && item.visiblyCursed()) condition = false;
            return condition;
        }

        @Override
        public void onSelect( Item item ) {
            if (item instanceof Armor) {
                Hero hero = Dungeon.hero;
                hero.sprite.operate( hero.pos );
                hero.busy();
                Sample.INSTANCE.play( Assets.Sounds.EVOKE, 1f, 0.5f);
                Item.evoke(hero);
                hero.spend( Food.TIME_TO_EAT );
                ((HeavyFlail) curItem).exp += ((Armor) item).tier-1;
                int preLevel = curItem.level();

                while (((HeavyFlail) curItem).exp >= curItem.level()+1 &&
                        curItem.level() < ((HeavyFlail) curItem).levelCap){
                    ((HeavyFlail) curItem).exp -= curItem.level()+1;
                    curItem.upgrade();
                    GLog.positive( Messages.get(HeavyFlail.class, "level_up") );
                }
                if (preLevel == curItem.level())
                    GLog.i( Messages.get(HeavyFlail.class, "feed") );
                if (item.isEquipped(hero))
                    ((Armor) item).doUnequip(hero, false);
                item.detach(hero.belongings.backpack);
            }
        }
    };
}
