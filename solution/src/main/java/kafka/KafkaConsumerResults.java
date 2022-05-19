package kafka;
import de.tum.i13.challenge.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;
import utils.Config;


import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        indicatorsPerBatch = new HashMap<>();
        crossoverEventsPerBatch = new HashMap<>();

        //final int giveUp = 100;
        final int giveUp = 50;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {

                String str = record.value();
                //System.out.println(str);

                String[] values = str.split(",");
                int currBatch = Integer.parseInt(values[0]);
                int prevBatch = currBatch - 1;
                Timestamp endOfWindow = stringToTimestamp(values[6],0);
                assert endOfWindow != null;
                //System.out.println("endOfWindow "+endOfWindow+" "+str);

                /*
                if (values[0].equals("49")){
                    System.out.println("NELL IF49");
                    System.out.println("kk :: "+stringToTimestamp(values[6],0));
                    System.out.println("jj :: "+firingWindows.get(Integer.valueOf(values[0])));
                    System.out.println("firingWindows "+firingWindows);
                    System.out.println("str: "+str);
                }
                 */

                TreeMap<Integer, Tuple2<Timestamp, Boolean>> firingWindowsClone = firingWindows;

                if (currBatch>0){
                    Timestamp endPrevBatch = firingWindowsClone.get(prevBatch)._1;
                    if (endOfWindow.compareTo(endPrevBatch) > 0 && (!firingWindowsClone.get(prevBatch)._2)){
                        //sono arrivati tutti i risultati della finestra precedente e quindi posso inviarli con gRPC
                        Tuple2<Timestamp, Boolean> check = new Tuple2<>(firingWindowsClone.get(prevBatch)._1, true);
                        firingWindowsClone.put(prevBatch, check);
                        firingWindows.put(prevBatch, check);

                        //todo. 25 sta a meta delle 8.20, quindi con la send li devo mandare tutti fino a currBatch-1 (la key25 no!)
                        System.out.println("endOfWindow: "+endOfWindow);
                        System.out.println("firingWindows.get(currBatch-1): "+firingWindowsClone.get(prevBatch));
                        System.out.println("firingWindowsClone "+firingWindowsClone);
                        System.out.println("tempo di inviare!!!! str: "+str);
                        System.out.println("keySet VISTE X RISULTATI: "+indicatorsPerBatch.keySet());

                        indicatorsPerBatch.keySet().forEach( batch -> {

                            ResultQ1 q1Result = ResultQ1.newBuilder()
                                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                                    .setBatchSeqId(batchSeqId.get(batch)) //set the sequence number
                                    .addAllIndicators(indicatorsPerBatch.get(batch))
                                    .build();
                            challengeClient.resultQ1(q1Result);

                        });
                        //todo controlla il clone che non punta a stessa area di memoria
                        crossoverEventsPerBatch.keySet().forEach( batch -> {

                            ResultQ2 q2Result = ResultQ2.newBuilder()
                                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                                    .setBatchSeqId(batchSeqId.get(batch)) //set the sequence number
                                    .addAllCrossoverEvents(crossoverEventsPerBatch.get(batch))
                                    .build();
                            challengeClient.resultQ2(q2Result);

                        });
                            //todo: last
                        final Set<Map.Entry<Integer, List<Indicator>>> entries = indicatorsPerBatch.entrySet();

                        for (Map.Entry<Integer, List<Indicator>> entry : entries) {
                            indicatorsPerBatch.remove(entry.getKey());
                            // do whatever
                        }
                        System.out.println("dopoREMOVE indicatorsPerBatch: "+indicatorsPerBatch.keySet());
                    }

                }

                Indicator.Builder indicator = Indicator.newBuilder();

                if (indicatorsPerBatch.containsKey(Integer.valueOf(values[0]))){

                    List<Indicator> indicators = indicatorsPerBatch.get(Integer.valueOf(values[0]));
                    indicator.setSymbol(values[1]);
                    indicator.setEma38(Float.parseFloat(values[2]));
                    indicator.setEma100(Float.parseFloat(values[3]));
                    //add list indicator
                    indicators.add(indicator.build());
                    indicatorsPerBatch.put(Integer.valueOf(values[0]), indicators);
                } else {

                    List<Indicator> indicators = new ArrayList<>();
                    indicator.setSymbol(values[1]);
                    indicator.setEma38(Float.parseFloat(values[2]));
                    indicator.setEma100(Float.parseFloat(values[3]));
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

                //System.out.println("indicatorsPerBatch key: "+indicatorsPerBatch.keySet());
                //System.out.println("crossoverEventsPerBatch key: "+crossoverEventsPerBatch.keySet());

                countFor.getAndIncrement();
                //System.out.println("nel for "+countFor);
            });

            countWhile ++;
            //System.out.println("nel while"+ countWhile);
            consumer.commitAsync();

            /*
            if (mustStop){
                int countRecords = consumerRecords.count();
                System.out.println("countRecords: "+countRecords);
            }

             */

        }

        //todo: last batch mandato (cosi mando un intervallo)
        //System.out.println("1indicatorsPerBatch.get(batch)0: "+indicatorsPerBatch.get(0));
        //indicatorsPerBatch.keySet().forEach(System.out::println);

        if (mustStop){
            challengeClient.endBenchmark(newBenchmark);
            System.out.println("ended Benchmark consumer");
        }

        //challengeClient.endBenchmark(newBenchmark);
        consumer.close();
        System.out.println("DONE");

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

}
