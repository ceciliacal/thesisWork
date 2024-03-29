package flink;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import scala.Tuple2;
import utils.Config;

import java.sql.Timestamp;
import java.util.*;


public class MyProcessWindowFunction extends ProcessWindowFunction<IntermediateOutput, FinalOutput, String, TimeWindow> {

    private Map<String, Integer> count;  //counts number of current window per symbol
    private Map<Tuple2<String,Integer>,Float> myEma38;   //K: <symbol,countWindow> - V: ema
    private Map<Tuple2<String,Integer>,Float> myEma100;
    private Map<String, List<Timestamp>> buyCrossovers;  //K: <symbol> - V: List of timestamps
    private Map<String, List<Timestamp>> sellCrossovers;



    @Override
    public void process(String s, ProcessWindowFunction<IntermediateOutput, FinalOutput, String, TimeWindow>.Context context, Iterable<IntermediateOutput> elements, Collector<FinalOutput> out) throws Exception {

        long windowStart = context.window().getStart();
        Date windowStartDate = new Date();
        windowStartDate.setTime(windowStart);
        Timestamp windowEndTs = new Timestamp(context.window().getEnd());
        IntermediateOutput res = elements.iterator().next();

        Map<String, Float> lastPricePerSymbol = res.getLastPricePerSymbol();    //last price of current symbol (string s = symbol) cause it's keyed stream
        Map<String, List<Integer>> symbolInBatches = res.getSymbolInBatches();  //list of #batch containing current symbol

        if (count==null){
            count = new HashMap<>();
            count.put(s,0);
        }
        else {
            if (!count.containsKey(s)){
                count.put(s,0);
            } else {
                count.put(s, count.get(s)+1);
            }
        }

        //========== QUERY1 ============
        if (myEma38==null){
            myEma38 = new HashMap<>();
            myEma38.put(new Tuple2<>(s,count.get(s)),null);
        }
        if (myEma100==null){
            myEma100 = new HashMap<>();
            myEma100.put(new Tuple2<>(s,count.get(s)),null);
        }

        //calculating ema38
        IntermediateOutput.calculateEMA(s,lastPricePerSymbol.get(s), count.get(s), Config.ema38, myEma38);
        //calculating ema100
        IntermediateOutput.calculateEMA(s, lastPricePerSymbol.get(s), count.get(s), Config.ema100, myEma100);

        //========== END OF QUERY1 ============


        //========== QUERY2 ============
        if (buyCrossovers ==null){
            buyCrossovers = new HashMap<>();
        }
        if (sellCrossovers ==null){
            sellCrossovers = new HashMap<>();
        }

        //calculating crossovers
        if (count.get(s)>0){
            if (myEma38.containsKey(new Tuple2<>(s,count.get(s)-1)) && myEma100.containsKey(new Tuple2<>(s,count.get(s)-1))){

                if (myEma38.get(new Tuple2<>(s,count.get(s))) > myEma100.get(new Tuple2<>(s,count.get(s)))){
                    if (myEma38.get(new Tuple2<>(s,count.get(s)-1)) <= myEma100.get(new Tuple2<>(s,count.get(s)-1))){
                        //buy
                        //System.out.println("BUY! "+s);
                        if (!buyCrossovers.containsKey(s)){
                            List<Timestamp> ts = new ArrayList<>();
                            ts.add(windowEndTs);
                            buyCrossovers.put(s, ts);
                        } else {
                            List<Timestamp> ts = buyCrossovers.get(s);
                            ts.add(windowEndTs);
                            buyCrossovers.put(s,ts);
                        }

                    }
                }

                if (myEma38.get(new Tuple2<>(s,count.get(s))) < myEma100.get(new Tuple2<>(s,count.get(s)))){
                    if (myEma38.get(new Tuple2<>(s,count.get(s)-1)) >= myEma100.get(new Tuple2<>(s,count.get(s)-1))) {
                        //sell
                        //System.out.println("SELL! "+s);
                        if (!sellCrossovers.containsKey(s)){
                            List<Timestamp> ts = new ArrayList<>();
                            ts.add(windowEndTs);
                            sellCrossovers.put(s, ts);
                        } else {
                            List<Timestamp> ts = sellCrossovers.get(s);
                            ts.add(windowEndTs);
                            sellCrossovers.put(s,ts);
                        }

                    }
                }
            }
        }



        //========== END QUERY2 ============

        //========== retrieving results per window ==========
        Map<String, Tuple2<Integer,Float>> symbol_WindowEma38 = new HashMap<>();
        Map<String, Tuple2<Integer,Float>> symbol_WindowEma100 = new HashMap<>();

        symbol_WindowEma38.put(s, new Tuple2<>(count.get(s),myEma38.get(new Tuple2<>(s,count.get(s)))));
        symbol_WindowEma100.put(s, new Tuple2<>(count.get(s),myEma100.get(new Tuple2<>(s,count.get(s)))));

        //getting last three ts for buys and sells lists
        List<Timestamp> lastThreeBuys = null;
        if (buyCrossovers.get(s)!=null){
            int sizeBuy = buyCrossovers.get(s).size();
            lastThreeBuys = new ArrayList<>();
            if (sizeBuy>=3){
                lastThreeBuys.add(buyCrossovers.get(s).get(sizeBuy-1));
                lastThreeBuys.add(buyCrossovers.get(s).get(sizeBuy-2));
                lastThreeBuys.add(buyCrossovers.get(s).get(sizeBuy-3));
            } else if (sizeBuy==2){
                lastThreeBuys.add(buyCrossovers.get(s).get(sizeBuy-1));
                lastThreeBuys.add(buyCrossovers.get(s).get(0));
            } else if (sizeBuy==1){
                lastThreeBuys.add(buyCrossovers.get(s).get(0));
            }
            //System.out.println(s+" "+windowStartDate+" - lastThreeBuys = "+ lastThreeBuys);
        }

        List<Timestamp> lastThreeSells = null;
        if (sellCrossovers.get(s)!=null){
            int sizeSell = sellCrossovers.get(s).size();
            lastThreeSells = new ArrayList<>();
            if (sizeSell>=3){
                lastThreeSells.add(sellCrossovers.get(s).get(sizeSell-1));
                lastThreeSells.add(sellCrossovers.get(s).get(sizeSell-2));
                lastThreeSells.add(sellCrossovers.get(s).get(sizeSell-3));
            } else if (sizeSell==2){
                lastThreeSells.add(sellCrossovers.get(s).get(sizeSell-1));
                lastThreeSells.add(sellCrossovers.get(s).get(0));
            } else if (sizeSell==1){
                lastThreeSells.add(sellCrossovers.get(s).get(0));
            }
            //System.out.println(s+" "+windowStartDate+" - lastThreeSells = "+ lastThreeSells );
        }

        Map<String, List<Timestamp>> symbol_buyCrossovers = new HashMap<>();
        Map<String, List<Timestamp>> symbol_sellCrossovers = new HashMap<>();
        symbol_buyCrossovers.put(s,lastThreeBuys);
        symbol_sellCrossovers.put(s, lastThreeSells);


        List<Integer> currBatches = symbolInBatches.get(s); //prendo tutti i batch in cui è presente il simbolo corrente s
        currBatches.forEach(batch -> {
            //faccio collect di tutti i batch del simbolo corrente e passo la Map dell'accumulator che è o VUOTA o  SE NUM==0 ha tutti i batch (di quel simbolo/i) con relativo timestamp iniziale
            //questo perche uno stesso simbolo può avere piu batch -> dipende dalla chiave
            FinalOutput finalOutput = new FinalOutput(s, batch, symbol_WindowEma38, symbol_WindowEma100, lastPricePerSymbol.get(s), symbol_buyCrossovers, symbol_sellCrossovers, res.getTimeBatch(), windowEndTs);
            out.collect(finalOutput);
        });

        //System.out.println("proc-time = "+new Timestamp(System.currentTimeMillis()));

        //========== end of retrieving results per window ==========

    }


}
