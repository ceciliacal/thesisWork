package flink;

import data.Event;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Accumulators collect distributed statistics or aggregates in a from user functions and operators.
* Each parallel instance creates and updates its own accumulator object, and the different parallel
* instances of the accumulator are later merged. merged by the system at the end of the job. The
* result can be obtained from the result of a job execution, or from the web runtime monitor.
*
* <p>The accumulators are inspired by the Hadoop/MapReduce counters.
*
* <p>The type added to the accumulator might differ from the type returned. This is the case e.g.
* for a set-accumulator: We add single objects, but the result is a set of objects.
*
* @param <V> Type of values that are added to the accumulator
* @param <R> Type of the accumulator result as it will be reported to the client
*/

public class MyAccumulator implements Serializable {

    private Map<String, Float> lastPricePerSymbol;      //K:symbol - V:last price
    private Map<String, List<Integer>> symbolInBatches; //K:symbol - V:list of batches num

    private Map<Integer, Timestamp> timeBatch;           //K:batchNum - V:timestamp che prendo dal Map;

    public MyAccumulator(){
        this.lastPricePerSymbol = new HashMap<>();
        this.symbolInBatches = new HashMap<>();
        this.timeBatch = new HashMap<>();
    }

    public void add(Event value) {

        List<Integer> batches;

        if (lastPricePerSymbol==null){
            lastPricePerSymbol = new HashMap<>();
        }
        if (symbolInBatches==null){
            symbolInBatches = new HashMap<>();
        }

        if (symbolInBatches.containsKey(value.getSymbol())){
            batches = symbolInBatches.get(value.getSymbol());
            if (!batches.contains(value.getBatch())){
                batches.add(value.getBatch());
            }
        } else {
            batches = new ArrayList<>();
            batches.add(value.getBatch());
        }
        //System.out.println("symbolInBatches: "+symbolInBatches);
        symbolInBatches.put(value.getSymbol(),batches);
        lastPricePerSymbol.put(value.getSymbol(), value.getLastTradePrice());

        //timeBatch.put(value.getSymbol()+value.getBatch(), new Timestamp(System.currentTimeMillis()));
        if (value.getCurrTime()!=null){
            //todo: metti che chiave di timeBatch è Integer e non String ! è inutile
            timeBatch.put(value.getBatch(), value.getCurrTime()._2); //ts che prendo dal Map lo salvo in timeBatch
                                                                                // (che è attributo dell'Accumulator),
                                                                                // cioè la prima volta che ho visto questo batch
            //così io alla fine mi ritrovo che l'accumulator ha un hashmap con tutti i tempi iniziali di ogni batch
            //System.out.println("timeBatch= "+timeBatch+"  currValue= "+value.getSymbol());
        }


    }


    public Map<String, Float> getLastPricePerSymbol() {
        return lastPricePerSymbol;
    }

    public void setLastPricePerSymbol(Map<String, Float> lastPricePerSymbol) {
        this.lastPricePerSymbol = lastPricePerSymbol;
    }

    public Map<String, List<Integer>> getSymbolInBatches() {
        return symbolInBatches;
    }

    public void setSymbolInBatches(Map<String, List<Integer>> symbolInBatches) {
        this.symbolInBatches = symbolInBatches;
    }

    public Map<Integer, Timestamp> getTimeBatch() {
        return timeBatch;
    }

    public void setTimeBatch(Map<Integer, Timestamp> timeBatch) {
        this.timeBatch = timeBatch;
    }
}

