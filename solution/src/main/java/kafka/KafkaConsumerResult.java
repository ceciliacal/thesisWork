package kafka;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;


public class KafkaConsumerResult {

    static String topic;
    static String BOOTSTRAP_SERVERS;
    static Map<Integer, Tuple2<Long, Timestamp>> batchStartTime;
    static Map<Integer, Tuple2<Long, Timestamp>> batchEndTime;
    static List<String> listProcTimes;

    public KafkaConsumerResult(Map<Integer, Tuple2<Long, Timestamp>> batchProcTime, String myTopic, String myKafkaAddress) {
        topic = myTopic;
        BOOTSTRAP_SERVERS = myKafkaAddress;
        batchStartTime = batchProcTime;
        batchEndTime = new HashMap<>();
        listProcTimes = new ArrayList<>();
    }

    private static org.apache.kafka.clients.consumer.Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Create the consumer using props.
        final org.apache.kafka.clients.consumer.Consumer<Long, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(getTopic()));
        return consumer;
    }

    public void runConsumer() throws InterruptedException, FileNotFoundException {

        final org.apache.kafka.clients.consumer.Consumer<Long, String> consumer = createConsumer();

        final int giveUp = 200;
        int noRecordsCount = 0;

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
                //System.out.println(str);
                String[] values = str.split(",");
                Integer batch = Integer.valueOf(values[0]);

                //first record from new batch
                if (batchStartTime.size()>0) {
                    if (!batchEndTime.containsKey(batch)) {
                        long currentTimeMs = System.currentTimeMillis();
                        Timestamp currentTs = new Timestamp(System.currentTimeMillis());
                        batchEndTime.put(batch, new Tuple2<>(currentTimeMs, currentTs));
                        long diffTime = currentTimeMs - batchStartTime.get(batch)._1;

                        listProcTimes.add(batch+","+batchStartTime.get(batch)._2+","+currentTs+","+diffTime+"\n");
                        System.out.println("--adding...    " + batch + ": " + batchStartTime.get(batch)._2 + " " + currentTs + " " + diffTime);
                    }
                }
                //System.out.println("batchStartTime.size(): "+batchStartTime.size()+"  batchEndTime.containsKey(batch): "+batchEndTime.containsKey(batch));
            });

            consumer.commitAsync();

        }

        System.out.println("sto fuori dal while");

        while (true){
            if (Producer.hasFinished){
                System.out.println("sto nell IF has finished");
                PrintWriter writer = new PrintWriter("results.txt");
                for (String listProcTime : listProcTimes) {
                    writer.append(listProcTime);
                }
                assert writer != null;
                writer.close();
                System.out.println("File is done");
                break;
            }
        }


    }

    public static String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBOOTSTRAP_SERVERS() {
        return BOOTSTRAP_SERVERS;
    }

    public void setBOOTSTRAP_SERVERS(String BOOTSTRAP_SERVERS) {
        this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
    }

    public Map<Integer, Tuple2<Long, Timestamp>> getBatchStartTime() {
        return batchStartTime;
    }

    public void setBatchStartTime(Map<Integer, Tuple2<Long, Timestamp>> batchStartTime) {
        this.batchStartTime = batchStartTime;
    }

    public static Map<Integer, Tuple2<Long, Timestamp>> getBatchEndTime() {
        return batchEndTime;
    }

    public static void setBatchEndTime(Map<Integer, Tuple2<Long, Timestamp>> batchEndTime) {
        KafkaConsumerResult.batchEndTime = batchEndTime;
    }
}
