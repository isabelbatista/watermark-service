package isabelbatista.test.watermark.preparation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import isabelbatista.test.watermark.documents.InvalidEnumException;
import isabelbatista.test.watermark.documents.Publication;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Isabel Batista on 11.05.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PreparationServiceTest {

    @Autowired
    private PreparationService preparationService;

    @Test
    public void shouldReturnPublicationListFromCsvImport() throws InvalidEnumException, PreparationException {
        List<Publication> publications = preparationService.importExampleData();

        assertNotNull(publications);
        assertThat(publications.size(), is(3));
        assertThat(publications.get(0).getTitle(), is("The Dark Code"));
    }
}
