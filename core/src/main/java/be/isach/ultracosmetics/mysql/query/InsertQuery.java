package be.isach.ultracosmetics.mysql.query;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.mysql.tables.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class InsertQuery {
    private final List<InsertItem[]> inserts = new ArrayList<>();
    private final Table table;
    // when true, adds IGNORE keyword which ignores the insert if a row with a matching primary key already exists.
    // used to ensure a player is present in database when joining.
    private final boolean ignore;
    private final String[] columns;
    private boolean updateOnDuplicate = false;

    public InsertQuery(Table table, boolean ignore, String... columns) {
        this.table = table;
        this.ignore = ignore;
        this.columns = columns;
    }

    public InsertQuery(Table table, String... columns) {
        this(table, false, columns);
    }

    public InsertQuery insert(InsertItem... item) {
        if (item.length != columns.length) {
            throw new IllegalArgumentException("Must have a value for every inserted column");
        }
        inserts.add(item);
        return this;
    }

    public InsertQuery insert(String... values) {
        InsertValue[] items = new InsertValue[values.length];
        for (int i = 0; i < values.length; i++) {
            items[i] = new InsertValue(values[i]);
        }
        return insert(items);
    }

    public InsertQuery updateOnDuplicate() {
        updateOnDuplicate = true;
        return this;
    }

    /**
     * Executes the query.
     * <p>
     * If no values were specified to insert, the query will not run.
     */
    public void execute() {
        // This happens regularly during migration.
        if (inserts.isEmpty()) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(
                    "Skipping query to " + table.getClass().getSimpleName() + " as no values were specified.");
            return;
        }
        StringBuilder sql = new StringBuilder("INSERT ");
        if (ignore) sql.append("IGNORE ");
        sql.append("INTO ").append(table.getWrappedName()).append(" ");
        List<Object> objects = new ArrayList<>();
        StringJoiner columns = new StringJoiner(", ", "(", ")");
        for (String col : this.columns) {
            columns.add(col);
        }
        StringJoiner values = new StringJoiner(",");
        for (InsertItem[] items : inserts) {
            StringJoiner sj = new StringJoiner(", ", "(", ")");
            for (InsertItem item : items) {
                sj.add(item.toSQL(objects));
            }
            values.add(sj.toString());
        }
        sql.append(columns).append(" VALUES ").append(values);

        if (updateOnDuplicate) {
            sql.append(" ON DUPLICATE KEY UPDATE ");
            StringJoiner update = new StringJoiner(", ");
            for (String col : this.columns) {
                update.add(col + "=VALUES(" + col + ")");
            }
            sql.append(update);
        }

        StandardQuery.printStringified(sql, objects);
        try (Connection connection = table.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < objects.size(); i++) {
                statement.setObject(i + 1, objects.get(i));
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
