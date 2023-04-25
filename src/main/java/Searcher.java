import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * @author Doris Peter, IDV AG
 */
public class Searcher {

  public static void main(String[] args) {
    QueryResponse response;
    try (SolrClient client =
        new Http2SolrClient.Builder("http://localhost:8983/solr/test_core_1").build()) {

      SolrQuery query = new SolrQuery();
      query.setQuery("Otherland AND Williams");
      // query.addFilterQuery("cat:electronics","store:amazon.com");
      query.setFields("title", "author");
      // query.setStart(0);
      // query.set("defType", "edismax");

      response = client.query(query);
    } catch (IOException | SolrServerException e) {
      throw new RuntimeException(e);
    }

    SolrDocumentList results = response.getResults();

    if (results.size() == 0) {
      System.out.println("Sorry, no matching documents were found.");
      for (SolrDocument result : results) {
        System.out.println(result);
      }
    } else {
      for (SolrDocument result : results) {
        System.out.println(result);
      }
    }
  }
}
