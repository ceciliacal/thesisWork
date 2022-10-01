package kafka;

import flink.MapFunctionEvent;
import flink.Queries;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import utils.Config;
import data.Event;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;


public class Consumer {

    private static long startTime;
    private static String kafkaAddress;
    private static String kafkaTopic;

    //creating kafka consumer to listen for data in kafka broker
    public static void main(String[] args) throws Exception {

        int parallelism = 3;
        //try (InputStream input = new FileInputStream("config.properties")) {
        try (InputStream input = new FileInputStream("/home/configProp/config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            // get the property value and print it out
            kafkaAddress = prop.getProperty("kafkaIpAddress")+":"+Config.KAFKA_PORT;
            kafkaTopic = prop.getProperty("topic");
            parallelism = Integer.parseInt(prop.getProperty("parallelism"));

            System.out.println("in CONSUMER: kafkaAddress= "+kafkaAddress);
            System.out.println("in CONSUMER: parallelism= "+parallelism);
            System.out.println("in CONSUMER: topic= "+kafkaTopic);

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        FlinkKafkaConsumer<String> consumer = createConsumer();
        consumer.assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofMillis(1)));
        StreamExecutionEnvironment env = createEnviroment();

        //mapping data from source into datastream of Events
        DataStream<Event> stream = env.addSource(consumer)
                .map(new MapFunctionEvent());

        //start queries calculation
        Queries.runQueries(stream, kafkaAddress);
        env.setParallelism(parallelism);
        env.execute("debsTest");

    }

    public static FlinkKafkaConsumer<String> createConsumer() throws Exception {
        //properties creation
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerGroup");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        //consumer creation
        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<>(kafkaTopic, new SimpleStringSchema(), props);

        System.out.println("---consumer created---");
        return myConsumer;

    }

    public static StreamExecutionEnvironment createEnviroment(){
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        System.out.println("--enviroment created---");
        return env;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime(long startTime) {
        Consumer.startTime = startTime;
    }

}