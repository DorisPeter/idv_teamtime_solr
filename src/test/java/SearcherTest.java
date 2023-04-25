import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.Test;

/**
 * @author Doris Peter, IDV AG
 */
class SearcherTest {

  // 1. Alle Dokumente
  @Test
  public void getAllDocumentsTest() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(8, result.getNumFound());
  }

  // 2. Dokumente, bei denen der Preis zwischen 10 und 15 liegt
  @Test
  public void getPrice() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("price:[10 TO 15]");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(5, result.getNumFound());
  }

  // 3.1. Dokumente, bei denen der Author Tad und Williams enthält
  @Test
  public void getTadWilliams() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("author:Tad AND author:Williams");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(2, result.getNumFound());
  }

  // 3.2. Dokumente, bei denen der Author Williams aber nicht Tad enthält
  @Test
  public void getWilliamsNotTad() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("-author:Tad AND author:Williams");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(2, result.getNumFound());
  }

  // 3.2 wie 3.1, aber als Rückgabefelder nur title, subtitle und author
  @Test
  public void getTadWilliamsTitles() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("author:Tad AND author:Williams");
    query.setFields("title", "subtitle");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(2, result.getNumFound());
    assertEquals(2, result.get(0).getFieldNames().size());
  }

  // 5. Dokumente, deren Jahr vor 2000 liegt
  @Test
  public void getBooksFrom20thCentury() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("year:[* TO 2000]");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(1, result.getNumFound());
  }

  // 6.1 Alle Dokumente sortiert aufsteigend nach Jahr
  @Test
  public void getAllDocumentsSortByYear() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setSort("year", ORDER.asc);
    query.setFields("title", "subtitle", "year", "author");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    int lastIndex = toIntExact(result.getNumFound()-1);
    int year1 = (int) result.get(0).getFieldValue("year");
    int year2 = (int) result.get(lastIndex).getFieldValue("year");
    System.out.println("Lowest Year: " + year1);
    System.out.println("Highest year: " + year2);
    assertTrue(year1 <= year2);
  }

  // 6.2. wie 6.1, aber nur die ersten 3 Treffer
  @Test
  public void getAllDocumentsSortByYearFirstThree() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setSort("year", ORDER.asc);
    query.setFields("title", "subtitle", "year", "author");
    query.setRows(3);
    SolrDocumentList result = searcher.runQuery(query).getResults();
    int year1 = (int) result.get(0).getFieldValue("year");
    int year2 = (int) result.get(2).getFieldValue("year");
    System.out.println("Lowest Year: " + year1);
    System.out.println("Highest year: " + year2);
    assertTrue(year1 <= year2);
    assertEquals(8, result.getNumFound());
    assertEquals(3, result.size());
  }

  // 7. Suche nach William soll auch Treffer mit Williams finden
  @Test
  public void getTadWilliamsFuzzy1() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("author:Tad AND author:William?");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(2, result.getNumFound());
  }

  @Test
  public void getTadWilliamsFuzzy2() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("author:Tad AND author:William~1");
    SolrDocumentList result = searcher.runQuery(query).getResults();
    assertEquals(2, result.getNumFound());
  }

  // 8. Tery Pratchet soll die gleichen Treffer ergeben wie Terry
  @Test
  public void getTerryPratchettFuzzy() {
    Searcher searcher = new Searcher();
    SolrQuery query1 = new SolrQuery();
    query1.setQuery("author:Terry AND author:Pratchett");
    SolrDocumentList result1 = searcher.runQuery(query1).getResults();

    SolrQuery query2 = new SolrQuery();
    query2.setQuery("author:tery~1 AND author:pratchet~1");
    SolrDocumentList result2 = searcher.runQuery(query2).getResults();
    assertEquals(2, result2.getNumFound());
    assertEquals(2, result1.getNumFound());
    assertEquals(result1.get(0).get("id"), result2.get(0).get("id"));
    assertEquals(result1.get(1).get("id"), result2.get(1).get("id"));
  }

  // 9.1. Facette nach Author bilden
  @Test
  public void getAuthorFacet() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFacet(true);
    query.addFacetField("author_facet");
    query.setRows(0);
    FacetField result = searcher.runQuery(query)
        .getFacetField("author_facet");
    System.out.println(result.toString());
    assertEquals(4, result.getValues().size() );
  }
  // 9.2. Facette nach allen Authoren, die mit 'T' beginnen, es sollen nur Facetten mit mindestens 2
  // Treffern angezeigt werden.
  @Test
  public void getAuthorFacetWithT() {
    Searcher searcher = new Searcher();
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFacet(true);
    query.addFacetField("author_facet");
    query.setParam("facet.prefix", "T");
    query.setRows(0);
    FacetField result = searcher.runQuery(query)
        .getFacetField("author_facet");
    System.out.println(result.toString());
    assertEquals(3, result.getValues().size() );
  }

}
