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

package com.trashboxbobylev.summoningpixeldungeon.levels.rooms.special;

import com.trashboxbobylev.summoningpixeldungeon.Challenges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Belongings;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.trashboxbobylev.summoningpixeldungeon.items.Ankh;
import com.trashboxbobylev.summoningpixeldungeon.items.Generator;
import com.trashboxbobylev.summoningpixeldungeon.items.Heap;
import com.trashboxbobylev.summoningpixeldungeon.items.Honeypot;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.MerchantsBeacon;
import com.trashboxbobylev.summoningpixeldungeon.items.Stylus;
import com.trashboxbobylev.summoningpixeldungeon.items.Torch;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.*;
import com.trashboxbobylev.summoningpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.Bag;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.MagicalHolster;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.PotionBandolier;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.ScrollHolder;
import com.trashboxbobylev.summoningpixeldungeon.items.bags.VelvetPouch;
import com.trashboxbobylev.summoningpixeldungeon.items.bombs.Bomb;
import com.trashboxbobylev.summoningpixeldungeon.items.food.SmallRation;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.Potion;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfExperience;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.Scroll;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.trashboxbobylev.summoningpixeldungeon.items.spells.BeaconOfReturning;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.Runestone;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfAugmentation;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.StoneOfEnchantment;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.WandOfMagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.*;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs.Staff;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Bolas;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.FishingSpear;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Javelin;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Shuriken;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Tomahawk;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.Trident;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.trashboxbobylev.summoningpixeldungeon.levels.Level;
import com.trashboxbobylev.summoningpixeldungeon.levels.Terrain;
import com.trashboxbobylev.summoningpixeldungeon.levels.painters.Painter;
import com.trashboxbobylev.summoningpixeldungeon.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShopRoom extends SpecialRoom {

	private ArrayList<Item> itemsToSpawn;
	
	@Override
	public int minWidth() {
		if (itemsToSpawn == null) itemsToSpawn = generateItems();
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	@Override
	public int minHeight() {
		if (itemsToSpawn == null) itemsToSpawn = generateItems();
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		placeShopkeeper( level );

		placeItems( level );
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}

	protected void placeItems( Level level ){

		if (itemsToSpawn == null)
			itemsToSpawn = generateItems();

		Point itemPlacement = new Point(entrance());
		if (itemPlacement.y == top){
			itemPlacement.y++;
		} else if (itemPlacement.y == bottom) {
			itemPlacement.y--;
		} else if (itemPlacement.x == left){
			itemPlacement.x++;
		} else {
			itemPlacement.x--;
		}

		for (Item item : itemsToSpawn) {

			if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
				itemPlacement.y--;
			} else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
				itemPlacement.x++;
			} else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
				itemPlacement.y++;
			} else {
				itemPlacement.x--;
			}

			int cell = level.pointToCell(itemPlacement);

			if (level.heaps.get( cell ) != null) {
				do {
					cell = level.pointToCell(random());
				} while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
			}

			level.drop( item, cell ).type = Heap.Type.FOR_SALE;
		}

	}
	
	protected static ArrayList<Item> generateItems() {

		ArrayList<Item> itemsToSpawn = new ArrayList<>();
		
		switch (Dungeon.depth) {
		case 6:
            Item i = Generator.random(Generator.Category.WAND);
            if (i != null && !i.cursed) i.upgrade(1).identify();
			itemsToSpawn.add(i);
			break;
			
		case 11:
			itemsToSpawn.add(new PotionOfExperience());
			itemsToSpawn.add(new ScrollOfDivination());
            i = Generator.random(Generator.Category.RING);
            if (i != null && !i.cursed) i.upgrade(1);
            itemsToSpawn.add(i);
			break;
			
		case 16:
			itemsToSpawn.add(new StoneOfEnchantment());
			itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_POTION));
			itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_SCROLL));
            i = Generator.randomMissile();
            if (i != null) i.upgrade(1).identify().quantity(1);
            itemsToSpawn.add(i);
			break;
			
		case 21:
		    itemsToSpawn.add(new ScrollOfEnchantment());
			itemsToSpawn.add( new BeaconOfReturning());
            itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_POTION));
            itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_POTION));
            itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_SCROLL));
            itemsToSpawn.add(Generator.random(Generator.Category.EXOTIC_SCROLL));
			itemsToSpawn.add( new Torch() );
			break;
		}
		
		itemsToSpawn.add( TippedDart.randomTipped(2) );

		itemsToSpawn.add( new MerchantsBeacon() );

        for (int i=0; i < 2; i++) itemsToSpawn.add(Generator.random(Generator.Category.STONE));

		itemsToSpawn.add(ChooseArmor(Dungeon.hero.belongings.armor));
        itemsToSpawn.add(ChooseWeapon((Weapon) Dungeon.hero.belongings.weapon));
        Staff s = Generator.randomStaff();
        while (s.cursed){
            s = Generator.randomStaff();
            s.identify();
        }
        itemsToSpawn.add(s);
        itemsToSpawn.add(Generator.randomMissile());
        itemsToSpawn.add(Generator.randomMissile());

		itemsToSpawn.add(ChooseBag(Dungeon.hero.belongings));

		itemsToSpawn.add( new PotionOfHealing() );
		for (int i=0; i < 3; i++)
			itemsToSpawn.add( Generator.random( Generator.Category.POTION ) );

		itemsToSpawn.add( new ScrollOfIdentify() );
		itemsToSpawn.add( new ScrollOfRemoveCurse() );
		itemsToSpawn.add( new ScrollOfMagicMapping() );
		itemsToSpawn.add( Generator.random( Generator.Category.SCROLL ) );

		for (int i=0; i < 4; i++)
			itemsToSpawn.add( Random.Int(2) == 0 ?
					Generator.random( Generator.Category.POTION ) :
					Generator.random( Generator.Category.SCROLL ) );


		itemsToSpawn.add( new SmallRation() );
		itemsToSpawn.add( new SmallRation() );
		
		switch (Random.Int(4)){
			case 0:
				itemsToSpawn.add( new Bomb() );
				break;
			case 1:
			case 2:
				itemsToSpawn.add( new Bomb.DoubleBomb() );
				break;
			case 3:
				itemsToSpawn.add( new Honeypot() );
				break;
		}

		itemsToSpawn.add( new Ankh() );
		itemsToSpawn.add( new StoneOfAugmentation() );

		TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null){
			int bags = 0;
			//creates the given float percent of the remaining bags to be dropped.
			//this way players who get the hourglass late can still max it, usually.
			switch (Dungeon.depth) {
				case 6:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.20f ); break;
				case 11:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.25f ); break;
				case 16:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.50f ); break;
				case 21:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.80f ); break;
			}

			for(int i = 1; i <= bags; i++){
				itemsToSpawn.add( new TimekeepersHourglass.sandBag());
				hourglass.sandBags ++;
			}
		}

		Item rare;
		switch (Random.Int(8)){
			case 0:
				rare = Generator.random( Generator.Category.WAND );
				rare.level( 0 );
				break;
			case 1:
				rare = Generator.random(Generator.Category.RING);
				rare.level( 0 );
				break;
			case 2:
				rare = Generator.random( Generator.Category.ARTIFACT );
				break;
			default:
				rare = new Stylus();
		}
		rare.cursed = false;
		rare.cursedKnown = true;
		itemsToSpawn.add( rare );

		//hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
		if (itemsToSpawn.size() > 63)
			throw new RuntimeException("Shop attempted to carry more than 63 items!");

		Random.shuffle(itemsToSpawn);
		return itemsToSpawn;
	}

	protected static Armor ChooseArmor(Armor armor){
	    //shop's armor will be better, that player's one

        //on faith is my armor, no armor
        if (Challenges.isItemBlocked(new Armor(2))) return null;
        //we do not bother, if hero's armor is too powerful
        if (armor.level() > 2) return null;
        //do not bother, if player have augmented their armor
        if (armor.augment != Armor.Augment.NONE) return null;

        boolean enchant = false;
        //if player's armor is not enchanted, with 75% shop's armor will be enchanted
        if (!armor.hasGoodGlyph() && Random.Int(4 ) < 4) enchant = true;

        int armorDefenseMax = armor.DRMax();

        //roll armor, until we get the needed
        Armor neededArmor;
        do {
            neededArmor = Generator.randomArmor();
            neededArmor.identify();
        } while (neededArmor.cursed && neededArmor.DRMax() <= armorDefenseMax && (enchant && !neededArmor.hasGoodGlyph())
        && (Random.Int(2) == 1 && neededArmor.tier > 3));
        return neededArmor;
    }

    protected static Weapon ChooseWeapon(Weapon weapon){
        //shop's weapon will be better, that player's one

        //we do not bother, if hero's weapon is too powerful
        if (weapon.level() > 2) return null;
        //do not bother, if player have augmented their weapon
        if (weapon.augment != Weapon.Augment.NONE) return null;

        boolean enchant = false;
        //if player's armor is not enchanted, with 75% shop's weapon will be enchanted
        if (!weapon.hasGoodEnchant() && Random.Int(4 ) < 4) enchant = true;

        int weaponDmgMax = weapon.max();

        //roll weapon, until we get the needed
        MeleeWeapon weapon1;
        do {
            weapon1 = Generator.randomWeapon();
            weapon1.identify();
        } while (weapon1.cursed && weapon1.max() <= weaponDmgMax && (enchant && !weapon1.hasGoodEnchant())
        && (Random.Int(2) == 1 && weapon1.tier > 3));
        return weapon1;
    }

	protected static Bag ChooseBag(Belongings pack){
	
		//0=pouch, 1=holder, 2=bandolier, 3=holster
		int[] bagItems = new int[4];

		//count up items in the main bag
		for (Item item : pack.backpack.items) {
			if (item instanceof Plant.Seed || item instanceof Runestone)    bagItems[0]++;
			if (item instanceof Scroll)                                     bagItems[1]++;
			if (item instanceof Potion)                                     bagItems[2]++;
			if (item instanceof Wand || item instanceof MissileWeapon)      bagItems[3]++;
		}
		
		//disqualify bags that have already been dropped
		if (Dungeon.LimitedDrops.VELVET_POUCH.dropped())                    bagItems[0] = -1;
		if (Dungeon.LimitedDrops.SCROLL_HOLDER.dropped())                   bagItems[1] = -1;
		if (Dungeon.LimitedDrops.POTION_BANDOLIER.dropped())                bagItems[2] = -1;
		if (Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped())                 bagItems[3] = -1;
		
		//find the best bag to drop. This does give a preference to later bags, if counts are equal
		int bestBagIdx = 0;
		for (int i = 1; i <= 3; i++){
			if (bagItems[bestBagIdx] <= bagItems[i]){
				bestBagIdx = i;
			}
		}
		
		//drop it, or return nothing if no bag works
		if (bagItems[bestBagIdx] == -1) return null;
		switch (bestBagIdx){
			case 0: default:
				Dungeon.LimitedDrops.VELVET_POUCH.drop();
				return new VelvetPouch();
			case 1:
				Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
				return new ScrollHolder();
			case 2:
				Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
				return new PotionBandolier();
			case 3:
				Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
				return new MagicalHolster();
		}

	}

}
