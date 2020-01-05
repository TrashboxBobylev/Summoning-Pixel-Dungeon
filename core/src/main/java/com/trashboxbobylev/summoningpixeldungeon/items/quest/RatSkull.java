/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.trashboxbobylev.summoningpixeldungeon.items.quest;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.items.EquipableItem;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.KindofMisc;
import com.trashboxbobylev.summoningpixeldungeon.items.Recipe;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.Artifact;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs.Staff;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

//huh?
public class RatSkull extends Artifact {
	
	{
		image = ItemSpriteSheet.SKULL;

		charge = 0;
		unique = true;
		chargeCap = 100;
	}

	public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && charge == 100 && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (action.equals(AC_USE)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell( zapper );
        }
    }
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

    @Override
    protected ArtifactBuff passiveBuff() {
        return new skullRecharge();
    }

    public class skullRecharge extends ArtifactBuff{
        @Override
        public boolean act() {
            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (50f - (chargeCap - charge)*5f);

                if (partialCharge >= 1) {
                    partialCharge --;
                    charge ++;

                    if (charge == chargeCap){
                        partialCharge = 0;
                    }
                }
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }
    }

    protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

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

                    curUser.busy();
                    Invisibility.dispel();

                        curUser.sprite.parent.add(
                                new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(shot.collisionPos)));
                        Sample.INSTANCE.play( Assets.SND_ZAP );
                        Char ch = Actor.findChar(shot.collisionPos);
                        if (ch != null){
                            ch.damage(Random.NormalIntRange(Dungeon.depth / 3, Math.round(Dungeon.depth * 1.5f)), this);
                            if (!ch.isAlive() && ch.alignment == Char.Alignment.ENEMY){
                                Buff.affect(curUser, Recharging.class, ((Mob)ch).EXP*2);
                                Buff.affect(curUser, Bless.class, 3f);
                                ((RatSkull)curItem).charge = 0;
                                updateQuickslot();
                            }
                        }
                    }
                    curItem.cursedKnown = true;
                }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };
}
