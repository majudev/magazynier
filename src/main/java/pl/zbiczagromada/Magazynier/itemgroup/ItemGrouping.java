package pl.zbiczagromada.Magazynier.itemgroup;

import lombok.Getter;
import pl.zbiczagromada.Magazynier.item.Item;
import pl.zbiczagromada.Magazynier.storageunit.StorageUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemGrouping {
    private final Long itemGroupId;
    private final String itemGroupName;
    private final String itemGroupMark;

    private final Long numberOfItems;
    private final Long numberOfCountedItems;
    private final Long numberOfIndividualItems;

    private final List<Item> individualItems;

    private final Map<Long, Long> countedItemsPerStorageUnit;

    private ItemGrouping(Long itemGroupId, String itemGroupName, String itemGroupMark, Long numberOfItems, Long numberOfCountedItems, Long numberOfIndividualItems, List<Item> individualItems, Map<Long, Long> countedItemsPerStorageUnit) {
        this.itemGroupId = itemGroupId;
        this.itemGroupName = itemGroupName;
        this.itemGroupMark = itemGroupMark;
        this.numberOfItems = numberOfItems;
        this.numberOfCountedItems = numberOfCountedItems;
        this.numberOfIndividualItems = numberOfIndividualItems;
        this.individualItems = individualItems;
        this.countedItemsPerStorageUnit = countedItemsPerStorageUnit;
    }

    public static List<ItemGrouping> createGroupingFromStorageUnit(StorageUnit storageUnit){
        Map<Long, List<Item>> individualItemsByGroup = new HashMap<Long, List<Item>>();
        Map<Long, Long> countedItemsByGroup = new HashMap<Long, Long>();
        Map<Long, String> itemGroupNames = new HashMap<Long, String>();
        Map<Long, String> itemGroupMarks = new HashMap<Long, String>();

        for(Item item : storageUnit.getItems()){
            if(!individualItemsByGroup.containsKey(item.getItemGroupId())){
                individualItemsByGroup.put(item.getItemGroupId(), new ArrayList<Item>());
            }
            if(!countedItemsByGroup.containsKey(item.getItemGroupId())){
                countedItemsByGroup.put(item.getItemGroupId(), 0L);
            }
            if(!itemGroupNames.containsKey(item.getItemGroupId())){
                itemGroupNames.put(item.getItemGroupId(), item.getItemGroup().getName());
            }
            if(!itemGroupMarks.containsKey(item.getItemGroupId())){
                itemGroupMarks.put(item.getItemGroupId(), item.getItemGroup().getMark());
            }

            if(item.getMark() != null /*|| item.getNotes() != null*/) {
                List<Item> individualItemsList = individualItemsByGroup.get(item.getItemGroupId());
                individualItemsList.add(item);
                individualItemsByGroup.put(item.getItemGroupId(), individualItemsList);
            }else{
                Long cnt = countedItemsByGroup.get(item.getItemGroupId());
                ++cnt;
                countedItemsByGroup.put(item.getItemGroupId(), cnt);
            }
        }

        List<ItemGrouping> grouping = new ArrayList<ItemGrouping>();

        itemGroupNames.forEach(
            (key, value) -> {
                Long countedItems = countedItemsByGroup.get(key);
                List<Item> individualItems = individualItemsByGroup.get(key);
                Map<Long, Long> countedItemsPerStorageUnit = new HashMap<>();
                countedItemsPerStorageUnit.put(storageUnit.getId(), countedItems);
                ItemGrouping itemGrouping = new ItemGrouping(
                        key,
                        value,
                        itemGroupMarks.get(key),
                        countedItems + individualItems.size(),
                        countedItems,
                        (long) individualItems.size(),
                        individualItems,
                        countedItemsPerStorageUnit);
                grouping.add(itemGrouping);
        });

        return grouping;
    }

    public static ItemGrouping createGroupingFromItemGroup(ItemGroup itemGroup){
        List<Item> individualItems = new ArrayList<Item>();
        Map<Long, Long> countedItemsPerStorageUnit = new HashMap<>();
        Long countedItems = 0L;
        Long itemGroupId = itemGroup.getId();
        String itemGroupName = itemGroup.getName();
        String itemGroupMark = itemGroup.getMark();

        for(Item item : itemGroup.getItems()){
            if(item.getMark() != null /*|| item.getNotes() != null*/) {
                individualItems.add(item);
            }else{
                ++countedItems;
                Long itemsInThisStorageUnit = countedItemsPerStorageUnit.get(item.getStorageUnitId());
                if(itemsInThisStorageUnit == null) itemsInThisStorageUnit = 0L;
                countedItemsPerStorageUnit.put(item.getStorageUnitId(), itemsInThisStorageUnit + 1);
            }
        }

        ItemGrouping itemGrouping = new ItemGrouping(
                itemGroupId,
                itemGroupName,
                itemGroupMark,
                countedItems + individualItems.size(),
                countedItems,
                (long) individualItems.size(),
                individualItems,
                countedItemsPerStorageUnit);

        return itemGrouping;
    }
}
