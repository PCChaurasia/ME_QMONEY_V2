
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {
  public static final String TOKEN = "5a541a54cae7d8255efc0c03800103d3dcb2b1ce";

  private RestTemplate restTemplate;
  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    // TODO Auto-generated method stub
    List<Candle>stocklist;
    if(from.compareTo( to)>=0){
      throw new RuntimeException();
    }
    String url = buildUri(symbol, from, to);
    String stocks = restTemplate.getForObject(url,String.class);
    ObjectMapper objectMapper = getObjectMapper();
    

    TiingoCandle[] stocksStarttoEnd = objectMapper.readValue(stocks, TiingoCandle[].class);

    if(stocksStarttoEnd == null){
      stocklist = Arrays.asList(new  TiingoCandle[0]);
    }else{
      stocklist = Arrays.asList(stocksStarttoEnd);
     
    }
    return stocklist;
    //return null;
  }
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    
     String uriTemplate =  "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate="
     + startDate + "&endDate=" + endDate + "&token=" + TOKEN;
 
        

            return uriTemplate;
}

}
