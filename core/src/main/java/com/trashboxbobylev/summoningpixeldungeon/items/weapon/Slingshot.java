/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon;

import com.badlogic.gdx.utils.IntMap;
import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.effects.Splash;
import com.trashboxbobylev.summoningpixeldungeon.items.Heap;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.Bag;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.sprites.MissileSprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
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
    }

    @Override
    public int STRReq(int lvl) {
        return Dungeon.hero.STR();
    }

    @Override
    public int min(int lvl) {
        return (STRReq() - 10) + 1;
    }

    @Override
    public int max(int lvl) {
        return 6 + (STRReq() - 10)*2;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_SHOOT);
        return actions;
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
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        charge	= bundle.getInt( VOLUME );
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target ) {
            if (target != null) {
                if (charge > 0) {
                    final Stone stone = new Stone();
                    target = new Ballistica( curUser.pos, target, Ballistica.PROJECTILE ).collisionPos;
                    final Char enemy = Actor.findChar(target);
                    charge -= 1;
                    updateQuickslot();
                    curUser.sprite.zap(target);
                    final float delay = speedFactor( curUser );
                    final int cell = target;
                    if (enemy != null && enemy != Dungeon.hero) {
                        ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                                reset(curUser.sprite,
                                        enemy.sprite,
                                        stone,
                                        new Callback() {
                                            @Override
                                            public void call() {
                                                if (!curUser.shoot(enemy, stone)) {
                                                    stone.rangedMiss(cell);
                                                } else {

                                                    stone.rangedHit(enemy, cell);

                                                }
                                                curUser.spendAndNext(delay);
                                            }
                                        });
                    } else {
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
                    }
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
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
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
        return true;
    }

    public class Stone extends MissileWeapon {
        {
            image = ItemSpriteSheet.THROWING_STONE;
            sticky = false;
        }

        @Override
        protected float durabilityPerUse() {
            return 0;
        }

        @Override
        public String info() {
            return desc();
        }

        @Override
        public int damageRoll(Char owner) {
            return Slingshot.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return Slingshot.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return Slingshot.this.proc(attacker, defender, damage);
        }

        @Override
        public float speedFactor(Char user) {
            return Slingshot.this.speedFactor(user);
        }

        @Override
        public int STRReq(int lvl) {
            return Slingshot.this.STRReq(lvl);
        }


        @Override
        public boolean doPickUp( Hero hero ) {

            Slingshot slingshot = hero.belongings.getItem( Slingshot.class );

            if (slingshot != null && !slingshot.isFull()){

                slingshot.collectStone(quantity);

            } else {

                    GLog.i( Messages.get(this, "already_full") );
                    return false;

            }

            Sample.INSTANCE.play( Assets.SND_DEWDROP );
            hero.spendAndNext( TIME_TO_PICK_UP );

            return true;
        }
    }
}
