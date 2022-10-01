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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Chicken;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Tierable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.journal.Document.GUIDE_SUMMONING;

public class Staff extends Weapon implements Tierable {
    //Staffs is kind of weapons, that can summon minions and hold the attunement

    //type of minion, since we need to spawn them, not hold
    public Class<? extends Minion> minionType;

    public Minion.MinionClass minionClass = Minion.MinionClass.MELEE;

    //the property of tank minion, to prevent abuse of high health minions
    protected boolean isTanky = false;
    public int tier;
    public MinionBalanceTable table;

    public static final String AC_SUMMON = "SUMMON";
    public static final String AC_DOWNGRADE = "DOWNGRADE";
    public static final String AC_TIERINFO = "TIERINFO";

    public int curCharges = 1;
    public float partialCharge = 0f;

    private static final int USES_TO_ID = 10;
    private int usesLeftToID = USES_TO_ID;
    private float availableUsesToID = USES_TO_ID/2f;

    public Charger charger;

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
            return "";
        }
    }

    @Override
    public int level() {
        updateLevel();
        return super.level();
    }

    @Override
    public Item upgrade() {
        if (isUpgradable())
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
        super.identify();

        updateQuickslot();

        return this;
    }

    {
        defaultAction = AC_SUMMON;
    }

    @Override
    public int min(int lvl) {
        return tier +  lvl / 2;
    }

    public int STRReq(int lvl){
        return (8 + tier * 2);
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +  (lvl*(tier+1)) / 2;
    }

    public int hp(int lvl){
        switch (lvl) {
            case 0: return table.hp1;
            case 1: return table.hp2;
            case 2: return table.hp3;
        }
        return 0;
    }

    //but they have additional stat: minion damage
    //as same as melee damage of regular weapons


    public int minionmin() {
        return Math.round(minionMin(level())* RingOfAttunement.damageMultiplier(Dungeon.hero));
    }

    public int minionMin(int lvl) {
        int dmg = 0;
        switch (lvl) {
            case 0: dmg = table.min1; break;
            case 1: dmg = table.min2; break;
            case 2: dmg = table.min3; break;
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
        return dmg;
    }

    public int minionmax() {
        return Math.round(minionMax(level())* RingOfAttunement.damageMultiplier(Dungeon.hero));
    }

    public int minionMax(int lvl) {
        int dmg = 0;
        switch (lvl) {
            case 0: dmg = table.max1; break;
            case 1: dmg = table.max2; break;
            case 2: dmg = table.max3; break;
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
        return dmg;
    }

    @Override
    public boolean isUpgradable() {
        return level() < 2;
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }

    public float requiredAttunement(){
        return requiredAttunement(level());
    }

    public float requiredAttunement(int lvl){
        switch (lvl) {
            case 0: return table.att1;
            case 1: return table.att2;
            case 2: return table.att3;
        }
        return 0;
    }

    @Override
    public String getTierMessage(int tier){
        return Messages.get(this, "tier" + tier,
                hp(tier-1),
                minionMin(tier-1),
                minionMax(tier-1),
                new DecimalFormat("#.##").format(requiredAttunement(tier-1))
        );
    }

    public void setClass(Minion.MinionClass minionClass){
        this.minionClass = minionClass;
        switch (this.minionClass){
            case DEFENSE:
                icon = ItemSpriteSheet.Icons.CLASS_DEFENSE;break;
            case MELEE:
                icon = ItemSpriteSheet.Icons.CLASS_MELEE;break;
            case MAGIC:
                icon = ItemSpriteSheet.Icons.CLASS_MAGIC;break;
            case RANGE:
                icon = ItemSpriteSheet.Icons.CLASS_RANGED;break;
            case SUPPORT:
                icon = ItemSpriteSheet.Icons.CLASS_SUPPORT;break;
        }
    }

    //you can use staff only while equipped
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (curCharges > 0) {
            actions.add( AC_SUMMON );
        }
        actions.remove( AC_EQUIP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null || Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
            GLog.warning( Messages.get(Wand.class, "no_magic") );
            return;
        }

        if (action.equals(AC_SUMMON) && !(this instanceof StationaryStaff)) {

            curUser = hero;
            try {
                if (curCharges > 0) summon(hero);
                else GLog.warning( Messages.get(Wand.class, "fizzles") );
            } catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
                GLog.warning( Messages.get(Wand.class, "fizzles") );
            }

        }
    }

    @Override
    public Item random() {
        //I: 75% (3/4)
        //II: 25% (1/4)
        int n = 0;
        if (Random.Int(4) == 0) {
            n++;
        }
        level(n);

        //30% chance to be cursed
        //25% chance to be enchanted
        float effectRoll = Random.Float();
        if (effectRoll < 0.3f) {
            enchant(Enchantment.randomCurse());
            cursed = true;
        } else if (effectRoll >= 0.75f){
            enchant();
        }

        return this;
    }

    public boolean tryToZap(Hero owner, int target ){

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
            if (Statistics.summonedMinions != 0 && !Document.ADVENTURERS_GUIDE.isPageRead("Summoning")){
                GLog.positive(Messages.get(Guidebook.class, "hint"));
                GameScene.flashForDocument(GUIDE_SUMMONING);
            }
            minion.strength = STRReq();
            this.customizeMinion(minion);
            minion.enchantment = enchantment;
            minion.lvl = level();
            minion.minionClass = minionClass;
            minion.attunement = requiredAttunement();

            //if we have upgraded robe, increase hp
            float robeBonus = 1f;
            if (curUser.belongings.armor instanceof ConjurerArmor && curUser.belongings.armor.level() > 0 && !(this instanceof ChickenStaff)) {
                robeBonus = 1f + curUser.belongings.armor.level() * 0.1f;
            }
            minion.setMaxHP((int) (hp(level()) * robeBonus));
        } else GLog.warning( Messages.get(Staff.class, "fizzles") );
        wandUsed(false);
    }

    public void customizeMinion(Minion minion){ }

    protected int chargeTurns = 400;

    public int getChargeTurns() {
        return chargeTurns;
    }

    public class Charger extends Buff {

        private static final float CHARGE_BUFF_BONUS = 0.1f;

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

            float mod = 1f;

            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.getClass() == minionType && !(mob instanceof Chicken)) {
                    mod = 0.33f;
                }
            }

            LockedFloor lock = target.buff(LockedFloor.class);
            if (lock == null || lock.regenOn())
                partialCharge += (1f / getChargeTurns()) * (Dungeon.hero.heroClass == HeroClass.CONJURER ? 1 : 0.9)*mod;
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
            if (Dungeon.hero.buff(Attunement.class) != null) robeBonus = Attunement.empowering();
            info += "\n\n" + Messages.get(Staff.class, "stats_known",
                    tier,
                    STRReq(),
                    Math.round(minionmin()*robeBonus),
                    Math.round(minionmax()*robeBonus),
                    (hp(level())),
                    requiredAttunement());
            if (STRReq() > Dungeon.hero.STR()) {
                info += " " + Messages.get(Staff.class, "too_heavy_uh");
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
    public String toString() {

        String name = name();
        String tier = "";
        switch (level()){
            case 0: tier = "I"; break;
            case 1: tier = "II"; break;
            case 2: tier = "III"; break;
        }

        name = Messages.format( "%s %s", name, tier  );

        return name;

    }

    @Override
    public int value() {
        int price = 30 * tier;
        if (hasGoodEnchant()) {
            price *= 1.5;
        }
        if (cursedKnown && (cursed || hasCurseEnchant())) {
            price /= 2;
        }
        if (levelKnown && level() > 0) {
            price *= Math.pow(2, level());
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }
}
