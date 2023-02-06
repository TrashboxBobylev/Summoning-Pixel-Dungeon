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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.Staff;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Iterator;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

public class Belongings implements Iterable<Item> {

	private Hero owner;
	
	public Bag backpack;

	public KindOfWeapon weapon = null;
	public Armor armor = null;

	public Artifact offenseAcc = null;
	public Artifact defenseAcc = null;
	public Artifact utilityAcc = null;

	//used when thrown weapons temporary occupy the weapon slot
	public KindOfWeapon stashedWeapon = null;

	public static class Backpack extends Bag {
		public int capacity(){
			int cap = super.capacity();
			for (Item item : items){
				if (item instanceof Bag){
					cap++;
				}
			}
			return cap;
		}
	}
	
	public Belongings( Hero owner ) {
		this.owner = owner;
		
		backpack = new Backpack();
		backpack.owner = owner;
	}
	
	private static final String WEAPON		= "weapon";
	private static final String ARMOR		= "armor";
	private static final String ARTIFACT   = "artifact";
	private static final String MISC       = "misc";
	private static final String RING       = "ring";

	private static final String OFFENSE_ACCESSORY = "offense_acc";
	private static final String DEFENSE_ACCESSORY = "defense_acc";
	private static final String UTILITY_ACCESSORY = "utility_acc";

	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );
		
		bundle.put( WEAPON, weapon );
		bundle.put( ARMOR, armor );
		bundle.put( OFFENSE_ACCESSORY, offenseAcc );
		bundle.put( DEFENSE_ACCESSORY, defenseAcc );
		bundle.put( UTILITY_ACCESSORY, utilityAcc );
	}
	
	public void restoreFromBundle( Bundle bundle ) {
		
		backpack.clear();
		backpack.restoreFromBundle( bundle );
		
		weapon = (KindOfWeapon) bundle.get(WEAPON);
		if (weapon instanceof MagesStaff){
			weapon = ((MagesStaff) weapon).wand;
		}
		if (weapon != null) {
			weapon.activate(owner);
		}
		
		armor = (Armor)bundle.get( ARMOR );
		if (armor != null){
			armor.activate( owner );
		}

		if (bundle.contains(ARTIFACT) || bundle.contains(MISC) || bundle.contains(RING)){
			String[] slotNames = {ARTIFACT, MISC, RING};
			for (String slot : slotNames){
				Artifact artifact = (Artifact) bundle.get(slot);

				if (artifact.activeBuff != null){
					artifact.activeBuff.detach();
					artifact.activeBuff = null;
				}

				artifact.passiveBuff.detach();
				artifact.passiveBuff = null;

				if (!artifact.collect(backpack)){
					Dungeon.quickslot.clearItem(artifact);
					updateQuickslot();
					Dungeon.level.drop( artifact, owner.pos );
				}

			}
		} else {
			offenseAcc = (Artifact) bundle.get(OFFENSE_ACCESSORY);
			defenseAcc = (Artifact) bundle.get(DEFENSE_ACCESSORY);
			utilityAcc = (Artifact) bundle.get(UTILITY_ACCESSORY);
		}

		if (offenseAcc != null) offenseAcc.activate(owner);
		if (defenseAcc != null) defenseAcc.activate(owner);
		if (utilityAcc != null) utilityAcc.activate(owner);
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		if (bundle.contains( ARMOR )){
			info.armorTier = ((Armor)bundle.get( ARMOR )).tier;
		} else {
			info.armorTier = 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Item> T getItem( Class<T> itemClass ) {

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				return (T)item;
			}
		}
		
		return null;
	}
	
	public boolean contains( Item contains ){
		
		for (Item item : this) {
			if (contains == item ) {
				return true;
			}
		}
		
		return false;
	}
	
	public Item getSimilar( Item similar ){
		
		for (Item item : this) {
			if (similar != item && similar.isSimilar(item)) {
				return item;
			}
		}
		
		return null;
	}
	
	public ArrayList<Item> getAllSimilar( Item similar ){
		ArrayList<Item> result = new ArrayList<>();
		
		for (Item item : this) {
			if (item != similar && similar.isSimilar(item)) {
				result.add(item);
			}
		}
		
		return result;
	}

	public<T extends Item> ArrayList<T> getAllItems( Class<T> itemClass ) {
		ArrayList<T> result = new ArrayList<>();

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				result.add((T) item);
			}
		}

		return result;
	}
	
	public void identify() {
		for (Item item : this) {
			item.identify();
		}
	}
	
	public void observe() {
		if (weapon != null) {
			weapon.identify();
			Badges.validateItemLevelAquired( weapon );
		}
		if (armor != null) {
			armor.identify();
			Badges.validateItemLevelAquired( armor );
		}
		if (offenseAcc != null) {
			offenseAcc.identify();
			Badges.validateItemLevelAquired(offenseAcc);
		}
		if (defenseAcc != null) {
			defenseAcc.identify();
			Badges.validateItemLevelAquired(defenseAcc);
		}
		if (utilityAcc != null) {
			utilityAcc.identify();
			Badges.validateItemLevelAquired(utilityAcc);
		}
		for (Item item : backpack) {
			if (item instanceof EquipableItem || item instanceof Wand) {
				item.cursedKnown = true;
			}
		}
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner, armor, weapon, offenseAcc, defenseAcc, utilityAcc);
	}
	
	public Item randomUnequipped() {
		return Random.element( backpack.items );
	}
	
	public void resurrect( int depth ) {

		for (Item item : backpack.items.toArray( new Item[0])) {
			if (item instanceof Key) {
				if (((Key)item).depth == depth) {
					item.detachAll( backpack );
				}
			} else if (item.unique) {
				item.detachAll(backpack);
				//you keep the bag itself, not its contents.
				if (item instanceof Bag){
					((Bag)item).resurrect();
				}
				item.collect();
			} else if (!item.isEquipped( owner )) {
				item.detachAll( backpack );
			}
		}
		
		if (weapon != null) {
			weapon.cursed = false;
			weapon.activate( owner );
		}
		
		if (armor != null) {
			armor.cursed = false;
			armor.activate( owner );
		}

		if (offenseAcc != null) {
			offenseAcc.cursed = false;
			offenseAcc.activate( owner );
		}
		if (defenseAcc != null) {
			defenseAcc.cursed = false;
			defenseAcc.activate( owner );
		}
		if (utilityAcc != null) {
			utilityAcc.cursed = false;
			utilityAcc.activate( owner );
		}
	}
	
	public int charge( float charge ) {
		
		int count = 0;
		
		for (Wand.Charger charger : owner.buffs(Wand.Charger.class)){
			charger.gainCharge(charge);
			count++;
		}

        for (Staff.Charger charger : owner.buffs(Staff.Charger.class)){
            charger.gainCharge(charge);
            count++;
        }
		
		return count;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		
		private Iterator<Item> backpackIterator = backpack.iterator();
		
		private Item[] equipped = {weapon, armor, offenseAcc, defenseAcc, utilityAcc};
		private int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}
			
			return backpackIterator.hasNext();
		}

		@Override
		public Item next() {
			
			while (index < backpackIndex) {
				Item item = equipped[index++];
				if (item != null) {
					return item;
				}
			}
			
			return backpackIterator.next();
		}

		@Override
		public void remove() {
			switch (index) {
			case 0:
				equipped[0] = weapon = null;
				break;
			case 1:
				equipped[1] = armor = null;
				break;
			case 2:
				equipped[2] = offenseAcc = null;
				break;
			case 3:
				equipped[3] = defenseAcc = null;
				break;
			case 4:
				equipped[4] = utilityAcc = null;
				break;
			default:
				backpackIterator.remove();
			}
		}
	}
}
