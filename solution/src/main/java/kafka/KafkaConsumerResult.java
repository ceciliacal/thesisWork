package kafka;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
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

        String token = "YocTCHFR59RXbWVTj87l4A-nREHNt4gB3PAqdNNBEB2Ey-GgtoUsRuhu0V4bnerCWi9tfgmbj0FxobjKS8L4EQ==";
        String bucket = "sol";
        String org = "myorg0";

        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());

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
                //result format example: 0,IEBBB.FR,2.7020512,1.0433663,null,null,{0=2022-09-26 17:52:17.267}
                //updated ---> 0,IEBBB.FR,2.7020512,1.0433663,null,null,{0=2022-09-26 17:58:06.063},2021-11-08 08:05:00.0

                String str = record.value();
                String[] values = str.split(",");
                Integer batch = Integer.valueOf(values[0]);
                //TODO. qui potrei prendere i tempi dal Consumer (Flink) mandandoli insieme ai risultati delle due query e fare
                //TODO. un confronto fra le tempistiche all'interno di Flink ed esternamente con Kafka

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

                //loading data into influxDb
                //updated ---> 0,IEBBB.FR,2.7020512,1.0433663,null,null,{0=2022-09-26 17:58:06.063},2021-11-08 08:05:00.0

                String input = values[7].substring(0, values[7].length() - 2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.UK);  // Specify locale to determine human language and cultural norms used in translating that input string.
                LocalDateTime ldt = LocalDateTime.parse(input , formatter);
                Instant eventTime = ldt.atZone(ZoneId.of("Europe/London")).toInstant();
                //System.out.println("--INSTANT: "+ldt.atZone(ZoneId.of("Europe/London")).toInstant());


                Point point = Point
                        .measurement("dspResults")      //questo potrebbe essere anche batch num in alternativa!
                        .addTag("symbol_id", values[1])
                        .addField("ema38", Double.valueOf(values[2]))
                        .addField("ema100", Double.valueOf(values[3]))
                        //todo: mettere stringa buy/hold/sell a seconda della size della lista
                        .addField("buy", values[4])
                        .addField("sell", values[5])
                        .addField("price", values[8])
                        .time(eventTime, WritePrecision.MS)
                        ;


                try (WriteApi writeApi = client.getWriteApi()) {
                    writeApi.writePoint(bucket, org, point);
                    //System.out.println("eventTime= "+eventTime);
                }


                //WriteApiBlocking writeApi = client.getWriteApiBlocking();
                //writeApi.writePoint(bucket, org, point);


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
