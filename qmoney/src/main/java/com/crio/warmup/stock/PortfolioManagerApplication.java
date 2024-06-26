
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  private final static String TOKEN = "5a541a54cae7d8255efc0c03800103d3dcb2b1ce";
  //private final static String TOKEN = "736e831219e0cb6291fa673dc6006fcfa1a06436";


  public static String getToken() {
    return TOKEN;
  }

  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager =
      PortfolioManagerFactory.getPortfolioManager(restTemplate);
  

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.


  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<String> listOfSymbols = new ArrayList<>();
    ObjectMapper objectMapper = getObjectMapper();
    File filename = resolveFileFromResources(args[0]);

    PortfolioTrade[] trades = objectMapper.readValue(filename, PortfolioTrade[].class);

    for (PortfolioTrade trade : trades) {
      listOfSymbols.add(trade.getSymbol());
    }
    return listOfSymbols;
  }


  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 =
        "/home/crio-user/workspace/crio-4as1931028-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@3bf7ca37";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }

 // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> totalReturnsDto = new ArrayList<TotalReturnsDto>();
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    LocalDate endDate = LocalDate.parse(args[1]);
    for (PortfolioTrade t : portfolioTrades) {
      String url = prepareUrl(t, endDate, getToken());
      System.out.println(url);
      TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
      if (results != null) {
        totalReturnsDto
            .add(new TotalReturnsDto(t.getSymbol(), results[results.length - 1].getClose()));
      }
    }

    Collections.sort(totalReturnsDto, new Comparator<TotalReturnsDto>() {

      @Override
      public int compare(TotalReturnsDto o1, TotalReturnsDto o2) {
        return (int) (o1.getClosingPrice().compareTo(o2.getClosingPrice()));
      }

    });

    List<String> listOfSortSymbolsOnClosingPrice = new ArrayList<>();
    for (int i = 0; i < totalReturnsDto.size(); i++) {
      listOfSortSymbolsOnClosingPrice.add(totalReturnsDto.get(i).getSymbol());
    }
    return listOfSortSymbolsOnClosingPrice;
  }



  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioTrades = getObjectMapper().readValue(
      resolveFileFromResources(filename), new TypeReference<List<PortfolioTrade>>() {});
  return portfolioTrades;
  }


  // TODO:
  // Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String Url = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
        + trade.getPurchaseDate().toString() + "&endDate=" + endDate + "&token=" + token;
    return Url;
  }

  // TODO:
  // Ensure all tests are passing using below command
  // ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    String url = prepareUrl(trade, endDate, token);
    RestTemplate restTemplate = new RestTemplate();
    TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);
    return Arrays.asList(tiingoCandles);
  }

  


  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    LocalDate localDate = LocalDate.parse(args[1]);
    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      List<Candle> candles = fetchCandles(portfolioTrade, localDate, getToken());
      AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(localDate, portfolioTrade,
          getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
      annualizedReturns.add(annualizedReturn);
    }
    return annualizedReturns.stream()
        .sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn())) // descending
                                                                                                // order
        .collect(Collectors.toList());
  }
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,
      Double buyPrice, Double sellPrice) {
    double totalNumberYears = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365.2422; // period
    double totalReturns = (sellPrice - buyPrice) / buyPrice; // absoluteReturn
    double annualizedReturns = Math.pow((1.0 + totalReturns), (1.0 / totalNumberYears)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }



  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  // Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  // call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  private static String readFileAsString(String fileName) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(resolveFileFromResources(fileName).toPath()), "UTF-8");
  }


  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    
    // List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    // LocalDate endDate = LocalDate.parse(args[1]);
    // return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioManager portfolioManager =
        PortfolioManagerFactory.getPortfolioManager(new RestTemplate());
    List<PortfolioTrade> portfolioTrades =
        objectMapper.readValue(contents, new TypeReference<List<PortfolioTrade>>() {});
    return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
  }

  
  

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainReadFile(args));
   // printJsonObject(mainReadQuotes(args));
    //printJsonObject(mainReadQuotes(new String[]{"trades.json" ,"2020-01-01"}));


   // printJsonObject(mainCalculateSingleReturn(args));
    printJsonObject(mainCalculateSingleReturn(new String[] {"trades.json", "2020-01-01"}));



   // printJsonObject(mainCalculateReturnsAfterRefactor(args));
  // printJsonObject(mainCalculateReturnsAfterRefactor(new String[] {"trades.json", "2020-01-01"}));


  }
}

