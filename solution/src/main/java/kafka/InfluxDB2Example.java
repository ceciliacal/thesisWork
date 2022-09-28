package kafka;

//package example;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;

public class InfluxDB2Example {
    public static void main(final String[] args) {

        String input = "2021-11-08 08:05:00.0";
        String end = input.substring(0, input.length() - 2);
        System.out.println("new= "+end);
        //String input = 2021-11-08 08:05:00.0 uuuu-M-d hh:mm:ss
        DateTimeFormatter f = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" , Locale.UK);  // Specify locale to determine human language and cultural norms used in translating that input string.
        LocalDateTime ldt = LocalDateTime.parse( end , f );
        System.out.println("--INSTANT: "+ldt.atZone(ZoneId.of("Europe/London")).toInstant());

        /*
        // You can generate an API token from the "API Tokens Tab" in the UI
        String token = "QGJ56WM5iU6wqiBoQG8Or_YfdjaOVzyTHDeJLi0eGsysTsooGytu-3ov_HRFWqh4kyOKfwnTSVIFW8WZLyeZTQ==";
        String bucket = "prova";
        String org = "uniroma2";

        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());

        //Per i risultati: già sicuramente il measurement è sempre lo stesso

        Point point1 = Point.measurement("sensor")
                .addTag("sensor_id", "TLM0103")
                .addField("location", "Mechanical Room")
                .addField("model_number", "TLM90012Z")
                .time(Instant.now(), WritePrecision.MS);



        Point point2 = Point.measurement("sensor")
                .addTag("sensor_id", "TLM0200")
                .addField("location", "Conference Room")
                .addField("model_number", "TLM89092B")
                .time(Instant.now(), WritePrecision.MS);



        Point point3 = Point.measurement("sensor")
                .addTag("sensor_id", "TLM0201")
                .addField("location", "Room 390")
                .addField("model_number", "TLM89102B")
                .time(Instant.now(), WritePrecision.MS);

        List<Point> listPoint = new ArrayList<Point>();

        listPoint.add(point1);
        listPoint.add(point2);
        listPoint.add(point3);


        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoints(bucket, org, listPoint);

         */



    }
}

/*
        Point point = Point
                .measurement("mem")
                .addTag("host", "host1")
                .addField("used_percent", 23.43234543)
                .time(Instant.now(), WritePrecision.NS)
                ;

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoint(bucket, org, point);
        */

