package twentysix.playr.mongo

import reflect.runtime.universe.{Type,TypeTag,typeOf}
import scala.concurrent.Future
import scala.language.implicitConversions
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsObject
import play.api.mvc.{Action, EssentialAction}
import play.modules.reactivemongo.MongoController
import twentysix.playr.core
import reactivemongo.bson.BSONObjectID
import twentysix.playr.ResourceWrapper

trait BaseResource extends core.ResourceTrait[JsObject]
                      with core.ResourceShortcuts
                      with ResourceShortcuts
                      with MongoController{
  type ResourceType

  def handleAction(selector: JsObject, f: Function2[JsObject, ResourceType, EssentialAction]) = {
    val action = EssentialAction { rh =>
      Iteratee.flatten {
        resourceFromSelector(selector).map { resource =>
          resource.map(f(selector, _)(rh)).getOrElse(Action{NotFound}(rh))
        }
      }
    }
    Some(action)
  }
  def resourceFromSelector(selector: JsObject): Future[Option[ResourceType]]
}

trait Resource[R] extends BaseResource {
  type ResourceType = R
}

object Resource {
  implicit def mongoResourceAction[R, C<:Resource[R]](f: (JsObject, R)=> EssentialAction)(implicit tt: TypeTag[(JsObject, R)=> EssentialAction]) =
    new core.ResourceAction[C]{
      def handleAction(controller: C, id: JsObject): Option[EssentialAction] = controller.handleAction(id, f)
      def getType: Type = tt.tpe
    }

  implicit def mongoSubResourceAction[R, C<:Resource[R]](f: C => (JsObject, R) => EssentialAction)(implicit tt: TypeTag[(JsObject, R)=> EssentialAction]) =
    new core.ResourceAction[C] {
      def handleAction(controller: C, id: JsObject): Option[EssentialAction] = controller.handleAction(id, f(controller))
      def getType: Type = tt.tpe
    }

  implicit def mongoControllerFactory[P<:Resource[_], C<:core.BaseResource: ResourceWrapper](f: JsObject => C ) =
    new core.ControllerFactory[P, C]{
      def construct(parent: P, resource: JsObject) = f(resource)
    }
}

trait ResourceRead extends core.ResourceRead {
  this: BaseResource =>
  def readResource(id: IdentifierType) = handleAction(id, read)
  def listResource = Some(list)

  def read(selector: JsObject, resource: ResourceType): EssentialAction
  def list: EssentialAction
}

trait ResourceWrite extends core.ResourceWrite{
  this: BaseResource =>
  def writeResource(id: IdentifierType) = handleAction(id, write)

  def write(selector: JsObject, resource: ResourceType): EssentialAction
}

trait ResourceDelete extends core.ResourceDelete {
  this: BaseResource =>
  def deleteResource(id: IdentifierType) = handleAction(id, delete)

  def delete(selector: JsObject, resource: ResourceType): EssentialAction
}

trait ResourceUpdate extends core.ResourceUpdate {
  this: BaseResource =>
  def updateResource(id: IdentifierType) = handleAction(id, update)

  def update(selector: JsObject, resource: ResourceType): EssentialAction
}

trait ResourceCreate extends core.ResourceCreate {
  this: BaseResource =>
  def createResource = Some(create)

  def create: EssentialAction
}

