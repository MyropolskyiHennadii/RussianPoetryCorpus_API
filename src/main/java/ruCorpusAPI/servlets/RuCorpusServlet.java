package ruCorpusAPI.servlets;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import ruCorpusAPI.config.CommonConstants;
import ruCorpusAPI.databaseOperations.PoemDB_Operation;
import ruCorpusAPI.exceptions.WrongRequestData;
import ruCorpusAPI.model.LineOfPoem;
import ruCorpusAPI.model.Poem;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "ApiPoetryCorpus", urlPatterns = "/apiPoetryCorpus")
public class RuCorpusServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(RuCorpusServlet.class);
    private static final Properties properties = CommonConstants.getInstance().getCommonProperty();

    @Override
    public void init() {
        LOGGER.info("Servlet's initialisation {}", getServletContext().getClass().getName());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response);

        LOGGER.debug("doPost");
        try {
            String params = getRequestData(request);
            LOGGER.info("doPost -> query: {}", params);
            if (params == null || params.isEmpty()) {
                throw new WrongRequestData("doPost -> Request data is null or empty.");
            }
            List<Poem> listPoems = getPoemsAndLines(params);
            LOGGER.trace("doPost -> Transform result list of poems to json object.");
            JSONArray jsonToReturn = getResultJsonDataForClient(listPoems);
            sendOkToClient(response, jsonToReturn);

        } catch (WrongRequestData e) {
            sendErrorToClient(response,
                    "doPost -> " + e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            sendErrorToClient(response,
                    "doPost -> Impossible read request data:" + e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (JSONException e){
            sendErrorToClient(response,
                    "doPost -> Impossible read json-data:" + e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private JSONArray getResultJsonDataForClient(List<Poem> listPoems) {
        JSONArray poems = new JSONArray();
        for (Poem poem: listPoems) {
            poems.put(poem.composeJsonObject());
        }
        LOGGER.trace("Transforming list to json was done successfully.");
        return poems;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        sendErrorToClient(response, "Method not allowed", 405);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
    }

    public List<Poem> getPoemsAndLines(String parameters) throws JSONException {

        LOGGER.debug("getPoemsAndLines -> Beginning.");
        JSONObject jsonParameters = new JSONObject(parameters).getJSONObject("parameters");

        //get all for author
        Integer authorCode = jsonParameters.getInt("authorCode");
        LOGGER.trace("Author code: {}", authorCode);
        List<Poem> listOfPoems = new PoemDB_Operation().getPoemsByAuthorID(authorCode);
        if (listOfPoems == null) {
            return null;
        }
        if (listOfPoems.size() == 0) {
            return listOfPoems;
        }
        LOGGER.trace("getPoemsAndLines -> List of poet for author was received.");
        //need to sort
        Collections.sort(listOfPoems, (u1, u2) -> (u1.getId_verse() - u2.getId_verse()));
        //sorting lines in poem
        for (Poem poem : listOfPoems) {
            Set<LineOfPoem> lineOfPoemSet = poem.getLinesOfPoem();
            List<LineOfPoem> lineOfPoemList = lineOfPoemSet.stream().collect(Collectors.toList());
            Collections.sort(lineOfPoemList, (u1, u2) -> (u1.getRow_key() - u2.getRow_key()));
            poem.setLinesOfPoemList(lineOfPoemList);
            //don't download:
            //poem.setLinesOfPoem(null);
        }
        LOGGER.trace("getPoemsAndLines -> Sorting of lines was done. Beginning to filter.");
        //get books, if book code > 0
        int bookCode = jsonParameters.getInt("bookCode");
        if (bookCode > 0) {
            listOfPoems = listOfPoems.stream().filter(a -> (a.getBook_source().getId_book_source() == bookCode)).collect(Collectors.toList());
        }

        //get poem with year
        try {
            Integer year_writing = jsonParameters.getInt("poem_year");
            if (year_writing > 0) {
                listOfPoems = setYearWritingFilter(listOfPoems, year_writing);
            }
        } catch (JSONException e) {
            //nothing: there isn't year
        } catch (NumberFormatException e) {
            //nothing: there isn't year
        }

        //get poems with substringTitle
        String substringTitle = jsonParameters.getString("substringTitle");
        if (!substringTitle.isEmpty()) {
            listOfPoems = setSubstringTitleFilter(listOfPoems, substringTitle);
        }

        //get poems with meter:
        String meter = jsonParameters.getString("meter");
        if (!meter.trim().equals("Без фильтра")) {
            listOfPoems = setMeterFilter(listOfPoems, meter);
        }

        //number of icts
        String icts = jsonParameters.getString("maxNumberOfIcts");
        if (!icts.isEmpty()) {
            Integer maxNumberOfIcts = Integer.parseInt(icts);
            if (maxNumberOfIcts > 0) {
                listOfPoems = setNumberOfIctsFilter(listOfPoems, maxNumberOfIcts);
            }
        }

        //number of feet
        String feet = jsonParameters.getString("numberOfFeet");
        if (!feet.isEmpty()) {
            Integer numberOfFeet = Integer.parseInt(feet);
            if (numberOfFeet > 0) {
                listOfPoems = setNumberOfFeetFilter(listOfPoems, numberOfFeet);
            }
        }

        //line's meter
        String lineMeter = jsonParameters.getString("lineMeter");
        if (!lineMeter.trim().equals("Без фильтра")) {
            listOfPoems = setLineMeterFilter(listOfPoems, lineMeter);
        }

        //substring
        String substring = jsonParameters.getString("substring");
        if (!substring.isEmpty()) {
            listOfPoems = setSubstringFilter(listOfPoems, substring);
        }

        //subRepresentation
        String subRepresentation = jsonParameters.getString("subRepresentation");
        if (!subRepresentation.isEmpty()) {
            listOfPoems = setSubrepresentationFilter(listOfPoems, subRepresentation);
        }

        //Strophe type
        String stropheType = jsonParameters.getString("stropheType");
        if (!stropheType.trim().equals("Без фильтра")) {
            listOfPoems = setStropheTypeFilter(listOfPoems, stropheType);
        }

        //rhyme type in strophe
        String rhymeTypeInStrophe = jsonParameters.getString("rhymeTypeInStrophe");
        if (!rhymeTypeInStrophe.trim().equals("Без фильтра")) {
            listOfPoems = setRhymeTypeInStropheFilter(listOfPoems, rhymeTypeInStrophe);
        }

        //solidForm
        String solidForm = jsonParameters.getString("solidForm");
        if (!solidForm.trim().equals("Без фильтра")) {
            listOfPoems = setSolidFormFilter(listOfPoems, solidForm);
        }

        //ending
        String ending = jsonParameters.getString("ending").trim();
        if (!ending.isEmpty()) {
            listOfPoems = setEndingFilter(listOfPoems, ending);
        }

        //kind of rhyme
        String rhymeKind = jsonParameters.getString("rhymeKind").trim();
        if (!rhymeKind.trim().equals("Без фильтра")) {
            listOfPoems = setRhymeKindFilter(listOfPoems, rhymeKind);
        }

        //clause
        String clause = jsonParameters.getString("clause").trim();
        if (!clause.isEmpty()) {
            listOfPoems = setClauseFilter(listOfPoems, clause);
        }

        //grammatical form of rhyme
        String grammaticalFormRhyme = jsonParameters.getString("grammaticalFormRhyme").trim();
        if (!grammaticalFormRhyme.trim().equals("Без фильтра")) {
            listOfPoems = setGrammaticalFormRhymeFilter(listOfPoems, grammaticalFormRhyme);
        }

        //sort of rhyme (accuracy and so on)
        String sortOfRhyme = jsonParameters.getString("sortOfRhyme").trim();
        if (!sortOfRhyme.trim().equals("Без фильтра")) {
            listOfPoems = setSortOfRhymeFilter(listOfPoems, sortOfRhyme);
        }

        //to avoid Poems with empty Lines (after filters it can be). That's why:
        listOfPoems = listOfPoems.stream().filter(a -> (!a.getLinesOfPoemList().isEmpty())).collect(Collectors.toList());

        LOGGER.debug("Filter were applied. Size of listOfPoems {}", listOfPoems.size());
        return listOfPoems;
    }

    private List<Poem> setYearWritingFilter(List<Poem> listOfPoems, Integer year_writing) {
        return listOfPoems.stream().filter(a -> (a.getPoem_year() == year_writing))
                .collect(Collectors.toList());
    }

    private List<Poem> setSortOfRhymeFilter(List<Poem> listOfPoems, String sortOfRhyme) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getRhyme_sort().trim().equals(sortOfRhyme))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setGrammaticalFormRhymeFilter(List<Poem> listOfPoems, String grammaticalFormRhyme) {
        if (grammaticalFormRhyme.equals("Глагол")) {
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream()
                    .filter(line -> (line.getRhyme().getGrammatical().trim().equals("Инфинитив") ||
                            line.getRhyme().getGrammatical().trim().equals("Глагол в личной форме"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Местоимение")) {
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getGrammatical().trim().contains("Местоимен"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Причастие")) {
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream()
                    .filter(line -> (line.getRhyme().getGrammatical().trim().equals("Причастие") ||
                            line.getRhyme().getGrammatical().trim().equals("Краткое причастие"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Прилагательное")) {
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getGrammatical().trim().contains("рилагательное"))).collect(Collectors.toList())));
        } else {
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getGrammatical().trim().contains(grammaticalFormRhyme))).collect(Collectors.toList())));
        }
        return listOfPoems;
    }

    private List<Poem> setClauseFilter(List<Poem> listOfPoems, String clause) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getClause().trim().equals(clause))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setRhymeKindFilter(List<Poem> listOfPoems, String rhymeKind) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getRhyme_kind().equals(rhymeKind))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setEndingFilter(List<Poem> listOfPoems, String ending) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getEnding().equals(ending))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setSolidFormFilter(List<Poem> listOfPoems, String solidForm) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getStrophe().getSolid_form().equals(solidForm))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setRhymeTypeInStropheFilter(List<Poem> listOfPoems, String rhymeTypeInStrophe) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getStrophe().getRhyme_type().equals(rhymeTypeInStrophe))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setStropheTypeFilter(List<Poem> listOfPoems, String stropheType) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getStrophe().getStrophe_type().equals(stropheType))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setSubstringTitleFilter(List<Poem> listOfPoems, String substringTitle) {
        return listOfPoems.stream().filter(a -> (
                        a.getEpigraph().contains(substringTitle)
                                || a.getDedicated_to().contains(substringTitle)
                                || a.getPoem_info().contains(substringTitle)
                                || a.getPoem_title().contains(substringTitle)
                                || a.getTranslate_from().contains(substringTitle)))
                .collect(Collectors.toList());
    }

    private List<Poem> setSubrepresentationFilter(List<Poem> listOfPoems, String subRepresentation) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(a -> (a.getRepresentation().contains(subRepresentation))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setSubstringFilter(List<Poem> listOfPoems, String substring) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(a -> (a.getLine().contains(substring))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setNumberOfFeetFilter(List<Poem> listOfPoems, Integer numberOfFeet) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(a -> (a.getNumber_of_tonic_feet() == numberOfFeet)).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setLineMeterFilter(List<Poem> listOfPoems, String lineMeter) {
        listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(a -> (a.getMeter_group().contains(lineMeter))).collect(Collectors.toList())));
        return listOfPoems;
    }

    private List<Poem> setNumberOfIctsFilter(List<Poem> listOfPoems, Integer maxNumberOfIcts) {
        return listOfPoems.stream().filter(a -> (a.getMax_number_of_stress_in_lines() == maxNumberOfIcts))
                .collect(Collectors.toList());
    }


    private List<Poem> setMeterFilter(List<Poem> listOfPoems, String meter) {
        if (!meter.trim().equals("Тонические")) {
            listOfPoems = listOfPoems.stream().filter(a -> (a.getMeter_group().trim().equals(meter))).collect(Collectors.toList());
        } else {
            listOfPoems = listOfPoems.stream()
                    .filter(a -> (!a.getMeter_group().trim().equals("Хореи")
                                    && !a.getMeter_group().trim().equals("Ямбы")
                                    && !a.getMeter_group().trim().equals("Дактили")
                                    && !a.getMeter_group().trim().equals("Амфибрахии")
                                    && !a.getMeter_group().trim().equals("Анапесты")
                                    && !a.getMeter_group().trim().equals("Верлибры")
                                    && !a.getMeter_group().trim().equals("Смешанные размеры")
                            )
                    ).collect(Collectors.toList());
        }
        return listOfPoems;
    }

    /**
     * sends error to client and logs it
     *
     * @param response
     * @param message
     */
    public static void sendErrorToClient(HttpServletResponse response, String message, int returnCode) {
        LOGGER.error(message);
        response.setStatus(returnCode);
        LOGGER.info("Status {} was sent to client.", returnCode);
    }

    /**
     * sends return data and OK to client
     *
     * @param response
     * @param returnData
     */
    public static void sendOkToClient(HttpServletResponse response, JSONArray returnData) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print(returnData.toString());
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Can not write to response: {}", e.getMessage());
        }
        response.setStatus(HttpServletResponse.SC_OK);
        LOGGER.info("Status OK was sent to client");
    }

    /**
     * sets headers for servlet's response
     *
     * @param resp
     */
    public static void setAccessControlHeaders(HttpServletResponse resp) {

        resp.setHeader("Access-Control-Allow-Origin", properties.getProperty("Access.Control.Allow.Origin"));
        resp.setHeader("Access-Control-Allow-Methods", properties.getProperty("Access.Control.Allow.Methods"));
        resp.setHeader("Access-Control-Allow-Headers", properties.getProperty("Access.Control.Allow.Headers"));
        /*resp.setHeader("Access-Control-Allow-Credentials", "true");*/
        /*resp.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");*/
        /*resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");*/
    }


    /**
     * gets data from request
     *
     * @param req
     * @return
     */
    private String getRequestData(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        req.setCharacterEncoding("UTF-8");
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
}
