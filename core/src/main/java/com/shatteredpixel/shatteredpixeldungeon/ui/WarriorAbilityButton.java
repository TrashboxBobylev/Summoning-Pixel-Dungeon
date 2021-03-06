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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.GameAction;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WarriorAbilityButton extends Tag {

    private static final float ENABLED	= 1.0f;
    private static final float DISABLED	= 0.3f;

    private ItemSprite sprite = null;
    public static WarriorAbilityButton instance;

    @Override
    public GameAction keyAction() {
        return SPDAction.TAG_WARRIOR;
    }

    public WarriorAbilityButton() {
        super(Window.TITLE_COLOR);
        setSize(24, 24);
        visible( false );
        enable( false );
        instance = this;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

    @Override
    protected synchronized void layout() {
        super.layout();

        if (sprite != null){
            sprite.x = x + (width - sprite.width()) / 2;
            sprite.y = y + (height - sprite.height()) / 2;
            PixelScene.align(sprite);
            if (!members.contains(sprite))
                add(sprite);
        }
    }

    private boolean enabled = true;
    private synchronized void enable( boolean value ) {
        enabled = value;
        if (sprite != null) {
            sprite.alpha( value ? ENABLED : DISABLED );
        }
    }

    private synchronized void visible( boolean value ) {
        bg.visible = value;
        if (sprite != null) {
            sprite.visible = value;
        }
    }

    private boolean needsLayout = false;

    @Override
    public synchronized void update() {
        super.update();
        lightness = 0.6f;

        if (!Dungeon.hero.ready){
            if (sprite != null) sprite.alpha(0.5f);
        } else {
            if (sprite != null) sprite.alpha(1f);
        }
        Hunger hunger = Dungeon.hero.buff(Hunger.class);
        if (hunger == null) return;

        if (Dungeon.hero.heroClass == HeroClass.WARRIOR && enabled) {
            if (Dungeon.hero.belongings.weapon != null && (
                    (Dungeon.hero.subClass == HeroSubClass.GLADIATOR && Dungeon.hero.buff(Stacks.class) != null && Dungeon.hero.buff(Stacks.class).damage >= 9)
                    || (!hunger.isHungry() && ((Weapon)Dungeon.hero.belongings.weapon).seal != null) && Dungeon.hero.subClass != HeroSubClass.GLADIATOR)) {
                visible(true);
                enable(true);
                if (instance != null) {
                    synchronized (instance) {
                        if (instance.sprite != null) {
                            instance.sprite.killAndErase();
                            instance.sprite = null;
                        }
                        if (Dungeon.hero.belongings.weapon != null && ((Weapon)Dungeon.hero.belongings.weapon).seal != null) {
                            instance.sprite = new ItemSprite(Dungeon.hero.belongings.weapon.image, null);
                        }
                        instance.needsLayout = true;
                    }
                }
            } else {
                visible(false);
                enable(false);
            }
        }

        if (needsLayout){
            layout();
            needsLayout = false;
        }
    }

    @Override
    protected void onClick() {
        if (enabled) GameScene.selectCell(attack);
    }

    private CellSelector.Listener attack = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            final Char enemy = Actor.findChar( cell );
            if (enemy == null
                    || !Dungeon.level.heroFOV[cell]
                    || !Dungeon.hero.canAttack(enemy)
                    || Dungeon.hero.isCharmedBy( enemy )){
                GLog.warning( Messages.get(Combo.class, "bad_target") );
            } else {
                Dungeon.hero.sprite.attack(cell, new Callback() {
                    @Override
                    public void call() {
                        doAttack(enemy);
                    }
                });
            }
        }

        @Override
        public String prompt() {
            return Messages.get(WarriorAbilityButton.class, "prompt");
        }
    };

    public static void doAttack(final Char enemy){
        AttackIndicator.target(enemy);

        if (enemy.defenseSkill(Dungeon.hero) >= Char.INFINITE_EVASION){
            enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
            Sample.INSTANCE.play(Assets.Sounds.MISS);
            return;
        } else if (enemy.isInvulnerable(Dungeon.hero.getClass())){
            enemy.sprite.showStatus( CharSprite.POSITIVE, Messages.get(Char.class, "invulnerable") );
            Sample.INSTANCE.play(Assets.Sounds.MISS);
            return;
        }

        int hungerCost = -50;
        if (Dungeon.hero.subClass == HeroSubClass.BERSERKER && Dungeon.level.adjacent(Dungeon.hero.pos, enemy.pos)){
            hungerCost = -100;
        }

        if (Dungeon.hero.subClass == HeroSubClass.GLADIATOR){
            Dungeon.hero.buff(Stacks.class).damage -= 10;
            if (Dungeon.hero.buff(Stacks.class).damage <= 0) Dungeon.hero.buff(Stacks.class).detach();
        } else Hunger.adjustHunger(hungerCost);

        int dmg; float delay;
        if (Dungeon.hero.belongings.weapon == null){
            dmg = Dungeon.hero.damageRoll();
            delay = 1.5f;
        }
        else {
            dmg = ((MeleeWeapon)Dungeon.hero.belongings.weapon).warriorAttack(Dungeon.hero.damageRoll(), enemy);
            delay = ((MeleeWeapon)Dungeon.hero.belongings.weapon).warriorDelay(Dungeon.hero.belongings.weapon.speedFactor(Dungeon.hero), enemy);
        }
        dmg = enemy.defenseProc(Dungeon.hero, dmg);
        dmg -= enemy.drRoll();
        if ( enemy.buff( Vulnerable.class ) != null){
            dmg *= 1.33f;
        }

        dmg = Dungeon.hero.attackProc(enemy, dmg);
        enemy.damage( dmg, Dungeon.hero );
        if (Dungeon.hero.buff(FireImbue.class) != null)
            Dungeon.hero.buff(FireImbue.class).proc(enemy);
        if (Dungeon.hero.buff(EarthImbue.class) != null)
            Dungeon.hero.buff(EarthImbue.class).proc(enemy);
        if (Dungeon.hero.buff(FrostImbue.class) != null)
            Dungeon.hero.buff(FrostImbue.class).proc(enemy);
        Dungeon.hero.hitSound(Random.Float(0.87f, 1.15f));
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        enemy.sprite.bloodBurstA( Dungeon.hero.sprite.center(), dmg );
        enemy.sprite.flash();
        if (delay > 0) Dungeon.hero.spendAndNext(delay);
        if (!enemy.isAlive()){
            GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.getName())) );
        }
    }
}
