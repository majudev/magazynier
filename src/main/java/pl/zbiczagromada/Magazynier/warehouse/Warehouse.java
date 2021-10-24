package pl.zbiczagromada.Magazynier.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.zbiczagromada.Magazynier.storageunit.StorageUnit;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter @Setter
public class Warehouse {
    @Id
    @SequenceGenerator(
            name = "warehouses_sequence",
            sequenceName = "warehouses_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "warehouses_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "warehouse_id")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String location;

    private String description;

    @OneToMany(mappedBy = "warehouse",
            cascade = CascadeType.ALL
    )
    @Setter(AccessLevel.NONE)
    private List<StorageUnit> storageUnits;

    private Warehouse() {}

    public Warehouse(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
