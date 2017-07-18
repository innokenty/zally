package de.zalando.zally.rule

import com.typesafe.config.Config
import de.zalando.zally.violation.Violation
import de.zalando.zally.violation.ViolationType
import io.swagger.models.ComposedModel
import io.swagger.models.HttpMethod
import io.swagger.models.Model
import io.swagger.models.RefModel
import io.swagger.models.Response
import io.swagger.models.Swagger
import io.swagger.models.properties.ObjectProperty
import io.swagger.models.properties.RefProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NotSpecifyStandardErrorCodesRule(@Autowired rulesConfig: Config) : AbstractRule() {
    override val title = "Not Specify Standard Error Codes"
    // TODO add reference to manual if any
    override val url = "https://zalando.github.io/restful-api-guidelines/"
    override val violationType = ViolationType.HINT
    override val code = "H002"
    private val description = "Not Specify Standard Error Status Codes Like 400, 404, 503 " +
            "Unless They Have Another Meaning Or Special Implementation/Contract Detail"

    // TODO clarify: the list of standard error codes should not be request type specific - iow no diff for POST and GET, right?
    private val standardErrorStatusCodes = rulesConfig.getConfig(name)
            .getIntList("standard_error_codes").toSet()

    override fun validate(swagger: Swagger): Violation? {

        val paths = swagger.paths.orEmpty().flatMap { pathEntry ->
            pathEntry.value.operationMap.orEmpty().flatMap { opEntry ->
                opEntry.value.responses.orEmpty().flatMap { responseEntry ->
                    val httpCode = responseEntry.key.toIntOrNull()
                    if (isStandardErrorCode(httpCode)) {
                        listOf("${pathEntry.key} ${opEntry.key} ${responseEntry.key}")
                    } else emptyList()
                }
            }
        }

        return if (paths.isNotEmpty()) Violation(this, title, description, violationType, url, paths) else null
    }

    private fun isStandardErrorCode(httpStatusCode: Int?): Boolean {
        return httpStatusCode in standardErrorStatusCodes
    }

}
