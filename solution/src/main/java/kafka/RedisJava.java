package kafka;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.timeseries.RedisTimeSeriesCommands;
import redis.clients.jedis.timeseries.TimeSeriesProtocol;

public class RedisJava {
    public static void main(String[] args) {

        Jedis jedis = new Jedis("localhost",6379);
        System.out.println("Connect to redis successfully!");
        //from w w  w .j a v  a  2s. c  o m
        System.out.println("Jedis pings: "+jedis.ping());

        jedis.set("name", "lipanpan");
        jedis.set("name2", String.valueOf(1));
        jedis.set("name2", String.valueOf(500));
        //jedis.set("prova", "cecilia");
        jedis.append("name", " is my lover!");
        String jrs = jedis.get("name");
        System.out.println(jrs);

        /*
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        //jedis.auth("password");
        System.out.println("Connection to server sucessfully");
        //check whether server is running or not
        System.out.println("Server is running: "+jedis.ping());

        //set the data in redis string
        jedis.set("tutorial-name", "Redis tutorial");
        // Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));


         */

    }
}
