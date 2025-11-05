package be.isach.ultracosmetics.mysql.query;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.mysql.column.Column;
import be.isach.ultracosmetics.mysql.tables.Table;
import be.isach.ultracosmetics.mysql.tables.TableInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StandardQuery {
    protected final Table table;
    protected final String command;
    protected final List<ClauseItem> whereItems = new ArrayList<>();
    protected final List<ClauseItem> setItems = new ArrayList<>();
    protected InnerJoin innerJoin;
    protected boolean or = false;
    protected boolean unsafe = false;

    public StandardQuery(Table table, String command) {
        this.table = table;
        this.command = command + " " + table.getWrappedName();
    }

    public StandardQuery where(ClauseItem clauseItem) {
        whereItems.add(clauseItem);
        return this;
    }

    public StandardQuery where(String key, Object value) {
        return where(new ClauseItemLiteral(key, value));
    }

    public StandardQuery uuid(UUID uuid) {
        return where(new ClauseItemLiteral("uuid", Table.binaryUUID(uuid)));
    }

    public StandardQuery set(ClauseItem clauseItem) {
        setItems.add(clauseItem);
        return this;
    }

    public StandardQuery set(String key, Object value) {
        return set(new ClauseItemLiteral(key, value));
    }

    public StandardQuery innerJoin(InnerJoin join) {
        this.innerJoin = join;
        return this;
    }

    /**
     * Joins conditions by OR instead of AND,
     * not including the first condition, like:
     * <p>
     * WHERE uuid = x'...' AND (id = 1 OR id = 2 ... )
     *
     * @return self
     */
    public StandardQuery andOr() {
        or = true;
        return this;
    }

    /**
     * Overrides the requirement that all queries have a WHERE clause.
     *
     * @return self
     */
    public StandardQuery unsafe() {
        unsafe = true;
        return this;
    }

    /**
     * Weird and janky because when a PreparedStatement is closed,
     * its ResultSet is also closed. To work around this, you must
     * pass a function as a parameter that returns whatever the
     * getResults() method as a whole should return (type parameters ensure same type)
     *
     * @param <T>           Return type, determined by function passed in.
     * @param processResult Function to process ResultSet.
     * @param multiRow      If true, will not preemptively call next()
     * @return Whatever processResult() returns.
     */
    public <T> T getResults(ResultGetter<T> processResult, boolean multiRow) {
        if (whereItems.size() == 0 && !unsafe) {
            throw new IllegalStateException("Should not execute non-INSERT query without WHERE clause");
        }
        StringBuilder sql = new StringBuilder(command);
        List<Object> objects = new ArrayList<>();
        addClause(sql, "SET", ", ", setItems, objects);
        if (innerJoin != null) {
            sql.append(" ").append(innerJoin.toSQL(table.getWrappedName()));
        }
        if (or) {
            sql.append(" WHERE ").append(whereItems.get(0).toSQL(objects)).append(" AND (");
            addClause(sql, null, " OR ", whereItems, objects);
            sql.append(")");
        } else {
            addClause(sql, "WHERE", " AND ", whereItems, objects);
        }

        printStringified(sql, objects);
        try (Connection connection = table.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < objects.size(); i++) {
                statement.setObject(i + 1, objects.get(i));
            }
            if (processResult == null) {
                statement.executeUpdate();
                return null;
            }
            ResultSet result = statement.executeQuery();
            if (!multiRow) {
                // yes, this is required if you want to be able to use getObject or whatever immediately
                result.next();
            }
            return processResult.process(result);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void execute() {
        getResults(null, false);
    }

    public boolean exists() {
        return getResults(ResultSet::next, false);
    }

    public int asInt() {
        return getResults(r -> r.getInt(1), false);
    }

    public boolean asBool() {
        return getResults(r -> r.getBoolean(1), false);
    }

    public String asString() {
        return getResults(r -> r.getString(1), false);
    }

    public Map<String, Object> getWholeRow() {
        return getResults(r -> {
            Map<String, Object> values = new HashMap<>();
            for (TableInfo info : table.getTableInfo()) {
                if (info instanceof Column<?>) {
                    values.put(((Column<?>) info).getName(), ((Column<?>) info).getValue(r));
                }
            }
            return values;
        }, false);
    }

    private void addClause(StringBuilder sb, String clause, String joiner, List<ClauseItem> items, List<Object> objects) {
        if (items.size() == 0) return;
        if (clause != null) {
            sb.append(" ").append(clause).append(" ");
        }
        StringJoiner sj = new StringJoiner(joiner);
        for (ClauseItem item : items) {
            sj.add(item.toSQL(objects));
        }
        sb.append(sj);
    }

    public static void printStringified(StringBuilder sql, List<Object> objects) {
        if (!UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().isDebug()) return;
        String plaintext = sql.toString();
        for (Object obj : objects) {
            if (obj instanceof byte[]) {
                byte[] data = (byte[]) obj;
                StringBuilder hex = new StringBuilder("x'");
                for (byte datum : data) {
                    // `byte & 0xFF` does magic to treat it as unsigned
                    hex.append(Integer.toHexString(datum & 0xFF));
                }
                hex.append("'");
                plaintext = plaintext.replaceFirst("\\?", hex.toString());
                continue;
            }
            plaintext = plaintext.replaceFirst("\\?", obj == null ? "NULL" : obj.toString());
        }
        UltraCosmeticsData.get().getPlugin().getSmartLogger().write("Executing SQL: " + plaintext);
    }
}
