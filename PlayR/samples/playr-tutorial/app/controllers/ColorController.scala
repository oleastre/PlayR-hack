package controllers

import twentysix.playr._
import twentysix.playr.mongo._
import play.api.mvc._
import play.api.libs.json.Json
import models._
import play.api.libs.json.JsObject

object ColorController extends MongoResource[Color]
                          with ResourceRead
                          with ResourceCreate
                          with ResourceWrite {
  val collectionName = "color"

  def fromId(sid: String) = toInt(sid).flatMap(id => ColorContainer.get(id))

  def list = Action { Ok(Json.toJson(ColorContainer.list)) }

  def read(selector: JsObject, color: Color) = Action { Ok(Json.toJson(color)) }

  def write(color: Color) = Action(parse.json) { request =>
    val newColor = request.body.as[Color].copy(id=color.id)
    ColorContainer.update(newColor)
    Ok(Json.toJson(newColor))
  }

  def create = Action(parse.json){ request =>
    val newColor = request.body.as[Color]
    ColorContainer.add(newColor.name, newColor.rgb)
    Created(Json.toJson(newColor))
  }
}
