package pl.zbiczagromada.Magazynier.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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

    @Transient
    @Getter(lazy = true)
    private final Long itemGroupId = this.getItemGroupIdLazy();

    @Transient
    @Getter(lazy = true)
    private final Long storageUnitId = this.getStorageUnitIdLazy();

    private Item() {}

    public Item(String mark, ItemGroup itemGroup, StorageUnit storageUnit, String notes) {
        this.mark = mark;
        this.itemGroup = itemGroup;
        this.storageUnit = storageUnit;
        this.notes = notes;
    }

    private Long getItemGroupIdLazy(){
        if(this.itemGroup == null) return null;
        return this.itemGroup.getId();
    }

    private Long getStorageUnitIdLazy(){
        if(this.storageUnit == null) return null;
        return this.storageUnit.getId();
    }
}
