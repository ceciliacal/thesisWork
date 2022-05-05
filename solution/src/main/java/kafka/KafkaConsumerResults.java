package kafka;
import de.tum.i13.challenge.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static kafka.Producer.*;

//todo fai mappa con key: batch(int) value: list<Indicator>
public class KafkaConsumerResults {

    private final static String TOPIC = "resultsTopic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9091";
    private static Map <Integer, List<Indicator>> indicatorsPerBatch;
    private static Map <Integer, List<CrossoverEvent>> crossoverEventsPerBatch;
    Benchmark newBenchmark;
    ChallengerGrpc.ChallengerBlockingStub challengeClient;
    //Map <Integer, List<Indicator>> indicatorsPerBatch;

    public KafkaConsumerResults(Benchmark newBenchmark, ChallengerGrpc.ChallengerBlockingStub challengeClient) {
        this.newBenchmark = newBenchmark;
        this.challengeClient = challengeClient;
    }

    public Benchmark getNewBenchmark() {
        return newBenchmark;
    }

    public void setNewBenchmark(Benchmark newBenchmark) {
        this.newBenchmark = newBenchmark;
    }

    public ChallengerGrpc.ChallengerBlockingStub getChallengeClient() {
        return challengeClient;
    }

    public void setChallengeClient(ChallengerGrpc.ChallengerBlockingStub challengeClient) {
        this.challengeClient = challengeClient;
    }



    private static Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    public void runConsumer() throws InterruptedException {

        int countWhile = 0;
        AtomicInteger countFor = new AtomicInteger();

        final Consumer<Long, String> consumer = createConsumer();
        intermediateResults = new HashMap<>();
        finalResults = new HashMap<>();
        indicatorsPerBatch = new HashMap<>();
        crossoverEventsPerBatch = new HashMap<>();

        //final int giveUp = 100;
        final int giveUp = 50;
        int noRecordsCount = 0;

        System.out.println("in runconsumer: newBenchmark = "+ newBenchmark);
        System.out.println("in runconsumer: challengeClient = "+challengeClient);

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                //System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());

                String str = record.value();
                System.out.println(str);    //todo: su kafka ieb della prima finestra c'è ma qui non lo stampa mai!!!

                String[] values = str.split(",");
                Indicator.Builder indicator = Indicator.newBuilder();

                if (indicatorsPerBatch.containsKey(Integer.valueOf(values[0]))){
                    List<Indicator> indicators = indicatorsPerBatch.get(Integer.valueOf(values[0]));

                    if (!indicators.contains(Indicator.newBuilder().build().getSymbol().equals(values[1]))){
                        indicator.setSymbol(values[1]);
                        indicator.setEma38(Float.valueOf(values[2]));
                        indicator.setEma100(Float.valueOf(values[3]));
                        //add list indicator
                        indicators.add(indicator.build());
                        indicatorsPerBatch.put(Integer.valueOf(values[0]), indicators);
                    }

                } else {
                    List<Indicator> indicators = new ArrayList<>();

                    indicator.setSymbol(values[1]);
                    indicator.setEma38(Float.valueOf(values[2]));
                    indicator.setEma100(Float.valueOf(values[3]));
                    //add list indicator
                    indicators.add(indicator.build());

                    indicatorsPerBatch.put(Integer.valueOf(values[0]), indicators);
                }


                List<Timestamp> buysTs = null;
                List<Timestamp> sellsTs = null;

                CrossoverEvent.Builder crossoverEvent = CrossoverEvent.newBuilder();
                crossoverEvent.setSymbol(values[1]);

                //retrieving crossovers ts lists (if any)
                if (!values[4].equals("null")){
                    //parse ts list
                    buysTs = createTimestampsList(values[5]);
                }

                if (!values[5].equals("null")){
                    sellsTs = createTimestampsList(values[6]);
                }

                if (buysTs!=null){  //if list is null that symbol has no crossovers
                    //if it does, we put each one of them inside CrossoverEvent through setTs

                    for(Timestamp ts: buysTs){  //set buys (at maximum, they're 3)
                        com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder().setSeconds(ts.getTime()).build();
                        crossoverEvent.setTs(timestamp);
                    }
                }
                if (sellsTs!=null){
                    for(Timestamp ts: sellsTs){  //set sells (at maximum, they're 3)
                        com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder().setSeconds(ts.getTime()).build();
                        crossoverEvent.setTs(timestamp);
                    }
                }

                if (crossoverEventsPerBatch.containsKey(Integer.valueOf(values[0]))){
                    List<CrossoverEvent> crossoverEvents = crossoverEventsPerBatch.get(Integer.valueOf(values[0]));
                    crossoverEvents.add(crossoverEvent.build());
                } else {
                    List<CrossoverEvent> crossoverEvents = new ArrayList<>();
                    crossoverEvents.add(crossoverEvent.build());
                    crossoverEventsPerBatch.put(Integer.valueOf(values[0]), crossoverEvents);
                }

                System.out.println("indicatorsPerBatch key: "+indicatorsPerBatch.keySet());
                System.out.println("crossoverEventsPerBatch key: "+crossoverEventsPerBatch.keySet());


                /*
                if (mustStop){
                    challengeClient.endBenchmark(newBenchmark);
                    System.out.println("ended Benchmark consumer");
                }
                */

                //TODO : check un solo : 0,IEBBB.FR,10.004864,4.0511293,null,null -> gli ema di che finestra sono???
                //TODO : nel for sono 24519, su kafdrop sono 24555 record. perche?
                countFor.getAndIncrement();
                //System.out.println("nel for "+countFor);
            });

            countWhile ++;
            //System.out.println("nel while"+ countWhile);
            consumer.commitAsync();

            if (mustStop){
                int countRecords = consumerRecords.count();
                System.out.println("countRecords: "+countRecords);
            }

            if (consumerRecords.isEmpty()){
                System.out.println("EMPTY!!");
            }

        }

        //System.out.println("1indicatorsPerBatch.get(batch)0: "+indicatorsPerBatch.get(0));
        indicatorsPerBatch.keySet().forEach( batch -> {
            /*
            System.out.println("1batch: "+batch);
            System.out.println("1batchSeqId.get(batch): "+batchSeqId.get(batch));
            System.out.println("1indicatorsPerBatch.get(batch): "+indicatorsPerBatch.get(batch));
*/

            ResultQ1 q1Result = ResultQ1.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batchSeqId.get(batch)) //set the sequence number
                    .addAllIndicators(indicatorsPerBatch.get(batch))
                    .build();
            challengeClient.resultQ1(q1Result);

        });

        crossoverEventsPerBatch.keySet().forEach( batch -> {
            /*
            System.out.println("2batch: "+batch);
            System.out.println("2batchSeqId.get(batch): "+batchSeqId.get(batch));
            System.out.println("2indicatorsPerBatch.get(batch): "+crossoverEventsPerBatch.get(batch));

             */
            ResultQ2 q2Result = ResultQ2.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batchSeqId.get(batch)) //set the sequence number
                    .addAllCrossoverEvents(crossoverEventsPerBatch.get(batch))
                    .build();
            challengeClient.resultQ2(q2Result);

        });

        if (mustStop){
            challengeClient.endBenchmark(newBenchmark);
            System.out.println("ended Benchmark consumer");
        }

        //challengeClient.endBenchmark(newBenchmark);
        consumer.close();
        System.out.println("DONE");

    }

    //putting results from batch longer than one window inside of "intermediateResults" map to collect them later on
    public static void putIntoMap(String str, int longBatch) {

        List<Timestamp> buysTs = null;
        List<Timestamp> sellsTs = null;

        //String[] lines = str.split(",");
        //for (String line : lines) {
            //String[] values = line.split(";");
            String[] values = str.split(",");
            if (Integer.valueOf(values[1])==longBatch){
                intermediateResults.put(new Tuple2<>(Integer.valueOf(values[1]),values[2]), new Tuple2<>(Float.valueOf(values[3]),Float.valueOf(values[4])));
                //todo: perche non ci sono le liste di ts??
                //retrieving crossovers ts lists (if any)
                if (!values[5].equals("null")) {
                    //parse ts list
                    buysTs = createTimestampsList(values[5]);
                }
                else if(!values[6].equals("null")){
                    sellsTs = createTimestampsList(values[6]);
                }
                intermediateResults2.put(new Tuple2<>(Integer.valueOf(values[1]),values[2]), new Result(values[2], Float.valueOf(values[3]), Float.valueOf(values[4]), buysTs, sellsTs));
            }
        //}
    }

    //populates finalResults map
    public static List<Integer> calculateResults(String str, int longBatch) throws IOException, InterruptedException {
        //System.out.println("Collecting results!");
        Result res;
        List<Integer> batchesInCurrentWindow = new ArrayList<>();
        List<Timestamp> buysTs = null;
        List<Timestamp> sellsTs = null;
        finalResults = new HashMap<>();     //every window has a new map

        //String[] lines = str.split(",");       //splitting the whole string (contains ALL results, separated from ",")
        String[] values = str.split(",");       //splitting the whole string (contains ALL results, separated from ",")
        //for (String line : lines) {
            //String[] values = line.split(";");  //splitting each field in one single line

            int currBatch = Integer.valueOf(values[1]);
            if (!batchesInCurrentWindow.contains(currBatch)){
                batchesInCurrentWindow.add(currBatch);
            }

            //retrieving crossovers ts lists (if any)
            if (!values[5].equals("null")) {
                //parse ts list
                buysTs = createTimestampsList(values[5]);
            }
            else if(!values[6].equals("null")){
                sellsTs = createTimestampsList(values[6]);
            }

            //if current line's batch equals longBatch get from intermediateResults map
            //the key <longBatch,currentSymbol> ad add its emas values to a Result object list in order to populate finalResults map.
            if(currBatch==longBatch) {
                System.out.println("value= "+ values[0]+","+values[1]+","+values[2]+","+values[3]+","+values[4]+","+values[5]+","+values[6]);
                Tuple2<Float, Float> emas = intermediateResults.get(new Tuple2<>(longBatch, values[2]));
                res = new Result(values[2], emas._1, emas._2, null, null);
                res = intermediateResults2.get(new Tuple2<>(longBatch, values[2]));
            } else {
                res = new Result(values[2], Float.parseFloat(values[3]), Float.parseFloat(values[4]), null,null);
            }

            if (buysTs!=null){
                res.setBuys(buysTs);
                //System.out.println("res BUY: "+res);
            } else if (sellsTs!=null){
                res.setSells(sellsTs);
            }

            if(!finalResults.containsKey(currBatch)){
                List<Result> resList = new ArrayList<>();
                resList.add(res);
                finalResults.put(currBatch,resList);
            } else {
                List<Result> resList = finalResults.get(currBatch);
                resList.add(res);
                finalResults.put(currBatch,resList);
            }

            //finish analyzing single line
            buysTs = null;
            sellsTs = null;

        //} //finish analyzing all lines

        return batchesInCurrentWindow;
    }

    public static List<Indicator> calculateIndicators(int i) {

        List<Indicator> indicatorsList = new ArrayList<>();
        List<Result> resList = finalResults.get(i);

        resList.stream().forEach(res -> {
            //indicator
            Indicator.Builder ind = Indicator.newBuilder();
            ind.setSymbol(res.getSymbol());
            ind.setEma38(Float.valueOf(res.getEma38()));
            ind.setEma100(Float.valueOf(res.getEma100()));
            //add list indicator
            indicatorsList.add(ind.build());


        } );

        //here we get list<Indicator> of #i batch

        return indicatorsList;

    }

    public static List<Timestamp> createTimestampsList(String str){

        List<Timestamp> list = new ArrayList<>();
        String[] line = str.split(",");
        int len = line.length;

        if (len>1){
            line[0] = line[0].substring(1);
            int lastStrlen = line[len-1].length();
            line[len-1] = line[len-1].substring(0,lastStrlen-1);
        } else {
            int lastStrlen = line[0].length();
            line[0]  = line[0].substring(1,lastStrlen-1);
        }

        for (String s : line) {
            //trasform string array in timestamp list
            Timestamp ts = Producer.stringToTimestamp(s, 0);
            list.add(ts);
        }

        return list;
    }

    //given timestamp lastTs, this method calculates upper bound window (every 5 mins)
    public static Timestamp windowProducingResult(Timestamp lastTs,Timestamp nextWindow){
        long res = nextWindow.getTime();
        while(true){
            res = res + TimeUnit.MINUTES.toMillis(windowLen);
            //System.out.println("res = "+new Timestamp(res));
            if (lastTs.compareTo(new Timestamp(res))<0){
                break;
            }
        }
        return new Timestamp(res);
    }

    public static List<CrossoverEvent> calculateCrossoverEvents(int i) {
        //System.out.println("STO IN CROSSOVERS!!!!!!!!!!! + i="+i);

        List<CrossoverEvent> crossoverEventList = new ArrayList<>();

        List<Result> resList = finalResults.get(i);
        resList.stream().forEach(res -> {
            CrossoverEvent.Builder cross = CrossoverEvent.newBuilder();
            cross.setSymbol(res.getSymbol());
            if (res.getBuys()!=null){  //if list is null that symbol has no crossovers
                //if it does, we put each one of them inside CrossoverEvent through setTs

                for(Timestamp ts: res.getBuys()){  //set buys (at maximum, they're 3)
                    com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder().setSeconds(ts.getTime()).build();
                    cross.setTs(timestamp);
                }
            }
            if (res.getSells()!=null){
                for(Timestamp ts: res.getSells()){  //set sells (at maximum, they're 3)
                    com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder().setSeconds(ts.getTime()).build();
                    cross.setTs(timestamp);
                }
            }

            crossoverEventList.add(cross.build());
        });
        return new ArrayList<>();
    }
}
