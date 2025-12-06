error id: file://<WORKSPACE>/src/main/scala/routes/Routes.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/routes/Routes.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -zio/Response#
	 -zio/http/Response#
	 -Response#
	 -scala/Predef.Response#
offset: 2293
uri: file://<WORKSPACE>/src/main/scala/routes/Routes.scala
text:
```scala
package routes

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import zio.*
import zio.stream.ZStream
import zio.http.*
import scala.language.implicitConversions
import kuzminki.api.Jsonb

import java.io.{PrintWriter, CharArrayWriter}


object Routes {

  private val routes = SelectRoute.routes ++
                       OperationRoute.routes ++
                       CacheRoute.routes ++
                       StreamRoute.routes ++
                       JsonbRoute.routes ++
                       ArrayRoute.routes ++
                       DateRoute.routes ++
                       TypeRoute.routes

  val app = (routes).mapError { ex =>
    Response.json(
      """{"error": "%s"}""".format(ex.getMessage)
    )
  }
}


trait Responses {

  implicit class QueryParamsMap(req: Request) {
    val q = req.url.queryParams.map.map(p => p._1 -> p._2(0))
  }

  def queryString(req: Request, key: String): String = {
    req.url.queryParams.map(key)(0)
  }

  def queryStringOpt(req: Request, key: String): Option[String] = {
    req.url.queryParams.map.get(key).map(_(0))
  }

  def queryInt(req: Request, key: String): Int = {
    req.url.queryParams.map(key)(0).toInt
  }

  def queryIntOpt(req: Request, key: String): Option[Int] = {
    req.url.queryParams.map.get(key).map(_(0).toInt)
  }

  def queryBigDecimal(req: Request, key: String): BigDecimal = {
    BigDecimal(req.url.queryParams.map(key)(0))
  }

  def queryBigDecimalOpt(req: Request, key: String): Option[BigDecimal] = {
    req.url.queryParams.map.get(key).map(v => BigDecimal(v(0)))
  }

  val bodyMap: Form => Map[String, String] = { form =>
    form.formData.map(f => f.name -> f.stringValue.get).toMap
  }

  def withParams[R](req: Request)(fn: Map[String, String] => RIO[R, Response]): RIO[R, Response] = {
    for {
      params  <- req.body.asURLEncodedForm.map(bodyMap)
      rsp     <- fn(params)
    } yield rsp
  }

  def withJsonBody[R](req: Request)(fn: String => RIO[R, Response]): RIO[R, Response] = {
    for {
      params  <- req.body.asString
      rsp     <- fn(params)
    } yield rsp
  }
  
  val notFound = """{"message": "not found"}"""
  val somethingWentWrong = """{"error":"Something went wrong"}"""
  val okTrue = """{"ok": true}"""

  val jsonObjResponse: Jsonb => Respo@@nse = { obj =>
    Response.json(obj.value)
  }

  val jsonOptResponse: Option[Jsonb] => Response = {
    case Some(obj) => Response.json(obj.value)
    case None => Response.json(notFound)
  }

  val jsonListResponse: List[Jsonb] => Response = { list =>
    Response.json("[%s]".format(list.map(_.value).mkString(",")))
  }

  val jsonOkResponse: Unit => Response = _ => jsonObjResponse(Jsonb(okTrue))
}







```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.