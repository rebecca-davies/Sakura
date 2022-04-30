package com.example.oneclicklavas;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
	name = "One Click Lavas",
	description = ""
)
@Slf4j
public class OneClickLavasPlugin extends Plugin
{
	// Injects our config
	@Inject
	private OneClickLavasConfig config;
	@Inject
	private Client client;
	private boolean needRing;
	private boolean needStamina;
	private boolean needDeposit;
	private boolean needNecklace;
	private boolean shouldEquipRing;
	private boolean shouldDrinkStamina;
	private boolean shouldEquipNecklace;
	private boolean shouldFillGiant = true;
	private boolean teleNow = false;
	private boolean shouldFillLarge = true;
	private boolean leaveBank = false;
	private boolean shouldFillMedium = true;
	private boolean shouldFillSmall = true;
	private boolean needEssence = true;
	private int timeout;

	// Provides our config
	@Provides
	OneClickLavasConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OneClickLavasConfig.class);
	}

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e){
		if(timeout!=0){
			e.consume();
			return;
		}
		GameObject bankChest = findGameObjectByName("Bank chest");
		if(bankChest!=null){
			if(bankOpen()){
				if(needDeposit||needRing||needNecklace||needStamina){
					if(needRing){
						Widget ring= getBankWidgetItem(ItemID.RING_OF_DUELING8);
						e.setMenuEntry(createWidgetEntry(ring,"Withdraw-1"));
						timeout = 1;
						needRing=false;
						return;
					}else if(needDeposit){
						Widget lavas = getBankInventoryItem(ItemID.LAVA_RUNE);
						e.setMenuEntry(createWidgetEntry(lavas,"Deposit-All"));
						needDeposit=false;
						return;
					}else if(needNecklace){
						Widget binding= getBankWidgetItem(ItemID.BINDING_NECKLACE);
						e.setMenuEntry(createWidgetEntry(binding,"Withdraw-1"));
						timeout = 1;
						needNecklace=false;
						return;
					}else if(needStamina){
						Widget stamina= getBankWidgetItem(ItemID.STAMINA_POTION1);
						e.setMenuEntry(createWidgetEntry(stamina,"Withdraw-1"));
						timeout = 1;
						needStamina=false;
						return;
					}
				}
				if(shouldDrinkStamina||shouldEquipNecklace||shouldEquipRing||needEssence){
					if(shouldDrinkStamina){
						Widget stamina= getBankInventoryItem(ItemID.STAMINA_POTION1);
						e.setMenuEntry(createWidgetEntry(stamina,"Drink"));
						shouldDrinkStamina = false;
						return;
					}else if(shouldEquipRing){
						Widget ring= getBankInventoryItem(ItemID.STAMINA_POTION1);
						e.setMenuEntry(createWidgetEntry(ring,"Wear"));
						shouldEquipRing =false;
						return;
					}else if(shouldEquipNecklace){
						Widget necklace= getBankInventoryItem(ItemID.BINDING_NECKLACE);
						e.setMenuEntry(createWidgetEntry(necklace,"Wear"));
						shouldEquipNecklace = false;
						return;
					}else if(needEssence){
						Widget pure= getBankWidgetItem(ItemID.PURE_ESSENCE);
						Widget rune= getBankWidgetItem(ItemID.RUNE_ESSENCE);
						needEssence = false;
						if (pure != null) {
							e.setMenuEntry(createWidgetEntry(pure, "Withdraw-All"));
							return;
						} else {
							e.setMenuEntry(createWidgetEntry(rune, "Withdraw-All"));
							return;
						}
					}
				}
				if(config.pouches()==POUCHESOPTIONS.LARGEGIANT){
					if(shouldFillLarge){
						Widget large = getBankInventoryItem(ItemID.LARGE_POUCH);
						e.setMenuEntry(createWidgetEntry(large,"Fill"));
						shouldFillLarge = false;
						return;
					}else if(shouldFillGiant){
						Widget giant = getBankInventoryItem(ItemID.GIANT_POUCH);
						e.setMenuEntry(createWidgetEntry(giant,"Fill"));
						shouldFillGiant = false;
						return;
					}else{
						if(!teleNow) {
							Widget pure = getBankWidgetItem(ItemID.PURE_ESSENCE);
							Widget rune = getBankWidgetItem(ItemID.RUNE_ESSENCE);
							if (pure != null) {
								e.setMenuEntry(createWidgetEntry(pure, "Withdraw-All"));
								teleNow = true;
								return;
							} else {
								e.setMenuEntry(createWidgetEntry(rune, "Withdraw-All"));
								teleNow = true;
								return;
							}
						}else{
							Widget ring = client.getWidget(WidgetInfo.EQUIPMENT_RING);
							e.setMenuEntry(createWidgetEntry(ring,"Duel Arena"));
							needEssence = true;
							teleNow = false;
							return;
						}
					}
				}else{
					if(shouldFillMedium){
						Widget medium = getBankInventoryItem(ItemID.MEDIUM_POUCH);
						e.setMenuEntry(createWidgetEntry(medium,"Fill"));
						shouldFillMedium = false;
						return;
					}else if(shouldFillSmall){
						Widget large = getBankInventoryItem(ItemID.SMALL_POUCH);
						e.setMenuEntry(createWidgetEntry(large,"Fill"));
						shouldFillSmall = false;
						return;
					}else if(shouldFillLarge){
						Widget large = getBankInventoryItem(ItemID.LARGE_POUCH);
						e.setMenuEntry(createWidgetEntry(large,"Fill"));
						shouldFillLarge = false;
						return;
					}else{
						if(!teleNow) {
							Widget pure = getBankWidgetItem(ItemID.PURE_ESSENCE);
							Widget rune = getBankWidgetItem(ItemID.RUNE_ESSENCE);
							if (pure != null) {
								e.setMenuEntry(createWidgetEntry(pure, "Withdraw-All"));
								teleNow = true;
								return;
							} else {
								e.setMenuEntry(createWidgetEntry(rune, "Withdraw-All"));
								teleNow = true;
								return;
							}
						}else{
							Widget ring = client.getWidget(WidgetInfo.EQUIPMENT_RING);
							e.setMenuEntry(createWidgetEntry(ring,"Duel Arena"));
							needEssence = true;
							teleNow = false;
							return;
						}
					}
				}
			}else{
				e.setMenuEntry(createMenuEntry(bankChest.getId(),MenuAction.GAME_OBJECT_FIRST_OPTION,bankChest.getSceneMinLocation().getX(),bankChest.getSceneMinLocation().getY(),false));
				return;
			}
		}
	}
	public GameObject findGameObjectByIds(int... ids){
		GameObjectQuery query = new GameObjectQuery();
		query.idEquals(ids);
		return query.result(client).nearestTo(client.getLocalPlayer());
	}
	public GameObject findGameObjectByName(String... ids){
		GameObjectQuery query = new GameObjectQuery();
		query.nameEquals(ids);
		return query.result(client).nearestTo(client.getLocalPlayer());
	}
	private boolean bankOpen() {
		return client.getItemContainer(InventoryID.BANK) != null;
	}
	private Widget getBankWidgetItem(int id) {
		Widget inventoryWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
		return getWidgetItem(id, inventoryWidget);
	}
	private Widget getInventoryItem(int id) {
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		return getWidgetItem(id, inventoryWidget);
	}
	private Widget getBankInventoryItem(int id) {
		Widget inventoryWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
		return getWidgetItem(id, inventoryWidget);
	}
	private MenuEntry createWidgetEntry(Widget widget,String action){
		//Arrays.stream(widget.getActions()).collect(Collectors.toList()).ind
		return null;//createMenuEntry(widget.getActions().indexOf(action)+1,MenuAction.CC_OP,widget.getIndex(),widget.getId(),true);
	}
	private Widget getWidgetItem(int id, Widget inventoryWidget) {
		if (inventoryWidget != null) {
			Widget[] items = inventoryWidget.getChildren();
			if(items==null){
				return null;
			}
			for (Widget item : items) {
				if (item.getId() == id) {
					return item;
				}
			}
		}
		return null;
	}

	private void getMaintenanceItems(){
		ArrayList<String> items = new ArrayList<>();
		ItemContainer equipmentContainer = client.getItemContainer(InventoryID.EQUIPMENT);
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		Item ring = equipmentContainer.getItem(EquipmentInventorySlot.RING.getSlotIdx());
		Item amulet = equipmentContainer.getItem(EquipmentInventorySlot.AMULET.getSlotIdx());
		Item lavas = inventory.getItem(ItemID.LAVA_RUNE);
		if(ring==null){
			needRing=true;
		}
		if(amulet==null){
			needNecklace=true;
		}
		if(client.getEnergy()<80){
			needStamina = true;
		}
		if(lavas!=null){
			needDeposit = true;
		}
	}
	public MenuEntry createMenuEntry(int identifier, MenuAction type, int param0, int param1, boolean forceLeftClick) {
		return client.createMenuEntry(0).setOption("").setTarget("").setIdentifier(identifier).setType(type)
				.setParam0(param0).setParam1(param1).setForceLeftClick(forceLeftClick);
	}
}