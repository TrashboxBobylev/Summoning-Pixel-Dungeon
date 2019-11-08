/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.items.armor;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroClass;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroSubClass;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs.ChaosSaber;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ElmoParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.Artifact;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.LoveHolder;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfCorruption;
import com.trashboxbobylev.summoningpixeldungeon.levels.Level;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.HeroSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ConjurerArmor extends ClassArmor {
	
	{
		image = ItemSpriteSheet.ARMOR_CONJURER;
		defaultAction = AC_OFFENSE;
	}

    private static final String AC_IMBUE = "IMBUE";
    private static final String AC_CHAOS = "CHAOS";
    private static final String AC_OFFENSE = "OFFENSE";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (!cursed) actions.add( AC_IMBUE );
        actions.remove(AC_UNEQUIP);
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        if (hero.subClass != HeroSubClass.NONE) {
            actions.add(AC_CHAOS);
            actions.add(AC_OFFENSE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_IMBUE)) {
            curUser = hero;
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR_FOR_IMBUE, Messages.get(this, "prompt"));
        } else if (action.equals(AC_CHAOS)) {
            if (hero.HP < 3 ||
                    (hero.heroClass == HeroClass.CONJURER && hero.HP < (hero.HT / 3))) {
                GLog.warning(Messages.get(this, "low_hp"));
            } else if (!isEquipped(hero)) {
                GLog.warning(Messages.get(this, "not_equipped"));
            } else {
                curUser = hero;
                Invisibility.dispel();
                ArrayList<Integer> spawnPoints = Level.getSpawningPoints(hero.pos);
                if (spawnPoints.size() > 0){
                    for (int i = 0; i < 2; i++) {
                        int index = Random.index( spawnPoints );

                        ChaosSaber mob = new ChaosSaber();
                        mob.duplicate( hero.HT / 6 );
                        GameScene.add( mob );
                        ScrollOfTeleportation.appear( mob, spawnPoints.get( index ) );

                        spawnPoints.remove( index );
                    }
                    curUser.HP -= (curUser.HT / 3);
                    curUser.spendAndNext( Actor.TICK );
                }
            }
        } else if (action.equals(AC_OFFENSE)){
            if (hero.subClass != HeroSubClass.NONE) {
                curUser = hero;
                GameScene.selectCell(zapper);
            } else {
                Messages.get(this, "no_mastery");
            }
        }
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null && item.isIdentified()) {
                upgrade( (Armor)item );
            }
        }
    };

    private void upgrade(Armor armor){
        GLog.warning( Messages.get(ConjurerArmor.class, "upgraded", armor.name()) );

        //syncs the level of the two items.
        int targetLevel = Math.max(this.level() - (curseInfusionBonus ? 1 : 0), armor.level());

        //if the robe's level is being overridden by the armor, preserve 1 upgrade
        if (armor.level() >= this.level() && this.level() > (curseInfusionBonus ? 1 : 0)) targetLevel++;

        level(targetLevel);
        updateQuickslot();
        this.armorTier = armor.tier;
        if (this.glyph == null) this.inscribe( armor.glyph );
        this.cursed = armor.cursed;
        this.curseInfusionBonus = armor.curseInfusionBonus;
        curUser.belongings.armor = this;
        ((HeroSprite)curUser.sprite).updateArmor();
        this.activate(curUser);
        armor.detach( curUser.belongings.backpack );
        curUser.sprite.operate( curUser.pos );
        curUser.spend( 2f );
        curUser.busy();
        GameScene.flash(0xFFFFFF);
        Sample.INSTANCE.play( Assets.SND_EVOKE );
    }

    @Override
    public int level() {
        return (int) (super.level() + Dungeon.hero.attunement() - 1);
    }

    @Override
	public void doSpecial() {
		
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]
				&& mob.alignment != Char.Alignment.ALLY) {
				Buff.prolong( mob, SoulParalysis.class, 7 );
			}
		}

		curUser.HP -= (curUser.HT / 8) + (curUser.HP / 4);
		
		curUser.spend( Actor.TICK );
		curUser.sprite.operate( curUser.pos );
		curUser.busy();
		
		curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
		Sample.INSTANCE.play( Assets.SND_LULLABY );
	}

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);

        //strength req decreases at +1,+3,+6,+10,etc.
        return (7 + Math.round(armorTier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
    }

    @Override
    public int DRMax(int lvl){
        //only 80% as effective
        int max = (int) ((armorTier * (2 + lvl) + augment.defenseFactor(lvl))*0.8f);
        if (lvl > max){
            return ((lvl - max)+1)/2;
        } else {
            return max;
        }
    }

    //offensive option
    protected CellSelector.Listener zapper = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null){
                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.MAGIC_BOLT);
                int cell = shot.collisionPos;

                if (target == curUser.pos || cell == curUser.pos) {
                    GLog.i( Messages.get(Wand.class, "self_target") );
                    return;
                }

                curUser.sprite.zap(cell);

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                LoveHolder artifact = null;
                if (curUser.belongings.misc1 instanceof LoveHolder) artifact = (LoveHolder) curUser.belongings.misc1;
                else if (curUser.belongings.misc2 instanceof LoveHolder) artifact = (LoveHolder) curUser.belongings.misc2;

                if (artifact == null){
                    GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                    return;
                } else {
                    final int str = artifact.str;
                    if (artifact.charge >= str || curUser.subClass == HeroSubClass.OCCULTIST){
                        if (curUser.subClass == HeroSubClass.SOUL_REAVER) artifact.charge -= str*2;
                        updateQuickslot();
                        curUser.busy();
                        Invisibility.dispel();
                        MagicMissile.boltFromChar(curUser.sprite.parent,
                                MagicMissile.BEACON,
                                curUser.sprite,
                                shot.collisionPos,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        Char ch = Actor.findChar(shot.collisionPos);

                                        if (ch != null){
                                            switch (curUser.subClass){
                                                case SOUL_REAVER:
                                                    doAsSoulReaver(ch, str, curUser);
                                                case OCCULTIST:
                                                    doAsOccultist(ch, str, curUser);
                                            }
                                        }
                                        curUser.spendAndNext(Actor.TICK);
                                    }
                                });
                    } else {
                        GLog.i(Messages.get(LoveHolder.class, "not_enough"));
                    }
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };

    public void doAsSoulReaver(Char target, int strength, Hero owner){
        //every charge adds +10% damage, initial is 0-hero level
        int damageRoll = Random.NormalIntRange(0, (int) (owner.lvl*Math.pow(1.25f, strength)));
        target.damage(damageRoll, this);

        target.sprite.burst(0xFFFFFFFF, strength*2);
    }

    public void doAsOccultist(Char target, int strength, Hero owner){
        HateOccult hateHolder = owner.buff(HateOccult.class);
        if (hateHolder != null) {
            //every charge consumes 2 hate and adds corruption power
            float corruptingPower = GameMath.gate(hateHolder.power, strength * 2f, 20f);
            if (strength == 0) corruptingPower = hateHolder.power;
            float enemyResist = WandOfCorruption.getEnemyResist(target, (Mob) target);

            //100% health: 3x resist   75%: 2.1x resist   50%: 1.5x resist   25%: 1.1x resist
            enemyResist *= 1 + 2 * Math.pow(target.HP / (float) target.HT, 2);

            hateHolder.power -= strength*2f;
            if (hateHolder.power <= 0f){
                hateHolder.detach();
            }
            if (strength > 0) owner.sprite.showStatus(CharSprite.DEFAULT, "-%s HATE", strength);
            BuffIndicator.refreshHero();

            if (corruptingPower > enemyResist) {
                WandOfCorruption.corruptEnemy(new WandOfCorruption(), (Mob) target);
                Sample.INSTANCE.play(Assets.SND_CURSED);
                //recover some hate
                hateHolder.gainHate((corruptingPower - enemyResist)*0.5f);
                BuffIndicator.refreshHero();
            }
        }
    }

}