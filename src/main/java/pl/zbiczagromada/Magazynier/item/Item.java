package pl.zbiczagromada.Magazynier.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.zbiczagromada.Magazynier.itemgroup.ItemGroup;
import pl.zbiczagromada.Magazynier.storageunit.StorageUnit;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter @Setter
public class Item {
    @Id
    @SequenceGenerator(
            name = "items_sequence",
            sequenceName = "items_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "items_sequence",
            strategy = GenerationType.SEQUENCE
    )

    @Column(name = "item_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true)
    private String mark;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "itemgroup_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ItemGroup itemGroup;

    @ManyToOne
    @JoinColumn(name = "storageunit_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private StorageUnit storageUnit;

    private Item() {}

    public Item(String mark, ItemGroup itemGroup, StorageUnit storageUnit, String notes) {
        this.mark = mark;
        this.itemGroup = itemGroup;
        this.storageUnit = storageUnit;
        this.notes = notes;
    }

    public Long getItemGroupId(){
        if(this.itemGroup == null) return null;
        return this.itemGroup.getId();
    }

    public Long getStorageUnitId(){
        if(this.storageUnit == null) return null;
        return this.storageUnit.getId();
    }
}
