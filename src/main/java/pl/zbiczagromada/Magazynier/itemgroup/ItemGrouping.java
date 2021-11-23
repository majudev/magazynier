package pl.zbiczagromada.Magazynier.itemgroup;

import lombok.Getter;
import org.apache.commons.collections4.MapIterator;
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

    private ItemGrouping(Long itemGroupId, String itemGroupName, String itemGroupMark, Long numberOfItems, Long numberOfCountedItems, Long numberOfIndividualItems, List<Item> individualItems) {
        this.itemGroupId = itemGroupId;
        this.itemGroupName = itemGroupName;
        this.itemGroupMark = itemGroupMark;
        this.numberOfItems = numberOfItems;
        this.numberOfCountedItems = numberOfCountedItems;
        this.numberOfIndividualItems = numberOfIndividualItems;
        this.individualItems = individualItems;
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

            if(item.getMark() != null || item.getNotes() != null) {
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
                ItemGrouping itemGrouping = new ItemGrouping(
                        key,
                        value,
                        itemGroupMarks.get(key),
                        countedItems + individualItems.size(),
                        countedItems,
                        (long) individualItems.size(),
                        individualItems
                );
                grouping.add(itemGrouping);
        });

        return grouping;
    }
}
