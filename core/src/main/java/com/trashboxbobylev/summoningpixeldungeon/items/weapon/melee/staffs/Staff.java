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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs;

import com.trashboxbobylev.summoningpixeldungeon.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.HeroClass;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ConjurerArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.Bag;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Staff extends Weapon {
    //Staffs is kind of weapons, that can summon minions and hold the attunement

    //type of minion, since we need to spawn them, not hold
    public Class<? extends Minion> minionType;
    public Minion.MinionClass minionClass = Minion.MinionClass.MELEE;

    //the property of tank minion, to prevent abuse of high health minions
    protected boolean isTanky = false;
    public int tier;
    public MinionBalanceTable table;

    public static final String AC_SUMMON = "SUMMON";
    public static final String AC_ZAP = "ZAP";

    public int curCharges = 1;
    public float partialCharge = 0f;

    private static final int USES_TO_ID = 10;
    private int usesLeftToID = USES_TO_ID;
    private float availableUsesToID = USES_TO_ID/2f;

    public Charger charger;

    private boolean curChargeKnown = false;

    //a lot of saving
    public static final String MAX_CHARGES = "max_charges";
    public static final String CUR_CHARGES = "cur_changes";
    public static final String PARTIAL_CHARGE = "partial_change";
    public static final String USES_LEFT_TO_DO = "uses_left_to_do";
    public static final String AVAILABLE_USES  = "available_uses";
    public static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";

    @Override
    public boolean collect( Bag container ) {
        if (super.collect( container )) {
            if (container.owner != null) {
                charge( container.owner );
                identify();
            }
            return true;
        } else {
            return false;
        }
    }

    public void charge(Char owner ) {
        if (charger == null) charger = new Charger();
        charger.attachTo( owner );
    }

    @Override
    public void onDetach( ) {
        stopCharging();
    }

    public void stopCharging() {
        if (charger != null) {
            charger.detach();
            charger = null;
        }
    }

    @Override
    public String status() {
        if (levelKnown) {
            if (curCharges == 0) return Messages.format( "%d%%", (int)(partialCharge * 100));
            else return "100%";
        } else {
            return null;
        }
    }

    @Override
    public int level() {
        if (!cursed && curseInfusionBonus){
            curseInfusionBonus = false;
            updateLevel();
        }
        return super.level() + (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public Item upgrade() {

        super.upgrade();

        updateLevel();
        curCharges = Math.min( curCharges + 1, 1);
        updateQuickslot();

        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();

        updateLevel();
        updateQuickslot();

        return this;
    }

    public void updateLevel() {
        curCharges = Math.min( curCharges, 1);
    }

    @Override
    public Item identify() {
        levelKnown = true;
        super.identify();

        updateQuickslot();

        return this;
    }

    {
        defaultAction = AC_SUMMON;
        usesTargeting = true;
    }

    //worse damage in melee
    //as example, Froggit Staff deals 1-4 damage and have +0.5/+1 scale
    @Override
    public int min(int lvl) {
        return tier +  lvl / 2;
    }

    public int STRReq(int lvl){
        lvl = Math.max(0, lvl);
        //strength req doesn't decreases
        return (7 + tier * 2);
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +  (lvl*(tier+1)) / 2;
    }

    public int hp(int lvl){
        return table.hp + table.hp_grow*lvl;
    }

    //but they have additional stat: minion damage
    //as same as melee damage of regular weapons


    public int minionmin() {
        return Math.round(minionMin(level())* RingOfAttunement.damageMultiplier(Dungeon.hero));
    }

    public int minionMin(int lvl) {
        return table.min + table.min_grow*lvl;
    }

    public int minionmax() {
        return Math.round(minionMax(level())* RingOfAttunement.damageMultiplier(Dungeon.hero));
    }

    public int minionMax(int lvl) {
        return table.max + table.max_grow*lvl;
    }

    @Override
    public boolean isUpgradable() {
        return level() < 3;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    //only for cursed behavour
    public int minionDamageRoll(Char owner) {
        return augment.damageFactor(Random.NormalIntRange(minionMin(level()), minionMax(level())));
    }

    public float requiredAttunement(){
        return 1;
    }

    //you can use staff only while equipped
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (curCharges > 0) {
            actions.add( AC_SUMMON );
        }
        actions.add( AC_ZAP );
        actions.remove( AC_EQUIP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON) && !(this instanceof StationaryStaff)) {

            curUser = hero;
            try {
                if (curCharges > 0) summon(hero);
                else GLog.warning( Messages.get(Wand.class, "fizzles") );
            } catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
                GLog.warning( Messages.get(Wand.class, "fizzles") );
            }

        } else if (action.equals(AC_ZAP)){
            curUser = hero;
            curItem = this;
            GameScene.selectCell( zapper );
        }
    }

    public boolean tryToZap( Hero owner, int target ){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(Wand.class, "no_magic") );
            return false;
        }

        if ( curCharges >= 0){
            return true;
        } else {
            GLog.warning(Messages.get(Wand.class, "fizzles"));
            return false;
        }
    }

    protected void wandUsed(boolean isZap) {
        if (!isIdentified() && availableUsesToID >= 1) {

                identify();
                Badges.validateItemLevelAquired( this );
        }

        if (!isZap) curCharges -= 1;

        updateQuickslot();

        curUser.spendAndNext( speedFactor(curUser) );
    }

    //summoning logic
    public void summon(Hero owner) throws InstantiationException, IllegalAccessException {

        //searching for available space
        ArrayList<Integer> spawnPoints = new ArrayList<Integer>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = owner.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                spawnPoints.add( p );
            }
        }

        if (spawnPoints.size() == 0){
            owner.sprite.zap(0);
            GLog.i( Messages.get(Staff.class, "no_space") );
            return;
        }

        //checking attunement
        if (requiredAttunement() > owner.attunement() || (requiredAttunement() + owner.usedAttunement > owner.attunement())){
            owner.sprite.zap(0);
            GLog.warning( Messages.get(Staff.class, "too_low_attunement") );
            return;
        }

        //checking existence of tank minions
        for (Char ch: Dungeon.level.mobs) {
            if (ch instanceof Minion && isTanky && ((Minion) ch).isTanky){
                owner.sprite.zap(0);
                GLog.warning( Messages.get(Staff.class, "cant_summon_tank") );
                return;
            }
        }

        int strength = 1;
        if (STRReq() > owner.STR())  strength += STRReq(level()) - owner.STR();
        if (cursed) strength *= 4;
        if (strength > 1) cursedKnown = true;

        //if anything is met, spawn minion
        //if hero do not have enough strength, summoning might fail
        if (strength == 1 || Random.Float() < 1 / (float) (strength * 2)) {
            Minion minion = minionType.newInstance();
            GameScene.add(minion);
            ScrollOfTeleportation.appear(minion, spawnPoints.get(Random.index(spawnPoints)));
            owner.usedAttunement += requiredAttunement();
            minion.setDamage(
                    minionmin(),
                    minionmax());
            Statistics.summonedMinions++;
            Badges.validateConjurerUnlock();
            minion.strength = STRReq();
            this.customizeMinion(minion);
            minion.enchantment = enchantment;
            minion.lvl = level();
            minion.minionClass = minionClass;

            //if we have upgraded robe, increase hp
            float robeBonus = 1f;
            if (curUser.belongings.armor instanceof ConjurerArmor && curUser.belongings.armor.level() > 0 && !(this instanceof ChickenStaff)) {
                robeBonus = 1f + curUser.belongings.armor.level() * 0.1f;
            }
            minion.setMaxHP((int) (hp(level()) * robeBonus));
        } else GLog.warning( Messages.get(Wand.class, "fizzles") );
        wandUsed(false);
    }

    public void customizeMinion(Minion minion){ }

    protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                final Staff staff = (Staff)curItem;

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

                //okay, this is incredible mess
                //basically it's copy-paste from various wand classes
                //ZAP from summon staff doesn't do damage and serves only as targetting tool for your minions
                if (staff.tryToZap(curUser, target)){
                    curUser.busy();
                    Invisibility.dispel();

                    if (curItem.cursed){
                        GLog.negative(Messages.get(Staff.class, "curse_discover", staff.name()));
                        curUser.damage(staff.minionDamageRoll(curUser), staff);
                        curUser.spendAndNext(1f);
                    } else {
                        curUser.sprite.parent.add(
                                new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(shot.collisionPos)));
                        Sample.INSTANCE.play( Assets.SND_ZAP );
                        Char ch = Actor.findChar(shot.collisionPos);
                        if (ch != null){
                            if (ch instanceof Minion){
                                ch.die( curUser );
                            }
                            else ch.damage(0, this);
                        }
                        staff.wandUsed(true);
                    }
                    curItem.cursedKnown = true;
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };

    public int chargeTurns = 400;

    public class Charger extends Buff {

        private static final float CHARGE_BUFF_BONUS = 0.25f;

        @Override
        public boolean attachTo( Char target ) {
            super.attachTo( target );

            return true;
        }

        @Override
        public boolean act() {
            if (curCharges < 1)
                recharge();

            while (partialCharge >= 1 && curCharges < 1) {
                partialCharge--;
                curCharges++;
            }

            if (curCharges == 1){
                partialCharge = 0;
            }

            spend( TICK );

            return true;
        }

        private void recharge(){

            LockedFloor lock = target.buff(LockedFloor.class);
            if (lock == null || lock.regenOn())
                partialCharge += (1f / chargeTurns) * (Dungeon.hero.heroClass == HeroClass.CONJURER ? 1 : 0.8);
            updateQuickslot();

            for (Recharging bonus : target.buffs(Recharging.class)){
                if (bonus != null && bonus.remainder() > 0f) {
                    partialCharge += CHARGE_BUFF_BONUS * bonus.remainder();
                }
            }
        }

        public Staff staff(){
            return Staff.this;
        }

        public void gainCharge(float charge) {
            partialCharge += charge;
            while (partialCharge >= 1f) {
                curCharges++;
                partialCharge--;
            }
            curCharges = Math.min(curCharges, 1);
            updateQuickslot();
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_CHARGES, 1);
        bundle.put(CUR_CHARGES, curCharges);
        bundle.put(PARTIAL_CHARGE, partialCharge);
        bundle.put( USES_LEFT_TO_DO, usesLeftToID );
        bundle.put( AVAILABLE_USES, availableUsesToID );
        bundle.put(CURSE_INFUSION_BONUS, curseInfusionBonus );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        usesLeftToID = bundle.getInt( USES_LEFT_TO_DO );
        availableUsesToID = bundle.getInt( AVAILABLE_USES );

        curCharges = bundle.getInt( CUR_CHARGES );
        partialCharge = bundle.getFloat( PARTIAL_CHARGE );
        curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
    }

    @Override
    public String info() {
        String info = desc();

        if (isIdentified()) {
            float robeBonus = 1f;
            if (Dungeon.hero.belongings.armor instanceof ConjurerArmor) {
                if (Dungeon.hero.belongings.armor.level() > 0 && !(this instanceof ChickenStaff)) {
                    robeBonus = 1f + Dungeon.hero.belongings.armor.level() * 0.1f;
                }
            }
            info += "\n\n" + Messages.get(Staff.class, "stats_known", tier,
                    augment.damageFactor(min()),
                    augment.damageFactor(max()),
                    STRReq(),
                    minionmin(),
                    minionmax(),
                    (int)(hp(level()) * robeBonus));
            if (STRReq() > Dungeon.hero.STR()) {
                info += " " + Messages.get(MeleeWeapon.class, "too_heavy");
            } else if (Dungeon.hero.STR() > STRReq()){
                info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
            }
        } else {
            info += "\n\n" + Messages.get(Staff.class, "stats_unknown", tier, min(0), max(0), STRReq(0), minionMin(0), minionMax(0), hp(0));
            if (STRReq(0) > Dungeon.hero.STR()) {
                info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
            }
        }

        String statsInfo = Messages.get(this, "stats_desc");
        if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

        if (level() >= 3) info += "\n\n" + Messages.get(Staff.class, "upgrade_info");

        info += "\n\n" + Messages.get(Staff.class, "class", minionClass);

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

        return info;
    }

    @Override
    public int price() {
        int price = 30 * tier;
        if (hasGoodEnchant()) {
            price *= 1.5;
        }
        if (cursedKnown && (cursed || hasCurseEnchant())) {
            price /= 2;
        }
        if (levelKnown && level() > 0) {
            price *= (level() + 1);
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }
}
