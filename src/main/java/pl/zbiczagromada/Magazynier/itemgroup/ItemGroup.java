package pl.zbiczagromada.Magazynier.itemgroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.zbiczagromada.Magazynier.item.Item;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "itemgroups")
@Getter @Setter
public class ItemGroup {
    @Id
    @SequenceGenerator(
            name = "itemgroups_sequence",
            sequenceName = "itemgroups_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "itemgroups_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "itemgroup_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String mark;

    @OneToMany(mappedBy = "itemGroup")
    @Setter(AccessLevel.NONE)
    private List<Item> items;

    protected ItemGroup() {}

    public ItemGroup(String name, String mark) {
        this.name = name;
        this.mark = mark;
    }
}
