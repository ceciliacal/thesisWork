package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.*;

public class Producer {

    private static String kafkaAddress;

    //creates kafka producer
    public static org.apache.kafka.clients.producer.Producer<String, String> createProducer(String kafkaAddress) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static void publishMessages(String kafkaAddress){

        final org.apache.kafka.clients.producer.Producer<String, String> producer = createProducer(kafkaAddress);
        System.out.println("------------------------START----------------------");

        try {
            Stream<String> FileStream = Files.lines(Paths.get(Config.dataset_path+".csv"));
            //reading file
            FileStream.forEach(line -> {

                String[] fields = line.split(",");
                String tsCurrent = fields[2];    //current row timestamp
                Timestamp eventTime = stringToTimestamp(tsCurrent,0);
                ProducerRecord<String, String> CsvRecord = new ProducerRecord<>( Config.TOPIC, 0, eventTime.getTime(), fields[4], line);

                //System.out.println("eventTime: "+eventTime);
                //System.out.println("line: " + line);

                //sending record
                producer.send(CsvRecord, (metadata, exception) -> {
                    if(metadata != null){
                        //successful writes
                        System.out.println("CsvData: -> "+ CsvRecord.key()+" | "+ CsvRecord.value());
                    }
                    else{
                        //unsuccessful writes
                        System.out.println("Error Sending Csv Record -> "+ CsvRecord.value());
                    }
                });
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------------------END----------------------");

    }

    public static Timestamp stringToTimestamp(String strDate, int invoker){

        SimpleDateFormat dateFormat;
        if (invoker==0){
            dateFormat = new SimpleDateFormat(Config.pattern2);
        } else {
            dateFormat = new SimpleDateFormat(Config.pattern);
        }

        try {
            Date parsedDate = dateFormat.parse(strDate);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            return timestamp;
        } catch(Exception e) {
            //error
            return null;
        }

    }



    public static void main(String[] args) throws Exception {
        String ip = args[0];
        String port = args[1];
        kafkaAddress = ip+":"+port;
        //kafkaAddress = "184.73.80.39:9092";
        publishMessages(kafkaAddress);
    }
}
