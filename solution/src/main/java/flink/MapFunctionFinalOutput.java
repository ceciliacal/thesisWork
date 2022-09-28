package flink;

import org.apache.flink.api.common.functions.MapFunction;

import java.sql.Timestamp;


public class MapFunctionFinalOutput implements MapFunction<FinalOutput, String> {

    @Override
    public String map(FinalOutput element) throws Exception {

        //System.out.println("--getTimeBatch di ELEMENT: "+element.getTimeBatch()+"   "+element.getSymbol());
        if (element.getTimeBatch().containsKey(element.getBatch())){
            long procStart = element.getTimeBatch().get(element.getBatch()).getTime();
            long diff = System.currentTimeMillis() - procStart;
            System.out.println(element.getBatch()+","+element.getTimeBatch().get(element.getBatch())+","+procStart+","+new Timestamp(System.currentTimeMillis())+","+System.currentTimeMillis()+","+diff);
        }

        String queriesResult =
                element.getBatch()+","+
                        element.getSymbol()+","+
                        element.getSymbol_WindowEma38().get(element.getSymbol())._2+","+
                        element.getSymbol_WindowEma100().get(element.getSymbol())._2+","+
                        element.getSymbol_buyCrossovers().get(element.getSymbol())+","+
                        element.getSymbol_sellCrossovers().get(element.getSymbol())+","+
                        element.getTimeBatch()+","+
                        element.getWindowEnd()+","+
                        element.getPrice();

        //System.out.println(queriesResult);
        return queriesResult;
    }

}