import twentysix.playr.RestApiRouter
import controllers.Application
import twentysix.playr.swagger.SwaggerRestDocumentation

object HackApi {
  val api = RestApiRouter()
      .add("tutorial" -> Application.api)

  val apidocs = new SwaggerRestDocumentation(api)
}