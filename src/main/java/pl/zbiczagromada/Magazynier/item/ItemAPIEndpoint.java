package pl.zbiczagromada.Magazynier.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroup;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroupRepository;
import pl.zbiczagromada.Magazynier.itemgroup.exceptions.NotEnoughCountedItemsInItemGroup;
import pl.zbiczagromada.Magazynier.storageunit.StorageUnit;
import pl.zbiczagromada.Magazynier.storageunit.StorageUnitRepository;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.warehouse.*;
import pl.zbiczagromada.Magazynier.itemgroup.exceptions.ItemGroupNotFoundException;
import pl.zbiczagromada.Magazynier.item.exceptions.ItemNotFoundException;
import pl.zbiczagromada.Magazynier.storageunit.exceptions.StorageUnitNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/item")
public class ItemAPIEndpoint {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemGroupRepository itemGroupRepository;
    @Autowired
    private StorageUnitRepository storageUnitRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private UserCacheService userCache;

    @GetMapping(
            path = "/get/{id}"
    )
    public Item getItem(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    @PostMapping(
            path = "/new/{itemGroupId}/{storageUnitId}"
    )
    @Transactional
    public Item addItem(@PathVariable("itemGroupId") Long itemGroupId, @PathVariable("storageUnitId") Long storageUnitId, @RequestBody(required = false) Item request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        ItemGroup itemGroup = itemGroupRepository.findById(itemGroupId).orElseThrow(() -> new ItemGroupNotFoundException(itemGroupId));
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).orElseThrow(() -> new StorageUnitNotFoundException(storageUnitId));

        String mark = null;
        String notes = null;
        if(request != null){
            mark = request.getMark();
            notes = request.getNotes();
        }

        Item item = new Item(mark, itemGroup, storageUnit, notes);

        return itemRepository.saveAndFlush(item);
    }

    @PutMapping(
            path = "/move/{itemId}/to/{newStorageUnitId}"
    )
    @Transactional
    public Item moveItem(@PathVariable("itemId") Long itemId, @PathVariable("newStorageUnitId") Long newStorageUnitId, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        StorageUnit storageUnit = storageUnitRepository.findById(newStorageUnitId).orElseThrow(() -> new StorageUnitNotFoundException(newStorageUnitId));

        item.setStorageUnit(storageUnit);

        return itemRepository.saveAndFlush(item);
    }

    @PutMapping(
            path = "/move/{count}/unmarked/{itemGroupId}/from/{storageUnitId}/to/{newStorageUnitId}"
    )
    @Transactional(
            rollbackOn = {
                    ResponseStatusException.class,
                    NotEnoughCountedItemsInItemGroup.class
            }
    )
    public void moveItemsUnmarked(@PathVariable("itemGroupId") Long itemGroupId, @PathVariable("storageUnitId") Long storageUnitId, @PathVariable("newStorageUnitId") Long newStorageUnitId, @PathVariable("count") Long count, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        ItemGroup itemGroup = itemGroupRepository.findById(itemGroupId).orElseThrow(() -> new ItemGroupNotFoundException(itemGroupId));
        StorageUnit newStorageUnit = storageUnitRepository.findById(newStorageUnitId).orElseThrow(() -> new StorageUnitNotFoundException(newStorageUnitId));

        Long counter = count;
        for(Item item : itemGroup.getItems()){
            if(counter == 0) break;

            if(item.getStorageUnitId() == storageUnitId && item.getMark() == null){
                item.setStorageUnit(newStorageUnit);
                //itemRepository.saveAndFlush(item);

                --counter;
            }
        }
        itemGroupRepository.saveAndFlush(itemGroup);
        if(counter != 0) throw new NotEnoughCountedItemsInItemGroup(itemGroupId);
    }

    @DeleteMapping(
            path = "/delete/{id}"
    )
    @Transactional
    public void deleteItem(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));

        itemRepository.delete(item);
        itemRepository.flush();
    }

    /*@GetMapping(
            path = "/getitemgroup/{itemId}"
    )
    public ItemGroup getItemGroup(@PathVariable Long itemId, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

        return item.getItemGroup();
    }*/
}
