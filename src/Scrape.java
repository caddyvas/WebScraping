import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scrape {

    private Elements covidOverAllElements;
    private Elements covidCountryElements;
    private Elements covidCanadaElements;
    private Elements covidUnitedStatesElements;
    private Elements covidIndiaElements;

    public static void main(String args[]) throws IOException {

        System.out.println("OM");
        Scrape scrape = new Scrape();
        scrape.initializeJsoupItems();

        // From WIKI

        scrape.displayOverallInfo();

        System.out.println();
        scrape.displayCountryWiseInfo();

        System.out.println();
        scrape.displayCanadaStats();

        System.out.println();
        scrape.displayUnitedStatesStats();
        scrape.displayIndiaStats();

    }

    private void displayOverallInfo() {

        // Display Overall cases
        System.out.println("Total Number of Confirmed Cases: " + covidOverAllElements.get(1).text());
        System.out.println("Total Number of Deaths: " + covidOverAllElements.get(2).text());
        System.out.println("Total Number of Recovered Cases: " + covidOverAllElements.get(3).text());

    }

    private void displayCountryWiseInfo() {
        List<CountryWise> countryWiseList = new ArrayList<>();
        for (Element element : covidCountryElements) {

            CountryWise countryWise = new CountryWise();
            String country = element.select("th[scope=row] a[title]").text();
            Elements ele = element.getElementsByTag("td").not("td:eq(5)");

            if (ele.text().isEmpty()) {
                // skip
                continue;
            }

            String confirmedCase = ele.select("td:eq(2)").text();
            String deathCase = ele.select("td:eq(3)").text();
            String recoveredCase = ele.select("td:eq(4)").text();

            countryWise.setCountry(country);
            countryWise.setConfirmedCase(confirmedCase);
            countryWise.setDeadCase(deathCase);
            countryWise.setRecoveredCase(recoveredCase);
            countryWiseList.add(countryWise);
        }

        int i = 1;
        for (CountryWise country : countryWiseList) {
            System.out.println(i + ". " + country.getCountry() + "----" + country.getConfirmedCase() + "--------" + country.getRecoveredCase());
            i++;
        }
    }

    private void displayCanadaStats() {

        if (covidCanadaElements.size() > 1)
            covidCanadaElements.remove(0);

        List<Canada> canadaList = new ArrayList<>();
        Elements ele = covidCanadaElements.select("tbody tr");
        for (Element element : ele) {

            Canada canada = new Canada();
            Elements elem = element.getElementsByTag("td");
            if (elem.text().isEmpty()) {
                continue;
            }

            String province = elem.select("td:eq(0)").text();
            String confirmedCases = elem.select("td:eq(3)").text();
            String presumptiveCases = elem.select("td:eq(4)").text();
            String deathCases = elem.select("td:eq(9)").text();
            String recoveredCases = elem.select("td:eq(8)").text();

            canada.setProvince(province);
            canada.setConfirmedCases(confirmedCases);
            canada.setPresumptiveCases(presumptiveCases);
            canada.setDeathCases(deathCases);
            canada.setRecoveredCases(recoveredCases);

            canadaList.add(canada);
        }

        for (Canada canadaCase : canadaList)
            System.out.println(canadaCase.getProvince() + "---" + canadaCase.getConfirmedCases() + "----" +
                    canadaCase.getPresumptiveCases() + "----" + canadaCase.getRecoveredCases());
    }

    private void displayUnitedStatesStats() {

        List<UnitedStates> unitedStatesList = new ArrayList<>();
        for (Element element : covidUnitedStatesElements) {

            UnitedStates unitedStates = new UnitedStates();

            if (element.text().isEmpty()) {
                continue;
            }

            String province = element.select("th").text();
            String confirmedCases = element.getElementsByTag("td").get(0).text();

            String deathCases = element.getElementsByTag("td").get(1).text();
            String recoveredCases = element.getElementsByTag("td").get(2).text();
            String activeCases = element.getElementsByTag("td").get(3).text();

            unitedStates.setProvince(province);
            unitedStates.setConfirmedCases(confirmedCases);
            unitedStates.setDeathCases(deathCases);
            unitedStates.setRecoveredCases(recoveredCases);
            unitedStates.setActiveCases(activeCases);

            unitedStatesList.add(unitedStates);

        }
        int i = 1;
        for (UnitedStates unitedStates : unitedStatesList) {
            System.out.println(i + "--" + unitedStates.getProvince() + "---" + unitedStates.getConfirmedCases() + "---" +
                    unitedStates.getRecoveredCases());
            i++;
        }
    }

    private void displayIndiaStats() {
        List<India> indiaList = new ArrayList<>();
        for (Element element : covidIndiaElements) {

            if (element.text().isEmpty()) {
                continue;
            }
            India india = new India();
            String state = element.select("th[scope]").text();
            String activeCases = element.getElementsByTag("td").get(0).text();
            String deathCases = element.getElementsByTag("td").get(1).text();
            String recoveredCases = element.getElementsByTag("td").get(2).text();

            india.setState(state);
            india.setActiveCases(activeCases);
            india.setDeathCases(deathCases);
            india.setRecoveries(recoveredCases);

            indiaList.add(india);
        }

        for(India india : indiaList) {
            System.out.println(india.getState() + "---" + india.getActiveCases() + "---" + india.getRecoveries());
        }
    }

    private void initializeJsoupItems() {
        try {
            Document covidDoc = Jsoup.connect(Constants.URL_WIKI).get();
            Document covidCanadaDoc = Jsoup.connect(Constants.URL_CANADA).get();
            Document covidUnitedStatesDoc = Jsoup.connect(Constants.URL_USA).get();
            Document covidIndiaDoc = Jsoup.connect(Constants.URL_INDIA).get();
            covidOverAllElements = covidDoc.select("table.wikitable.plainrowheaders.sortable tr th.covid-total-row");
            covidCountryElements = covidDoc.select("table.wikitable.plainrowheaders.sortable tbody tr").not("tr.sortbottom");
            covidCanadaElements = covidCanadaDoc.select("table.wikitable.sortable");
            covidUnitedStatesElements = covidUnitedStatesDoc.select("table.wikitable.plainrowheaders.sortable tbody tr").
                    not("tr.sortbottom").not("tr:eq(0)").not("tr:eq(1)").not("tr:eq(2)");
            covidIndiaElements = covidIndiaDoc.select("table.wikitable.plainrowheaders.sortable.mw-collapsible tbody tr").
                    not("tr.sortbottom").not("tr:eq(0)").not("tr:eq(1)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

