import com.codahale.metrics.annotation.Timed
import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result.healthy
import com.codahale.metrics.health.HealthCheck.Result.unhealthy
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import org.hibernate.validator.constraints.Length
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

class HWConfiguration(
        @JsonProperty val template: String,
        @JsonProperty val defaultName: String = "Stranger") : Configuration() {

    constructor() : this("", "")
}


class HWSaying(
        @JsonProperty val id: Long,
        @JsonProperty @field:Length(max = 3) val content: String) {

    constructor() : this(0, "")
}


@Path("/hello-world")
@Produces(APPLICATION_JSON)
class HWResource(val template: String, val defaultName: String) {

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


class HWHealthCheck(val template: String) : HealthCheck() {

    override fun check(): Result {
        val saying = String.format(template, "TEST")

        return if (!saying.contains("TEST")) {
            unhealthy("template doesn't include a name")
        } else {
            healthy()
        }
    }

}

class HWApplication : Application<HWConfiguration>() {

    override fun getName() = "hello-world"

    override fun run(conf: HWConfiguration, env: Environment) {
        val hwResource = HWResource(conf.template, conf.defaultName)
        val healthCheck = HWHealthCheck(conf.template)

        env.jersey().register(hwResource)
        env.healthChecks().register("template", healthCheck)
    }

}

fun main(args: Array<String>) = HWApplication().run(*args)
