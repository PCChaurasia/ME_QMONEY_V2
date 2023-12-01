
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Collection;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.List;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
// import javax.management.RuntimeErrorException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  public static final String TOKEN = "5a541a54cae7d8255efc0c03800103d3dcb2b1ce";

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  public RestTemplate restTemplate = new RestTemplate();

  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }



  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF



  // private Comparator<AnnualizedReturn> getComparator() {
  // return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  // }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

    return stockQuotesService.getStockQuote(symbol, from, to);

  //   if (from.compareTo(to) >= 0) {
  //     throw new RuntimeException();
  //   }
  //   String url = buildUri(symbol, from, to);
  //   String stocks = restTemplate.getForObject(url, String.class);
  //   ObjectMapper objectMapper = getObjectMapper();
  //   TiingoCandle[] stocksStarttoEnd = objectMapper.readValue(stocks, TiingoCandle[].class);

  //   if (stocksStarttoEnd == null) {
  //     return new ArrayList<Candle>();
  //   } else {
  //     List<Candle> stocklist = Arrays.asList(stocksStarttoEnd);
  //     return stocklist;
  //   }
  // }

  // private static ObjectMapper getObjectMapper() {
  //   ObjectMapper objectMapper = new ObjectMapper();
  //   objectMapper.registerModule(new JavaTimeModule());
  //   return objectMapper;
  }



  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate="
        + startDate + "&endDate=" + endDate + "&token=" + TOKEN;


    return uriTemplate;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();

    for (int i = 0; i < portfolioTrades.size(); i++) {
      AnnualizedReturn annualizedReturn = getAnnualizedReturn(portfolioTrades.get(i), endDate);

      annualizedReturns.add(annualizedReturn);

    }

    return annualizedReturns.stream()
        .sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn())) // descending
                                                                                                // order
        .collect(Collectors.toList());
  }


  // TODO Auto-generated method stub
  // return null;



  private AnnualizedReturn getAnnualizedReturn(PortfolioTrade portfolioTrade, LocalDate endDate) {
    AnnualizedReturn annualizedReturn;
    String symbol = portfolioTrade.getSymbol();
    LocalDate startLocalDate = portfolioTrade.getPurchaseDate();

    try {
      List<Candle> stocksStarttoEnd = getStockQuote(symbol, startLocalDate, endDate);

      Candle stockStartDate = stocksStarttoEnd.get(0);
      Candle stockLastDate = stocksStarttoEnd.get(stocksStarttoEnd.size() - 1);


      Double buyPrice = stockStartDate.getOpen();
      Double sellPrice = stockLastDate.getClose();

      Double totalReturns = (sellPrice - buyPrice) / buyPrice;
      double totalNumberYears =
          ChronoUnit.DAYS.between(portfolioTrade.getPurchaseDate(), endDate) / 365d;

      double annualizedReturns = Math.pow((1.0 + totalReturns), (1.0 / totalNumberYears)) - 1;
      annualizedReturn =
          new AnnualizedReturn(portfolioTrade.getSymbol(), annualizedReturns, totalReturns);

    } catch (JsonProcessingException e) {
      annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);

    }
    return annualizedReturn;
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }



  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Modify the function #getStockQuote and start delegating to calls to
  // stockQuoteService provided via newly added constructor of the class.
  // You also have a liberty to completely get rid of that function itself, however, make sure
  // that you do not delete the #getStockQuote function.

  private StockQuotesService stockQuotesService;

  PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }


}
