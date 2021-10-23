package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemGroupNotEmptyException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemGroupNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.WarehouseNotEmptyException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.WarehouseNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(path = "/warehouse")
public class WarehouseAPIEndpoint {
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
    public Warehouse getWarehouse(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        return warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFoundException(id));
    }

    @PostMapping(
            path = "/new"
    )
    @Transactional
    public Warehouse addWarehouse(@RequestBody Warehouse request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();

        if(name == null) throw new InvalidRequestException(List.of("name"));

        Warehouse warehouse = new Warehouse(name, location, description);

        return warehouseRepository.saveAndFlush(warehouse);
    }

    @PutMapping(
            path = "/edit/{id}"
    )
    @Transactional
    public Warehouse editWarehouse(@PathVariable Long id, @RequestBody Warehouse request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String location = request.getLocation();
        String description = request.getDescription();

        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFoundException(id));

        if(name != null) warehouse.setName(name);
        if(location != null) warehouse.setName(location);
        if(description != null) warehouse.setName(description);

        return warehouseRepository.saveAndFlush(warehouse);
    }

    @DeleteMapping(
            path = "/delete/{id}"
    )
    @Transactional
    public void deleteWarehouse(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        //StorageUnit storageUnit = storageUnitRepository.findById(id).orElseThrow(() -> new StorageUnitNotFoundException(id));
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFoundException(id));

        if(!warehouse.getStorageUnits().isEmpty()) throw new WarehouseNotEmptyException(id);

        warehouseRepository.deleteById(id);
        warehouseRepository.flush();
    }
}
