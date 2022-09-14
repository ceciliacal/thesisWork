package flink;

import data.Event;
import kafka.Consumer;
import org.apache.flink.api.common.functions.MapFunction;
import scala.Tuple2;

import java.io.PrintWriter;
import java.sql.Timestamp;


public class MapFunctionEvent implements MapFunction<String, Event> {

    @Override
    public Event map(String value) throws Exception {

        //System.out.println("value = "+value);
        String line[] = value.split(",");
        Timestamp currTime = null;
        Long currMs = null;

        Event event = new Event(line[0], Integer.parseInt(line[4]), line[1],line[2], Float.parseFloat(line[3]), Integer.parseInt(line[5]));

        if (event.getNumEvent()==0){
            currMs = System.currentTimeMillis();
            currTime = new Timestamp(currMs);
            //System.out.println("map-time = "+currTime+"  "+event);
        }

        if (currMs!=null && currTime!=null){
            event.setCurrTime(new Tuple2<>(currMs,currTime));
        } else {
            event.setCurrTime(null);
        }


        return event;

    }
}
