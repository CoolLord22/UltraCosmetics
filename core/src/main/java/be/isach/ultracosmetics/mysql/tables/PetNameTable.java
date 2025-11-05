package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.mysql.column.*;
import be.isach.ultracosmetics.mysql.query.InnerJoin;
import be.isach.ultracosmetics.mysql.query.InsertQuery;
import be.isach.ultracosmetics.mysql.query.InsertValue;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PetNameTable extends Table {
    private final PlayerDataTable playerData;
    private final CosmeticTable cosmeticTable;

    public PetNameTable(DataSource dataSource, String name, PlayerDataTable playerData, CosmeticTable cosmeticTable) {
        super(dataSource, name);
        this.playerData = playerData;
        this.cosmeticTable = cosmeticTable;
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new UUIDColumn());
        tableInfo.add(new VirtualUUIDColumn());
        tableInfo.add(new Column<>("id", "INTEGER NOT NULL", Integer.class));
        tableInfo.add(new StringColumn("name", 256, false));
        tableInfo.add(new ForeignKeyConstraint("uuid", playerData.getWrappedName(), "uuid"));
        tableInfo.add(new ForeignKeyConstraint("id", cosmeticTable.getWrappedName(), "id"));
        tableInfo.add(new UniqueConstraint("uuid", "id"));
    }

    public String getPetName(UUID uuid, PetType type) {
        return select("name").uuid(uuid).where(cosmeticTable.subqueryFor(type, false)).asString();
    }

    public Map<PetType, String> getAllPetNames(UUID uuid) {
        return select("name, type").uuid(uuid).innerJoin(new InnerJoin(cosmeticTable.getWrappedName(), "id")).getResults(r -> {
            Map<PetType, String> names = new HashMap<>();
            while (r.next()) {
                String name = r.getString("name");
                ifParseable(Category.PETS, r.getString("type"), (c, t) -> names.put((PetType) t, name));
            }
            return names;
        }, true);
    }

    public void setPetName(UUID uuid, PetType type, String name) {
        if (name == null) {
            removePetName(uuid, type);
            return;
        }
        insert("uuid", "id", "name").insert(insertUUID(uuid), cosmeticTable.subqueryFor(type, true), new InsertValue(name))
                .updateOnDuplicate().execute();
    }

    public void removePetName(UUID uuid, PetType type) {
        delete().uuid(uuid).where(cosmeticTable.subqueryFor(type, false)).execute();
    }

    public void setAllPetNames(UUID uuid, Map<PetType, String> names) {
        delete().uuid(uuid).execute();
        if (names.size() == 0) return;
        InsertQuery query = insert("uuid", "id", "name");
        InsertValue uuidVal = insertUUID(uuid);
        for (Entry<PetType, String> entry : names.entrySet()) {
            if (entry.getValue() == null) continue;
            query.insert(uuidVal, cosmeticTable.subqueryFor(entry.getKey(), true), new InsertValue(entry.getValue()));
        }
        query.updateOnDuplicate().execute();
    }
}
