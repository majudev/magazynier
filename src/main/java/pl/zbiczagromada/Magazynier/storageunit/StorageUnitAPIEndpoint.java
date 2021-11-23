package pl.zbiczagromada.Magazynier.storageunit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.FieldProjector;
import pl.zbiczagromada.Magazynier.exceptions.EmptyPropertyException;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.item.Item;
import pl.zbiczagromada.Magazynier.item.ItemRepository;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroup;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroupRepository;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGrouping;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.warehouse.Warehouse;
import pl.zbiczagromada.Magazynier.warehouse.WarehouseRepository;
import pl.zbiczagromada.Magazynier.storageunit.exceptions.StorageUnitNotEmptyException;
import pl.zbiczagromada.Magazynier.storageunit.exceptions.StorageUnitNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.WarehouseNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/storageunit")
public class StorageUnitAPIEndpoint {
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

    String[] slimResponseFields = {
            "id",
            "name",
            "location",
            "description",
            "warehouseId"
    };

    @GetMapping(
            path = "/get/{id}"
    )
    public StorageUnit getStorageUnit(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        return storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));
    }

    @GetMapping(
            path = "/meta/{id}"
    )
    public Map<String, Object> getStorageUnitMeta(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        return FieldProjector.project(storageUnit, slimResponseFields);
    }

    @GetMapping(
            path = "/items/{id}"
    )
    public List<Item> getStorageUnitItems(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        return storageUnit.getItems();
    }

    @GetMapping(
            path = "/items/grouped/{id}"
    )
    public List<ItemGrouping> getStorageUnitItemsGrouped(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));
        //itemGroupRepository.findAllByStorageUnitIdPastItem(storageUnit.getId());
        return ItemGrouping.createGroupingFromStorageUnit(storageUnit);
    }

    @PostMapping(
            path = "/new/{warehouseId}"
    )
    @Transactional
    public StorageUnit addStorageUnit(@PathVariable Long warehouseId, @RequestBody StorageUnit request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();

        if(name == null) throw new InvalidRequestException(List.of("name"));
        if(name.isEmpty()) throw new EmptyPropertyException(List.of("name"));

        if(location.isEmpty()) location = null;
        if(description.isEmpty()) description = null;

        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

        StorageUnit storageUnit = new StorageUnit(name, location, description, warehouse);

        return storageUnitRepository.saveAndFlush(storageUnit);
    }

    @PutMapping(
            path = "/edit/{id}"
    )
    @Transactional
    public Map<String, Object> editStorageUnit(@PathVariable Long id, @RequestBody StorageUnit request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        if(name != null){
            if(name.isEmpty()) throw new EmptyPropertyException(List.of("name"));
            storageUnit.setName(name);
        }
        if(location != null){
            if(location.isEmpty()) storageUnit.setLocation(null);
            else storageUnit.setLocation(location);
        }
        if(description != null){
            if(description.isEmpty()) storageUnit.setDescription(null);
            else storageUnit.setDescription(description);
        }

        storageUnit = storageUnitRepository.saveAndFlush(storageUnit);

        return FieldProjector.project(storageUnit, slimResponseFields);
    }

    @PutMapping(
            path = "/move/{id}/{newWarehouseId}"
    )
    @Transactional
    public Map<String, Object> moveStorageUnit(@PathVariable Long id, @PathVariable Long newWarehouseId, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        Warehouse newWarehouse = warehouseRepository.findById(newWarehouseId).orElseThrow(() -> new WarehouseNotFoundException(newWarehouseId));

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        storageUnit.setWarehouse(newWarehouse);
        storageUnit.setLocation(null);

        storageUnit = storageUnitRepository.saveAndFlush(storageUnit);

        return FieldProjector.project(storageUnit, slimResponseFields);
    }

    @DeleteMapping(
            path = "/delete/{id}"
    )
    @Transactional
    public void deleteStorageUnit(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        if(!storageUnit.getItems().isEmpty()) throw new StorageUnitNotEmptyException(id);

        storageUnitRepository.deleteById(id);
        storageUnitRepository.flush();
    }
}
