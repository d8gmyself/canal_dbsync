package com.d8gmyself.dbsync.etl.load.loader.impl;

import com.d8gmyself.dbsync.etl.commons.model.EventColumn;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.load.loader.Loader;
import com.d8gmyself.dbsync.utils.BeanUtils;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 *
 * @author ZhangDuo
 */
public class DefaultLoder implements Loader {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final DataSource dataSource = (DataSource) BeanUtils.getBean("dataSource");

    @Override
    public void changeToSqls(List<EventData> eventDatas) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        for (EventData item : eventDatas) {
            if (item.getEventType().isInsert()) {
                this.initInsertSQL(item);
            } else if (item.getEventType().isUpdate()) {
                this.initUpdateSQL(item);
            } else if (item.getEventType().isDelete()) {
                this.initDeleteSQL(item);
            }
        }
        log.debug("build sql statement cost:{}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public boolean load(List<EventData> eventDatas) {
        /*final Stopwatch stopwatch = Stopwatch.createStarted();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            boolean lastAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            for (EventData item : eventDatas) {
                statement.addBatch(item.getSql());
                log.debug(item.getSql());
            }
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(lastAutoCommit);
            log.debug("execute batch sql cost:{}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return true;
        } catch (SQLException e) {
            throw new LoadException(e.getMessage(), e);
        } finally {
            CloseableUtils.close(statement);
            CloseableUtils.close(connection);
        }*/
        eventDatas.forEach(eventData -> System.out.println(eventData.getSql()));
        return true;
    }

    private String getValue(EventColumn column) {
        if (column.isNull()) {
            return "NULL";
        }
        int sqlType = column.getSqlType();
        switch (sqlType) {
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
                return "'" + escapeMySqlVarchar(column.getValue()) + "'";
            default:
                return column.getValue();
        }
    }

    private String getOldValue(EventColumn column) {
        int sqlType = column.getSqlType();
        switch (sqlType) {
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
                return "'" + column.getOldValue() + "'";
            default:
                return column.getOldValue();
        }
    }

    private void initInsertSQL(EventData eventData) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append("`").append(eventData.getSchemaName()).append("`.`").append(eventData.getTableName()).append("`");
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder onDuplicate = new StringBuilder();
        for (EventColumn column : eventData.getColumns()) {
            columns.append(", `").append(column.getName()).append("`");
            values.append(", ").append(getValue(column));
            if (!column.isKey()) {
                onDuplicate.append(", `").append(column.getName()).append("` = VALUES(`").append(
                        column.getName()).append("`)");
            }
        }
        if (columns.length() > 0) {
            eventData.setSql(sqlBuilder.append("(").append(columns.substring(1)).append(") VALUES (").append(values.substring(1))
                    .append(") ON DUPLICATE KEY UPDATE ").append(onDuplicate.substring(1)).toString());
        }
    }

    private void initUpdateSQL(EventData eventData) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append("`").append(eventData.getSchemaName()).append("`.`").append(eventData.getTableName())
                .append("`").append(" SET ");
        StringBuilder valueSets = new StringBuilder();
        StringBuilder where = new StringBuilder();
        for (EventColumn column : eventData.getColumns()) {
            if (column.isKey()) {
                where.append(" and `").append(column.getName()).append("` = ").append(getOldValue(column));
                continue;
            }
            if (!column.isNull()) {
                valueSets.append(", `").append(column.getName()).append("` = ").append(getValue(column));
            } else {
                valueSets.append(", `").append(column.getName()).append("` = NULL");
            }
        }
        if (valueSets.length() > 0 && where.length() > 0) {
            eventData.setSql(sqlBuilder.append(valueSets.substring(1)).append(" WHERE ").append(where.toString().replaceFirst("and", ""))
                    .toString());
        }
    }

    private void initDeleteSQL(EventData eventData) {
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        sqlBuilder.append("`").append(eventData.getSchemaName()).append("`.`").append(eventData.getTableName())
                .append("`");
        StringBuilder where = new StringBuilder();
        for (EventColumn column : eventData.getColumns()) {
            if (column.isKey()) {
                where.append(" and `").append(column.getName()).append("` = ").append(getValue(column));
            }
        }
        if (where.length() > 0) {
            eventData.setSql(sqlBuilder.append(" WHERE ").append(where.toString().replaceFirst("and", ""))
                    .toString());
        }
    }

    /**
     * 转义关键字
     *
     * @param varchar 要转义的内容
     * @return 转义后的内容
     */
    private String escapeMySqlVarchar(String varchar) {
        char[] chars = new char[varchar.length()];
        varchar.getChars(0, varchar.length(), chars, 0);
        List<Character> list = Lists.newArrayList();
        for (char c : chars) {
            switch (c) {
                case 0: /* Must be escaped for 'mysql' */
                    list.add('\\');
                    list.add('0');
                    break;

                case '\n': /* Must be escaped for logs */
                    list.add('\\');
                    list.add('n');
                    break;
                case '\r':
                    list.add('\\');
                    list.add('r');
                    break;
                case '\\':
                    list.add('\\');
                    list.add('\\');
                    break;
                case '\'':
                    list.add('\\');
                    list.add('\'');
                    break;
                case '"': /* Better safe than sorry */
                    list.add('\\');
                    list.add('"');
                    break;
                case '\032': /* This gives problems on Win32 */
                    list.add('\\');
                    list.add('Z');
                    break;
                default:
                    list.add(c);
            }
        }
        char[] newChars = new char[list.size()];
        for (int n = 0, max = list.size(); n < max; n++) {
            newChars[n] = list.get(n);
        }
        return new String(newChars);
    }

}
