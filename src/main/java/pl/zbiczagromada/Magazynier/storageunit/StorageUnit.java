package pl.zbiczagromada.Magazynier.storageunit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.zbiczagromada.Magazynier.item.Item;
import pl.zbiczagromada.Magazynier.warehouse.Warehouse;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "storageunits")
@Getter @Setter
public class StorageUnit {
    @Id
    @SequenceGenerator(
            name = "storageunits_sequence",
            sequenceName = "storageunits_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "storageunits_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "storageunit_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Warehouse warehouse;

    @Transient
    @Getter(lazy = true)
    private final Long warehouseId = this.getWarehouseIdLazy();

    private String location;

    private String description;

    @OneToMany(mappedBy = "storageUnit",
            cascade = CascadeType.ALL
    )
    @Setter(AccessLevel.NONE)
    private List<Item> items;

    private StorageUnit() {}

    public StorageUnit(String name, String location, String description, Warehouse warehouse) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.warehouse = warehouse;
    }

    private Long getWarehouseIdLazy(){
        if(this.warehouse == null) return null;
        return this.warehouse.getId();
    }
}
