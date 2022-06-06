package flink;

import data.Event;
import org.apache.flink.api.common.functions.AggregateFunction;


public class MyAggregateFunction implements AggregateFunction<Event, MyAccumulator, OutputQuery> {

    @Override
    public MyAccumulator createAccumulator() {
        return new MyAccumulator();
    }


    @Override
    public MyAccumulator add(Event value, MyAccumulator accumulator) {
        accumulator.add(value);
        return accumulator;

    }

    @Override
    public OutputQuery getResult(MyAccumulator accumulator) {
        return new OutputQuery(accumulator.getLastPricePerSymbol(), accumulator.getSymbolInBatches(), accumulator.getTimeBatch());
    }

    @Override
    public MyAccumulator merge(MyAccumulator a, MyAccumulator b) {
        return a;
    }

}
