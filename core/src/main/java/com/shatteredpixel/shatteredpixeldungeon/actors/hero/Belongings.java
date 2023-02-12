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
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

public class Belongings implements Iterable<Item> {

	private Hero owner;
	
	public Bag backpack;

	public KindOfWeapon weapon = null;
	public Armor armor = null;

	public ArrayList<Artifact> accs = new ArrayList<>(Arrays.asList(null, null, null));

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

	private static final String ACCESSORY = "accessories";

	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );
		
		bundle.put( WEAPON, weapon );
		bundle.put( ARMOR, armor );
//		bundle.put( OFFENSE_ACCESSORY, offenseAcc );
//		bundle.put( DEFENSE_ACCESSORY, defenseAcc );
//		bundle.put( UTILITY_ACCESSORY, utilityAcc );
		bundle.put(ACCESSORY, accs);
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

				if (artifact != null) {

					if (artifact.activeBuff != null) {
						artifact.activeBuff.detach();
						artifact.activeBuff = null;
					}

					if (artifact.passiveBuff != null) {
						artifact.passiveBuff.detach();
						artifact.passiveBuff = null;
					}

					if (!artifact.collect(backpack)) {
						Dungeon.quickslot.clearItem(artifact);
						updateQuickslot();
						Dungeon.level.drop(artifact, owner.pos);
					}
				}

			}
		} else {
			Collection<Bundlable> accessories = bundle.getCollection(ACCESSORY);
			Bundlable[] accList = accessories.toArray(new Bundlable[0]);
			for (int index = 0; index < accList.length; index++){
				accs.set(index, (Artifact) accList[index]);
			}
		}
		for (Artifact acc: accs){
			if (acc != null)
				acc.activate(owner);
		}

		if (owner.armorAbility != null)
			owner.armorAbility.activate(owner);
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
		for (Artifact acc: accs){
			acc.identify();
			Badges.validateItemLevelAquired(acc);
		}
		for (Item item : backpack) {
			if (item instanceof EquipableItem || item instanceof Wand) {
				item.cursedKnown = true;
			}
		}
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner, armor, weapon);
		for (Artifact acc: accs){
			ScrollOfRemoveCurse.uncurse(owner, acc);
		}
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

		for (Artifact acc: accs){
			if (acc != null) {
				acc.cursed = false;
				acc.activate(owner);
			}
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
		private int accIndex = 0;
		
		private Iterator<Item> backpackIterator = backpack.iterator();
		
		private Item[] equipped = {weapon, armor};
		private int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}

			if (accs.size() > 0) {
				for (int i=accIndex; i < accs.size(); i++) {
					if (accs.get(accIndex) != null) {
						return true;
					}
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

			if (accs.size() > 0) {
				while (accIndex < accs.size()) {
					Item item = accs.get(accIndex++);
					if (item != null) {
						return item;
					}
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
			default:
				switch (accIndex){
					case 0:
						accs.set(0, null);
						break;
					case 1:
						accs.set(1, null);
						break;
					case 2:
						accs.set(2, null);
						break;
				}
				backpackIterator.remove();
			}
		}
	}
}
