package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.mysql.column.*;
import be.isach.ultracosmetics.mysql.query.InnerJoin;
import be.isach.ultracosmetics.mysql.query.InsertQuery;
import be.isach.ultracosmetics.mysql.query.InsertValue;
import be.isach.ultracosmetics.mysql.query.StandardQuery;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UnlockedTable extends Table {
    private final PlayerDataTable playerData;
    private final CosmeticTable cosmeticTable;

    public UnlockedTable(DataSource dataSource, String name, PlayerDataTable playerData, CosmeticTable cosmeticTable) {
        super(dataSource, name);
        this.playerData = playerData;
        this.cosmeticTable = cosmeticTable;
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new UUIDColumn());
        tableInfo.add(new VirtualUUIDColumn());
        tableInfo.add(new Column<>("id", "INTEGER NOT NULL", Integer.class));
        tableInfo.add(new UniqueConstraint("uuid", "id"));
        tableInfo.add(new ForeignKeyConstraint("uuid", playerData.getWrappedName(), "uuid"));
        tableInfo.add(new ForeignKeyConstraint("id", cosmeticTable.getWrappedName(), "id"));
    }

    public boolean hasUnlocked(UUID uuid, CosmeticType<?> type) {
        return selectVoid().uuid(uuid).where(cosmeticTable.subqueryFor(type, false)).exists();
    }

    public Set<CosmeticType<?>> getAllUnlocked(UUID uuid) {
        return select("category, type").uuid(uuid).innerJoin(new InnerJoin(cosmeticTable.getWrappedName(), "id")).getResults(r -> {
            Set<CosmeticType<?>> unlocked = new HashSet<>();
            while (r.next()) {
                ifParseable(r.getString("category"), r.getString("type"), (c, t) -> unlocked.add(t));
            }
            return unlocked;
        }, true);
    }

    public void setUnlocked(UUID uuid, Set<CosmeticType<?>> types) {
        InsertQuery query = insertIgnore("uuid", "id");
        types.forEach(t -> query.insert(insertUUID(uuid), cosmeticTable.subqueryFor(t, true)));
        query.execute();
    }

    public void unsetUnlocked(UUID uuid, Set<CosmeticType<?>> types) {
        StandardQuery query = delete().uuid(uuid).andOr();
        types.forEach(t -> query.where(cosmeticTable.subqueryFor(t, false)));
        query.execute();
    }

    // Deletes all, then inserts all unlocked
    public void setAllUnlocked(UUID uuid, Set<CosmeticType<?>> unlocked) {
        delete().uuid(uuid).execute();
        if (unlocked.size() == 0) return;
        InsertQuery query = insert("uuid", "id");
        InsertValue uuidValue = insertUUID(uuid);
        for (CosmeticType<?> type : unlocked) {
            query.insert(uuidValue, cosmeticTable.subqueryFor(type, true));
        }
        query.execute();
    }
}
