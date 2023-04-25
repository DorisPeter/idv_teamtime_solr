import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;

/**
 * @author Doris Peter, IDV AG
 */
public class Indexer {

  public static void main(String[] args) {
    try (SolrClient client = new Http2SolrClient.Builder(
        "http://localhost:8983/solr/test_core_1").build()) {
      SolrInputDocument doc = new SolrInputDocument();
      doc.addField("id", "7");
      doc.addField("isbn", "978-1408114391");
      doc.addField("title", "Cat on a Hot Tin Roof");
      doc.addField("description",
          "In Cat on a Hot Tin Roof a Southern family meet to celebrate "
              + "'Big Daddy' Pollit's birthday: Gooper with his wife and children, "
              + "his brother Brick - an ageing, broken football star - and his wife Maggie. "
              + "But as the party unfolds the facade of a happy family gathering is "
              + "fractured by sexual frustration, repressed love, and greed in the light of their "
              + "father's impending death.");
      doc.addField("author", "Tennessee Williams");
      doc.addField("country", "USA");

      try {
        client.add(doc);
      } catch (SolrServerException | IOException e) {
        throw new RuntimeException(e);
      }
      try {
        client.commit();
      } catch (SolrServerException | IOException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Commit done");
  }
}

