package pl.zbiczagromada.Magazynier.storageunit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.item.ItemRepository;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroupRepository;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.warehouse.Warehouse;
import pl.zbiczagromada.Magazynier.warehouse.WarehouseRepository;
import pl.zbiczagromada.Magazynier.storageunit.exceptions.StorageUnitNotEmptyException;
import pl.zbiczagromada.Magazynier.storageunit.exceptions.StorageUnitNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.WarehouseNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

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

    @GetMapping(
            path = "/get/{id}"
    )
    public StorageUnit getStorageUnit(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        return storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));
    }

    @PostMapping(
            path = "/new"
    )
    @Transactional
    public StorageUnit addStorageUnit(@RequestBody StorageUnit request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();

        if(name == null) throw new InvalidRequestException(List.of("name"));
        if(request.getWarehouseId() == null) throw new InvalidRequestException(List.of("warehouse"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new WarehouseNotFoundException(request.getWarehouseId()));

        StorageUnit storageUnit = new StorageUnit(name, location, description, warehouse);

        return storageUnitRepository.saveAndFlush(storageUnit);
    }

    @PutMapping(
            path = "/edit/{id}"
    )
    @Transactional
    public StorageUnit editStorageUnit(@PathVariable Long id, @RequestBody StorageUnit request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();
        /*Warehouse warehouse = null;
        if(request.getWarehouseId() != null){
            warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new WarehouseNotFoundException(request.getWarehouseId()));
        }*/

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        if(name != null) storageUnit.setName(name);
        if(location != null) storageUnit.setLocation(location);
        if(description != null) storageUnit.setDescription(description);
        //if(warehouse != null) storageUnit.setWarehouse(warehouse);

        return storageUnitRepository.saveAndFlush(storageUnit);
    }

    @PutMapping(
            path = "/move/{id}/{newWarehouseId}"
    )
    @Transactional
    public StorageUnit moveStorageUnit(@PathVariable Long id, @PathVariable Long newWarehouseId, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        Warehouse newWarehouse = warehouseRepository.findById(newWarehouseId).orElseThrow(() -> new WarehouseNotFoundException(newWarehouseId));

        StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));

        storageUnit.setWarehouse(newWarehouse);

        return storageUnitRepository.saveAndFlush(storageUnit);
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
