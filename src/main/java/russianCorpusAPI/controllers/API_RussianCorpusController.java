package russianCorpusAPI.controllers;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import russianCorpusAPI.databaseOperations.PoemDB_Operation;
import russianCorpusAPI.model.LineOfPoem;
import russianCorpusAPI.model.Poem;

import java.util.*;
import java.util.stream.Collectors;

//after dockerizing frontend:
@CrossOrigin(origins = {"http://94.130.181.51:8103"})
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("apiPoetryCorpus/")
public class API_RussianCorpusController {

    private static final Logger logger = LoggerFactory.getLogger(API_RussianCorpusController.class);

    @PostMapping(value = "get_poems_and_lines", headers = {"Content-type=application/json"})
    public List<Poem> getPoemsAndLines(@RequestBody String params) {
        logger.info("params: {}", params);

        List<Poem> listOfPoems = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(params);
            JSONObject jsonParameter = (JSONObject) jsonObject.get("parameters");
            //get all for author
            Integer authorCode = ((Long) jsonParameter.get("authorCode")).intValue();
            listOfPoems = new PoemDB_Operation().getPoemsByAuthorID(authorCode);

            if (listOfPoems == null) {
                return null;
            }
            if (listOfPoems.size() == 0) {
                return listOfPoems;
            }

            //need to sort
            Collections.sort(listOfPoems, new Comparator<Poem>() {
                @Override
                public int compare(Poem u1, Poem u2) {
                    return (u1.getId_verse() - u2.getId_verse());
                }
            });
            //sorting lines in poem
            for (Poem poem : listOfPoems) {
                Set<LineOfPoem> lineOfPoemSet = poem.getLinesOfPoem();
                List<LineOfPoem> lineOfPoemList = lineOfPoemSet.stream().collect(Collectors.toList());
                Collections.sort(lineOfPoemList, new Comparator<LineOfPoem>() {
                    @Override
                    public int compare(LineOfPoem u1, LineOfPoem u2) {
                        return (u1.getRow_key() - u2.getRow_key());
                    }
                });
                //don't download:
                poem.setLinesOfPoemList(lineOfPoemList);
                poem.setLinesOfPoem(null);
            }

            //get books, if book code > 0
            Integer bookCode = Integer.parseInt((String) jsonParameter.get("bookCode"));
            if (bookCode > 0) {
                listOfPoems = listOfPoems.stream().filter(a -> (a.getBook_source().getId_book_source() == bookCode)).collect(Collectors.toList());
            }

            //get poem with year
            try {
                Integer year_writing = Integer.parseInt((String) "" + (jsonParameter.get("poem_year")));
                if(year_writing > 0){
                    listOfPoems = setYearWritingFilter(listOfPoems, year_writing);
                }
            } catch (NumberFormatException e){
                //nothing: it isn't year
            }

            //get poems with substringTitle
            String substringTitle = (String) jsonParameter.get("substringTitle");
            if (!substringTitle.isEmpty()) {
                listOfPoems = setSubstringTitleFilter(listOfPoems, substringTitle);
            }

            //get poems with meter:
            String meter = (String) jsonParameter.get("meter");
            if (!meter.trim().equals("Без фильтра")) {
                listOfPoems = setMeterFilter(listOfPoems, meter);
            }

            //number of icts
            String icts = (String) ("" + jsonParameter.get("maxNumberOfIcts"));
            if (!icts.isEmpty()) {
                Integer maxNumberOfIcts = Integer.parseInt(icts);
                if (maxNumberOfIcts > 0) {
                    listOfPoems = setNumberOfIctsFilter(listOfPoems, maxNumberOfIcts);
                }
            }

            //number of feet
            String feet = (String) ("" + jsonParameter.get("numberOfFeet"));
            if (!feet.isEmpty()) {
                Integer numberOfFeet = Integer.parseInt(feet);
                if (numberOfFeet > 0) {
                    listOfPoems = setNumberOfFeetFilter(listOfPoems, numberOfFeet);
                }
            }

            //line's meter
            String lineMeter = (String) jsonParameter.get("lineMeter");
            if (!lineMeter.trim().equals("Без фильтра")) {
                listOfPoems = setLineMeterFilter(listOfPoems, lineMeter);
            }

            //substring
            String substring = (String) jsonParameter.get("substring");
            if (!substring.isEmpty()) {
                listOfPoems = setSubstringFilter(listOfPoems, substring);
            }

            //subRepresentation
            String subRepresentation = (String) jsonParameter.get("subRepresentation");
            if (!subRepresentation.isEmpty()) {
                listOfPoems = setSubrepresentationFilter(listOfPoems, subRepresentation);
            }

            //Strophe type
            String stropheType = (String) jsonParameter.get("stropheType");
            if (!stropheType.trim().equals("Без фильтра")) {
                listOfPoems = setStropheTypeFilter(listOfPoems, stropheType);
            }

            //rhyme type in strophe
            String rhymeTypeInStrophe = (String) jsonParameter.get("rhymeTypeInStrophe");
            if (!rhymeTypeInStrophe.trim().equals("Без фильтра")) {
                listOfPoems = setRhymeTypeInStropheFilter(listOfPoems, rhymeTypeInStrophe);
            }

            //solidForm
            String solidForm = (String) jsonParameter.get("solidForm");
            if (!solidForm.trim().equals("Без фильтра")) {
                listOfPoems = setSolidFormFilter(listOfPoems, solidForm);
            }

            //ending
            String ending = ((String) jsonParameter.get("ending")).trim();
            if (!ending.isEmpty()) {
                listOfPoems = setEndingFilter(listOfPoems, ending);
            }

            //kind of rhyme
            String rhymeKind = ((String) jsonParameter.get("rhymeKind")).trim();
            if (!rhymeKind.trim().equals("Без фильтра")) {
                listOfPoems = setRhymeKindFilter(listOfPoems, rhymeKind);
            }

            //clause
            String clause = ((String) jsonParameter.get("clause")).trim();
            if (!clause.isEmpty()) {
                listOfPoems = setClauseFilter(listOfPoems, clause);
            }

            //grammatical form of rhyme
            String grammaticalFormRhyme = ((String) jsonParameter.get("grammaticalFormRhyme")).trim();
            if (!grammaticalFormRhyme.trim().equals("Без фильтра")) {
                listOfPoems = setGrammaticalFormRhymeFilter(listOfPoems, grammaticalFormRhyme);
            }

            //sort of rhyme (accuracy and so on)
            String sortOfRhyme = ((String) jsonParameter.get("sortOfRhyme")).trim();
            if (!sortOfRhyme.trim().equals("Без фильтра")) {
                listOfPoems = setSortOfRhymeFilter(listOfPoems, sortOfRhyme);
            }

        } catch (ParseException e) {
            logger.error("Can't parse json query {}", e.getMessage());
        }

        //to avoid Poems with empty Lines (after filters it can be). That's why:
        listOfPoems = listOfPoems.stream().filter(a -> (!a.getLinesOfPoemList().isEmpty())).collect(Collectors.toList());

        logger.info("Size of result {}", listOfPoems.size());
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
        if(grammaticalFormRhyme.equals("Глагол")){
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream()
                    .filter(line -> (line.getRhyme().getGrammatical().trim().equals("Инфинитив") ||
                    line.getRhyme().getGrammatical().trim().equals("Глагол в личной форме"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Местоимение")){
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getGrammatical().trim().contains("Местоимен"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Причастие")){
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream()
                    .filter(line -> (line.getRhyme().getGrammatical().trim().equals("Причастие") ||
                            line.getRhyme().getGrammatical().trim().equals("Краткое причастие"))).collect(Collectors.toList())));
        } else if (grammaticalFormRhyme.equals("Прилагательное")){
            listOfPoems.forEach(poem -> poem.setLinesOfPoemList(poem.getLinesOfPoemList().stream().filter(line -> (line.getRhyme().getGrammatical().trim().contains("рилагательное"))).collect(Collectors.toList())));
        } else{
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

}
