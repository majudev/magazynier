package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemGroupNotEmptyException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.ItemGroupNotFoundException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.StorageUnitNotEmptyException;
import pl.zbiczagromada.Magazynier.warehouse.exceptions.StorageUnitNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(path = "/itemgroup")
public class ItemGroupAPIEndpoint {
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
    public ItemGroup getItemGroup(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        return itemGroupRepository.findById(id).orElseThrow(() -> new ItemGroupNotFoundException(id));
    }

    @PostMapping(
            path = "/new"
    )
    @Transactional
    public ItemGroup addItemGroup(@RequestBody ItemGroup request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String mark = request.getMark();
        if(name == null) throw new InvalidRequestException(List.of("name"));

        ItemGroup itemGroup = new ItemGroup(name, mark);

        return itemGroupRepository.saveAndFlush(itemGroup);
    }

    @PutMapping(
            path = "/edit/{id}"
    )
    @Transactional
    public ItemGroup editItemGroup(@PathVariable Long id, @RequestBody ItemGroup request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        String name = request.getName();
        String mark = request.getMark();

        ItemGroup itemGroup = itemGroupRepository.findById(id).orElseThrow(() -> new ItemGroupNotFoundException(id));

        if(name != null) itemGroup.setName(name);
        if(mark != null) itemGroup.setMark(mark);

        return itemGroupRepository.saveAndFlush(itemGroup);
    }

    @DeleteMapping(
            path = "/delete/{id}"
    )
    @Transactional
    public void deleteItemGroup(@PathVariable Long id, HttpSession session){
        User user = userCache.getUserFromSession(session);
        //if user has permissions

        ItemGroup itemGroup = itemGroupRepository.findById(id).orElseThrow(() -> new ItemGroupNotFoundException(id));

        if(!itemGroup.getItems().isEmpty()) throw new ItemGroupNotEmptyException(id);

        itemGroupRepository.deleteById(id);
        itemGroupRepository.flush();
    }
}
