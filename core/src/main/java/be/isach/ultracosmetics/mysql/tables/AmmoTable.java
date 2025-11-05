package be.isach.ultracosmetics.mysql.tables;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.mysql.column.*;
import be.isach.ultracosmetics.mysql.query.InnerJoin;
import be.isach.ultracosmetics.mysql.query.InsertQuery;
import be.isach.ultracosmetics.mysql.query.InsertValue;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class AmmoTable extends Table {
    private final PlayerDataTable playerData;
    private final CosmeticTable cosmeticTable;

    public AmmoTable(DataSource dataSource, String name, PlayerDataTable playerData, CosmeticTable cosmeticTable) {
        super(dataSource, name);
        this.playerData = playerData;
        this.cosmeticTable = cosmeticTable;
    }

    @Override
    public void setupTableInfo() {
        tableInfo.add(new UUIDColumn());
        tableInfo.add(new VirtualUUIDColumn());
        tableInfo.add(new Column<>("id", "INTEGER NOT NULL", Integer.class));
        tableInfo.add(new Column<>("ammo", "INTEGER NOT NULL DEFAULT 0", Integer.class));
        tableInfo.add(new ForeignKeyConstraint("uuid", playerData.getWrappedName(), "uuid"));
        tableInfo.add(new ForeignKeyConstraint("id", cosmeticTable.getWrappedName(), "id"));
        tableInfo.add(new UniqueConstraint("uuid", "id"));
    }

    public int getAmmo(UUID uuid, GadgetType type) {
        return select("ammo").uuid(uuid).where(cosmeticTable.subqueryFor(type, false)).asInt();
    }

    public Map<GadgetType, Integer> getAllAmmo(UUID uuid) {
        return select("ammo, type").uuid(uuid).innerJoin(new InnerJoin(cosmeticTable.getWrappedName(), "id")).getResults(r -> {
            Map<GadgetType, Integer> ammo = new HashMap<>();
            while (r.next()) {
                int ammoAmount = r.getInt("ammo");
                ifParseable(Category.GADGETS, r.getString("type"), (c, t) -> ammo.put((GadgetType) t, ammoAmount));
            }
            return ammo;
        }, true);
    }

    public void setAmmo(UUID uuid, GadgetType type, int amount) {
        insert("uuid", "id", "ammo").insert(insertUUID(uuid), cosmeticTable.subqueryFor(type, true), new InsertValue(amount))
                .updateOnDuplicate().execute();
    }

    public void setAllAmmo(UUID uuid, Map<GadgetType, Integer> ammo) {
        delete().uuid(uuid).execute();
        if (ammo.size() == 0) return;
        InsertQuery query = insert("uuid", "id", "ammo");
        InsertValue uuidVal = insertUUID(uuid);
        for (Entry<GadgetType, Integer> entry : ammo.entrySet()) {
            if (entry.getValue() == null || entry.getValue() == 0) continue;
            query.insert(uuidVal, cosmeticTable.subqueryFor(entry.getKey(), true), new InsertValue(entry.getValue()));
        }
        query.updateOnDuplicate().execute();
    }
}
