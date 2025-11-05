package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.mysql.column.*;
import be.isach.ultracosmetics.mysql.query.InnerJoin;
import be.isach.ultracosmetics.mysql.query.InsertQuery;
import be.isach.ultracosmetics.mysql.query.InsertValue;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class EquippedTable extends Table {
    private final PlayerDataTable playerData;
    private final CosmeticTable cosmeticTable;

    public EquippedTable(DataSource dataSource, String name, PlayerDataTable playerData, CosmeticTable cosmeticTable) {
        super(dataSource, name);
        this.playerData = playerData;
        this.cosmeticTable = cosmeticTable;
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new UUIDColumn());
        tableInfo.add(new VirtualUUIDColumn());
        tableInfo.add(new Column<>("id", "INTEGER NOT NULL", Integer.class));
        tableInfo.add(new StringColumn("category", 32, true));
        tableInfo.add(new ForeignKeyConstraint("uuid", playerData.getWrappedName(), "uuid"));
        // `category` is not a unique key in the cosmetics table, so we need to pair it with `id`
        tableInfo.add(new ForeignKeyConstraint("id,category", cosmeticTable.getWrappedName(), "id,category"));
        tableInfo.add(new UniqueConstraint("uuid", "category"));
    }

    public Map<Category, CosmeticType<?>> getEquipped(UUID uuid) {
        return select(getWrappedName() + ".category, type").uuid(uuid).innerJoin(new InnerJoin(cosmeticTable.getWrappedName(), "id")).getResults(r -> {
            Map<Category, CosmeticType<?>> equipped = new HashMap<>();
            while (r.next()) {
                ifParseable(r.getString("category"), r.getString("type"), equipped::put);
            }
            return equipped;
        }, true);
    }

    public void setEquipped(UUID uuid, CosmeticType<?> type) {
        insert("uuid", "id", "category").insert(insertUUID(uuid), cosmeticTable.subqueryFor(type, true), new InsertValue(cleanCategoryName(type)))
                .updateOnDuplicate().execute();
    }

    public void unsetEquipped(UUID uuid, Category cat) {
        delete().uuid(uuid).where("category", cleanCategoryName(cat)).execute();
    }

    public void clearAllEquipped(UUID uuid) {
        delete().uuid(uuid).execute();
    }

    /*
     * Only used for migration
     */
    public void setAllEquipped(UUID uuid, Map<Category, CosmeticType<?>> equipped) {
        clearAllEquipped(uuid);
        if (equipped.isEmpty()) return;
        InsertQuery query = insert("uuid", "id", "category");
        InsertValue uuidVal = insertUUID(uuid);
        for (Entry<Category, CosmeticType<?>> entry : equipped.entrySet()) {
            query.insert(uuidVal, cosmeticTable.subqueryFor(entry.getValue(), true), new InsertValue(cleanCategoryName(entry.getKey())));
        }
        query.updateOnDuplicate().execute();
    }
}
