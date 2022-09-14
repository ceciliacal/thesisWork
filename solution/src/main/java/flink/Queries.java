package flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import utils.Config;
import data.Event;
import java.util.Properties;


public class Queries {

    public static void runQueries(DataStream<Event> stream, String kafkaAddress){

        KeyedStream<Event, String> keyedStream = stream
                .keyBy(Event::getSymbol);

        SingleOutputStreamOperator<FinalOutput> finalOutputDataStream = keyedStream
                .window(TumblingEventTimeWindows.of(Time.minutes(Config.windowLen)))
                .aggregate(new MyAggregateFunction(), new MyProcessWindowFunction())
                ;

        finalOutputDataStream
                .keyBy(FinalOutput::getBatch)
                .map(new MapFunctionFinalOutput())
                .addSink(new FlinkKafkaProducer<String>(Config.TOPIC_RES,
                        new utils.ProducerStringSerializationSchema(Config.TOPIC_RES),
                        getFlinkPropAsProducer(kafkaAddress),
                        FlinkKafkaProducer.Semantic.EXACTLY_ONCE));

                //KafkaSink<String> sink = KafkaSink.<String>builder().setBootstrapServers(kafkaAddress).setKafkaProducerConfig(getFlinkPropAsProducer()).setRecordSerializer(KafkaRecordSerializationSchema.builder().setTopic(Config.TOPIC_RES).setValueSerializationSchema(new SimpleStringSchema()).build()).setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE).build();

    }




    //metodo che crea proprietà per creare sink verso kafka
    public static Properties getFlinkPropAsProducer(String kafkaAddress){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaAddress);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG,Config.CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "20971520");

        return properties;

    }
}
