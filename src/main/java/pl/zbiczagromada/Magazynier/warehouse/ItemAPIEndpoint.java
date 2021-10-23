package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemGroupNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.StorageUnitNotEmptyException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.StorageUnitNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
