/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tpch;

import com.facebook.presto.spi.ConnectorColumnHandle;
import com.facebook.presto.spi.ConnectorRecordSetProvider;
import com.facebook.presto.spi.ConnectorSplit;
import com.facebook.presto.spi.RecordSet;
import com.google.common.collect.ImmutableList;
import io.airlift.tpch.TpchColumn;
import io.airlift.tpch.TpchEntity;
import io.airlift.tpch.TpchTable;

import java.util.List;

import static com.facebook.presto.tpch.TpchRecordSet.createTpchRecordSet;
import static com.facebook.presto.tpch.Types.checkType;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TpchRecordSetProvider
        implements ConnectorRecordSetProvider
{
    @Override
    public RecordSet getRecordSet(ConnectorSplit split, List<? extends ConnectorColumnHandle> columns)
    {
        TpchSplit tpchSplit = checkType(split, TpchSplit.class, "split");

        checkNotNull(columns, "columns is null");
        checkArgument(!columns.isEmpty(), "must provide at least one column");

        String tableName = tpchSplit.getTableHandle().getTableName();

        TpchTable<?> tpchTable = TpchTable.getTable(tableName);

        return getRecordSet(tpchTable, columns, tpchSplit.getTableHandle().getScaleFactor(), tpchSplit.getPartNumber(), tpchSplit.getTotalParts());
    }

    public <E extends TpchEntity> RecordSet getRecordSet(
            TpchTable<E> table,
            List<? extends ConnectorColumnHandle> columns,
            double scaleFactor,
            int partNumber,
            int totalParts)
    {
        ImmutableList.Builder<TpchColumn<E>> builder = ImmutableList.builder();
        for (ConnectorColumnHandle column : columns) {
            String columnName = checkType(column, TpchColumnHandle.class, "column").getColumnName();
            if (columnName.equalsIgnoreCase(TpchMetadata.ROW_NUMBER_COLUMN_NAME)) {
                builder.add(new RowNumberTpchColumn<E>());
            }
            else {
                builder.add(table.getColumn(columnName));
            }
        }

        return createTpchRecordSet(table, builder.build(), scaleFactor, partNumber + 1, totalParts);
    }

    private static class RowNumberTpchColumn<E extends TpchEntity>
            implements TpchColumn<E>
    {
        @Override
        public String getColumnName()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<?> getType()
        {
            return Long.class;
        }

        @Override
        public double getDouble(E entity)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(E entity)
        {
            return entity.getRowNumber();
        }

        @Override
        public String getString(E entity)
        {
            throw new UnsupportedOperationException();
        }
    }
}
