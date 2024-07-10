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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.WarriorAbilityButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Slingshot extends Weapon {

    private static final int MAX_VOLUME = 1;
    private static final String AC_SHOOT = "SHOOT";
    public int charge;

    {
        image = ItemSpriteSheet.SLINGSHOT;
        defaultAction = AC_SHOOT;
        usesTargeting = true;
        levelKnown = true;
        bones = false;
        unique = true;
        Stone.slingshot = this;
    }

    @Override
    public int STRReq() {
        return Dungeon.hero.STR();
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq();
    }

    @Override
    public int min(int lvl) {
        return (STRReq() - 10)*3 + 3 + (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public int max(int lvl) {
        return 5 + (STRReq() - 10)*3 + (curseInfusionBonus ? 2 : 0);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_SHOOT);
        return actions;
    }

    @Override
    protected void onThrow(int cell) {
        Stone.slingshot = null;
        super.onThrow(cell);
    }

    @Override
    public boolean doPickUp(Hero hero) {
        Stone.slingshot = this;
        return super.doPickUp(hero);
    }

    @Override
    public void doDrop(Hero hero) {
        Stone.slingshot = null;
        super.doDrop(hero);
    }

    @Override
    public String status() {
        return Messages.format( "%d/%d", charge, MAX_VOLUME );
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell( shooter );

        }
    }

    private static final String VOLUME	= "volume";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( VOLUME, charge );
        levelKnown = true;
        cursedKnown = true;
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        charge	= bundle.getInt( VOLUME );
        levelKnown = true;
        cursedKnown = true;
        level(1);
        Stone.slingshot = this;
    }

    @Override
    public int level() {
        return (Dungeon.hero != null ) ? Dungeon.hero.STR() - 10 : 0;
    }

    @Override
    public int damageRoll(Char owner) {
        return augment.damageFactor(super.damageRoll(owner));
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target ) {
            if (target != null) {
                if (charge > 0) {
                    final Stone stone = new Stone();
                    target = new Ballistica( curUser.pos, target, Ballistica.FRIENDLY_PROJECTILE ).collisionPos;
                    charge -= 1;
                    updateQuickslot();
                    curUser.sprite.zap(target);
                    final float delay = speedFactor( curUser );
                    final int cell = target;
                    ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                            reset(curUser.sprite,
                                    target,
                                    stone,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            curUser.spendAndNext(delay);
                                            stone.onThrow(cell);
                                        }
                            });
                } else if (charge == 0) {
                    Messages.get(Slingshot.class, "no_charge");
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    @Override
    public String info() {
        String info = desc();

        info += "\n\n" + Messages.get( Slingshot.class, "stats",
                Math.round(augment.damageFactor((STRReq() - 10)*3 + 3 + (curseInfusionBonus ? 1 : 0))),
                STRReq());

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped( Dungeon.hero )) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

        return info;
    }

    public void collectStone(int quantity) {
        charge += quantity;
        if (charge >= MAX_VOLUME) {
            charge = MAX_VOLUME;
        }

        updateQuickslot();
    }

    public boolean isFull() {
        return charge >= MAX_VOLUME;
    }

    @Override
    public boolean isUpgradable() {
            return false;
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }



    public static class Stone extends MissileWeapon {
        {
            image = ItemSpriteSheet.THROWING_STONE;
            sticky = false;
            unique = true;
        }

        @Override
        protected float durabilityPerUse() {
            return 0;
        }

        @Override
        public String info() {
            return desc();
        }

        private static Slingshot slingshot;

        @Override
        public int damageRoll(Char owner) {
            if (slingshot != null) return slingshot.damageRoll( owner);
            else return 0;
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            if (slingshot != null) return slingshot.hasEnchant(type, owner);
            else return false;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {

            if (slingshot != null) return slingshot.proc(attacker, defender, damage);
            else return damage;
        }

        @Override
        public float speedFactor(Char user) {
            if (slingshot != null) return slingshot.speedFactor(user);
            else return 1f;
        }

        @Override
        public int STRReq(int lvl) {
            if (slingshot != null)return slingshot.STRReq(lvl);
            else return 10;
        }


        @Override
        public boolean doPickUp( Hero hero ) {
            slingshot = hero.belongings.getItem(Slingshot.class);

            if (slingshot != null) {
                if (!slingshot.isFull()) {

                    slingshot.collectStone(quantity);

                } else {

                    GLog.i(Messages.get(this, "already_full"));
                    return false;

                }
            } else {

                GLog.i(Messages.get(this, "cant_pickup"));
                return false;

            }

            GameScene.pickUp( this, hero.pos );
            Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
            hero.spendAndNext( TIME_TO_PICK_UP );

            return true;
        }

        @Override
        public void rangedHit(Char enemy, int cell) {
            super.rangedHit(enemy, cell);
            Hunger hunger = Dungeon.hero.buff(Hunger.class);
            if (Dungeon.hero.subClass == HeroSubClass.BERSERKER &&
                !(hunger == null) && !hunger.isHungry() &&
                    Dungeon.hero.belongings.weapon != null &&
                    ((MeleeWeapon)Dungeon.hero.belongings.weapon).checkSeal() != null){
                WarriorAbilityButton.doAttack(enemy);
            }
        }
    }
}
