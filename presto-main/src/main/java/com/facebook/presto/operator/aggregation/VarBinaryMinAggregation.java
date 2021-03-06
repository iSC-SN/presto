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

import com.facebook.presto.operator.aggregation.state.SliceState;
import com.facebook.presto.spi.block.Block;
import io.airlift.slice.Slice;

import static com.facebook.presto.spi.type.VarcharType.VARCHAR;

public class VarBinaryMinAggregation
        extends AbstractSimpleAggregationFunction<SliceState>
{
    public static final VarBinaryMinAggregation VAR_BINARY_MIN = new VarBinaryMinAggregation();

    public VarBinaryMinAggregation()
    {
        super(VARCHAR, VARCHAR, VARCHAR);
    }

    @Override
    protected void processInput(SliceState state, Block block, int index)
    {
        state.setSlice(min(state.getSlice(), block.getSlice(index)));
    }

    private static Slice min(Slice a, Slice b)
    {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) < 0 ? a : b;
    }
}
