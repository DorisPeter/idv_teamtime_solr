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
  private final SolrClient client;

  public Searcher(){
    String solrUrl = "http://localhost:8983/solr/library";
    client =
        new Http2SolrClient.Builder(solrUrl).build();
  }

  public QueryResponse runQuery(SolrQuery query) {
    QueryResponse response;
    try {
      response = client.query(query);
    } catch (SolrServerException | IOException e) {
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
    return response;
  }
}
