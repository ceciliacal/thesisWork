package kafka;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import utils.Config;


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

        String token = "4unS5E8b5itJZFPvFFgQymMElyc81ISzUbnZnwdTAg00S7bIo0b__A1KU1z_mO03KvFWayN4-VpE8G0oY6obfA==";
        String bucket = "sol";
        String org = "org0";

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
                //result format example: ---> 0,IEBBB.FR,2.7020512,1.0433663,null,null,{0=2022-09-26 17:58:06.063},2021-11-08 08:05:00.0

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

                //---------- loading data into influxDb ----------
                Timestamp currWindowEnd = stringToTimestamp(values[7],0);
                assert currWindowEnd != null;

                //Timestamp startInfluxWrite = stringToTimestamp("2021-11-09 13:59:00.0",0);
                //Timestamp endInfluxWrite = stringToTimestamp("2021-11-09 14:31:00.0",0);

                String input = values[7].substring(0, values[7].length() - 2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.UK);  // Specify locale to determine human language and cultural norms used in translating that input string.
                LocalDateTime ldt = LocalDateTime.parse(input, formatter);
                Instant eventTime = ldt.atZone(ZoneId.of("Europe/London")).toInstant();

                Point point = Point
                        .measurement("dspResults")      //questo potrebbe essere anche batch num in alternativa!
                        .addTag("symbol_id", values[1])
                        .addField("ema38", Double.valueOf(values[2]))
                        .addField("ema100", Double.valueOf(values[3]))
                        .addField("price", Double.valueOf(values[8]));

                if (values[4].equals("null")){
                    point.addField("buy", "-");
                } else {
                    point.addField("buy", values[9]);
                }
                if(values[5].equals("null")){
                    point.addField("sell", "-");
                } else {
                    point.addField("sell", values[10]);
                }

                point.time(eventTime, WritePrecision.MS);
                System.out.println(str);

                try (WriteApi writeApi = client.getWriteApi()) {
                    writeApi.writePoint(bucket, org, point);
                    //System.out.println("eventTime= "+eventTime);
                }



            });

            consumer.commitAsync();

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

    public static Timestamp stringToTimestamp(String strDate, int invoker){

        SimpleDateFormat dateFormat = null;

        if (invoker==0){
            dateFormat = new SimpleDateFormat(Config.pattern2);
        } else {
            dateFormat = new SimpleDateFormat(Config.pattern);
        }

        try {
            Date parsedDate = dateFormat.parse(strDate);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            /*
            System.out.println("parsedDate.getTime() = "+parsedDate.getTime());
            System.out.println("parsedDate = "+parsedDate);
            System.out.println("strDate = "+strDate);
             */
            return timestamp;
        } catch(Exception e) {
            //error
            return null;
        }

    }

}

/*
                        .addField("buy", values[4])
                        .addField("sell", values[5])
                        .addField("price", Double.valueOf(values[8]))
                        .time(eventTime, WritePrecision.MS)
                        ;
    */