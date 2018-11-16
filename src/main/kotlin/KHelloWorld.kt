import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotEmpty
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

class HWConfiguration(
        @JsonProperty @NotEmpty val template: String="",
        @JsonProperty @NotEmpty val defaultName: String = "Stranger") : Configuration() {

    constructor() : this("", "") // needed by Jackson deserialization
}


class HWSaying(
        @JsonProperty val id: Long = 0,
        @JsonProperty @field:Length(max = 3) val content: String = "") {

    constructor() : this(0, "") // needed by Jackson deserialization
}


@Path("/hello-world")
@Produces(APPLICATION_JSON)
class HWResource(
        private val template: String,
        private val defaultName: String) {

    private val counter = AtomicLong()

    @GET
    @Timed
    fun getHello(@QueryParam("name") name: Optional<String>): HWSaying {
        val value = String.format(template, name.orElse(defaultName))
        return HWSaying(counter.incrementAndGet(), value)
    }

    @POST
    @Timed
    fun postHello(@Valid saying: HWSaying) = HWSaying(saying.id, "bye ${saying.content}")
}



class HWApplication : Application<HWConfiguration>() {

    override fun getName() = "hello-world"

    override fun run(conf: HWConfiguration, env: Environment) {
        val hwResource = HWResource(conf.template, conf.defaultName)
        val healthCheck = HWHealthCheck(conf.template)

        env.jersey().register(hwResource)
        env.healthChecks().register("hcTemplate", healthCheck)
    }

}

fun main(args: Array<String>) = HWApplication().run(*args)
