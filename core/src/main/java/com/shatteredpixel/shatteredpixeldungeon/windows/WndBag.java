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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class WndBag extends WndTabbed {
	
	//only one bag window can appear at a time
	public static Window INSTANCE;
	
	//FIXME this is getting cumbersome, there should be a better way to manage this
	public static enum Mode {
		ALL,
		UNIDENTIFED,
		UNCURSABLE,
		CURSABLE,
		UPGRADEABLE,
		QUICKSLOT,
		FOR_SALE,
		WEAPON,
		ARMOR,
        ARMOR_FOR_IMBUE,
		ENCHANTABLE,
		ENCHANTED,
		ENCHANTABLE_WEAPONS,
		WAND,
		SEED,
		FOOD,
		POTION,
		SCROLL,
		INTUITIONABLE,
		EQUIPMENT,
		TRANMSUTABLE,
		ALCHEMY,
		RECYCLABLE,
		NOT_EQUIPPED
	}

	protected static final int COLS_P    = 5;
	protected static final int COLS_L    = 5;
	
	protected static int SLOT_WIDTH_P   = 28;
	protected static int SLOT_WIDTH_L   = 28;

	protected static int SLOT_HEIGHT_P	= 28;
	protected static int SLOT_HEIGHT_L	= 28;

	protected static final int SLOT_MARGIN	= 1;
	
	protected static final int TITLE_HEIGHT	= 14;

	private ItemSelector selector;

	private int nCols;
	private int nRows;

	private int slotWidth;
	private int slotHeight;

	protected int count;
	protected int col;
	protected int row;

	private static Bag lastBag;

	public WndBag( Bag bag ) {
		this(bag, null);
	}

	public WndBag( Bag bag, ItemSelector selector ) {
		
		super();
		
		if( INSTANCE != null ){
			INSTANCE.hide();
		}
		INSTANCE = this;

		this.selector = selector;

		lastBag = bag;

		slotWidth = PixelScene.landscape() ? SLOT_WIDTH_L : SLOT_WIDTH_P;
		slotHeight = PixelScene.landscape() ? SLOT_HEIGHT_L : SLOT_HEIGHT_P;

		nCols = PixelScene.landscape() ? COLS_L : COLS_P;
		nRows = (int)Math.ceil(25/(float)nCols) ; //we expect to lay out 25 slots in all cases

		if (SPDSettings.flipInventory()){
//			col = nCols;
			row = nRows;
		}

		int windowWidth = slotWidth * nCols + SLOT_MARGIN * (nCols - 1);
		int windowHeight = TITLE_HEIGHT + slotHeight * nRows + SLOT_MARGIN * (nRows - 1);

		if (PixelScene.landscape()){
			while (slotHeight >= 24 && (windowHeight + 20 + chrome.marginTop()) > PixelScene.uiCamera.height){
				slotHeight--;
				windowHeight -= nRows;
			}
		} else {
			while (slotWidth >= 26 && (windowWidth + chrome.marginHor()) > PixelScene.uiCamera.width){
				slotWidth--;
				windowWidth -= nCols;
			}
		}

		placeTitle( bag, windowWidth );

		if (SPDSettings.flipInventory())
			count = bag.capacity();

		placeItems( bag );

		resize( windowWidth, windowHeight );

		Belongings stuff = Dungeon.hero.belongings;
		Bag[] bags = {
			stuff.backpack,
			stuff.getItem( VelvetPouch.class ),
			stuff.getItem( ScrollHolder.class ),
			stuff.getItem( PotionBandolier.class ),
			stuff.getItem( MagicalHolster.class ),
			stuff.getItem( ConjurerBook.class )};

		for (Bag b : bags) {
			if (b != null) {
				BagTab tab = new BagTab( b );
				add( tab );
				tab.select( b == bag );
			}
		}

		layoutTabs();
	}

	public static WndBag lastBag( ItemSelector selector ) {

		if (lastBag != null && Dungeon.hero.belongings.backpack.contains( lastBag )) {

			return new WndBag( lastBag, selector );

		} else {

			return new WndBag( Dungeon.hero.belongings.backpack, selector );

		}
	}

	public static WndBag getBag( ItemSelector selector ) {
		if (selector.preferredBag() == Belongings.Backpack.class){
			return new WndBag( Dungeon.hero.belongings.backpack, selector );

		} else if (selector.preferredBag() != null){
			Bag bag = Dungeon.hero.belongings.getItem( selector.preferredBag() );
			if (bag != null) return new WndBag( bag, selector );
		}

		return lastBag( selector );
	}

	protected void placeTitle( Bag bag, int width ){

		float titleWidth;
		if (Dungeon.energy == 0) {
			ItemSprite gold = new ItemSprite(ItemSpriteSheet.GOLD, null);
			gold.x = width - gold.width();
			gold.y = (TITLE_HEIGHT - gold.height()) / 2f;
			PixelScene.align(gold);
			add(gold);

			BitmapText amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
			amt.hardlight(TITLE_COLOR);
			amt.measure();
			amt.x = width - gold.width() - amt.width() - 1;
			amt.y = (TITLE_HEIGHT - amt.baseLine()) / 2f - 1;
			PixelScene.align(amt);
			add(amt);

			titleWidth = amt.x;
		} else {

			Image gold = Icons.get(Icons.COIN_SML);
			gold.x = width - gold.width() - 0.5f;
			gold.y = 0;
			PixelScene.align(gold);
			add(gold);

			BitmapText amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
			amt.hardlight(TITLE_COLOR);
			amt.measure();
			amt.x = width - gold.width() - amt.width() - 2f;
			amt.y = 0;
			PixelScene.align(amt);
			add(amt);

			titleWidth = amt.x;

			Image energy = Icons.get(Icons.ENERGY_SML);
			energy.x = width - energy.width();
			energy.y = gold.height();
			PixelScene.align(energy);
			add(energy);

			amt = new BitmapText(Integer.toString(Dungeon.energy), PixelScene.pixelFont);
			amt.hardlight(0x44CCFF);
			amt.measure();
			amt.x = width - energy.width() - amt.width() - 1;
			amt.y = energy.y;
			PixelScene.align(amt);
			add(amt);

			titleWidth = Math.min(titleWidth, amt.x);
		}

		String title = selector != null ? selector.textPrompt() : null;
		RenderedTextBlock txtTitle = PixelScene.renderTextBlock(
				title != null ? Messages.titleCase(title) : Messages.titleCase( bag.name() ), 8 );
		txtTitle.hardlight( TITLE_COLOR );
		txtTitle.maxWidth( (int)titleWidth - 2 );
		txtTitle.setPos(
				1,
				(TITLE_HEIGHT - txtTitle.height()) / 2f - 1
		);
		PixelScene.align(txtTitle);
		add( txtTitle );
	}
	
	protected void placeItems( Bag container ) {
		
		// Equipped items
		Belongings stuff = Dungeon.hero.belongings;

		if (!SPDSettings.flipInventory()) {
			placeItem( stuff.weapon != null ? stuff.weapon : new Placeholder( ItemSpriteSheet.WEAPON_HOLDER ) );
			placeItem( stuff.armor != null ? stuff.armor : new Placeholder( ItemSpriteSheet.ARMOR_HOLDER ) );
			for (Artifact acc: stuff.accs){
				placeItem( acc != null ? acc : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
			}
//			placeItem( stuff.offenseAcc != null ? stuff.offenseAcc : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
//			placeItem( stuff.defenseAcc != null ? stuff.defenseAcc : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
//			placeItem( stuff.utilityAcc != null ? stuff.utilityAcc : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );

		}
		else {
			placeItem(new lul());
		}

		//the container itself if it's not the root backpack
		if (container != Dungeon.hero.belongings.backpack){
			placeItem(container);
			count += SPDSettings.flipInventory() ? 1 : -1; //don't count this one, as it's not actually inside of itself
		}

		// Items in the bag, except other containers (they have tags at the bottom)
		for (Item item : container.items.toArray(new Item[0])) {
			if (!(item instanceof Bag)) {
				placeItem( item );
			} else {
				count += SPDSettings.flipInventory() ? -1 : 1;
			}
		}

		
		// Free Space
		if (SPDSettings.flipInventory()) {
			while (count >= 0) {
				placeItem(null);
			}
			count = -1 - container.capacity() + 20;
			col = nCols;
			row = 0;
		}
		else {
			while ((count - 5) < container.capacity()) {
				placeItem(null);
			}
		}

		if (SPDSettings.flipInventory()) {
			placeItem( stuff.accs.get(2) != null ? stuff.accs.get(2) : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
			placeItem( stuff.accs.get(1) != null ? stuff.accs.get(1) : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
			placeItem( stuff.accs.get(0) != null ? stuff.accs.get(0) : new Placeholder( ItemSpriteSheet.ARTIFACT_HOLDER ) );
			placeItem( stuff.armor != null ? stuff.armor : new Placeholder( ItemSpriteSheet.ARMOR_HOLDER ) );
			placeItem( stuff.weapon != null ? stuff.weapon : new Placeholder( ItemSpriteSheet.WEAPON_HOLDER ) );
		}


	}
	
	protected void placeItem( final Item item ) {

		count += SPDSettings.flipInventory() ? -1 : 1;

		int x = (col + (SPDSettings.flipInventory() ? -1 : 0)) * (slotWidth + SLOT_MARGIN);
		int y = TITLE_HEIGHT + row * (slotHeight + SLOT_MARGIN);
		
		add( new ItemButton( item ).setPos( x, y ) );

		if (SPDSettings.flipInventory()) {
			if (--col <= 0) {
				col = nCols;
				row--;
			}
		} else {
			if (++col >= nCols) {
				col = 0;
				row++;
			}
		}

	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.INVENTORY) {
			hide();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void onBackPressed() {
		if (selector != null) {
			selector.onSelect( null );
		}
		super.onBackPressed();
	}

	@Override
	protected void onClick( Tab tab ) {
		hide();
		Game.scene().addToFront(new WndBag(((BagTab) tab).bag, selector));
	}
	
	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this){
			INSTANCE = null;
		}
	}
	
	@Override
	protected int tabHeight() {
		return 20;
	}
	
	private Image icon( Bag bag ) {
		if (bag instanceof VelvetPouch) {
			return Icons.get( Icons.SEED_POUCH );
		} else if (bag instanceof ScrollHolder) {
			return Icons.get( Icons.SCROLL_HOLDER );
		} else if (bag instanceof MagicalHolster) {
			return Icons.get( Icons.WAND_HOLSTER );
		} else if (bag instanceof PotionBandolier) {
			return Icons.get( Icons.POTION_BANDOLIER );
		} else if (bag instanceof ConjurerBook) {
			return Icons.get( Icons.CONJURER_BOOK );
		} else {
			return Icons.get( Icons.BACKPACK );
		}
	}
	
	private class BagTab extends IconTab {

		private Bag bag;
		
		public BagTab( Bag bag ) {
			super( icon(bag) );
			
			this.bag = bag;
		}
		
	}
	
	public static class Placeholder extends Item {

		public Placeholder(int image ) {
			this.image = image;
		}

		@Override
		public String name() {
			return null;
		}

		@Override
		public boolean isIdentified() {
		return true;
	}
		
		@Override
		public boolean isEquipped( Hero hero ) {
			return true;
		}
	}

	public static class lul extends Item{

	}
	
	private class ItemButton extends ItemSlot {
		
		private static final int NORMAL		= 0x9953564D;
		private static final int EQUIPPED	= 0x9991938C;
		
		private Item item;
		private ColorBlock bg;
		
		public ItemButton( Item item ) {
			
			super( item );

			this.item = item;
			if (item instanceof Gold || item instanceof Bag || item instanceof lul) {
				bg.visible = false;
			}
			
			width = slotWidth;
			height = slotHeight;
		}
		
		@Override
		protected void createChildren() {
			bg = new ColorBlock( 1, 1, NORMAL );
			add( bg );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			bg.size(width, height);
			bg.x = x;
			bg.y = y;
			
			super.layout();
		}
		
		@Override
		public void item( Item item ) {

			super.item(item);
			if (item != null) {

				bg.texture(TextureCache.createSolid(item.isEquipped(Dungeon.hero) ? EQUIPPED : NORMAL));
				if (item.cursed && item.cursedKnown) {
					bg.ra = +0.3f;
					bg.ga = -0.15f;
				} else if (!item.isIdentified()) {
					if ((item instanceof EquipableItem || item instanceof Wand) && item.cursedKnown) {
						bg.ba = 0.3f;
					} else {
						bg.ra = 0.3f;
						bg.ba = 0.3f;
					}
				}

				if (item.name() == null) {
					enable(false);
				} else if (selector != null) {
					enable(selector.itemSelectable(item));
				}
//					enable(
//						mode == Mode.FOR_SALE && Shopkeeper.willBuyItem(item) ||
//						mode == Mode.UPGRADEABLE && item.isUpgradable() ||
//						mode == Mode.UNIDENTIFED && !item.isIdentified() ||
//						mode == Mode.UNCURSABLE && ScrollOfRemoveCurse.uncursable(item) ||
//						mode == Mode.CURSABLE && ((item instanceof EquipableItem && !(item instanceof MissileWeapon)) || item instanceof Wand) ||
//						mode == Mode.QUICKSLOT && (!item.getDefaultAction().equals("")) ||
//						mode == Mode.WEAPON && (item instanceof MeleeWeapon) ||
//						mode == Mode.ARMOR && (item instanceof Armor) ||
//                        mode == Mode.ARMOR_FOR_IMBUE && (item instanceof Armor && !(item instanceof ConjurerArmor)) ||
//						mode == Mode.ENCHANTABLE && ((item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof Slingshot || (item instanceof Wand && Dungeon.hero.heroClass == HeroClass.MAGE) || item instanceof Armor || item instanceof Staff || (item instanceof MissileWeapon && Dungeon.hero.hasTalent(Talent.WILD_SORCERY))) && !(item instanceof Broadsword)) ||
//						mode == Mode.ENCHANTED && ((item instanceof Armor && ((Armor) item).glyph != null) || (item instanceof Weapon && ((Weapon) item).enchantment != null)) ||
//								mode == Mode.WAND && (item instanceof Wand) ||
//						mode == Mode.ENCHANTABLE_WEAPONS && ((item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof Slingshot || item instanceof Staff) && !(item instanceof Broadsword)) ||
//						mode == Mode.SEED && SandalsOfNature.canUseSeed(item) ||
//						mode == Mode.FOOD && (item instanceof Food) ||
//						mode == Mode.POTION && (item instanceof Potion) ||
//						mode == Mode.SCROLL && (item instanceof Scroll) ||
//						mode == Mode.INTUITIONABLE && StoneOfIntuition.isIntuitionable(item) ||
//						mode == Mode.EQUIPMENT && (item instanceof EquipableItem || item instanceof Wand) ||
//						mode == Mode.ALCHEMY && Recipe.usableInRecipe(item) ||
//						mode == Mode.TRANMSUTABLE && ScrollOfTransmutation.canTransmute(item) ||
//						mode == Mode.NOT_EQUIPPED && !item.isEquipped(Dungeon.hero) ||
//						mode == Mode.RECYCLABLE && Recycle.isRecyclable(item) ||
//						mode == Mode.ALL
//					);
			} else {
				bg.color( NORMAL );
			}
		}
		
		@Override
		protected void onPointerDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.Sounds.CLICK, 0.7f, 0.7f, 1.2f );
		}
		
		protected void onPointerUp() {
			bg.brightness( 1.0f );
		}

		@Override
		protected void onClick() {
			if (lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){

				hide();

			} else if (selector != null) {

				hide();
				selector.onSelect( item );

			} else {

				Game.scene().addToFront(new WndUseItem( WndBag.this, item ) );

			}
		}

		@Override
		protected boolean onLongClick() {
			if (selector == null && item.getDefaultAction() != null) {
				hide();
				Dungeon.quickslot.setSlot( 0 , item );
				QuickSlotButton.refresh();
				return true;
			} else if (selector != null) {
				Game.scene().addToFront(new WndInfoItem(item));
				return true;
			} else {
				return false;
			}
		}
	}

	public interface ItemSelector {
		String textPrompt();
		default Class<?extends Bag> preferredBag(){
			return null; //defaults to last bag opened
		}
		boolean itemSelectable( Item item );
		void onSelect( Item item );
	}
}
