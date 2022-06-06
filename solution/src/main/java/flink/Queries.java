package flink;



import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import utils.Config;
import data.Event;
import utils.ProducerStringSerializationSchema;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;


public class Queries {

    public static void runQueries(DataStream<Event> stream, String kafkaAddress){

        KeyedStream<Event, String> keyedStream = stream
                .keyBy(Event::getSymbol);

        //KafkaSink<String> sink = KafkaSink.<String>builder().setBootstrapServers(kafkaAddress).setKafkaProducerConfig(getFlinkPropAsProducer()).setRecordSerializer(KafkaRecordSerializationSchema.builder().setTopic(Config.TOPIC_RES).setValueSerializationSchema(new SimpleStringSchema()).build()).setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE).build();

        DataStreamSink<String> finalOutputDataStream = keyedStream
                .window(TumblingEventTimeWindows.of(Time.minutes(Config.windowLen)))
                .aggregate(new MyAggregateFunction(), new MyProcessWindowFunction())
                .windowAll(TumblingEventTimeWindows.of(Time.minutes(Config.windowLen)))
                .process(new ProcessAllWindowFunction<FinalOutput, String, TimeWindow>() {
                    @Override
                    public void process(ProcessAllWindowFunction<FinalOutput, String, TimeWindow>.Context context, Iterable<FinalOutput> elements, Collector<String> out) throws Exception {
                        Timestamp start = new Timestamp(context.window().getStart());
                        Timestamp end = new Timestamp(context.window().getEnd());

                        //System.out.println("proc-time = "+new Timestamp(System.currentTimeMillis()));
                        System.out.println("Firing window: "+new Date(context.window().getStart()));

                        elements.forEach( element -> {
                            /*
                            if(element.getTimeBatch().size()>0 ){
                                System.out.println("inPROC: "+element.getBatch()+","+element.getTimeBatch()+","+element.getTimeBatch().values());
                            }
                             */

                            if (element.getTimeBatch().containsKey(element.getBatch().toString())){
                                long procStart = element.getTimeBatch().get(element.getBatch().toString()).getTime();
                                long diff = System.currentTimeMillis() - procStart;
                                System.out.println(element.getBatch()+","+element.getTimeBatch().get(element.getBatch().toString())+","+procStart+","+new Timestamp(System.currentTimeMillis())+","+System.currentTimeMillis()+","+diff);
                            }

                            String stringToSend =
                                    element.getBatch()+","+
                                            element.getSymbol()+","+
                                            element.getSymbol_WindowEma38().get(element.getSymbol())._2+","+
                                            element.getSymbol_WindowEma100().get(element.getSymbol())._2+","+
                                            element.getSymbol_buyCrossovers().get(element.getSymbol())+","+
                                            element.getSymbol_sellCrossovers().get(element.getSymbol())+","+
                                            end;
                            ;
                            //System.out.println("stringToSend = "+stringToSend);
                            out.collect(stringToSend);

                        });

                    }
                })
                .addSink(new FlinkKafkaProducer<String>(Config.TOPIC_RES,
                        new ProducerStringSerializationSchema(Config.TOPIC_RES),
                        getFlinkPropAsProducer(kafkaAddress),
                        FlinkKafkaProducer.Semantic.EXACTLY_ONCE));;

/*
                finalOutputDataStream
                .keyBy(FinalOutput::getBatch)
                .window(TumblingEventTimeWindows.of(Time.minutes(Config.windowLen)))
                .process(new ProcessWindowFunction<FinalOutput, String, Integer, TimeWindow>() {
                    @Override
                    public void process(Integer batchKey, ProcessWindowFunction<FinalOutput, String, Integer, TimeWindow>.Context context, Iterable<FinalOutput> elements, Collector<String> out) throws Exception {
                        Timestamp start = new Timestamp(context.window().getStart());
                        Timestamp end = new Timestamp(context.window().getEnd());
                        String startWindow = start.toString();
                        String endWindow = end.toString();

                        System.out.println("key = "+batchKey+" "+start);

                        elements.forEach( element -> {
                            if (batchKey == element.getBatch()){
                                //System.out.println("key2 = "+batchKey);
                                String stringToSend =
                                        element.getBatch()+","+
                                        element.getSymbol()+","+
                                        element.getSymbol_WindowEma38().get(element.getSymbol())._2+","+
                                        element.getSymbol_WindowEma100().get(element.getSymbol())._2+","+
                                        element.getSymbol_buyCrossovers().get(element.getSymbol())+","+
                                        element.getSymbol_sellCrossovers().get(element.getSymbol())+","+
                                        end;
                        ;
                                //System.out.println("stringToSend = "+stringToSend);
                                out.collect(stringToSend);
                            }
                        });
                    }
                })
                //.sinkTo(sink);
                .addSink(new FlinkKafkaProducer<String>(Config.TOPIC_RES,
                        new utils.ProducerStringSerializationSchema(Config.TOPIC_RES),
                        getFlinkPropAsProducer(kafkaAddress),
                        FlinkKafkaProducer.Semantic.EXACTLY_ONCE));

 */
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
