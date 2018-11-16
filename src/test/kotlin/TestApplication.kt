import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.Test
import javax.ws.rs.client.Entity
import org.mockito.Mockito.`when` as When


class TestApplication {

    companion object {
        @ClassRule
        @JvmField
        val RULE = DropwizardAppRule<HWConfiguration>(HWApplication::class.java, ResourceHelpers.resourceFilePath("config.yml"))
    }


    @Test
    fun `test POST`() {
        val client = JerseyClientBuilder(RULE.environment).build("test client")

        val response = client.target(
                String.format("http://localhost:%d/hello-world", RULE.localPort))
                .request()
                .post(Entity.json("""{"id": 1, "content" : "bob"}"""))

        System.err.println(response)
        assertThat(response.status).isEqualTo(200);
    }
}