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
package com.facebook.presto.operator.aggregation;

import com.facebook.presto.operator.aggregation.state.InitialLongValue;
import com.facebook.presto.operator.aggregation.state.NullableLongState;
import com.facebook.presto.spi.block.Block;

import static com.facebook.presto.spi.type.BigintType.BIGINT;

public class LongMinAggregation
        extends AbstractSimpleAggregationFunction<LongMinAggregation.LongMinState>
{
    public static final LongMinAggregation LONG_MIN = new LongMinAggregation();

    public LongMinAggregation()
    {
        super(BIGINT, BIGINT, BIGINT);
    }

    @Override
    public void processInput(LongMinState state, Block block, int index)
    {
        state.setNull(false);
        state.setLong(Math.min(state.getLong(), block.getLong(index)));
    }

    public interface LongMinState
            extends NullableLongState
    {
        @Override
        @InitialLongValue(Long.MAX_VALUE)
        long getLong();
    }
}
