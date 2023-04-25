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
    try (SolrClient client =
        new Http2SolrClient.Builder("http://localhost:8983/solr/library").build()) {
      SolrInputDocument doc = new SolrInputDocument();
      doc.addField("id", "8");
      doc.addField("isbn", "978-3596271207");
      doc.addField("title", "Endstation Sehnsucht");
      doc.addField(
          "description",
          "Endstation Sehnsucht‹ erzählt die Geschichte von Blanche Dubois, "
              + "einer Lehrerin aus den Südstaaten. Blanche erlebt die Auflösung ihrer Familie, "
              + "einer nach dem anderen stirbt, sie muß zusehen, wie der einstmals stolze Besitz, "
              + "das Herrenhaus »Belle Reve«, zwangsversteigert wird.");
      doc.addField("author", "Tennessee Williams");
      doc.addField("country", "USA");
      doc.addField("year", "2010");

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
